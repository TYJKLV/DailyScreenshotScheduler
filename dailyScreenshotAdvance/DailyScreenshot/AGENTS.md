# Repository Guidelines

## Project Structure & Module Organization
- Root contains two Java projects: `daily-screenshot` (plain IntelliJ project) and `screenshot-maven` (Maven project).
- Main source files live under `*/src/main/java/com/yyk`, including `DailyScreenshotScheduler`, `ScreenShot`, `SaveScreenshot`, and related helpers.
- Build outputs and generated artifacts are placed in `daily-screenshot/out` and `screenshot-maven/target`. Do not edit these directories by hand.

## Build, Test, and Development Commands
- Maven build: `cd screenshot-maven && mvn clean package` to produce a runnable JAR in `target`.
- Maven tests: `mvn test` to run all unit tests (when present).
- Non-Maven project: prefer running `com.yyk.Main` from your IDE; if needed, compile and run with `javac` and `java` using the `com.yyk` package structure.

## Coding Style & Naming Conventions
- Use standard Java style: 4-space indentation, UTF-8 encoding, and lines ideally under 120 characters.
- Class names use PascalCase (for example `RandomTime`), methods and variables use camelCase (for example `saveScreenshot`), and constants use UPPER_SNAKE_CASE.
- Keep packages lowercase under `com.yyk`, avoid non-ASCII characters in code identifiers and filenames.

## Testing Guidelines
- Place unit tests for the Maven project under `src/test/java/com/yyk` with class names ending in `Test`, for example `RandomTimeTest`.
- Use JUnit 5 where possible, with test method names describing behavior, such as `shouldGenerateRandomTimeWithinRange`.
- New features should include corresponding tests, and `mvn test` should pass before opening a pull request.

## Commit & Pull Request Guidelines
- Use clear, short commit messages following `type: summary`, for example `feat: add daily screenshot scheduler` or `fix: handle high-dpi screens`.
- Each pull request should include: a summary of changes, motivation or linked issue, how it was tested (including commands), and screenshots or logs when UI or behavior changes.
- Keep pull requests focused on a single logical change; avoid mixing large refactors with feature work.

## Agent-Specific Instructions
- Automated tools and AI agents should modify only source directories and configuration files, never `out` or `target` directly.
- When assisting contributors, default to concise explanations and respect the structure and conventions described in this document.

