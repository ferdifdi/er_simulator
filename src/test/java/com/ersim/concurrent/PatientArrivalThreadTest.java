package com.ersim.concurrent;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the PatientArrivalThread synthetic patient generator.
 *
 * TODO #Sruthi: implement all tests below.
 */
class PatientArrivalThreadTest {

    @Test
    void generatePatient_returnsNonNullWithRandomLevel() {
        // TODO #Sruthi: assertNotNull(p), assertNotNull(p.getTriageLevel())
        fail("not implemented");
    }

    @Test
    void run_enqueuesPatientsUntilStopped() throws InterruptedException {
        // TODO #Sruthi: start thread, sleep briefly, stop, assert queue.size() > 0
        fail("not implemented");
    }

    @Test
    void stop_breaksTheRunLoopWithinReasonableTime() throws InterruptedException {
        // TODO #Sruthi: thread should join within 1s of stop()
        fail("not implemented");
    }
}
