# Local Development Setup

## Prerequisites

- Java 17+
- Node.js 20+
- Maven 3.8+
- Git

## Backend Setup

1. Navigate to backend directory:
   ```bash
   cd backend
   ```

2. Install Maven dependencies:
   ```bash
   mvn clean install
   ```

3. Build the project:
   ```bash
   mvn clean package
   ```

4. Run the application:
   ```bash
   mvn spring-boot:run
   ```

The backend API will be available at `http://localhost:8080`

## Frontend Setup

1. Navigate to frontend directory:
   ```bash
   cd frontend
   ```

2. Install Node dependencies:
   ```bash
   npm install
   ```

3. Start development server:
   ```bash
   npm start
   ```

The frontend will be available at `http://localhost:4200`

## Environment Variables

### Backend
Create `backend/src/main/resources/application.properties`:
```properties
spring.application.name=mylibrary-backend
server.port=8080
```

### Frontend
Environment variables are typically set in `.angular-cli.json` or environment files.

## Database

The project uses H2 in-memory database by default. No setup required for local development.

## IDE Setup

### IntelliJ IDEA
1. Import the project as Maven project
2. Go to Settings → Build, Execution, Deployment → Build Tools → Maven
3. Set JDK to 17+

### VS Code
1. Install extensions: Extension Pack for Java, Angular Language Service
2. Configure workspace settings for formatting preferences

## Troubleshooting

- **Port already in use**: Change port in `application.properties` or `ng serve --port XXXX`
- **Dependencies not downloading**: Clear Maven cache: `mvn clean`
- **Node version mismatch**: Use nvm to switch to Node 20
