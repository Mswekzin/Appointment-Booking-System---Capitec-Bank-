package org.example.service;

import org.example.dto.AppointmentResponse;
import org.example.dto.CreateAppointmentRequest;
import org.example.exception.BookingConflictException;
import org.example.model.Branch;
import org.example.repository.BranchRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
class AppointmentServiceTest {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private BranchRepository branchRepository;

    @Test
    void createAppointmentAndRejectDoubleBooking() {
        Branch branch = branchRepository.save(new Branch("Test Branch", "123 Test St", LocalTime.of(9, 0), LocalTime.of(17, 0), 30));
        LocalDateTime startsAt = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);

        AppointmentResponse first = appointmentService.createAppointment(new CreateAppointmentRequest(
                branch.getId(),
                "Jane Doe",
                "jane@example.com",
                "+12025550123",
                startsAt
        ));

        assertEquals("BOOKED", first.status());

        assertThrows(BookingConflictException.class, () -> appointmentService.createAppointment(new CreateAppointmentRequest(
                branch.getId(),
                "John Doe",
                "john@example.com",
                "+12025550124",
                startsAt
        )));
    }

    @Test
    void rejectSlotWithSecondsPrecisionToPreventBypass() {
        Branch branch = branchRepository.save(new Branch("Precision Branch", "456 Clock Ave", LocalTime.of(9, 0), LocalTime.of(17, 0), 30));
        LocalDateTime invalidStart = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(30).withNano(0);

        assertThrows(IllegalArgumentException.class, () -> appointmentService.createAppointment(new CreateAppointmentRequest(
                branch.getId(),
                "Precision User",
                "precision@example.com",
                "+12025550999",
                invalidStart
        )));
    }
}
