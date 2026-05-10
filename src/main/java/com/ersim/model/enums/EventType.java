package com.ersim.model.enums;

/**
 * Types of events recorded in TriageEventLog.
 *
 * TODO #Sruthi: add TRANSFERRED event type if status TRANSFERRED is used.
 */
public enum EventType {
    ADMITTED,
    ROOM_ASSIGNED,
    TREATMENT_STARTED,
    DISCHARGED,
    LEVEL_UPGRADED
}
