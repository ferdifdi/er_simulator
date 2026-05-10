package com.ersim.concurrent;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit + concurrency tests for TriageQueue.
 *
 * TODO #Ferdi: implement all tests below.
 */
class TriageQueueTest {

    @Test
    void enqueue_thenDequeue_returnsHighestPriorityFirst() {
        // TODO #Ferdi: push ESI_3, ESI_1, ESI_5 — dequeue order must be 1, 3, 5
        fail("not implemented");
    }

    @Test
    void upgradeLevel_movesPatientForwardInQueue() {
        // TODO #Ferdi: enqueue at ESI_3, upgrade to ESI_1, then dequeue — should come out first
        fail("not implemented");
    }

    @Test
    void getQueueSnapshot_returnsCopy_notLiveView() {
        // TODO #Ferdi: mutating the returned list must not affect the queue
        fail("not implemented");
    }

    @Test
    void concurrentEnqueueDequeue_noLostPatients() throws InterruptedException {
        // TODO #Ferdi: stress test — N producer threads enqueue, M consumers dequeue.
        //              Total dequeued count must equal total enqueued.
        fail("not implemented");
    }
}
