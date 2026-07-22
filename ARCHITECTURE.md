# Architecture Notes

## Overview
This project is a modular monolith built with Spring Boot. It delivers both REST backend APIs and a professional multi-page frontend in one deployable unit.

Flow:
- Browser UI (Thymeleaf + vanilla JS) calls REST endpoints
- Controllers validate request shape and delegate to services
- Services enforce booking rules and orchestrate workflows
- Repositories persist entities through Spring Data JPA
- H2 (local) or PostgreSQL (Docker profile) stores data

## Frontend
- **Sticky header** with tab navigation (Book / My Bookings)
- **3-step booking wizard**: Branch & Date → Contact Details → Review & Confirm
- **Slot chip grid**: clickable time-slot buttons replacing plain dropdowns
- **Branch info strip**: address and hours shown dynamically on selection
- **Booking summary**: human-readable review before submitting
- **Confirmation card**: green success panel showing booking details and simulated confirmation message
- **My Bookings tab**: look up all appointments by email, cancel active bookings
- **Toast notifications**: non-blocking feedback for errors and success
- **Loading spinner**: visual feedback during API calls
- **Client-side validation**: field-level error messages before submission
- **Fully responsive**: works on mobile via CSS Grid breakpoints

## Components
- `org.example.controller`
  - `PageController`: serves the booking page (`/`)
  - `BranchController`: branch listing and slot discovery APIs
  - `AppointmentController`: create/read/cancel appointment APIs + by-email lookup
- `org.example.service`
  - `AppointmentService`: core booking logic (hours, slot alignment, conflict checks)
  - `ConfirmationService`: simulated confirmation dispatch (logged and persisted)
- `org.example.repository`
  - `BranchRepository`, `CustomerRepository`, `AppointmentRepository`
- `org.example.model`
  - `Branch`, `Customer`, `Appointment`, `AppointmentStatus`
- `org.example.exception`
  - Centralized API error handling via `GlobalExceptionHandler`

## Data Model
- `branches` — `id`, `name` (unique), `address`, `open_time`, `close_time`, `slot_minutes`
- `customers` — `id`, `full_name`, `email`, `phone`
- `appointments` — `id`, `branch_id`, `customer_id`, `starts_at`, `ends_at`, `status`, `created_at`, `confirmation_message`, `confirmation_sent_at`

## Booking Rules
- Appointment must be in the future
- Appointment time must be within branch working hours
- Appointment time must align with the branch slot interval
- One active (`BOOKED`) appointment per branch + slot (conflict check)
- Cancellation sets status to `CANCELLED`; history is preserved

## API Contract
| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/branches` | List all branches |
| `GET` | `/api/branches/{id}/slots?date=YYYY-MM-DD` | Available slots for date |
| `POST` | `/api/appointments` | Create appointment |
| `GET` | `/api/appointments/{id}` | Get appointment by ID |
| `PATCH` | `/api/appointments/{id}/cancel` | Cancel appointment |
| `GET` | `/api/customers/{id}/appointments` | Appointments by customer ID |
| `GET` | `/api/customers/by-email?email=...` | Appointments by customer email |
| `GET` | `/actuator/health` | Health check (DB + disk) |

## Non-Functional Decisions
- `open-in-view=false` prevents lazy-loading leaks into the view layer
- DTOs isolate API responses from JPA entities
- Test profile uses isolated in-memory H2 (`create-drop`) — no shared state between test runs
- Multi-stage Dockerfile keeps runtime image small (JRE only)
- Spring Boot Actuator exposes `/actuator/health` and `/actuator/info` for readiness checks

## Deployment Profiles
| Profile | Database | When to use |
|---------|----------|-------------|
| default | H2 file-based | Local development |
| test | H2 in-memory | Automated tests |
| docker | PostgreSQL | Docker Compose / production |

## Tradeoffs and Recommended Next Steps
- **Database**: Replace H2 with PostgreSQL + Flyway migrations for schema versioning
- **Notifications**: Replace in-process confirmation with async queue (RabbitMQ / Kafka)
- **Security**: Add Spring Security with JWT or session-based auth for admin cancel/list endpoints
- **Observability**: Add Micrometer metrics, structured logging (JSON), and distributed tracing
- **Testing**: Add Testcontainers for real PostgreSQL integration tests
