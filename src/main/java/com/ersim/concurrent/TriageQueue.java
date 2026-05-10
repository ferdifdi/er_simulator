package com.ersim.concurrent;

import com.ersim.model.Patient;
import com.ersim.model.enums.TriageLevel;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Thread-safe priority queue of waiting patients, ordered by ESI level.
 * Wraps a PriorityBlockingQueue and a ReentrantLock for compound operations
 * such as upgrading a patient's triage level (which requires removing and
 * re-inserting the patient atomically).
 */
@Component
public class TriageQueue {

    private final PriorityBlockingQueue<Patient> queue = new PriorityBlockingQueue<>();
    private final ReentrantLock lock = new ReentrantLock();

    /**
     * Insert a patient into the priority queue.
     */
    public void enqueue(Patient p) {
        if (p == null) return;
        queue.add(p);
    }

    /**
     * Remove and return the highest-priority patient. Blocks if empty.
     */
    public Patient dequeue() throws InterruptedException {
        return queue.take();
    }

    /**
     * Atomically remove a patient by id, update their triage level, then re-insert.
     */
    public void upgradeLevel(String id, TriageLevel level) {
        if (id == null || level == null) return;
        lock.lock();
        try {
            Patient target = null;
            for (Iterator<Patient> it = queue.iterator(); it.hasNext(); ) {
                Patient p = it.next();
                if (id.equals(p.getPatientId())) {
                    target = p;
                    break;
                }
            }
            if (target != null) {
                queue.remove(target);
                target.setTriageLevel(level);
                queue.add(target);
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Returns a non-mutating snapshot of patients currently in the queue,
     * sorted by priority. Useful for the GUI and REST /queue endpoint.
     */
    public List<Patient> getQueueSnapshot() {
        List<Patient> copy = new ArrayList<>(queue);
        Collections.sort(copy);
        return Collections.unmodifiableList(copy);
    }

    public int size() {
        return queue.size();
    }
}
