# TaskFlow API - Setup and Run Guide

## 📋 Prerequisites

Before you begin, ensure you have the following installed:
- **Java 17** or higher ([Download](https://adoptium.net/))
- **Maven 3.6+** ([Download](https://maven.apache.org/download.cgi))
- **Docker Desktop** ([Download](https://www.docker.com/products/docker-desktop))

## 🚀 Quick Start

### Step 1: Start the Database

Open a terminal in the project directory and run:

```bash
docker-compose up -d
```

This will start a PostgreSQL database container on port 5432.

To verify the database is running:
```bash
docker ps
```

You should see `taskflow-postgres` container running.

### Step 2: Build the Application

```bash
mvn clean install
```

This will:
- Download all dependencies
- Compile the source code
- Run tests (if any)
- Package the application

### Step 3: Run the Application

```bash
mvn spring-boot:run
```

Or if you prefer using the compiled JAR:
```bash
java -jar target/TaskFlow-api-0.0.1-SNAPSHOT.jar
```

The application will start on **http://localhost:8080**

### Step 4: Verify the Application

You should see logs indicating:
```
Tomcat started on port 8080
Started TaskFlowApiApplication
Test users seeded successfully!
```

## 🧪 Testing the API

### Option 1: Using cURL

#### 1. Login to get JWT token
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"john@example.com\",\"password\":\"password123\"}"
```

Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "email": "john@example.com",
  "name": "John Doe"
}
```

Copy the `token` value for subsequent requests.

#### 2. Create a Project
```bash
curl -X POST http://localhost:8080/api/projects \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d "{\"title\":\"My Project\",\"description\":\"Project description\"}"
```

#### 3. Get All Projects
```bash
curl -X GET http://localhost:8080/api/projects \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

#### 4. Create a Task
```bash
curl -X POST http://localhost:8080/api/projects/1/tasks \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d "{\"title\":\"Task 1\",\"description\":\"Task description\",\"dueDate\":\"2025-12-31\"}"
```

### Option 2: Using Postman/Insomnia

1. Import the `API_EXAMPLES.md` file or manually create requests
2. Set the base URL to `http://localhost:8080`
3. For authenticated endpoints, add header: `Authorization: Bearer YOUR_TOKEN`

### Option 3: Using REST Client (VS Code Extension)

Install the "REST Client" extension and use `API_EXAMPLES.md` file.

## 🔑 Test User Accounts

The application automatically creates these test users on first startup:

| Email                  | Password      | Name       |
|------------------------|---------------|------------|
| john@example.com       | password123   | John Doe   |
| jane@example.com       | password123   | Jane Smith |
| admin@example.com      | password123   | Admin User |

## 📊 Database Management

### View Database Logs
```bash
docker-compose logs postgres
```

### Access PostgreSQL CLI
```bash
docker exec -it taskflow-postgres psql -U taskflow_user -d taskflow_db
```

Common PostgreSQL commands:
```sql
\dt                -- List all tables
\d users           -- Describe users table
SELECT * FROM users;
SELECT * FROM projects;
SELECT * FROM tasks;
\q                 -- Quit
```

### Stop the Database
```bash
docker-compose down
```

### Reset the Database (Delete all data)
```bash
docker-compose down -v
docker-compose up -d
```

Then restart the application to reseed test users.

## 🏗️ Project Structure

```
src/main/java/com/taskflowapi/
├── config/              # Security and app configuration
│   └── SecurityConfig.java
├── controller/          # REST API endpoints
│   ├── AuthController.java
│   ├── ProjectController.java
│   └── TaskController.java
├── dto/                 # Data Transfer Objects
│   ├── LoginRequest.java
│   ├── LoginResponse.java
│   ├── ProjectRequest.java
│   ├── ProjectResponse.java
│   ├── ProjectProgressResponse.java
│   ├── TaskRequest.java
│   └── TaskResponse.java
├── entity/              # JPA Entities
│   ├── User.java
│   ├── Project.java
│   └── Task.java
├── exception/           # Exception handling
│   ├── GlobalExceptionHandler.java
│   ├── ResourceNotFoundException.java
│   ├── UnauthorizedException.java
│   └── ErrorResponse.java
├── filter/              # Security filters
│   └── JwtAuthenticationFilter.java
├── repository/          # Data access layer
│   ├── UserRepository.java
│   ├── ProjectRepository.java
│   └── TaskRepository.java
├── security/            # Security utilities
│   ├── JwtUtil.java
│   └── CustomUserDetailsService.java
├── service/             # Business logic
│   ├── AuthService.java
│   ├── ProjectService.java
│   └── TaskService.java
├── util/                # Utility classes
│   └── DataSeeder.java
└── TaskFlowApiApplication.java
```

## 🔧 Configuration

Edit `src/main/resources/application.properties` to customize:

- **Server Port**: `server.port=8080`
- **Database URL**: `spring.datasource.url=jdbc:postgresql://localhost:5432/taskflow_db`
- **JWT Secret**: `jwt.secret=your-secret-key...`
- **JWT Expiration**: `jwt.expiration=86400000` (24 hours in milliseconds)

## 🛠️ Development Tips

### Hot Reload
The application uses Spring Boot DevTools for automatic restart on code changes.

### Change Log Level
Add to `application.properties`:
```properties
logging.level.com.taskflowapi=DEBUG
logging.level.org.springframework.security=DEBUG
```

### Disable Auto Schema Creation (Production)
```properties
spring.jpa.hibernate.ddl-auto=validate
```

## 📝 API Endpoints

| Method | Endpoint                          | Description              | Auth Required |
|--------|-----------------------------------|--------------------------|---------------|
| POST   | /api/auth/login                   | Login user               | No            |
| GET    | /api/projects                     | Get all user projects    | Yes           |
| POST   | /api/projects                     | Create new project       | Yes           |
| GET    | /api/projects/{id}                | Get project details      | Yes           |
| GET    | /api/projects/{id}/progress       | Get project progress     | Yes           |
| GET    | /api/projects/{projectId}/tasks   | Get all tasks            | Yes           |
| POST   | /api/projects/{projectId}/tasks   | Create new task          | Yes           |
| PATCH  | /api/tasks/{id}/complete          | Toggle task completion   | Yes           |
| DELETE | /api/tasks/{id}                   | Delete task              | Yes           |

## ❌ Troubleshooting

### Port 8080 already in use
Change the port in `application.properties`:
```properties
server.port=8081
```

### Port 5432 already in use (PostgreSQL)
Change the port in `docker-compose.yml`:
```yaml
ports:
  - "5433:5432"
```
And update `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5433/taskflow_db
```

### Database connection refused
Ensure Docker container is running:
```bash
docker-compose up -d
docker ps
```

### JWT token invalid
- Token expires after 24 hours by default
- Login again to get a new token
- Ensure you're using `Bearer YOUR_TOKEN` format

## 🧹 Cleanup

Stop and remove everything:
```bash
docker-compose down -v
mvn clean
```

## 📚 Technologies Used

- **Spring Boot 4.0.1** - Framework
- **Spring Security** - Authentication & Authorization
- **Spring Data JPA** - Data persistence
- **PostgreSQL 16** - Database
- **JWT (jjwt 0.12.3)** - Token-based auth
- **Lombok** - Reduce boilerplate code
- **Hibernate Validator** - Input validation
- **Docker** - Containerization

## ✅ Features Implemented

All required user stories have been implemented:

### ✓ EPIC 1: Authentication & Authorization
- JWT token generation and validation
- User login endpoint
- Protected API routes
- Seeded test users

### ✓ EPIC 2: Projects Management
- Project database model
- Create, list, and retrieve projects
- User ownership validation

### ✓ EPIC 3: Tasks Management
- Task database model
- Create, list, toggle completion, and delete tasks
- Project association

### ✓ EPIC 4: Project Progress
- Progress calculation endpoint
- Percentage calculation

### ✓ EPIC 5: Technical Infrastructure
- Clean project structure
- PostgreSQL database with Docker
- Global error handling
- Input validation

## 🎯 Next Steps (Optional Enhancements)

Consider implementing these bonus features:
- Unit tests with high coverage
- Task pagination
- Task filtering and search
- Update endpoints for projects and tasks
- Refresh token mechanism
- Email verification
- Password reset functionality

## 📞 Support

For issues or questions:
1. Check the logs in the terminal
2. Verify database is running
3. Ensure all prerequisites are installed
4. Review the error messages in the API responses

---

**Happy Coding! 🚀**
