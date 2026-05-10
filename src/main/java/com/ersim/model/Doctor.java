package com.ersim.model;

/**
 * In-memory model of a Doctor assigned to a TreatmentRoom.
 * Not persisted (yet) — held by TreatmentRoom thread.
 *
 * TODO #Sruthi: full implementation of treat() and setAvailable() with
 *               appropriate synchronization on isAvailable.
 */
public class Doctor {

    private String doctorId;
    private String name;
    private String specialization;
    private boolean isAvailable;

    public Doctor() {
        // TODO #Sruthi: empty constructor
    }

    public Doctor(String doctorId, String name, String specialization) {
        // TODO #Sruthi: assign fields, default isAvailable = true
    }

    /**
     * Simulate the doctor treating a patient. May sleep for a duration
     * determined by the patient's triage level.
     */
    public void treat(Patient p) {
        // TODO #Sruthi: simulate treatment time (Thread.sleep keyed off triage level),
        //               must be safe to call from a TreatmentRoom worker thread.
    }

    public synchronized void setAvailable(boolean b) {
        // TODO #Sruthi: update isAvailable atomically
        this.isAvailable = b;
    }

    // ------------------------------------------------------------------
    // Getters
    // TODO #Sruthi: implement getters as needed
    // ------------------------------------------------------------------

    public String getDoctorId() { return doctorId; }
    public String getName() { return name; }
    public String getSpecialization() { return specialization; }
    public synchronized boolean isAvailable() { return isAvailable; }
}
