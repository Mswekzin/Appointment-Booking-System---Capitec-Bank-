# Appointment Booking System

Production-style full-stack Spring Boot application for scheduling branch appointments with simulated customer confirmations.

## Features
- Branch browsing and real-time slot lookup by date
- Appointment booking with validation and conflict detection
- Simulated confirmation message (logged and returned in response)
- Appointment lookup and cancellation endpoints
- Basic web UI (Thymeleaf + vanilla JavaScript)
- Automated tests for service and API flows

## Tech Stack
- Java 21
- Spring Boot 3.3
- Spring Web, Spring Data JPA, Validation, Thymeleaf
- H2 database
- Maven Wrapper
- Docker (multi-stage build)

## API Overview
- `GET /api/branches` - list branches
- `GET /api/branches/{branchId}/slots?date=YYYY-MM-DD` - available slots
- `POST /api/appointments` - create appointment
- `GET /api/appointments/{appointmentId}` - appointment details
- `PATCH /api/appointments/{appointmentId}/cancel` - cancel appointment
- `GET /api/customers/{customerId}/appointments` - customer appointment history

## Build and Run (Local)
```powershell
.\mvnw.cmd clean test
.\mvnw.cmd spring-boot:run
```

Application URLs:
- UI: `http://localhost:8080/`
- H2 console: `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:file:./data/appointments;MODE=PostgreSQL;DB_CLOSE_ON_EXIT=FALSE`
  - User: `sa`
  - Password: *(blank)*

## Test
```powershell
.\mvnw.cmd test
```

## Test Without Local Maven (Docker)
```powershell
docker run --rm -v "${PWD}:/workspace" -w /workspace maven:3.9.9-eclipse-temurin-21 mvn -q test
```

## Run with Docker
```powershell
docker build -t appointment-booking-system .
docker run --rm -p 8080:8080 appointment-booking-system
```

## Troubleshooting
- If IntelliJ shows unresolved Spring/JUnit imports, refresh Maven project after wrapper generation.
- If wrapper download fails once on Windows temp folder permissions, clear stale temp dirs and retry:
```powershell
Get-ChildItem "$env:TEMP" -Filter "tmp*.tmp.dir" -Directory | Remove-Item -Recurse -Force -ErrorAction SilentlyContinue
.\mvnw.cmd test
```

## Documentation
- Design and decisions: `ARCHITECTURE.md`
- Version history: `CHANGELOG.md`

## Design Notes
- Layered architecture: controller -> service -> repository
- Validation and centralized API error handling via `@RestControllerAdvice`
- Slot generation enforces branch hours and interval alignment
- Conflict checking prevents duplicate active bookings per branch and slot

## Suggested Production Hardening
- Replace H2 with PostgreSQL and Flyway migrations
- Add authentication/authorization for admin actions
- Emit confirmations to async queue (e.g., Kafka/RabbitMQ)
- Add observability (Micrometer + tracing + dashboards)

## Publish to GitHub
```powershell
git init
git add .
git commit -m "feat: initial production-grade appointment booking system"
git branch -M main
git remote add origin https://github.com/<your-username>/<your-repo>.git
git push -u origin main
```
