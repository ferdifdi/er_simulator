package com.ersim.service;

import com.ersim.concurrent.TriageQueue;
import com.ersim.model.Patient;
import com.ersim.model.TriageEventLog;
import com.ersim.model.enums.PatientStatus;
import com.ersim.model.enums.TriageLevel;
import com.ersim.repository.PatientRepository;
import com.ersim.repository.TriageEventLogRepository;
import com.ersim.websocket.WebSocketBroadcaster;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Integration tests for TriageService.
 */
@SpringBootTest
@TestPropertySource(properties = {
        "er.rooms.count=0",
        "er.simulation.autostart=false"
})
class TriageServiceTest {

    @Autowired private TriageService service;
    @Autowired private TriageQueue queue;

    @MockBean private PatientRepository patientRepository;
    @MockBean private TriageEventLogRepository logRepository;
    @MockBean private WebSocketBroadcaster broadcaster;

    @BeforeEach
    void resetRepoStubs() {
        reset(patientRepository, logRepository, broadcaster);
        when(patientRepository.save(any(Patient.class)))
                .thenAnswer(inv -> inv.getArgument(0));
        when(logRepository.save(any(TriageEventLog.class)))
                .thenAnswer(inv -> inv.getArgument(0));
        // Drain anything left behind from prior tests
        while (queue.size() > 0) {
            try { queue.dequeue(); } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    @Test
    void admitPatient_persistsAndEnqueues() {
        int before = queue.size();
        Patient p = new Patient(UUID.randomUUID().toString(), "Alice", 30, TriageLevel.ESI_3_URGENT);

        Patient result = service.admitPatient(p);

        assertNotNull(result);
        verify(patientRepository).save(any(Patient.class));
        assertEquals(before + 1, queue.size());
        verify(broadcaster).broadcastQueue();
    }

    @Test
    void dischargePatient_setsStatusAndLogs() {
        String id = UUID.randomUUID().toString();
        Patient stored = new Patient(id, "Bob", 50, TriageLevel.ESI_2_EMERGENT);
        when(patientRepository.findById(id)).thenReturn(Optional.of(stored));

        service.dischargePatient(id);

        assertEquals(PatientStatus.DISCHARGED, stored.getStatus());
        verify(patientRepository).save(stored);
        verify(logRepository).save(any(TriageEventLog.class));
    }

    @Test
    void upgradeTriageLevel_repositionsInQueue() throws InterruptedException {
        String id = UUID.randomUUID().toString();
        Patient p = new Patient(id, "Carol", 22, TriageLevel.ESI_3_URGENT);
        Patient other = new Patient(UUID.randomUUID().toString(), "Other", 22, TriageLevel.ESI_2_EMERGENT);
        when(patientRepository.findById(id)).thenReturn(Optional.of(p));

        service.admitPatient(other);
        service.admitPatient(p);

        service.upgradeTriageLevel(id, TriageLevel.ESI_1_IMMEDIATE);

        Patient head = queue.dequeue();
        assertEquals(id, head.getPatientId());
        assertEquals(TriageLevel.ESI_1_IMMEDIATE, head.getTriageLevel());
    }

    @Test
    void stressTest_50Arrivals_noRaceConditions() throws InterruptedException {
        int count = 50;
        int beforeQueue = queue.size();
        ExecutorService pool = Executors.newFixedThreadPool(10);
        CountDownLatch done = new CountDownLatch(count);
        AtomicInteger failures = new AtomicInteger();

        for (int i = 0; i < count; i++) {
            pool.submit(() -> {
                try {
                    Patient p = new Patient(UUID.randomUUID().toString(),
                            "stress", 1, TriageLevel.ESI_3_URGENT);
                    service.admitPatient(p);
                } catch (Throwable t) {
                    failures.incrementAndGet();
                } finally {
                    done.countDown();
                }
            });
        }

        assertTrue(done.await(10, TimeUnit.SECONDS));
        pool.shutdownNow();

        assertEquals(0, failures.get(), "no admitPatient call should throw");
        verify(patientRepository, times(count)).save(any(Patient.class));
        assertEquals(beforeQueue + count, queue.size());
    }
}
