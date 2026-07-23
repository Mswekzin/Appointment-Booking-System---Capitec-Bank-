package org.example.service;

import org.example.model.Appointment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ConfirmationService {

    private static final Logger logger = LoggerFactory.getLogger(ConfirmationService.class);

    private final JavaMailSender mailSender;
    private final String mailFrom;
    private final boolean realEmailEnabled;

    public ConfirmationService(ObjectProvider<JavaMailSender> mailSenderProvider,
                               @Value("${app.notifications.mail.from:no-reply@appointease.local}") String mailFrom,
                               @Value("${app.notifications.mail.enabled:false}") boolean realEmailEnabled) {
        this.mailSender = mailSenderProvider.getIfAvailable();
        this.mailFrom = mailFrom;
        this.realEmailEnabled = realEmailEnabled;
    }

    public String sendConfirmation(Appointment appointment) {
        String message = "Confirmation sent to " + appointment.getCustomer().getEmail()
                + " for branch " + appointment.getBranch().getName()
                + " at " + appointment.getStartsAt();

        if (realEmailEnabled && mailSender != null) {
            try {
                SimpleMailMessage mail = new SimpleMailMessage();
                mail.setFrom(mailFrom);
                mail.setTo(appointment.getCustomer().getEmail());
                mail.setSubject("Appointment Confirmation #" + appointment.getId());
                mail.setText(buildEmailBody(appointment));
                mailSender.send(mail);
                logger.info("EMAIL_SENT appointmentId={} to={}", appointment.getId(), appointment.getCustomer().getEmail());
            } catch (Exception ex) {
                logger.error("EMAIL_SEND_FAILED appointmentId={} error={}", appointment.getId(), ex.getMessage());
                message = "Email send failed. Simulated confirmation generated for " + appointment.getCustomer().getEmail();
            }
        } else {
            if (realEmailEnabled) {
                logger.warn("EMAIL_NOT_CONFIGURED appointmentId={} reason=mail sender bean unavailable", appointment.getId());
            }
            logger.info("SIMULATED_NOTIFICATION appointmentId={} message={}", appointment.getId(), message);
        }

        appointment.setConfirmation(message, LocalDateTime.now());
        return message;
    }

    private String buildEmailBody(Appointment appointment) {
        return "Hello " + appointment.getCustomer().getFullName() + ",\n\n"
                + "Your appointment has been confirmed.\n"
                + "Booking ID: " + appointment.getId() + "\n"
                + "Branch: " + appointment.getBranch().getName() + "\n"
                + "Address: " + appointment.getBranch().getAddress() + "\n"
                + "Date/Time: " + appointment.getStartsAt() + "\n\n"
                + "Thank you.";
    }
}
