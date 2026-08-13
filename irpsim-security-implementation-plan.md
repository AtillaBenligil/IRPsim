# IRPsim Security Implementation Plan (LDAP + RBAC)

This plan is designed for the IRPsim backend repository that handles HTTP authentication, sessions, scenarios/jobs APIs, and standing data APIs.

## Goal

Implement:
- LDAP authentication (login, logout, password change)
- Role and group based access for scenarios and simulation jobs
- Read/write access control for standing data

## Prerequisites

- Backend repository cloned locally
- Java/Gradle build passes (`./gradlew verifyAll` or backend equivalent)
- Local LDAP runtime available (for example via Docker Compose)

## Sprint 1: LDAP Authentication

### Technical tasks

- Add LDAP connection and bind/authentication service.
- Add endpoints for login, logout, and password change.
- Add session handling/token lifecycle integration.
- Add backend configuration for LDAP host, port, bind DN, and base DN.
- Add a local startup profile that includes LDAP server startup in build automation.

### Acceptance criteria

- Valid LDAP credentials can log in.
- Logout invalidates session/token.
- Password change updates LDAP account and re-authentication works.
- Automated tests cover success and failure paths.

## Sprint 2: RBAC for Scenarios and Simulation Jobs

### Technical tasks

- Add domain model for users, groups, and memberships if not already present.
- Add authorization model for scenario and job visibility/editability.
- Add CRUD operations for groups and membership changes.
- Enforce access checks in service layer and query filtering.

### Acceptance criteria

- Users only see scenarios/jobs they are permitted to read.
- Unauthorized updates/deletes are rejected.
- Group operations are auditable and tested.

## Sprint 3: Standing Data Access Control

### Technical tasks

- Define ACL model with subject (user/group), resource, and action (read/write).
- Replace legacy responsible-person mechanism with ACL checks.
- Implement migration script from legacy ownership to ACL entries.
- Add API/UI contract updates for assigning and modifying ACLs.

### Acceptance criteria

- Read and write permissions enforced for standing data.
- Legacy records are migrated without data loss.
- Existing workflows continue where equivalent permissions exist.

## Testing Strategy

- Unit tests for auth and authorization decisions.
- Integration tests with local LDAP.
- Regression tests for existing scenario/job/data flows.
- Negative tests for unauthorized access.

## Architecture Artifacts for Reports

- Component diagram: auth provider, auth service, authorization service, data services.
- Sequence diagrams:
  - Login/logout/password change
  - Scenario/job access decision
  - Standing data read/write decision
- Alternative discussion examples:
  - LDAP direct bind vs sync-to-local-user cache
  - Pure RBAC vs RBAC + per-resource ACL

## Evidence to Capture Each Sprint

- Build/test command outputs.
- CI status and workflow run links.
- Requirement-to-test mapping table.
- Summary of impediments and mitigation.

## Immediate Execution Checklist (Backend Repo)

- [ ] Identify backend module handling auth/session.
- [ ] Add LDAP integration dependencies.
- [ ] Add auth endpoints and tests.
- [ ] Add group and permission model extensions.
- [ ] Add scenario/job/standing-data authorization checks.
- [ ] Add migration and regression tests.
