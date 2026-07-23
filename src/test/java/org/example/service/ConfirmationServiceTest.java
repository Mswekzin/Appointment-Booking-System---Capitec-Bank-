package org.example.service;

import org.example.model.Appointment;
import org.example.model.Branch;
import org.example.model.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ConfirmationServiceTest {

    @Test
    void sendsRealEmailWhenEnabledAndSenderAvailable() {
        JavaMailSender mailSender = mock(JavaMailSender.class);
        @SuppressWarnings("unchecked")
        ObjectProvider<JavaMailSender> provider = mock(ObjectProvider.class);
        when(provider.getIfAvailable()).thenReturn(mailSender);

        ConfirmationService service = new ConfirmationService(provider, "no-reply@example.com", true);
        Appointment appointment = sampleAppointment();

        String message = service.sendConfirmation(appointment);

        verify(mailSender).send(any(SimpleMailMessage.class));
        assertTrue(message.contains("Confirmation sent to"));
    }

    @Test
    void fallsBackToSimulationWhenSenderMissing() {
        @SuppressWarnings("unchecked")
        ObjectProvider<JavaMailSender> provider = mock(ObjectProvider.class);
        when(provider.getIfAvailable()).thenReturn(null);

        ConfirmationService service = new ConfirmationService(provider, "no-reply@example.com", true);
        Appointment appointment = sampleAppointment();

        String message = service.sendConfirmation(appointment);

        assertTrue(message.contains("Confirmation sent to"));
    }

    @Test
    void doesNotSendEmailWhenFeatureDisabled() {
        JavaMailSender mailSender = mock(JavaMailSender.class);
        @SuppressWarnings("unchecked")
        ObjectProvider<JavaMailSender> provider = mock(ObjectProvider.class);
        when(provider.getIfAvailable()).thenReturn(mailSender);

        ConfirmationService service = new ConfirmationService(provider, "no-reply@example.com", false);
        Appointment appointment = sampleAppointment();

        service.sendConfirmation(appointment);

        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    private Appointment sampleAppointment() {
        Branch branch = new Branch("Downtown", "100 Main St", LocalTime.of(9, 0), LocalTime.of(17, 0), 30);
        Customer customer = new Customer("Test User", "test@example.com", "+12025550123");
        LocalDateTime startsAt = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
        return new Appointment(branch, customer, startsAt, startsAt.plusMinutes(30));
    }
}

