package com.ersim.model.enums;

/**
 * Emergency Severity Index (ESI) levels.
 * Lower ordinal = higher priority (ESI_1_IMMEDIATE is most urgent).
 */
public enum TriageLevel {
    ESI_1_IMMEDIATE,
    ESI_2_EMERGENT,
    ESI_3_URGENT,
    ESI_4_LESS_URGENT,
    ESI_5_NON_URGENT
}
