# Testing Checklist

Use this checklist to verify all functionality works correctly.

## ✅ Pre-Flight Checks

- [ ] Java 17+ installed (`java -version`)
- [ ] Maven installed (`mvn -version`)
- [ ] Docker Desktop running
- [ ] Port 8080 is available
- [ ] Port 5432 is available

## 🗄️ Database Setup

- [ ] Start database: `docker-compose up -d`
- [ ] Verify container running: `docker ps` (should see `taskflow-postgres`)
- [ ] Check logs: `docker-compose logs postgres`

## 🚀 Application Startup

- [ ] Build project: `mvn clean install`
- [ ] Start application: `mvn spring-boot:run`
- [ ] Verify startup in logs:
  - [ ] "Tomcat started on port 8080"
  - [ ] "Started TaskFlowApiApplication"
  - [ ] "Test users seeded successfully!"

## 🔐 Authentication Tests

### Test 1: Login with John
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"john@example.com","password":"password123"}'
```
- [ ] Returns 200 OK
- [ ] Response contains `token`, `email`, `name`
- [ ] Copy token for next tests

### Test 2: Login with Invalid Credentials
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"john@example.com","password":"wrongpassword"}'
```
- [ ] Returns 500 (with error message "Invalid credentials")

### Test 3: Login with Invalid Email Format
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"notanemail","password":"password123"}'
```
- [ ] Returns 400 Bad Request
- [ ] Shows validation error for email

## 📁 Project Tests

**Set your token**: Replace `YOUR_TOKEN` in commands below with the token from login.

### Test 4: Create Project
```bash
curl -X POST http://localhost:8080/api/projects \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"title":"My First Project","description":"Testing project creation"}'
```
- [ ] Returns 201 Created
- [ ] Response contains project with id, title, description, userId, timestamps
- [ ] Note the project `id` for next tests

### Test 5: Get All Projects
```bash
curl -X GET http://localhost:8080/api/projects \
  -H "Authorization: Bearer YOUR_TOKEN"
```
- [ ] Returns 200 OK
- [ ] Array contains the project created in Test 4

### Test 6: Get Project by ID
```bash
curl -X GET http://localhost:8080/api/projects/1 \
  -H "Authorization: Bearer YOUR_TOKEN"
```
- [ ] Returns 200 OK
- [ ] Contains project details

### Test 7: Get Project without Token
```bash
curl -X GET http://localhost:8080/api/projects/1
```
- [ ] Returns 403 Forbidden (or 401)

### Test 8: Get Non-existent Project
```bash
curl -X GET http://localhost:8080/api/projects/999 \
  -H "Authorization: Bearer YOUR_TOKEN"
```
- [ ] Returns 404 Not Found

### Test 9: Create Project without Title
```bash
curl -X POST http://localhost:8080/api/projects \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"description":"No title provided"}'
```
- [ ] Returns 400 Bad Request
- [ ] Shows validation error for title

## ✅ Task Tests

### Test 10: Create Task
```bash
curl -X POST http://localhost:8080/api/projects/1/tasks \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"title":"Complete backend API","description":"Implement all endpoints","dueDate":"2025-12-31"}'
```
- [ ] Returns 201 Created
- [ ] Response contains task with id, title, description, dueDate, isCompleted=false
- [ ] Note the task `id` for next tests

### Test 11: Create Another Task
```bash
curl -X POST http://localhost:8080/api/projects/1/tasks \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"title":"Write documentation","description":"Create README and guides"}'
```
- [ ] Returns 201 Created
- [ ] dueDate is null (optional field works)

### Test 12: Get All Tasks for Project
```bash
curl -X GET http://localhost:8080/api/projects/1/tasks \
  -H "Authorization: Bearer YOUR_TOKEN"
```
- [ ] Returns 200 OK
- [ ] Array contains both tasks created

### Test 13: Toggle Task Completion
```bash
curl -X PATCH http://localhost:8080/api/tasks/1/complete \
  -H "Authorization: Bearer YOUR_TOKEN"
```
- [ ] Returns 200 OK
- [ ] `isCompleted` is now `true`

### Test 14: Toggle Task Completion Again
```bash
curl -X PATCH http://localhost:8080/api/tasks/1/complete \
  -H "Authorization: Bearer YOUR_TOKEN"
```
- [ ] Returns 200 OK
- [ ] `isCompleted` is now `false` (toggled back)

### Test 15: Delete Task
```bash
curl -X DELETE http://localhost:8080/api/tasks/2 \
  -H "Authorization: Bearer YOUR_TOKEN"
```
- [ ] Returns 204 No Content

### Test 16: Verify Task Deleted
```bash
curl -X GET http://localhost:8080/api/projects/1/tasks \
  -H "Authorization: Bearer YOUR_TOKEN"
```
- [ ] Returns 200 OK
- [ ] Array contains only 1 task (task 2 was deleted)

### Test 17: Create Task without Title
```bash
curl -X POST http://localhost:8080/api/projects/1/tasks \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"description":"No title"}'
```
- [ ] Returns 400 Bad Request
- [ ] Shows validation error for title

## 📊 Progress Tests

### Test 18: Get Project Progress (1 task, 0 completed)
```bash
curl -X PATCH http://localhost:8080/api/tasks/1/complete \
  -H "Authorization: Bearer YOUR_TOKEN"
```
First mark task as incomplete, then check progress:
```bash
curl -X GET http://localhost:8080/api/projects/1/progress \
  -H "Authorization: Bearer YOUR_TOKEN"
```
- [ ] Returns 200 OK
- [ ] `totalTasks`: 1
- [ ] `completedTasks`: 0
- [ ] `progressPercentage`: 0.0

### Test 19: Complete Task and Check Progress
```bash
curl -X PATCH http://localhost:8080/api/tasks/1/complete \
  -H "Authorization: Bearer YOUR_TOKEN"

curl -X GET http://localhost:8080/api/projects/1/progress \
  -H "Authorization: Bearer YOUR_TOKEN"
```
- [ ] Returns 200 OK
- [ ] `totalTasks`: 1
- [ ] `completedTasks`: 1
- [ ] `progressPercentage`: 100.0

### Test 20: Add More Tasks and Check Progress
```bash
curl -X POST http://localhost:8080/api/projects/1/tasks \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"title":"Task 2"}'

curl -X POST http://localhost:8080/api/projects/1/tasks \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"title":"Task 3"}'

curl -X GET http://localhost:8080/api/projects/1/progress \
  -H "Authorization: Bearer YOUR_TOKEN"
```
- [ ] Returns 200 OK
- [ ] `totalTasks`: 3
- [ ] `completedTasks`: 1
- [ ] `progressPercentage`: 33.33 (approx)

## 🔒 Authorization Tests

### Test 21: Login as Jane
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"jane@example.com","password":"password123"}'
```
- [ ] Returns 200 OK
- [ ] Copy Jane's token

### Test 22: Try to Access John's Project
```bash
curl -X GET http://localhost:8080/api/projects/1 \
  -H "Authorization: Bearer JANE_TOKEN"
```
- [ ] Returns 404 Not Found (Jane doesn't own this project)

### Test 23: Jane Creates Her Own Project
```bash
curl -X POST http://localhost:8080/api/projects \
  -H "Authorization: Bearer JANE_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"title":"Jane Project","description":"Jane work"}'
```
- [ ] Returns 201 Created
- [ ] `userId` is different from John's projects

### Test 24: Jane Gets Her Projects
```bash
curl -X GET http://localhost:8080/api/projects \
  -H "Authorization: Bearer JANE_TOKEN"
```
- [ ] Returns 200 OK
- [ ] Array contains only Jane's project (not John's)

## 🗃️ Database Verification

### Test 25: Check Database Directly
```bash
docker exec -it taskflow-postgres psql -U taskflow_user -d taskflow_db
```

In PostgreSQL:
```sql
\dt
SELECT * FROM users;
SELECT * FROM projects;
SELECT * FROM tasks;
\q
```
- [ ] Tables exist: users, projects, tasks
- [ ] 3 users in users table
- [ ] Projects and tasks created in previous tests are visible

## 🧹 Cleanup and Restart Tests

### Test 26: Stop and Restart Database
```bash
docker-compose down
docker-compose up -d
```
Stop application (Ctrl+C), then restart:
```bash
mvn spring-boot:run
```
- [ ] Users are reseeded
- [ ] Previous projects/tasks are gone (fresh database)

### Test 27: Persistent Data Test
Create a project, stop application, restart, check if project still exists:
```bash
# Create project
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"john@example.com","password":"password123"}'

curl -X POST http://localhost:8080/api/projects \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"title":"Persistent Project"}'

# Stop application (Ctrl+C)
# Restart: mvn spring-boot:run

# Login again and check
curl -X GET http://localhost:8080/api/projects \
  -H "Authorization: Bearer NEW_TOKEN"
```
- [ ] Project still exists after restart

## 📝 Final Checks

- [ ] No errors in application logs
- [ ] All tests passed
- [ ] Database is working correctly
- [ ] JWT authentication is working
- [ ] User authorization is enforced
- [ ] Validation is working
- [ ] Error handling is working

## 🎉 Success Criteria

If all checkboxes above are checked, the API is fully functional and ready for use!

---

## 🐛 Troubleshooting

**Issue**: Can't connect to database
- **Solution**: Check Docker is running, verify container with `docker ps`

**Issue**: Port already in use
- **Solution**: Change port in application.properties or docker-compose.yml

**Issue**: JWT token expired
- **Solution**: Login again to get a new token

**Issue**: 401 Unauthorized
- **Solution**: Ensure you're using `Bearer YOUR_TOKEN` format

**Issue**: 403 Forbidden
- **Solution**: You're trying to access resources you don't own

---

## 📊 Test Results Template

```
Date: ___________
Tester: ___________

Pre-Flight Checks: ☐ PASS ☐ FAIL
Database Setup: ☐ PASS ☐ FAIL
Application Startup: ☐ PASS ☐ FAIL
Authentication Tests (1-3): ☐ PASS ☐ FAIL
Project Tests (4-9): ☐ PASS ☐ FAIL
Task Tests (10-17): ☐ PASS ☐ FAIL
Progress Tests (18-20): ☐ PASS ☐ FAIL
Authorization Tests (21-24): ☐ PASS ☐ FAIL
Database Verification (25): ☐ PASS ☐ FAIL
Cleanup Tests (26-27): ☐ PASS ☐ FAIL

Overall Status: ☐ PASS ☐ FAIL

Notes:
________________
________________
________________
```
