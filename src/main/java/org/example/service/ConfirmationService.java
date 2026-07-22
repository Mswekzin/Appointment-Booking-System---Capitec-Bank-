package org.example.service;

import org.example.model.Appointment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ConfirmationService {

    private static final Logger logger = LoggerFactory.getLogger(ConfirmationService.class);

    public String sendConfirmation(Appointment appointment) {
        String message = "Confirmation sent to " + appointment.getCustomer().getEmail()
                + " for branch " + appointment.getBranch().getName()
                + " at " + appointment.getStartsAt();

        appointment.setConfirmation(message, LocalDateTime.now());
        logger.info("SIMULATED_NOTIFICATION appointmentId={} message={}", appointment.getId(), message);
        return message;
    }
}

