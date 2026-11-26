
# Performance Evaluation and Benchmarking

AI-Powered IT Asset Management System

Version: 1.0.0 — Final Release

Course: CMSC 495 — Computer Capstone Project

University of Maryland Global Campus

---

## 1. Executive Summary

The Final Release of the AI-Powered IT Asset Management System introduces significant architectural, functional, security, testing, and deployment improvements over the Alpha Release.

Performance evaluation was conducted using actual application execution, authenticated REST API requests, automated testing, JaCoCo code coverage, AI recommendation generation, Docker deployment, and GitHub Actions CI/CD verification.

### Final Release Results

| Metric | Observed Result |
|---|---:|
| Automated tests | 138 |
| Successful tests | 138 |
| Failed tests | 0 |
| Test errors | 0 |
| Skipped tests | 0 |
| Automated test pass rate | 100% |
| Instruction coverage | 91.43% |
| Line coverage | 91.32% |
| Branch coverage | 70.83% |
| Complexity coverage | 71.37% |
| Method coverage | 92.03% |
| Class coverage | 100% |
| API benchmark requests | 60 |
| Successful API requests | 60 |
| API request success rate | 100% |
| Combined mean API latency | 3.619 ms |
| Mean observed AI latency | 5064.5 ms |
| Local application startup | 2.087 seconds |
| Local Docker build | 25.9 seconds |
| Final CI/CD execution | 1 minute 43 seconds |
| Container deployment | Successful |
| Application health | UP |

The results demonstrate successful functional integration, automated verification, responsive local API operations, AI recommendation generation, and reproducible containerized deployment.

These results represent controlled development and integration environments rather than production-scale service guarantees.

---

## 2. Evaluation Objectives

The performance evaluation was designed to:

1. Establish measurable application performance indicators.
2. Evaluate architectural refinements introduced after the Alpha Release.
3. Assess authenticated inventory retrieval performance.
4. Verify application reliability through automated testing.
5. Measure source-code coverage.
6. Evaluate AI recommendation response times.
7. Establish a methodology for evaluating AI recommendation quality.
8. Verify security control behavior.
9. Verify reproducible containerized deployment.
10. Identify remaining performance and scalability limitations.
11. Establish measurable objectives for future engineering improvements.

All reported measurements are distinguished from proposed acceptance targets and theoretical architectural benefits.

---

## 3. Alpha Release Baseline

The Alpha Release established the initial functional application and automated testing environment.

### 3.1 Automated Test Baseline

| Metric | Observed Result |
|---|---:|
| Total tests | 8 |
| Successful tests | 8 |
| Failed tests | 0 |
| Errors | 0 |
| Skipped tests | 0 |
| Pass rate | 100% |
| Build result | BUILD SUCCESS |
| Maven execution duration | 3.500 seconds |
| Java version | 25.0.2 |
| Spring Boot version | 4.0.9 |
| Test database | H2 |

The Maven execution duration represents the complete observed test command execution rather than individual API response latency.

### 3.2 Assignment Regression Baseline

Following peer review, an additional regression test was introduced to evaluate whether ordinary asset updates preserved existing employee assignments.

| Metric | Observed Result |
|---|---:|
| Total tests | 9 |
| Successful tests | 8 |
| Failed tests | 1 |
| Errors | 0 |
| Skipped tests | 0 |
| Build result | BUILD FAILURE |
| Maven execution duration | 2.978 seconds |

Expected employee assignment:

    John Smith

Actual employee assignment:

    null

The regression test demonstrated that an ordinary asset update could unintentionally remove an existing employee assignment.

This established a reproducible functional defect requiring remediation.

### 3.3 Corrective Engineering Actions

The Final Release introduced:

- Relational employee assignment management.
- Transactional asset operations.
- Assignment preservation during ordinary updates.
- Explicit assignment and unassignment operations.
- Employee deletion safeguards.
- Server-side request validation.
- Expanded automated regression testing.
- Authentication and authorization controls.
- Paginated inventory retrieval.

The corrected assignment behavior is included in the expanded automated verification suite.

### 3.4 Baseline Limitations

The Alpha Release did not establish independently verified measurements for:

- REST API latency.
- Concurrent request throughput.
- Large inventory scalability.
- Database query execution efficiency.
- Source-code coverage percentages.
- Live AI recommendation latency.
- AI recommendation quality.
- Containerized deployment reliability.

Consequently, percentage improvements in API latency, AI performance, and database throughput cannot be calculated against the Alpha Release.

---

## 4. Benchmark Environment

### 4.1 Local Development Environment

| Component | Configuration |
|---|---|
| Operating system | macOS |
| Processor architecture | ARM64 |
| Processor model | Not recorded |
| Physical memory | Not recorded |
| Java version | 25.0.2 |
| Spring Boot version | 4.0.9 |
| Application database | MySQL |
| Automated test database | H2 |
| Build tool | Maven Wrapper |
| Application port | 8080 |
| Development environment | Visual Studio Code |

The local benchmarks were conducted against a development application rather than a dedicated production performance-testing environment.

### 4.2 Containerized Environment

| Component | Configuration |
|---|---|
| Application runtime | Eclipse Temurin Java 25 |
| Database image | MySQL 8.4 |
| Container orchestration | Docker Compose |
| Docker Desktop | 4.99.0 |
| Docker Engine | 29.9.3 |
| Docker Compose | 5.7.0 |
| Application port | 8080 |
| Host binding | 127.0.0.1 |
| Database port | 3306 within private network |
| Database storage | Persistent Docker volume |
| Application image | ai-it-asset-management-system:1.0.0 |
| CPU allocation | Not recorded |
| Memory allocation | Not recorded |

AI functionality was disabled during the container deployment verification to avoid dependence on external AI provider availability.

Live AI recommendation generation was evaluated separately in the local development environment.

### 4.3 Continuous Integration Environment

| Component | Configuration |
|---|---|
| Platform | GitHub Actions |
| Runner | ubuntu-latest |
| Java distribution | Eclipse Temurin |
| Java version | 25 |
| Build tool | Maven Wrapper |
| Test reporting | Maven Surefire |
| Coverage tool | JaCoCo |
| Deployment tool | Docker Compose |
| Repository branch | main |
| Verified application commit | f85eb30 |
| Final workflow run | 7 |

Final verified workflow:

https://github.com/ilerioluwaa/ai-it-asset-management-system/actions/runs/45769400901

The verified commit identifies the application revision associated with the successful final integration and deployment workflow.

Subsequent documentation-only revisions may have different commit identifiers.

---

## 5. Environment Verification Commands

The following commands support reproducibility and future benchmark comparisons.

### Operating System

```bash
sw_vers
```

### Processor Architecture

```bash
uname -m
```

### Processor Information

```bash
sysctl -n machdep.cpu.brand_string
```

### Physical Memory

```bash
sysctl -n hw.memsize
```

The memory command reports the available physical memory in bytes.

### Java Environment

```bash
java -version
```

### Maven Environment

```bash
./mvnw --version
```

### Docker Environment

```bash
docker --version
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

Future benchmark executions should retain the command output alongside their measurement results.

---

## 6. Performance Indicators

The evaluation uses the following measurable indicators.

| Indicator | Measurement Unit |
|---|---|
| Minimum API latency | Milliseconds |
| Mean API latency | Milliseconds |
| Median API latency | Milliseconds |
| 95th percentile latency | Milliseconds |
| Maximum API latency | Milliseconds |
| Request success rate | Percentage |
| Sequential request rate | Requests per second |
| Concurrent throughput | Requests per second |
| Response payload size | Bytes |
| Automated test duration | Seconds |
| Automated test pass rate | Percentage |
| Instruction coverage | Percentage |
| Branch coverage | Percentage |
| Line coverage | Percentage |
| Method coverage | Percentage |
| Class coverage | Percentage |
| Application startup duration | Seconds |
| Docker image build duration | Seconds |
| AI recommendation latency | Milliseconds |
| AI numerical consistency | Percentage |
| AI recommendation quality | Project-defined score |
| Container memory consumption | MiB or GiB |
| Container CPU utilization | Percentage |

Not every indicator was measured during the final verification period.

Unmeasured indicators are identified explicitly rather than assigned estimated results.

---

## 7. Performance Acceptance Targets

The following targets represent project-defined engineering acceptance criteria.

They are not production service-level agreements.

| Metric | Target | Observed Result | Evaluation |
|---|---:|---:|---|
| Asset retrieval median | <= 200 ms | Not recorded | Not evaluated |
| Asset retrieval p95 | <= 500 ms | Not recorded | Not evaluated |
| Employee retrieval median | <= 200 ms | Not recorded | Not evaluated |
| Employee retrieval p95 | <= 500 ms | Not recorded | Not evaluated |
| Dashboard statistics p95 | <= 1000 ms | Not recorded | Not evaluated |
| Sequential request success | 100% | 100% | Met |
| Automated test pass rate | 100% | 100% | Met |
| Automated test errors | 0 | 0 | Met |
| Instruction coverage | >= 80% | 91.43% | Exceeded |
| Line coverage | >= 80% | 91.32% | Exceeded |
| Branch coverage | >= 70% | 70.83% | Exceeded |
| Local application startup | <= 60 seconds | 2.087 seconds | Met |
| AI recommendation latency | <= 60 seconds | Maximum observed: 5.250 seconds | Met in observed trials |
| AI numerical consistency | 100% | Not formally scored | Not evaluated |
| Unauthorized API access | Rejected | HTTP 401 | Met in deployment checks |
| Container health | UP | UP | Met |

Although the observed maximum API latencies were below the proposed median and percentile targets, the exact median and p95 measurements were not retained.

Coverage targets are evaluation objectives rather than automatically enforced Maven build thresholds.

---

## 8. Performance Testing Methodology

### 8.1 Executed API Benchmark

The final local API evaluation consisted of:

- Authenticated requests.
- Three application endpoints.
- Twenty sequential requests per endpoint.
- Sixty measured requests in total.
- HTTP response status verification.
- Minimum response-time measurement.
- Mean response-time measurement.
- Maximum response-time measurement.

All sixty measured requests returned HTTP 200.

The evaluation did not simulate multiple concurrent users.

The exact inventory record count and complete raw latency distribution were not retained in the consolidated results.

### 8.2 Recommended Expanded Testing Procedure

Future benchmark executions should:

1. Record the application version and Git commit.
2. Record hardware and software configuration.
3. Start the application and database.
4. Verify application health.
5. Prepare a representative fictional dataset.
6. Authenticate as an administrator.
7. Perform five preliminary warm-up requests.
8. Execute at least thirty measured requests per endpoint.
9. Record HTTP status and request duration.
10. Record response payload size.
11. Calculate summary statistics.
12. Compare measurements against acceptance targets.
13. Repeat the evaluation using different dataset sizes.
14. Document failures and environmental limitations.

### 8.3 Warm-Up Requests

Warm-up requests help reduce the influence of:

- Initial class loading.
- Database connection establishment.
- Runtime initialization.
- Initial application caching.

Warm-up measurements should be excluded from primary latency calculations.

The exact warm-up configuration of the completed benchmark was not retained.

### 8.4 Failure Reporting

Failed requests must remain included in the overall request success-rate calculation.

Successful response latency should be reported separately from failed request latency.

Authentication redirects must not be interpreted as successful API responses.

---

## 9. Benchmark Dataset Design

Future scalability evaluation should use fictional records in a dedicated test database.

### Proposed Dataset Configurations

| Dataset | Assets | Employees |
|---|---:|---:|
| Small | 10 | 4 |
| Medium | 100 | 25 |
| Large | 1000 | 100 |
| Extended | 10000 | 500 |

Each dataset should include representative:

- Asset categories.
- Employee departments.
- Available assets.
- Assigned assets.
- Maintenance assets.
- Retired assets.

All serial numbers and employee email addresses must remain unique.

Assigned assets must reference valid employees.

These configurations represent proposed scalability experiments.

The completed local benchmark did not establish measured performance across all four dataset sizes.

---

## 10. REST API Benchmark Endpoints

### Executed Benchmark Endpoints

| Endpoint | Operation | Requests |
|---|---|---:|
| GET /api/assets | Asset retrieval | 20 |
| GET /api/employees | Employee retrieval | 20 |
| GET /api/assets/statistics | Inventory statistics | 20 |
| Total | | 60 |

### Additional Recommended Endpoints

| Endpoint | Operation |
|---|---|
| GET /api/assets?page=0&size=10 | Paginated inventory retrieval |
| GET /api/assets?page=0&size=100 | Larger inventory page |
| GET /api/assets?status=AVAILABLE | Status-filtered retrieval |
| GET /api/assets?keyword=Laptop | Keyword search |
| GET /api/employees?page=0&size=10 | Employee directory retrieval |
| GET /api/employees/statistics | Employee statistics |
| GET /actuator/health | Application availability |

Application API benchmarks require an authenticated administrator session.

The health endpoint is publicly accessible and is used for deployment readiness verification.

---

## 11. Reproducible Browser Benchmark

The following JavaScript utility supports future performance verification.

It may be executed from the browser developer console while authenticated to the application.

The utility performs read-only GET requests.

```javascript
async function benchmarkEndpoint(
    endpoint,
    iterations = 20,
    warmup = 5
) {
    if (!Number.isInteger(iterations) || iterations < 1) {
        throw new Error("Iterations must be a positive integer.");
    }

    if (!Number.isInteger(warmup) || warmup < 0) {
        throw new Error("Warm-up count must be a non-negative integer.");
    }

    for (let i = 0; i < warmup; i++) {
        const response = await fetch(endpoint, {
            credentials: "same-origin",
            cache: "no-store"
        });

        await response.arrayBuffer();

        if (!response.ok || response.redirected) {
            throw new Error(
                "Warm-up request failed: " + response.status
            );
        }
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
                (
                    contentType.includes("application/json") ||
                    contentType.includes("+json")
                );

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
            : Number(value.toFixed(3));

    const result = {
        endpoint,

        measuredRequests: iterations,

        successfulRequests,

        failedRequests:
            iterations - successfulRequests,

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

        statuses,

        rawSuccessfulLatenciesMs: measurements
    };

    console.table({
        endpoint: result.endpoint,
        requests: result.measuredRequests,
        successful: result.successfulRequests,
        failed: result.failedRequests,
        minimumMs: result.minimumMs,
        meanMs: result.meanMs,
        medianMs: result.medianMs,
        p95Ms: result.p95Ms,
        maximumMs: result.maximumMs,
        successRatePercent: result.successRatePercent
    });

    return result;
}
```

### Example Benchmark Execution

```javascript
await benchmarkEndpoint("/api/assets", 20);
```

```javascript
await benchmarkEndpoint("/api/employees", 20);
```

```javascript
await benchmarkEndpoint("/api/assets/statistics", 20);
```

### Expanded Benchmark Execution

```javascript
await benchmarkEndpoint(
    "/api/assets?page=0&size=100",
    30
);
```

```javascript
await benchmarkEndpoint(
    "/api/employees/statistics",
    30
);
```

### Combined Benchmark

```javascript
const endpoints = [
    "/api/assets",
    "/api/employees",
    "/api/assets/statistics"
];

const results = [];

for (const endpoint of endpoints) {
    results.push(
        await benchmarkEndpoint(endpoint, 20)
    );
}

console.table(results);
```

This utility is provided for reproducibility and expanded future measurements.

It should not be interpreted as evidence that every metric it calculates was recorded during the original benchmark.

Browser measurements include client-side request handling and response body retrieval.

Sequential request rates must not be interpreted as maximum concurrent server throughput.

---

## 12. Performance Calculation Methods

### 12.1 Mean Response Time

    Mean =
        Sum of Successful Response Times
        / Number of Successful Responses

### 12.2 Median Response Time

The median represents the middle value of the sorted response-time measurements.

For an even number of measurements, the median is the average of the two middle values.

### 12.3 95th Percentile Response Time

The 95th percentile represents the response time at or below which approximately 95% of measured successful requests completed.

The nearest-rank calculation is:

    Rank = Ceiling(0.95 × Number of Measurements)

### 12.4 Request Success Rate

    Success Rate =
        Successful Requests / Total Requests × 100

### 12.5 Sequential Request Rate

    Request Rate =
        Total Requests / Total Elapsed Seconds

### 12.6 Performance Improvement

Where comparable baseline measurements exist:

    Improvement Percentage =
        (Baseline Latency - Revised Latency)
        / Baseline Latency × 100

This calculation requires equivalent measurement conditions.

No Alpha Release API latency measurements were available for a valid before-and-after latency comparison.

---

## 13. Observed API Performance Results

### 13.1 Local Benchmark Results

| Endpoint | Requests | Minimum ms | Mean ms | Maximum ms | Success |
|---|---:|---:|---:|---:|---:|
| /api/assets | 20 | 3.355 | 3.957 | 5.030 | 100% |
| /api/employees | 20 | 3.183 | 3.808 | 4.871 | 100% |
| /api/assets/statistics | 20 | 2.437 | 3.091 | 7.494 | 100% |

### 13.2 Combined API Results

| Metric | Observed Result |
|---|---:|
| Total measured requests | 60 |
| Successful requests | 60 |
| Failed requests | 0 |
| HTTP 200 responses | 60 |
| Request success rate | 100% |
| Overall minimum latency | 2.437 ms |
| Combined mean latency | 3.619 ms |
| Overall maximum latency | 7.494 ms |
| Median latency | Not recorded |
| 95th percentile latency | Not recorded |
| Response payload size | Not recorded |
| Concurrent throughput | Not measured |

### 13.3 Combined Mean Calculation

Because each endpoint received the same number of measured requests, the combined mean can be calculated using the three endpoint means.

    Combined Mean =
        (3.957 + 3.808 + 3.091) / 3

    Combined Mean =
        3.618666... milliseconds

    Rounded Combined Mean =
        3.619 milliseconds

The combined mean is calculated from the reported endpoint means and is therefore subject to their original rounding precision.

### 13.4 Request Success Calculation

    Success Rate = 60 / 60 × 100

    Success Rate = 100%

### 13.5 Mean Response-Time Comparison

Each block represents approximately 0.25 milliseconds.

    Asset Retrieval       3.957 ms | ################
    Employee Retrieval    3.808 ms | ###############
    Asset Statistics      3.091 ms | ############

### 13.6 Interpretation

Asset retrieval demonstrated a mean response time of 3.957 milliseconds.

Employee retrieval demonstrated a mean response time of 3.808 milliseconds.

Asset statistics demonstrated a mean response time of 3.091 milliseconds.

All measured requests completed successfully.

The statistics endpoint demonstrated the lowest mean latency but also the highest individual observed maximum latency.

This illustrates why average latency alone is insufficient for comprehensive performance evaluation.

The measurements support responsive local application behavior under the tested workload.

They do not establish production capacity, concurrent throughput, or performance under large inventory workloads.

---

## 14. Pagination and Scalability Evaluation

The Alpha Release retrieved the complete asset inventory through its collection endpoint.

The Final Release introduces paginated asset and employee retrieval.

### 14.1 Architectural Benefits

Pagination is intended to provide:

- Controlled record counts per request.
- Smaller response payloads.
- Reduced frontend rendering requirements.
- Improved inventory navigation.
- Reduced unnecessary record retrieval.
- Better support for growing inventory datasets.

### 14.2 Illustrative Record Retrieval Comparison

| Total Assets | Alpha Full Retrieval | Final Page Size 10 | Record Reduction |
|---|---:|---:|---:|
| 10 | 10 | 10 | 0% |
| 100 | 100 | 10 | 90% |
| 1000 | 1000 | 10 | 99% |
| 10000 | 10000 | 10 | 99.9% |

For an inventory containing 1000 records:

    Record Reduction =
        (1000 - 10) / 1000 × 100

    Record Reduction = 99%

This represents a theoretical reduction in the number of asset records returned by a single request.

It does not establish an equivalent reduction in:

- API latency.
- Database workload.
- Memory consumption.
- Network transfer size.
- CPU utilization.

Pagination may require additional count queries and database processing.

### 14.3 Scalability Verification Status

| Dataset | Verification Status |
|---|---|
| Local development inventory | Sequential benchmark completed |
| Controlled 10-asset dataset | Not independently benchmarked |
| Controlled 100-asset dataset | Not independently benchmarked |
| Controlled 1000-asset dataset | Not independently benchmarked |
| Controlled 10000-asset dataset | Not independently benchmarked |
| Concurrent user workload | Not evaluated |

Pagination provides an architectural foundation for scalability, but its performance under increasing workload remains subject to further testing.

---

## 15. Database Performance Evaluation

The revised database architecture introduces:

- Unique asset serial number constraints.
- Unique employee email constraints.
- Asset status indexes.
- Asset category indexes.
- Employee department indexes.
- Employee foreign-key relationships.
- Database connection pooling.
- Transactional inventory operations.
- Aggregate statistics queries.
- Paginated database retrieval.

These mechanisms support data integrity and controlled inventory access.

Their individual performance contributions were not isolated through dedicated comparative profiling.

### 15.1 Query Execution Plan Inspection

MySQL execution plans may be inspected using EXPLAIN.

Example asset status query:

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

The actual table and column names should be confirmed against the deployed database schema before execution.

Review the following execution-plan characteristics:

- Selected indexes.
- Estimated rows examined.
- Query access type.
- Filtering behavior.
- Additional sorting requirements.

Index availability does not guarantee index selection for every query.

### 15.2 Database Performance Limitations

Additional evaluation is required for:

- Case-insensitive wildcard searches.
- Employee assignment count queries.
- Large-offset pagination.
- Concurrent equipment allocation.
- Aggregate statistics query costs.
- Connection pool configuration.
- Database locking behavior.
- Schema migration from previous application versions.

These limitations are documented in the technical debt register.

See [TECHNICAL_DEBT.md](TECHNICAL_DEBT.md).

---

## 16. Automated Test Evaluation

### 16.1 Test Execution Commands

Execute the complete automated test suite:

```bash
./mvnw clean test
```

Execute the complete build and coverage lifecycle:

```bash
./mvnw clean verify
```

### 16.2 Automated Test Scope

The automated test suite includes the following test classes:

| Test Class | Primary Responsibility |
|---|---|
| AssetControllerTest | Asset operations, validation, and assignment behavior |
| EmployeeControllerTest | Employee operations and deletion safeguards |
| AiRecommendationServiceTest | AI integration and provider response handling |
| SecurityTest | Authentication, authorization, and security controls |
| AssetmanagementApplicationTests | Application context and integration verification |

### 16.3 Final Automated Test Results

| Metric | Observed Result |
|---|---:|
| Total tests | 138 |
| Passed tests | 138 |
| Failed tests | 0 |
| Errors | 0 |
| Skipped tests | 0 |
| Pass rate | 100% |
| Reported test duration | 9.62 seconds |
| Build result | Successful |

The reported test duration is distinct from the complete GitHub Actions workflow duration.

Individual test-class counts are available in the generated Surefire reports.

### 16.4 Automated Test Pass Rate

    Passed Tests =
        Total Tests - Failures - Errors - Skipped Tests

    Passed Tests =
        138 - 0 - 0 - 0

    Passed Tests = 138

    Pass Rate =
        138 / 138 × 100

    Pass Rate = 100%

### 16.5 Alpha and Final Release Comparison

| Metric | Alpha Release | Final Release |
|---|---:|---:|
| Automated tests | 8 | 138 |
| Passed tests | 8 | 138 |
| Failed tests | 0 | 0 |
| Errors | 0 | 0 |
| Skipped tests | 0 | 0 |
| Pass rate | 100% | 100% |
| Assignment regression | Failed in subsequent test | Corrective verification included |
| Code coverage | Not measured | Measured |
| AI integration testing | Not established | Included |
| Security testing | Not established | Included |
| Container deployment verification | Not established | Successful |

### 16.6 Test Suite Growth

    Additional Tests =
        Final Test Count - Alpha Test Count

    Additional Tests =
        138 - 8

    Additional Tests = 130

    Test Count Increase =
        (138 - 8) / 8 × 100

    Test Count Increase = 1625%

    Test Suite Growth Factor =
        138 / 8

    Test Suite Growth Factor = 17.25

The Final Release contains 17.25 times as many automated tests as the Alpha Release baseline.

The increased test count provides broader verification opportunities but does not independently establish proportional improvements in software quality.

Test relevance, assertion quality, defect detection, and coverage must also be considered.

---

## 17. Code Coverage Evaluation

JaCoCo is integrated into the Maven build lifecycle.

### 17.1 Generate Coverage Reports

```bash
./mvnw clean verify
```

Open the HTML report on macOS:

```bash
open target/site/jacoco/index.html
```

### 17.2 Coverage Report Locations

HTML report:

    target/site/jacoco/index.html

XML report:

    target/site/jacoco/jacoco.xml

Automated test reports:

    target/surefire-reports/

### 17.3 Final Coverage Measurements

| Coverage Metric | Proposed Target | Actual Result | Evaluation |
|---|---:|---:|---|
| Instruction coverage | 80% | 91.43% | Exceeded |
| Branch coverage | 70% | 70.83% | Exceeded |
| Line coverage | 80% | 91.32% | Exceeded |
| Complexity coverage | Record result | 71.37% | Recorded |
| Method coverage | Record result | 92.03% | Recorded |
| Class coverage | Record result | 100% | Recorded |

### 17.4 Coverage Target Comparison

| Metric | Difference Above Target |
|---|---:|
| Instruction coverage | 11.43 percentage points |
| Branch coverage | 0.83 percentage points |
| Line coverage | 11.32 percentage points |

### 17.5 Coverage Visualization

Each bar represents approximately 100% of its respective coverage metric.

    Instruction  91.43% | ##################..
    Branch       70.83% | ##############......
    Line         91.32% | ##################..
    Complexity   71.37% | ##############......
    Method       92.03% | ##################..
    Class       100.00% | ####################

### 17.6 Coverage Calculation

    Coverage Percentage =
        Covered Items / Total Items × 100

### 17.7 Coverage Interpretation

The application exceeded all three explicitly defined numerical coverage targets.

Instruction and line coverage exceeded 90%.

Class coverage reached 100%, indicating that every class included in the coverage report received some execution coverage.

Class coverage does not indicate that every method, branch, or behavior within those classes was fully tested.

Branch coverage reached 70.83%, leaving approximately 29.17% of reported branches uncovered.

Future testing should prioritize:

- Additional exceptional execution paths.
- Complex conditional logic.
- Database integrity edge cases.
- AI provider failure scenarios.
- Concurrent assignment behavior.
- Security-related error handling.

High code coverage does not establish the absence of software defects.

---

## 18. Security Verification

Security evaluation focuses on whether implemented controls behave as intended.

### 18.1 Automated Security Evaluation

The automated verification suite includes security-related scenarios covering:

- Administrator authentication.
- Unauthorized API access.
- Authorization controls.
- CSRF protection.
- Session management.
- Logout behavior.
- Protected application resources.
- Invalid authentication attempts.

The complete automated suite passed without failures or errors.

### 18.2 Deployment Security Verification

The final GitHub Actions deployment performed the following endpoint checks.

| Security Scenario | Expected Result | Observed Result |
|---|---|---|
| Unauthenticated asset API request | HTTP 401 | HTTP 401 |
| Unauthenticated employee API request | HTTP 401 | HTTP 401 |
| Unauthenticated AI API request | HTTP 401 | HTTP 401 |
| Public login page | HTTP 200 | HTTP 200 |
| Public health endpoint | UP | UP |

### 18.3 Local Browser Verification

Local containerized application verification included:

- Successful login page rendering.
- Successful administrator authentication.
- Successful dashboard access.
- Successful asset registration.
- Updated inventory statistics following registration.

### 18.4 Additional Data Integrity Controls

The application includes controls intended to reject:

- Duplicate asset serial numbers.
- Duplicate employee email addresses.
- Invalid request data.
- Invalid assignment operations.
- Deletion of assigned assets.
- Deletion of employees with existing asset assignments.

These behaviors are addressed through application validation, database constraints, and automated tests.

Individual test assertions may be reviewed in the source code and Surefire reports.

### 18.5 Security Evaluation Limitations

The completed evaluation does not constitute:

- A comprehensive penetration test.
- Independent security certification.
- Production vulnerability assessment.
- Distributed denial-of-service testing.
- Enterprise identity management validation.
- Formal compliance certification.

Additional security hardening remains necessary before unrestricted public deployment.

---

## 19. Application Startup Performance

Application startup duration was recorded from the local Spring Boot application logs.

### 19.1 Observed Startup Result

| Environment | Observed Result |
|---|---|
| Local Java and MySQL startup | 2.087 seconds |
| Local Docker application startup | Successful; exact duration not recorded |
| Local Docker readiness | Health UP; exact duration not recorded |
| GitHub Actions deployment job | 64 seconds |
| Complete GitHub Actions workflow | 103 seconds |

The deployment job duration includes multiple deployment activities and must not be interpreted as application startup time alone.

### 19.2 Startup Acceptance Target

    Proposed Maximum Startup Duration = 60 seconds

    Observed Local Startup Duration = 2.087 seconds

    Evaluation = Target Met

### 19.3 Reproduce Local Startup Measurement

Load the local environment configuration:

```bash
set -a
source .env
set +a
```

Start the application:

```bash
./mvnw spring-boot:run
```

Inspect the Spring Boot startup log.

Record the reported application startup duration.

### 19.4 Reproduce Container Startup Verification

```bash
AI_ENABLED=false docker compose up -d --build
```

Inspect container status:

```bash
docker compose ps
```

Inspect application and database logs:

```bash
docker compose logs
```

Verify application health:

```bash
curl -i http://localhost:8080/actuator/health
```

The expected healthy response includes:

```json
{
    "status": "UP"
}
```

Additional health-group information may also be present.

Application startup, container readiness, database initialization, and Docker image construction should be measured separately.

---

## 20. Container Performance and Resource Evaluation

### 20.1 Local Docker Build Results

| Metric | Observed Result |
|---|---|
| Docker Compose configuration | Valid |
| Docker image build | Successful |
| Docker image build duration | 25.9 seconds |
| Application image tag | ai-it-asset-management-system:1.0.0 |
| Observed image ID | 022a87c05bf3 |
| Reported image disk usage | 649 MB |
| Reported image content size | 178 MB |
| Database container | Healthy |
| Application container | Running |
| Application health | UP |

Docker-reported disk usage and content size are distinct measurements.

They should not be treated as equivalent to runtime memory consumption.

### 20.2 Local Functional Deployment Verification

The containerized application was successfully accessed through:

    http://localhost:8080

A test asset was registered through the browser interface.

| Field | Observed Value |
|---|---|
| Asset name | Docker Test Laptop |
| Category | Laptop |
| Serial number | DOCKER-001 |
| Status | Available |
| Asset ID | 1 |

The dashboard reflected the newly registered asset.

This demonstrated functional communication between:

- Browser interface.
- Spring Boot application container.
- MySQL database container.
- Application persistence layer.

### 20.3 Restart Verification

The Docker Compose services were restarted successfully.

Following the restart:

- The application container returned to a running state.
- The database container returned to a healthy state.
- The application health endpoint returned UP.

The database uses a persistent Docker volume.

Following the Docker Compose restart, the original asset (ID 1, serial number DOCKER-001) was successfully retrieved through the browser interface, confirming database persistence across the tested container restart.

Evidence: Final_Docker_Persistence_Verification.png

### 20.4 Resource Consumption Measurements

| Scenario | CPU Usage | Application Memory | Database Memory |
|---|---|---|---|
| Idle | Not measured | Not measured | Not measured |
| Inventory retrieval | Not measured | Not measured | Not measured |
| Dashboard retrieval | Not measured | Not measured | Not measured |
| AI generation | Not measured | Not measured | Not measured |

Future resource evaluation may use:

```bash
docker stats --no-stream
```

Repeated observations should be collected under controlled workloads.

A single resource snapshot is insufficient to establish sustained or peak resource usage.

---

## 21. AI Recommendation Performance

The AI recommendation service records the response time associated with recommendation generation.

### 21.1 Factors Affecting AI Latency

AI recommendation latency may depend on:

- Selected AI model.
- Provider infrastructure.
- Network conditions.
- Prompt processing.
- Response generation length.
- Available computational resources.
- Provider request limits.
- Model initialization.

### 21.2 Observed Live AI Measurements

Two successful recommendation-generation events were recorded in the local application logs.

| Trial | Response Time ms | Response Time seconds | Result |
|---|---:|---:|---|
| 1 | 4879 | 4.879 | Recommendation generated |
| 2 | 5250 | 5.250 | Recommendation generated |

The confirmed local configuration uses Ollama with the llama3.2:3b model at http://localhost:11434/v1. The model identifier was not independently recorded alongside the earlier latency measurements.

### 21.3 AI Latency Summary

| Metric | Observed Result |
|---|---:|
| Recorded successful generations | 2 |
| Minimum latency | 4879 ms |
| Mean latency | 5064.5 ms |
| Median latency | 5064.5 ms |
| Maximum latency | 5250 ms |
| Mean latency in seconds | 5.0645 seconds |
| Observed latency range | 371 ms |
| Overall provider failure rate | Not established |
| Production reliability | Not established |

### 21.4 Mean AI Response-Time Calculation

    Mean AI Latency =
        (4879 + 5250) / 2

    Mean AI Latency =
        10129 / 2

    Mean AI Latency =
        5064.5 milliseconds

    Mean AI Latency =
        5.0645 seconds

### 21.5 AI Latency Target Evaluation

    Proposed Maximum Latency = 60 seconds

    Maximum Observed Latency = 5.250 seconds

    Evaluation = Target Met in Recorded Trials

Both recorded recommendation generations completed within the proposed sixty-second response-time target.

### 21.6 AI Performance Interpretation

The observed measurements demonstrate successful AI recommendation generation in the local application.

AI recommendation generation requires additional processing compared with ordinary inventory retrieval.

The observed AI latency is therefore substantially greater than the measured REST API retrieval latency.

These operations perform different computational tasks and should not be interpreted as equivalent workloads.

Only two live latency measurements were retained.

This is sufficient to demonstrate observed functionality and preliminary latency behavior, but insufficient to establish:

- Statistically robust latency distributions.
- Production provider availability.
- Long-term AI reliability.
- Concurrent AI request capacity.
- Comprehensive AI recommendation accuracy.

### 21.7 Recommended Expanded AI Benchmark

Future AI evaluation should:

1. Configure a compatible AI provider.
2. Verify the selected model.
3. Prepare a known inventory dataset.
4. Record the inventory statistics.
5. Generate an AI recommendation.
6. Record the model identifier.
7. Record the reported response time.
8. Retain the recommendation text.
9. Review numerical accuracy.
10. Review relevance and actionability.
11. Record failed requests.
12. Repeat across representative inventory scenarios.

At least five live recommendation requests are recommended for an expanded initial evaluation.

Larger samples are required for statistically meaningful reliability conclusions.

---

## 22. AI Recommendation Quality Evaluation

AI response speed alone does not establish recommendation quality.

Recommendations must also be evaluated against the inventory information supplied to the AI service.

### 22.1 Evaluation Criteria

| Criterion | Description |
|---|---|
| Numerical consistency | Reported quantities match source statistics |
| Relevance | Recommendations address inventory conditions |
| Actionability | Suggestions provide practical operational guidance |
| Grounding | Claims are supported by available information |
| Privacy | Individual employee information is not unnecessarily disclosed |
| Human oversight | Recommendations remain advisory |

### 22.2 Scoring Method

| Score | Meaning |
|---|---|
| 0 | Criterion not satisfied |
| 1 | Criterion partially satisfied |
| 2 | Criterion fully satisfied |

Maximum score per recommendation:

    6 Criteria × 2 Points = 12 Points

Quality percentage:

    Quality Percentage =
        Observed Score / 12 × 100

This is a project-defined human evaluation rubric.

It is not a standardized AI accuracy benchmark.

### 22.3 Numerical Consistency

    Numerical Consistency =
        Correct Numerical Claims
        / Total Verifiable Numerical Claims × 100

If a recommendation contains no verifiable numerical claims, numerical consistency should be recorded as Not Applicable.

A recommendation must not receive a perfect numerical consistency score solely because it contains no numerical claims.

### 22.4 Scenario A: Balanced Inventory

| Metric | Value |
|---|---:|
| Total assets | 20 |
| Available | 6 |
| Assigned | 10 |
| Maintenance | 3 |
| Retired | 1 |
| Employees | 10 |

Expected evaluation focus:

- Recognize available inventory.
- Consider maintenance requirements.
- Avoid unsupported procurement conclusions.

### 22.5 Scenario B: Limited Availability

| Metric | Value |
|---|---:|
| Total assets | 20 |
| Available | 1 |
| Assigned | 16 |
| Maintenance | 2 |
| Retired | 1 |
| Employees | 18 |

Expected evaluation focus:

- Recognize limited available equipment.
- Consider potential allocation constraints.
- Avoid claiming a confirmed shortage without evidence of individual equipment requirements.

### 22.6 Scenario C: Elevated Maintenance

| Metric | Value |
|---|---:|
| Total assets | 20 |
| Available | 3 |
| Assigned | 8 |
| Maintenance | 8 |
| Retired | 1 |
| Employees | 10 |

Expected evaluation focus:

- Recognize the elevated maintenance proportion.
- Recommend reviewing maintenance priorities.
- Avoid inventing specific equipment failures.

### 22.7 Scenario D: Empty Inventory

| Metric | Value |
|---|---:|
| Total assets | 0 |
| Available | 0 |
| Assigned | 0 |
| Maintenance | 0 |
| Retired | 0 |
| Employees | 0 |

Expected evaluation focus:

- Recognize insufficient operational data.
- Recommend establishing inventory records.
- Avoid fabricating assets or employees.

### 22.8 Formal AI Quality Evaluation Status

| Scenario | Numerical Consistency | Quality Score |
|---|---|---|
| Balanced inventory | Not formally scored | Not recorded |
| Limited availability | Not formally scored | Not recorded |
| Elevated maintenance | Not formally scored | Not recorded |
| Empty inventory | Not formally scored | Not recorded |

The scenario datasets are proposed evaluation cases rather than completed live experiments.

No unsupported AI accuracy percentage is assigned.

### 22.9 Responsible AI Design

The application is designed to:

- Generate recommendations using aggregate inventory information.
- Limit unnecessary disclosure of employee information.
- Preserve human oversight.
- Present recommendations as advisory information.
- Avoid autonomous inventory modifications.
- Handle AI service failures without requiring autonomous corrective actions.

Automated AI integration testing and successful live recommendation generation support functional verification.

They do not replace independent evaluation of recommendation accuracy, fairness, and usefulness.

---

## 23. Deployment Reliability Evaluation

The Final Release includes automated continuous integration and containerized deployment verification through GitHub Actions.

### 23.1 Final Workflow Information

| Metric | Observed Result |
|---|---|
| Workflow | Spring Boot CI/CD |
| Workflow run | 7 |
| Branch | main |
| Application commit | f85eb30 |
| Overall workflow status | Successful |
| Total workflow duration | 1 minute 43 seconds |
| Build, test, and coverage job | 35 seconds |
| Docker deployment verification job | 1 minute 4 seconds |

Workflow evidence:

https://github.com/ilerioluwaa/ai-it-asset-management-system/actions/runs/45769400901

### 23.2 Deployment Verification Results

| Criterion | Expected Result | Observed Result |
|---|---|---|
| Maven build | Successful | Successful |
| Automated tests | Successful | 138 passed |
| Coverage report | Generated | Generated |
| Docker image build | Successful | Successful |
| MySQL startup | Healthy | Healthy |
| Spring Boot startup | Successful | Successful |
| Health endpoint | UP | UP |
| Login page | HTTP 200 | HTTP 200 |
| Protected asset API | HTTP 401 | HTTP 401 |
| Protected employee API | HTTP 401 | HTTP 401 |
| Protected AI API | HTTP 401 | HTTP 401 |
| Deployment artifacts | Uploaded | Uploaded |

### 23.3 Generated CI/CD Artifacts

| Artifact | Reported Size |
|---|---:|
| application-package | 54.1 MB |
| automated-test-reports | 25.7 KB |
| deployment-evidence | 3.69 KB |
| jacoco-coverage-report | 292 KB |

These artifacts provide supporting evidence for:

- Application packaging.
- Automated test execution.
- Source-code coverage.
- Container deployment verification.
- Application health checks.
- Protected endpoint verification.

### 23.4 Continuous Integration History

The repository recorded seven successful GitHub Actions workflow runs through the verified Final Release workflow.

Earlier workflow runs primarily established continuous integration and automated testing.

The final workflow additionally established automated container deployment verification.

Consequently, seven successful workflow runs must not be interpreted as seven independently verified container deployments.

### 23.5 Deployment Success Rate

    Deployment Success Rate =
        Successful Deployment Runs
        / Total Completed Deployment Attempts × 100

The verified final workflow successfully completed its deployment job.

A broader deployment reliability percentage requires additional comparable deployment attempts.

### 23.6 CI/CD Limitations

The deployment workflow verifies a temporary MySQL-backed Docker environment.

It does not establish:

- Permanent public hosting.
- Production uptime.
- Production infrastructure availability.
- Multi-region deployment.
- Automatic production rollback.
- Production disaster recovery.
- Live AI provider availability inside the CI environment.

AI functionality was disabled during automated container deployment verification.

Two nonfatal GitHub Actions runtime deprecation warnings were observed.

Updating the affected GitHub Actions dependencies is recorded as a maintenance improvement.

---

## 24. Software Quality Evaluation

The application evaluation is organized around software quality characteristics commonly associated with ISO/IEC 25010.

### 24.1 Quality Characteristics

| Quality Characteristic | Project Evaluation | Evidence |
|---|---|---|
| Functional suitability | Asset, employee, assignment, and AI functionality | Automated tests and runtime verification |
| Performance efficiency | Responsive local API operations | 60-request benchmark |
| Compatibility | Java and containerized deployment | Local and CI deployment |
| Interaction capability | Browser-based administration | Manual browser verification |
| Reliability | Automated verification and service recovery | 138 passing tests and restart health |
| Security | Authentication and protected resources | Security tests and HTTP 401 checks |
| Maintainability | Layered architecture and documented technical debt | Source code and documentation |
| Flexibility | Environment-based configuration | Native and Docker execution |
| Safety | Advisory AI and human oversight | Application design |

### 24.2 Quality Evaluation Interpretation

The project demonstrates measurable evidence across multiple software quality characteristics.

The strongest quantitative evidence includes:

- Automated test pass rate.
- Source-code coverage.
- Sequential API response latency.
- AI recommendation response time.
- Application startup duration.
- Successful CI/CD execution.
- Containerized deployment verification.

Additional evidence is required for:

- Formal usability assessment.
- Large-scale performance efficiency.
- Production availability.
- Comprehensive security assessment.
- AI recommendation quality.
- Long-term operational reliability.

ISO/IEC 25010 is used as a conceptual evaluation reference.

The application has not undergone formal ISO certification or independent conformity assessment.

---

## 25. Evidence Collection and Reproducibility

The following evidence supports the Final Release evaluation.

### 25.1 Automated Testing Evidence

- Maven build output.
- Alpha Release test results.
- Assignment regression failure evidence.
- Final automated test reports.
- Surefire XML reports.
- Final GitHub Actions test summary.

### 25.2 Coverage Evidence

- JaCoCo HTML report.
- JaCoCo XML report.
- GitHub Actions coverage summary.
- Downloaded coverage artifact.

### 25.3 Performance Evidence

- Local API benchmark results.
- HTTP response status measurements.
- Minimum API response times.
- Mean API response times.
- Maximum API response times.
- Spring Boot startup logs.
- AI recommendation generation latency logs.

### 25.4 Deployment Evidence

- Docker Compose configuration verification.
- Docker image build output.
- Container status output.
- MySQL health verification.
- Application health responses.
- Browser login verification.
- Docker-based asset registration.
- Post-restart health verification.
- GitHub Actions deployment summary.
- Downloaded deployment evidence artifact.

### 25.5 Repository Evidence

Repository:

https://github.com/ilerioluwaa/ai-it-asset-management-system

Continuous integration history:

https://github.com/ilerioluwaa/ai-it-asset-management-system/actions

Verified Final Release workflow:

https://github.com/ilerioluwaa/ai-it-asset-management-system/actions/runs/45769400901

Verified application commit:

    f85eb30

### 25.6 Recommended Evidence Organization

    Final_Evidence/
        Testing/
        Coverage/
        Performance/
        Security/
        AI/
        Docker/
        CI-CD/

This structure is recommended for organizing retained submission evidence.

The actual repository and local evidence directories may use different organization.

### 25.7 Evidence Protection

Public evidence must not contain:

- Database passwords.
- Administrator passwords.
- AI API keys.
- Authentication cookies.
- Session identifiers.
- Confidential employee information.
- Unreviewed database backups.
- Private environment configuration.

Generated evidence should be reviewed before publication.

---

## 26. Final Performance Summary

| Evaluation Area | Result | Verification Status |
|---|---|---|
| Automated test execution | 138 passed | Verified |
| Automated test pass rate | 100% | Verified |
| Assignment regression remediation | Corrective verification included | Verified through automated suite |
| Instruction coverage | 91.43% | Verified |
| Line coverage | 91.32% | Verified |
| Branch coverage | 70.83% | Verified |
| Asset retrieval benchmarking | 3.957 ms mean | Verified |
| Employee retrieval benchmarking | 3.808 ms mean | Verified |
| Dashboard benchmarking | 3.091 ms mean | Verified |
| Combined API latency | 3.619 ms mean | Calculated |
| API request success | 60/60 | Verified |
| Large dataset scalability | Pagination implemented | Additional benchmarking required |
| Security verification | Automated and deployment checks passed | Verified within tested scope |
| Application startup | 2.087 seconds | Verified |
| AI response latency | 5064.5 ms mean | Preliminary verification |
| AI output quality | Evaluation methodology established | Formal scoring outstanding |
| Docker image build | 25.9 seconds | Verified |
| Docker application deployment | Successful | Verified |
| Docker restart recovery | Health UP | Verified |
| Post-restart record persistence | Original asset successfully retrieved | Verified |
| GitHub Actions verification | Successful | Verified |
| CI/CD duration | 103 seconds | Verified |
| Concurrent load testing | Not measured | Outstanding |
| Production availability | Not measured | Outstanding |

The evaluation distinguishes completed verification activities from architectural capabilities and outstanding performance experiments.

---

## 27. Performance Limitations and Future Work

### 27.1 Current Evaluation Limitations

The completed evaluation has the following limitations:

- Local hardware may not represent production infrastructure.
- Exact processor and memory specifications were not retained.
- H2 tests do not fully reproduce MySQL behavior.
- Sequential benchmarks do not establish concurrent capacity.
- The exact benchmark dataset size was not retained.
- Raw API latency distributions were not retained.
- Median and percentile API latency were not recorded.
- Response payload sizes were not recorded.
- Database query execution plans were not independently profiled.
- Container CPU and memory consumption were not measured.
- Only two live AI latency measurements were retained.
- Formal AI recommendation quality scoring remains outstanding.
- Code coverage does not guarantee defect-free software.
- Container deployment verification does not establish production uptime.
- Persistence was verified across a container restart; disaster recovery and database restoration remain untested.
- Comprehensive stress and endurance testing remain outstanding.

### 27.2 Future Performance Evaluation Roadmap

| Priority | Improvement | Proposed Verification |
|---|---|---|
| High | Concurrent asset assignment testing | Simultaneous assignment requests and data integrity assertions |
| High | Large inventory benchmarking | Controlled datasets containing 1000 and 10000 assets |
| High | AI quality evaluation | Scored recommendation scenarios |
| High | Production security hardening | Security assessment and configuration review |
| Medium | Concurrent API load testing | Multiple virtual users with latency and throughput reporting |
| Medium | Database query optimization | MySQL EXPLAIN and query profiling |
| Medium | Employee query optimization | Query-count and response-time comparison |
| Medium | Container resource profiling | Repeated CPU and memory measurements |
| Medium | Database backup and recovery verification | Restore database backup and verify inventory integrity |
| Medium | Expanded branch coverage | Additional exception and boundary tests |
| Medium | Stable pagination response format | Explicit response DTO verification |
| Medium | Database migration management | Versioned schema migration testing |
| Low | Endurance testing | Extended controlled workload execution |
| Low | Production observability | Application metrics, logging, and alerting |
| Low | Automated dependency maintenance | Updated CI/CD actions and dependency checks |

These priorities are proposed engineering recommendations rather than completed implementation commitments.

See [TECHNICAL_DEBT.md](TECHNICAL_DEBT.md) for the broader technical debt register.

---

## 28. Technical References

The following references provide supporting guidance for software quality evaluation, automated testing, performance measurement, database management, and containerized deployment.

### ISO/IEC 25010

Software product quality model.

https://www.iso.org/standard/78176.html

### JaCoCo

Java code coverage measurement and reporting.

https://www.jacoco.org/jacoco/trunk/doc/

### Apache Maven Surefire

Automated Java test execution and reporting.

https://maven.apache.org/surefire/maven-surefire-plugin/

### Spring Boot

Application configuration, testing, and operational monitoring.

https://docs.spring.io/spring-boot/

### Spring Security

Authentication, authorization, and application security.

https://docs.spring.io/spring-security/reference/

### MySQL

Database administration, indexing, and query optimization.

https://dev.mysql.com/doc/

### Docker

Container construction, deployment, and resource management.

https://docs.docker.com/

### GitHub Actions

Continuous integration and automated deployment workflows.

https://docs.github.com/actions

### Apache JMeter

Recommended tool for future concurrent performance and load testing.

https://jmeter.apache.org/

### Grafana k6

Recommended tool for future scripted load and performance testing.

https://grafana.com/docs/k6/latest/

These references support the evaluation methodology and proposed future improvements.

They are not the source of the project's measured performance results.

---

## 29. Conclusion

The Final Release of the AI-Powered IT Asset Management System demonstrates measurable progress beyond the Alpha Release.

The application successfully completed 138 automated tests with zero failures, errors, or skipped tests.

The automated test suite increased from eight to 138 tests, representing 130 additional tests and a 1625% increase in test count.

JaCoCo reported:

- 91.43% instruction coverage.
- 91.32% line coverage.
- 70.83% branch coverage.
- 71.37% complexity coverage.
- 92.03% method coverage.
- 100% class coverage.

The application successfully completed sixty sequential authenticated API benchmark requests with a 100% success rate and a combined mean response time of approximately 3.619 milliseconds.

Two recorded live AI recommendation generations demonstrated a mean response time of approximately 5.065 seconds.

The local application started in 2.087 seconds, and the Docker application image was successfully constructed in 25.9 seconds.

The final GitHub Actions CI/CD workflow completed successfully in one minute and forty-three seconds, verifying automated testing, code coverage, application packaging, MySQL-backed container deployment, application health, and protected API behavior.

These results demonstrate successful application integration, measurable local performance, expanded automated verification, functional AI recommendation generation, and reproducible containerized deployment.

The remaining limitations primarily concern production-scale performance, concurrent workloads, resource profiling, formal AI quality evaluation, production security hardening, and long-term operational reliability.

The application is delivered as a functional academic Final Release with documented engineering evidence and a defined roadmap for further professional development.

---

AI-Powered IT Asset Management System

Version 1.0.0

CMSC 495 — Computer Capstone Project

University of Maryland Global Campus
