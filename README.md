# 🎓 EduCore — Event-driven Academic Management System

<p align="center">
  <img src="https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=java" />
  <img src="https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=for-the-badge&logo=springboot" />
  <img src="https://img.shields.io/badge/Apache_Kafka-231F20?style=for-the-badge&logo=apachekafka" />
  <img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white" />
  <img src="https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white" />
  <img src="https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=jsonwebtokens" />
  <img src="https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white" />
  <img src="https://img.shields.io/badge/CI/CD-GitHub_Actions-2088FF?style=for-the-badge&logo=githubactions&logoColor=white" />
</p>

> A production-ready **EduCore — Event-driven Academic Management System** built with a modern microservices architecture. The system covers authentication, student data management, academic grading, and real-time analytics — demonstrating enterprise-level patterns such as event-driven communication, JWT-based security, API gateway routing, and service discovery.

---

## 📐 System Architecture

```
                          ┌─────────────────────────────┐
                          │        Client / Browser       │
                          └──────────────┬──────────────┘
                                         │ HTTP Request
                          ┌──────────────▼──────────────┐
                          │         API Gateway           │
                          │  (Spring Cloud Gateway)       │
                          │  • JWT Validation             │
                          │  • Role-based Authorization   │
                          │  • Request Routing            │
                          └──┬──────┬──────┬──────┬──────┘
                             │      │      │      │
               ┌─────────────▼─┐  ┌─▼────┐  ┌───▼──────────┐  ┌──────────────────┐
               │  Auth Service  │  │Student│  │ Grade Service │  │Analytics Service │
               │                │  │Service│  │               │  │                  │
               │ • Register     │  │       │  │ • Subjects    │  │ • GPA Tracking   │
               │ • Login / JWT  │  │• CRUD │  │ • Grades      │  │ • Classification │
               │ • Role Mgmt    │  │• Paging│ │ • Teaching    │  │ • Kafka Consumer │
               └───────┬────────┘  └───┬───┘  └──────┬────────┘  └─────────────────┘
                       │               │              │
                       │        ┌──────▼──────────────▼──────┐
                       │        │         Apache Kafka         │
                       │        │  • StudentCreatedEvent       │
                       │        │  • StudentUpdatedEvent       │
                       │        │  • StudentDeletedEvent       │
                       │        │  • GradeCreatedEvent         │
                       │        └──────────────────────────────┘
                       │
               ┌───────▼──────────────┐
               │     Eureka Server     │
               │  (Service Discovery)  │
               └──────────────────────┘
```

---

## ✨ Key Features

### 🔐 Auth Service
- User registration and login with **JWT** access tokens
- **Role-based access control** (ADMIN, TEACHER, STUDENT)
- Auto-initialization of default roles and admin account on startup
- Internal API for inter-service user lookups
- Secured with **Spring Security** + custom `JwtAuthenticationFilter`

### 🧑‍🎓 Student Service
- Full CRUD operations for student profiles
- **Pagination & filtering** support for large datasets
- Publishes domain events (`StudentCreatedEvent`, `StudentUpdatedEvent`, `StudentDeletedEvent`) to Kafka
- Communicates with Auth Service via **OpenFeign** for account linking
- Internal endpoints for service-to-service calls
- Enum-based status (`ACTIVE`, `INACTIVE`) and gender support

### 📚 Grade Service
- Subject management with status tracking (`SubjectStatus`)
- Grade management with multiple grade types (`GradeType`) and semesters
- **Teaching Assignment** management — links teachers to subjects
- Publishes `GradeCreatedEvent` via Kafka
- Consumes student events from Kafka to maintain local data consistency
- Cross-service calls to both Auth and Student services via **Feign Clients**
- Advanced query support via **JPA Specifications**

### 📊 Analytics Service
- Consumes grade and student events from Kafka
- Computes **GPA** and academic performance analytics per student
- Classifies students based on performance (`Classification` enum)
- Maintains a local **GradeCache** for fast analytics queries
- Exposes analytics endpoints secured via JWT

### 🌐 API Gateway
- Single entry point for all client requests
- **JWT validation** at the gateway level before forwarding to downstream services
- **Role-based route authorization** — ADMIN-only routes, TEACHER-only routes, etc.
- Custom `ForbiddenHandler` and `UnauthorizedHandler` for clean error responses
- `RouteValidator` for public vs. protected route management

### 🔍 Eureka Server
- **Service discovery and registration** for all microservices
- Health monitoring and instance management

---

## 🛠 Tech Stack

| Category | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.x |
| API Gateway | Spring Cloud Gateway |
| Service Discovery | Netflix Eureka |
| Inter-service Communication | OpenFeign |
| Async Messaging | Apache Kafka |
| Security | Spring Security + JWT (JJWT) |
| Persistence | Spring Data JPA + Hibernate |
| Database | MySQL |
| Object Mapping | MapStruct |
| API Documentation | SpringDoc OpenAPI (Swagger UI) |
| Containerization | Docker + Docker Compose |
| CI/CD | GitHub Actions |
| Testing | JUnit 5 + Mockito |
| Build Tool | Maven (Multi-module) |

---

## 📁 Project Structure

```
educore-system/
├── api-gateway/            # Spring Cloud Gateway — routing & JWT auth
├── eureka-server/          # Netflix Eureka — service registry
├── auth-service/           # Authentication, JWT issuance, user & role management
├── student-service/        # Student profile CRUD, Kafka producer
├── grade-service/          # Subjects, grades, teaching assignments, Kafka producer/consumer
├── analytics-service/      # Performance analytics, GPA calculation, Kafka consumer
├── common-event/           # Shared Kafka event DTOs (StudentCreatedEvent, GradeCreatedEvent, ...)
├── docker/
│   └── mysql/
│       └── init.sql        # Database initialization script
├── docker-compose.yaml     # Full-stack orchestration
├── pom.xml                 # Root multi-module Maven POM
└── .github/
    └── workflows/
        └── ci.yml          # GitHub Actions CI pipeline
```

---

## 🔄 Event-Driven Communication

The system uses **Apache Kafka** for asynchronous, decoupled communication between services. Shared event schemas are defined in the `common-event` module.

```
Student Service ──► [student.created]  ──► Grade Service, Analytics Service
Student Service ──► [student.updated]  ──► Analytics Service
Student Service ──► [student.deleted]  ──► Analytics Service
Grade Service   ──► [grade.created]    ──► Analytics Service
```

This pattern ensures **loose coupling** — services remain independent and resilient to downstream failures.

---

## 🔐 Security Model

Each microservice independently validates JWT tokens via a shared `JwtAuthenticationFilter`. The API Gateway performs an **initial validation and role check** before forwarding requests, providing a defense-in-depth security model.

```
Request → API Gateway (JWT Validation + Role Check) → Microservice (JWT Re-validation)
```

**Roles:**
- `ROLE_ADMIN` — Full system access, user and role management
- `ROLE_TEACHER` — Grade and subject management
- `ROLE_STUDENT` — Read own profile and grades

---

## 🚀 Getting Started

### Prerequisites

- Docker & Docker Compose
- Java 17+
- Maven 3.8+

### Run with Docker Compose

```bash
# Clone the repository
git clone https://github.com/trinhxuanhuan/educore-system.git
cd educore-system

# Start all services
docker-compose up --build
```

This will start:

| Service | Port |
|---|---|
| Eureka Server | `8761` |
| API Gateway | `8080` |
| Auth Service | `8081` |
| Student Service | `8082` |
| Grade Service | `8083` |
| Analytics Service | `8084` |
| MySQL | `3306` |
| Apache Kafka | `9092` |

### Access Swagger UI

Each service exposes its own Swagger documentation:

| Service | Swagger URL |
|---|---|
| Auth Service | http://localhost:8081/swagger-ui.html |
| Student Service | http://localhost:8082/swagger-ui.html |
| Grade Service | http://localhost:8083/swagger-ui.html |
| Analytics Service | http://localhost:8084/swagger-ui.html |
| Eureka Dashboard | http://localhost:8761 |

---

## 🧪 Testing

Unit tests are written using **JUnit 5** and **Mockito**, covering the service layer of each microservice.

```bash
# Run all tests across all modules
mvn test

# Run tests for a specific service
cd auth-service && mvn test
```

**Test coverage includes:**
- `AuthServiceImplTest` — login, registration, JWT generation
- `AdminServiceImplTest` — role assignment, user management
- `UserServiceImplTest` — user retrieval and updates
- `StudentServiceImplTest` — CRUD, pagination, Kafka event publishing
- `GradeServiceImplTest` — grade creation, validation, event publishing
- `AnalyticsServiceImplTest` — GPA calculation, classification logic

---

## 🔧 CI/CD Pipeline

The project includes a **GitHub Actions** workflow (`.github/workflows/ci.yml`) that automatically:

1. Checks out the code
2. Sets up Java 17
3. Builds all modules with Maven
4. Runs the full test suite

This ensures every push and pull request is validated before merging.

---

## 📖 API Overview

### Auth Service (`/api/auth`)

| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/api/auth/register` | Public | Register new user |
| `POST` | `/api/auth/login` | Public | Login and receive JWT |
| `GET` | `/api/users/me` | Authenticated | Get current user info |
| `POST` | `/api/admin/assign-role` | ADMIN | Assign role to user |

### Student Service (`/api/students`)

| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/api/students` | ADMIN | Create new student |
| `GET` | `/api/students` | ADMIN, TEACHER | Get all students (paginated) |
| `GET` | `/api/students/{id}` | ADMIN, TEACHER, STUDENT | Get student by ID |
| `PUT` | `/api/students/{id}` | ADMIN | Update student |
| `DELETE` | `/api/students/{id}` | ADMIN | Delete student |
| `PATCH` | `/api/students/{id}/self` | STUDENT | Student updates own profile |

### Grade Service (`/api/grades`, `/api/subjects`)

| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/api/subjects` | ADMIN | Create subject |
| `POST` | `/api/grades` | TEACHER | Submit grade for student |
| `PUT` | `/api/grades/{id}` | TEACHER | Update grade |
| `GET` | `/api/grades` | ADMIN, TEACHER | Query grades (filterable) |
| `POST` | `/api/teaching-assignments` | ADMIN | Assign teacher to subject |

### Analytics Service (`/api/analytics`)

| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/api/analytics/students/{id}` | ADMIN, TEACHER | Get student analytics & GPA |

---

## 💡 Design Decisions

- **`common-event` module** — Shared Kafka event DTOs are extracted into a dedicated Maven module, ensuring schema consistency across producer and consumer services without code duplication.
- **MapStruct** — Used for compile-time, type-safe object mapping between entities and DTOs, avoiding runtime reflection overhead.
- **JPA Specifications** — Implemented in `grade-service` for dynamic, composable query filtering.
- **DataInitializer / RoleInitializer / AdminInitializer** — Ensures the system is ready to use immediately after startup with default roles and an admin account.
- **Internal Controllers** — Dedicated `InternalController` endpoints (e.g. `StudentInternalController`, `InternalUserController`) are used for service-to-service communication, keeping public and internal APIs cleanly separated.

---

## 👤 Author

**Trịnh Xuân Huấn**

- GitHub: [@trinhxuanhuan](https://github.com/trinhxuanhuan)

---

> *Built as a portfolio project to demonstrate microservices architecture, event-driven design, and enterprise Spring Boot patterns.*
