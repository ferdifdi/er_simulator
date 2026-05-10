package com.ersim.model.enums;

/**
 * Lifecycle status of a Patient in the ER.
 *
 * TODO #Sruthi: review whether TRANSFERRED needs a sub-state (e.g., transfer destination).
 */
public enum PatientStatus {
    WAITING,
    IN_TREATMENT,
    DISCHARGED,
    TRANSFERRED
}
