# Car Dealership Inventory System

![Build](https://img.shields.io/badge/build-passing-brightgreen)
![Coverage (Backend)](https://img.shields.io/badge/coverage%20(backend)-92%25%20instr%2C%2084%25%20branch-yellow)
![Coverage (Frontend)](https://img.shields.io/badge/coverage%20(frontend)-100%25-brightgreen)
![Java](https://img.shields.io/badge/Java-21-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4-green)
![React](https://img.shields.io/badge/React-19-61DAFB)

A full-stack Car Dealership Inventory System with JWT-authenticated role-based access, complete vehicle CRUD, smart search with pagination, purchase/restock inventory management, and a premium dark-themed responsive UI.

## Production Deployment

- **Frontend**: [https://car-dealership-3fqi.vercel.app/](https://car-dealership-3fqi.vercel.app/)
- **Backend API**: [http://carlelo-env.eba-xb8gjg6d.eu-north-1.elasticbeanstalk.com](http://carlelo-env.eba-xb8gjg6d.eu-north-1.elasticbeanstalk.com)
- **Database**: AWS RDS PostgreSQL 16

---

## Project Description

This application enables car dealership staff to manage their vehicle inventory through a web interface. Users can browse vehicles, search by make/model/category/price, purchase vehicles (decrementing stock), and view vehicle details. Administrators can additionally create, edit, delete vehicles, and restock inventory. The system is built following TDD (Red-Green-Refactor) with AI co-author trailers on every commit.

---

## Architecture Diagram

```
┌───────────────────────────────────────────────────────────┐
│                    Browser (React SPA)                     │
│  ┌─────────────────────────────────────────────────────┐  │
│  │  Vite Dev Server (port 5173) /api → backend:5000     │  │
│  │  React Router 7 │ Axios │ AuthContext │ sonner      │  │
│  └──────┬──────────────────────────────────────────────┘  │
└─────────┼─────────────────────────────────────────────────┘
          │ HTTP (JWT Bearer Token)
          ▼
┌───────────────────────────────────────────────────────────┐
│                 Spring Boot API (port 5000)                │
│  ┌─────────┐  ┌──────────┐  ┌──────────┐  ┌───────────┐  │
│  │ Security │  │Controller│  │ Service  │  │   JPA     │  │
│  │  Filter  │──▶  层    │──▶ 层     │──▶Repository│  │
│  │ JWT Auth │  │ REST     │  │ Business │  │  Spring   │  │
│  │ Role     │  │ Endpoints│  │  Logic   │  │ Data JPA  │  │
│  └─────────┘  └──────────┘  └──────────┘  └─────┬─────┘  │
│                                                  │        │
│  ┌───────────────────────────────────────────────┘        │
│  │  HikariCP Connection Pool (min=10, max=50)             │
│  │  Spring Cache (ConcurrentMapCacheManager)              │
│  └───────────────────────────────────────────────────────┘│
└──────────────────────────┬────────────────────────────────┘
                           │ JDBC
                           ▼
              ┌──────────────────────┐
              │    PostgreSQL 16      │
              │  Database: cardeal.   │
              │  - users              │
              │  - vehicles           │
              │  - categories         │
              │  - purchases          │
              │  - inventory_txn      │
              │  + pg_trgm GIN idx    │
              └──────────────────────┘
```

---

## Entity-Relationship Diagram

```
┌─────────────────┐       ┌──────────────────┐
│     users        │       │    vehicles       │
├─────────────────┤       ├──────────────────┤
│ id (PK, BIGINT) │       │ id (PK, BIGINT)  │
│ username (UNIQ) │       │ make (VARCHAR)    │
│ email (UNIQ)    │       │ model (VARCHAR)   │
│ password (BCRY) │       │ year (INT)        │
│ role (ENUM)     │       │ price (DECIMAL)   │
└────────┬────────┘       │ quantity (INT)    │
         │                │ category_id (FK)  │
         │                │ description (TXT) │
         │                └─────────┬─────────┘
         │                          │
         ▼                          ▼
┌──────────────────┐     ┌─────────────────────────┐
│    purchases      │     │ inventory_transactions   │
├──────────────────┤     ├─────────────────────────┤
│ id (PK, BIGINT)  │     │ id (PK, BIGINT)         │
│ user_id (FK) ────┼──┐  │ vehicle_id (FK) ────────┤
│ vehicle_id (FK) ─┼──┼──┤ user_id (FK)            │
│ quantity (INT)   │  │  │ type (PURCHASE/RESTOCK)  │
│ unit_price (DEC) │  │  │ quantity_change (INT)    │
│ total_price(DEC) │  │  │ previous_qty (INT)       │
│ purchased_at(TM) │  │  │ new_quantity (INT)       │
└──────────────────┘  │  │ created_at (TIMESTAMP)   │
                      │  └─────────────────────────┘
                      │
                      └──────────────────┐
                                         ▼
                                ┌────────────────┐
                                │   categories   │
                                ├────────────────┤
                                │ id (PK, BIGINT)│
                                │ name (UNIQ)    │
                                │ description    │
                                └────────────────┘
```

---

## Authentication Flow

The application uses **JWT (JSON Web Token)** for stateless authentication. Upon login, the server returns a signed JWT that the client stores in `localStorage` and sends as a `Bearer` token in the `Authorization` header for all subsequent requests.

```
┌─────────┐          ┌───────────────┐          ┌───────────┐
│  Client  │          │  Spring Boot  │          │PostgreSQL │
└────┬─────┘          └───────┬───────┘          └─────┬─────┘
     │                        │                        │
     │  POST /api/auth/register                         │
     │  {username, email, password}                     │
     │──────────────────────►│                        │
     │                        │  INSERT INTO users      │
     │                        │────────────────────────►│
     │                        │         OK              │
     │                        │◄────────────────────────│
     │  {token, username,     │                        │
     │   email, role}         │                        │
     │◄──────────────────────│                        │
     │                        │                        │
     │  POST /api/auth/login                            │
     │  {username, password}                            │
     │──────────────────────►│                        │
     │                        │  SELECT * FROM users    │
     │                        │  WHERE username=?       │
     │                        │────────────────────────►│
     │                        │         user row        │
     │                        │◄────────────────────────│
     │                        │  Validate password      │
     │                        │  Generate JWT           │
     │  {token, username,     │                        │
     │   email, role, expiresIn}                       │
     │◄──────────────────────│                        │
     │                        │                        │
     │  GET /api/vehicles (Authorization: Bearer <jwt>) │
     │──────────────────────►│                        │
     │                        │  JwtAuthFilter validates│
     │                        │  token, sets Security-  │
     │                        │  Context                │
     │                        │  SELECT * FROM vehicles │
     │                        │────────────────────────►│
     │                        │       vehicle rows      │
     │                        │◄────────────────────────│
     │  {vehicles,            │                        │
     │   pagination}          │                        │
     │◄──────────────────────│                        │
┌────┴─────┐          ┌───────┴───────┐          ┌─────┴─────┐
│  Client  │          │  Spring Boot  │          │PostgreSQL │
└─────────┘          └───────────────┘          └───────────┘
```

### Sequence Diagram (Login)

```
Client                    AuthController              AuthService          UserRepository        JwtService
  │                            │                         │                     │                   │
  │──POST /api/auth/login─────►│                         │                     │                   │
  │   {username, password}     │                         │                     │                   │
  │                            │──authenticate()────────►│                     │                   │
  │                            │                         │──findByUsername()──►│                   │
  │                            │                         │◄──User entity──────│                   │
  │                            │                         │                     │                   │
  │                            │                         │──matches(password)──────────────────────►│
  │                            │                         │◄──valid──────────────────────────────────│
  │                            │                         │                     │                   │
  │                            │                         │──generateToken()───────────────────────►│
  │                            │                         │◄──JWT string────────────────────────────│
  │                            │◄──AuthResponse {token}─│                     │                   │
  │◄──200 OK {token, ...}─────│                         │                     │                   │
```

---

## Tech Stack

| Layer | Technology | Rationale |
|-------|-----------|-----------|
| **Language** | Java 21 | LTS, virtual threads preview, pattern matching, records |
| **Backend Framework** | Spring Boot 3.4 | Auto-configuration, mature ecosystem, embedded Tomcat |
| **Security** | Spring Security 6 + jjwt 0.12 | Industry-standard auth; jjwt is lightweight and well-maintained |
| **Database** | PostgreSQL 16 | Robust, pg_trgm for fuzzy text search, production-grade |
| **ORM** | Spring Data JPA + Hibernate 6 | Declarative repositories, pessimistic locking, batch support |
| **Connection Pool** | HikariCP | Fastest pool, built into Spring Boot; tuned for 10-50 connections |
| **Caching** | ConcurrentMapCacheManager | Lightweight in-process cache (Redis unavailable); `@Cacheable`/`@CacheEvict` on VehicleService |
| **API Documentation** | springdoc-openapi (Swagger UI) | Auto-generates OpenAPI 3.0 spec from annotations; JWT bearer support |
| **Testing (BE)** | JUnit 5 + Mockito + H2 (dev) + Testcontainers (CI) | TDD with mocks for unit tests; H2 for local; PostgreSQL container in CI |
| **Coverage (BE)** | JaCoCo 0.8.12 | 80% instruction / 70% branch minimum; excludes DTOs/entities/enums/config |
| **Linting (BE)** | Checkstyle (Google Java Style) | Enforces consistent code style; line length 140; validates on `mvn verify` |
| **Frontend** | React 19 + Vite 6 | Latest React with Concurrent features; fast HMR via Vite |
| **Routing** | React Router 7 | Nested routes, loaders, protected route guards |
| **HTTP Client** | Axios 1.7 | Interceptors for JWT injection, error handling |
| **Notifications** | sonner | Lightweight toast library for success/error feedback |
| **Testing (FE)** | Vitest 2 + React Testing Library 16 | Fast, Vite-native testing; RTL for component behavior tests |
| **Coverage (FE)** | @vitest/coverage-v8 | 100% threshold enforced on statements/branches/functions/lines |
| **CI/CD** | GitHub Actions | 6-job pipeline (lint, test, build, CodeQL, dependency review) |
| **Containerization** | Docker Compose | PostgreSQL 16 + pgAdmin; all config via `.env` |

---

## Features

- **User Authentication**: Register and login with JWT token-based authentication
- **Role-Based Access**: `USER` (browse, purchase) and `ADMIN` (full CRUD, restock) roles
- **Vehicle CRUD**: Add, view, update, and delete vehicles with full validation
- **Smart Search**: Filter by make, model, category, year, and price range with paginated results
- **Inventory Management**: Purchase vehicles (decrements stock) and restock (admin only)
- **Vehicle Detail Page**: View full details, purchase, restock (admin), delete (admin)
- **Responsive UI**: Premium dark theme with glassmorphism cards, amber accent, Inter font
- **Shimmer Skeletons**: Loading placeholders with animated shimmer effect
- **Mobile Hamburger Menu**: Collapsible navigation for small screens
- **Comprehensive Testing**: TDD approach; 118 backend tests, 99 frontend tests
- **Performance Tuning**: pg_trgm GIN indexes, HikariCP pool tuning, Hibernate batch inserts, Spring caching
- **Swagger UI**: Interactive API documentation at `/swagger-ui.html`
- **Docker Compose**: One-command infrastructure setup with PostgreSQL 16

---

## Prerequisites

| Tool | Version | Purpose |
|------|---------|---------|
| Java | 21+ (OpenJDK Temurin) | Backend runtime |
| Maven | 3.9+ | Backend build & test |
| Node.js | 18+ (22 recommended) | Frontend runtime |
| npm | 10+ | Frontend dependency management |
| Docker Desktop | Latest | Docker Compose for PostgreSQL (optional) |
| PostgreSQL | 16 (or use Docker) | Database (can use Docker instead of local install) |

---

## Quick Start

### Option 1: Docker Compose (recommended)

```bash
# 1. Clone the repository
git clone https://github.com/veddarji/car-dealership.git
cd INqubits

# 2. Start PostgreSQL via Docker
docker compose -f backend/docker-compose.yml up -d

# 3. Start the backend
cd backend
mvn spring-boot:run

# 4. In a new terminal, start the frontend
cd frontend
npm install
npm run dev
```

The API will be at `http://localhost:5000`, the frontend at `http://localhost:5173`, and Swagger UI at `http://localhost:5000/swagger-ui.html`.

### Option 2: Local PostgreSQL

```bash
# 1. Create the database
psql -U postgres -c "CREATE DATABASE cardealership;"

# 2. Start backend
cd backend
mvn spring-boot:run

# 3. Start frontend
cd frontend
npm install
npm run dev
```

### Default Credentials

| Role | Username | Password |
|------|----------|----------|
| ADMIN | `admin` | `admin123` |

A regular user can be registered via the frontend's Register page or via `POST /api/auth/register`.

---

## Environment Variables

All configuration is externalized in `backend/src/main/resources/application.properties` with sensible defaults.

| Variable | Default | Description |
|----------|---------|-------------|
| `SERVER_PORT` | `5000` | API server port |
| `DB_HOST` | `localhost` | PostgreSQL host |
| `DB_PORT` | `5432` | PostgreSQL port |
| `DB_NAME` | `cardealership` | Database name |
| `DB_USERNAME` | `postgres` | Database user |
| `DB_PASSWORD` | `postgres` | Database password |
| `SHOW_SQL` | `false` | Log Hibernate SQL |
| `JWT_SECRET` | *(long base64 key)* | HMAC-SHA256 key for JWT signing |
| `JWT_EXPIRATION_MS` | `86400000` | JWT token validity (24h) |
| `ADMIN_USERNAME` | `admin` | Seeded admin username |
| `ADMIN_EMAIL` | `admin@cardealership.com` | Seeded admin email |
| `ADMIN_PASSWORD` | `admin123` | Seeded admin password |
| `HIKARI_MIN_IDLE` | `10` | Minimum connection pool size |
| `HIKARI_MAX_POOL` | `50` | Maximum connection pool size |
| `HIKARI_IDLE_TIMEOUT` | `300000` | Idle timeout (ms) |
| `HIKARI_MAX_LIFETIME` | `600000` | Max connection lifetime (ms) |
| `HIKARI_CONN_TIMEOUT` | `30000` | Connection timeout (ms) |
| `HIKARI_LEAK_DETECT` | `60000` | Leak detection threshold (ms) |

For Docker Compose, copy `backend/.env.example` to `backend/.env` and customize.

---

## API Documentation

Interactive Swagger UI is available when the backend is running:

> **Production**: [http://carlelo-env.eba-xb8gjg6d.eu-north-1.elasticbeanstalk.com/swagger-ui.html](http://carlelo-env.eba-xb8gjg6d.eu-north-1.elasticbeanstalk.com/swagger-ui.html)
> **Local**: `http://localhost:5000/swagger-ui.html`

The OpenAPI spec is generated at `/v3/api-docs` and includes JWT Bearer authentication — click the **Authorize** button in Swagger UI to paste your token.

---

## API Endpoints

### Authentication

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| `POST` | `/api/auth/register` | Public | Register a new user |
| `POST` | `/api/auth/login` | Public | Login, returns JWT token |

### Vehicles

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| `GET` | `/api/vehicles` | JWT | List all vehicles (paginated) |
| `GET` | `/api/vehicles/search` | JWT | Search/filter vehicles (make, model, category, price range) |
| `GET` | `/api/vehicles/{id}` | JWT | Get vehicle by ID |
| `POST` | `/api/vehicles` | JWT | Create a new vehicle |
| `PUT` | `/api/vehicles/{id}` | JWT | Update an existing vehicle |
| `DELETE` | `/api/vehicles/{id}` | ADMIN | Delete a vehicle (cascades to purchases + transactions) |

### Inventory

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| `POST` | `/api/vehicles/{id}/purchase` | JWT | Purchase a vehicle (decrements quantity) |
| `POST` | `/api/vehicles/{id}/restock` | ADMIN | Restock a vehicle (increments quantity) |

### Categories

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| `GET` | `/api/categories` | JWT | List all categories |

### Query Parameters for Search

| Parameter | Type | Example |
|-----------|------|---------|
| `make` | String | `Toyota` |
| `model` | String | `Camry` |
| `category` | String | `SUV` |
| `minPrice` | BigDecimal | `20000` |
| `maxPrice` | BigDecimal | `50000` |
| `page` | Integer (default 0) | `0` |
| `size` | Integer (default 10) | `10` |
| `sort` | String | `price,asc` or `make,desc` |

### Sample Requests

```bash
# Register
curl -X POST http://localhost:5000/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"jdoe","email":"j@doe.com","password":"secret123"}'

# Login
curl -X POST http://localhost:5000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# Get vehicles (with token)
TOKEN="<your-jwt-token>"
curl http://localhost:5000/api/vehicles?page=0&size=5 \
  -H "Authorization: Bearer $TOKEN"

# Search
curl "http://localhost:5000/api/vehicles/search?make=Toyota&minPrice=10000&maxPrice=50000" \
  -H "Authorization: Bearer $TOKEN"

# Create vehicle (ADMIN only)
curl -X POST http://localhost:5000/api/vehicles \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"make":"Honda","model":"Civic","year":2024,"price":28000,"quantity":5,"category":"Sedan"}'

# Purchase (USER)
curl -X POST http://localhost:5000/api/vehicles/1/purchase \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"quantity":1}'

# Restock (ADMIN only)
curl -X POST http://localhost:5000/api/vehicles/1/restock \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"quantity":10}'
```

---

## Running Tests

### Backend

```bash
# Unit tests only
cd backend
mvn test

# Full verification (tests + JaCoCo coverage + Checkstyle)
mvn clean verify

# Generate JaCoCo report only
mvn jacoco:report
# Report: backend/target/site/jacoco/index.html

# Quick smoke test (PowerShell)
.\test-all.ps1
```

### Frontend

```bash
cd frontend

# Run all tests
npm test

# Run tests with coverage
npm run test:coverage
# Report: frontend/coverage/lcov-report/index.html

# Run in watch mode
npm run test:watch
```

---

## Test Coverage

### Backend — JaCoCo Report (92% instruction, 84% branch)

```
┌────────────────────────────┬────────────┬──────────┬────────────┐
│ Package                    │ Instr Cov  │ Br. Cov  │ Lines      │
├────────────────────────────┼────────────┼──────────┼────────────┤
│ com.cardealership.controller│  79%      │   n/a    │ 33/40      │
│ com.cardealership.service  │  95%      │   92%    │ 145/147    │
│ com.cardealership.repository│  86%      │   90%    │ 15/17      │
│ com.cardealership.exception │  91%      │  100%    │ 23/25      │
│ com.cardealership.security  │ 100%      │   64%    │ 54/54      │
├────────────────────────────┼────────────┼──────────┼────────────┤
│ Total                      │  92%      │   84%    │ 270/283    │
└────────────────────────────┴────────────┴──────────┴────────────┘
```

**22 test classes — 118 tests — all passing**

| Test Class | Tests | Status |
|-----------|-------|--------|
| `AuthServiceTest` | 7 | ✅ |
| `VehicleServiceTest` | 13 | ✅ |
| `InventoryServiceTest` | 7 | ✅ |
| `AuthControllerTest` | 6 | ✅ |
| `VehicleControllerTest` | 17 | ✅ |
| `UserRepositoryTest` | 7 | ✅ |
| `VehicleRepositoryTest` | 7 | ✅ |
| `CategoryRepositoryTest` | 4 | ✅ |
| `CategoryServiceTest` | 5 | ✅ |
| `PurchaseRepositoryTest` | 3 | ✅ |
| `PurchaseServiceTest` | 1 | ✅ |
| `InventoryTransactionRepositoryTest` | 2 | ✅ |
| `JwtServiceTest` | 7 | ✅ |
| `CustomUserDetailsServiceTest` | 2 | ✅ |
| `SecurityConfigTest` | 4 | ✅ |
| `SwaggerDocTest` | 10 | ✅ |
| `DevProfileTest` | 1 | ✅ |
| `DockerConfigTest` | 1 | ✅ |
| `AuthFlowIT` | 4 | ✅ |
| `VehicleFlowIT` | 6 | ✅ |
| `InventoryFlowIT` | 4 | ✅ |
| **Total** | **118** | **✅** |

### Frontend — Vitest Coverage (100% all categories)

```
┌───────────────────────────┬─────────┬──────────┬─────────┬─────────┐
│ File                      │ Stmts   │ Branch   │ Funcs   │ Lines   │
├───────────────────────────┼─────────┼──────────┼─────────┼─────────┤
│ All files                 │  100%   │  100%    │  100%   │  100%   │
│ api/authApi.js            │  100%   │  100%    │  100%   │  100%   │
│ api/vehicleApi.js         │  100%   │  100%    │  100%   │  100%   │
│ features/admin/AdminRoute │  100%   │  100%    │  100%   │  100%   │
│ features/auth/LoginPage   │  100%   │  100%    │  100%   │  100%   │
│ features/auth/ProtectedRt │  100%   │  100%    │  100%   │  100%   │
│ features/vehicles/Search  │  100%   │  100%    │  100%   │  100%   │
│ features/vehicles/Card    │  100%   │  100%    │  100%   │  100%   │
│ features/vehicles/Detail  │  100%   │  100%    │  100%   │  100%   │
│ features/vehicles/Form    │  100%   │  100%    │  100%   │  100%   │
│ features/vehicles/Grid    │  100%   │  100%    │  100%   │  100%   │
│ shared/components/Badge   │  100%   │  100%    │  100%   │  100%   │
│ shared/components/Button  │  100%   │  100%    │  100%   │  100%   │
│ shared/components/Input   │  100%   │  100%    │  100%   │  100%   │
│ shared/components/Loader  │  100%   │  100%    │  100%   │  100%   │
└───────────────────────────┴─────────┴──────────┴─────────┴─────────┘
```

**13 test files — 99 tests — all passing**

---

## Screenshots

### Application Pages

| Page | Screenshot |
|------|-----------|
| **Login** | ![Login Page](https://raw.githubusercontent.com/veddarji/car-dealership/fix/auth-logging-500/screenshots/login.png) |
| **Register** | ![Register Page](https://raw.githubusercontent.com/veddarji/car-dealership/fix/auth-logging-500/screenshots/register.png) |
| **Dashboard** | ![Dashboard](https://raw.githubusercontent.com/veddarji/car-dealership/fix/auth-logging-500/screenshots/dashboard.png) |
| **Vehicle Detail** | ![Vehicle Detail](https://raw.githubusercontent.com/veddarji/car-dealership/fix/auth-logging-500/screenshots/vehicle-detail.png) |
| **Admin Panel** | ![Admin Panel](https://raw.githubusercontent.com/veddarji/car-dealership/fix/auth-logging-500/screenshots/admin.png) |
| **Mobile Dashboard** | ![Mobile Dashboard](https://raw.githubusercontent.com/veddarji/car-dealership/fix/auth-logging-500/screenshots/dashboard-mobile.png) |

### Backend JaCoCo Coverage Report

![JaCoCo Coverage](https://raw.githubusercontent.com/veddarji/car-dealership/fix/auth-logging-500/screenshots/coverage-backend.png)

The full JaCoCo HTML report is available at `backend/target/site/jacoco/index.html` after running `mvn jacoco:report`. Current coverage: **92% instruction, 85% branch** across 17 classes.

### Frontend Vitest Coverage Report

![Vitest Coverage](https://raw.githubusercontent.com/veddarji/car-dealership/fix/auth-logging-500/screenshots/coverage-frontend.png)

The full Vitest HTML coverage report is available at `frontend/coverage/lcov-report/index.html` after running `npm run test:coverage`. Current results: **99 tests in 13 files, all passing**.

### Swagger UI

![Swagger UI](https://raw.githubusercontent.com/veddarji/car-dealership/fix/auth-logging-500/screenshots/swagger.png)

Open `http://localhost:8080/swagger-ui.html` in a browser while the backend is running to see the interactive API documentation with all endpoints and the Authorize button for JWT.

---

## Project Structure

```
INqubits/
├── backend/                          # Spring Boot REST API
│   ├── src/
│   │   ├── main/java/com/cardealership/
│   │   │   ├── CarDealershipApplication.java    # Entry point
│   │   │   ├── config/
│   │   │   │   ├── CacheConfig.java             # ConcurrentMapCacheManager
│   │   │   │   ├── CorsConfig.java              # CORS (dev + Vercel production)
│   │   │   │   ├── DatabaseIndexInitializer.java # pg_trgm + B-tree idx
│   │   │   │   ├── DataSeeder.java              # Admin user + 10 sample vehicles
│   │   │   │   ├── OpenApiConfig.java           # Swagger with JWT bearer
│   │   │   │   └── SecurityConfig.java          # Spring Security filter chain
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java          # POST /auth/register, /auth/login
│   │   │   │   ├── CategoryController.java      # GET /categories
│   │   │   │   └── VehicleController.java       # Full CRUD + search + purchase + restock
│   │   │   ├── dto/                             # Request/Response DTOs
│   │   │   ├── entity/                          # JPA entities (User, Vehicle, Category, Purchase, InventoryTransaction)
│   │   │   ├── enums/                           # Role (USER, ADMIN)
│   │   │   ├── exception/                       # Custom exceptions + GlobalExceptionHandler
│   │   │   ├── repository/                      # Spring Data JPA repos + VehicleSpecifications
│   │   │   ├── security/
│   │   │   │   ├── CustomUserDetailsService.java
│   │   │   │   ├── JwtAuthenticationFilter.java # OncePerRequestFilter
│   │   │   │   └── JwtService.java              # Token generation/validation
│   │   │   └── service/
│   │   │       ├── AuthService.java             # Register/login logic
│   │   │       ├── CategoryService.java          # Category listing
│   │   │       ├── InventoryService.java         # Purchase/restock with optimistic locking
│   │   │       ├── PurchaseService.java          # Purchase history
│   │   │       └── VehicleService.java           # CRUD + search with caching
│   │   └── test/java/com/cardealership/         # 22 test classes, 118 tests
│   ├── pom.xml                                  # JaCoCo, Checkstyle, springdoc
│   ├── checkstyle.xml                           # Google Java Style + line 140
│   └── test-all.ps1                             # PowerShell E2E smoke test
│
├── frontend/                          # React 19 + Vite SPA
│   ├── src/
│   │   ├── api/
│   │   │   ├── axiosInstance.js       # Axios with JWT interceptor, empty baseURL (Vite proxy)
│   │   │   ├── authApi.js             # login(), register()
│   │   │   └── vehicleApi.js          # CRUD + search + purchase + restock
│   │   ├── features/
│   │   │   ├── admin/
│   │   │   │   ├── AdminPage.jsx      # Vehicle grid with create/edit/restock/delete
│   │   │   │   └── AdminRoute.jsx     # Admin role guard
│   │   │   ├── auth/
│   │   │   │   ├── AuthContext.jsx    # React context for auth state
│   │   │   │   ├── LoginPage.jsx      # Login form with toast errors
│   │   │   │   ├── ProtectedRoute.jsx # Auth guard (redirects to /login)
│   │   │   │   └── RegisterPage.jsx   # Registration form
│   │   │   └── vehicles/
│   │   │       ├── DashboardPage.jsx  # Main page with SearchBar + VehicleGrid
│   │   │       ├── SearchBar.jsx      # Search filters (make, model, category, price)
│   │   │       ├── VehicleCard.jsx    # Individual vehicle card with Details link
│   │   │       ├── VehicleDetailPage.jsx # Full detail view + purchase/restock/delete
│   │   │       ├── VehicleForm.jsx    # Create/edit vehicle form
│   │   │       └── VehicleGrid.jsx    # Grid layout with shimmer skeletons
│   │   ├── hooks/
│   │   │   ├── useAuth.js             # Auth hook with login/logout/register
│   │   │   └── useVehicles.js         # Vehicle data fetching hook
│   │   ├── shared/components/
│   │   │   ├── Badge.jsx              # Badge with variant support
│   │   │   ├── Button.jsx             # Button with variant/size/loading
│   │   │   ├── Input.jsx              # Styled input with label/error
│   │   │   ├── Loader.jsx             # Loading spinner
│   │   │   ├── Modal.jsx              # Reusable modal with backdrop
│   │   │   ├── Navbar.jsx             # Responsive nav with hamburger menu
│   │   │   └── Pagination.jsx         # Page navigation controls
│   │   ├── App.jsx                    # React Router setup
│   │   ├── main.jsx                   # Entry point with AuthProvider
│   │   └── index.css                  # Design system (dark theme, glassmorphism)
│   └── vitest.config.js               # 70% coverage threshold (currently at 100%)
│
├── .github/workflows/ci.yml           # 6-job CI pipeline
├── docker-compose.yml                 # (in backend/) PostgreSQL 16 + API
└── README.md                          # This file
```

---

## Git Commit History

```
* d05dfa5 Phase 11 & Phase 10: JaCoCo, Checkstyle, backend fixes, vehicle detail page, 100% frontend coverage (#28)
* 4f20b5c Phase 10: Frontend component tests with coverage thresholds (#27)
* 67af783 Phase 9: Frontend feature-based architecture with design system (#26)
* c7c2867 Phase 5 + Phase 8: Testcontainers and complete React frontend (#25)
* 4c088dd Phase 8.1: Database performance, caching, and connection pool tuning (#24)
* 4df6469 Phase 8: Dev profile, purchase quantity, test script, README updates (#23)
* 814aa04 Phase 8: H2 Dev Profile for local development (#22)
* c91eb88 Phase 7: Docker Compose & Environment Variables (#21)
* e2d1334 Phase 6: API Documentation (Swagger/OpenAPI) (#20)
* 05c14df Phase 5: Integration Tests with Testcontainers (#19)
* 9a2536d fix: CI failures — JaCoCo enum, frontend vitest config and placeholder test
* 90eaa3f Phase 4: Controller Layer (Auth, Vehicle, DataSeeder)
* 36220a7 chore: Rename repo to autovault, upgrade CI with coverage gate and test bots
* c620cca Phase 3: Service Layer (AuthService, VehicleService, InventoryService)
* fec88c2 Phase 2: Security Layer (JWT auth, role-based access, CORS)
* 0b57693 ci: Fix GitHub Actions workflow for development phase
* 1b9ecaa feat: Implement paginated VehicleRepository with dynamic search
* a803581 test: Add failing repository tests for User and Vehicle entities
* 181d30c ci: Add GitHub Actions workflow
* 0f52546 chore: Initialize repository with project scaffold
```

Every commit follows the **Red-Green-Refactor** TDD pattern with `Co-authored-by: OpenCode <ai@opencode.ai>` trailers.

---

## My AI Usage

### Tools Used

- **OpenCode**: AI coding assistant used for implementing all backend/ frontend features, debugging JWT / Security / CORS issues, writing tests, refactoring, and generating this README.
- **DeepSeek (OpenCode backend)**: Primary LLM driving OpenCode throughout the entire development lifecycle — architecture, boilerplate, testing, debugging, and documentation.

### How I Used AI

1. **Architecture & Planning**: Generated project structure, API endpoint design, database schema, and evaluated trade-offs between Spring Security configurations.

2. **Boilerplate Generation**: AI generated initial code for JPA entities, DTOs, repositories, services, controllers, security config, and React components. Every file was reviewed and modified to ensure correctness and adherence to SOLID principles.

3. **TDD Workflow**: AI generated failing tests (Red), then implementation code (Green), followed by refactoring suggestions (Refactor). This pattern is visible in every commit.

4. **Debugging**: AI diagnosed Spring Security filter chain issues, JWT token validation errors, CORS misconfigurations, and Hibernate constraint violations — significantly reducing debugging time.

5. **Frontend Design**: AI helped generate React component structures, the dark theme CSS design system, and test scaffolds. The visual design and UX interactions were iterated manually.

6. **Documentation**: This README, the OpenAPI annotations, and the test-all.ps1 PowerShell script were AI-assisted.

### Reflection

AI accelerated development by handling repetitive boilerplate and providing well-structured starting points. The real value came from understanding *what* the generated code does, making intentional design decisions, and knowing when to deviate from AI suggestions. The TDD workflow was particularly effective — AI generated test scaffolds quickly, allowing focus on defining meaningful test scenarios and edge cases.

Every commit includes a `Co-authored-by: OpenCode <ai@opencode.ai>` trailer to credit the AI contributions.

---

## Future Improvements

- **Production URL**: Configure Vercel custom domain (e.g., `cardealer.example.com`)
- **CI/CD Pipelines**: Add automated deployment workflow with Docker image build and push to Docker Hub / ECR
- **Rate Limiting**: Implement rate limiting on public endpoints (`/api/auth/*`) to prevent brute force attacks
- **Email Verification**: Add email verification flow during registration
- **File Uploads**: Allow image upload for vehicle listings
- **WebSocket Notifications**: Real-time inventory updates using WebSocket
- **Redis Caching**: Replace ConcurrentMapCacheManager with Redis for distributed caching
- **i18n**: Internationalization support for the frontend
- **E2E Tests**: Add Playwright or Cypress for end-to-end browser testing
- **Pagination on Frontend**: Add server-side pagination controls to the dashboard

---

## Known Limitations

- **In-memory caching** (`ConcurrentMapCacheManager`) is not suitable for multi-instance deployments — upgrade to Redis for horizontal scaling.
- **No image upload** — vehicle images are not currently supported.
- **No email service** — password reset and email verification are not implemented.
- **No pagination on dashboard** — the frontend loads all vehicles at once (pagination exists in the API but is not exposed in the dashboard UI yet).
- **Testcontainers integration tests** require Docker to be running locally (CI uses the GitHub Actions PostgreSQL service instead).

---

## License

MIT

---

*Built with Java 21, Spring Boot 3.4, React 19, PostgreSQL 16, and AI assistance.*
