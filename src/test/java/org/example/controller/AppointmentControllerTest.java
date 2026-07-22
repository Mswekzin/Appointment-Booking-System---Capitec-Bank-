package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.CreateAppointmentRequest;
import org.example.model.Branch;
import org.example.repository.BranchRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BranchRepository branchRepository;

    @Test
    void createAppointmentReturnsCreated() throws Exception {
        Branch branch = branchRepository.save(new Branch("API Branch", "22 API Way", LocalTime.of(9, 0), LocalTime.of(17, 0), 30));

        CreateAppointmentRequest payload = new CreateAppointmentRequest(
                branch.getId(),
                "Alice Example",
                "alice@example.com",
                "+12025550111",
                LocalDateTime.now().plusDays(1).withHour(11).withMinute(0).withSecond(0).withNano(0)
        );

        mockMvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.appointmentId").isNumber())
                .andExpect(jsonPath("$.status").value("BOOKED"));
    }
}

