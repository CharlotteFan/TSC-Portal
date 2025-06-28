# TSC-Portal

## Project Overview

TSC-Portal is a sample Spring Boot web service for transaction management in a banking system, built with Java 21. The application allows users to record, view, and manage financial transactions, with all data stored in memory (no persistent storage required). The project emphasizes high performance, robust validation, and comprehensive testing.

## Features

- Core entity: Transaction
- In-memory data storage (no database required)
- Clear, well-structured RESTful API
- High performance for all core operations
- Efficient data queries and pagination
- Caching mechanisms for improved performance
- Robust validation and exception handling
- Comprehensive unit and stress testing
- Containerization support (Docker, Kubernetes)
- Maven for project management

## Main API Endpoints

- `POST /transaction/create` — Create a transaction (`userId` required)
- `PUT /transaction/update` — Update a transaction (`userId` required)
- `POST /transaction/approve` — Approve a transaction (`userId` required)
- `POST /transaction/reject` — Reject a transaction (`userId` required)
- `POST /transaction/cancel` — Cancel a transaction (`userId` required)
- `GET /transaction/{id}` — Get transaction by ID (`userId` required)
- `DELETE /transaction/{id}` — Delete a transaction (`userId` required)
- `POST /transaction/search` — Search transactions with pagination and filters

Error handling is implemented for scenarios such as creating duplicate transactions or deleting non-existent transactions.

## Page Functionalities

- Add, modify, and delete transactions
- Display transaction list with pagination

## Getting Started

### 1. Clone the Project

```bash
git clone <https://github.com/CharlotteFan/TSC-Portal.git/>
cd TSC-Portal
```

### 2. Build and Run

```bash
mvn clean package
java -jar target/tsc-portal-*.jar
```

Or with Docker:

```bash
docker build -t tsc-portal .
docker run -p 8080:8080 tsc-portal
```

### 3. Access the API

```bash
mvn test
```

## Dependencies

In addition to the standard JDK, the following main dependencies are used:

- `spring-boot-starter-web`: Web service development
- `spring-boot-starter-validation`: Parameter validation
- `spring-boot-starter-test`: Unit testing
- `spring-boot-starter-cache`: Caching support
- `springdoc-openapi-ui` (optional): API documentation
- `caffeine` (optional): High-performance local cache
- `junit`, `mockito`: Testing and mocking
- `testcontainers` (optional): Stress testing in containers

See `pom.xml` for the full list. If you add other third-party libraries, please list and explain them here.

## Design Notes

- Uses thread-safe in-memory storage (e.g., ConcurrentHashMap)
- Transaction entity can be extended with types/categories
- Unified exception handling with standard error responses
- Pagination support via `page` and `size` parameters
- Caching for frequently accessed queries
- Unit and stress tests for all core logic

## Containerization & Deployment

- Standard Dockerfile provided for easy build and deployment
- Example Kubernetes YAML for orchestration (if needed)

## Contact & Feedback

For suggestions or issues, please open an Issue or Pull Request.

---

This project is ideal for learning and demonstrating Spring Boot RESTful API design, transaction management, caching, testing, and containerization best practices.
