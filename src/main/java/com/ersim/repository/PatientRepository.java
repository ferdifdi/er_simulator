package com.ersim.repository;

import com.ersim.model.Patient;
import com.ersim.model.enums.PatientStatus;
import com.ersim.model.enums.TriageLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Spring Data JPA repository for Patient entities.
 * Spring Data generates implementations from the method names below.
 */
@Repository
public interface PatientRepository extends JpaRepository<Patient, String> {

    List<Patient> findByStatus(PatientStatus status);

    List<Patient> findByTriageLevel(TriageLevel triageLevel);

    List<Patient> findByArrivalTimeBetween(LocalDateTime start, LocalDateTime end);
}
