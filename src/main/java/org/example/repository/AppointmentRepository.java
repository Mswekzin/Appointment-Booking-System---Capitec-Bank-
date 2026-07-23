package org.example.repository;

import org.example.model.Appointment;
import org.example.model.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    boolean existsByBranchIdAndStatusAndStartsAtLessThanAndEndsAtGreaterThan(Long branchId,
                                                                              AppointmentStatus status,
                                                                              LocalDateTime proposedEnd,
                                                                              LocalDateTime proposedStart);

    List<Appointment> findByCustomerIdOrderByStartsAtAsc(Long customerId);

    List<Appointment> findByBranchIdAndStartsAtBetweenAndStatus(Long branchId,
                                                                LocalDateTime startsAt,
                                                                LocalDateTime endsAt,
                                                                AppointmentStatus status);
}
