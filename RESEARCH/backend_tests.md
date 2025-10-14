# Research Report
## Backend Testing
### Summary of Work
<!--One paragraph summary of the research being performed-->
This research is about the most popular backend testing approaches. It identifies
challenges in backend testing and how to navigate those challenges with different methods.

### Motivation
<!--Explain why you felt the need to perform this research-->
We feel testing in general will be one of the most challenging things to do
in this project. So I thought I would make sure we had some idea of methods on how to test our code.
### Time Spent

<!--Explain how your time was spent-->
Reading and documenting online sources ~ 60 minutes
Watching tutorials ~ 30 minutes
### Results
Key Findings

Layered Testing Strategy Is Essential
All sources emphasize that backend testing must be approached in layers rather than relying on a single type of test.

Unit tests validate each microservice’s internal logic.

Contract tests ensure that services communicate correctly through well-defined interfaces.

Integration tests verify how services and databases interact.

End-to-end (E2E) tests confirm that full workflows across multiple services function correctly.
This layered structure balances reliability, performance, and test coverage, avoiding the bottlenecks of running too many slow E2E tests. 1
2

Contract Testing Improves Stability and Autonomy
According to the Pact documentation, consumer-driven contract testing (CDCT) allows teams to verify service interactions without deploying the entire system.
Consumers define the requests and responses they expect, and providers validate that their implementations meet those expectations.
This approach reduces integration failures, increases team independence, and speeds up CI/CD pipelines. 3

Research Confirms Industry Pain Points
The ScienceDirect review identifies common challenges faced by both industry and academia:

Flaky tests caused by timing, asynchronous events, or unstable dependencies.

High testing costs and time consumption at the integration and system levels.

Test maintenance overhead, especially for mocks, stubs, and contract files.

Complexity in testing event-driven or asynchronous systems, where interactions are harder to capture and verify.
Research supports using hybrid approaches and automation to mitigate these issues. 2

Service Virtualization Supports Test Isolation
The AccelQ blog highlights service virtualization as a key method for testing services independently when real dependencies are unavailable or unreliable.
Virtualized services simulate APIs or databases, improving test stability and speed. However, teams must periodically validate against real systems to prevent drift between simulated and actual behavior. 1

Ongoing Test Maintenance Is Critical
All three sources underline that backend systems evolve continuously. As services change, contracts, mocks, and integration tests must be updated to remain valid. Automating this maintenance through CI/CD pipelines helps sustain reliability over time. 1
3

Practical Recommendations for the Team

Adopt a testing pyramid: prioritize unit and contract tests, supported by targeted integration and limited E2E testing.

Implement consumer-driven contract testing with Pact or similar tools to reduce integration complexity.

Use service virtualization to stabilize test environments, but verify periodically against real dependencies.

Monitor and reduce flaky tests through isolation, controlled test data, and deterministic configurations.

Maintain and version contracts and mocks as part of regular development workflows.
### Sources

[^1]: https://www.accelq.com/blog/testing-microservices
[^2]: https://www.sciencedirect.com/science/article/abs/pii/S0164121224002760
[^3]: https://docs.pact.io

