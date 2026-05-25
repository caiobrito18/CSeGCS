# MyLibrary v1.0.0 Installation Guide

**Last Updated**: December 19, 2024  
**Version**: 1.0.0  
**Status**: Production Ready

---

## 📋 Table of Contents

1. [Prerequisites](#prerequisites)
2. [System Requirements](#system-requirements)
3. [Installation Steps](#installation-steps)
4. [Verification Steps](#verification-steps)
5. [Troubleshooting](#troubleshooting)
6. [First Run](#first-run)

---

## Prerequisites

Before installing MyLibrary, ensure you have the following:

### Required Software

1. **Java Development Kit (JDK) 17+**
   - Download from: https://www.oracle.com/java/technologies/downloads/
   - Verify installation:
     ```bash
     java -version
     # Should show version 17 or higher
     ```

2. **Apache Maven 3.9+**
   - Download from: https://maven.apache.org/download.cgi
   - Verify installation:
     ```bash
     mvn -version
     # Should show Maven 3.9 or higher
     ```

3. **Node.js 20+ and npm 10+**
   - Download from: https://nodejs.org/
   - Verify installation:
     ```bash
     node --version
     npm --version
     # Should show Node 20+ and npm 10+
     ```

4. **Git**
   - Download from: https://git-scm.com/
   - Verify installation:
     ```bash
     git --version
     ```

5. **Angular CLI (for frontend development)**
   ```bash
   npm install -g @angular/cli@17
   ng version
   ```

### Optional but Recommended

- **Docker** (for containerized deployment)
- **PostgreSQL 15+** (for production database)
- **Postman** or **Insomnia** (for API testing)

---

## System Requirements

### Minimum Specifications

| Component | Minimum | Recommended |
|-----------|---------|-------------|
| CPU | 2 cores | 4+ cores |
| RAM | 4 GB | 8+ GB |
| Disk Space | 2 GB | 5+ GB |
| OS | Windows 10, macOS 10.14+, Ubuntu 18.04+ | Latest LTS version |

### Network Requirements

- Internet connection (for downloading dependencies)
- Ports available:
  - `8080` - Backend API
  - `4200` - Frontend development server

---

## Installation Steps

### Step 1: Clone the Repository

```bash
# Clone the repository
git clone https://github.com/your-org/mylibrary-gcs.git
cd mylibrary-gcs

# Verify structure
ls -la
# Should show: backend/, frontend/, README.md, CHANGELOG.md, etc.
```

### Step 2: Backend Installation

#### 2.1 Navigate to Backend Directory
```bash
cd backend
```

#### 2.2 Build with Maven
```bash
mvn clean install
```

**Expected Output:**
```
[INFO] BUILD SUCCESS
[INFO] Total time: XX.XXX s
[INFO] Finished at: 2024-XX-XX
```

#### 2.3 Verify Maven Installation
```bash
mvn --version
java -version
```

### Step 3: Frontend Installation

#### 3.1 Navigate to Frontend Directory
```bash
cd ../frontend
# or from project root: cd frontend
```

#### 3.2 Install Node Dependencies
```bash
npm install
```

**Expected Output:**
```
added XXX packages in XXs
```

#### 3.3 Verify Angular CLI
```bash
ng version
```

Should show Angular 17+ installed.

### Step 4: Database Setup

MyLibrary uses **H2 Database** by default for development.

#### 4.1 H2 Database (Development - Automatic)
The H2 database is automatically configured in `application.properties`:
- Database: H2 in-memory
- Console: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (empty)

#### 4.2 PostgreSQL Setup (Production)

If using PostgreSQL in production:

**Install PostgreSQL:**
```bash
# macOS
brew install postgresql

# Windows
# Download from https://www.postgresql.org/download/windows/

# Ubuntu/Debian
sudo apt-get install postgresql postgresql-contrib
```

**Create Database:**
```sql
CREATE USER mylibrary WITH PASSWORD 'mylibrary123';
CREATE DATABASE mylibrary_db OWNER mylibrary;
GRANT ALL PRIVILEGES ON DATABASE mylibrary_db TO mylibrary;
```

**Update Configuration:**

Edit `backend/src/main/resources/application-prod.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/mylibrary_db
spring.datasource.username=mylibrary
spring.datasource.password=mylibrary123
spring.datasource.driver-class-name=org.postgresql.Driver
```

---

## Verification Steps

### Step 1: Backend Verification

#### 1.1 Start Backend Server
```bash
cd backend
mvn spring-boot:run
```

**Expected Output:**
```
[main] o.s.b.w.embedded.tomcat.TomcatWebServer : Tomcat started on port(s): 8080
[main] c.m.MyLibraryApplication : Started MyLibraryApplication
```

#### 1.2 Test Backend Health
```bash
# In a new terminal
curl http://localhost:8080/actuator/health

# Expected response
{"status":"UP"}
```

#### 1.3 Test API Endpoints
```bash
# Get all categories (should return empty array initially)
curl http://localhost:8080/api/categorias
# Response: []
```

### Step 2: Frontend Verification

#### 2.1 Start Frontend Development Server
```bash
cd frontend
ng serve
```

**Expected Output:**
```
✔ Compiled successfully.
⠋ Compiling...
Application bundle generated successfully.
```

#### 2.2 Access Frontend
Open browser and navigate to:
```
http://localhost:4200
```

You should see the MyLibrary application interface.

#### 2.3 Verify Components Load
- Header displays "MyLibrary - Gerencimento de Acervo"
- Navigation menu is visible
- Dashboard loads without errors
- No console errors (check browser F12 console)

### Step 3: Integration Verification

With both backend and frontend running:

1. Navigate to Categories page
2. Try creating a new category
3. Verify it appears in the list
4. Check browser network tab shows API calls to `http://localhost:8080/api/categorias`

---

## First Run

### 1. Backend First Run

```bash
cd backend
mvn spring-boot:run
```

**What happens:**
- H2 database is created in memory
- Tables are automatically created via JPA
- Application starts on port 8080
- Initial data (if configured) is loaded

**Log into H2 Console:**
1. Open: http://localhost:8080/h2-console
2. JDBC URL: `jdbc:h2:mem:testdb`
3. Username: `sa`
4. Password: (leave empty)
5. Click "Connect"

### 2. Frontend First Run

```bash
cd frontend
npm install  # If not already done
ng serve
```

**What happens:**
- Dependencies are downloaded
- Application is compiled
- Dev server starts on port 4200
- Opens in default browser (may need manual refresh)

### 3. Create Test Data

Using the UI or API:

**Create a Category:**
```bash
curl -X POST http://localhost:8080/api/categorias \
  -H "Content-Type: application/json" \
  -d '{"nome":"Ficção Científica"}'
```

**Register a Book:**
```bash
curl -X POST http://localhost:8080/api/livros \
  -H "Content-Type: application/json" \
  -d '{
    "isbn":"978-0-13-595705-9",
    "titulo":"Clean Code",
    "autor":"Robert C. Martin",
    "ano":2008,
    "categoria":{"id":1}
  }'
```

---

## Troubleshooting

### Java/Maven Issues

**Error: `java: command not found`**
- Solution: Install JDK 17+ and add to PATH
- Verify: `java -version`

**Error: `mvn: command not found`**
- Solution: Install Maven 3.9+ and add to PATH
- Verify: `mvn --version`

**Error: `BUILD FAILURE`**
- Solution: 
  ```bash
  mvn clean install -X  # Run with debug output
  mvn clean            # Clean old build artifacts
  ```

### Node/npm Issues

**Error: `npm: command not found`**
- Solution: Install Node.js 20+ from https://nodejs.org/
- Verify: `npm --version`

**Error: Dependency conflicts**
- Solution:
  ```bash
  npm ci                    # Clean install
  rm -rf node_modules package-lock.json  # Windows: del node_modules, package-lock.json
  npm install
  ```

### Port Conflicts

**Error: `Address already in use: port 8080`**
- Solution: Kill process using port 8080
  ```bash
  # macOS/Linux
  lsof -i :8080
  kill -9 <PID>
  
  # Windows
  netstat -ano | findstr :8080
  taskkill /PID <PID> /F
  ```

**Error: `Port 4200 already in use`**
- Solution: Use different port
  ```bash
  ng serve --port 4201
  ```

### Database Issues

**Error: `H2 database locked`**
- Solution: H2 doesn't support multiple connections
  - Ensure only one backend instance is running
  - Restart the application

**PostgreSQL Connection Error**
- Verify PostgreSQL is running
- Check username/password in `application-prod.properties`
- Test connection:
  ```bash
  psql -U mylibrary -d mylibrary_db
  ```

### Compilation Issues

**Frontend Compilation Error**
```bash
ng build --configuration development
ng cache clean  # Clear Angular cache
npm install     # Reinstall dependencies
```

**Backend Compilation Error**
```bash
mvn clean compile  # Clean and recompile
mvn dependency:tree  # Check dependency tree
```

---

## Next Steps After Installation

1. **Read the Documentation**
   - [RELEASE-NOTES-v1.0.0.md](RELEASE-NOTES-v1.0.0.md) - What's new
   - [API-REFERENCE.md](LIVRO-API-REFERENCE.md) - API endpoints
   - [DEVELOPMENT.md](DEVELOPMENT.md) - Development setup

2. **Run Tests**
   ```bash
   # Backend
   cd backend
   mvn test
   
   # Frontend
   cd frontend
   ng test
   ```

3. **Explore the Application**
   - Create categories
   - Register books
   - Create loans
   - View dashboard

4. **Customize for Production**
   - See [DEPLOYMENT.md](DEPLOYMENT.md)
   - Configure PostgreSQL
   - Set up environment variables
   - Review security settings

---

## Support & Help

### Common Resources

- **GitHub Issues**: Report bugs or request features
- **Discussion Forum**: Ask questions and share ideas
- **API Documentation**: [API-REFERENCE.md](LIVRO-API-REFERENCE.md)
- **FAQ**: [FAQ.md](FAQ.md)

### Getting Help

1. Check [Troubleshooting](#troubleshooting) above
2. Review [FAQ.md](FAQ.md)
3. Check GitHub Issues for similar problems
4. Open a new issue with details:
   - OS and version
   - Java/Node version
   - Complete error message
   - Steps to reproduce

---

## What's Next?

Now that MyLibrary is installed and running:

1. **Explore the UI**: Navigate through all pages and features
2. **Test the API**: Use curl or Postman to test endpoints
3. **Create Test Data**: Add categories, books, and loans
4. **Run Tests**: Execute the test suite to verify everything
5. **Deploy**: Follow [DEPLOYMENT.md](DEPLOYMENT.md) for production setup

---

**Installation Complete!** 🎉

Your MyLibrary v1.0.0 is ready to use. Start with the [RELEASE-NOTES-v1.0.0.md](RELEASE-NOTES-v1.0.0.md) for an overview of features.

For questions or issues, see [Troubleshooting](#troubleshooting) or [FAQ.md](FAQ.md).
