package com.ersim.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Patient entity, focused on the compareTo() ordering
 * used by PriorityBlockingQueue.
 *
 * TODO #Ferdi: implement all tests below.
 */
class PatientTest {

    @Test
    void compareTo_lowerEsiLevel_isHigherPriority() {
        // TODO #Ferdi: ESI_1 should compare < ESI_3
        fail("not implemented");
    }

    @Test
    void compareTo_sameEsiLevel_earlierArrivalIsHigherPriority() {
        // TODO #Ferdi: same level, earlier arrivalTime -> negative compareTo
        fail("not implemented");
    }

    @Test
    void setTriageLevel_updatesPriority() {
        // TODO #Ferdi: changing triageLevel should also change priority field
        fail("not implemented");
    }

    @Test
    void newPatient_defaultsToWaitingStatus() {
        // TODO #Ferdi: brand-new Patient should have status WAITING
        fail("not implemented");
    }
}
