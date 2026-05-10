package com.ersim.service;

import com.ersim.concurrent.TreatmentRoom;
import com.ersim.concurrent.TriageQueue;
import com.ersim.model.Patient;
import com.ersim.model.TriageEventLog;
import com.ersim.model.enums.EventType;
import com.ersim.model.enums.TriageLevel;
import com.ersim.repository.PatientRepository;
import com.ersim.repository.TriageEventLogRepository;
import com.ersim.websocket.WebSocketBroadcaster;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Central service that coordinates patient intake, room assignment,
 * triage updates, and discharges. Owns the TreatmentRoom thread pool.
 *
 * TODO #Ferdi: full implementation of all admit / assign / discharge /
 *              upgrade / report methods. Wire WebSocketBroadcaster
 *              calls on every state change.
 */
@Service
public class TriageService {

    @Autowired private TriageQueue queue;
    @Autowired private PatientRepository patientRepository;
    @Autowired private TriageEventLogRepository logRepository;
    @Autowired private WebSocketBroadcaster broadcaster;

    private ExecutorService roomPool;
    private List<TreatmentRoom> rooms;

    @PostConstruct
    public void init() {
        // TODO #Ferdi: build N TreatmentRoom workers, spin up ExecutorService,
        //              submit each room to the pool, also start PatientArrivalThread.
    }

    /**
     * Admit a new patient: persist, log ADMITTED, enqueue, broadcast.
     */
    public Patient admitPatient(Patient p) {
        // TODO #Ferdi: save to DB, log ADMITTED event, enqueue, broadcastQueue
        return null;
    }

    /**
     * Assign waiting patient to a specific room. Usually called internally
     * by TreatmentRoom workers, but exposed for manual override.
     */
    public void assignRoom(Patient patient, String roomId) {
        // TODO #Ferdi: locate room, mark assigned, log ROOM_ASSIGNED, broadcast
    }

    /**
     * Discharge patient from a room: persist status, log DISCHARGED, broadcast.
     */
    public void dischargePatient(String patientId) {
        // TODO #Ferdi: find patient, set DISCHARGED, log event, broadcastRoomStatus
    }

    /**
     * Upgrade a patient's ESI level (e.g., from 3 to 1). Must
     * re-prioritize them in the queue.
     */
    public void upgradeTriageLevel(String patientId, TriageLevel newLevel) {
        // TODO #Ferdi: call queue.upgradeLevel, persist patient, log LEVEL_UPGRADED,
        //              broadcastQueue
    }

    /**
     * Snapshot of the queue (for REST /queue and GUI).
     */
    public List<Patient> getQueueStatus() {
        // TODO #Ferdi: return queue.getQueueSnapshot()
        return List.of();
    }

    /**
     * Snapshot of all rooms (for REST /rooms and GUI).
     */
    public List<TreatmentRoom.RoomStatusSnapshot> getRoomStatus() {
        // TODO #Ferdi: map rooms to snapshots
        return List.of();
    }

    /**
     * Aggregate report for /report endpoint: avg wait, throughput, counts.
     */
    public Object getReport() {
        // TODO #Ferdi: aggregate from logRepository (avg wait per ESI level,
        //              total discharged, currently-occupied rooms, etc.)
        return null;
    }

    /**
     * Internal helper: persist a TriageEventLog entry.
     */
    private void logEvent(Patient p, EventType type, String roomId) {
        // TODO #Ferdi: build TriageEventLog, save via logRepository,
        //              also broadcaster.broadcastEvent(log)
    }
}
