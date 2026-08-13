# Resit Technical Progress Log

## 2026-08-04 Baseline Build and Test

### Objective
Establish a reproducible baseline build and test state before implementing new security features.

### Completed
- Verified repository build status with Java build integration.
- Executed project build: success.
- Executed Java test suite: success.
- Added GitHub Actions pipeline for continuous verification on push and pull request.

### CI Workflow Added
- File: `.github/workflows/ci.yml`
- Java versions: 17 and 21
- Pipeline steps:
  - clean build
  - internal tests
  - toy model tests
  - uber JAR build

### Why This Matters
- Creates objective evidence of engineering progress.
- Reduces integration risk by validating all commits automatically.
- Provides baseline confidence before introducing authentication and authorization changes in IRPsim platform repositories.

### Next Technical Steps
- Identify and clone the IRPsim backend repository responsible for user authentication/session handling.
- Implement LDAP login/logout/password change in backend service.
- Add role and group-based authorization checks for scenario and simulation job visibility.
- Add read/write access control model for standing data.

## 2026-08-04 CI and Verification Hardening

### Objective
Improve reproducibility and ensure one-command verification for local development and CI.

### Completed
- Added a unified Gradle task: `verifyAll`.
- Updated GitHub Actions to execute `verifyAll` on Java 17 and 21.
- Updated README with a local verification workflow and Java environment troubleshooting.

### Artifacts Updated
- `build.gradle`
- `.github/workflows/ci.yml`
- `readme.md`

### Why This Matters
- Eliminates command drift between local checks and CI.
- Makes verification repeatable for each sprint release.
- Produces clearer evidence for engineering process quality in assessment.

## 2026-08-04 Submission and Security Planning Artifacts

### Objective
Strengthen delivery readiness and prepare immediate execution documents for the LDAP/RBAC implementation phase.

### Completed
- Added CI badge visibility in `readme.md`.
- Added release readiness checklist: `resit-release-checklist.md`.
- Added backend-focused LDAP/RBAC implementation plan template: `irpsim-security-implementation-plan.md`.

### Why This Matters
- Makes project health visible from the repository front page.
- Reduces sprint submission mistakes via a concrete pre-release checklist.
- Provides a ready-to-execute security implementation plan once the backend repository is active.

## 2026-08-04 Backend Repository Activation

### Objective
Start security implementation preparation in the actual IRPsim backend repository where HTTP endpoints and persistence are implemented.

### Completed
- Cloned the IRPsim backend repository locally.
- Added backend-local resit artifacts (progress log, release checklist, and code-mapped LDAP/RBAC implementation plan).
- Updated backend README with CI badge and security sprint support pointers.

### Why This Matters
- Moves work from generic planning to repository-specific execution.
- Establishes concrete code targets for authentication and authorization implementation.

## 2026-08-04 Backend Sprint 1 Scaffold

### Objective
Implement first executable authentication layer scaffolding in the backend repository.

### Completed
- Added auth endpoints for login, logout, and password change.
- Added LDAP configuration and JNDI LDAP service abstraction.
- Added in-memory session token service.
- Added authentication request filter registration in backend server startup.
- Added initial unit tests for authentication endpoint and session service.

### Why This Matters
- Converts planning artifacts into real source code for Sprint 1.
- Establishes the integration seam to add authorization checks in Sprint 2 and Sprint 3.
