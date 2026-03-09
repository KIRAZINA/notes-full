# Notes API (Backend Only)

A backend-only note management service built with Java and Spring Boot.

## What This Project Includes
- JWT-based authentication (`/api/auth/register`, `/api/auth/login`, `/api/auth/me`)
- Notes CRUD + archive/trash operations
- Tag CRUD + note-tag relations
- Unified API response format (`ApiResponse`)
- Centralized error handling
- Brute-force login protection
- Integration + API tests

## Tech Stack
- Java 17
- Spring Boot 3.1.5
- Spring Security (JWT-only)
- Spring Data JPA / Hibernate
- Flyway migrations
- H2 (dev), PostgreSQL (runtime option)
- Gradle Wrapper

## Security Model
- Bearer token auth only (no HTTP Basic)
- JWT secret is read from environment variables
- Fail-fast startup if JWT secret is missing/invalid
- Unified JSON security errors:
  - `401` -> `error.code = "unauthorized"`
  - `403` -> `error.code = "forbidden"`

## Required Environment Variables
Set at least the JWT secret before running:

Windows PowerShell:
```powershell
$env:JWT_SECRET="replace-with-strong-secret-at-least-32-chars"
```

Optional:
```powershell
$env:JWT_EXPIRATION_MS="3600000"
```

## Run Locally
```bash
cd notes
./gradlew bootRun
```

Base URL: `http://localhost:8080/api`

## Run Tests
```bash
cd notes
./gradlew clean test
```

## API Documentation
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Main Endpoints
### Auth
- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/auth/me`

### Notes
- `GET /api/notes`
- `POST /api/notes`
- `GET /api/notes/{id}`
- `PUT /api/notes/{id}`
- `DELETE /api/notes/{id}`
- `POST /api/notes/{id}/archive`
- `POST /api/notes/{id}/restore-archive`
- `POST /api/notes/{id}/trash`
- `POST /api/notes/{id}/restore-trash`

### Tags
- `GET /api/tags`
- `POST /api/tags`
- `DELETE /api/tags/{id}`

### Note-Tag
- `POST /api/notes/{id}/tags/{tagId}`
- `DELETE /api/notes/{id}/tags/{tagId}`
- `PUT /api/notes/{id}/tags`

## Project Layout
- `notes/` Spring Boot backend code
- `README.md` Root project documentation
- `.gitignore`
