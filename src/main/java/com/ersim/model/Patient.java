package com.ersim.model;

import com.ersim.model.enums.PatientStatus;
import com.ersim.model.enums.TriageLevel;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Patient entity. Each patient has a unique ID, demographic info, an
 * ESI triage level, and a lifecycle status.
 *
 * Implements Comparable so that PriorityBlockingQueue can order patients
 * by ESI severity (lowest ordinal == highest priority).
 *
 * TODO #Ferdi: full implementation of fields, getters/setters, compareTo.
 */
@Entity
@Table(name = "patients")
public class Patient implements Comparable<Patient> {

    @Id
    private String patientId;

    private String name;
    private int age;

    @Enumerated(EnumType.STRING)
    private TriageLevel triageLevel;

    private int priority;
    private LocalDateTime arrivalTime;

    @Enumerated(EnumType.STRING)
    private PatientStatus status;

    public Patient() {
        // TODO #Ferdi: required no-arg constructor for JPA — leave blank
    }

    public Patient(String patientId, String name, int age, TriageLevel triageLevel) {
        // TODO #Ferdi: assign fields, set arrivalTime to now, default status to WAITING,
        //              and derive priority from triageLevel.ordinal().
    }

    @Override
    public int compareTo(Patient other) {
        // TODO #Ferdi: order by triageLevel ordinal first, then by arrivalTime
        //              (earlier arrival wins). Used by PriorityBlockingQueue.
        return 0;
    }

    // ------------------------------------------------------------------
    // Getters / Setters
    // TODO #Ferdi: implement all getters and setters
    // ------------------------------------------------------------------

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public TriageLevel getTriageLevel() { return triageLevel; }
    public void setTriageLevel(TriageLevel triageLevel) {
        // TODO #Ferdi: also update priority field whenever triageLevel changes
        this.triageLevel = triageLevel;
    }

    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }

    public LocalDateTime getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(LocalDateTime arrivalTime) { this.arrivalTime = arrivalTime; }

    public PatientStatus getStatus() { return status; }
    public void setStatus(PatientStatus status) { this.status = status; }
}
