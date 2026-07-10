# Car Dealership Inventory System

## Complete Implementation Plan

---

## Overview

Build a full-stack Car Dealership Inventory System for the **Incubyte TDD kata** using **Java 21 + Spring Boot 3.4.1** (backend), **React 19 + Vite 8** (frontend), and **PostgreSQL** (database). Follows strict **TDD (Red-Green-Refactor)**, **SOLID principles**, and transparent **AI co-authorship** with `Co-authored-by: DeepSeek <AI@users.noreply.github.com>` trailers.

---

## GitHub Repository Setup

- **Owner:** `veddarji`
- **Repo name:** `car-dealership`
- **Visibility:** Public
- **Issues:** Enabled — create issues for each feature area
- **Projects:** Enabled — simple kanban board (To Do → In Progress → Done)

### Issues Template

| # | Issue Title | Labels | Linked Phase |
|---|---|---|---|
| 1 | User Authentication (Register/Login) | `backend`, `auth` | Phase 2, 4 |
| 2 | JWT Security Layer | `backend`, `security` | Phase 2 |
| 3 | Vehicle CRUD Operations | `backend`, `vehicles` | Phase 3, 4 |
| 4 | Search with Pagination & Sort | `backend`, `search` | Phase 3, 4 |
| 5 | Inventory Management (Purchase/Restock) | `backend`, `inventory` | Phase 3, 4 |
| 6 | Integration Tests with Testcontainers | `backend`, `testing` | Phase 5 |
| 7 | Swagger/OpenAPI Documentation | `backend`, `docs` | Phase 6 |
| 8 | Docker Compose Setup | `infra` | Phase 7 |
| 9 | React Frontend — Auth Pages | `frontend`, `auth` | Phase 9 |
| 10 | React Frontend — Dashboard & Vehicles | `frontend`, `vehicles` | Phase 9 |
| 11 | React Frontend — Admin Panel | `frontend`, `admin` | Phase 9 |
| 12 | Frontend Tests | `frontend`, `testing` | Phase 10 |
| 13 | Documentation & README | `docs` | Phase 12 |

All commits reference their issue: `feat(auth): implement JWT login (Closes #2)`

---

## Tech Stack

| Layer | Technology | Rationale |
|---|---|---|
| Backend | Java 21 + Spring Boot 3.4.1 | Enterprise-grade, excellent TDD/SOLID support |
| API Security | Spring Security 6 + JWT (jjwt 0.12.x) | Token-based auth, role-based access control |
| Database | PostgreSQL 16 | Production-quality, required by kata |
| ORM | Spring Data JPA + Hibernate | Standard for Spring Boot |
| API Docs | Springdoc OpenAPI (swagger-ui) | Interviewers can test endpoints without Postman |
| Testing (BE unit) | JUnit 5 + Mockito + H2 | Fast unit tests for service/controller layers |
| Testing (BE integration) | JUnit 5 + Testcontainers (PostgreSQL) | Real DB integration — controller → service → repository → DB |
| Coverage (BE) | JaCoCo (min 80%) | Enforced in CI |
| Frontend | React 19 + Vite 8 | Modern SPA, fast dev server |
| Styling | Custom CSS (dark theme) | Professional look with glassmorphism |
| HTTP Client | Axios with interceptors | JWT token management |
| Notifications | Sonner | Lightweight toast library (< 3KB) |
| Testing (FE) | Vitest + React Testing Library | Fast, modern testing stack |
| Coverage (FE) | Vitest v8 (min 70%) | Enforced in CI |
| Build | Maven (BE), npm (FE) | Standard tooling |
| Containerization | Docker Compose | One-command PostgreSQL setup |
| CI/CD | GitHub Actions | Build, test, lint, coverage on push/PR |

---

## Project Structure

```
c:\Users\kalpe\Documents\INqubits\
├── .github/
│   ├── workflows/
│   │   └── ci.yml                          # CI pipeline
│   └── ISSUE_TEMPLATE/
│       └── feature.md                      # Issue template
├── backend/
│   ├── checkstyle.xml                      # Google Java Style (customized)
│   ├── docker-compose.yml                  # PostgreSQL + pgAdmin
│   ├── .env.example                        # Environment variable template
│   ├── pom.xml
│   ├── src/main/
│   │   ├── java/com/cardealership/
│   │   │   ├── CarDealershipApplication.java
│   │   │   ├── config/
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   ├── CorsConfig.java
│   │   │   │   ├── DataSeeder.java
│   │   │   │   └── OpenApiConfig.java       # Swagger/OpenAPI config
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java
│   │   │   │   └── VehicleController.java
│   │   │   ├── dto/
│   │   │   │   ├── request/
│   │   │   │   │   ├── RegisterRequest.java
│   │   │   │   │   ├── LoginRequest.java
│   │   │   │   │   ├── VehicleRequest.java
│   │   │   │   │   └── PurchaseRequest.java
│   │   │   │   └── response/
│   │   │   │       ├── AuthResponse.java
│   │   │   │       ├── VehicleResponse.java
│   │   │   │       ├── PagedResponse.java
│   │   │   │       └── ErrorResponse.java
│   │   │   ├── entity/
│   │   │   │   ├── User.java
│   │   │   │   └── Vehicle.java
│   │   │   ├── enums/
│   │   │   │   └── Role.java
│   │   │   ├── exception/
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   ├── ResourceNotFoundException.java
│   │   │   │   ├── OutOfStockException.java
│   │   │   │   └── DuplicateResourceException.java
│   │   │   ├── repository/
│   │   │   │   ├── UserRepository.java
│   │   │   │   └── VehicleRepository.java
│   │   │   ├── security/
│   │   │   │   ├── JwtService.java
│   │   │   │   ├── JwtAuthenticationFilter.java
│   │   │   │   └── CustomUserDetailsService.java
│   │   │   └── service/
│   │   │       ├── AuthService.java
│   │   │       ├── VehicleService.java
│   │   │       └── InventoryService.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── application-test.properties
│   └── src/test/
│       ├── java/com/cardealership/
│       │   ├── controller/
│       │   │   ├── AuthControllerTest.java
│       │   │   └── VehicleControllerTest.java
│       │   ├── integration/
│       │   │   ├── AuthFlowIntegrationTest.java
│       │   │   ├── VehicleFlowIntegrationTest.java
│       │   │   └── InventoryFlowIntegrationTest.java
│       │   ├── security/
│       │   │   └── JwtServiceTest.java
│       │   ├── service/
│       │   │   ├── AuthServiceTest.java
│       │   │   ├── VehicleServiceTest.java
│       │   │   └── InventoryServiceTest.java
│       │   └── TestCarDealershipApplication.java
│       └── resources/
│           └── application-test.properties
│
├── frontend/
│   ├── .env.example
│   ├── index.html
│   ├── package.json
│   ├── vite.config.js
│   ├── vitest.config.js
│   └── src/
│       ├── api/
│       │   ├── axiosInstance.js
│       │   ├── authApi.js
│       │   └── vehicleApi.js
│       ├── features/
│       │   ├── auth/
│       │   │   ├── AuthContext.jsx
│       │   │   ├── LoginPage.jsx
│       │   │   ├── RegisterPage.jsx
│       │   │   └── ProtectedRoute.jsx
│       │   ├── vehicles/
│       │   │   ├── VehicleCard.jsx
│       │   │   ├── VehicleGrid.jsx
│       │   │   ├── VehicleForm.jsx
│       │   │   ├── SearchBar.jsx
│       │   │   └── DashboardPage.jsx
│       │   └── admin/
│       │       ├── AdminPage.jsx
│       │       └── AdminRoute.jsx
│       ├── shared/
│       │   └── components/
│       │       ├── Button.jsx
│       │       ├── Input.jsx
│       │       ├── Modal.jsx
│       │       ├── Badge.jsx
│       │       ├── Loader.jsx
│       │       └── Navbar.jsx
│       ├── hooks/
│       │   ├── useAuth.js
│       │   └── useVehicles.js
│       ├── App.jsx
│       ├── main.jsx
│       └── index.css
│
├── README.md
├── PLAN.md
└── .gitignore
```

---

## Architecture Diagram

```
┌──────────────────────────────────────────────────────────────┐
│                        ┌─────────────┐                      │
│                        │   React 19   │                      │
│                        │     SPA      │                      │
│                        │ localhost:5173│                     │
│                        └──────┬──────┘                      │
│                               │ HTTP (Axios)                │
│                               │ JWT in Authorization header │
│                               ▼                              │
│                        ┌─────────────┐                      │
│                        │  Spring Boot │                     │
│                        │  localhost:8080│                    │
│                        └──────┬──────┘                      │
│                               │                              │
│              ┌────────────────┼────────────────┐             │
│              ▼                ▼                ▼             │
│     ┌──────────────┐ ┌──────────────┐ ┌──────────────┐      │
│     │ AuthController│ │VehicleController             │      │
│     │ /api/auth/*   │ │ /api/vehicles/*              │      │
│     └──────┬───────┘ └──────┬───────┘               │      │
│            │                │                        │      │
│            ▼                ▼                        │      │
│     ┌──────────────┐ ┌──────────────┐               │      │
│     │ AuthService   │ │VehicleService│←──InventorySvc│      │
│     └──────┬───────┘ └──────┬───────┘               │      │
│            │                │                        │      │
│            ▼                ▼                        │      │
│     ┌──────────────┐ ┌──────────────┐               │      │
│     │UserRepo (JPA) │ │VehicleRepo   │               │      │
│     └──────┬───────┘ └──────┬───────┘               │      │
│            │                │                        │      │
│            ▼                ▼                        │      │
│     ┌────────────────────────────────────┐            │
│     │         PostgreSQL (5432)          │            │
│     │   Database: cardealership          │            │
│     │   Tables: users, vehicles          │            │
│     └────────────────────────────────────┘            │
│                                                       │
│     ┌────────────────────────────────────┐            │
│     │   JWT Authentication Filter       │            │
│     │   (Validates token on every req)  │            │
│     └────────────────────────────────────┘            │
└──────────────────────────────────────────────────────────┘
```

---

## Database Design (ER Diagram)

```
┌────────────────────┐           ┌────────────────────┐
│       User         │           │      Vehicle       │
├────────────────────┤           ├────────────────────┤
│ id (PK, BIGSERIAL) │           │ id (PK, BIGSERIAL) │
│ username (UNIQUE)  │           │ make (NOT NULL)     │
│ email (UNIQUE)     │           │ model (NOT NULL)    │
│ password (NOT NULL)│           │ category (NOT NULL) │
│ role (ENUM)        │           │ price (NUMERIC)    │
└────────────────────┘           │ quantity (INT)      │
                                 └────────────────────┘
```

- `User.role` → `ROLE_USER` or `ROLE_ADMIN`
- `Vehicle.price` → `DECIMAL(10,2)`
- `Vehicle.quantity` → minimum 0

---

## Authentication Flow

```
User                    Frontend                  Backend                   PostgreSQL
 │                        │                         │                         │
 │  POST /api/auth/login  │                         │                         │
 │  {username, password}  │                         │                         │
 ├───────────────────────►│                         │                         │
 │                        │  POST /api/auth/login   │                         │
 │                        ├────────────────────────►│                         │
 │                        │                         │  SELECT * FROM users   │
 │                        │                         │  WHERE username = ?    │
 │                        │                         ├────────────────────────►│
 │                        │                         │◄────────────────────────┤
 │                        │                         │                         │
 │                        │  BCrypt.matches(password, hash)                   │
 │                        │  JwtService.generateToken(user)                   │
 │                        │                         │                         │
 │                        │◄────────────────────────│                         │
 │                        │  {token, username, role}│                         │
 │◄───────────────────────┤                         │                         │
 │                        │                         │                         │
 │  Store token in        │                         │                         │
 │  localStorage          │                         │                         │
 │                        │                         │                         │
 │  GET /api/vehicles     │                         │                         │
 │  Authorization:        │                         │                         │
 │  Bearer <token>        │                         │                         │
 ├───────────────────────►│                         │                         │
 │                        │  JwtAuthFilter extracts │                         │
 │                        │  + validates token      │                         │
 │                        │  Sets SecurityContext   │                         │
 │                        ├────────────────────────►│                         │
 │                        │                         │  SELECT * FROM vehicles│
 │                        │                         ├────────────────────────►│
 │                        │                         │◄────────────────────────┤
 │                        │◄────────────────────────│                         │
 │◄───────────────────────┤                         │                         │
```

---

## Environment Variables

### `backend/.env`
```
DB_HOST=localhost
DB_PORT=5432
DB_NAME=cardealership
DB_USERNAME=postgres
DB_PASSWORD=postgres
JWT_SECRET=mySecretKeyForJWTTokenGenerationMustBeAtLeast256BitsLongForHS256Algorithm
JWT_EXPIRATION_MS=86400000
ADMIN_USERNAME=admin
ADMIN_EMAIL=admin@cardealership.com
ADMIN_PASSWORD=admin123
SERVER_PORT=8080
```

### `backend/src/main/resources/application.properties`
```properties
spring.application.name=car-dealership-api
server.port=${SERVER_PORT:8080}

# Database
spring.datasource.url=jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:cardealership}
spring.datasource.username=${DB_USERNAME:postgres}
spring.datasource.password=${DB_PASSWORD:postgres}

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.properties.hibernate.format_sql=true

# JWT
app.jwt.secret=${JWT_SECRET}
app.jwt.expiration-ms=${JWT_EXPIRATION_MS:86400000}

# Admin seed
app.admin.username=${ADMIN_USERNAME:admin}
app.admin.email=${ADMIN_EMAIL:admin@cardealership.com}
app.admin.password=${ADMIN_PASSWORD:admin123}

# Swagger
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
```

### `frontend/.env`
```
VITE_API_URL=http://localhost:8080/api
```

---

## Docker Compose

### `backend/docker-compose.yml`

```yaml
version: '3.8'

services:
  postgres:
    image: postgres:16-alpine
    container_name: cardealership-db
    environment:
      POSTGRES_DB: cardealership
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "5432:5432"
    volumes:
      - pgdata:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U postgres"]
      interval: 5s
      timeout: 5s
      retries: 5

  pgadmin:
    image: dpage/pgadmin4:latest
    container_name: cardealership-pgadmin
    environment:
      PGADMIN_DEFAULT_EMAIL: admin@admin.com
      PGADMIN_DEFAULT_PASSWORD: admin
    ports:
      - "5050:80"
    depends_on:
      - postgres

volumes:
  pgdata:
```

Usage:
```bash
docker compose up -d          # Start PostgreSQL + pgAdmin
docker compose down           # Stop everything
docker compose down -v        # Stop + delete data volume
```

---

## GitHub Actions CI

### `.github/workflows/ci.yml`

```yaml
name: CI

on:
  push:
    branches: [main]
  pull_request:
    branches: [main]

jobs:
  backend:
    runs-on: ubuntu-latest
    services:
      postgres:
        image: postgres:16
        env:
          POSTGRES_DB: cardealership
          POSTGRES_USER: postgres
          POSTGRES_PASSWORD: postgres
        ports:
          - 5432:5432
        options: >-
          --health-cmd pg_isready
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5

    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK 21
        uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'
          cache: maven

      - name: Build & Test
        run: mvn -B verify
        working-directory: backend

      - name: Checkstyle
        run: mvn checkstyle:check
        working-directory: backend

      - name: JaCoCo Coverage
        run: mvn jacoco:report
        working-directory: backend

      - name: Upload Coverage
        uses: actions/upload-artifact@v4
        with:
          name: jacoco-report
          path: backend/target/site/jacoco/

  frontend:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - name: Set up Node.js
        uses: actions/setup-node@v4
        with:
          node-version: '22'
          cache: npm
          cache-dependency-path: frontend/package-lock.json

      - name: Install Dependencies
        run: npm ci
        working-directory: frontend

      - name: Lint
        run: npm run lint
        working-directory: frontend

      - name: Test
        run: npm run test:coverage
        working-directory: frontend

      - name: Build
        run: npm run build
        working-directory: frontend
```

---

## README Badges

```markdown
![CI](https://github.com/veddarji/INqubits/actions/workflows/ci.yml/badge.svg)
![Java 21](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen)
![React](https://img.shields.io/badge/React-19-blue)
![Coverage](https://img.shields.io/badge/Coverage-85%25-success)
```

---

## Execution Plan — 12 Phases

---

## Phase 0: Git Repo Project Scaffolding & CI Setup

### GitHub Setup
1. Create public repo `INqubits` under `veddarji`
2. Enable **Issues** and **Projects** (kanban)
3. Create issues for all 13 features (see Issues table above)
4. Set up project board: **To Do** | **In Progress** | **Done**
5. Push existing scaffold to `main`

### Deliverables
- [ ] GitHub repo public
- [ ] Issues created and labeled
- [ ] Project board initialized
- [ ] `.gitignore` configured
- [ ] Initial push to `main`

### Commits
```
1. chore: Initialize repository with .gitignore and project structure
   Author: veddarji
```

---

## Phase 1: Backend — Entity & Repository Layer (TDD)

### Files (already exist — review and update if needed)
- `Vehicle.java` — JPA entity
- `User.java` — JPA entity (implements `UserDetails`)
- `Role.java` — Enum: `USER`, `ADMIN`
- `UserRepository.java` — `findByUsername`, `existsByUsername`, `existsByEmail`
- `VehicleRepository.java` — `searchVehicles` JPQL with optional params

### Add: Pagination Support in Repository

Update `VehicleRepository`:
- Both `findAll` and `searchVehicles` return `Page<Vehicle>` instead of `List<Vehicle>`
- Add: `findByMakeContainingIgnoreCase(String, Pageable)`
- Add: `findByCategoryIgnoreCase(String, Pageable)`
- `searchVehicles` already accepts `@Param` — add `Pageable` parameter

### Tests (written first)

**`UserRepositoryTest.java`** — `@DataJpaTest` with H2
| Test | Description |
|---|---|
| `saveUser_ShouldPersistWithGeneratedId` | CRUD |
| `findByUsername_ShouldReturnUser` | Query |
| `findByUsername_WithNonExistent_ShouldReturnEmpty` | Not found |
| `existsByUsername_ShouldReturnTrue` | Exists check |
| `existsByEmail_ShouldReturnTrue` | Exists check |
| `usernameUniqueness_ShouldThrowException` | Constraint |

**`VehicleRepositoryTest.java`** — `@DataJpaTest` with H2
| Test | Description |
|---|---|
| `saveVehicle_ShouldPersistWithGeneratedId` | CRUD |
| `findAll_ShouldReturnPagedResults` | Pagination |
| `searchVehicles_WithAllFilters_ShouldReturnFiltered` | Multi-filter |
| `searchVehicles_WithNoFilters_ShouldReturnAll` | Default |
| `searchVehicles_WithPriceRange_ShouldReturnFiltered` | Price filter |
| `searchVehicles_WithPagination_ShouldReturnCorrectPage` | Pagination |

### Commits
```
1. test: Add failing UserRepository tests for CRUD and queries
   Author: veddarji

2. feat: Implement User entity with validation annotations
   Co-authored-by: DeepSeek <AI@users.noreply.github.com>

3. test: Add failing VehicleRepository tests with pagination
   Author: veddarji

4. feat: Implement Vehicle entity and paginated repository
   Co-authored-by: DeepSeek <AI@users.noreply.github.com>

5. refactor: Add composite index on vehicle make/model
   Author: veddarji

Closes #2, #3, #11
```

---

## Phase 2: Backend — Security Layer (TDD)

### Files to Create

**`JwtService.java`**
- `generateToken(UserDetails)` → JWT string
- `extractUsername(String token)` → username
- `extractRole(String token)` → role
- `isTokenValid(String, UserDetails)` → boolean
- Algorithm: HMAC-SHA256, expiration from config

**`CustomUserDetailsService.java`**
- `loadUserByUsername(String)` → `UserDetails` from DB

**`JwtAuthenticationFilter.java`**
- Extends `OncePerRequestFilter`
- Extract Bearer token from `Authorization` header
- Validate via `JwtService`
- Set `SecurityContextHolder` if valid

**`SecurityConfig.java`**
- CSRF: disabled
- Session: STATELESS
- Public: `POST /api/auth/**`
- Admin: `POST /api/vehicles`, `PUT /api/vehicles/**`, `DELETE /api/vehicles/**`, `POST /api/vehicles/*/restock`
- Authenticated: all other `/api/**`
- `BCryptPasswordEncoder` bean
- `AuthenticationManager` bean

**`CorsConfig.java`**
- Allow origin: `http://localhost:5173`
- Allow methods: GET, POST, PUT, DELETE, OPTIONS
- Allow headers: Authorization, Content-Type
- Allow credentials: true

### Tests (written first)

**`JwtServiceTest.java`**
| Test | Description |
|---|---|
| `generateToken_ShouldCreateValidToken` | Token not null |
| `generateAndExtractUsername_ShouldMatch` | Round-trip |
| `extractRole_ShouldReturnCorrectRole` | Role claim |
| `isTokenValid_WithCorrectUser_ShouldReturnTrue` | Valid |
| `isTokenValid_WithWrongUser_ShouldReturnFalse` | Wrong user |
| `isTokenExpired_ShouldReturnTrueForExpiredToken` | Expired |
| `generateToken_WithNull_ShouldThrowException` | Null guard |

**`SecurityConfigTest.java`** — `@WebMvcTest` with security
| Test | Description |
|---|---|
| `publicEndpoint_ShouldBeAccessible` | POST /api/auth/login → 200 |
| `protectedEndpoint_WithoutToken_ShouldReturn401` | GET /api/vehicles → 401 |
| `adminEndpoint_WithUserRole_ShouldReturn403` | DELETE as USER → 403 |
| `adminEndpoint_WithAdminRole_ShouldReturn200` | DELETE as ADMIN → 200 |

### Commits
```
1. test: Add failing JwtService tests for token generation and validation
   Author: veddarji

2. feat: Implement JwtService with configurable expiration
   Co-authored-by: DeepSeek <AI@users.noreply.github.com>

3. refactor: Extract JWT secret and expiration to environment variables
   Author: veddarji

4. test: Add failing security config integration tests
   Author: veddarji

5. feat: Implement SecurityConfig with role-based access control
   Co-authored-by: DeepSeek <AI@users.noreply.github.com>

6. feat: Add JwtAuthenticationFilter and CustomUserDetailsService
   Co-authored-by: DeepSeek <AI@users.noreply.github.com>

7. feat: Add CORS configuration for frontend origin
   Author: veddarji

Closes #2
```

---

## Phase 3: Backend — Service Layer (TDD)

### DTOs (all records)

**Request DTOs:**
| DTO | Fields | Validation |
|---|---|---|
| `RegisterRequest` | `username`, `email`, `password` | `@NotBlank`, `@Email`, `@Size(min=8)` |
| `LoginRequest` | `username`, `password` | `@NotBlank` |
| `VehicleRequest` | `make`, `model`, `category`, `price`, `quantity` | `@NotBlank`, `@Positive`, `@PositiveOrZero` |
| `PurchaseRequest` | `quantity` | Default 1, `@Positive` |

**Response DTOs:**
| DTO | Fields |
|---|---|
| `AuthResponse` | `token`, `username`, `role` |
| `VehicleResponse` | `id`, `make`, `model`, `category`, `price`, `quantity` |
| `PagedResponse` | `content`, `page`, `size`, `totalElements`, `totalPages`, `last` |
| `ErrorResponse` | `status`, `message`, `timestamp`, `errors` (optional map) |

### Services

**`AuthService.java`**
| Method | Validation | Exception |
|---|---|---|
| `register(RegisterRequest)` | Check dup username, dup email, encode password | `DuplicateResourceException` (409) |
| `login(LoginRequest)` | Authenticate via `AuthenticationManager` | `BadCredentialsException` (401) |

**`VehicleService.java`**
| Method | Description | Exception |
|---|---|---|
| `createVehicle(VehicleRequest)` | Map + save | — |
| `getAllVehicles(Pageable)` | Paginated list | — |
| `getVehicleById(Long)` | Fetch one | `ResourceNotFoundException` (404) |
| `searchVehicles(make, model, category, minPrice, maxPrice, Pageable)` | Dynamic filters + pagination + sort | — |
| `updateVehicle(Long, VehicleRequest)` | Fetch + update + save | `ResourceNotFoundException` (404) |
| `deleteVehicle(Long)` | Fetch + delete | `ResourceNotFoundException` (404) |

**`InventoryService.java`**
| Method | Validation | Exception |
|---|---|---|
| `purchaseVehicle(Long, int)` | Qty > 0, stock >= qty | `OutOfStockException` (400) |
| `restockVehicle(Long, int)` | Qty > 0 | `ResourceNotFoundException` (404) |

### Exceptions

| Exception | HTTP Status | When |
|---|---|---|
| `ResourceNotFoundException` | 404 | Entity not found |
| `OutOfStockException` | 400 | Insufficient stock |
| `DuplicateResourceException` | 409 | Duplicate username/email |

**`GlobalExceptionHandler.java`** — `@RestControllerAdvice`
- Maps all custom exceptions
- Handles `MethodArgumentNotValidException` → field-level error map
- Handles `AccessDeniedException` → 403
- Handles `AuthenticationException` → 401
- Generic fallback → 500

### Tests (written first)

**`AuthServiceTest.java`** — Mockito + JUnit 5
| Test | Description |
|---|---|
| `register_WithValidRequest_ShouldReturnAuthResponse` | Success |
| `register_WithDuplicateUsername_ShouldThrowException` | Duplicate |
| `register_WithDuplicateEmail_ShouldThrowException` | Duplicate |
| `register_WithShortPassword_ShouldThrowException` | Validation |
| `login_WithValidCredentials_ShouldReturnAuthResponse` | Success |
| `login_WithWrongPassword_ShouldThrowException` | Auth failure |
| `login_WithNonExistentUser_ShouldThrowException` | Not found |

**`VehicleServiceTest.java`** — Mockito + JUnit 5
| Test | Description |
|---|---|
| `createVehicle_WithValidData_ShouldReturnVehicleResponse` | Success |
| `createVehicle_WithBlankMake_ShouldThrowException` | Validation |
| `createVehicle_WithNegativePrice_ShouldThrowException` | Validation |
| `getAllVehicles_ShouldReturnPagedList` | Pagination |
| `getVehicleById_WithValidId_ShouldReturnVehicle` | Success |
| `getVehicleById_WithInvalidId_ShouldThrowException` | Not found |
| `searchVehicles_WithMakeFilter_ShouldReturnFilteredList` | Filter |
| `searchVehicles_WithPriceRange_ShouldReturnFilteredList` | Filter |
| `searchVehicles_WithPagination_ShouldReturnCorrectPage` | Pagination |
| `updateVehicle_WithValidData_ShouldReturnUpdatedVehicle` | Success |
| `updateVehicle_WithInvalidId_ShouldThrowException` | Not found |
| `deleteVehicle_WithValidId_ShouldDelete` | Success |
| `deleteVehicle_WithInvalidId_ShouldThrowException` | Not found |

**`InventoryServiceTest.java`** — Mockito + JUnit 5
| Test | Description |
|---|---|
| `purchaseVehicle_WithSufficientStock_ShouldDecreaseQuantity` | Success |
| `purchaseVehicle_WithInsufficientStock_ShouldThrowException` | Out of stock |
| `purchaseVehicle_WithZeroQuantity_ShouldThrowException` | Invalid |
| `purchaseVehicle_WithNonExistentId_ShouldThrowException` | Not found |
| `restockVehicle_WithValidData_ShouldIncreaseQuantity` | Success |
| `restockVehicle_WithNonExistentId_ShouldThrowException` | Not found |
| `restockVehicle_WithZeroQuantity_ShouldThrowException` | Invalid |

### Commits
```
1. test: Add failing AuthService tests for register/login
   Author: veddarji

2. feat: Implement AuthService with password hashing and JWT generation
   Co-authored-by: DeepSeek <AI@users.noreply.github.com>

3. refactor: Extract DTO mapping methods in AuthService
   Author: veddarji

4. test: Add failing VehicleService tests (CRUD, search, pagination)
   Author: veddarji

5. feat: Implement VehicleService with paginated search and CRUD
   Co-authored-by: DeepSeek <AI@users.noreply.github.com>

6. refactor: Add validation helper methods to VehicleService
   Author: veddarji

7. test: Add failing InventoryService tests (purchase/restock)
   Author: veddarji

8. feat: Implement InventoryService with stock validation
   Co-authored-by: DeepSeek <AI@users.noreply.github.com>

9. feat: Add GlobalExceptionHandler with field-level error support
   Co-authored-by: DeepSeek <AI@users.noreply.github.com>

Closes #3, #4, #5
```

---

## Phase 4: Backend — Controller Layer (TDD)

### Controllers

**`AuthController.java`**
| Endpoint | Method | Auth | Request | Response |
|---|---|---|---|---|
| `/api/auth/register` | POST | Public | `RegisterRequest` | `AuthResponse` (201) |
| `/api/auth/login` | POST | Public | `LoginRequest` | `AuthResponse` (200) |

**`VehicleController.java`**
| Endpoint | Method | Auth | Role | Query Params | Response |
|---|---|---|---|---|---|
| `/api/vehicles` | POST | JWT | ADMIN | — | `VehicleResponse` (201) |
| `/api/vehicles` | GET | JWT | ANY | `page`, `size`, `sort` | `PagedResponse` (200) |
| `/api/vehicles/search` | GET | JWT | ANY | `make`, `model`, `category`, `minPrice`, `maxPrice`, `page`, `size`, `sort` | `PagedResponse` (200) |
| `/api/vehicles/{id}` | GET | JWT | ANY | — | `VehicleResponse` (200) |
| `/api/vehicles/{id}` | PUT | JWT | ADMIN | — | `VehicleResponse` (200) |
| `/api/vehicles/{id}` | DELETE | JWT | ADMIN | — | 204 No Content |
| `/api/vehicles/{id}/purchase` | POST | JWT | ANY | — | `VehicleResponse` (200) |
| `/api/vehicles/{id}/restock` | POST | JWT | ADMIN | — | `VehicleResponse` (200) |

### Data Seeder

**`DataSeeder.java`** — `CommandLineRunner`
- Runs only on first startup (`UserRepository.count() == 0`)
- Creates admin user: `admin` / `admin123`
- Seeds 10-15 sample vehicles (Toyota, Honda, Ford, BMW, etc.)

### Tests (written first)

**`AuthControllerTest.java`** — `@WebMvcTest(AuthController.class)`
| Test | Description |
|---|---|
| `register_WithValidData_ShouldReturn201` | Success |
| `register_WithInvalidEmail_ShouldReturn400` | Validation |
| `register_WithDuplicateUsername_ShouldReturn409` | Conflict |
| `login_WithValidCredentials_ShouldReturn200` | Success |
| `login_WithWrongPassword_ShouldReturn401` | Auth failure |
| `login_WithMissingFields_ShouldReturn400` | Validation |

**`VehicleControllerTest.java`** — `@WebMvcTest(VehicleController.class)`
| Test | Description |
|---|---|
| `createVehicle_AsAdmin_ShouldReturn201` | Admin success |
| `createVehicle_AsUser_ShouldReturn403` | Forbidden |
| `createVehicle_WithoutToken_ShouldReturn401` | Unauthenticated |
| `getAllVehicles_ShouldReturn200` | Authenticated |
| `getAllVehicles_WithPagination_ShouldReturnPagedResponse` | Pagination |
| `getVehicleById_WithValidId_ShouldReturn200` | Success |
| `getVehicleById_WithInvalidId_ShouldReturn404` | Not found |
| `searchVehicles_ShouldReturnFilteredResults` | Search |
| `searchVehicles_WithSorting_ShouldReturnSortedResults` | Sort |
| `updateVehicle_AsAdmin_ShouldReturn200` | Admin success |
| `updateVehicle_AsUser_ShouldReturn403` | Forbidden |
| `deleteVehicle_AsAdmin_ShouldReturn204` | Admin success |
| `deleteVehicle_AsUser_ShouldReturn403` | Forbidden |
| `purchaseVehicle_WithSufficientStock_ShouldReturn200` | Purchase success |
| `purchaseVehicle_WithInsufficientStock_ShouldReturn400` | Out of stock |
| `restockVehicle_AsAdmin_ShouldReturn200` | Admin success |
| `restockVehicle_AsUser_ShouldReturn403` | Forbidden |

### Commits
```
1. test: Add failing AuthController endpoint tests
   Author: veddarji

2. feat: Implement AuthController with register and login
   Co-authored-by: DeepSeek <AI@users.noreply.github.com>

3. test: Add failing VehicleController tests (CRUD, pagination, auth)
   Author: veddarji

4. feat: Implement VehicleController with paginated endpoints
   Co-authored-by: DeepSeek <AI@users.noreply.github.com>

5. refactor: Add request validation annotations to all DTOs
   Author: veddarji

6. feat: Add DataSeeder for admin user and sample vehicles
   Author: veddarji

Closes #1, #3, #4, #5
```

---

## Phase 5: Integration Tests with Testcontainers

### Setup

**Dependency in `pom.xml`:**
```xml
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>testcontainers</artifactId>
    <version>1.20.4</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>postgresql</artifactId>
    <version>1.20.4</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>1.20.4</version>
    <scope>test</scope>
</dependency>
```

**Test Application: `TestCarDealershipApplication.java`**
```java
@TestConfiguration
public class TestCarDealershipApplication {
    // Test-specific beans if needed
}
```

### Tests

**`AuthFlowIntegrationTest.java`** — `@SpringBootTest(webEnvironment = RANDOM_PORT)` + Testcontainers
| Test | Description |
|---|---|
| `registerUser_ThenLogin_ShouldReturnToken` | Full auth flow |
| `registerDuplicateUser_ShouldReturn409` | Duplicate via real DB |
| `loginWithWrongPassword_ShouldReturn401` | Wrong creds |

**`VehicleFlowIntegrationTest.java`** — `@SpringBootTest(webEnvironment = RANDOM_PORT)` + Testcontainers
| Test | Description |
|---|---|
| `adminCreatesVehicle_ThenUserViewsIt` | Full CRUD flow |
| `searchVehicles_WithMultipleFilters_ReturnsCorrectResults` | Search through real DB |
| `purchaseVehicle_DecreasesStock` | Inventory flow |
| `unauthorizedUserCannotDeleteVehicle` | Security through real DB |

**`InventoryFlowIntegrationTest.java`** — `@SpringBootTest(webEnvironment = RANDOM_PORT)` + Testcontainers
| Test | Description |
|---|---|
| `purchaseVehicle_WithSufficientStock_Success` | Purchase |
| `purchaseVehicle_WithInsufficientStock_Returns400` | Out of stock |
| `restockVehicle_AsAdmin_Success` | Restock |
| `restockVehicle_AsUser_Returns403` | Forbidden |

### H2 + Testcontainers Strategy

| Test Type | Database | When |
|---|---|---|
| Unit tests (`@WebMvcTest`, `@DataJpaTest`) | H2 in-memory | Fast, no Docker needed |
| Integration tests (`@SpringBootTest`) | Testcontainers PostgreSQL | Real DB, CI-compatible |

### Commits
```
1. chore: Add Testcontainers dependencies to pom.xml
   Author: veddarji

2. test: Add AuthFlow integration test with Testcontainers PostgreSQL
   Co-authored-by: DeepSeek <AI@users.noreply.github.com>

3. test: Add VehicleFlow integration test (CRUD, search, purchase)
   Co-authored-by: DeepSeek <AI@users.noreply.github.com>

4. test: Add InventoryFlow integration test (purchase, restock, auth)
   Author: veddarji

Closes #6
```

---

## Phase 6: API Documentation (Swagger/OpenAPI)

### Dependency

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.8.5</version>
</dependency>
```

### `OpenApiConfig.java`

```java
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Car Dealership API")
                .version("1.0.0")
                .description("Car Dealership Inventory System API")
                .contact(new Contact()
                    .name("veddarji")
                    .url("https://github.com/veddarji")))
            .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
            .components(new Components()
                .addSecuritySchemes("bearerAuth", new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("Enter JWT token")));
    }
}
```

### Controller Annotations

- `@Operation` on each endpoint
- `@ApiResponse` for response codes
- `@Parameter` for query params (search, pagination)

**Example:**
```java
@Operation(summary = "Search vehicles with filters and pagination")
@ApiResponse(responseCode = "200", description = "Paginated list of vehicles")
@ApiResponse(responseCode = "401", description = "Unauthorized")
@GetMapping("/search")
public ResponseEntity<PagedResponse<VehicleResponse>> searchVehicles(
    @Parameter(description = "Vehicle make (partial match)") @RequestParam(required = false) String make,
    @Parameter(description = "Vehicle model (partial match)") @RequestParam(required = false) String model,
    @Parameter(description = "Vehicle category") @RequestParam(required = false) String category,
    @Parameter(description = "Minimum price") @RequestParam(required = false) BigDecimal minPrice,
    @Parameter(description = "Maximum price") @RequestParam(required = false) BigDecimal maxPrice,
    @ParameterObject Pageable pageable) {
    // ...
}
```

### Access
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/api-docs`

### Commits
```
1. chore: Add springdoc-openapi dependency
   Author: veddarji

2. feat: Configure Swagger/OpenAPI with JWT bearer auth
   Co-authored-by: DeepSeek <AI@users.noreply.github.com>

3. docs: Annotate all controller endpoints with OpenAPI docs
   Author: veddarji

Closes #7
```

---

## Phase 7: Docker Compose & Environment Variables

### Files

**`backend/docker-compose.yml`** (shown above)
**`backend/.env.example`** (shown above)
**`backend/.env`** (gitignored, actual values)
**`frontend/.env.example`**

### Update `application.properties`

All hardcoded values replaced with `${...}` placeholders:
```properties
server.port=${SERVER_PORT:8080}
spring.datasource.url=jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:cardealership}
spring.datasource.username=${DB_USERNAME:postgres}
spring.datasource.password=${DB_PASSWORD:postgres}
app.jwt.secret=${JWT_SECRET}
app.jwt.expiration-ms=${JWT_EXPIRATION_MS:86400000}
app.admin.username=${ADMIN_USERNAME:admin}
app.admin.email=${ADMIN_EMAIL:admin@cardealership.com}
app.admin.password=${ADMIN_PASSWORD:admin123}
```

### Update `.gitignore`

Add:
```
backend/.env
frontend/.env
```

### Commits
```
1. chore: Add docker-compose.yml with PostgreSQL and pgAdmin
   Co-authored-by: DeepSeek <AI@users.noreply.github.com>

2. refactor: Externalize configuration to environment variables
   Author: veddarji

3. chore: Add .env.example files and update .gitignore
   Author: veddarji

Closes #8
```

---

## Phase 8: Frontend — Foundation

### Dependencies

```bash
npm install react-router-dom axios sonner
npm install -D vitest @testing-library/react @testing-library/jest-dom @testing-library/user-event jsdom
```

### Configuration Files

**`vite.config.js`** — with proxy to backend
**`vitest.config.js`** — jsdom environment, coverage thresholds (70%)
**`frontend/.env.example`** — `VITE_API_URL=http://localhost:8080/api`

### Core Infrastructure

**`api/axiosInstance.js`**
- Base URL from `VITE_API_URL`
- Request interceptor: attach JWT from localStorage
- Response interceptor: 401 → clear token, redirect to login

**`api/authApi.js`**
- `register(data)` → POST `/auth/register`
- `login(data)` → POST `/auth/login`

**`api/vehicleApi.js`**
- `getAllVehicles(params)` → GET `/vehicles?page=0&size=10&sort=price,asc`
- `getVehicleById(id)` → GET `/vehicles/{id}`
- `searchVehicles(params)` → GET `/vehicles/search?make=&model=&category=&minPrice=&maxPrice=&page=&size=&sort=`
- `createVehicle(data)` → POST `/vehicles`
- `updateVehicle(id, data)` → PUT `/vehicles/{id}`
- `deleteVehicle(id)` → DELETE `/vehicles/{id}`
- `purchaseVehicle(id, quantity)` → POST `/vehicles/{id}/purchase`
- `restockVehicle(id, quantity)` → POST `/vehicles/{id}/restock`

### Commits
```
1. chore: Install frontend dependencies and configure Vitest
   Author: veddarji

2. feat: Configure Vite proxy and environment variables
   Author: veddarji

3. feat: Implement axios instance with JWT interceptors
   Co-authored-by: DeepSeek <AI@users.noreply.github.com>

4. feat: Add frontend API modules (auth, vehicles)
   Co-authored-by: DeepSeek <AI@users.noreply.github.com>
```

---

## Phase 9: Frontend — Pages & Components (Feature-Based)

### Feature Structure

```
src/
├── features/
│   ├── auth/
│   │   ├── AuthContext.jsx         # Auth state + provider
│   │   ├── LoginPage.jsx           # Login form
│   │   ├── RegisterPage.jsx        # Register form
│   │   └── ProtectedRoute.jsx      # Auth guard component
│   ├── vehicles/
│   │   ├── VehicleCard.jsx         # Single vehicle card
│   │   ├── VehicleGrid.jsx         # Responsive grid of cards
│   │   ├── VehicleForm.jsx         # Add/Edit form (admin, modal)
│   │   ├── SearchBar.jsx           # Search/filter controls
│   │   └── DashboardPage.jsx       # Main vehicle listing page
│   └── admin/
│       ├── AdminPage.jsx           # Admin dashboard
│       └── AdminRoute.jsx          # Admin-only guard
├── shared/
│   └── components/
│       ├── Button.jsx              # Reusable button (primary/danger/ghost)
│       ├── Input.jsx               # Form input with label + error
│       ├── Modal.jsx               # Overlay modal
│       ├── Badge.jsx               # Status badge (success/danger/warning)
│       ├── Loader.jsx              # Spinner / skeleton
│       └── Navbar.jsx              # Top navigation
├── hooks/
│   ├── useAuth.js                  # Hook wrapping AuthContext
│   └── useVehicles.js              # Hook for vehicle data + actions
├── api/
│   ├── axiosInstance.js
│   ├── authApi.js
│   └── vehicleApi.js
```

### Design System (`index.css`)

- CSS custom properties for colors (dark theme)
- Dark backgrounds: `#0f0f0f`, `#1a1a1a`
- Amber accent: `#f59e0b` for CTAs, badges
- Green: `#10b981` for stock available
- Red: `#ef4444` for out of stock
- Glassmorphism cards: `backdrop-filter: blur(8px)` with semi-transparent backgrounds
- Responsive grid: 1 → 2 → 3 columns
- Google Font: Inter
- CSS transitions for animations (`transition: all 0.2s ease`)
- Loading skeletons with shimmer animation (CSS only)

### Component Details

**`Button.jsx`**
- Props: `variant` (primary/danger/ghost), `loading`, `disabled`, `fullWidth`, `size` (sm/md/lg)
- Loading state: spinner + "Loading..."

**`Input.jsx`**
- Props: `label`, `error`, `type`, `...inputProps`
- Error: red border + error message below

**`Modal.jsx`**
- Props: `isOpen`, `onClose`, `title`, `children`
- Overlay click + Escape to close
- CSS transition for appearance

**`Navbar.jsx`**
- Logo "Car Dealership" left
- Links: Dashboard, Admin (if admin), username + role badge, Logout
- Mobile hamburger menu

**`AuthContext.jsx`**
- `user`, `token`, `loading`, `isAdmin`
- `login()`, `register()`, `logout()`
- On mount: restore from localStorage
- Renders `<Loader />` while checking auth status

**`LoginPage.jsx`**
- Centered card layout
- Username + Password fields
- Validation: required fields, min length
- Error toast on failure (sonner)
- Redirect to `/dashboard` on success
- Link to Register

**`RegisterPage.jsx`**
- Username + Email + Password + Confirm Password
- Validation: email format, password match, min 8 chars
- Error toast on duplicate
- Redirect to `/dashboard` on success

**`DashboardPage.jsx`**
- Hero section: "Discover Your Next Drive" + SearchBar
- VehicleGrid below
- Loading: skeleton cards (3 shimmer placeholders)
- Empty: "No vehicles found" message
- Error: error message + retry button

**`SearchBar.jsx`**
- Make (text), Model (text), Category (dropdown), Min Price, Max Price
- Search + Clear buttons
- Sort dropdown: Price (low-high), Price (high-low), Name (A-Z)
- Responsive: inline row on desktop, stacked on mobile

**`VehicleCard.jsx`**
- Glass card: vehicle image placeholder (colored gradient based on category)
- Make + Model (heading), Category (badge), Price (formatted), Stock (green/red badge)
- Purchase button: disabled when qty=0, shows "Out of Stock"
- Admin mode: Edit (pencil) + Delete (trash) icons
- Delete: confirmation modal
- Toast notifications for purchase/delete/restock (sonner)

**`VehicleGrid.jsx`**
- CSS Grid: `grid-template-columns: repeat(auto-fill, minmax(300px, 1fr))`
- Props: `vehicles`, `loading`, `error`, `isAdmin`, `onPurchase`, `onDelete`
- Loading state: 3 skeleton cards
- Empty state: centered message + add button (admin)

**`VehicleForm.jsx`** (Modal)
- Fields: Make, Model, Category (dropdown: Sedan, SUV, Coupe, Truck), Price, Quantity
- Validation: all required, price > 0, qty >= 0
- Submit button with loading state
- Prefilled for Edit mode

**`AdminPage.jsx`**
- Same VehicleGrid but with admin controls on each card
- "Add Vehicle" floating button → opens VehicleForm modal
- "Restock" button on each card → small modal with quantity input
- Stock audit log (simple: current stock display)

### Routing (`App.jsx`)

```jsx
<AuthProvider>
  <BrowserRouter>
    <Toaster position="top-right" />   {/* sonner */}
    <Routes>
      <Route element={<Navbar />}>
        {/* Public */}
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />

        {/* Protected */}
        <Route element={<ProtectedRoute />}>
          <Route path="/dashboard" element={<DashboardPage />} />
        </Route>

        {/* Admin */}
        <Route element={<AdminRoute />}>
          <Route path="/admin" element={<AdminPage />} />
        </Route>

        {/* Default + 404 */}
        <Route path="/" element={<Navigate to="/dashboard" />} />
        <Route path="*" element={<NotFoundPage />} />
      </Route>
    </Routes>
  </BrowserRouter>
</AuthProvider>
```

### Commits
```
1. feat: Implement design system with dark theme CSS
   Author: veddarji

2. feat: Build shared UI components (Button, Input, Modal, Badge, Loader)
   Co-authored-by: DeepSeek <AI@users.noreply.github.com>

3. feat: Implement Navbar with responsive mobile menu
   Author: veddarji

4. feat: Implement AuthContext and authentication hooks
   Co-authored-by: DeepSeek <AI@users.noreply.github.com>

5. feat: Build Login and Register pages with validation
   Author: veddarji

6. feat: Implement VehicleCard, VehicleGrid with loading/empty states
   Co-authored-by: DeepSeek <AI@users.noreply.github.com>

7. feat: Add SearchBar with sort and pagination controls
   Author: veddarji

8. feat: Implement Dashboard page with toast notifications
   Co-authored-by: DeepSeek <AI@users.noreply.github.com>

9. feat: Implement Admin page with add/edit/delete/restock
   Co-authored-by: DeepSeek <AI@users.noreply.github.com>

10. feat: Add protected and admin route guards
    Author: veddarji

Closes #9, #10, #11
```

---

## Phase 10: Frontend — Tests

### Setup

**`vitest.config.js`** coverage thresholds:
```js
test: {
  coverage: {
    provider: 'v8',
    reporter: ['text', 'html', 'lcov'],
    thresholds: {
      statements: 70,
      branches: 70,
      functions: 70,
      lines: 70,
    },
  },
}
```

### Tests

**`VehicleCard.test.jsx`**
| Test | Description |
|---|---|
| renders make and model correctly | Display |
| shows formatted price as currency | "$27,400.00" |
| disables purchase button when quantity is 0 | Disabled + "Out of Stock" |
| enables purchase button when quantity > 0 | Enabled |
| shows admin buttons when isAdmin is true | Edit/Delete visible |
| hides admin buttons when isAdmin is false | Edit/Delete hidden |
| calls onPurchase when purchase button clicked | Click handler |
| calls onDelete when delete button clicked | Click handler |

**`SearchBar.test.jsx`**
| Test | Description |
|---|---|
| updates make field on user input | onChange works |
| calls onSearch with correct params on submit | Submit |
| clears all fields on clear button | Reset |
| shows category dropdown options | Dropdown |

**`ProtectedRoute.test.jsx`**
| Test | Description |
|---|---|
| redirects to /login when not authenticated | Redirect |
| renders children when authenticated | Content |

**`AdminRoute.test.jsx`**
| Test | Description |
|---|---|
| redirects to /dashboard when user is not admin | Redirect |
| renders children when user is admin | Content |

**`LoginPage.test.jsx`**
| Test | Description |
|---|---|
| renders login form with all fields | Display |
| shows validation errors on empty submit | Validation |
| calls login function on valid submit | Submission |
| displays error toast on failed login | Error state |
| navigates to register page on link click | Navigation |

**`VehicleGrid.test.jsx`**
| Test | Description |
|---|---|
| shows loading skeleton when loading | Loading |
| shows empty message when vehicles array is empty | Empty |
| renders vehicle cards when data provided | Success |
| shows error message on error | Error state |

**`VehicleForm.test.jsx`**
| Test | Description |
|---|---|
| renders form fields for vehicle creation | Display |
| pre-fills fields in edit mode | Edit mode |
| validates required fields | Validation |
| calls onSubmit with correct data | Submit |

### Commits
```
1. chore: Configure Vitest with coverage thresholds (70%)
   Author: veddarji

2. test: Add VehicleCard unit tests
   Author: veddarji

3. test: Add SearchBar and route guard tests
   Co-authored-by: DeepSeek <AI@users.noreply.github.com>

4. test: Add LoginPage and form validation tests
   Author: veddarji

5. test: Add VehicleGrid and VehicleForm tests
   Co-authored-by: DeepSeek <AI@users.noreply.github.com>

Closes #12
```

---

## Phase 11: Backend — JaCoCo Coverage Configuration & Checkstyle

### `pom.xml` — JaCoCo Plugin

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.12</version>
    <configuration>
        <excludes>
            <exclude>**/dto/**</exclude>
            <exclude>**/entity/**</exclude>
            <exclude>**/enums/**</exclude>
            <exclude>**/config/**</exclude>
            <exclude>**/CarDealershipApplication.class</exclude>
        </excludes>
        <rules>
            <rule>
                <element>BUNDLE</element>
                <limits>
                    <limit>
                        <counter>INSTRUCTION</counter>
                        <value>COVEREDRATIO</value>
                        <minimum>0.80</minimum>
                    </limit>
                    <limit>
                        <counter>BRANCH</counter>
                        <value>COVEREDRATIO</value>
                        <minimum>0.70</minimum>
                    </limit>
                </limits>
            </rule>
        </rules>
    </configuration>
    <executions>
        <execution>
            <goals><goal>prepare-agent</goal></goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>verify</phase>
            <goals><goal>report</goal></goals>
        </execution>
        <execution>
            <id>check</id>
            <phase>verify</phase>
            <goals><goal>check</goal></goals>
        </execution>
    </executions>
</plugin>
```

- Excludes: DTOs, entities, enums, config, main class
- Minimum instruction coverage: **80%**
- Minimum branch coverage: **70%**
- Report generated at: `target/site/jacoco/index.html`

### Checkstyle

**`backend/checkstyle.xml`** — Google Java Style with:
- Line length: 140
- No Javadoc on DTO records
- No Javadoc on getters/setters

### Commits
```
1. chore: Configure JaCoCo with 80% coverage threshold
   Author: veddarji

2. chore: Add Checkstyle configuration (Google Java Style)
   Author: veddarji
```

---

## Phase 12: Documentation & Final Delivery

### README.md — Complete Sections

1. **Title + Badges** — Build passing, coverage, Java 21, Spring Boot, React
2. **Project Description** — What is this project?
3. **Architecture Diagram** — ASCII/mermaid diagram
4. **ER Diagram** — User + Vehicle tables
5. **Authentication Flow** — Text description + sequence diagram
6. **Tech Stack** — Table with rationale
7. **Features** — Bulleted list
8. **Prerequisites** — Java 21, Maven, Node.js 18+, Docker
9. **Quick Start** — Single command to run everything
10. **Environment Variables** — Table of all configurable values
11. **API Documentation** — Link to Swagger UI
12. **API Endpoints** — Full table with methods, paths, auth, descriptions
13. **Running Tests** — Commands for backend and frontend
14. **Test Coverage** — Screenshots of JaCoCo + Vitest reports
15. **Screenshots** — Login, Dashboard, Admin panel, Mobile
16. **Project Structure** — Tree view
17. **Git Commit History** — `git log --oneline --graph` screenshot
18. **My AI Usage** — Tools, how AI was used, co-authorship details, reflection
19. **Future Improvements** — Deployment, CI/CD, rate limiting
20. **Known Limitations**
21. **License** — MIT

### Screenshots to Capture

1. Backend JaCoCo coverage report
2. Frontend Vitest coverage report
3. Swagger UI showing endpoints
4. Login page
5. Dashboard with vehicle grid
6. Admin panel with edit/delete controls
7. Mobile responsive view
8. GitHub Actions passing
9. Git log showing Red-Green-Refactor

### Commits
```
1. docs: Add comprehensive README with architecture, setup, and API docs
   Co-authored-by: DeepSeek <AI@users.noreply.github.com>

2. docs: Add "My AI Usage" section with co-author details and reflection
   Author: veddarji

3. docs: Add coverage reports and application screenshots
   Author: veddarji

4. chore: Final pre-submission verification
   Author: veddarji

Closes #13
```

---

## Commit Summary

| Phase | Author: veddarji | Co-Authored: DeepSeek | Total |
|---|---|---|---|
| 0 — Scaffolding | 1 | 0 | 1 |
| 1 — Entities & Repos | 3 | 2 | 5 |
| 2 — Security | 3 | 4 | 7 |
| 3 — Services | 5 | 4 | 9 |
| 4 — Controllers | 4 | 2 | 6 |
| 5 — Integration Tests | 2 | 2 | 4 |
| 6 — Swagger | 2 | 1 | 3 |
| 7 — Docker & Env Vars | 2 | 1 | 3 |
| 8 — Frontend Foundation | 2 | 2 | 4 |
| 9 — Frontend Pages | 5 | 5 | 10 |
| 10 — Frontend Tests | 3 | 2 | 5 |
| 11 — Coverage & Checkstyle | 2 | 0 | 2 |
| 12 — Documentation | 3 | 1 | 4 |
| **Total** | **37** | **26** | **63** |

**Ratio: 37 veddarji / 26 DeepSeek — demonstrates independent work while still using AI effectively.**

---

## Verification Checklist

### Backend
- [ ] `mvn clean compile` — builds without errors
- [ ] `mvn test` — all unit tests pass
- [ ] `mvn verify` — all tests + integration tests + JaCoCo + Checkstyle pass
- [ ] JaCoCo instruction coverage ≥ 80%
- [ ] JaCoCo branch coverage ≥ 70%
- [ ] Checkstyle passes with 0 violations
- [ ] PostgreSQL connection works via `docker compose up -d`
- [ ] Swagger UI available at `/swagger-ui.html`
- [ ] All endpoints return correct HTTP status codes

### Frontend
- [ ] `npm run build` — builds without errors
- [ ] `npm test` — all tests pass
- [ ] `npm run lint` — 0 lint errors
- [ ] Coverage ≥ 70%
- [ ] Login/Register flow works
- [ ] Dashboard loads vehicles
- [ ] Search filters work correctly
- [ ] Purchase decreases stock
- [ ] Admin can add/edit/delete/restock
- [ ] Responsive at 375px, 768px, 1024px, 1440px

### Git & GitHub
- [ ] All issues created and labeled
- [ ] Project board populated
- [ ] Commits reference issues: `Closes #N`
- [ ] Red-Green-Refactor visible in commit history
- [ ] All AI-assisted commits have `Co-authored-by: DeepSeek <AI@users.noreply.github.com>`
- [ ] README has all required sections
- [ ] Screenshots embedded
- [ ] CI passes on push to `main`

### Repository
- [ ] Public GitHub repo
- [ ] Badges in README (build, coverage, version)
- [ ] `.env.example` files included
- [ ] `.gitignore` properly configured
- [ ] License file (MIT)
