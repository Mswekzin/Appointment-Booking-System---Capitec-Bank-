# Changelog

## [1.0.0] - 2026-07-22

### Added
- Spring Boot 3.3 full-stack appointment booking implementation
- Domain model for branches, customers, and appointments
- REST API for listing branches, viewing slots, booking, fetching, and cancelling appointments
- Simulated confirmation workflow with persisted confirmation metadata
- Validation and centralized API exception handling
- Seed data for demo branches
- Frontend booking page with dynamic slot loading and booking submission
- Integration tests for service and controller booking flows
- Dockerfile and `.dockerignore` for containerized build/run
- Maven Wrapper (`mvnw`, `mvnw.cmd`) for environments without global Maven
- Documentation set: `README.md`, `ARCHITECTURE.md`, `CHANGELOG.md`

### Verified
- `./mvnw.cmd -q test` passes locally
- `docker run --rm -v "${PWD}:/workspace" -w /workspace maven:3.9.9-eclipse-temurin-21 mvn -q test` passes

