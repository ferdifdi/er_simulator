package com.ersim.service;

import com.ersim.concurrent.PatientArrivalThread;
import com.ersim.concurrent.TreatmentRoom;
import com.ersim.concurrent.TriageQueue;
import com.ersim.model.Doctor;
import com.ersim.model.Patient;
import com.ersim.model.TriageEventLog;
import com.ersim.model.enums.EventType;
import com.ersim.model.enums.PatientStatus;
import com.ersim.model.enums.TriageLevel;
import com.ersim.repository.PatientRepository;
import com.ersim.repository.TriageEventLogRepository;
import com.ersim.websocket.WebSocketBroadcaster;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Central service that coordinates patient intake, room assignment,
 * triage updates, and discharges. Owns the TreatmentRoom thread pool.
 */
@Service
public class TriageService {

    @Autowired private TriageQueue queue;
    @Autowired private PatientRepository patientRepository;
    @Autowired private TriageEventLogRepository logRepository;
    @Autowired private WebSocketBroadcaster broadcaster;

    @Value("${ersim.rooms.count:3}")
    private int roomsCount;

    @Value("${ersim.simulation.autostart:true}")
    private boolean autostart;

    private ExecutorService roomPool;
    private List<TreatmentRoom> rooms = new ArrayList<>();
    private Thread arrivalThread;
    private PatientArrivalThread arrivalRunnable;

    @PostConstruct
    public void init() {
        if (roomsCount > 0) {
            roomPool = Executors.newFixedThreadPool(roomsCount);
            for (int i = 0; i < roomsCount; i++) {
                Doctor doc = new Doctor("D" + (i + 1), "Doctor " + (i + 1), "General");
                TreatmentRoom room = new TreatmentRoom("R" + (i + 1), queue, doc, this);
                rooms.add(room);
                roomPool.submit(room);
            }
        }
        if (autostart) {
            arrivalRunnable = new PatientArrivalThread(this::admitPatient);
            arrivalThread = new Thread(arrivalRunnable, "patient-arrival");
            arrivalThread.setDaemon(true);
            arrivalThread.start();
        }
    }

    @PreDestroy
    public void shutdown() {
        for (TreatmentRoom r : rooms) r.stop();
        if (arrivalRunnable != null) arrivalRunnable.stop();
        if (roomPool != null) {
            roomPool.shutdownNow();
            try {
                roomPool.awaitTermination(2, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Admit a new patient: persist, log ADMITTED, enqueue, broadcast.
     */
    public Patient admitPatient(Patient p) {
        if (p == null) return null;
        if (p.getStatus() == null) p.setStatus(PatientStatus.WAITING);
        if (p.getArrivalTime() == null) p.setArrivalTime(LocalDateTime.now());
        if (p.getTriageLevel() != null) {
            p.setPriority(p.getTriageLevel().ordinal());
        }
        Patient saved = patientRepository.save(p);
        logEvent(saved, EventType.ADMITTED, null);
        queue.enqueue(saved);
        broadcaster.broadcastQueue();
        return saved;
    }

    /**
     * Assign waiting patient to a specific room. Usually called internally
     * by TreatmentRoom workers, but exposed for manual override.
     */
    public void assignRoom(Patient patient, String roomId) {
        if (patient == null || roomId == null) return;
        for (TreatmentRoom r : rooms) {
            if (roomId.equals(r.getRoomId())) {
                r.assignPatient(patient);
                broadcaster.broadcastRoomStatus();
                return;
            }
        }
    }

    /**
     * Discharge patient from a room: persist status, log DISCHARGED, broadcast.
     */
    public void dischargePatient(String patientId) {
        if (patientId == null) return;

        for (TreatmentRoom r : rooms) {
            Patient cp = r.getCurrentPatient();
            if (cp != null && patientId.equals(cp.getPatientId())) {
                r.discharge();
                break;
            }
        }

        patientRepository.findById(patientId).ifPresent(p -> {
            if (p.getStatus() != PatientStatus.DISCHARGED) {
                p.setStatus(PatientStatus.DISCHARGED);
                patientRepository.save(p);
                logEvent(p, EventType.DISCHARGED, null);
            }
        });
        broadcaster.broadcastRoomStatus();
    }

    /**
     * Upgrade a patient's ESI level (e.g., from 3 to 1). Must
     * re-prioritize them in the queue.
     */
    public void upgradeTriageLevel(String patientId, TriageLevel newLevel) {
        if (patientId == null || newLevel == null) return;
        queue.upgradeLevel(patientId, newLevel);
        patientRepository.findById(patientId).ifPresent(p -> {
            p.setTriageLevel(newLevel);
            patientRepository.save(p);
            logEvent(p, EventType.LEVEL_UPGRADED, null);
        });
        broadcaster.broadcastQueue();
    }

    /**
     * Snapshot of the queue (for REST /queue and GUI).
     */
    public List<Patient> getQueueStatus() {
        return queue.getQueueSnapshot();
    }

    /**
     * Snapshot of all rooms (for REST /rooms and GUI).
     */
    public List<TreatmentRoom.RoomStatusSnapshot> getRoomStatus() {
        List<TreatmentRoom.RoomStatusSnapshot> snaps = new ArrayList<>(rooms.size());
        for (TreatmentRoom r : rooms) snaps.add(r.getRoomStatus());
        return snaps;
    }

    /**
     * Aggregate report for /report endpoint: avg wait, throughput, counts.
     */
    public Object getReport() {
        Map<String, Object> report = new HashMap<>();
        report.put("queueSize", queue.size());
        report.put("totalRooms", rooms.size());
        long occupied = rooms.stream()
                .filter(r -> r.getCurrentPatient() != null)
                .count();
        report.put("occupiedRooms", occupied);
        report.put("totalEvents", logRepository.count());
        report.put("admitted", logRepository.findByEventType(EventType.ADMITTED).size());
        report.put("discharged", logRepository.findByEventType(EventType.DISCHARGED).size());
        report.put("upgrades", logRepository.findByEventType(EventType.LEVEL_UPGRADED).size());
        return report;
    }

    /**
     * Internal helper: persist a TriageEventLog entry. Public so worker
     * threads (TreatmentRoom) can publish their own events.
     */
    public void logEvent(Patient p, EventType type, String roomId) {
        TriageEventLog log = new TriageEventLog(p, type, roomId);
        logRepository.save(log);
        broadcaster.broadcastEvent(log);
    }
}
