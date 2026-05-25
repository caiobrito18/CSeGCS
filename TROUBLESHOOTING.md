# MyLibrary Troubleshooting Guide

**Version**: 1.0.0  
**Last Updated**: December 19, 2024  
**Purpose**: Diagnose and resolve common issues

---

## 🔧 Backend Issues

### Application Won't Start

#### Error: "Port 8080 already in use"

**Symptoms:**
```
Caused by: java.net.BindException: Address already in use
```

**Solutions:**

1. Find and kill process using port 8080:
```bash
# Linux/Mac
lsof -i :8080
kill -9 <PID>

# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

2. Use different port:
```properties
# In application.properties
server.port=8081
```

3. Check if backup instance running:
```bash
systemctl status mylibrary
systemctl stop mylibrary
```

---

#### Error: "Database connection refused"

**Symptoms:**
```
Could not get a resource from the pool
org.postgresql.util.PSQLException: Connection refused
```

**Solutions:**

1. Verify PostgreSQL running:
```bash
# Linux
sudo systemctl status postgresql

# Windows
# Check Services -> PostgreSQL
```

2. Verify connection string:
```properties
# In application.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/mylibrary_db
spring.datasource.username=mylibrary
spring.datasource.password=correct-password
```

3. Test connection directly:
```bash
psql -h localhost -U mylibrary -d mylibrary_db
```

4. Check PostgreSQL listening:
```bash
netstat -an | grep 5432
```

---

#### Error: "Java not found"

**Symptoms:**
```
'java' is not recognized as an internal or external command
```

**Solutions:**

1. Install Java 17:
   - Download from oracle.com
   - Install to default location

2. Add to PATH:
```bash
# Linux
echo "export PATH=$PATH:/usr/lib/jvm/java-17-openjdk/bin" >> ~/.bashrc
source ~/.bashrc

# Windows
# System Properties -> Environment Variables -> PATH -> Add Java path
```

3. Verify installation:
```bash
java -version
javac -version
```

---

#### Error: "Maven command not found"

**Symptoms:**
```
'mvn' is not recognized as an internal or external command
```

**Solutions:**

1. Install Maven 3.9+:
   - Download from maven.apache.org
   - Extract to folder
   - Add to PATH

2. Verify installation:
```bash
mvn --version
```

---

### Build Failures

#### Error: "BUILD FAILURE"

**Symptoms:**
```
[ERROR] BUILD FAILURE
[INFO] Total time: 1.234 s
```

**Solutions:**

1. Clean build:
```bash
mvn clean install
```

2. Check Java version:
```bash
java -version
# Should be 17+
```

3. Check dependencies:
```bash
mvn dependency:tree
mvn dependency:resolve
```

4. Clear Maven cache:
```bash
rm -rf ~/.m2/repository
mvn clean install
```

5. Check for syntax errors:
```bash
mvn compile
```

---

### Runtime Errors

#### Error: "NullPointerException" or "AttributeError"

**Symptoms:**
```
java.lang.NullPointerException: null
```

**Solutions:**

1. Check application logs:
```bash
tail -f logs/mylibrary.log
tail -100 logs/mylibrary.log | grep ERROR
```

2. Enable debug logging:
```properties
logging.level.com.mylibrary=DEBUG
```

3. Restart application with debug output:
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--debug"
```

---

#### Error: "Unique constraint violated"

**Symptoms:**
```
org.hibernate.exception.ConstraintViolationException: 
could not execute statement; SQL [...]
```

**Solutions:**

1. Check for duplicate data:
```sql
-- Check for duplicate categories
SELECT nome, COUNT(*) FROM categoria GROUP BY nome HAVING COUNT(*) > 1;

-- Check for duplicate ISBNs
SELECT isbn, COUNT(*) FROM livro GROUP BY isbn HAVING COUNT(*) > 1;
```

2. Delete duplicate entries:
```sql
-- Remove duplicates
DELETE FROM categoria WHERE id IN (
    SELECT id FROM categoria WHERE nome IN (
        SELECT nome FROM categoria GROUP BY nome HAVING COUNT(*) > 1
    ) AND id NOT IN (
        SELECT MIN(id) FROM categoria GROUP BY nome HAVING COUNT(*) > 1
    )
);
```

3. Check validation in service:
```java
// Ensure business logic validates uniqueness
if (categoriaRepository.existsByNome(categoria.getNome())) {
    throw new IllegalArgumentException("Category already exists");
}
```

---

## 🖥️ Frontend Issues

### Page Won't Load

#### Error: "Failed to load module"

**Symptoms:**
```
ERROR in src/app/...
Module not found: Can't resolve '@angular/...'
```

**Solutions:**

1. Install dependencies:
```bash
npm install
```

2. Clear npm cache:
```bash
npm cache clean --force
rm -rf node_modules
npm install
```

3. Check Node version:
```bash
node --version
# Should be 20+
npm --version
# Should be 10+
```

---

#### Error: "Compilation failed"

**Symptoms:**
```
ERROR in src/app/...
Type '...' is not assignable to type '...'
```

**Solutions:**

1. Check TypeScript version:
```bash
npm list typescript
# Should be 5+
```

2. Fix type errors:
```bash
ng build  # Shows all errors
```

3. Check tsconfig.json:
```json
{
  "compilerOptions": {
    "target": "ES2020",
    "module": "ES2020"
  }
}
```

---

### API Connection Issues

#### Error: "Failed to fetch from API"

**Symptoms:**
```
error: 0 Unknown Error
```

**Solutions:**

1. Check backend is running:
```bash
curl http://localhost:8080/actuator/health
```

2. Check API URL in environment:
```typescript
// src/environments/environment.ts
export const environment = {
  apiUrl: 'http://localhost:8080'
};
```

3. Check CORS configuration:
```properties
# In backend application.properties
server.servlet.context-path=/
```

4. Check network tab in browser console:
   - Press F12
   - Go to Network tab
   - Check failed requests
   - Look for CORS errors

---

#### Error: "401 Unauthorized"

**Symptoms:**
```
Status Code: 401
Response: Unauthorized
```

**Solutions:**

1. Check if authentication required:
   - Review API documentation
   - Check backend authorization

2. Add authentication token (if implemented):
```typescript
// In http interceptor
headers = headers.set('Authorization', `Bearer ${token}`);
```

---

### Component Issues

#### Error: "Component not rendering"

**Symptoms:**
- Blank page or empty section
- Component doesn't appear

**Solutions:**

1. Check browser console:
```bash
# Press F12 in browser
# Check Console tab for errors
```

2. Verify component is declared:
```typescript
@NgModule({
  declarations: [YourComponent],
  imports: [CommonModule]
})
export class YourModule { }
```

3. Check route configuration:
```typescript
const routes: Routes = [
  { path: 'your-path', component: YourComponent }
];
```

4. Verify template syntax:
```html
<!-- Check for correct syntax -->
<div *ngIf="condition">Shown if true</div>
<div *ngFor="let item of items">{{ item.name }}</div>
```

---

#### Error: "Two-way binding not working"

**Symptoms:**
- Input field changes don't reflect
- Component state not updating

**Solutions:**

1. Import FormsModule:
```typescript
import { FormsModule } from '@angular/forms';

@NgModule({
  imports: [FormsModule]
})
```

2. Use correct syntax:
```html
<!-- Correct two-way binding -->
<input [(ngModel)]="propertyName">

<!-- Alternatively, use reactive forms -->
<input [formControl]="formControl">
```

3. For reactive forms, import ReactiveFormsModule:
```typescript
import { ReactiveFormsModule } from '@angular/forms';

@NgModule({
  imports: [ReactiveFormsModule]
})
```

---

## 🗄️ Database Issues

### Cannot Connect to Database

#### Error: "psql: could not connect to server"

**Symptoms:**
```
psql: could not connect to server: 
Connection refused
Is the server running on host "localhost" (127.0.0.1)?
```

**Solutions:**

1. Check if PostgreSQL running:
```bash
# Linux
sudo systemctl status postgresql
sudo systemctl start postgresql

# Windows
# Check Services panel for PostgreSQL
```

2. Check PostgreSQL listening:
```bash
netstat -an | grep 5432
```

3. Check pg_hba.conf:
```bash
# Linux
sudo nano /etc/postgresql/15/main/pg_hba.conf
# Ensure local connections allowed

# Windows
# Check: C:\Program Files\PostgreSQL\15\data\pg_hba.conf
```

4. Verify credentials:
```bash
psql -h localhost -U mylibrary -d mylibrary_db -W
# Enter password when prompted
```

---

### Data Issues

#### Error: "Data not appearing in UI"

**Symptoms:**
- List is empty
- Expected data missing

**Solutions:**

1. Check database directly:
```sql
SELECT * FROM categoria;
SELECT * FROM livro;
SELECT * FROM emprestimo;
```

2. Check backend logs:
```bash
tail -f logs/mylibrary.log
grep -i ERROR logs/mylibrary.log
```

3. Check API responses:
```bash
curl -s http://localhost:8080/api/categorias | jq
curl -s http://localhost:8080/api/livros | jq
```

4. Verify data was created:
   - Check frontend console for errors
   - Check network tab for failed requests
   - Verify POST responses are 201/200

---

#### Error: "Duplicate key value"

**Symptoms:**
```
ERROR: duplicate key value violates unique constraint
```

**Solutions:**

1. Find duplicates:
```sql
SELECT isbn, COUNT(*) FROM livro 
GROUP BY isbn 
HAVING COUNT(*) > 1;
```

2. Delete duplicates:
```sql
DELETE FROM livro WHERE id NOT IN (
  SELECT MIN(id) FROM livro GROUP BY isbn
);
```

3. Check validation logic in service

---

## 🔒 Security Issues

### CORS Errors

**Error:** "Access to XMLHttpRequest blocked by CORS policy"

**Solutions:**

1. Enable CORS in backend:
```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            .allowedOrigins("http://localhost:4200")
            .allowedMethods("GET", "POST", "PUT", "DELETE")
            .allowedHeaders("*")
            .allowCredentials(true);
    }
}
```

2. Update for production:
```java
.allowedOrigins("https://your-domain.com")
```

---

### SSL Certificate Issues

**Error:** "SSL certificate problem"

**Solutions:**

1. Verify certificate:
```bash
openssl x509 -in cert.pem -text -noout
```

2. Check expiration:
```bash
openssl x509 -in cert.pem -noout -dates
```

3. For self-signed (dev only):
```bash
openssl req -x509 -newkey rsa:4096 -keyout key.pem -out cert.pem -days 365
```

4. In production, use Let's Encrypt:
```bash
sudo certbot certonly --nginx -d your-domain.com
```

---

## 📊 Performance Issues

### Slow API Responses

**Symptom:** API takes > 500ms

**Solutions:**

1. Check database queries:
```bash
# Enable query logging
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

2. Add database indexes:
```sql
CREATE INDEX idx_livro_categoria_id ON livro(categoria_id);
CREATE INDEX idx_livro_isbn ON livro(isbn);
CREATE INDEX idx_emprestimo_livro_id ON emprestimo(livro_id);
```

3. Optimize queries (avoid N+1):
```java
// Use JOIN FETCH
@Query("SELECT l FROM Livro l JOIN FETCH l.categoria")
List<Livro> findAllWithCategoria();
```

4. Check connection pool:
```properties
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
```

---

### Slow Page Load

**Symptom:** Frontend takes > 2 seconds

**Solutions:**

1. Build production bundle:
```bash
ng build --configuration production
```

2. Check bundle size:
```bash
ng build --stats-json
# Analyze with: npm install -g webpack-bundle-analyzer
webpack-bundle-analyzer dist/mylibrary/stats.json
```

3. Lazy load routes:
```typescript
const routes: Routes = [
  { path: 'categories', loadChildren: () => import('./categories/categories.module').then(m => m.CategoriesModule) }
];
```

4. Enable gzip compression in Nginx:
```nginx
gzip on;
gzip_types text/plain text/css application/json text/javascript;
```

---

## 🚨 Emergency Procedures

### System Down - Full Recovery

1. **Check status:**
```bash
systemctl status mylibrary
systemctl status postgresql
systemctl status nginx
```

2. **Restart services:**
```bash
sudo systemctl restart postgresql
sudo systemctl restart mylibrary
sudo systemctl restart nginx
```

3. **Check logs:**
```bash
tail -100 /opt/mylibrary/logs/mylibrary.log
```

4. **Restore from backup if needed:**
```bash
psql -U mylibrary -d mylibrary_db < /backups/latest_backup.sql
```

5. **Verify health:**
```bash
curl http://localhost:8080/actuator/health
curl http://localhost/
```

---

## 📞 Getting Help

### Before Contacting Support

1. ☐ Check this troubleshooting guide
2. ☐ Check application logs
3. ☐ Check system logs (journalctl, event viewer)
4. ☐ Verify prerequisites installed
5. ☐ Test with simple curl request
6. ☐ Review documentation

### When Reporting Issues

Include:
- Error message (full stack trace if available)
- Steps to reproduce
- Environment (OS, Java version, Node version)
- Logs (last 100 lines of error log)
- What you've already tried

---

**Troubleshooting Guide Version**: 1.0.0  
**Last Updated**: December 19, 2024  
**Status**: Ready to Use
