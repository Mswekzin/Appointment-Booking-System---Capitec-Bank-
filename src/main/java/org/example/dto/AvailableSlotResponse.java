package org.example.dto;

import java.time.LocalDateTime;

public record AvailableSlotResponse(LocalDateTime startsAt, LocalDateTime endsAt) {
}

