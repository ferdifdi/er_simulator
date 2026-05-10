package com.ersim.controller;

import com.ersim.concurrent.TreatmentRoom;
import com.ersim.model.Patient;
import com.ersim.model.enums.TriageLevel;
import com.ersim.service.TriageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API for the ER Triage Simulator.
 *
 * Endpoints:
 *   POST   /patients            – admit a new patient
 *   GET    /queue               – list current waiting patients
 *   GET    /rooms               – list room statuses
 *   GET    /report              – aggregate statistics
 *   PUT    /patients/{id}/level – upgrade triage level
 *   DELETE /patients/{id}       – discharge / remove a patient
 */
@RestController
public class TriageController {

    @Autowired
    private TriageService service;

    @PostMapping("/patients")
    public ResponseEntity<Patient> admitPatient(@RequestBody Patient patient) {
        Patient saved = service.admitPatient(patient);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/queue")
    public ResponseEntity<List<Patient>> getQueue() {
        return ResponseEntity.ok(service.getQueueStatus());
    }

    @GetMapping("/rooms")
    public ResponseEntity<List<TreatmentRoom.RoomStatusSnapshot>> getRooms() {
        return ResponseEntity.ok(service.getRoomStatus());
    }

    @GetMapping("/report")
    public ResponseEntity<Object> getReport() {
        return ResponseEntity.ok(service.getReport());
    }

    @PutMapping("/patients/{id}/level")
    public ResponseEntity<Void> upgradeLevel(@PathVariable("id") String id,
                                             @RequestBody TriageLevel newLevel) {
        service.upgradeTriageLevel(id, newLevel);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/patients/{id}")
    public ResponseEntity<Void> dischargePatient(@PathVariable("id") String id) {
        service.dischargePatient(id);
        return ResponseEntity.ok().build();
    }
}
