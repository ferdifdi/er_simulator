package com.ersim.concurrent;

import com.ersim.model.Patient;
import com.ersim.model.enums.TriageLevel;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Thread-safe priority queue of waiting patients, ordered by ESI level.
 * Wraps a PriorityBlockingQueue and a ReentrantLock for compound operations
 * such as upgrading a patient's triage level (which requires removing and
 * re-inserting the patient atomically).
 *
 * TODO #Ferdi: full implementation, including thread-safe upgradeLevel
 *              and snapshot operations.
 */
@Component
public class TriageQueue {

    private final PriorityBlockingQueue<Patient> queue = new PriorityBlockingQueue<>();
    private final ReentrantLock lock = new ReentrantLock();

    /**
     * Insert a patient into the priority queue.
     */
    public void enqueue(Patient p) {
        // TODO #Ferdi: add patient to queue (PriorityBlockingQueue.add is thread-safe)
    }

    /**
     * Remove and return the highest-priority patient. Blocks if empty.
     */
    public Patient dequeue() throws InterruptedException {
        // TODO #Ferdi: return queue.take()
        return null;
    }

    /**
     * Atomically remove a patient by id, update their triage level, then re-insert.
     */
    public void upgradeLevel(String id, TriageLevel level) {
        // TODO #Ferdi: acquire lock, find by id, remove, set new level, re-add, release lock
    }

    /**
     * Returns a non-mutating snapshot of patients currently in the queue,
     * sorted by priority. Useful for the GUI and REST /queue endpoint.
     */
    public List<Patient> getQueueSnapshot() {
        // TODO #Ferdi: return a sorted, immutable copy of the queue contents
        return List.of();
    }

    public int size() {
        // TODO #Ferdi: return queue.size()
        return 0;
    }
}
