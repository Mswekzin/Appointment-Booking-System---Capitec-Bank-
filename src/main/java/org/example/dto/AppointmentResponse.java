package org.example.dto;

import org.example.model.Appointment;

import java.time.LocalDateTime;

public record AppointmentResponse(Long appointmentId,
                                  Long branchId,
                                  String branchName,
                                  Long customerId,
                                  String customerName,
                                  String customerEmail,
                                  String customerPhone,
                                  LocalDateTime startsAt,
                                  LocalDateTime endsAt,
                                  String status,
                                  String confirmationMessage,
                                  LocalDateTime confirmationSentAt) {

    public static AppointmentResponse from(Appointment appointment) {
        return new AppointmentResponse(
                appointment.getId(),
                appointment.getBranch().getId(),
                appointment.getBranch().getName(),
                appointment.getCustomer().getId(),
                appointment.getCustomer().getFullName(),
                appointment.getCustomer().getEmail(),
                appointment.getCustomer().getPhone(),
                appointment.getStartsAt(),
                appointment.getEndsAt(),
                appointment.getStatus().name(),
                appointment.getConfirmationMessage(),
                appointment.getConfirmationSentAt()
        );
    }
}

