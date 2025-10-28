```markdown
# Notes App (Spring Boot Backend + Vanilla JS Frontend)

A full-stack note-taking application with a **Java Spring Boot backend** (`notes/`) and a **minimal frontend** (`frontend/`).  
It supports user registration, login with JWT authentication, and CRUD operations for notes.

---

## ✨ Features
- User registration and login with JWT
- Create, read, update, and delete notes
- Simple frontend with authentication and note management
- CORS configured for local frontend (`http://localhost:5500`)
- Audit logging for note operations
- REST API with unified response format

---

## 🛠️ Tech Stack
- **Backend:** Java 17, Spring Boot, Spring Security, JPA/Hibernate
- **Database:** H2 (in-memory) or any JPA-compatible DB
- **Frontend:** Vanilla HTML, CSS, JavaScript
- **Build Tool:** Gradle

---

## 🚀 Getting Started

### Clone the repository
```bash
git clone https://github.com/USERNAME/notes-full.git
cd notes-full
```

### Backend (`notes/`)
1. Navigate to the backend folder:
   ```bash
   cd notes
   ```
2. Run the backend:
   ```bash
   ./gradlew bootRun
   ```
   The backend will start on `http://localhost:8080`.

### Frontend (`frontend/`)
1. Open a new terminal and navigate to the frontend folder:
   ```bash
   cd frontend
   ```
2. Start a simple HTTP server (Python example):
   ```bash
   python -m http.server 5500
   ```
3. Open in browser:
   ```
   http://localhost:5500/index.html
   ```

---

## 📂 Project Structure
```
notes-full/
 ├── frontend/                  # Simple frontend (HTML/CSS/JS)
 │    ├── index.html
 │    ├── style.css
 │    └── app.js
 ├── notes/                     # Spring Boot backend
 │    ├── src/main/java/com/example/notes
 │    │    ├── audit
 │    │    ├── common
 │    │    ├── config
 │    │    ├── note
 │    │    ├── security
 │    │    ├── tag
 │    │    ├── user
 │    │    └── web
 │    │         ├── dto
 │    │         └── mapper
 │    ├── src/main/resources/db/migration
 │    ├── src/test/java/com/example/notes
 │    └── build.gradle
 ├── .gitignore
 └── README.md
```

---

## 🔒 Authentication
- Register a new user via `POST /api/auth/register`
- Login via `POST /api/auth/login` to receive a JWT
- Use the JWT in `Authorization: Bearer <token>` header for all protected endpoints

---

## 📌 Example API Requests

### Register
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","email":"test@example.com","password":"secret"}'
```

### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"secret"}'
```

### Create Note
```bash
curl -X POST http://localhost:8080/api/notes \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -d '{"title":"My Note","content":"Hello World"}'
```
