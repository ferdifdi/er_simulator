package com.ersim.model;

import com.ersim.model.enums.PatientStatus;
import com.ersim.model.enums.TriageLevel;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Patient entity, focused on the compareTo() ordering
 * used by PriorityBlockingQueue.
 */
class PatientTest {

    @Test
    void compareTo_lowerEsiLevel_isHigherPriority() {
        Patient critical = new Patient("p1", "Alice", 40, TriageLevel.ESI_1_IMMEDIATE);
        Patient urgent = new Patient("p2", "Bob", 35, TriageLevel.ESI_3_URGENT);
        assertTrue(critical.compareTo(urgent) < 0,
                "ESI_1 should have higher priority (compareTo < 0) than ESI_3");
        assertTrue(urgent.compareTo(critical) > 0);
    }

    @Test
    void compareTo_sameEsiLevel_earlierArrivalIsHigherPriority() {
        Patient earlier = new Patient("p1", "Alice", 40, TriageLevel.ESI_3_URGENT);
        Patient later = new Patient("p2", "Bob", 35, TriageLevel.ESI_3_URGENT);
        earlier.setArrivalTime(LocalDateTime.of(2026, 1, 1, 10, 0));
        later.setArrivalTime(LocalDateTime.of(2026, 1, 1, 11, 0));
        assertTrue(earlier.compareTo(later) < 0);
        assertTrue(later.compareTo(earlier) > 0);
    }

    @Test
    void setTriageLevel_updatesPriority() {
        Patient p = new Patient("p1", "Alice", 40, TriageLevel.ESI_3_URGENT);
        int initial = p.getPriority();
        p.setTriageLevel(TriageLevel.ESI_1_IMMEDIATE);
        assertEquals(TriageLevel.ESI_1_IMMEDIATE.ordinal(), p.getPriority());
        assertNotEquals(initial, p.getPriority());
    }

    @Test
    void newPatient_defaultsToWaitingStatus() {
        Patient p = new Patient("p1", "Alice", 40, TriageLevel.ESI_3_URGENT);
        assertEquals(PatientStatus.WAITING, p.getStatus());
        assertNotNull(p.getArrivalTime());
    }
}
