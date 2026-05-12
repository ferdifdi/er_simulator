package com.ersim.concurrent;

import com.ersim.model.Doctor;
import com.ersim.model.Patient;
import com.ersim.model.enums.EventType;
import com.ersim.model.enums.PatientStatus;
import com.ersim.model.enums.RoomStatus;
import com.ersim.service.TriageService;

/**
 * A TreatmentRoom worker thread. Each instance pulls the highest-priority
 * patient off the shared TriageQueue, simulates treatment via its assigned
 * Doctor, then discharges the patient and becomes AVAILABLE again.
 */
public class TreatmentRoom implements Runnable {

    private final String roomId;
    private final TriageQueue queue;
    private final Doctor doctor;
    private final TriageService service;
    private volatile Patient currentPatient;
    private volatile RoomStatus status = RoomStatus.AVAILABLE;
    private volatile boolean running = true;

    public TreatmentRoom(String roomId, TriageQueue queue, Doctor doctor) {
        this(roomId, queue, doctor, null);
    }

    public TreatmentRoom(String roomId, TriageQueue queue, Doctor doctor, TriageService service) {
        this.roomId = roomId;
        this.queue = queue;
        this.doctor = doctor;
        this.service = service;
    }

    @Override
    public void run() {
        while (running) {
            try {
                Patient p = queue.dequeue();
                if (p == null) continue;
                assignPatient(p);
                if (doctor != null) doctor.treat(p);
                discharge();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                running = false;
            }
        }
    }

    /**
     * Mark this room OCCUPIED and store the current patient.
     */
    public void assignPatient(Patient p) {
        this.currentPatient = p;
        this.status = RoomStatus.OCCUPIED;
        if (p != null) {
            p.setStatus(PatientStatus.IN_TREATMENT);
            if (service != null) {
                service.logEvent(p, EventType.ROOM_ASSIGNED, roomId);
                service.logEvent(p, EventType.TREATMENT_STARTED, roomId);
            }
        }
    }

    /**
     * Free the room, set patient status DISCHARGED, log event.
     */
    public void discharge() {
        Patient finished = this.currentPatient;
        if (finished != null) {
            finished.setStatus(PatientStatus.DISCHARGED);
            if (service != null) {
                service.logEvent(finished, EventType.DISCHARGED, roomId);
            }
        }
        this.currentPatient = null;
        this.status = RoomStatus.AVAILABLE;
    }

    /**
     * Snapshot of the room's current state for /rooms endpoint and GUI.
     */
    public RoomStatusSnapshot getRoomStatus() {
        RoomStatusSnapshot snap = new RoomStatusSnapshot();
        snap.roomId = roomId;
        snap.status = status;
        Patient cp = currentPatient;
        snap.currentPatientName = cp != null ? cp.getName() : null;
        snap.currentPatientId   = cp != null ? cp.getPatientId() : null;
        snap.doctorName = doctor != null ? doctor.getName() : null;
        return snap;
    }

    public String getRoomId() { return roomId; }
    public RoomStatus getStatus() { return status; }
    public Patient getCurrentPatient() { return currentPatient; }
    public Doctor getDoctor() { return doctor; }

    public void stop() { this.running = false; }

    /** Lightweight DTO for snapshots. */
    public static class RoomStatusSnapshot {
        public String roomId;
        public RoomStatus status;
        public String currentPatientName;
        public String currentPatientId;
        public String doctorName;
    }
}
