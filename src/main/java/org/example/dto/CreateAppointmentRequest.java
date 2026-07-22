package org.example.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;

public record CreateAppointmentRequest(
        @NotNull Long branchId,
        @NotBlank String customerName,
        @NotBlank @Email String customerEmail,
        @NotBlank @Pattern(regexp = "^[0-9+()\\-\\s]{7,20}$") String customerPhone,
        @NotNull @Future LocalDateTime startsAt
) {
}

