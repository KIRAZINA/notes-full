# Notes API (Backend Only)

A simple Java backend for notes, tags, and JWT authentication.

## Project Contents
- Spring Boot 3.3.5 backend in `notes/`
- JWT authentication for API access
- Notes CRUD, archive/trash, and tag relationships
- Centralized API responses and error handling
- Integration and API tests

## Tech Stack
- Java 21
- Spring Boot 3.3.5
- Spring Security JWT
- Spring Data JPA / Hibernate
- H2 for development, PostgreSQL supported
- Maven Wrapper
- Docker

## Run Locally
```bash
cd notes
./mvnw spring-boot:run
```

On Windows:
```powershell
cd notes
.\mvnw.cmd spring-boot:run
```

Default base URL:
`http://localhost:8080/api`

## Run Tests
```bash
cd notes
./mvnw clean test
```

## Docker
Build image:
```bash
cd notes
docker build -t notes-api .
```

Run container:
```bash
docker run --rm -p 8080:8080 -e JWT_SECRET=replace-with-strong-secret-at-least-32-chars notes-api
```

## Environment Variables
Optional overrides:
```powershell
$env:JWT_SECRET="replace-with-strong-secret-at-least-32-chars"
$env:JWT_EXPIRATION_MS="3600000"
$env:SERVER_PORT="8081"
```

## API Overview
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

## Docs
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI: `http://localhost:8080/v3/api-docs`

## Project Layout
- `notes/` Spring Boot source and Maven project
- `README.md` project documentation
- `.gitignore` ignore rules for temporary files and build outputs

