package org.example.repository;

import jakarta.persistence.LockModeType;
import org.example.model.Appointment;
import org.example.model.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    /**
     * Returns true if any BOOKED appointment for the given branch overlaps
     * the proposed [proposedStart, proposedEnd) interval.
     * Overlap condition: existing.startsAt < proposedEnd AND existing.endsAt > proposedStart
     * Uses a PESSIMISTIC WRITE lock so concurrent requests on the same slot are serialised at DB level.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT COUNT(a) > 0
            FROM Appointment a
            WHERE a.branch.id = :branchId
              AND a.status = :status
              AND a.startsAt < :proposedEnd
              AND a.endsAt   > :proposedStart
            """)
    boolean hasOverlappingBooking(@Param("branchId") Long branchId,
                                  @Param("status") AppointmentStatus status,
                                  @Param("proposedEnd") LocalDateTime proposedEnd,
                                  @Param("proposedStart") LocalDateTime proposedStart);

    List<Appointment> findByCustomerIdOrderByStartsAtDesc(Long customerId);

    @Query("""
            SELECT a FROM Appointment a
            WHERE a.branch.id = :branchId
              AND a.startsAt BETWEEN :dayStart AND :dayEnd
              AND a.status   = :status
            """)
    List<Appointment> findBookedForBranchOnDay(@Param("branchId") Long branchId,
                                               @Param("dayStart") LocalDateTime dayStart,
                                               @Param("dayEnd") LocalDateTime dayEnd,
                                               @Param("status") AppointmentStatus status);
}
