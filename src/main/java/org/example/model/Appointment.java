package org.example.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(nullable = false)
    private LocalDateTime startsAt;

    @Column(nullable = false)
    private LocalDateTime endsAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private String confirmationMessage;

    @Column(nullable = false)
    private LocalDateTime confirmationSentAt;

    protected Appointment() {
    }

    public Appointment(Branch branch, Customer customer, LocalDateTime startsAt, LocalDateTime endsAt) {
        this.branch = branch;
        this.customer = customer;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
        this.status = AppointmentStatus.BOOKED;
        this.createdAt = LocalDateTime.now();
        this.confirmationMessage = "";
        this.confirmationSentAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Branch getBranch() {
        return branch;
    }

    public Customer getCustomer() {
        return customer;
    }

    public LocalDateTime getStartsAt() {
        return startsAt;
    }

    public LocalDateTime getEndsAt() {
        return endsAt;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getConfirmationMessage() {
        return confirmationMessage;
    }

    public LocalDateTime getConfirmationSentAt() {
        return confirmationSentAt;
    }

    public void cancel() {
        this.status = AppointmentStatus.CANCELLED;
    }

    public void setConfirmation(String confirmationMessage, LocalDateTime confirmationSentAt) {
        this.confirmationMessage = confirmationMessage;
        this.confirmationSentAt = confirmationSentAt;
    }
}

