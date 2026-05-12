package com.ersim.concurrent;

import com.ersim.model.Doctor;
import com.ersim.model.Patient;
import com.ersim.model.enums.RoomStatus;
import com.ersim.model.enums.TriageLevel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TreatmentRoom worker. TreatmentRoom is now a thin
 * in-memory state holder — persistence and patient-status updates live
 * in TriageService — so these tests assert on room state only.
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
    }

    @Test
    void releaseRoom_marksRoomAvailableAndReturnsPatient() {
        TriageQueue q = new TriageQueue();
        Doctor doc = new Doctor("D1", "Dr. Who", "General");
        TreatmentRoom room = new TreatmentRoom("R1", q, doc);

        Patient p = new Patient("p1", "Alice", 30, TriageLevel.ESI_2_EMERGENT);
        room.assignPatient(p);
        Patient released = room.releaseRoom();

        assertSame(p, released);
        assertEquals(RoomStatus.AVAILABLE, room.getStatus());
        assertNull(room.getCurrentPatient());
    }

    @Test
    void releaseRoom_secondCallReturnsNull() {
        TriageQueue q = new TriageQueue();
        Doctor doc = new Doctor("D1", "Dr. Who", "General");
        TreatmentRoom room = new TreatmentRoom("R1", q, doc);

        Patient p = new Patient("p1", "Alice", 30, TriageLevel.ESI_2_EMERGENT);
        room.assignPatient(p);
        assertSame(p, room.releaseRoom());
        assertNull(room.releaseRoom(), "second release must be a no-op (idempotent)");
    }

    @Test
    void run_picksUpAndOccupiesRoom() throws InterruptedException {
        TriageQueue q = new TriageQueue();
        Doctor doc = new Doctor("D1", "Dr. Who", "General");
        TreatmentRoom room = new TreatmentRoom("R1", q, doc);

        Patient p = new Patient("p1", "Alice", 30, TriageLevel.ESI_1_IMMEDIATE);
        q.enqueue(p);

        Thread t = new Thread(room, "test-room");
        t.start();

        long deadline = System.currentTimeMillis() + 2_000;
        while (System.currentTimeMillis() < deadline && room.getStatus() != RoomStatus.OCCUPIED) {
            Thread.sleep(20);
        }
        assertEquals(RoomStatus.OCCUPIED, room.getStatus());
        assertSame(p, room.getCurrentPatient());

        room.stop();
        t.interrupt();
        t.join(2_000);
    }
}
