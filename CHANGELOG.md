# Changelog

All notable changes to this project are documented here. The format is based
on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and this project
aims to follow [Semantic Versioning](https://semver.org/).

## [Unreleased]

### Added
* Bean validation constraints on the Income, Expense, Goal, and User models
* `CurrentUserService` to resolve the authenticated user (id or full user) in one place
* `GeminiRoadmapService` with configurable model, endpoint, and HTTP timeouts
* `FinancialCalculatorService` holding the savings-goal, compound-interest, and budget-ratio math
* Spring Boot Actuator health endpoint (`/actuator/health`) for deploy checks
* GitHub Actions CI and CodeQL security scanning workflows
* Dependabot configuration for Maven and GitHub Actions updates
* JaCoCo test coverage reporting
* Unit, web-layer, and data-layer (`@JdbcTest` against H2) tests covering the
  service, controller, and DAO/repository layers
* Project documentation: README, CONTRIBUTING, SECURITY policy, and issue / PR templates

### Changed
* Extracted the Gemini API call and the financial calculators out of their controllers into services
* Consolidated the duplicated current-user lookup across all controllers into `CurrentUserService`
* Default error responses no longer expose messages or stack traces
* Logging defaults lowered to `INFO`

### Fixed
* Ownership checks on goal view / add-savings, expense deletion, and job deletion (IDOR)
* Null-safe parsing of Gemini responses and of a goal's deadline
* Removed hardcoded database credentials in favor of environment variables

### Security
* Scrubbed a leaked database password from the source tree
* Added a private vulnerability reporting policy
