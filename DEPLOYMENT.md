# Deployment Procedures

## Environments

- **Local**: Development machine
- **Staging**: Pre-production testing (optional)
- **Production**: Live environment

## Local Deployment

### Run Locally

**Backend:**
```bash
cd backend
mvn spring-boot:run
```

**Frontend:**
```bash
cd frontend
npm start
```

Access the application:
- Frontend: http://localhost:4200
- Backend API: http://localhost:8080

## Production Deployment

### Prerequisites

- Server/cloud platform ready
- Java 17+ runtime
- Node.js 20+ (for frontend build)
- Database configured

### Build for Production

**Backend:**
```bash
cd backend
mvn clean package -Pprod
# Creates: target/mylibrary-backend-1.0.0.jar
```

**Frontend:**
```bash
cd frontend
npm run build
# Creates: dist/mylibrary-frontend/
```

### Deployment Steps

1. **Build Artifacts**
   - Create JAR and distribution builds
   - Verify all tests pass
   - Check no secrets in artifacts

2. **Deploy Backend**
   ```bash
   scp backend/target/mylibrary-backend-1.0.0.jar user@server:/opt/mylibrary/
   ssh user@server
   java -jar /opt/mylibrary/mylibrary-backend-1.0.0.jar
   ```

3. **Deploy Frontend**
   ```bash
   scp -r frontend/dist/* user@server:/var/www/mylibrary/
   # Or upload to CDN/static hosting
   ```

4. **Configure Environment**
   - Set application properties
   - Configure database connections
   - Setup CORS if needed

### Docker Deployment (Optional)

**Build Images:**
```bash
docker build -t mylibrary-backend:1.0.0 ./backend
docker build -t mylibrary-frontend:1.0.0 ./frontend
```

**Run with Docker Compose:**
```yaml
version: '3.8'
services:
  backend:
    image: mylibrary-backend:1.0.0
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
  
  frontend:
    image: mylibrary-frontend:1.0.0
    ports:
      - "80:80"
```

## Rollback Procedure

If issues occur after deployment:

1. **Identify Issue**
   - Check logs and monitoring
   - Verify recent changes

2. **Rollback Steps**
   ```bash
   # Stop current version
   docker stop mylibrary-backend mylibrary-frontend
   
   # Start previous version
   docker run -d --name mylibrary-backend mylibrary-backend:previous-version
   docker run -d --name mylibrary-frontend mylibrary-frontend:previous-version
   ```

3. **Investigate**
   - Review logs
   - Check database state
   - Document issue

4. **Redeploy with Fix**
   - Fix identified issues
   - Run tests
   - Deploy again

## Health Checks

**Backend Health Endpoint:**
```bash
curl http://localhost:8080/actuator/health
```

**Frontend Availability:**
```bash
curl -I http://localhost:4200/
```

## Monitoring Post-Deployment

- Check application logs
- Monitor resource usage
- Verify database connectivity
- Test API endpoints
- Monitor user-facing functionality

## Release Notes Template

```
# Release v1.0.0

## Features
- Feature 1
- Feature 2

## Bug Fixes
- Fixed issue #123
- Fixed issue #124

## Breaking Changes
- None

## Migration Guide
- Step 1
- Step 2

## Deployment Time
- Estimated: 5-10 minutes
- Zero downtime: Yes/No
```
