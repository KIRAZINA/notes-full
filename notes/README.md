# Notes Backend

Spring Boot backend for a note-taking application.

## Features
- JWT authentication (register/login/me)
- Notes CRUD + archive/trash actions
- Tags CRUD + note-tag relations
- Unified API response format
- Input validation and centralized error handling
- Audit logging

## Tech Stack
- Java 17
- Spring Boot 3.1.5
- Spring Security
- JPA/Hibernate
- Flyway
- H2 / PostgreSQL
- Gradle

## Run
```bash
set JWT_SECRET=replace-with-strong-secret-at-least-32-chars
./gradlew bootRun
```

Server URL: `http://localhost:8080`

## Test
```bash
./gradlew test
```

## API
Base path: `/api`

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
- `POST /api/notes/{id}/unarchive`
- `POST /api/notes/{id}/trash`
- `POST /api/notes/{id}/restore`

### Tags
- `GET /api/tags`
- `POST /api/tags`
- `GET /api/tags/{id}`
- `PUT /api/tags/{id}`
- `DELETE /api/tags/{id}`

### Note-Tag
- `POST /api/notes/{noteId}/tags/{tagId}`
- `DELETE /api/notes/{noteId}/tags/{tagId}`
- `PUT /api/notes/{noteId}/tags`
