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
- H2 (local default profile)
- PostgreSQL (Docker profile)
- Maven Wrapper
- Docker + Docker Compose

## API Overview
- `GET /api/branches` - list branches
- `GET /api/branches/{branchId}/slots?date=YYYY-MM-DD` - available slots
- `POST /api/appointments` - create appointment
- `GET /api/appointments/{appointmentId}` - appointment details
- `PATCH /api/appointments/{appointmentId}/cancel` - cancel appointment
- `GET /api/customers/{customerId}/appointments` - customer appointment history

## Quick Test (Recommended First)
```powershell
.\mvnw.cmd test
```

## Run Locally (H2)
```powershell
.\mvnw.cmd spring-boot:run
```

Application URLs:
- UI: `http://localhost:8080/`
- H2 console: `http://localhost:8080/h2-console`

## Run with Docker Compose (App + PostgreSQL)
```powershell
docker compose up --build
```

Then open:
- UI: `http://localhost:8080/`

Stop services:
```powershell
docker compose down
```

Stop + remove DB volume:
```powershell
docker compose down -v
```

## Run with Docker (App only)
This mode uses the default app profile (H2) and does not start PostgreSQL.

```powershell
docker build -t appointment-booking-system .
docker run --rm -p 8080:8080 appointment-booking-system
```

## IntelliJ Red Errors Fix
If you still see red imports (Spring/JUnit), it is usually IDE indexing/dependency sync.

1. In Maven tool window, click **Reload All Maven Projects**.
2. Ensure SDK is Java 21 in Project Structure.
3. Run this once in terminal to force dependency resolution:
```powershell
.\mvnw.cmd -U clean test
```
4. If still red: `File -> Invalidate Caches -> Invalidate and Restart`.

## Documentation
- Design and decisions: `ARCHITECTURE.md`
- Version history: `CHANGELOG.md`

## Publish to GitHub
Repository is already initialized locally on branch `main`.

```powershell
Set-Location "C:\Users\QXZ5GIQ\IdeaProjects\AppointmentBookingSystem"
git remote add origin https://github.com/<your-username>/<your-repo>.git
git push -u origin main
```

If remote already exists:
```powershell
git remote set-url origin https://github.com/<your-username>/<your-repo>.git
git push -u origin main
```
