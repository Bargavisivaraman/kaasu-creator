# Kaasu Creator

[![CI](https://github.com/Bargavisivaraman/kaasu-creator/actions/workflows/ci.yml/badge.svg)](https://github.com/Bargavisivaraman/kaasu-creator/actions/workflows/ci.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Java 21](https://img.shields.io/badge/Java-21-orange.svg)](https://adoptium.net/)
[![Spring Boot 3.5](https://img.shields.io/badge/Spring%20Boot-3.5-brightgreen.svg)](https://spring.io/projects/spring-boot)

An anime-styled personal finance web app featuring the Kaasu-chan mascot. Track income, plan budgets, log work hours, set savings goals, and explore financial calculators, all behind a secure login.

Built with Spring Boot 3.5 and Java 21, backed by Supabase PostgreSQL.

## Features

* **Income tracking** with precise `BigDecimal` money math (regular and extra income)
* **Budget planner** with category breakdown and a budget ratio calculator
* **Timesheet** for logging jobs and work hours
* **Savings goals** with progress and add-savings actions
* **Financial calculators** for compound interest and savings goal projections
* **AI roadmap** generator powered by Gemini
* **Secure auth** using Spring Security with BCrypt password hashing

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 21 |
| Framework | Spring Boot 3.5 (Web MVC, Security, Validation) |
| Templates | Thymeleaf |
| Data access | Spring JDBC (`JdbcTemplate`) |
| Database | Supabase PostgreSQL |
| Build | Maven (wrapper included) |
| Deploy | Render (Docker and native Java runtime) |

## Getting Started

### Prerequisites

* Java 21
* A Supabase PostgreSQL database (free tier works)

### Configuration

The app reads database credentials from environment variables. Copy the example file and fill in your own values:

```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

Then set the following variables (or export them in your shell):

| Variable | Description |
|----------|-------------|
| `DATABASE_URL` | JDBC URL for your Supabase Postgres instance |
| `DATABASE_USERNAME` | Database username |
| `DATABASE_PASSWORD` | Database password |
| `GEMINI_API_KEY` | Optional, for the AI roadmap feature |

### Run locally

```bash
./mvnw spring-boot:run
```

The app starts on `http://localhost:9093` by default. Override with `PORT`.

### Run the tests

```bash
./mvnw test
```

## Project Structure

```
src/main/java/kaasu_creator/
  controller/   Web request handlers (Thymeleaf views)
  service/      Business logic
  dao/          JdbcTemplate data access
  model/        Domain objects (Income, Expense, Goal, ...)
  config/       Security and database initialization
src/main/resources/
  templates/    Thymeleaf HTML views
  static/       CSS and JavaScript
  schema.sql    Database schema
```

## Deployment

The repository ships with a `Dockerfile` and a `render.yaml` for one-click deployment to [Render](https://render.com). Database credentials are injected as environment variables and never committed to source control.

## License

Released under the MIT License. See [LICENSE](LICENSE) for details.
