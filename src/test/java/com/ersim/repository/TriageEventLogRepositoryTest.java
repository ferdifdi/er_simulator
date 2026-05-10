package com.ersim.repository;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Repository tests for TriageEventLog using H2 in-memory DB.
 *
 * TODO #Sruthi: implement all tests below.
 */
@DataJpaTest
class TriageEventLogRepositoryTest {

    @Test
    void saveAndRetrieveLog() {
        // TODO #Sruthi: persist log, retrieve, fields equal
        fail("not implemented");
    }

    @Test
    void findByPatient_returnsOnlyThatPatientsLogs() {
        // TODO #Sruthi: insert logs for two patients, query one, expect only that one's logs
        fail("not implemented");
    }

    @Test
    void findByEventType_returnsOnlyMatchingType() {
        // TODO #Sruthi: query ADMITTED returns only ADMITTED logs
        fail("not implemented");
    }

    @Test
    void findByTimestampBetween_returnsRange() {
        // TODO #Sruthi: window query returns only logs in range
        fail("not implemented");
    }

    @Test
    void getWaitDuration_returnsPositiveSeconds() {
        // TODO #Sruthi: log.getWaitDuration() > 0 for past arrival
        fail("not implemented");
    }
}
