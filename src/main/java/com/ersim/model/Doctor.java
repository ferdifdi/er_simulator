package com.ersim.model;

/**
 * In-memory model of a Doctor assigned to a TreatmentRoom.
 * Not persisted (yet) — held by TreatmentRoom thread.
 */
public class Doctor {

    private String doctorId;
    private String name;
    private String specialization;
    private boolean isAvailable;

    public Doctor() {
        // empty constructor
    }

    public Doctor(String doctorId, String name, String specialization) {
        this.doctorId = doctorId;
        this.name = name;
        this.specialization = specialization;
        this.isAvailable = true;
    }

    /**
     * Simulate the doctor treating a patient. May sleep for a duration
     * determined by the patient's triage level.
     */
    public void treat(Patient p) {
        // ESI_1=20s, ESI_2=16s, ESI_3=12s, ESI_4=10s, ESI_5=8s
        long treatmentTime = (20L - p.getTriageLevel().ordinal() * 4) * 1000;
        
        setAvailable(false);
        try {
            Thread.sleep(treatmentTime);
        } catch (InterruptedException e) {
            // Restore interrupt flag
            Thread.currentThread().interrupt();
        } finally {
            setAvailable(true);
        }
    }

    public synchronized void setAvailable(boolean b) {
        this.isAvailable = b;
    }

    // ------------------------------------------------------------------
    // Getters
    // ------------------------------------------------------------------

    public String getDoctorId() { return doctorId; }
    public String getName() { return name; }
    public String getSpecialization() { return specialization; }
    public synchronized boolean isAvailable() { return isAvailable; }
}
