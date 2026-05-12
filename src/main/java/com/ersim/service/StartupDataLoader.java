package com.ersim.service;

import com.ersim.model.AppUser;
import com.ersim.model.Patient;
import com.ersim.model.enums.Role;
import com.ersim.model.enums.TriageLevel;
import com.ersim.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * Runs once on startup to:
 *   1. Seed default users (admin, nurse, doctor) if the table is empty.
 *   2. Pre-populate the ER queue with 15 realistic patients so the
 *      dashboard shows live data immediately.
 */
@Component
public class StartupDataLoader implements ApplicationRunner {

    @Autowired private TriageService triageService;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Value("${ersim.rooms.count:3}")
    private int roomsCount;

    private static final Object[][] INITIAL_PATIENTS = {
        {"Maria Garcia",      45, TriageLevel.ESI_1_IMMEDIATE},
        {"James Wilson",      67, TriageLevel.ESI_2_EMERGENT},
        {"Linda Martinez",    32, TriageLevel.ESI_3_URGENT},
        {"Robert Taylor",     58, TriageLevel.ESI_2_EMERGENT},
        {"Patricia Anderson", 41, TriageLevel.ESI_3_URGENT},
        {"Michael Thomas",    29, TriageLevel.ESI_4_LESS_URGENT},
        {"Barbara Jackson",   73, TriageLevel.ESI_1_IMMEDIATE},
        {"William Harris",    55, TriageLevel.ESI_3_URGENT},
        {"Elizabeth White",   38, TriageLevel.ESI_5_NON_URGENT},
        {"David Thompson",    62, TriageLevel.ESI_2_EMERGENT},
        {"Susan Moore",       47, TriageLevel.ESI_4_LESS_URGENT},
        {"Richard Lee",       25, TriageLevel.ESI_3_URGENT},
        {"Jessica Clark",     34, TriageLevel.ESI_5_NON_URGENT},
        {"Joseph Lewis",      80, TriageLevel.ESI_2_EMERGENT},
        {"Sarah Robinson",    19, TriageLevel.ESI_4_LESS_URGENT},
    };

    @Override
    public void run(ApplicationArguments args) {
        seedUsers();
        if (roomsCount > 0) seedPatients();
    }

    private void seedUsers() {
        if (userRepository.count() > 0) return;
        userRepository.saveAll(List.of(
            new AppUser("admin",  passwordEncoder.encode("admin123"),  Role.ADMIN),
            new AppUser("nurse",  passwordEncoder.encode("nurse123"),  Role.NURSE),
            new AppUser("doctor", passwordEncoder.encode("doc123"),    Role.DOCTOR)
        ));
    }

    private void seedPatients() {
        for (Object[] row : INITIAL_PATIENTS) {
            Patient p = new Patient(
                UUID.randomUUID().toString(),
                (String)     row[0],
                (Integer)    row[1],
                (TriageLevel) row[2]
            );
            triageService.admitPatient(p);
        }
    }
}
