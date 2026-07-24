package org.example.service;

import org.example.dto.AppointmentResponse;
import org.example.dto.AvailableSlotResponse;
import org.example.dto.CreateAppointmentRequest;
import org.example.exception.BookingConflictException;
import org.example.exception.NotFoundException;
import org.example.model.Appointment;
import org.example.model.AppointmentStatus;
import org.example.model.Branch;
import org.example.model.Customer;
import org.example.repository.AppointmentRepository;
import org.example.repository.BranchRepository;
import org.example.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AppointmentService {

    private static final Logger logger = LoggerFactory.getLogger(AppointmentService.class);

    private final BranchRepository branchRepository;
    private final CustomerRepository customerRepository;
    private final AppointmentRepository appointmentRepository;
    private final ConfirmationService confirmationService;

    public AppointmentService(BranchRepository branchRepository,
                              CustomerRepository customerRepository,
                              AppointmentRepository appointmentRepository,
                              ConfirmationService confirmationService) {
        this.branchRepository = branchRepository;
        this.customerRepository = customerRepository;
        this.appointmentRepository = appointmentRepository;
        this.confirmationService = confirmationService;
    }

    @Transactional(readOnly = true)
    public List<AvailableSlotResponse> getAvailableSlots(Long branchId, LocalDate date) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new NotFoundException("Branch not found"));

        LocalDateTime dayStart = date.atStartOfDay();
        LocalDateTime dayEnd   = date.atTime(LocalTime.MAX);

        List<Appointment> existing = appointmentRepository.findBookedForBranchOnDay(
                branchId, dayStart, dayEnd, AppointmentStatus.BOOKED);

        List<AvailableSlotResponse> slots = new ArrayList<>();
        LocalDateTime cursor  = date.atTime(branch.getOpenTime());
        LocalDateTime closeAt = date.atTime(branch.getCloseTime());

        while (!cursor.plusMinutes(branch.getSlotMinutes()).isAfter(closeAt)) {
            LocalDateTime slotEnd   = cursor.plusMinutes(branch.getSlotMinutes());
            LocalDateTime slotStart = cursor;
            boolean booked = existing.stream()
                    .anyMatch(a -> a.getStartsAt().isBefore(slotEnd) && a.getEndsAt().isAfter(slotStart));

            if (!booked && slotStart.isAfter(LocalDateTime.now())) {
                slots.add(new AvailableSlotResponse(slotStart, slotEnd));
            }
            cursor = slotEnd;
        }
        return slots;
    }

    /**
     * SERIALIZABLE isolation + PESSIMISTIC_WRITE lock inside hasOverlappingBooking
     * guarantees that two concurrent requests for the same slot cannot both succeed.
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public AppointmentResponse createAppointment(CreateAppointmentRequest request) {
        try {
            Branch branch = branchRepository.findById(request.branchId())
                    .orElseThrow(() -> new NotFoundException("Branch not found"));

            validateSlot(branch, request.startsAt());

            LocalDateTime appointmentStart = request.startsAt();
            LocalDateTime appointmentEnd   = appointmentStart.plusMinutes(branch.getSlotMinutes());

            boolean occupied = appointmentRepository.hasOverlappingBooking(
                    branch.getId(),
                    AppointmentStatus.BOOKED,
                    appointmentEnd,
                    appointmentStart
            );
            if (occupied) {
                throw new BookingConflictException("Selected slot is already booked. Please choose another time.");
            }

            Customer customer = customerRepository.findByEmailIgnoreCase(request.customerEmail())
                    .orElseGet(() -> customerRepository.save(new Customer(
                            request.customerName().trim(),
                            request.customerEmail().trim().toLowerCase(),
                            request.customerPhone().trim()
                    )));

            Appointment appointment = new Appointment(branch, customer, appointmentStart, appointmentEnd);
            Appointment saved = appointmentRepository.save(appointment);
            confirmationService.sendConfirmation(saved);

            logger.info("Appointment created id={} branch={} startsAt={} customer={}",
                    saved.getId(), branch.getName(), appointmentStart, customer.getEmail());

            return AppointmentResponse.from(saved);

        } catch (DataIntegrityViolationException ex) {
            // Last-resort safety net: DB-level constraint violation from concurrent insert
            logger.warn("Concurrent booking conflict caught at DB level: {}", ex.getMessage());
            throw new BookingConflictException("Selected slot was just taken. Please choose another time.");
        }
    }

    @Transactional(readOnly = true)
    public AppointmentResponse getAppointment(Long appointmentId) {
        return AppointmentResponse.from(appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new NotFoundException("Appointment not found")));
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> getAppointmentsByCustomer(Long customerId) {
        return appointmentRepository.findByCustomerIdOrderByStartsAtDesc(customerId)
                .stream()
                .map(AppointmentResponse::from)
                .toList();
    }

    @Transactional
    public AppointmentResponse cancelAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new NotFoundException("Appointment not found"));

        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            return AppointmentResponse.from(appointment);
        }

        appointment.cancel();
        logger.info("Appointment cancelled id={}", appointmentId);
        return AppointmentResponse.from(appointment);
    }

    private void validateSlot(Branch branch, LocalDateTime startsAt) {
        if (!startsAt.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Appointment must be in the future");
        }

        if (startsAt.getSecond() != 0 || startsAt.getNano() != 0) {
            throw new IllegalArgumentException("Selected slot must be on exact minute boundaries");
        }

        LocalTime localTime = startsAt.toLocalTime();
        if (localTime.isBefore(branch.getOpenTime()) || !localTime.isBefore(branch.getCloseTime())) {
            throw new IllegalArgumentException("Selected slot is outside branch working hours");
        }

        int minutesFromOpen = (localTime.getHour() - branch.getOpenTime().getHour()) * 60
                + localTime.getMinute() - branch.getOpenTime().getMinute();
        if (minutesFromOpen < 0 || minutesFromOpen % branch.getSlotMinutes() != 0) {
            throw new IllegalArgumentException("Selected slot does not align with branch slot interval");
        }
    }
}
