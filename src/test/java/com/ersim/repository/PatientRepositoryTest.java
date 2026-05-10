package com.ersim.repository;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Repository tests using H2 in-memory DB.
 *
 * TODO #Ferdi: implement all tests below.
 */
@DataJpaTest
class PatientRepositoryTest {

    @Test
    void saveAndFindById() {
        // TODO #Ferdi: save patient, retrieve by id, fields equal
        fail("not implemented");
    }

    @Test
    void findByStatus_returnsOnlyMatchingPatients() {
        // TODO #Ferdi: save mixed-status patients, query WAITING, expect only WAITING
        fail("not implemented");
    }

    @Test
    void findByTriageLevel_returnsOnlyMatchingLevel() {
        // TODO #Ferdi: query ESI_1 should return only those patients
        fail("not implemented");
    }

    @Test
    void findByArrivalTimeBetween_returnsRange() {
        // TODO #Ferdi: window query returns only patients in range
        fail("not implemented");
    }
}
