# Reflective Report #2

## Sprint Scope
This sprint focused on moving from authentication scaffolding to enforceable authorization behavior in the backend. The goal was to protect a real operation with role checks, keep the architecture incremental, and generate evidence that authorization is now part of the active request pipeline.

## 1) Requirements

### Requirement Coverage
The project brief requires three capability groups:
- LDAP authentication (login, logout, password change, plus LDAP server startup in build automation).
- Role-based access to scenarios and simulation jobs, including user groups and membership management.
- Role-based read/write access for standing data, replacing the responsible-person assignment model.

### What Was Implemented This Sprint
Week 2 delivered the first concrete authorization enforcement in running backend code:
- Added reusable authorization policy annotations for endpoint-level access control.
- Added an authorization request filter that enforces authentication and group requirements.
- Registered the authorization filter in server startup so policy checks run in the HTTP request chain.
- Applied policy to an existing operational endpoint:
  - Cleanup endpoint now requires authenticated access.
  - Cleanup start operation now requires admin group membership.
- Added authorization-focused unit tests for key decisions:
  - Missing principal results in 401.
  - Wrong group results in 403.
  - Correct group allows request processing.

### What Remains Open
- LDAP server startup automation in build scripts is still pending.
- Role and group rules for scenario and simulation-job ownership and visibility are still pending.
- Standing-data read/write ACL migration is still pending.

### Evidence
- Backend progress evidence is documented in backend resit-progress.md under the Week 2 section.
- New and updated backend classes include:
  - security authorization annotations,
  - authorization request filter,
  - CleanupEndpoint policy annotations,
  - new authorization filter test.

## 2) Architecture

### Chosen Architecture
I continued the additive security architecture introduced in Week 1 and extended it with an authorization layer:
- Authentication filter resolves token to principal.
- Authorization filter evaluates endpoint policy annotations.
- Endpoint methods declare required access explicitly with annotations.

This keeps security concerns modular and aligned with the existing Jersey and Grizzly request pipeline.

### Why This Fits the Brief
This architecture matches the requirements while reducing integration risk:
- It enables incremental rollout endpoint by endpoint.
- It supports group-based policy enforcement without rewriting domain logic.
- It provides a reusable mechanism that can be applied next to scenario, job, and standing-data endpoints.

### Alternative and Rationale
An alternative was to implement authorization logic directly inside each endpoint method.

I did not choose this because:
- It duplicates logic across endpoints.
- It increases maintenance cost and inconsistency risk.
- It makes later policy changes harder to apply globally.

Trade-off of the chosen approach:
- Authorization is now centralized and reusable, but coverage is still partial until more endpoints are annotated and validated.

## 3) Testing

### Testing Performed
- Added a dedicated authorization filter unit test class that verifies:
  - unauthenticated access rejection,
  - insufficient-group rejection,
  - accepted access for matching group.
- Existing authentication and session unit tests remain available from Week 1.

### Validation Environment Work
- Identified and documented Java runtime compatibility constraints in backend compilation.
- Confirmed backend-core relies on Nashorn-era APIs not available on newer Java versions.
- Installed Java 11 and reran backend Maven commands with explicit JAVA_HOME to align with project compatibility.

### Test Outcome and Current Gap
- Authorization implementation compiles at code level and is integrated into server wiring.
- Full targeted test completion in this environment remains affected by reactor bootstrap and dependency cycle constraints.
- The next step is a clean full Maven cycle under the Java 11 toolchain to produce complete pass artifacts for all targeted tests.

## 4) Impediments

### Impediments Encountered
- Java 21 incompatibility with backend-core Nashorn-dependent sources.
- Multi-module Maven reactor complexity during targeted test execution.
- Long dependency bootstrap cycles in validation runs.

### Mitigation This Sprint
- Switched validation runtime to Java 11 and documented the compatibility requirement.
- Kept changes scoped to one protected endpoint to ensure controlled progress.
- Added unit-level authorization tests to provide immediate verification signal.

### Mitigation Next Sprint
- Establish a stable Java 11 build profile for backend validation commands.
- Run full clean Maven cycle to generate definitive test pass evidence.
- Extend annotation-based authorization to scenario and job endpoints, then add endpoint-level negative tests.

## Conclusion
Week 2 achieved meaningful technical progression from authentication-only scaffolding to enforceable authorization behavior. The backend now contains a reusable authorization mechanism integrated into the request pipeline and applied to a real operation. This de-risks Week 3, where the same mechanism can be expanded to scenario, simulation-job, and standing-data access rules.
