package com.ersim.concurrent;

import com.ersim.model.Patient;
import com.ersim.model.enums.TriageLevel;

import java.util.Random;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Background thread that simulates patient arrivals at the ER. Periodically
 * generates a Patient with a random triage level and pushes it onto the
 * shared TriageQueue.
 */
public class PatientArrivalThread implements Runnable {

    private final TriageQueue queue;
    private final Consumer<Patient> admitter;
    private final Random random = new Random();
    private volatile boolean running = true;
    private static final String[] FAKE_NAMES = {
        "John Smith", "Jane Doe", "Bob Johnson", "Alice Williams",
        "Charlie Brown", "Diana Prince", "Eve Davis", "Frank Miller"
    };

    public PatientArrivalThread(TriageQueue queue) {
        this.queue = queue;
        this.admitter = queue::enqueue;
    }

    public PatientArrivalThread(Consumer<Patient> admitter) {
        this.queue = null;
        this.admitter = admitter;
    }

    @Override
    public void run() {
        while (running) {
            Patient patient = generatePatient();
            admitter.accept(patient);
            try {
                Thread.sleep(1000 + random.nextInt(3000));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    /**
     * Generates a synthetic Patient with a random ESI level, name, and ID.
     */
    public Patient generatePatient() {
        String patientId = UUID.randomUUID().toString();
        String name = FAKE_NAMES[random.nextInt(FAKE_NAMES.length)];
        int age = 18 + random.nextInt(73);
        TriageLevel triageLevel = TriageLevel.values()[random.nextInt(TriageLevel.values().length)];
        return new Patient(patientId, name, age, triageLevel);
    }

    public void stop() {
        this.running = false;
    }
}
