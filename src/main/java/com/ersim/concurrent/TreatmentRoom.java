package com.ersim.concurrent;

import com.ersim.model.Doctor;
import com.ersim.model.Patient;
import com.ersim.model.enums.RoomStatus;

/**
 * A TreatmentRoom worker thread. Each instance pulls the highest-priority
 * patient off the shared TriageQueue, simulates treatment via its assigned
 * Doctor, then discharges the patient and becomes AVAILABLE again.
 *
 * TODO #Ferdi: full implementation of run(), assignPatient(), discharge().
 *              Must publish DB events via TriageEventLogRepository (inject
 *              via constructor or via a shared TriageService callback).
 */
public class TreatmentRoom implements Runnable {

    private final String roomId;
    private final TriageQueue queue;
    private final Doctor doctor;
    private volatile Patient currentPatient;
    private volatile RoomStatus status = RoomStatus.AVAILABLE;
    private volatile boolean running = true;

    public TreatmentRoom(String roomId, TriageQueue queue, Doctor doctor) {
        this.roomId = roomId;
        this.queue = queue;
        this.doctor = doctor;
    }

    @Override
    public void run() {
        // TODO #Ferdi: while running, dequeue patient, assignPatient, doctor.treat,
        //               then discharge. Handle InterruptedException to exit cleanly.
    }

    /**
     * Mark this room OCCUPIED and store the current patient.
     */
    public void assignPatient(Patient p) {
        // TODO #Ferdi: set status OCCUPIED, set currentPatient, set patient status
        //               to IN_TREATMENT, log ROOM_ASSIGNED + TREATMENT_STARTED events.
    }

    /**
     * Free the room, set patient status DISCHARGED, log event.
     */
    public void discharge() {
        // TODO #Ferdi: log DISCHARGED event, clear currentPatient,
        //               set status back to AVAILABLE (or CLEANING then AVAILABLE).
    }

    /**
     * Snapshot of the room's current state for /rooms endpoint and GUI.
     */
    public RoomStatusSnapshot getRoomStatus() {
        // TODO #Ferdi: return immutable snapshot containing roomId, status,
        //               currentPatient name, doctor name.
        return null;
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
        public String doctorName;
    }
}
