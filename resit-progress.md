# Resit Technical Progress Log (Backend)

## 2026-08-04 Backend Discovery and Baseline Planning

### Objective
Start practical security implementation work in the actual IRPsim backend repository and map LDAP or RBAC changes to concrete code areas.

### Completed
- Cloned backend repository.
- Confirmed stack and runtime shape:
  - Multi-module Maven project.
  - HTTP server bootstrapped via Grizzly and Jersey in backend-server.
- Confirmed no existing integrated LDAP or role-based auth layer in backend source.
- Identified key endpoint and entity targets for phased implementation.

### Key Code Targets Identified
- Server bootstrap and request pipeline:
  - backend-server/src/main/java/de/unileipzig/irpsim/server/ServerStarter.java
- Scenario management endpoint:
  - backend-server/src/main/java/de/unileipzig/irpsim/server/data/simulationparameters/ScenarioEndpoint.java
- Simulation job endpoints:
  - backend-server/src/main/java/de/unileipzig/irpsim/server/optimisation/endpoints/OptimisationJobEndpoint.java
- Standing data endpoint with responsible-person filters:
  - backend-server/src/main/java/de/unileipzig/irpsim/server/standingdata/endpoints/StammdatumEndpoint.java
- Scenario entity metadata:
  - backend-core/src/main/java/de/unileipzig/irpsim/core/data/simulationparameters/OptimisationScenario.java
- Simulation job persistence entity:
  - backend-core/src/main/java/de/unileipzig/irpsim/core/simulation/data/persistence/OptimisationJobPersistent.java
- Standing data entity with responsible-person fields:
  - backend-core/src/main/java/de/unileipzig/irpsim/core/standingdata/data/Stammdatum.java

### Why This Matters
- Confirms technical work started in the correct repository.
- Provides a concrete implementation entry map for Sprint 1 to 3.
- Reduces architecture ambiguity before coding authentication and authorization features.

## 2026-08-04 Sprint 1 Authentication Scaffold Implemented

### Objective
Create executable backend scaffolding for LDAP authentication and session handling without breaking existing endpoints.

### Completed
- Added authentication endpoint package and HTTP endpoints:
  - POST `/simulation/auth/login`
  - POST `/simulation/auth/logout`
  - POST `/simulation/auth/change-password`
- Added LDAP service abstractions and default JNDI implementation.
- Added in-memory session service for token lifecycle.
- Added request authentication filter and registered it in server startup.
- Added unit tests for session handling and authentication endpoint behavior.

### New Code Artifacts
- `backend-server/src/main/java/de/unileipzig/irpsim/server/security/auth/AuthEndpoint.java`
- `backend-server/src/main/java/de/unileipzig/irpsim/server/security/auth/AuthPrincipal.java`
- `backend-server/src/main/java/de/unileipzig/irpsim/server/security/filter/AuthRequestFilter.java`
- `backend-server/src/main/java/de/unileipzig/irpsim/server/security/service/LdapConfiguration.java`
- `backend-server/src/main/java/de/unileipzig/irpsim/server/security/service/LdapAuthenticationService.java`
- `backend-server/src/main/java/de/unileipzig/irpsim/server/security/service/JndiLdapAuthenticationService.java`
- `backend-server/src/main/java/de/unileipzig/irpsim/server/security/service/InMemorySessionService.java`
- `backend-server/src/main/java/de/unileipzig/irpsim/server/security/service/SessionService.java`
- `backend-server/src/main/java/de/unileipzig/irpsim/server/security/service/SecurityServices.java`
- `backend-server/src/test/java/de/unileipzig/irpsim/server/security/AuthEndpointTest.java`
- `backend-server/src/test/java/de/unileipzig/irpsim/server/security/InMemorySessionServiceTest.java`

### Validation Notes
- Maven wrapper is available and runs correctly.
- Full module test execution is currently blocked by unresolved inter-module artifacts unless upstream module build/install is run first.

## 2026-08-13 Sprint 2 Authorization Enforcement (First Protected Endpoint)

### Objective
Move from authentication scaffolding to enforceable authorization behavior on a real backend operation.

### Completed
- Added reusable endpoint-level authorization annotations:
  - `@RequiresAuthentication`
  - `@RequiresGroup`
- Added `AuthorizationRequestFilter` to enforce these annotations at request time.
- Registered authorization filter in server bootstrap (`ServerStarter`) after authentication resolution.
- Applied first live policy to an existing endpoint:
  - `CleanupEndpoint` now requires authentication at class level.
  - `CleanupEndpoint.startCleanup()` now requires membership in `admin` group.
- Added unit tests for authorization filter decisions:
  - Missing principal returns `401`.
  - Authenticated principal without required group returns `403`.
  - Authenticated principal with required group is allowed.

### New Code Artifacts
- `backend-server/src/main/java/de/unileipzig/irpsim/server/security/authorization/RequiresAuthentication.java`
- `backend-server/src/main/java/de/unileipzig/irpsim/server/security/authorization/RequiresGroup.java`
- `backend-server/src/main/java/de/unileipzig/irpsim/server/security/filter/AuthorizationRequestFilter.java`
- `backend-server/src/test/java/de/unileipzig/irpsim/server/security/AuthorizationRequestFilterTest.java`

### Updated Artifacts
- `backend-server/src/main/java/de/unileipzig/irpsim/server/ServerStarter.java`
- `backend-server/src/main/java/de/unileipzig/irpsim/server/endpoints/CleanupEndpoint.java`

### Validation Notes
- Verified that Java 21 is incompatible with backend-core Nashorn-dependent sources (`jdk.nashorn.api.scripting` removed).
- Installed Java 11 locally for compatibility and reran backend reactor commands with explicit `JAVA_HOME`.
- Validation runs now reach backend module compilation and dependency bootstrap; final targeted test completion still requires a clean full Maven cycle in this environment.

### Why This Matters for Week 2
- Demonstrates concrete transition from login-only scaffold to enforced authorization behavior.
- Establishes a reusable authorization policy mechanism that can be applied next to scenario, job, and standing-data endpoints.
