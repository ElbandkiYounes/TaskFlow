# TaskFlow - Project Management Application

Full-stack project management app with authentication and task tracking.

## 🛠️ Tech Stack

- **Backend:** Java 17 + Spring Boot 4.0.1 + Spring Security + JWT
- **Frontend:** React 19.2.0 + TypeScript + Vite + Tailwind CSS
- **Database:** MySQL 8.0
- **Deployment:** Docker + Docker Compose

## 📋 Prerequisites

- Docker
- Docker Compose

## 🚀 Quick Start

1. **Clone the repository**
2. **Run with Docker:**

```bash
docker-compose up --build
```

This automatically sets up:
- MySQL database (port 3306)
- Backend API (port 8080)
- Frontend (port 80)

**Access:**
- Frontend: http://localhost
- Backend API: http://localhost:8080

**Stop:**
```bash
docker-compose down
```

**Remove all data:**
```bash
docker-compose down -v
```

## 🗄️ Database Configuration

Docker automatically creates:
- Database: `taskflow_db`
- User: `taskflow_user`
- Password: `taskflow_password`

No manual setup required!

## 📁 Project Structure

```
TaskFlow/
├── docker-compose.yml       # Docker orchestration
├── TaskFlow-api/           # Spring Boot backend
│   ├── src/main/java/      # Java source code
│   ├── pom.xml             # Maven dependencies
│   └── Dockerfile
└── TaskFlow-front/         # React frontend
    ├── src/                # React components
    ├── package.json        # npm dependencies
    └── Dockerfile
```


