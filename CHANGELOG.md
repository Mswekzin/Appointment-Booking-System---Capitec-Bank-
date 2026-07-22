# Changelog

All notable changes to this project are documented in this file.

## [1.2.0] - 2026-07-22

### Added
- Professional UI redesign with tab navigation and 3-step booking wizard
- Slot-chip selection UX and structured booking review screen
- "My Bookings" email lookup with cancellation action
- Spring Boot Actuator health endpoint exposure (`/actuator/health`)

### Changed
- Improved frontend validation and toast-based user feedback
- Updated architecture documentation with deployment profiles and endpoint table

### Fixed
- Ignored local H2 `data/` folder to avoid committing DB files

## [1.1.0] - 2026-07-22

### Added
- Docker Compose setup for app + PostgreSQL
- Docker profile configuration (`application-docker.yml`)
- PostgreSQL runtime dependency and updated run documentation

## [1.0.0] - 2026-07-22

### Added
- Initial full-stack Spring Boot appointment booking system
- Branch/slot discovery APIs and appointment create/read/cancel APIs
- Seed branch data, centralized exception handling, and test profile
- Dockerfile, README, and architecture notes

