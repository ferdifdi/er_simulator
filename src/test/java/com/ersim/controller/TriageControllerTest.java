package com.ersim.controller;

import com.ersim.model.Patient;
import com.ersim.model.enums.TriageLevel;
import com.ersim.service.TriageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * REST controller tests using MockMvc.
 */
@WebMvcTest(TriageController.class)
class TriageControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private TriageService service;

    @Test
    void postPatients_returns201() throws Exception {
        Patient p = new Patient("p1", "Alice", 30, TriageLevel.ESI_2_EMERGENT);
        when(service.admitPatient(any(Patient.class))).thenReturn(p);

        mockMvc.perform(post("/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(p)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.patientId").value("p1"))
                .andExpect(jsonPath("$.name").value("Alice"));
    }

    @Test
    void getQueue_returnsJsonArray() throws Exception {
        Patient p = new Patient("p1", "Alice", 30, TriageLevel.ESI_2_EMERGENT);
        when(service.getQueueStatus()).thenReturn(List.of(p));

        mockMvc.perform(get("/queue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].patientId").value("p1"));
    }

    @Test
    void getRooms_returnsJsonArray() throws Exception {
        when(service.getRoomStatus()).thenReturn(List.of());

        mockMvc.perform(get("/rooms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void putUpgradeLevel_returns200() throws Exception {
        mockMvc.perform(put("/patients/{id}/level", "p1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"ESI_1_IMMEDIATE\""))
                .andExpect(status().isOk());

        verify(service).upgradeTriageLevel(eq("p1"), eq(TriageLevel.ESI_1_IMMEDIATE));
    }

    @Test
    void deletePatient_returns200() throws Exception {
        mockMvc.perform(delete("/patients/{id}", "p1"))
                .andExpect(status().isOk());

        verify(service).dischargePatient("p1");
    }
}
