
# AI-Powered IT Asset Management System

![Java](https://img.shields.io/badge/Java-25-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.9-green)
![MySQL](https://img.shields.io/badge/MySQL-8.4-blue)
![Docker](https://img.shields.io/badge/Docker-Containerized-blue)
![Version](https://img.shields.io/badge/Version-1.0.0-blue)

[![Spring Boot CI/CD](https://github.com/ilerioluwaa/ai-it-asset-management-system/actions/workflows/maven.yml/badge.svg)](https://github.com/ilerioluwaa/ai-it-asset-management-system/actions/workflows/maven.yml)

## 1. Project Overview

The AI-Powered IT Asset Management System is a web-based application designed to help organizations manage technology assets, employee equipment assignments, inventory availability, and operational decision-making.

The application combines traditional asset management functionality with an AI recommendation service that analyzes aggregated inventory statistics and provides operational recommendations for human review.

The system was developed as part of the CMSC 495 Computer Capstone Project at the University of Maryland Global Campus (UMGC).

## 2. Problem Statement

Organizations frequently manage technology assets through spreadsheets, disconnected applications, and manual administrative processes.

These approaches can introduce several operational challenges:

- Inaccurate inventory records.
- Duplicate asset registrations.
- Untracked employee equipment assignments.
- Limited visibility into equipment availability.
- Inefficient maintenance planning.
- Unauthorized modification of organizational records.
- Limited analytical support for inventory decisions.

This application addresses these challenges through centralized inventory management, relational database integration, automated validation, administrator authentication, operational analytics, and AI-assisted recommendations.

## 3. Project Objectives

- Centralize organizational IT asset records.
- Maintain accurate employee equipment assignments.
- Reduce duplicate and invalid records.
- Improve inventory visibility and accountability.
- Protect administrative operations.
- Provide AI-assisted operational recommendations.
- Support automated testing and reproducible deployment.
- Establish a maintainable and extensible software architecture.

## 4. Core Features

### Asset Management

- Register new technology assets.
- View and search asset inventory.
- Update existing asset records.
- Delete eligible assets.
- Prevent duplicate serial numbers.
- Filter assets by operational status.
- Navigate paginated inventory results.
- Assign and unassign equipment.
- Prevent deletion of assigned assets.

Supported asset statuses:

- AVAILABLE
- ASSIGNED
- MAINTENANCE
- RETIRED

### Employee Management

- Register employees.
- View employee records.
- Update employee information.
- Delete employees without assigned equipment.
- Validate employee email addresses.
- Prevent duplicate employee emails.
- Search employees by name or email.
- Filter employees by department.
- Display assigned asset counts.

### Asset Allocation

The asset and employee modules are integrated through a relational database association.

The system supports:

- Assigning available assets to registered employees.
- Returning assigned equipment to available inventory.
- Preserving employee assignments during unrelated asset updates.
- Maintaining assignment relationships when employee names change.
- Preventing deletion of employees with assigned assets.

### Operational Dashboard

The dashboard displays:

- Total assets.
- Available assets.
- Assigned assets.
- Assets under maintenance.
- Retired assets.
- Total employees.
- Asset utilization percentage.
- Asset category distribution.
- Employee department distribution.

### AI Recommendation Service

The AI recommendation service is designed to analyze aggregated operational statistics and generate natural-language recommendations.

Potential recommendations include:

- Equipment availability improvements.
- Inventory utilization opportunities.
- Maintenance prioritization.
- Equipment allocation considerations.
- Procurement planning considerations.

The AI service requires a configured OpenAI-compatible chat completion provider.

AI recommendations are advisory and require human review. The AI service does not independently modify inventory records or assign equipment.

## 5. Technology Stack

| Technology | Purpose |
|---|---|
| Java 25 | Backend programming language |
| Spring Boot 4.0.9 | Application framework |
| Spring MVC | REST API development |
| Spring Data JPA | Database persistence |
| Hibernate | Object-relational mapping |
| MySQL 8.4 | Application database |
| H2 | Isolated automated test database |
| Spring Security | Authentication and authorization |
| Jakarta Bean Validation | Input validation |
| HTML5 | Frontend structure |
| CSS3 | Frontend styling |
| JavaScript | Client-side application logic |
| Bootstrap 5.3.3 | Responsive interface components |
| Maven | Build and dependency management |
| JUnit 5 | Automated testing |
| Mockito | Test dependency simulation |
| MockMvc | HTTP integration testing |
| JaCoCo | Code coverage reporting |
| Docker | Application containerization |
| Docker Compose | Multi-container deployment |
| GitHub Actions | Automated CI/CD verification |
| Git | Version control |
| REST API | Application and AI provider communication |

## 6. System Architecture

The application follows a layered architecture that separates presentation, request handling, business logic, persistence, and external AI integration.

```mermaid
flowchart TD
    U[Administrator] --> UI[Web Interface]
    UI --> SEC[Spring Security]
    SEC --> API[REST Controllers]

    API --> AS[Asset Service]
    API --> ES[Employee Service]
    API --> AI[AI Recommendation Service]

    AS --> AR[Asset Repository]
    AS --> ER[Employee Repository]
    ES --> ER
    ES --> AR

    AR --> DB[(MySQL Database)]
    ER --> DB

    AI --> AS
    AI --> ES
    AI --> EXT[Configured AI Provider]
```

### Presentation Layer

The presentation layer consists of HTML, CSS, Bootstrap, and JavaScript.

It provides the dashboard, asset inventory, employee directory, and AI recommendation interface.

### Controller Layer

REST controllers receive HTTP requests, validate incoming request data, and return structured responses.

Primary controllers:

- AssetController
- EmployeeController
- AiController

### Service Layer

The service layer contains business rules, validation logic, assignment management, operational statistics, and AI provider communication.

Primary services:

- AssetService
- EmployeeService
- AiRecommendationService

### Persistence Layer

Spring Data JPA repositories provide database access and query functionality.

Primary repositories:

- AssetRepository
- EmployeeRepository

### Security Layer

Spring Security protects application resources through administrator authentication, authorization, session management, CSRF protection, and security response headers.

## 7. Database Design

The application uses two primary relational database entities.

### Asset Entity

| Field | Description |
|---|---|
| id | Unique asset identifier |
| name | Asset name |
| category | Equipment category |
| serial_number | Unique serial number |
| status | Operational status |
| employee_id | Assigned employee reference |

### Employee Entity

| Field | Description |
|---|---|
| id | Unique employee identifier |
| name | Employee name |
| email | Unique employee email |
| department | Organizational department |

### Entity Relationship Diagram

```mermaid
erDiagram
    EMPLOYEE o|--o{ ASSET : receives

    EMPLOYEE {
        bigint id PK
        varchar name
        varchar email UK
        varchar department
    }

    ASSET {
        bigint id PK
        varchar name
        varchar category
        varchar serial_number UK
        varchar status
        bigint employee_id FK
    }
```

An employee may have multiple assigned assets. An asset may be unassigned or associated with one employee.

The employee relationship uses a foreign key rather than storing an independent employee name within the asset record.

## 8. Project Structure

```text
ai-it-asset-management-system/
|
|-- .github/
|   `-- workflows/
|       `-- maven.yml
|
|-- .mvn/
|
|-- src/
|   |-- main/
|   |   |-- java/com/cmsc495/assetmanagement/
|   |   |   |-- config/
|   |   |   |-- controller/
|   |   |   |-- dto/
|   |   |   |-- exception/
|   |   |   |-- model/
|   |   |   |-- repository/
|   |   |   |-- service/
|   |   |   `-- AssetmanagementApplication.java
|   |   |
|   |   `-- resources/
|   |       |-- static/
|   |       |   |-- index.html
|   |       |   |-- script.js
|   |       |   `-- style.css
|   |       `-- application.properties
|   |
|   `-- test/
|       |-- java/com/cmsc495/assetmanagement/
|       |   |-- controller/
|       |   |-- security/
|       |   |-- service/
|       |   `-- AssetmanagementApplicationTests.java
|       `-- resources/
|           `-- application-test.properties
|
|-- Dockerfile
|-- compose.yaml
|-- .dockerignore
|-- .gitignore
|-- mvnw
|-- mvnw.cmd
|-- pom.xml
|-- README.md
|-- API.md
|-- INSTALLATION.md
|-- USER_MANUAL.md
|-- TECHNICAL_DEBT.md
`-- PERFORMANCE.md
```

## 9. Installation and Setup

### Prerequisites

For local development:

- Java Development Kit 25.
- MySQL Server.
- Git.
- Internet access for initial dependency downloads.

For container deployment:

- Docker.
- Docker Compose.

An AI provider is optional for core asset and employee management functionality.

### Clone the Repository

```bash
git clone https://github.com/ilerioluwaa/ai-it-asset-management-system.git

cd ai-it-asset-management-system
```

### Option A: Local Development

Create a new MySQL database for the application.

```sql
CREATE DATABASE assetmanagement_final;
```

Configure the application environment variables.

```bash
export DB_URL="jdbc:mysql://localhost:3306/assetmanagement_final"

export DB_USERNAME="your_database_username"

export DB_PASSWORD="your_database_password"

export ADMIN_USERNAME="admin"

export ADMIN_PASSWORD="your_strong_admin_password"

export AI_ENABLED="false"
```

The configured database user must have appropriate permissions for the new database.

The administrator password must contain at least 12 characters and must not exceed 72 UTF-8 bytes.

Start the application.

```bash
chmod +x mvnw

./mvnw spring-boot:run
```

Open the application in a browser.

```text
http://localhost:8080
```

Authenticate using the administrator credentials configured through environment variables.

### Option B: Docker Deployment

Create a local `.env` file in the project root.

```dotenv
DB_NAME=assetmanagement
DB_USERNAME=assetadmin
DB_PASSWORD=replace_with_database_password
MYSQL_ROOT_PASSWORD=replace_with_root_password

ADMIN_USERNAME=admin
ADMIN_PASSWORD=replace_with_strong_admin_password

PORT=8080
COOKIE_SECURE=false

AI_ENABLED=false
AI_API_KEY=
AI_MODEL=
AI_BASE_URL=
```

Replace the example passwords before deployment.

Do not commit the `.env` file to version control.

Build and start the application.

```bash
docker compose up -d --build
```

Inspect the deployment.

```bash
docker compose ps
```

Check application health.

```bash
curl http://localhost:8080/actuator/health
```

The expected response for a healthy application is:

```json
{
  "status": "UP"
}
```

Stop the deployment while preserving database records.

```bash
docker compose down
```

For detailed installation instructions, see [INSTALLATION.md](INSTALLATION.md).

### Database Migration Notice

The Final Release introduces an employee foreign-key relationship and enumerated asset statuses.

An existing Alpha Release database must be backed up and migrated before use with the revised application.

A new database is recommended for initial Final Release verification.

Automatic schema updates do not replace a controlled database migration process.

## 10. AI Configuration

The AI service supports a configurable OpenAI-compatible chat completion endpoint.

The following environment variables control AI functionality:

| Variable | Description |
|---|---|
| AI_ENABLED | Enables or disables the AI service |
| AI_API_KEY | Provider API credential, if required |
| AI_MODEL | Provider-specific model identifier |
| AI_BASE_URL | Provider API base URL |

### External AI Provider

Example configuration:

```bash
export AI_ENABLED="true"

export AI_API_KEY="your_provider_api_key"

export AI_MODEL="your_provider_model"

export AI_BASE_URL="https://api.openai.com/v1"
```

The model must be available to the configured provider account.

External AI providers may require separate billing and account configuration.

### Local AI Provider

For local development, an OpenAI-compatible provider such as Ollama may be configured.

```bash
export AI_ENABLED="true"

export AI_API_KEY=""

export AI_MODEL="your_installed_model"

export AI_BASE_URL="http://localhost:11434/v1"
```

The local model must be installed and the provider must be running before generating recommendations.

This localhost configuration assumes Spring Boot is running directly on the host computer rather than inside a Docker container.

### AI Data Flow

```mermaid
flowchart TD
    A[Asset Statistics] --> C[AI Recommendation Service]
    B[Employee Statistics] --> C

    C --> D[Aggregate Data Preparation]
    D --> E[Structured Prompt]
    E --> F[Configured AI Provider]
    F --> G[Recommendation Response]
    G --> H[Administrator Review]
    H --> I[Human Decision]
```

### AI Privacy and Governance

The AI provider receives selected aggregate inventory statistics rather than individual employee records.

The AI integration is designed to exclude:

- Employee names.
- Employee email addresses.
- Individual asset serial numbers.
- Individual employee equipment assignments.

The AI service does not receive direct database access or tools for modifying inventory.

Generated recommendations must be reviewed by an authorized administrator before operational decisions are made.

AI output may contain inaccuracies and must not be treated as authoritative without verification.

## 11. REST API Overview

Application API endpoints require administrator authentication.

State-changing requests also require a valid CSRF token.

### Asset Endpoints

| Method | Endpoint | Purpose |
|---|---|---|
| GET | /api/assets | Retrieve paginated assets |
| GET | /api/assets/{id} | Retrieve an asset |
| POST | /api/assets | Register an asset |
| PUT | /api/assets/{id} | Update an asset |
| DELETE | /api/assets/{id} | Delete an eligible asset |
| POST | /api/assets/{id}/assign | Assign equipment |
| POST | /api/assets/{id}/unassign | Return equipment |
| GET | /api/assets/statistics | Retrieve inventory statistics |

### Employee Endpoints

| Method | Endpoint | Purpose |
|---|---|---|
| GET | /api/employees | Retrieve paginated employees |
| GET | /api/employees/{id} | Retrieve an employee |
| POST | /api/employees | Register an employee |
| PUT | /api/employees/{id} | Update an employee |
| DELETE | /api/employees/{id} | Delete an eligible employee |
| GET | /api/employees/departments | Retrieve departments |
| GET | /api/employees/statistics | Retrieve employee statistics |

### AI Endpoints

| Method | Endpoint | Purpose |
|---|---|---|
| GET | /api/ai/status | Retrieve AI configuration status |
| POST | /api/ai/recommendations | Generate AI recommendations |

### Security and Monitoring Endpoints

| Method | Endpoint | Purpose |
|---|---|---|
| GET | /login | Administrator login page |
| POST | /login | Authenticate administrator |
| POST | /logout | Terminate authenticated session |
| GET | /api/csrf | Retrieve CSRF token |
| GET | /actuator/health | Application health status |
| GET | /actuator/info | Protected application information |

For detailed request examples, response formats, and HTTP status codes, see [API.md](API.md).

## 12. Security Implementation

The application incorporates security controls intended to reduce common web application risks.

### Authentication and Authorization

- Administrator authentication.
- Role-based access restrictions.
- BCrypt password hashing.
- Session-based authentication.
- Session fixation protection.
- Logout and session invalidation.

### Input Validation

- Required-field validation.
- Maximum input lengths.
- Employee email validation.
- Asset status validation.
- Duplicate serial number prevention.
- Duplicate employee email prevention.
- Employee relationship validation.

### Web Application Security

- CSRF protection.
- Content Security Policy.
- Clickjacking protection.
- Content-type sniffing protection.
- Restricted error information.
- HTTP-only session cookies.

### Infrastructure Security

- Environment-based credential configuration.
- Non-root application container.
- Private database network.
- No directly published MySQL port.
- Localhost application binding by default.

The current implementation is intended for controlled deployment and evaluation.

Production deployment would require additional controls, including HTTPS, persistent identity management, rate limiting, audit logging, database migration management, vulnerability scanning, and operational monitoring.

## 13. Automated Testing

The automated test suite is organized into five primary test classes.

| Test Class | Testing Focus |
|---|---|
| AssetControllerTest | Asset CRUD, validation, allocation, and regression testing |
| EmployeeControllerTest | Employee CRUD, validation, and cross-module integration |
| AiRecommendationServiceTest | AI provider communication, privacy, and failure handling |
| SecurityTest | Authentication, authorization, CSRF, and security headers |
| AssetmanagementApplicationTests | Application initialization and database connectivity |

The test environment uses an isolated H2 in-memory database.

External AI responses are simulated during automated AI service testing to provide repeatable results without requiring paid API access.

### Execute Automated Tests

```bash
./mvnw clean test
```

### Execute Build and Coverage Verification

```bash
./mvnw clean verify
```

### Test Reports

Maven Surefire reports are generated in:

```text
target/surefire-reports/
```

JaCoCo coverage reports are generated in:

```text
target/site/jacoco/
```

Open the HTML coverage report:

```text
target/site/jacoco/index.html
```

### Verification Status

The Alpha Release baseline completed eight automated tests successfully.

The expanded Final Release test suite requires consolidated execution and verification.

Final test counts, failures, execution duration, and code coverage percentages must be taken from the generated Maven and JaCoCo reports.

Simulated AI provider tests do not replace live AI functionality and output-quality evaluation.

## 14. Continuous Integration and Deployment

The GitHub Actions workflow is configured to automate:

1. Repository checkout.
2. Java 25 environment configuration.
3. Maven dependency management.
4. Application compilation.
5. Automated test execution.
6. JaCoCo coverage generation.
7. Application packaging.
8. Docker image construction.
9. MySQL-backed container deployment.
10. Application health verification.
11. Authentication smoke testing.
12. Deployment evidence collection.

### CI/CD Pipeline

```mermaid
flowchart TD
    A[GitHub Push or Pull Request] --> B[Compile Application]
    B --> C[Execute Automated Tests]
    C --> D{Tests Passed?}

    D -->|No| E[Workflow Failure]
    D -->|Yes| F[Generate Coverage Report]

    F --> G[Package Application]
    G --> H[Build Docker Image]
    H --> I[Deploy Application and MySQL]
    I --> J[Health and Security Checks]

    J --> K{Verification Passed?}
    K -->|No| E
    K -->|Yes| L[Upload Deployment Evidence]
    L --> M[Clean Up Temporary Containers]
```

### Workflow Artifacts

The workflow is configured to generate:

- automated-test-reports
- jacoco-coverage-report
- application-package
- deployment-evidence

GitHub Actions workflow history:

https://github.com/ilerioluwaa/ai-it-asset-management-system/actions

Deployment verification takes place in an isolated GitHub Actions environment.

This does not constitute permanent public production hosting.

## 15. Technical Debt and Refinement

The Final Release design incorporates improvements identified during development and peer review.

Major refinement areas include:

- Server-side asset validation.
- Employee information validation.
- Duplicate record prevention.
- Relational employee assignments.
- Preservation of existing asset assignments.
- Employee deletion safeguards.
- Paginated inventory retrieval.
- Administrator authentication.
- Expanded automated testing.
- AI provider integration.
- Container deployment verification.

A peer review identified a defect in which editing an asset could unintentionally remove its existing employee assignment.

The defect was reproduced through a failing Alpha Release regression test.

The revised implementation uses a relational employee association and includes a regression test intended to verify assignment preservation.

Resolution must be confirmed through successful execution of the revised regression test.

For the complete technical debt register, see [TECHNICAL_DEBT.md](TECHNICAL_DEBT.md).

## 16. Performance and Scalability

The application incorporates several design measures intended to support maintainability and scalability:

- Paginated asset retrieval.
- Paginated employee retrieval.
- Database indexes.
- Unique database constraints.
- Connection pooling.
- Transactional service operations.
- Aggregate database queries.
- AI provider connection and response timeouts.
- Container-aware JVM memory configuration.

Performance measurements will include:

- REST API response latency.
- Inventory retrieval performance.
- Automated test execution duration.
- AI recommendation response latency.
- Application startup duration.
- Database-backed deployment reliability.
- Code coverage metrics.

Performance results must be based on actual measurements rather than estimated or simulated production benchmarks.

See [PERFORMANCE.md](PERFORMANCE.md) for benchmark procedures and results.

## 17. Ethical and Responsible Computing

The application follows responsible computing principles through:

- Protection of organizational information.
- Limited disclosure of employee data.
- Human oversight of AI-generated recommendations.
- Transparent documentation of AI limitations.
- Separation of AI recommendations from administrative actions.
- Accurate reporting of testing and deployment results.

AI recommendations must not be used to make discriminatory employee decisions or unsupported assumptions about individual performance.

Administrators remain responsible for verifying recommendations and approving operational changes.

## 18. Known Limitations

The current release has the following limitations:

- Administrator accounts use in-memory identity management.
- Production-grade audit logging is not implemented.
- Application-level rate limiting is not implemented.
- Permanent public hosting is not configured.
- Database migrations require additional management.
- Concurrent asset assignment behavior requires further evaluation.
- Employee assignment counts may introduce additional database queries.
- Employee selection currently retrieves the available employee directory across multiple pages.
- AI output quality depends on the configured provider and model.
- AI recommendations require human verification.
- Live AI availability depends on provider configuration and connectivity.
- Automated coverage reports do not guarantee the absence of software defects.

These limitations represent opportunities for future refinement.

## 19. Documentation

| Document | Description |
|---|---|
| README.md | Project overview and architecture |
| API.md | REST API reference |
| INSTALLATION.md | Installation and deployment guide |
| USER_MANUAL.md | Application usage instructions |
| TECHNICAL_DEBT.md | Technical debt and refinement register |
| PERFORMANCE.md | Performance testing and benchmark results |

## 20. Development Roadmap

Potential future improvements include:

- Persistent administrator and employee accounts.
- Granular role-based access control.
- Multi-factor authentication.
- Comprehensive audit logging.
- Automated database migrations.
- Advanced inventory reporting.
- Asset maintenance scheduling.
- Barcode and QR code integration.
- AI-assisted inventory forecasting.
- Improved recommendation evaluation.
- Concurrency and load testing.
- Production monitoring and alerting.
- Secure cloud deployment.

## 21. Version History

| Version | Release | Description |
|---|---|---|
| 0.1.0 | Initial Development | Core project setup |
| 0.5.0 | Alpha Release | Asset management and initial automated testing |
| 1.0.0 | Final Release Candidate | Expanded integration, security, AI, testing, and deployment configuration |

Version 1.0.0 remains a release candidate until consolidated verification is completed.

## 22. Project Contributors

### Shalom Olukunle

University of Maryland Global Campus

CMSC 495 - Computer Capstone Project

Primary development contributions:

- System architecture and implementation.
- Backend REST API development.
- Database integration.
- Frontend application development.
- Asset and employee module integration.
- AI recommendation service integration.
- Application security implementation.
- Automated testing and regression testing.
- CI/CD configuration.
- Container deployment configuration.
- Technical documentation.

### Peer Review Acknowledgment

Jose Rodriguez-Stahr provided Alpha Release peer review feedback concerning assignment preservation, validation, authorization, scalability, and documentation.

The feedback informed the Final Release refinement process.

## 23. Academic and Professional Integrity

This project was developed for academic evaluation and professional software engineering development.

AI-assisted development tools were used to support implementation, documentation, test design, and technical problem-solving.

Generated material requires developer review, testing, and verification.

Testing results, performance measurements, deployment outcomes, and contribution records must accurately reflect completed and verified work.

## 24. Repository

GitHub Repository:

https://github.com/ilerioluwaa/ai-it-asset-management-system

---

AI-Powered IT Asset Management System

CMSC 495 - Computer Capstone Project

University of Maryland Global Campus

Version 1.0.0 - Final Release Candidate
