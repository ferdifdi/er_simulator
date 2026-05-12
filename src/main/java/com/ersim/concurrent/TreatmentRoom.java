package com.ersim.concurrent;

import com.ersim.model.Doctor;
import com.ersim.model.Patient;
import com.ersim.model.enums.RoomStatus;
import com.ersim.service.TriageService;

/**
 * A TreatmentRoom worker thread. Each instance pulls the highest-priority
 * patient off the shared TriageQueue, simulates treatment via its assigned
 * Doctor, then releases the room and becomes AVAILABLE again.
 *
 * Room state changes go through synchronized assignPatient/releaseRoom so
 * the room worker thread and any HTTP-driven manual-discharge thread can
 * never both "discharge" the same patient.
 */
public class TreatmentRoom implements Runnable {

    private final String roomId;
    private final TriageQueue queue;
    private final Doctor doctor;
    private final TriageService service;
    private Patient currentPatient;
    private RoomStatus status = RoomStatus.AVAILABLE;
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
                if (service != null) {
                    service.onRoomAssigned(this, p);
                } else {
                    assignPatient(p);
                }
                if (doctor != null) doctor.treat(p);
                if (service != null) {
                    service.onTreatmentComplete(this);
                } else {
                    releaseRoom();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                running = false;
            } catch (Exception e) {
                System.err.println("[TreatmentRoom " + roomId + "] error: " + e.getMessage());
                releaseRoom();
            }
        }
    }

    /**
     * Mark this room OCCUPIED with the given patient. Pure in-memory state
     * change — persistence and event logging are the service's job.
     */
    public synchronized void assignPatient(Patient p) {
        this.currentPatient = p;
        this.status = RoomStatus.OCCUPIED;
    }

    /**
     * Atomically release the room. Returns the patient that was occupying
     * the room, or {@code null} if the room was already free. This is the
     * single synchronization point that lets the worker thread and the
     * HTTP-driven manual-discharge thread coordinate: whoever calls first
     * gets the patient back; the other gets null and does nothing.
     */
    public synchronized Patient releaseRoom() {
        Patient released = this.currentPatient;
        this.currentPatient = null;
        this.status = RoomStatus.AVAILABLE;
        return released;
    }

    /**
     * Snapshot of the room's current state for /rooms endpoint and GUI.
     */
    public synchronized RoomStatusSnapshot getRoomStatus() {
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
    public synchronized RoomStatus getStatus() { return status; }
    public synchronized Patient getCurrentPatient() { return currentPatient; }
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
