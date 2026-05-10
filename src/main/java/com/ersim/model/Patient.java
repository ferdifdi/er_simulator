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
    }

    public Patient(String patientId, String name, int age, TriageLevel triageLevel) {
        this.patientId = patientId;
        this.name = name;
        this.age = age;
        this.triageLevel = triageLevel;
        this.priority = triageLevel != null ? triageLevel.ordinal() : Integer.MAX_VALUE;
        this.arrivalTime = LocalDateTime.now();
        this.status = PatientStatus.WAITING;
    }

    @Override
    public int compareTo(Patient other) {
        int byLevel = Integer.compare(
                this.triageLevel != null ? this.triageLevel.ordinal() : Integer.MAX_VALUE,
                other.triageLevel != null ? other.triageLevel.ordinal() : Integer.MAX_VALUE);
        if (byLevel != 0) return byLevel;
        if (this.arrivalTime == null && other.arrivalTime == null) return 0;
        if (this.arrivalTime == null) return 1;
        if (other.arrivalTime == null) return -1;
        return this.arrivalTime.compareTo(other.arrivalTime);
    }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public TriageLevel getTriageLevel() { return triageLevel; }
    public void setTriageLevel(TriageLevel triageLevel) {
        this.triageLevel = triageLevel;
        this.priority = triageLevel != null ? triageLevel.ordinal() : Integer.MAX_VALUE;
    }

    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }

    public LocalDateTime getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(LocalDateTime arrivalTime) { this.arrivalTime = arrivalTime; }

    public PatientStatus getStatus() { return status; }
    public void setStatus(PatientStatus status) { this.status = status; }
}
