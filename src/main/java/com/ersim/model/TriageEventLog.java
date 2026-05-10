package com.ersim.model;

import com.ersim.model.enums.EventType;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Immutable audit record of a single triage event for a patient.
 * Used to compute statistics like average wait time, throughput,
 * and event counts per level.
 *
 * TODO #Sruthi: full implementation including the getWaitDuration()
 *               helper that returns elapsed time between this event and
 *               the patient's arrivalTime.
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
        // TODO #Sruthi: required no-arg constructor for JPA
    }

    public TriageEventLog(Patient patient, EventType eventType, String roomId) {
        // TODO #Sruthi: assign fields and set timestamp to LocalDateTime.now()
    }

    /**
     * Returns the wait duration in seconds between the patient's arrival
     * and this event timestamp.
     */
    public long getWaitDuration() {
        // TODO #Sruthi: compute Duration.between(patient.arrivalTime, this.timestamp).toSeconds()
        return 0L;
    }

    // ------------------------------------------------------------------
    // Getters / Setters
    // TODO #Sruthi: implement all getters and setters
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
