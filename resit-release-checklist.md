# Resit Release Checklist (Backend)

Use this checklist before each sprint hand-in to keep backend implementation, demo, and report evidence aligned.

## Build and Runtime Baseline

- [ ] Run backend build command successfully (`./mvnw -B package -DskipTests -pl ":backend-server,:backend-gams" -am`).
- [ ] Confirm backend starts locally with MariaDB and expected environment variables.
- [ ] Verify API responds under the configured server port.

## Security Feature Verification

- [ ] LDAP login works with valid credentials.
- [ ] Invalid credentials are rejected with consistent HTTP status.
- [ ] Logout invalidates session or token.
- [ ] Password change flow is tested and documented.
- [ ] Scenario and simulation job access is filtered by user or group permission.
- [ ] Standing data read and write checks are enforced by user or group permission.

## Regression and Quality

- [ ] Existing scenario import and simulation execution paths still work.
- [ ] Existing standing data endpoints still function for authorized users.
- [ ] Negative tests exist for unauthorized access attempts.
- [ ] CI workflow is green after changes.

## Demo and Report Readiness

- [ ] Demo script includes one authorized and one unauthorized request example.
- [ ] Architecture diagrams include auth and authorization flow.
- [ ] Reflective report includes requirements, architecture alternatives, testing, impediments.
- [ ] All claims in report have command output, test, or code evidence.
