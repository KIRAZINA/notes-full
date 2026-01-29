# Notes Application - Full Stack

A comprehensive note-taking application built with **Java Spring Boot** backend and **Vanilla JavaScript** frontend. Features user authentication, JWT tokens, CORS support, and complete REST API for note management.

## ✨ Key Features

- **User Management**
  - User registration and authentication
  - JWT-based token authentication
  - Secure password storage
  - Brute-force attack protection

- **Note Operations**
  - Create, read, update, and delete notes
  - Archive and restore notes
  - Move notes to trash
  - Pinned notes support

- **Tag Management**
  - Create and manage tags
  - Assign multiple tags to notes
  - Filter notes by tags
  - Delete tags

- **Security**
  - Brute-force login protection (5 attempts, 15-minute lock)
  - JWT token-based authentication
  - CORS properly configured
  - Input validation on all endpoints
  - SQL injection prevention via JPA

- **Code Quality**
  - Unified API response format
  - Comprehensive error handling
  - Full test coverage
  - English documentation throughout

---

## 🛠️ Tech Stack

### Backend
- **Language:** Java 17
- **Framework:** Spring Boot 3.1.5
- **Security:** Spring Security + JWT
- **ORM:** JPA/Hibernate with Lombok
- **Database:** H2 (development) or PostgreSQL (production)
- **Build:** Gradle 8.2
- **Migrations:** Flyway
- **Mapping:** MapStruct

### Frontend
- **HTML5** - Page structure
- **CSS3** - Styling
- **Vanilla JavaScript (ES6+)** - No frameworks, pure JavaScript
- **Fetch API** - HTTP requests
- **LocalStorage** - Token persistence

---

## 📋 Prerequisites

- **Java 17+**
- **Gradle 8.2+** (included via wrapper)
- **Python 3** or **Node.js** (for simple HTTP server)
- **PostgreSQL 12+** (optional, for production)

---

## 🚀 Quick Start

### 1. Clone Repository

```bash
git clone https://github.com/yourusername/notes-app.git
cd notes-app
```

### 2. Backend Setup

```bash
cd notes
./gradlew bootRun
```

Backend will start at **`http://localhost:8080`**

Development uses H2 in-memory database (data resets on restart).

### 3. Frontend Setup

In a new terminal:

```bash
cd frontend

# Option 1: Python
python -m http.server 5500

# Option 2: Node.js
npx http-server -p 5500

# Option 3: Python 3
python3 -m http.server 5500
```

Frontend will be available at **`http://localhost:5500`**

### 4. Access Application

Open browser and navigate to: `http://localhost:5500`

---

## 📂 Project Structure

```
notes-app/
├── frontend/                           # Vanilla JS Frontend
│   ├── index.html                      # Main HTML page
│   ├── style.css                       # Styling
│   └── app.js                          # JavaScript logic
│
├── notes/                              # Spring Boot Backend
│   ├── src/main/java/com/example/notes/
│   │   ├── audit/                      # Audit logging
│   │   ├── common/                     # Common exceptions
│   │   ├── config/                     # Security and CORS config
│   │   ├── note/                       # Note entity, service, repository
│   │   ├── security/                   # JWT, login attempt tracking
│   │   ├── tag/                        # Tag entity, service, repository
│   │   ├── user/                       # User entity, service, repository
│   │   └── web/                        # Controllers and DTOs
│   │
│   ├── src/main/resources/
│   │   ├── db/migration/               # Flyway migrations (V1-V5)
│   │   ├── application.yml             # Default configuration
│   │   ├── application-dev.yml         # Development profile
│   │   └── application-prod.yml        # Production profile
│   │
│   ├── src/test/java/                  # Integration and unit tests
│   ├── build.gradle                    # Build configuration
│   └── gradlew                         # Gradle wrapper
│
├── .gitignore                          # Git ignore rules
├── README.md                           # This file
├── BUGFIXES_REPORT.md                  # Detailed bug fixes
├── COMPLETION_SUMMARY.md               # Work summary
├── RUNNING_INSTRUCTIONS.md             # Detailed setup guide
├── FINAL_REPORT.md                     # Final status report
└── LOCALIZATION_REPORT.md              # Language verification
```

---

## 🔐 API Authentication

All protected endpoints require JWT token in header:

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIs...
```

### Get Token

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
    "token": "eyJhbGciOiJIUzI1NiIs...",
    "expiresIn": 3600000
  }
}
```

---

## 📚 API Endpoints

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login user (returns JWT)
- `GET /api/auth/me` - Get current user info

### Notes
- `GET /api/notes` - List all user's notes
- `POST /api/notes` - Create new note
- `GET /api/notes/{id}` - Get note by ID
- `PUT /api/notes/{id}` - Update note
- `DELETE /api/notes/{id}` - Delete note
- `POST /api/notes/{id}/archive` - Archive note
- `POST /api/notes/{id}/unarchive` - Restore note
- `POST /api/notes/{id}/trash` - Move to trash
- `POST /api/notes/{id}/restore` - Restore from trash

### Tags
- `GET /api/tags` - List all user's tags
- `POST /api/tags` - Create new tag
- `GET /api/tags/{id}` - Get tag by ID
- `PUT /api/tags/{id}` - Update tag
- `DELETE /api/tags/{id}` - Delete tag

### Note-Tag Relations
- `POST /api/notes/{noteId}/tags/{tagId}` - Add tag to note
- `DELETE /api/notes/{noteId}/tags/{tagId}` - Remove tag from note
- `PUT /api/notes/{noteId}/tags` - Set all tags for note

---

## 🧪 Testing

### Run All Tests

```bash
cd notes
./gradlew test
```

### Run Specific Test

```bash
./gradlew test --tests "com.example.notes.web.NoteControllerIntegrationTest"
```

### Build Production JAR

```bash
./gradlew build
java -jar build/libs/notes-0.0.1-SNAPSHOT.jar
```

---

## 🌍 Production Deployment

### Setup PostgreSQL

```bash
# Create database
createdb notes_db

# Optional: create user
createuser notes_user -P
```

### Environment Variables

```bash
export SPRING_PROFILES_ACTIVE=prod
export JDBC_DATABASE_USERNAME=notes_user
export JDBC_DATABASE_PASSWORD=your_secure_password
export JWT_SECRET=your-secret-key-minimum-32-characters
export JWT_EXPIRATION_MS=3600000
```

### Run Production Build

```bash
./gradlew bootJar
java -jar build/libs/notes-0.0.1-SNAPSHOT.jar
```

---

## 📊 Security Features

- ✅ **Brute-Force Protection** - 5 failed attempts = 15-minute account lock
- ✅ **JWT Authentication** - Stateless token-based auth
- ✅ **Password Hashing** - BCrypt with configurable strength
- ✅ **CORS Security** - Only localhost:3000 and localhost:5500
- ✅ **Input Validation** - All DTOs validated with Spring validation
- ✅ **SQL Injection Prevention** - JPA parameterized queries
- ✅ **Information Leak Prevention** - Same error messages for security
- ✅ **Secure Headers** - Proper HTTP status codes and error messages

---

## 🐛 Known Issues and Fixes

All critical bugs have been identified and fixed:

1. ✅ CORS configuration duplication
2. ✅ Missing validation in DTOs
3. ✅ Broken authentication tests
4. ✅ Inconsistent API response formats
5. ✅ Database migration conflicts
6. ✅ No brute-force protection
7. ✅ JWT optimization
8. ✅ Boundary case validation
9. ✅ Missing exception handlers
10. ✅ Information leak vulnerabilities
11. ✅ Weak DTO validation
12. ✅ Outdated frontend code

See [BUGFIXES_REPORT.md](BUGFIXES_REPORT.md) for detailed information.

---

## 📖 Documentation

- **[BUGFIXES_REPORT.md](BUGFIXES_REPORT.md)** - Detailed description of all bugs found and fixed
- **[RUNNING_INSTRUCTIONS.md](RUNNING_INSTRUCTIONS.md)** - Complete setup and deployment guide
- **[COMPLETION_SUMMARY.md](COMPLETION_SUMMARY.md)** - Summary of all changes made
- **[FINAL_REPORT.md](FINAL_REPORT.md)** - Final status and verification report
- **[LOCALIZATION_REPORT.md](LOCALIZATION_REPORT.md)** - English localization verification

---

## 🎯 Testing the Application

### 1. Create Account
- Open frontend at `http://localhost:5500`
- Register with any username/email/password

### 2. Login
- Click "Switch to Login"
- Login with your credentials

### 3. Create Note
- Enter title and content
- Click "Create Note"
- Note appears in the list

### 4. Manage Tags
- Create tags in "Tags" section
- Add/remove tags from notes
- Filter notes by tags

### 5. Test Brute-Force Protection
- Try logging in with wrong password 5 times
- Account locks for 15 minutes
- HTTP 429 response returned

---

## 🛠️ Development Tools

### Swagger API Documentation
```
http://localhost:8080/swagger-ui.html
```

### H2 Console (Development Only)
```
http://localhost:8080/h2-console
Username: sa
Password: (empty)
```

### Check Logs
```bash
tail -f notes/build/*.log
```

---

## 📞 Support

For detailed information about:
- **Setup Issues** - See [RUNNING_INSTRUCTIONS.md](RUNNING_INSTRUCTIONS.md)
- **Bug Fixes** - See [BUGFIXES_REPORT.md](BUGFIXES_REPORT.md)
- **API Usage** - See [RUNNING_INSTRUCTIONS.md](RUNNING_INSTRUCTIONS.md)
- **Code Changes** - See source code comments (all in English)

---

## ✅ Verification Checklist

Before deploying to production:

- [ ] Java 17+ installed and working
- [ ] Gradle wrapper builds successfully
- [ ] All tests pass: `./gradlew test`
- [ ] Backend starts: `./gradlew bootRun`
- [ ] Frontend loads at `localhost:5500`
- [ ] Can register new user
- [ ] Can login and get JWT token
- [ ] Can create/edit/delete notes
- [ ] Can manage tags
- [ ] Brute-force protection works (5 attempts lock)
- [ ] PostgreSQL ready (for production)
- [ ] Environment variables configured
- [ ] Production JAR builds: `./gradlew bootJar`

---

## 📊 Project Statistics

- **Java Classes:** 25+
- **Test Files:** 8+
- **Total LOC:** ~3500+
- **Code Quality:** Production-Ready
- **Test Coverage:** Comprehensive
- **Documentation:** Complete (English)
- **Localization:** 100% English

---

## 🔄 Version History

- **v0.0.1-SNAPSHOT** - Initial release
  - Complete CRUD operations for notes
  - User authentication with JWT
  - Tag management system
  - Brute-force protection
  - Full test coverage
  - Production-ready security

---

## 📄 License

This project is provided as-is for educational and development purposes.

---

## 👥 Contributors

- Development and Bug Fixes completed on **January 29, 2026**
- 12 issues identified and fixed
- All tests passing
- Production ready

---

**Last Updated:** January 29, 2026  
**Status:** ✅ Production Ready  
**Build:** Successful  
**Tests:** All Passing
