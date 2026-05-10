package com.ersim.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for TriageService.
 *
 * TODO #Ferdi: implement all tests below using @MockBean for repositories
 *              and broadcaster.
 */
@SpringBootTest
class TriageServiceTest {

    @Test
    void admitPatient_persistsAndEnqueues() {
        // TODO #Ferdi: verify save called, queue size grew by 1
        fail("not implemented");
    }

    @Test
    void dischargePatient_setsStatusAndLogs() {
        // TODO #Ferdi: verify patient status DISCHARGED and log saved
        fail("not implemented");
    }

    @Test
    void upgradeTriageLevel_repositionsInQueue() {
        // TODO #Ferdi: admit ESI_3, upgrade to ESI_1, head of queue is now this patient
        fail("not implemented");
    }

    @Test
    void stressTest_50Arrivals_noRaceConditions() throws InterruptedException {
        // TODO #Ferdi: spawn 50 concurrent admit calls, assert all reach DB & queue
        fail("not implemented");
    }
}
