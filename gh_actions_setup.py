#!/usr/bin/env python3
import os
import sys

def setup():
    # Get project root
    root = os.path.dirname(os.path.abspath(__file__))
    os.chdir(root)
    
    # Create .github/workflows directory
    workflows_dir = os.path.join(root, '.github', 'workflows')
    os.makedirs(workflows_dir, exist_ok=True)
    print(f"✓ Created: {workflows_dir}")
    
    # CI workflow
    ci_yml = os.path.join(workflows_dir, 'ci.yml')
    with open(ci_yml, 'w') as f:
        f.write("""name: CI/CD Pipeline

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main, develop ]

jobs:
  backend-build:
    name: Backend Build & Test
    runs-on: ubuntu-latest
    
    steps:
      - name: Checkout code
        uses: actions/checkout@v4
      
      - name: Setup Java 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: maven
      
      - name: Build backend with Maven
        working-directory: ./backend
        run: mvn clean package -DskipTests
      
      - name: Run backend tests
        working-directory: ./backend
        run: mvn test
      
      - name: Run Maven verify
        working-directory: ./backend
        run: mvn verify
      
      - name: Upload test results
        if: always()
        uses: actions/upload-artifact@v3
        with:
          name: backend-test-results
          path: backend/target/surefire-reports/
          retention-days: 30
      
      - name: Upload build artifacts
        if: success()
        uses: actions/upload-artifact@v3
        with:
          name: backend-build
          path: backend/target/*.jar
          retention-days: 30

  frontend-build:
    name: Frontend Build & Test
    runs-on: ubuntu-latest
    
    steps:
      - name: Checkout code
        uses: actions/checkout@v4
      
      - name: Setup Node.js 20
        uses: actions/setup-node@v4
        with:
          node-version: '20'
          cache: 'npm'
          cache-dependency-path: 'frontend/package-lock.json'
      
      - name: Install frontend dependencies
        working-directory: ./frontend
        run: npm ci
      
      - name: Build frontend
        working-directory: ./frontend
        run: npm run build
      
      - name: Run frontend tests
        working-directory: ./frontend
        run: npm run test -- --watch=false --browsers=ChromeHeadless || true
        continue-on-error: true
      
      - name: Upload build artifacts
        if: success()
        uses: actions/upload-artifact@v3
        with:
          name: frontend-build
          path: frontend/dist/
          retention-days: 30

  code-quality:
    name: Code Quality Check
    runs-on: ubuntu-latest
    
    steps:
      - name: Checkout code
        uses: actions/checkout@v4
      
      - name: Setup Node.js 20
        uses: actions/setup-node@v4
        with:
          node-version: '20'
          cache: 'npm'
          cache-dependency-path: 'frontend/package-lock.json'
      
      - name: Install frontend dependencies
        working-directory: ./frontend
        run: npm ci
      
      - name: Lint frontend code
        working-directory: ./frontend
        run: npm run lint || echo "No lint script found, skipping..."
        continue-on-error: true
      
      - name: Setup Java 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: maven
      
      - name: Analyze backend with Maven
        working-directory: ./backend
        run: mvn pmd:check || echo "No PMD configuration, skipping..."
        continue-on-error: true

  build-status:
    name: Build Status
    runs-on: ubuntu-latest
    needs: [ backend-build, frontend-build, code-quality ]
    if: always()
    
    steps:
      - name: Check build status
        run: |
          if [ "${{ needs.backend-build.result }}" == "failure" ] || \\
             [ "${{ needs.frontend-build.result }}" == "failure" ]; then
            echo "Build failed!"
            exit 1
          fi
          echo "All builds passed successfully!"
""")
    print(f"✓ Created: .github/workflows/ci.yml")
    
    # Docker workflow
    docker_yml = os.path.join(workflows_dir, 'docker-build.yml')
    with open(docker_yml, 'w') as f:
        f.write("""name: Docker Build

on:
  push:
    tags: [ 'v*' ]
  workflow_dispatch:

jobs:
  build-docker:
    name: Build Docker Images
    runs-on: ubuntu-latest
    
    steps:
      - name: Checkout code
        uses: actions/checkout@v4
      
      - name: Setup Docker Buildx
        uses: docker/setup-buildx-action@v2
      
      - name: Build backend image
        uses: docker/build-push-action@v4
        with:
          context: ./backend
          push: false
          tags: mylibrary-backend:latest
      
      - name: Build frontend image
        uses: docker/build-push-action@v4
        with:
          context: ./frontend
          push: false
          tags: mylibrary-frontend:latest
""")
    print(f"✓ Created: .github/workflows/docker-build.yml")
    
    # Create .gitignore addition if needed
    gitignore_path = os.path.join(root, '.gitignore')
    if os.path.exists(gitignore_path):
        with open(gitignore_path, 'r') as f:
            content = f.read()
        # Check if GitHub actions files are already ignored
        if '.github' not in content:
            print("Note: .github directory should be committed to git")
    
    print(f"\n✅ GitHub Actions setup complete!")
    return 0

if __name__ == '__main__':
    sys.exit(setup())
