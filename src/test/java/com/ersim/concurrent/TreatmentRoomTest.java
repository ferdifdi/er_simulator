package com.ersim.concurrent;

import com.ersim.model.Doctor;
import com.ersim.model.Patient;
import com.ersim.model.enums.PatientStatus;
import com.ersim.model.enums.RoomStatus;
import com.ersim.model.enums.TriageLevel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TreatmentRoom worker.
 */
class TreatmentRoomTest {

    @Test
    void assignPatient_marksRoomOccupied() {
        TriageQueue q = new TriageQueue();
        Doctor doc = new Doctor("D1", "Dr. Who", "General");
        TreatmentRoom room = new TreatmentRoom("R1", q, doc);

        Patient p = new Patient("p1", "Alice", 30, TriageLevel.ESI_2_EMERGENT);
        room.assignPatient(p);

        assertEquals(RoomStatus.OCCUPIED, room.getStatus());
        assertSame(p, room.getCurrentPatient());
        assertEquals(PatientStatus.IN_TREATMENT, p.getStatus());
    }

    @Test
    void discharge_marksRoomAvailableAndClearsPatient() {
        TriageQueue q = new TriageQueue();
        Doctor doc = new Doctor("D1", "Dr. Who", "General");
        TreatmentRoom room = new TreatmentRoom("R1", q, doc);

        Patient p = new Patient("p1", "Alice", 30, TriageLevel.ESI_2_EMERGENT);
        room.assignPatient(p);
        room.discharge();

        assertEquals(RoomStatus.AVAILABLE, room.getStatus());
        assertNull(room.getCurrentPatient());
        assertEquals(PatientStatus.DISCHARGED, p.getStatus());
    }

    @Test
    void run_processesPatientsFromQueue() throws InterruptedException {
        TriageQueue q = new TriageQueue();
        Doctor doc = new Doctor("D1", "Dr. Who", "General");
        TreatmentRoom room = new TreatmentRoom("R1", q, doc);

        Patient p = new Patient("p1", "Alice", 30, TriageLevel.ESI_1_IMMEDIATE);
        q.enqueue(p);

        Thread t = new Thread(room, "test-room");
        t.start();

        long deadline = System.currentTimeMillis() + 2_000;
        while (System.currentTimeMillis() < deadline && p.getStatus() != PatientStatus.DISCHARGED) {
            Thread.sleep(20);
        }

        room.stop();
        t.interrupt();
        t.join(2_000);

        assertEquals(PatientStatus.DISCHARGED, p.getStatus());
    }
}
