package com.ersim.model;

import com.ersim.model.enums.EventType;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Immutable audit record of a single triage event for a patient.
 * Used to compute statistics like average wait time, throughput,
 * and event counts per level.
 */
@Entity
@Table(name = "triage_event_log")
public class TriageEventLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long logId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    private String roomId;
    private LocalDateTime timestamp;

    public TriageEventLog() {
        // required no-arg constructor for JPA
    }

    public TriageEventLog(Patient patient, EventType eventType, String roomId) {
        this.patient = patient;
        this.eventType = eventType;
        this.roomId = roomId;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Returns the wait duration in seconds between the patient's arrival
     * and this event timestamp.
     */
    public long getWaitDuration() {
        return java.time.Duration.between(patient.getArrivalTime(), this.timestamp).toSeconds();
    }

    // ------------------------------------------------------------------
    // Getters / Setters
    // ------------------------------------------------------------------

    public Long getLogId() { return logId; }
    public void setLogId(Long logId) { this.logId = logId; }

    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }

    public EventType getEventType() { return eventType; }
    public void setEventType(EventType eventType) { this.eventType = eventType; }

    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
