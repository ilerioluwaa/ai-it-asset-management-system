
# Performance Evaluation and Benchmarking

AI-Powered IT Asset Management System

Version: 1.0.0 - Final Release Candidate

## 1. Purpose

This document establishes the performance evaluation,
benchmarking, and software quality assessment methodology
for the AI-Powered IT Asset Management System.

The evaluation focuses on:

- Application responsiveness.
- Database retrieval efficiency.
- Inventory scalability.
- Automated test reliability.
- Source-code coverage.
- Application startup performance.
- Deployment reliability.
- AI recommendation latency.
- AI recommendation accuracy and usefulness.
- Security control effectiveness.

Performance conclusions must be supported by reproducible
measurements rather than estimated or fabricated results.

## 2. Evaluation Objectives

The primary objectives are to:

1. Establish measurable application performance indicators.
2. Evaluate the effectiveness of architectural refinements.
3. Assess the scalability of paginated inventory retrieval.
4. Verify application reliability through automated testing.
5. Measure AI recommendation response times.
6. Evaluate AI-generated recommendations against source data.
7. Verify reproducible containerized deployment.
8. Identify remaining performance-related technical debt.

## 3. Alpha Release Baseline

The Alpha Release established the initial functional
application and automated testing environment.

### Verified Automated Test Baseline

| Metric | Observed Result |
|---|---|
| Total tests | 8 |
| Successful tests | 8 |
| Failed tests | 0 |
| Errors | 0 |
| Skipped tests | 0 |
| Build result | BUILD SUCCESS |
| Maven execution duration | 3.500 seconds |
| Java version | 25.0.2 |
| Spring Boot version | 4.0.9 |
| Test database | H2 in-memory database |

The Maven execution duration represents the observed
build and test command duration.

It does not represent individual API response latency.

### Assignment Regression Baseline

Following peer review, an additional regression test
was introduced to evaluate assignment preservation.

| Metric | Observed Result |
|---|---|
| Total tests | 9 |
| Successful tests | 8 |
| Failed tests | 1 |
| Errors | 0 |
| Skipped tests | 0 |
| Build result | BUILD FAILURE |
| Maven execution duration | 2.978 seconds |

The regression test demonstrated that an ordinary asset
update could remove an existing employee assignment.

Expected employee assignment: John Smith.

Actual employee assignment: null.

The failure established a reproducible functional
defect requiring remediation.

### Baseline Limitations

The Alpha Release did not establish independently
verified measurements for:

- REST API response latency.
- Large inventory retrieval performance.
- Database query efficiency.
- Source-code coverage percentages.
- Live AI recommendation latency.
- AI recommendation quality.
- Containerized deployment reliability.

These measurements must not be retrospectively invented.

## 4. Benchmark Environment

All performance measurements must identify the
environment in which testing was conducted.

### Local Development Environment

| Component | Configuration |
|---|---|
| Operating system | macOS |
| Processor architecture | ARM64 |
| Processor model | Pending verification |
| Available memory | Pending verification |
| Java version | 25.0.2 |
| Spring Boot version | 4.0.9 |
| Application database | MySQL |
| Automated test database | H2 |
| Browser | Pending verification |
| Network configuration | Pending verification |

### Containerized Environment

| Component | Configuration |
|---|---|
| Application runtime | Eclipse Temurin Java 25 |
| Database image | MySQL 8.4 |
| Container orchestration | Docker Compose |
| Application port | 8080 |
| Database port | 3306 within private network |
| Database storage | Persistent Docker volume |
| Docker version | Pending verification |
| CPU allocation | Pending verification |
| Memory allocation | Pending verification |

### Continuous Integration Environment

| Component | Configuration |
|---|---|
| Platform | GitHub Actions |
| Runner | ubuntu-latest |
| Java distribution | Eclipse Temurin |
| Java version | 25 |
| Build tool | Maven Wrapper |
| Coverage tool | JaCoCo |
| Deployment tool | Docker Compose |

The actual runner environment and workflow execution
details must be recorded during verification.

## 5. Environment Verification Commands

### Operating System

```bash
sw_vers
```

### Processor Information

```bash
sysctl -n machdep.cpu.brand_string
```

### Available Memory

```bash
sysctl -n hw.memsize
```

The memory command returns a value in bytes.

### Java Version

```bash
java -version
```

### Maven Environment

```bash
./mvnw --version
```

### Docker Version

```bash
docker --version
```

### Docker Compose Version

```bash
docker compose version
```

### Container Resource Usage

```bash
docker stats --no-stream
```

### Source-Code Revision

```bash
git rev-parse HEAD
```

Record the commit identifier associated with each
benchmark execution.

## 6. Performance Indicators

The following indicators will be used to evaluate
application performance and quality.

| Indicator | Measurement Unit |
|---|---|
| API response latency | Milliseconds |
| Median response latency | Milliseconds |
| 95th percentile latency | Milliseconds |
| Maximum response latency | Milliseconds |
| Request success rate | Percentage |
| Request throughput | Requests per second |
| Response payload size | Bytes |
| Automated test duration | Seconds |
| Automated test pass rate | Percentage |
| Instruction coverage | Percentage |
| Branch coverage | Percentage |
| Line coverage | Percentage |
| Application startup duration | Seconds |
| AI recommendation latency | Milliseconds |
| AI recommendation accuracy | Percentage |
| Container memory consumption | MiB or GiB |
| Container CPU utilization | Percentage |

## 7. Proposed Performance Acceptance Targets

The following targets are proposed engineering
acceptance criteria for controlled evaluation.

They are not measured results or guaranteed
production service-level agreements.

| Metric | Proposed Target |
|---|---|
| Asset retrieval median latency | 200 ms or less |
| Asset retrieval p95 latency | 500 ms or less |
| Employee retrieval median latency | 200 ms or less |
| Employee retrieval p95 latency | 500 ms or less |
| Dashboard statistics p95 latency | 1000 ms or less |
| Sequential request success rate | 100% |
| Automated test pass rate | 100% |
| Unexpected automated test errors | 0 |
| Instruction coverage | At least 80% |
| Line coverage | At least 80% |
| Branch coverage | At least 70% |
| Application startup duration | 60 seconds or less |
| AI recommendation latency | 60 seconds or less |
| AI numerical consistency | 100% |
| Unauthorized API access | Rejected |
| Container deployment health | UP |

Coverage targets are evaluation goals rather than
automatically enforced Maven build thresholds.

AI response latency depends on the selected model,
provider infrastructure, network conditions, and
available computational resources.

## 8. Performance Testing Methodology

Performance testing should use a controlled and
documented procedure.

### Testing Procedure

1. Record the application version and Git commit.
2. Record the hardware and software environment.
3. Start the application and database.
4. Verify application health.
5. Prepare a representative test dataset.
6. Authenticate as an administrator.
7. Perform initial warm-up requests.
8. Execute repeated benchmark requests.
9. Record response status and duration.
10. Calculate summary statistics.
11. Compare measurements with acceptance targets.
12. Document failures and limitations.

### Warm-Up Requests

Execute five preliminary requests before recording
performance measurements.

Warm-up requests reduce the influence of initial
class loading, database connection establishment,
and other startup-related activity.

Warm-up measurements should be excluded from the
primary latency calculations.

### Measurement Sample

Execute at least 30 measured requests for each
selected endpoint and dataset configuration.

Record the following:

- HTTP response status.
- Response duration.
- Response payload size.
- Dataset size.
- Requested page size.
- Testing environment.

Failed requests must be reported separately rather
than silently excluded from the success-rate calculation.

## 9. Benchmark Dataset Design

Performance testing should use fictional records.

The proposed dataset configurations are:

| Dataset | Assets | Employees |
|---|---:|---:|
| Small | 10 | 4 |
| Medium | 100 | 25 |
| Large | 1000 | 100 |
| Extended | 10000 | 500 |

The extended dataset is optional and intended
for additional scalability evaluation.

Each dataset should include representative:

- Asset categories.
- Employee departments.
- Available assets.
- Assigned assets.
- Maintenance assets.
- Retired assets.

All serial numbers and employee email addresses
must remain unique.

Assigned assets must reference valid employees.

Benchmark datasets must be created in a dedicated
test database rather than an operational database.

## 10. REST API Benchmark Endpoints

The following endpoints are selected for
performance evaluation.

| Endpoint | Operation |
|---|---|
| GET /api/assets?page=0&size=10 | Paginated inventory retrieval |
| GET /api/assets?page=0&size=100 | Larger inventory page |
| GET /api/assets?status=AVAILABLE | Status-filtered retrieval |
| GET /api/assets?keyword=Laptop | Keyword search |
| GET /api/employees?page=0&size=10 | Employee directory retrieval |
| GET /api/assets/statistics | Inventory statistics |
| GET /api/employees/statistics | Employee statistics |
| GET /actuator/health | Application availability |

Application API benchmarks require an authenticated
administrator session.

The health endpoint is publicly accessible.

## 11. Browser-Based API Benchmarking

The following JavaScript benchmark may be executed
from the browser developer console while signed
in to the application.

The benchmark performs read-only requests against
the selected endpoint.

```javascript
async function benchmarkEndpoint(
    endpoint,
    iterations = 30,
    warmup = 5
) {
    for (let i = 0; i < warmup; i++) {
        const response = await fetch(endpoint, {
            credentials: "same-origin",
            cache: "no-store"
        });

        await response.arrayBuffer();
    }

    const measurements = [];
    const statuses = {};
    let successfulRequests = 0;
    let totalBytes = 0;

    const benchmarkStart = performance.now();

    for (let i = 0; i < iterations; i++) {
        const start = performance.now();

        try {
            const response = await fetch(endpoint, {
                credentials: "same-origin",
                cache: "no-store"
            });

            const body = await response.arrayBuffer();
            const duration = performance.now() - start;

            statuses[response.status] =
                (statuses[response.status] || 0) + 1;

            const contentType =
                response.headers.get("content-type") || "";

            const successful =
                response.ok &&
                !response.redirected &&
                contentType.includes("application/json");

            if (successful) {
                successfulRequests++;
                measurements.push(duration);
                totalBytes += body.byteLength;
            }

        } catch (error) {
            statuses.networkError =
                (statuses.networkError || 0) + 1;
        }
    }

    const elapsed =
        performance.now() - benchmarkStart;

    measurements.sort((a, b) => a - b);

    const count = measurements.length;

    const percentile = (p) => {
        if (count === 0) {
            return null;
        }

        const index = Math.max(
            0,
            Math.ceil(p * count) - 1
        );

        return measurements[index];
    };

    const mean = count > 0
        ? measurements.reduce((a, b) => a + b, 0) / count
        : null;

    const median = count === 0
        ? null
        : count % 2 === 0
            ? (
                measurements[count / 2 - 1] +
                measurements[count / 2]
              ) / 2
            : measurements[Math.floor(count / 2)];

    const round = (value) =>
        value === null
            ? null
            : Number(value.toFixed(2));

    const result = {
        endpoint,
        measuredRequests: iterations,
        successfulRequests,
        failedRequests: iterations - successfulRequests,
        successRatePercent: round(
            successfulRequests / iterations * 100
        ),
        minimumMs: count > 0
            ? round(measurements[0])
            : null,
        meanMs: round(mean),
        medianMs: round(median),
        p95Ms: round(percentile(0.95)),
        maximumMs: count > 0
            ? round(measurements[count - 1])
            : null,
        averageResponseBytes: count > 0
            ? Math.round(totalBytes / count)
            : null,
        sequentialRequestsPerSecond: round(
            iterations / (elapsed / 1000)
        ),
        statuses
    };

    console.table(result);

    return result;
}
```

### Example Benchmark Execution

```javascript
await benchmarkEndpoint(
    "/api/assets?page=0&size=10"
);
```

```javascript
await benchmarkEndpoint(
    "/api/assets?page=0&size=100"
);
```

```javascript
await benchmarkEndpoint(
    "/api/employees?page=0&size=10"
);
```

```javascript
await benchmarkEndpoint(
    "/api/assets/statistics"
);
```

```javascript
await benchmarkEndpoint(
    "/api/employees/statistics"
);
```

The measurements include browser-side request handling
and response body retrieval.

The reported response size represents the decoded
response body rather than total network transfer size.

Sequential requests per second must not be interpreted
as maximum concurrent server throughput.

This procedure does not replace dedicated load testing.

## 12. Performance Calculation Methods

### Mean Response Time

    Mean = Sum of successful response times
           / Number of successful responses

### Median Response Time

The median is the middle value of the sorted
response-time measurements.

For an even number of measurements, the median
is the average of the two middle values.

### 95th Percentile Response Time

The 95th percentile represents the response time
at or below which approximately 95% of measured
successful requests completed.

The benchmark uses the nearest-rank method.

    Rank = Ceiling(0.95 * Number of measurements)

### Request Success Rate

    Success Rate =
        Successful Requests / Total Requests * 100

### Sequential Request Rate

    Request Rate =
        Total Requests / Total Elapsed Seconds

### Performance Improvement

Where comparable baseline measurements exist:

    Improvement Percentage =
        (Baseline Latency - Revised Latency)
        / Baseline Latency * 100

This formula must only be used when the baseline
and revised measurements were collected under
comparable conditions.

## 13. API Performance Results

### Small Dataset

| Endpoint | Requests | Median ms | p95 ms | Success Rate |
|---|---:|---:|---:|---:|
| Asset retrieval, size 10 | Pending | Pending | Pending | Pending |
| Asset retrieval, size 100 | Pending | Pending | Pending | Pending |
| Asset status filtering | Pending | Pending | Pending | Pending |
| Asset keyword search | Pending | Pending | Pending | Pending |
| Employee retrieval | Pending | Pending | Pending | Pending |
| Asset statistics | Pending | Pending | Pending | Pending |
| Employee statistics | Pending | Pending | Pending | Pending |

### Medium Dataset

| Endpoint | Requests | Median ms | p95 ms | Success Rate |
|---|---:|---:|---:|---:|
| Asset retrieval, size 10 | Pending | Pending | Pending | Pending |
| Asset retrieval, size 100 | Pending | Pending | Pending | Pending |
| Asset status filtering | Pending | Pending | Pending | Pending |
| Asset keyword search | Pending | Pending | Pending | Pending |
| Employee retrieval | Pending | Pending | Pending | Pending |
| Asset statistics | Pending | Pending | Pending | Pending |
| Employee statistics | Pending | Pending | Pending | Pending |

### Large Dataset

| Endpoint | Requests | Median ms | p95 ms | Success Rate |
|---|---:|---:|---:|---:|
| Asset retrieval, size 10 | Pending | Pending | Pending | Pending |
| Asset retrieval, size 100 | Pending | Pending | Pending | Pending |
| Asset status filtering | Pending | Pending | Pending | Pending |
| Asset keyword search | Pending | Pending | Pending | Pending |
| Employee retrieval | Pending | Pending | Pending | Pending |
| Asset statistics | Pending | Pending | Pending | Pending |
| Employee statistics | Pending | Pending | Pending | Pending |

Results must be populated after benchmark execution.

## 14. Pagination and Scalability Evaluation

The Alpha Release retrieved the complete asset
inventory through its collection endpoint.

The Final Release Candidate introduces paginated
asset and employee retrieval.

### Expected Architectural Benefits

- Smaller response payloads.
- Reduced frontend rendering requirements.
- Controlled records per request.
- Improved navigation through large datasets.
- Reduced unnecessary entity retrieval.

### Illustrative Record Retrieval Comparison

| Total Assets | Alpha Full Retrieval | Revised Page Size 10 |
|---:|---:|---:|
| 10 | 10 | 10 |
| 100 | 100 | 10 |
| 1000 | 1000 | 10 |
| 10000 | 10000 | 10 |

For an inventory containing 1000 records, retrieving
a page of ten records reduces the number of returned
asset records by:

    (1000 - 10) / 1000 * 100 = 99%

This is a theoretical record-count reduction.

It does not establish a 99% reduction in response
latency, database workload, or transferred bytes.

Pagination may also require count queries and
additional database processing.

Actual performance improvements must be measured.

## 15. Database Performance Evaluation

The revised database design introduces:

- Unique serial number constraints.
- Unique employee email constraints.
- Asset status indexes.
- Asset category indexes.
- Employee department indexes.
- Employee foreign-key relationships.
- Database connection pooling.
- Aggregate statistics queries.

### Query Inspection

MySQL execution plans may be inspected using
the EXPLAIN statement.

Example:

```sql
EXPLAIN
SELECT *
FROM asset
WHERE status = 'AVAILABLE'
ORDER BY id DESC
LIMIT 10;
```

Example employee assignment query:

```sql
EXPLAIN
SELECT *
FROM asset
WHERE employee_id = 1;
```

Review:

- Selected indexes.
- Estimated rows examined.
- Query access type.
- Filtering behavior.
- Additional sorting requirements.

Index availability does not guarantee that
MySQL will select an index for every query.

### Known Database Limitations

The current design requires additional evaluation of:

- Case-insensitive wildcard searches.
- Employee assignment count queries.
- Large-offset pagination.
- Concurrent equipment allocation.
- Aggregate statistics query costs.
- Database connection pool configuration.

These limitations are recorded in the technical
debt register.

## 16. Automated Test Evaluation

Execute the complete automated test suite:

```bash
./mvnw clean test
```

Execute the build and coverage lifecycle:

```bash
./mvnw clean verify
```

### Test Class Results

| Test Class | Executed | Passed | Failed | Errors | Skipped |
|---|---:|---:|---:|---:|---:|
| AssetControllerTest | Pending | Pending | Pending | Pending | Pending |
| EmployeeControllerTest | Pending | Pending | Pending | Pending | Pending |
| AiRecommendationServiceTest | Pending | Pending | Pending | Pending | Pending |
| SecurityTest | Pending | Pending | Pending | Pending | Pending |
| AssetmanagementApplicationTests | Pending | Pending | Pending | Pending | Pending |
| Total | Pending | Pending | Pending | Pending | Pending |

### Test Pass Rate

    Passed Tests =
        Total Tests - Failures - Errors - Skipped Tests

    Executed Tests =
        Total Tests - Skipped Tests

    Pass Rate =
        Passed Tests / Executed Tests * 100

Skipped tests must be reported separately.

### Alpha and Final Release Comparison

| Metric | Alpha Baseline | Final Release |
|---|---|---|
| Total tests | 8 | Pending |
| Failures | 0 | Pending |
| Errors | 0 | Pending |
| Skipped tests | 0 | Pending |
| Pass rate | 100% | Pending |
| Maven execution duration | 3.500 seconds | Pending |
| Assignment regression | Failed in subsequent test | Pending |
| Code coverage | Not measured | Pending |

An increased test count does not independently
establish increased software quality.

Test relevance, assertion quality, coverage,
and defect detection must also be considered.

## 17. Code Coverage Evaluation

JaCoCo is integrated into the Maven build lifecycle.

Generate the coverage report:

```bash
./mvnw clean verify
```

Open the HTML report on macOS:

```bash
open target/site/jacoco/index.html
```

### Coverage Report Locations

HTML report:

    target/site/jacoco/index.html

XML report:

    target/site/jacoco/jacoco.xml

### Coverage Measurements

| Coverage Metric | Proposed Target | Actual Result |
|---|---:|---|
| Instruction coverage | 80% | Pending |
| Line coverage | 80% | Pending |
| Branch coverage | 70% | Pending |
| Method coverage | Record actual result | Pending |
| Class coverage | Record actual result | Pending |

### Coverage Calculation

    Coverage Percentage =
        Covered Items / Total Items * 100

Coverage should be reviewed at both application
and package levels.

Priority areas include:

- Asset assignment preservation.
- Employee deletion safeguards.
- Duplicate record validation.
- Authentication and authorization.
- CSRF protection.
- AI provider error handling.
- Database integrity operations.

High code coverage does not establish the
absence of software defects.

## 18. Security Verification

Security evaluation focuses on whether the
implemented controls behave as intended.

| Security Scenario | Expected Result | Actual Result |
|---|---|---|
| Unauthenticated asset API request | HTTP 401 | Pending |
| Unauthenticated employee API request | HTTP 401 | Pending |
| Unauthenticated AI API request | HTTP 401 | Pending |
| Unauthorized user role | HTTP 403 | Pending |
| Missing CSRF token | HTTP 403 | Pending |
| Invalid CSRF token | HTTP 403 | Pending |
| Valid administrator login | Authentication succeeds | Pending |
| Invalid administrator login | Authentication rejected | Pending |
| Logout | Session terminated | Pending |
| Duplicate asset registration | HTTP 409 | Pending |
| Invalid request data | HTTP 400 | Pending |
| Assigned asset deletion | HTTP 409 | Pending |
| Assigned employee deletion | HTTP 409 | Pending |

These tests establish specific security control
behavior under controlled conditions.

They do not constitute a comprehensive penetration
test or production security certification.

## 19. Application Startup Performance

Application startup duration should be recorded
from actual Spring Boot startup logs.

For local development:

```bash
./mvnw spring-boot:run
```

For Docker deployment:

```bash
docker compose up -d --build
```

Inspect container logs:

```bash
docker compose logs application
```

Record the reported Spring Boot startup duration.

Separately measure deployment readiness from
container startup until the health endpoint
first returns UP.

### Startup Results

| Environment | Application Startup | Deployment Readiness |
|---|---|---|
| Local Java and MySQL | Pending | Pending |
| Local Docker Compose | Pending | Pending |
| GitHub Actions deployment | Pending | Pending |

Docker image build duration must be reported
separately from application startup duration.

## 20. Container Resource Evaluation

Resource consumption may be inspected using:

```bash
docker stats --no-stream
```

Record resource consumption under:

- Idle application conditions.
- Inventory retrieval activity.
- Dashboard statistics retrieval.
- AI recommendation generation.

### Resource Measurements

| Scenario | Application CPU | Application Memory | Database Memory |
|---|---|---|---|
| Idle | Pending | Pending | Pending |
| Inventory retrieval | Pending | Pending | Pending |
| Dashboard retrieval | Pending | Pending | Pending |
| AI recommendation | Pending | Pending | Pending |

A single resource snapshot is not sufficient
to establish sustained or peak resource usage.

Repeated observations should be collected where
resource performance conclusions are required.

## 21. AI Recommendation Performance

The AI recommendation service records the
response time associated with recommendation
generation.

AI latency depends on:

- Selected AI model.
- Provider infrastructure.
- Network latency.
- Prompt processing.
- Response generation length.
- Available computational resources.
- Provider request limits.

### AI Benchmark Procedure

1. Configure a compatible AI provider.
2. Verify that the selected model is available.
3. Prepare a known inventory dataset.
4. Record the inventory statistics.
5. Generate a recommendation.
6. Record the model identifier.
7. Record the reported response time.
8. Review numerical accuracy.
9. Review recommendation relevance.
10. Repeat the procedure using representative scenarios.

A minimum of five live recommendation requests
is proposed for an initial latency evaluation.

Provider usage costs and local computational
requirements must be considered.

### AI Performance Results

| Trial | Model | Response Time ms | Result |
|---|---|---:|---|
| 1 | Pending | Pending | Pending |
| 2 | Pending | Pending | Pending |
| 3 | Pending | Pending | Pending |
| 4 | Pending | Pending | Pending |
| 5 | Pending | Pending | Pending |

### AI Latency Summary

| Metric | Result |
|---|---|
| Successful requests | Pending |
| Failed requests | Pending |
| Minimum latency | Pending |
| Mean latency | Pending |
| Median latency | Pending |
| Maximum latency | Pending |
| Success rate | Pending |

A five-request sample provides an initial
evaluation rather than a statistically robust
production reliability estimate.

## 22. AI Recommendation Quality Evaluation

Live AI recommendations must be evaluated
against known inventory data.

### Evaluation Criteria

| Criterion | Description |
|---|---|
| Numerical consistency | Reported quantities match source statistics |
| Relevance | Recommendations address inventory conditions |
| Actionability | Suggestions provide practical operational guidance |
| Grounding | Claims are supported by available information |
| Privacy | Individual employee information is not disclosed |
| Human oversight | Recommendations remain advisory |

### Scoring Method

Each criterion is assigned a score.

| Score | Meaning |
|---|---|
| 0 | Criterion not satisfied |
| 1 | Criterion partially satisfied |
| 2 | Criterion fully satisfied |

Maximum score per recommendation: 12.

    Quality Percentage =
        Observed Score / 12 * 100

This is a project-defined human evaluation rubric.

It is not a standardized AI accuracy benchmark.

Numerical consistency should also be evaluated
independently.

    Numerical Consistency =
        Correct Numerical Claims
        / Total Verifiable Numerical Claims * 100

If a recommendation contains no numerical claims,
record numerical consistency as Not Applicable.

Do not assign a perfect numerical consistency
score solely because no numerical claims exist.

### AI Evaluation Scenarios

#### Scenario A: Balanced Inventory

| Metric | Value |
|---|---:|
| Total Assets | 20 |
| Available | 6 |
| Assigned | 10 |
| Maintenance | 3 |
| Retired | 1 |
| Employees | 10 |

Expected evaluation focus:

- Recognize available inventory.
- Consider maintenance requirements.
- Avoid unsupported procurement conclusions.

#### Scenario B: Limited Availability

| Metric | Value |
|---|---:|
| Total Assets | 20 |
| Available | 1 |
| Assigned | 16 |
| Maintenance | 2 |
| Retired | 1 |
| Employees | 18 |

Expected evaluation focus:

- Recognize limited available equipment.
- Consider potential allocation constraints.
- Avoid claiming a confirmed shortage without
  evidence of individual equipment requirements.

#### Scenario C: Elevated Maintenance

| Metric | Value |
|---|---:|
| Total Assets | 20 |
| Available | 3 |
| Assigned | 8 |
| Maintenance | 8 |
| Retired | 1 |
| Employees | 10 |

Expected evaluation focus:

- Recognize the high maintenance proportion.
- Recommend reviewing maintenance priorities.
- Avoid inventing specific equipment failures.

#### Scenario D: Empty Inventory

| Metric | Value |
|---|---:|
| Total Assets | 0 |
| Available | 0 |
| Assigned | 0 |
| Maintenance | 0 |
| Retired | 0 |
| Employees | 0 |

Expected evaluation focus:

- Recognize insufficient operational data.
- Recommend establishing inventory records.
- Avoid fabricating assets or employees.

### AI Quality Results

| Scenario | Numerical Consistency | Quality Score | Result |
|---|---|---|---|
| Balanced inventory | Pending | Pending | Pending |
| Limited availability | Pending | Pending | Pending |
| Elevated maintenance | Pending | Pending | Pending |
| Empty inventory | Pending | Pending | Pending |

Actual recommendation text should be retained
as supporting evaluation evidence.

## 23. Deployment Reliability Evaluation

The GitHub Actions deployment verification
workflow evaluates application startup within
a temporary MySQL-backed Docker environment.

### Deployment Verification Criteria

| Criterion | Expected Result | Actual Result |
|---|---|---|
| Maven build | Successful | Pending |
| Automated tests | Successful | Pending |
| Coverage report | Generated | Pending |
| Docker image build | Successful | Pending |
| MySQL startup | Healthy | Pending |
| Spring Boot startup | Successful | Pending |
| Health endpoint | UP | Pending |
| Login page | HTTP 200 | Pending |
| Protected asset API | HTTP 401 | Pending |
| Protected employee API | HTTP 401 | Pending |
| Protected AI API | HTTP 401 | Pending |
| Deployment artifacts | Uploaded | Pending |

### Deployment Reliability Formula

    Deployment Success Rate =
        Successful Deployment Runs
        / Total Completed Deployment Attempts * 100

Canceled workflow runs should be reported
separately.

Multiple completed deployment attempts are
required before drawing broader reliability
conclusions.

The workflow verifies temporary deployment
rather than permanent production hosting.

## 24. Software Quality Evaluation

The application evaluation is organized around
software quality characteristics commonly
associated with ISO/IEC 25010.

| Quality Characteristic | Project Evaluation |
|---|---|
| Functional suitability | Asset, employee, allocation, and AI verification |
| Performance efficiency | API latency and resource measurements |
| Compatibility | Environment and deployment verification |
| Interaction capability | Browser usability and interface evaluation |
| Reliability | Automated testing and deployment verification |
| Security | Authentication, authorization, and CSRF testing |
| Maintainability | Layered architecture and technical debt management |
| Flexibility | Configuration and container deployment |
| Safety | Human oversight and prevention of unintended actions |

The framework is used as an evaluation reference.

The application has not undergone formal
ISO certification or independent conformity assessment.

## 25. Evidence Collection

The following evidence should be retained
during consolidated verification:

- Maven build output.
- Surefire test reports.
- JaCoCo HTML and XML reports.
- Assignment regression test results.
- API benchmark output.
- Benchmark environment details.
- AI recommendation responses.
- AI response latency measurements.
- AI quality evaluation records.
- Docker container status.
- Application health responses.
- GitHub Actions deployment artifacts.
- Application startup logs.
- Relevant Git commit identifiers.

Suggested evidence directory:

    evidence/
        testing/
        coverage/
        performance/
        security/
        ai/
        deployment/

Do not include passwords, API keys, session
cookies, database backups, or confidential
employee information in public evidence files.

Generated evidence should be reviewed before
publication.

## 26. Final Performance Summary

| Evaluation Area | Status |
|---|---|
| Automated test execution | Pending |
| Assignment regression remediation | Pending |
| Code coverage measurement | Pending |
| Asset retrieval benchmarking | Pending |
| Employee retrieval benchmarking | Pending |
| Dashboard benchmarking | Pending |
| Database scalability evaluation | Pending |
| Security verification | Pending |
| Application startup measurement | Pending |
| AI response latency evaluation | Pending |
| AI output quality evaluation | Pending |
| Docker deployment verification | Pending |
| GitHub Actions verification | Pending |

This table must be updated using actual
verification evidence.

## 27. Performance Limitations

The proposed evaluation has several limitations:

- Local hardware may not represent production infrastructure.
- H2 tests do not fully reproduce MySQL behavior.
- Sequential benchmarks do not establish concurrent capacity.
- Small benchmark samples limit statistical confidence.
- Browser measurements include client-side overhead.
- AI latency varies by model and provider.
- AI output quality requires human judgment.
- Code coverage does not guarantee defect-free software.
- Container deployment verification does not establish uptime.
- Comprehensive stress and endurance testing remain outstanding.

Future evaluations should incorporate:

- Concurrent load testing.
- Stress testing.
- Endurance testing.
- Database query profiling.
- Memory profiling.
- Concurrent assignment testing.
- Larger AI evaluation datasets.
- Production monitoring.

## 28. Technical References

ISO/IEC 25010 software quality model:

https://www.iso.org/standard/78176.html

JaCoCo documentation:

https://www.jacoco.org/jacoco/trunk/doc/

Apache Maven Surefire documentation:

https://maven.apache.org/surefire/maven-surefire-plugin/

Spring Boot documentation:

https://docs.spring.io/spring-boot/

MySQL documentation:

https://dev.mysql.com/doc/

Docker documentation:

https://docs.docker.com/

GitHub Actions documentation:

https://docs.github.com/actions

## 29. Conclusion

The Final Release Candidate introduces architectural
refinements intended to improve functionality,
maintainability, security, and scalability.

The performance evaluation methodology establishes
measurable criteria for assessing these improvements.

Final conclusions must be based on actual application
execution, automated test results, benchmark measurements,
AI recommendation evaluation, and deployment evidence.

The application must not be represented as fully
verified until the required evaluation activities
have been completed.

---

AI-Powered IT Asset Management System

CMSC 495 - Computer Capstone Project

University of Maryland Global Campus
