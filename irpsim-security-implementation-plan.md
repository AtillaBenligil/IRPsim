# IRPsim Backend Security Implementation Plan (Code-Mapped)

## Scope

Implement in this repository:
- LDAP authentication: login, logout, password change
- Role or group-based access for scenarios and simulation jobs
- Read or write access control for standing data
- Replacement of responsible-person-only ownership model in standing data

## Architecture Entry Points in Current Code

- HTTP startup and Jersey registration:
  - backend-server/src/main/java/de/unileipzig/irpsim/server/ServerStarter.java
- Scenario endpoints:
  - backend-server/src/main/java/de/unileipzig/irpsim/server/data/simulationparameters/ScenarioEndpoint.java
- Simulation job endpoints:
  - backend-server/src/main/java/de/unileipzig/irpsim/server/optimisation/endpoints/OptimisationJobEndpoint.java
  - backend-server/src/main/java/de/unileipzig/irpsim/server/optimisation/endpoints/OptimisationEndpoint.java
  - backend-server/src/main/java/de/unileipzig/irpsim/server/optimisation/endpoints/OptimisationStateEndpoint.java
- Standing data endpoints:
  - backend-server/src/main/java/de/unileipzig/irpsim/server/standingdata/endpoints/StammdatumEndpoint.java
  - backend-server/src/main/java/de/unileipzig/irpsim/server/standingdata/endpoints/StammdatumEntityEndpoint.java
  - backend-server/src/main/java/de/unileipzig/irpsim/server/standingdata/endpoints/DataEndpoint.java

## Data Model Entry Points

- Scenario metadata currently contains creator string but no ACL:
  - backend-core/src/main/java/de/unileipzig/irpsim/core/data/simulationparameters/OptimisationScenario.java
- Simulation job persistence currently has no owner or ACL fields:
  - backend-core/src/main/java/de/unileipzig/irpsim/core/simulation/data/persistence/OptimisationJobPersistent.java
- Standing data currently uses responsible person fields:
  - backend-core/src/main/java/de/unileipzig/irpsim/core/standingdata/data/Stammdatum.java

## Sprint 1: LDAP Authentication

### Implementation tasks

- Add auth package under backend-server for identity and session handling.
- Add LDAP configuration model using environment variables:
  - IRPSIM_LDAP_URL
  - IRPSIM_LDAP_BASE_DN
  - IRPSIM_LDAP_BIND_DN
  - IRPSIM_LDAP_BIND_PASSWORD
- Introduce endpoints:
  - POST /auth/login
  - POST /auth/logout
  - POST /auth/change-password
- Add a Jersey request filter that resolves authenticated principal from session or token.
- Register auth filter and endpoints in server startup pipeline.

### Tests

- Unit tests for LDAP bind and password change handling.
- Endpoint tests for login success and failure, logout invalidation, change-password success and failure.

## Sprint 2: RBAC for Scenarios and Simulation Jobs

### Implementation tasks

- Add domain entities in backend-core for users, groups, and memberships.
- Add permission mapping entities for scenario and job resources.
- Add authorization service in backend-server and enforce checks in:
  - ScenarioEndpoint
  - OptimisationEndpoint
  - OptimisationJobEndpoint
  - OptimisationStateEndpoint
- Add group management endpoints for create group, add member, remove member.

### Tests

- Endpoint tests validating per-user visibility filtering.
- Negative tests for unauthorized read, update, delete operations.

## Sprint 3: Standing Data Access Control

### Implementation tasks

- Add ACL mapping for standing data resources with subject and permission mode.
- Extend Stammdatum access queries to include ACL constraints.
- Replace responsible-person-only logic with ACL checks while retaining compatibility fields.
- Add migration that maps existing responsible persons to initial ACL entries.

### Tests

- Regression tests for existing standing data operations under authorized identities.
- Negative tests for unauthorized read and write actions.
- Migration tests validating that legacy data remains accessible with mapped permissions.

## Suggested Initial Package Layout

- backend-server/src/main/java/de/unileipzig/irpsim/server/security/auth
- backend-server/src/main/java/de/unileipzig/irpsim/server/security/filter
- backend-server/src/main/java/de/unileipzig/irpsim/server/security/service
- backend-core/src/main/java/de/unileipzig/irpsim/core/security/entity
- backend-core/src/main/java/de/unileipzig/irpsim/core/security/persistence

## Done Definition per Sprint

- Feature code merged and build passes.
- Automated tests added and passing.
- API behavior documented with request and response examples.
- Reflective report section updated with requirements, architecture, testing, impediments.
