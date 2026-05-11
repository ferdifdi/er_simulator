package com.ersim.concurrent;

import com.ersim.model.Patient;
import com.ersim.model.enums.TriageLevel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the PatientArrivalThread synthetic patient generator.
 */
class PatientArrivalThreadTest {

    @Test
    void generatePatient_returnsNonNullWithRandomLevel() {
        TriageQueue q = new TriageQueue();
        PatientArrivalThread thread = new PatientArrivalThread(q);
        Patient p = thread.generatePatient();
        
        assertNotNull(p);
        assertNotNull(p.getPatientId());
        assertNotNull(p.getName());
        assertNotNull(p.getTriageLevel());
        assertTrue(p.getAge() >= 18 && p.getAge() <= 90);
    }

    @Test
    void run_enqueuesPatientsUntilStopped() throws InterruptedException {
        TriageQueue q = new TriageQueue();
        PatientArrivalThread patientThread = new PatientArrivalThread(q);
        Thread thread = new Thread(patientThread);
        thread.start();
        
        Thread.sleep(500);
        patientThread.stop();
        thread.join(1000);
        
        assertTrue(q.size() > 0, "Queue should have at least one patient");
    }

    @Test
    void stop_breaksTheRunLoopWithinReasonableTime() throws InterruptedException {
        TriageQueue q = new TriageQueue();
        PatientArrivalThread patientThread = new PatientArrivalThread(q);
        Thread thread = new Thread(patientThread);
        thread.start();
        
        patientThread.stop();
        boolean joined = thread.join(1000);
        
        assertTrue(joined, "Thread should join within 1 second of stop()");
    }
}
