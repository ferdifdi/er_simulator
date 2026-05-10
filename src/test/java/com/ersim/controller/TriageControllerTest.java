package com.ersim.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * REST controller tests using MockMvc.
 *
 * TODO #Ferdi: implement all tests below using MockMvc + @MockBean TriageService.
 */
@WebMvcTest(TriageController.class)
class TriageControllerTest {

    @Test
    void postPatients_returns201() {
        // TODO #Ferdi: POST /patients with JSON body, expect 201 + Patient body
        fail("not implemented");
    }

    @Test
    void getQueue_returnsJsonArray() {
        // TODO #Ferdi: GET /queue, expect 200 + JSON array
        fail("not implemented");
    }

    @Test
    void getRooms_returnsJsonArray() {
        // TODO #Ferdi: GET /rooms, expect 200 + JSON array
        fail("not implemented");
    }

    @Test
    void putUpgradeLevel_returns200() {
        // TODO #Ferdi: PUT /patients/{id}/level, expect 200
        fail("not implemented");
    }

    @Test
    void deletePatient_returns200() {
        // TODO #Ferdi: DELETE /patients/{id}, expect 200
        fail("not implemented");
    }
}
