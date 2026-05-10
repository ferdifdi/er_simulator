package com.ersim.concurrent;

import com.ersim.model.Patient;

import java.util.Random;

/**
 * Background thread that simulates patient arrivals at the ER. Periodically
 * generates a Patient with a random triage level and pushes it onto the
 * shared TriageQueue.
 *
 * TODO #Sruthi: full implementation of run() and generatePatient().
 */
public class PatientArrivalThread implements Runnable {

    private final TriageQueue queue;
    private final Random random = new Random();
    private volatile boolean running = true;

    public PatientArrivalThread(TriageQueue queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        // TODO #Sruthi: loop while `running` — generate patient, enqueue,
        //               sleep for a random interval, handle InterruptedException.
    }

    /**
     * Generates a synthetic Patient with a random ESI level, name, and ID.
     */
    public Patient generatePatient() {
        // TODO #Sruthi: create Patient with UUID, fake name, age, random TriageLevel
        return null;
    }

    public void stop() {
        // TODO #Sruthi: set running = false to break the run loop
        this.running = false;
    }
}
