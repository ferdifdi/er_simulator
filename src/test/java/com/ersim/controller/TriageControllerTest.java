package com.ersim.controller;

import com.ersim.model.Patient;
import com.ersim.model.enums.TriageLevel;
import com.ersim.security.JwtUtil;
import com.ersim.security.SecurityConfig;
import com.ersim.service.TriageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * REST controller tests using MockMvc.
 * @WithMockUser supplies a pre-authenticated principal.
 * .with(csrf()) satisfies CSRF protection on mutating requests.
 */
@WebMvcTest(TriageController.class)
@Import(SecurityConfig.class)
@WithMockUser(roles = "ADMIN")
class TriageControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean  private TriageService service;
    @MockBean  private JwtUtil jwtUtil;

    @Test
    void postPatients_returns201() throws Exception {
        Patient p = new Patient("p1", "Alice", 30, TriageLevel.ESI_2_EMERGENT);
        when(service.admitPatient(any(Patient.class))).thenReturn(p);

        mockMvc.perform(post("/patients")
                        .with(csrf())
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
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"ESI_1_IMMEDIATE\""))
                .andExpect(status().isOk());

        verify(service).upgradeTriageLevel(eq("p1"), eq(TriageLevel.ESI_1_IMMEDIATE));
    }

    @Test
    void deletePatient_returns200() throws Exception {
        mockMvc.perform(delete("/patients/{id}", "p1").with(csrf()))
                .andExpect(status().isOk());

        verify(service).dischargePatient("p1");
    }

    @Test
    @WithMockUser(roles = "NURSE")
    void postPatients_asNurse_returns201() throws Exception {
        Patient p = new Patient("p2", "Bob", 45, TriageLevel.ESI_3_URGENT);
        when(service.admitPatient(any(Patient.class))).thenReturn(p);

        mockMvc.perform(post("/patients")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(p)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void postPatients_asDoctor_returns403() throws Exception {
        Patient p = new Patient("p3", "Carol", 55, TriageLevel.ESI_4_LESS_URGENT);

        mockMvc.perform(post("/patients")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(p)))
                .andExpect(status().isForbidden());
    }
}
