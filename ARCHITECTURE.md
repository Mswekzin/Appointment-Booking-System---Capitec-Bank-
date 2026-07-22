# Architecture Notes

## Overview
This project is a modular monolith built with Spring Boot. It delivers both backend APIs and a lightweight frontend in one deployable unit.

Flow:
- Browser UI (`Thymeleaf` + vanilla JS) calls REST endpoints
- Controllers validate request shape and delegate business logic
- Services enforce booking rules and orchestrate workflows
- Repositories persist entities through Spring Data JPA
- H2 stores data for local/demo use

## Components
- `org.example.controller`
  - `PageController`: serves the booking page (`/`)
  - `BranchController`: branch listing and slot discovery APIs
  - `AppointmentController`: create/read/cancel appointment APIs
- `org.example.service`
  - `AppointmentService`: core booking logic (hours, slot alignment, conflict checks)
  - `ConfirmationService`: simulated confirmation dispatch (logged and persisted)
- `org.example.repository`
  - `BranchRepository`, `CustomerRepository`, `AppointmentRepository`
- `org.example.model`
  - `Branch`, `Customer`, `Appointment`, `AppointmentStatus`
- `org.example.exception`
  - centralized API errors with `GlobalExceptionHandler`

## Data Model
- `branches`
  - `id`, `name` (unique), `address`, `open_time`, `close_time`, `slot_minutes`
- `customers`
  - `id`, `full_name`, `email`, `phone`
- `appointments`
  - `id`, `branch_id`, `customer_id`, `starts_at`, `ends_at`, `status`,
    `created_at`, `confirmation_message`, `confirmation_sent_at`

## Booking Rules
- Appointment must be in the future
- Appointment time must be within branch hours
- Appointment time must align with branch slot interval
- One active (`BOOKED`) appointment per branch+slot
- Cancel changes status to `CANCELLED` to keep booking history

## API Contract
- `GET /api/branches`
- `GET /api/branches/{branchId}/slots?date=YYYY-MM-DD`
- `POST /api/appointments`
- `GET /api/appointments/{appointmentId}`
- `PATCH /api/appointments/{appointmentId}/cancel`
- `GET /api/customers/{customerId}/appointments`

## Non-Functional Decisions
- `open-in-view=false` to avoid accidental lazy-loading during view rendering
- DTOs isolate API responses from JPA entities
- Test profile uses isolated in-memory H2 schema (`create-drop`)
- Dockerfile uses multi-stage image for smaller runtime footprint

## Tradeoffs and Next Steps
- H2 is used for speed and portability; production should use PostgreSQL
- Confirmation is simulated in-process; production should use async messaging
- Authentication is omitted for brief scope; production should secure endpoints

