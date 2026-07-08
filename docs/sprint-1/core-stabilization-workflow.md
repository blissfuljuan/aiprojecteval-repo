# Core Stabilization Workflow

## Purpose

Use this workflow before adding new feature modules or expanding scaffolded modules. It keeps the implemented core stable while the application is still a modular monolith.

## Steps

1. Verify the target module against `docs/AI_CONTEXT.md`.
2. Prefer service-to-service communication across module boundaries.
3. Do not import repositories from another feature module.
4. Add or update architecture tests when a module is promoted from scaffold to implemented core.
5. Add focused unit tests for changed service behavior.
6. Run the backend test suite before moving to a new module.

## Stabilization Applied

- Project access now matches list behavior: students are scoped to owned projects, while staff can read projects surfaced by the all-projects view.
- Project mutation is explicit: students may modify owned projects, admins and instructors may modify any project, and evaluators/advisers are read-only.
- Document audit fields now resolve email-based authenticated principals to the current user ID.
- Project proposal dependencies on course class, identity, and project repositories were moved behind service boundaries.
- AI proposal evaluation now uses `ProjectProposalService` instead of directly importing the proposal repository.
- Architecture scaffold coverage now includes `courseclass` and `projectproposal`.

## Verification

- Backend command: `mvn test`
- Result: `BUILD SUCCESS`, 138 tests run, 0 failures, 0 errors, 0 skipped.

## Environment Note

The repository Maven wrapper failed in this shell before Maven started. Verification used the cached Maven 3.9.14 executable directly with `JAVA_HOME` set to `C:\Users\eric\.jdks\corretto-21.0.11`.
