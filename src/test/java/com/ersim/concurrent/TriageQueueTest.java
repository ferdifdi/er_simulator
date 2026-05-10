package com.ersim.concurrent;

import com.ersim.model.Patient;
import com.ersim.model.enums.TriageLevel;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit + concurrency tests for TriageQueue.
 */
class TriageQueueTest {

    @Test
    void enqueue_thenDequeue_returnsHighestPriorityFirst() throws InterruptedException {
        TriageQueue q = new TriageQueue();
        q.enqueue(new Patient("p3", "Three", 30, TriageLevel.ESI_3_URGENT));
        q.enqueue(new Patient("p1", "One", 30, TriageLevel.ESI_1_IMMEDIATE));
        q.enqueue(new Patient("p5", "Five", 30, TriageLevel.ESI_5_NON_URGENT));

        assertEquals("p1", q.dequeue().getPatientId());
        assertEquals("p3", q.dequeue().getPatientId());
        assertEquals("p5", q.dequeue().getPatientId());
    }

    @Test
    void upgradeLevel_movesPatientForwardInQueue() throws InterruptedException {
        TriageQueue q = new TriageQueue();
        q.enqueue(new Patient("a", "A", 30, TriageLevel.ESI_2_EMERGENT));
        q.enqueue(new Patient("b", "B", 30, TriageLevel.ESI_3_URGENT));

        q.upgradeLevel("b", TriageLevel.ESI_1_IMMEDIATE);

        assertEquals("b", q.dequeue().getPatientId());
    }

    @Test
    void getQueueSnapshot_returnsCopy_notLiveView() {
        TriageQueue q = new TriageQueue();
        q.enqueue(new Patient("p1", "One", 30, TriageLevel.ESI_3_URGENT));

        List<Patient> snap = q.getQueueSnapshot();
        assertEquals(1, snap.size());
        assertThrows(UnsupportedOperationException.class, () -> snap.add(null));
        // Queue itself unaffected
        assertEquals(1, q.size());
    }

    @Test
    void concurrentEnqueueDequeue_noLostPatients() throws InterruptedException {
        TriageQueue q = new TriageQueue();
        int producers = 5;
        int consumers = 5;
        int perProducer = 20;
        int total = producers * perProducer;

        ExecutorService pool = Executors.newFixedThreadPool(producers + consumers);
        CountDownLatch ready = new CountDownLatch(1);
        AtomicInteger consumed = new AtomicInteger();
        CountDownLatch done = new CountDownLatch(total);

        for (int i = 0; i < producers; i++) {
            final int pid = i;
            pool.submit(() -> {
                try { ready.await(); } catch (InterruptedException e) { return; }
                for (int j = 0; j < perProducer; j++) {
                    q.enqueue(new Patient("p-" + pid + "-" + j, "n", 1,
                            TriageLevel.values()[j % TriageLevel.values().length]));
                }
            });
        }

        for (int i = 0; i < consumers; i++) {
            pool.submit(() -> {
                try {
                    ready.await();
                    while (consumed.get() < total) {
                        Patient p = q.dequeue();
                        if (p != null) {
                            consumed.incrementAndGet();
                            done.countDown();
                        }
                    }
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        ready.countDown();
        assertTrue(done.await(10, TimeUnit.SECONDS), "all patients should be consumed");
        assertEquals(total, consumed.get());
        assertEquals(0, q.size());
        pool.shutdownNow();
    }
}
