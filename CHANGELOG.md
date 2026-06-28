# Changelog

All notable changes to this project are documented here. The format is based
on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and this project
aims to follow [Semantic Versioning](https://semver.org/).

## [Unreleased]

### Added
* Bean validation constraints on the Income, Expense, Goal, and User models
* `CurrentUserService` to resolve the authenticated user's id in one place
* `GeminiRoadmapService` with configurable model, endpoint, and HTTP timeouts
* Spring Boot Actuator health endpoint (`/actuator/health`) for deploy checks
* GitHub Actions CI and CodeQL security scanning workflows
* Dependabot configuration for Maven and GitHub Actions updates
* Unit and web-layer tests across the service and controller layers
* Project documentation: README, CONTRIBUTING, SECURITY policy, and issue / PR templates

### Changed
* Extracted the Gemini API call out of the controller into a dedicated service
* Default error responses no longer expose messages or stack traces
* Logging defaults lowered to `INFO`

### Fixed
* Ownership checks on goal view / add-savings and expense deletion (IDOR)
* Null-safe parsing of Gemini responses and of a goal's deadline
* Removed hardcoded database credentials in favor of environment variables

### Security
* Scrubbed a leaked database password from the source tree
* Added a private vulnerability reporting policy
