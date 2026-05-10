package com.ersim.concurrent;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TreatmentRoom worker.
 *
 * TODO #Ferdi: implement all tests below.
 */
class TreatmentRoomTest {

    @Test
    void assignPatient_marksRoomOccupied() {
        // TODO #Ferdi: assertEquals(OCCUPIED, room.getStatus()) after assign
        fail("not implemented");
    }

    @Test
    void discharge_marksRoomAvailableAndClearsPatient() {
        // TODO #Ferdi: after discharge, status == AVAILABLE and currentPatient == null
        fail("not implemented");
    }

    @Test
    void run_processesPatientsFromQueue() throws InterruptedException {
        // TODO #Ferdi: enqueue patient, run room briefly, assert patient was treated
        fail("not implemented");
    }
}
