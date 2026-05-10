package com.ersim.repository;

import com.ersim.model.Patient;
import com.ersim.model.TriageEventLog;
import com.ersim.model.enums.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Spring Data JPA repository for TriageEventLog entries.
 *
 * TODO #Sruthi: confirm that lazy-loaded Patient field doesn't break tests
 *               (may need @Transactional on tests).
 */
@Repository
public interface TriageEventLogRepository extends JpaRepository<TriageEventLog, Long> {

    List<TriageEventLog> findByPatient(Patient patient);

    List<TriageEventLog> findByEventType(EventType eventType);

    List<TriageEventLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
}
