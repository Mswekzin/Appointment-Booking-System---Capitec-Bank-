package org.example.controller;

import jakarta.validation.Valid;
import org.example.dto.AppointmentResponse;
import org.example.dto.CreateAppointmentRequest;
import org.example.service.AppointmentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping("/api/appointments")
    @ResponseStatus(HttpStatus.CREATED)
    public AppointmentResponse createAppointment(@Valid @RequestBody CreateAppointmentRequest request) {
        return appointmentService.createAppointment(request);
    }

    @GetMapping("/api/appointments/{appointmentId}")
    public AppointmentResponse getAppointment(@PathVariable Long appointmentId) {
        return appointmentService.getAppointment(appointmentId);
    }

    @PatchMapping("/api/appointments/{appointmentId}/cancel")
    public AppointmentResponse cancelAppointment(@PathVariable Long appointmentId) {
        return appointmentService.cancelAppointment(appointmentId);
    }

    @GetMapping("/api/customers/{customerId}/appointments")
    public List<AppointmentResponse> getCustomerAppointments(@PathVariable Long customerId) {
        return appointmentService.getAppointmentsByCustomer(customerId);
    }
}

