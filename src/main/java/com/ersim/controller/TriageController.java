package com.ersim.controller;

import com.ersim.concurrent.TreatmentRoom;
import com.ersim.model.Patient;
import com.ersim.model.enums.TriageLevel;
import com.ersim.service.TriageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API for the ER Triage Simulator.
 *
 * Endpoints:
 *   POST   /patients          – admit a new patient
 *   GET    /queue             – list current waiting patients
 *   GET    /rooms             – list room statuses
 *   GET    /report            – aggregate statistics
 *   PUT    /patients/{id}/level – upgrade triage level
 *   DELETE /patients/{id}     – discharge / remove a patient
 *
 * TODO #Ferdi: full endpoint implementations and Postman testing.
 */
@RestController
@RequestMapping("/")
public class TriageController {

    @Autowired
    private TriageService service;

    @PostMapping("/patients")
    public ResponseEntity<Patient> admitPatient(@RequestBody Patient patient) {
        // TODO #Ferdi: call service.admitPatient(patient), return 201
        return null;
    }

    @GetMapping("/queue")
    public ResponseEntity<List<Patient>> getQueue() {
        // TODO #Ferdi: return service.getQueueStatus()
        return null;
    }

    @GetMapping("/rooms")
    public ResponseEntity<List<TreatmentRoom.RoomStatusSnapshot>> getRooms() {
        // TODO #Ferdi: return service.getRoomStatus()
        return null;
    }

    @GetMapping("/report")
    public ResponseEntity<Object> getReport() {
        // TODO #Ferdi: return service.getReport()
        return null;
    }

    @PutMapping("/patients/{id}/level")
    public ResponseEntity<Void> upgradeLevel(@PathVariable("id") String id,
                                             @RequestBody TriageLevel newLevel) {
        // TODO #Ferdi: call service.upgradeTriageLevel(id, newLevel)
        return null;
    }

    @DeleteMapping("/patients/{id}")
    public ResponseEntity<Void> dischargePatient(@PathVariable("id") String id) {
        // TODO #Ferdi: call service.dischargePatient(id)
        return null;
    }
}
