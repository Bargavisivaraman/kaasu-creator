# Contributing to Kaasu Creator

Thanks for your interest in improving Kaasu Creator. This guide explains how to set up the project, the conventions we follow, and how to submit changes.

## Getting started

1. Fork and clone the repository.
2. Copy `src/main/resources/application.properties.example` to `application.properties` and fill in your own Supabase credentials via environment variables.
3. Build and run the tests:

   ```bash
   ./mvnw test
   ```

4. Start the app locally:

   ```bash
   ./mvnw spring-boot:run
   ```

## Requirements

* Java 21
* Maven (the bundled wrapper `./mvnw` is fine)

## Branching and commits

* Create a feature branch off `main` (for example `feat/budget-export`).
* Follow the [Conventional Commits](https://www.conventionalcommits.org) style:
  * `feat:` a new feature
  * `fix:` a bug fix (`fix(security):` for security fixes)
  * `docs:` documentation only
  * `test:` adding or fixing tests
  * `refactor:` code change that neither fixes a bug nor adds a feature
  * `chore:` tooling, config, or housekeeping
  * `ci:` continuous integration changes
* Keep commits focused. One logical change per commit.

## Pull requests

1. Make sure `./mvnw test` passes.
2. Never commit secrets or credentials. Database passwords and API keys belong in environment variables.
3. Fill in the pull request template and link any related issues.
4. A maintainer will review your change and may request adjustments.

## Code style

* The project ships an `.editorconfig`. Most editors will pick up the indentation and whitespace rules automatically.
* Use `BigDecimal` for any money calculation. Never use `double` for currency.

## Reporting bugs and requesting features

Use the issue templates under **New issue**. Provide as much detail as you can so the problem can be reproduced.

Thank you for contributing.
