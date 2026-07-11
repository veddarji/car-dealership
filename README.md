# Car Dealership Inventory System

A full-stack Car Dealership Inventory System built with **Java Spring Boot** (backend) and **React + Vite** (frontend), backed by **PostgreSQL**.

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Java 21 + Spring Boot 3.4 |
| Security | Spring Security 6 + JWT (jjwt) |
| Database | PostgreSQL |
| ORM | Spring Data JPA + Hibernate |
| Frontend | React 19 + Vite |
| Styling | Custom CSS (Dark Theme) |
| Animations | Framer Motion |
| Testing (BE) | JUnit 5 + Mockito + H2 |
| Testing (FE) | Vitest + React Testing Library |
| Coverage | JaCoCo |

## Features

- **User Authentication**: Register/Login with JWT token-based auth
- **Role-Based Access**: USER and ADMIN roles with granular permissions
- **Vehicle CRUD**: Add, view, update, and delete vehicles
- **Smart Search**: Filter by make, model, category, and price range
- **Inventory Management**: Purchase vehicles (decreases stock) and restock (admin only)
- **Responsive UI**: Premium dark theme with glassmorphism design
- **Comprehensive Testing**: TDD approach with high code coverage

## Prerequisites

- **Java 21** (OpenJDK recommended)
- **Maven 3.9+**
- **Node.js 18+** and npm
- **PostgreSQL 14+**

## Setup & Run Locally

### 1. Clone the repository

```bash
git clone <repository-url>
cd INqubits
```

### 2. Set up PostgreSQL

Create a database named `cardealership`:

```sql
CREATE DATABASE cardealership;
```

The default connection config expects PostgreSQL on `localhost:5432` with username `postgres` and password `postgres`. To change this, edit `backend/src/main/resources/application.properties`.

### 3. Start the Backend

```bash
cd backend
mvn spring-boot:run
```

The API will be available at `http://localhost:8080`.

On first run, the application seeds:
- An admin user: `admin` / `admin123`
- 10 sample vehicles

### 4. Start the Frontend

```bash
cd frontend
npm install
npm run dev
```

The app will be available at `http://localhost:5173`.

## API Endpoints

### Authentication
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/api/auth/register` | Register new user | Public |
| POST | `/api/auth/login` | Login & get JWT | Public |

### Vehicles
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/api/vehicles` | List all vehicles | JWT |
| GET | `/api/vehicles/search` | Search/filter vehicles | JWT |
| GET | `/api/vehicles/{id}` | Get vehicle by ID | JWT |
| POST | `/api/vehicles` | Add new vehicle | JWT |
| PUT | `/api/vehicles/{id}` | Update vehicle | JWT |
| DELETE | `/api/vehicles/{id}` | Delete vehicle | JWT + ADMIN |

### Inventory
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/api/vehicles/{id}/purchase` | Purchase vehicle | JWT |
| POST | `/api/vehicles/{id}/restock` | Restock vehicle | JWT + ADMIN |

## Running Tests

### Backend
```bash
cd backend
mvn test                        # Run all tests
mvn jacoco:report               # Generate coverage report (target/site/jacoco/index.html)
```

### Frontend
```bash
cd frontend
npm test                        # Run all tests
npm run test:coverage           # Run with coverage
```

## Project Structure

```
├── backend/                    # Spring Boot REST API
│   ├── src/main/java/com/cardealership/
│   │   ├── config/             # Security, CORS, data seeder
│   │   ├── controller/         # REST controllers
│   │   ├── dto/                # Request/Response DTOs
│   │   ├── entity/             # JPA entities
│   │   ├── enums/              # Role enum
│   │   ├── exception/          # Custom exceptions + global handler
│   │   ├── repository/         # Spring Data JPA repositories
│   │   ├── security/           # JWT service, filter, UserDetails
│   │   └── service/            # Business logic
│   └── src/test/               # Test suite
├── frontend/                   # React SPA
│   ├── src/
│   │   ├── api/                # Axios instance + API modules
│   │   ├── components/         # Reusable UI components
│   │   ├── context/            # Auth context
│   │   ├── pages/              # Page components
│   │   └── routes/             # Route guards
│   └── ...
└── README.md
```

## Test Report

All **103 backend tests** pass (JUnit 5 + Mockito + H2):

| Test Class | Tests | Status |
|-----------|-------|--------|
| `AuthServiceTest` | 7 | ✅ |
| `VehicleServiceTest` | 13 | ✅ |
| `InventoryServiceTest` | 7 | ✅ |
| `AuthControllerTest` | 6 | ✅ |
| `VehicleControllerTest` | 17 | ✅ |
| `UserRepositoryTest` | 7 | ✅ |
| `VehicleRepositoryTest` | 7 | ✅ |
| `JwtServiceTest` | 7 | ✅ |
| `CustomUserDetailsServiceTest` | 2 | ✅ |
| `SecurityConfigTest` | 4 | ✅ |
| `SwaggerDocTest` | 10 | ✅ |
| `DevProfileTest` | 1 | ✅ |
| `DockerConfigTest` | 1 | ✅ |
| `AuthFlowIT` | 4 | ✅ |
| `VehicleFlowIT` | 6 | ✅ |
| `InventoryFlowIT` | 4 | ✅ |
| `CategoryRepositoryTest` | 4 | ✅ |
| `CategoryServiceTest` | 5 | ✅ |
| `PurchaseRepositoryTest` | 3 | ✅ |
| `PurchaseServiceTest` | 1 | ✅ |
| `InventoryTransactionRepositoryTest` | 2 | ✅ |
| **Total** | **118** | **✅ All Passing** |

Quick API smoke-test script:
```powershell
cd backend
.\test-all.ps1
```

This tests all 14 endpoints end-to-end: register, login, CRUD, purchase, restock, auth errors.

## Performance & Scalability (100K+ Users)

### Database Indexes
| Index | Type | Purpose |
|-------|------|---------|
| `idx_vehicles_make_trgm` | GIN (pg_trgm) | Fast `LIKE '%...%'` on make |
| `idx_vehicles_model_trgm` | GIN (pg_trgm) | Fast `LIKE '%...%'` on model |
| `idx_vehicles_category` | B-tree | Exact filter on category |
| `idx_vehicles_price` | B-tree | Range queries on price |
| `idx_vehicles_make_model` | Composite B-tree | Combined make+model sort |
| `idx_vehicles_category_price` | Composite B-tree | Category + price filter |

### Caching
Spring Cache (`ConcurrentMapCacheManager`) with `@Cacheable`/`@CacheEvict`:
- `getAllVehicles` — cached per pageable
- `getVehicleById` — cached per ID
- Write operations (`create`, `update`, `delete`) evict entire cache

### Connection Pool (HikariCP)
| Parameter | Value | Env Override |
|-----------|-------|-------------|
| min-idle | 10 | `HIKARI_MIN_IDLE` |
| max-pool-size | 50 | `HIKARI_MAX_POOL` |
| idle-timeout | 300s | `HIKARI_IDLE_TIMEOUT` |
| max-lifetime | 600s | `HIKARI_MAX_LIFETIME` |
| connection-timeout | 30s | `HIKARI_CONN_TIMEOUT` |

### Batch Processing
- Hibernate batch inserts/updates enabled (batch_size=25)
- SQL logging disabled by default (`SHOW_SQL=false`)

## Database

Connect with psql:
```powershell
& "C:\Program Files\PostgreSQL\17\bin\psql.exe" -h localhost -U postgres -d cardealership
```

Five tables:
- `users` — id, username, email, role (USER / ADMIN)
- `vehicles` — id, make, model, category, price, quantity
- `categories` — id, name (unique), description
- `purchases` — id, user_id (FK), vehicle_id (FK), quantity, unit_price, total_price, purchased_at
- `inventory_transactions` — id, vehicle_id (FK), user_id (FK), type (PURCHASE/RESTOCK), quantity_change, previous/new quantity, created_at

## My AI Usage

### Tools Used
- **OpenCode**: AI coding assistant used for implementing backend features, debugging JWT/security issues, writing test scripts, and refactoring the controller layer.
- **Google Gemini (Antigravity)**: Primary AI assistant for the entire development lifecycle

### How I Used AI

1. **Architecture & Planning**: Used Gemini to brainstorm the project structure, API endpoint design, and database schema. It helped me evaluate trade-offs between different Spring Security configurations.

2. **Boilerplate Generation**: Gemini generated initial boilerplate for JPA entities, DTOs, repository interfaces, and Spring Security configuration. I reviewed and modified each file to ensure correctness and adherence to SOLID principles.

3. **Test Writing**: Used AI to generate test scaffolds following TDD patterns (Red-Green-Refactor). I manually refined assertions and edge cases to ensure meaningful coverage.

4. **Frontend Components**: AI helped generate React component structures and the CSS design system. I iterated on the visual design and UX interactions.

5. **Debugging**: Used AI to diagnose Spring Security filter chain issues and JWT token validation errors.

### Reflection

AI significantly accelerated development by handling repetitive boilerplate and providing well-structured starting points. However, the real value came from understanding _what_ the generated code does, making intentional design decisions, and knowing when to deviate from AI suggestions. The TDD workflow was particularly enhanced — AI could generate test scaffolds quickly, letting me focus on defining meaningful test scenarios and edge cases.

## Screenshots

_Screenshots will be added after the frontend is built._

## License

MIT
