# MyLibrary Production Setup Guide

**Version**: 1.0.0  
**Last Updated**: December 19, 2024  
**Purpose**: Complete production environment setup

---

## 📋 Prerequisites

### Hardware Requirements

- **CPU**: 4+ cores (minimum 2)
- **RAM**: 8 GB (minimum 4 GB)
- **Disk**: 50 GB SSD (minimum 20 GB)
- **Network**: 1Gbps connection
- **Uptime**: 99.9%+ availability

### Software Requirements

- **Operating System**: Ubuntu 20.04 LTS, CentOS 8+, or Windows Server 2019+
- **Java**: OpenJDK 17 LTS
- **Node.js**: 20 LTS (for frontend build)
- **PostgreSQL**: 15+
- **Docker**: 20.10+ (optional but recommended)
- **Git**: Latest version

### Network Requirements

- **Ports to Open**:
  - 80 (HTTP)
  - 443 (HTTPS)
  - 8080 (Backend API, internal)
  - 5432 (PostgreSQL, internal)
- **Firewall**: Configure appropriately
- **SSL/TLS**: Certificate prepared

---

## 🔧 System Configuration

### Linux Setup (Ubuntu 20.04 LTS)

#### 1. Update System
```bash
sudo apt-get update
sudo apt-get upgrade -y
sudo apt-get install -y build-essential
```

#### 2. Install Java 17
```bash
sudo apt-get install -y openjdk-17-jdk-headless
java -version
```

#### 3. Install PostgreSQL 15
```bash
sudo apt-get install -y postgresql postgresql-contrib
sudo systemctl start postgresql
sudo systemctl enable postgresql
```

#### 4. Configure PostgreSQL
```bash
sudo -u postgres psql
```

```sql
CREATE USER mylibrary WITH PASSWORD 'your-secure-password';
CREATE DATABASE mylibrary_db OWNER mylibrary;
GRANT ALL PRIVILEGES ON DATABASE mylibrary_db TO mylibrary;
\q
```

#### 5. Verify PostgreSQL
```bash
psql -h localhost -U mylibrary -d mylibrary_db
```

### Windows Server Setup

#### 1. Install Java 17
- Download from oracle.com
- Add to PATH
- Verify: `java -version`

#### 2. Install PostgreSQL
- Download from postgresql.org
- Run installer
- Configure port (default 5432)
- Create database and user as above

#### 3. Install Node.js (for builds)
- Download from nodejs.org
- Add to PATH
- Verify: `node --version`, `npm --version`

---

## 📦 Application Deployment

### Backend Deployment

#### 1. Prepare Application Directory
```bash
sudo mkdir -p /opt/mylibrary
sudo mkdir -p /opt/mylibrary/logs
sudo mkdir -p /opt/mylibrary/config
sudo chown -R $USER:$USER /opt/mylibrary
```

#### 2. Copy Application Files
```bash
# From your build location
cp target/mylibrary-1.0.0.jar /opt/mylibrary/
cp src/main/resources/application-prod.properties /opt/mylibrary/config/
```

#### 3. Configure Production Properties

Edit `/opt/mylibrary/config/application-prod.properties`:

```properties
# Server Configuration
server.port=8080
server.servlet.context-path=/
server.shutdown=graceful

# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/mylibrary_db
spring.datasource.username=mylibrary
spring.datasource.password=your-secure-password
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQL15Dialect
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=true

# Logging
logging.level.root=INFO
logging.level.com.mylibrary=INFO
logging.file.name=/opt/mylibrary/logs/mylibrary.log
logging.file.max-size=10MB
logging.file.max-history=30

# Actuator
management.endpoints.web.exposure.include=health,metrics,info
management.endpoint.health.show-details=when-authorized
```

#### 4. Create Systemd Service

Create `/etc/systemd/system/mylibrary.service`:

```ini
[Unit]
Description=MyLibrary Application
After=syslog.target network.target postgresql.service
Requires=postgresql.service

[Service]
Type=simple
User=ubuntu
WorkingDirectory=/opt/mylibrary
ExecStart=/usr/bin/java -Xmx1024m -Xms512m -Dspring.profiles.active=prod -jar mylibrary-1.0.0.jar
Restart=on-failure
RestartSec=10
StandardOutput=append:/opt/mylibrary/logs/mylibrary.log
StandardError=append:/opt/mylibrary/logs/mylibrary-error.log
EnvironmentFile=/opt/mylibrary/config/.env

[Install]
WantedBy=multi-user.target
```

#### 5. Create Environment File

Create `/opt/mylibrary/config/.env`:

```bash
JAVA_OPTS="-Xmx1024m -Xms512m"
SPRING_PROFILES_ACTIVE=prod
DATABASE_URL=jdbc:postgresql://localhost:5432/mylibrary_db
DATABASE_USER=mylibrary
DATABASE_PASSWORD=your-secure-password
```

#### 6. Start Backend Service
```bash
sudo systemctl daemon-reload
sudo systemctl start mylibrary
sudo systemctl enable mylibrary
sudo systemctl status mylibrary
```

#### 7. Verify Backend
```bash
curl -s http://localhost:8080/actuator/health | jq
```

### Frontend Deployment

#### 1. Build Frontend
```bash
cd frontend
npm install
ng build --configuration production
```

#### 2. Prepare Web Server Directory
```bash
sudo mkdir -p /var/www/mylibrary
sudo chown -R www-data:www-data /var/www/mylibrary
```

#### 3. Deploy Frontend
```bash
sudo cp -r dist/mylibrary/* /var/www/mylibrary/
```

#### 4. Configure Nginx

Create `/etc/nginx/sites-available/mylibrary`:

```nginx
server {
    listen 80;
    server_name your-domain.com www.your-domain.com;

    # Redirect HTTP to HTTPS
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name your-domain.com www.your-domain.com;

    # SSL Configuration
    ssl_certificate /etc/ssl/certs/your-cert.crt;
    ssl_certificate_key /etc/ssl/private/your-key.key;
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers HIGH:!aNULL:!MD5;

    # Compression
    gzip on;
    gzip_types text/plain text/css application/json application/javascript text/javascript;

    # Root directory
    root /var/www/mylibrary;
    index index.html;

    # Frontend routes - serve index.html for all unknown routes
    location / {
        try_files $uri $uri/ /index.html;
    }

    # API proxy
    location /api/ {
        proxy_pass http://localhost:8080/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_read_timeout 90;
    }

    # Static assets
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }

    # Deny access to hidden files
    location ~ /\. {
        deny all;
    }
}
```

#### 5. Enable Nginx Site
```bash
sudo ln -s /etc/nginx/sites-available/mylibrary /etc/nginx/sites-enabled/
sudo systemctl reload nginx
```

---

## 🔐 Security Configuration

### SSL/TLS Setup

#### Using Let's Encrypt (Free)
```bash
sudo apt-get install -y certbot python3-certbot-nginx
sudo certbot certonly --nginx -d your-domain.com -d www.your-domain.com
```

#### Update Nginx with Certificate
```bash
# Edit nginx config with certificate paths
ssl_certificate /etc/letsencrypt/live/your-domain.com/fullchain.pem;
ssl_certificate_key /etc/letsencrypt/live/your-domain.com/privkey.pem;
```

#### Auto-Renew Certificate
```bash
sudo systemctl enable certbot.timer
sudo systemctl start certbot.timer
```

### Firewall Configuration

```bash
sudo ufw default deny incoming
sudo ufw default allow outgoing
sudo ufw allow 22/tcp      # SSH
sudo ufw allow 80/tcp      # HTTP
sudo ufw allow 443/tcp     # HTTPS
sudo ufw enable
```

### Database Security

```bash
# Restrict PostgreSQL to localhost
sudo nano /etc/postgresql/15/main/postgresql.conf
# Set: listen_addresses = 'localhost'

# Update pg_hba.conf for local connection only
sudo nano /etc/postgresql/15/main/pg_hba.conf
```

---

## 📊 Monitoring & Logging

### Application Monitoring

#### Health Check Endpoint
```bash
curl -s http://localhost:8080/actuator/health
```

#### Metrics Endpoint
```bash
curl -s http://localhost:8080/actuator/metrics
```

### Log Monitoring
```bash
# View recent logs
tail -f /opt/mylibrary/logs/mylibrary.log

# Search for errors
grep ERROR /opt/mylibrary/logs/mylibrary.log

# Log rotation (automatic with systemd)
```

### System Monitoring

#### Disk Space
```bash
df -h
du -sh /opt/mylibrary
du -sh /var/www/mylibrary
```

#### Database Backup
```bash
# Daily backup script
sudo crontab -e
```

Add:
```cron
0 2 * * * pg_dump -U mylibrary mylibrary_db > /backups/mylibrary_$(date +\%Y\%m\%d).sql
```

#### Create Backup Directory
```bash
sudo mkdir -p /backups
sudo chown postgres:postgres /backups
```

---

## 🔄 Backup & Recovery

### Database Backup

```bash
# Full backup
pg_dump -U mylibrary -d mylibrary_db > mylibrary_backup.sql

# Compress backup
pg_dump -U mylibrary -d mylibrary_db | gzip > mylibrary_backup.sql.gz
```

### Database Restore

```bash
# From backup file
psql -U mylibrary -d mylibrary_db < mylibrary_backup.sql

# From compressed file
gunzip -c mylibrary_backup.sql.gz | psql -U mylibrary -d mylibrary_db
```

### Automated Backups

Create `/opt/mylibrary/backup.sh`:

```bash
#!/bin/bash
BACKUP_DIR="/backups"
DATE=$(date +%Y%m%d_%H%M%S)
DB_NAME="mylibrary_db"
DB_USER="mylibrary"

# Create backup
pg_dump -U $DB_USER $DB_NAME | gzip > $BACKUP_DIR/mylibrary_$DATE.sql.gz

# Keep only last 30 days
find $BACKUP_DIR -name "mylibrary_*.sql.gz" -mtime +30 -delete

echo "Backup completed: $DATE"
```

Make executable:
```bash
chmod +x /opt/mylibrary/backup.sh
```

Schedule in crontab:
```bash
0 2 * * * /opt/mylibrary/backup.sh
```

---

## 📈 Performance Tuning

### Java Tuning

```properties
# In application-prod.properties
server.tomcat.threads.max=200
server.tomcat.threads.min-spare=10
server.connection-timeout=20000

# Heap size in systemd service
-Xmx2048m -Xms1024m
```

### PostgreSQL Tuning

Edit `/etc/postgresql/15/main/postgresql.conf`:

```conf
max_connections = 200
shared_buffers = 256MB
effective_cache_size = 1GB
maintenance_work_mem = 64MB
checkpoint_completion_target = 0.9
wal_buffers = 16MB
default_statistics_target = 100
```

Restart PostgreSQL:
```bash
sudo systemctl restart postgresql
```

### Nginx Tuning

```nginx
worker_processes auto;
worker_connections 1024;
client_max_body_size 50M;
```

---

## 🚨 Health Checks

### Manual Health Check
```bash
#!/bin/bash
# health-check.sh

# Check backend
BACKEND=$(curl -s http://localhost:8080/actuator/health | grep -o '"status":"UP"')

# Check database
DB=$(psql -h localhost -U mylibrary -d mylibrary_db -c "SELECT 1" 2>&1)

# Check frontend
FRONTEND=$(curl -s -o /dev/null -w "%{http_code}" http://localhost)

echo "Backend: $([[ $BACKEND ]] && echo 'OK' || echo 'FAIL')"
echo "Database: $([[ $DB == *'1'* ]] && echo 'OK' || echo 'FAIL')"
echo "Frontend: $([[ $FRONTEND == '200' ]] && echo 'OK' || echo 'FAIL')"
```

---

## 📝 Maintenance Procedures

### Regular Maintenance

- **Daily**: Check logs, monitor disk space
- **Weekly**: Review application metrics, database health
- **Monthly**: Update patches, optimize database
- **Quarterly**: Security audit, capacity planning

### Database Maintenance

```bash
# Analyze database
sudo -u postgres vacuumdb -U mylibrary -d mylibrary_db -a -z

# Check index health
sudo -u postgres psql -U mylibrary -d mylibrary_db -c "\d+"
```

---

## ✅ Verification Checklist

After setup completion:

- [ ] Backend running: `systemctl status mylibrary`
- [ ] Frontend accessible: `curl http://localhost`
- [ ] Database connected: `psql -U mylibrary -d mylibrary_db`
- [ ] SSL certificate valid: `openssl x509 -in cert.pem -text`
- [ ] Firewall rules applied: `ufw status`
- [ ] Backups working: Check `/backups` directory
- [ ] Logs being written: Check `/opt/mylibrary/logs`
- [ ] Health endpoint responds: `curl http://localhost:8080/actuator/health`

---

**Guide Version**: 1.0.0  
**Status**: Complete and Ready  
**Last Updated**: December 19, 2024
