package com.ersim.repository;

import com.ersim.model.Patient;
import com.ersim.model.TriageEventLog;
import com.ersim.model.enums.EventType;
import com.ersim.model.enums.TriageLevel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Repository tests for TriageEventLog using H2 in-memory DB.
 */
@DataJpaTest
class TriageEventLogRepositoryTest {

    @Autowired
    private PatientRepository patientRepository;
    
    @Autowired
    private TriageEventLogRepository logRepository;

    @Test
    void saveAndRetrieveLog() {
        Patient p = new Patient("p1", "Alice", 30, TriageLevel.ESI_2_EMERGENT);
        patientRepository.save(p);
        
        TriageEventLog log = new TriageEventLog(p, EventType.ADMITTED, "R1");
        TriageEventLog saved = logRepository.save(log);
        
        assertNotNull(saved.getLogId());
        assertEquals(p.getPatientId(), saved.getPatient().getPatientId());
        assertEquals(EventType.ADMITTED, saved.getEventType());
        assertEquals("R1", saved.getRoomId());
        assertNotNull(saved.getTimestamp());
    }

    @Test
    void findByPatient_returnsOnlyThatPatientsLogs() {
        Patient p1 = new Patient("p1", "Alice", 30, TriageLevel.ESI_2_EMERGENT);
        Patient p2 = new Patient("p2", "Bob", 40, TriageLevel.ESI_3_URGENT);
        patientRepository.save(p1);
        patientRepository.save(p2);
        
        logRepository.save(new TriageEventLog(p1, EventType.ADMITTED, "R1"));
        logRepository.save(new TriageEventLog(p1, EventType.TREATMENT_STARTED, "R1"));
        logRepository.save(new TriageEventLog(p2, EventType.ADMITTED, "R2"));
        
        List<TriageEventLog> p1Logs = logRepository.findByPatient(p1);
        assertEquals(2, p1Logs.size());
        assertTrue(p1Logs.stream().allMatch(log -> log.getPatient().getPatientId().equals("p1")));
        
        List<TriageEventLog> p2Logs = logRepository.findByPatient(p2);
        assertEquals(1, p2Logs.size());
        assertEquals("p2", p2Logs.get(0).getPatient().getPatientId());
    }

    @Test
    void findByEventType_returnsOnlyMatchingType() {
        Patient p = new Patient("p1", "Alice", 30, TriageLevel.ESI_2_EMERGENT);
        patientRepository.save(p);
        
        logRepository.save(new TriageEventLog(p, EventType.ADMITTED, "R1"));
        logRepository.save(new TriageEventLog(p, EventType.ADMITTED, "R1"));
        logRepository.save(new TriageEventLog(p, EventType.TREATMENT_STARTED, "R1"));
        logRepository.save(new TriageEventLog(p, EventType.DISCHARGED, "R1"));
        
        List<TriageEventLog> admitted = logRepository.findByEventType(EventType.ADMITTED);
        assertEquals(2, admitted.size());
        assertTrue(admitted.stream().allMatch(log -> log.getEventType() == EventType.ADMITTED));
        
        List<TriageEventLog> discharged = logRepository.findByEventType(EventType.DISCHARGED);
        assertEquals(1, discharged.size());
        assertEquals(EventType.DISCHARGED, discharged.get(0).getEventType());
    }

    @Test
    void findByTimestampBetween_returnsRange() {
        Patient p = new Patient("p1", "Alice", 30, TriageLevel.ESI_2_EMERGENT);
        patientRepository.save(p);
        
        LocalDateTime t0 = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime t1 = LocalDateTime.of(2026, 1, 1, 12, 0);
        LocalDateTime t2 = LocalDateTime.of(2026, 1, 2, 12, 0);
        LocalDateTime t3 = LocalDateTime.of(2026, 1, 3, 0, 0);
        
        TriageEventLog log1 = new TriageEventLog(p, EventType.ADMITTED, "R1");
        log1.setTimestamp(t0);
        TriageEventLog log2 = new TriageEventLog(p, EventType.TREATMENT_STARTED, "R1");
        log2.setTimestamp(t2);
        TriageEventLog log3 = new TriageEventLog(p, EventType.DISCHARGED, "R1");
        log3.setTimestamp(t3);
        
        logRepository.save(log1);
        logRepository.save(log2);
        logRepository.save(log3);
        
        List<TriageEventLog> range = logRepository.findByTimestampBetween(t1, t3);
        assertEquals(2, range.size(), "Should contain logs at t2 and t3");
    }

    @Test
    void getWaitDuration_returnsPositiveSeconds() {
        Patient p = new Patient("p1", "Alice", 30, TriageLevel.ESI_2_EMERGENT);
        p.setArrivalTime(LocalDateTime.now().minusSeconds(5));
        patientRepository.save(p);
        
        TriageEventLog log = new TriageEventLog(p, EventType.ADMITTED, "R1");
        logRepository.save(log);
        
        long wait = log.getWaitDuration();
        assertTrue(wait >= 0, "Wait duration should be positive or zero");
    }
}
