# Resit Release Checklist

Use this checklist before each submission point to produce objective evidence of engineering quality.

## Release Build and Test

- [ ] Run `./gradlew verifyAll` successfully.
- [ ] Confirm no new test failures in default tests, `internaltest`, and `toymodel` suites.
- [ ] Confirm `build/libs/IRPact-1.0-SNAPSHOT-uber.jar` is generated.
- [ ] Record Java version used for the build.

## Source Control and Traceability

- [ ] Ensure every code change has a clear commit message.
- [ ] Ensure each requirement has linked code and/or test evidence.
- [ ] Ensure CI status is green for the target branch.

## Demonstration Readiness (Week 4)

- [ ] Prepare a short demo scenario that runs reliably.
- [ ] Verify commands work from a clean terminal session.
- [ ] Prepare slides showing requirements, architecture, implementation choices, and live run.
- [ ] Time the demo to stay within 15 minutes.

## Reflective Report Readiness

- [ ] Requirements: list implemented sprint requirements and status.
- [ ] Architecture: document chosen architecture and one alternative considered.
- [ ] Testing: include automated test strategy and evidence.
- [ ] Impediments: explain blockers, mitigation, and next sprint actions.
- [ ] Quality: run spelling and grammar checks.

## Submission Packaging

- [ ] Create the sprint source zip matching repository state.
- [ ] Verify zip opens and includes expected files.
- [ ] Ensure report page limit is respected.
- [ ] Verify uploaded artifacts match the sprint milestone scope.
