package com.ersim.repository;

import com.ersim.model.Patient;
import com.ersim.model.enums.PatientStatus;
import com.ersim.model.enums.TriageLevel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Repository tests using H2 in-memory DB.
 */
@DataJpaTest
class PatientRepositoryTest {

    @Autowired
    private PatientRepository repository;

    @Test
    void saveAndFindById() {
        Patient p = new Patient("p1", "Alice", 30, TriageLevel.ESI_2_EMERGENT);
        repository.save(p);

        Optional<Patient> found = repository.findById("p1");
        assertTrue(found.isPresent());
        assertEquals("Alice", found.get().getName());
        assertEquals(30, found.get().getAge());
        assertEquals(TriageLevel.ESI_2_EMERGENT, found.get().getTriageLevel());
    }

    @Test
    void findByStatus_returnsOnlyMatchingPatients() {
        Patient waiting = new Patient("p1", "A", 30, TriageLevel.ESI_3_URGENT);
        Patient discharged = new Patient("p2", "B", 40, TriageLevel.ESI_4_LESS_URGENT);
        discharged.setStatus(PatientStatus.DISCHARGED);
        repository.save(waiting);
        repository.save(discharged);

        List<Patient> waitingList = repository.findByStatus(PatientStatus.WAITING);
        assertEquals(1, waitingList.size());
        assertEquals("p1", waitingList.get(0).getPatientId());
    }

    @Test
    void findByTriageLevel_returnsOnlyMatchingLevel() {
        repository.save(new Patient("p1", "A", 30, TriageLevel.ESI_1_IMMEDIATE));
        repository.save(new Patient("p2", "B", 30, TriageLevel.ESI_3_URGENT));
        repository.save(new Patient("p3", "C", 30, TriageLevel.ESI_1_IMMEDIATE));

        List<Patient> esi1 = repository.findByTriageLevel(TriageLevel.ESI_1_IMMEDIATE);
        assertEquals(2, esi1.size());
        assertTrue(esi1.stream().allMatch(p -> p.getTriageLevel() == TriageLevel.ESI_1_IMMEDIATE));
    }

    @Test
    void findByArrivalTimeBetween_returnsRange() {
        LocalDateTime t0 = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime t1 = LocalDateTime.of(2026, 1, 1, 12, 0);
        LocalDateTime t2 = LocalDateTime.of(2026, 1, 2, 12, 0);
        LocalDateTime t3 = LocalDateTime.of(2026, 1, 3, 12, 0);

        Patient inRange = new Patient("p1", "A", 30, TriageLevel.ESI_3_URGENT);
        inRange.setArrivalTime(t2);
        Patient outOfRange = new Patient("p2", "B", 30, TriageLevel.ESI_3_URGENT);
        outOfRange.setArrivalTime(t0);

        repository.save(inRange);
        repository.save(outOfRange);

        List<Patient> result = repository.findByArrivalTimeBetween(t1, t3);
        assertEquals(1, result.size());
        assertEquals("p1", result.get(0).getPatientId());
    }
}
