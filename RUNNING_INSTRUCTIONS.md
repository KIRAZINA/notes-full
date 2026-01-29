# 🚀 Notes Application - Running Instructions

## Requirements

- **Java 17+** 
- **Gradle 8.2+** (included in project via gradlew)
- **PostgreSQL 12+** (for production) or H2 (for development)

---

## 🏃 Quick Start (Development)

### 1. Navigate to Project

```bash
cd /path/to/project
cd notes-full
```

### 2. Run Backend (Spring Boot)

```bash
cd notes
./gradlew bootRun  # Linux/Mac
./gradlew.bat bootRun  # Windows
```

Backend will start at `http://localhost:8080`

Development uses H2 in-memory database, so data will be reset on restart.

### 3. Run Frontend (Vanilla JS)

In a new terminal:

```bash
cd frontend
python -m http.server 5500  # Python 3
# or
python3 -m http.server 5500  # macOS
# or
npx http-server -p 5500  # Node.js
```

Open browser at `http://localhost:5500`

---

## 📋 Spring Boot Profiles

### Development (Default)

```bash
./gradlew bootRun
# or
./gradlew bootRun --args='--spring.profiles.active=dev'
```

**Uses:** H2 in-memory database  
**SQL Console:** `http://localhost:8080/h2-console`

### Production

```bash
./gradlew bootRun --args='--spring.profiles.active=prod'
```

**Requires environment variables:**
- `JDBC_DATABASE_USERNAME` - database user
- `JDBC_DATABASE_PASSWORD` - database password
- `JWT_SECRET` - JWT secret key (minimum 32 characters)
- `JWT_EXPIRATION_MS` - token lifetime in milliseconds

Configure in `src/main/resources/application-prod.yml`

---

## 🧪 Testing

### Run All Tests

```bash
./gradlew test
```

### Run Specific Test

```bash
./gradlew test --tests "com.example.notes.user.UserServiceTest"
```

### Run With Coverage Report

```bash
./gradlew test jacocoTestReport
# Report: build/reports/jacoco/test/html/index.html
```

---

## 🔐 API Authentication

### Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john",
    "password": "password123"
  }'
```

**Response:**
```json
{
  "success": true,
  "data": {
    "token": "eyJhbGc...",
    "expiresIn": 3600000
  }
}
```

### Get Current User

```bash
curl http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer eyJhbGc..."
```

---

## 📝 API Endpoints

### Notes
- `GET /api/notes` - List all notes
- `POST /api/notes` - Create note
- `GET /api/notes/{id}` - Get note by ID
- `PUT /api/notes/{id}` - Update note
- `DELETE /api/notes/{id}` - Delete note
- `POST /api/notes/{id}/archive` - Archive note
- `POST /api/notes/{id}/unarchive` - Unarchive note
- `POST /api/notes/{id}/trash` - Move to trash
- `POST /api/notes/{id}/restore` - Restore from trash

### Tags
- `GET /api/tags` - List all tags
- `POST /api/tags` - Create tag
- `GET /api/tags/{id}` - Get tag by ID
- `PUT /api/tags/{id}` - Update tag
- `DELETE /api/tags/{id}` - Delete tag

### Note-Tag Relations
- `POST /api/notes/{noteId}/tags/{tagId}` - Add tag to note
- `DELETE /api/notes/{noteId}/tags/{tagId}` - Remove tag from note
- `PUT /api/notes/{noteId}/tags` - Set tags for note

---

## 🐛 Troubleshooting

### Port Already in Use

If port 8080 is already in use:

```bash
./gradlew bootRun --args='--server.port=8081'
```

### Database Connection Error

Check database connection in `application-prod.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/notes_db
    username: ${JDBC_DATABASE_USERNAME}
    password: ${JDBC_DATABASE_PASSWORD}
```

### Tests Failing

Clear cache and rebuild:

```bash
./gradlew clean test
```

### JWT Token Expired

Get new token via login endpoint or restart with longer `JWT_EXPIRATION_MS`:

```bash
JWT_EXPIRATION_MS=7200000 ./gradlew bootRun --args='--spring.profiles.active=prod'
```

---

## 📦 Build JAR File

### Development JAR

```bash
./gradlew build
# Output: notes/build/libs/notes-0.0.1-SNAPSHOT.jar
```

### Run JAR

```bash
java -jar notes/build/libs/notes-0.0.1-SNAPSHOT.jar
```

### Production JAR with Environment

```bash
JDBC_DATABASE_USERNAME=user \
JDBC_DATABASE_PASSWORD=pass \
JWT_SECRET=your-secret-key-minimum-32-characters \
java -jar notes/build/libs/notes-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=prod
```

---

## 🚀 Docker (Optional)

### Build Image

```bash
docker build -t notes-app .
```

### Run Container

```bash
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e JDBC_DATABASE_USERNAME=user \
  -e JDBC_DATABASE_PASSWORD=pass \
  -e JWT_SECRET=your-secret-key \
  notes-app
```

---

## 📊 Development Tools

### View API Documentation

```
Swagger UI: http://localhost:8080/swagger-ui.html
OpenAPI JSON: http://localhost:8080/v3/api-docs
```

### H2 Console (Dev Only)

```
URL: http://localhost:8080/h2-console
Username: sa
Password: (empty)
```

---

## ✅ Verification Checklist

- [ ] Java 17+ installed: `java -version`
- [ ] Gradle wrapper working: `./gradlew --version`
- [ ] Backend starts without errors
- [ ] Frontend loads at `localhost:5500`
- [ ] Can login with credentials
- [ ] Can create/read/update/delete notes
- [ ] Can create/manage tags
- [ ] Tests pass: `./gradlew test`
- [ ] JAR builds successfully: `./gradlew build`

---

**Last Updated:** January 29, 2026
