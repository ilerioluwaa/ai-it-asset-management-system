# AI IT Asset Management System

**CMSC 495 – Computer Capstone Project**

## Project Overview

The AI IT Asset Management System is a web-based application designed to help organizations register, track, update, and manage IT assets through a centralized dashboard.

The project's long-term objective is to incorporate artificial intelligence to support asset monitoring and decision-making. The Alpha Release establishes the foundational asset management functionality required for further development.

## Technology Stack

- **Backend:** Java, Spring Boot, Spring Data JPA
- **Frontend:** HTML, CSS, JavaScript, Bootstrap
- **Database:** MySQL
- **Build and dependency management:** Maven
- **Automated testing:** JUnit, Spring Boot Test, MockMvc, H2 test database
- **Version control:** Git and GitHub
- **Continuous integration:** GitHub Actions

## Implemented Alpha Features

### Asset Registration
Users can register IT assets by providing the asset name, category, serial number, and status.

### Asset Inventory
The dashboard displays registered assets and their associated information. A total asset counter updates based on inventory records.

### Asset Editing
Users can edit existing asset information and save changes to the database.

### Asset Deletion
Users can remove assets after confirming the deletion request.

### Input Validation and Feedback
The application provides required-field validation and displays success or error messages for asset management operations.

## Application Architecture

The application uses a layered architecture:

1. The frontend sends HTTP requests to the Spring Boot REST API.
2. REST controllers process asset management requests.
3. Spring Data JPA repositories handle database operations.
4. MySQL stores asset information.
5. The frontend retrieves and displays updated records.

## Running the Application

### Prerequisites
- Java 25
- Maven or the included Maven Wrapper
- MySQL
- A configured application database and database user

### Local Setup

1. Clone the GitHub repository.
2. Configure the database connection in `src/main/resources/application.properties`.
3. Provide the database password using the `DB_PASSWORD` environment variable.
4. Open a terminal in the project directory.
5. Start the application:

```bash
./mvnw spring-boot:run
```

6. Open the application at:

`http://localhost:8080`

## Automated Testing

The Alpha Release includes automated tests covering asset management API functionality and application startup.

The latest verified test execution reported:

- Tests executed: 8
- Passed: 8
- Failures: 0
- Errors: 0
- Skipped: 0

Tests can be executed locally using:

```bash
./mvnw clean test
```

## Continuous Integration

The repository uses GitHub Actions with a Spring Boot CI workflow.

The workflow automatically builds the project and executes Maven tests when changes are pushed to the repository.

The latest reviewed workflow completed successfully, including compilation, automated testing, and application packaging.

## Current Limitations and Future Development

The Alpha Release focuses on foundational IT asset management capabilities.

Planned improvements include:

- AI-assisted asset analysis and recommendations
- Enhanced asset lifecycle tracking
- Improved security and access controls
- Additional validation and automated test coverage
- Dashboard and user experience refinements

These capabilities are planned improvements and should not be considered implemented Alpha features.

## Academic Context

Developed as part of the University of Maryland Global Campus CMSC 495 Computer Capstone Project.