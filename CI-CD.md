# CI/CD Pipeline Documentation

## Overview

The MyLibrary project uses GitHub Actions for continuous integration and deployment.

## Workflows

### 1. CI Pipeline (ci.yml)
Triggered on every push and pull request to `main` or `develop` branches.

**Jobs:**
- **backend-build**: Maven build and test
  - Java 17 setup
  - Maven dependency caching
  - Build with `mvn clean package`
  - Run tests with `mvn test`
  - Verify with `mvn verify`
  - Upload test results and JAR artifacts

- **frontend-build**: Node build and test
  - Node.js 20 setup
  - npm dependency caching
  - Install with `npm ci`
  - Build with `npm run build`
  - Run tests with Angular testing framework
  - Upload build artifacts

- **code-quality**: Code quality checks
  - Frontend linting (if configured)
  - Backend PMD analysis (if configured)
  - Continues on error to not block builds

- **build-status**: Final status check
  - Ensures critical jobs passed
  - Provides overall build status

### 2. Docker Build (docker-build.yml)
Triggered on version tags (v*) for production releases.

**Features:**
- Builds backend and frontend Docker images
- Can be extended to push to registry

## Build Matrix

| Component | Runtime | Version | Cache |
|-----------|---------|---------|-------|
| Backend   | Java    | 17      | Maven |
| Frontend  | Node.js | 20      | npm   |

## Artifact Management

**Backend Artifacts:**
- Location: `backend/target/surefire-reports/`
- Retention: 30 days
- Type: JUnit XML test results

**Frontend Artifacts:**
- Location: `frontend/dist/`
- Retention: 30 days
- Type: Built application

## Branch Protection Rules

Recommended branch protection for `main` and `develop`:

1. ✓ Require status checks to pass (CI pipeline)
2. ✓ Require code reviews (1-2 reviewers)
3. ✓ Dismiss stale reviews on push
4. ✓ Require administrator approval for exceptions

## Monitoring

### GitHub Actions Dashboard
- Navigate to: Repository → Actions
- View workflow runs, logs, and artifacts
- Monitor build times and trends

### Notifications
- Email notifications on workflow failures
- Watch GitHub for updates

## Performance

**Expected Build Times:**
- Backend: 3-5 minutes (with caching)
- Frontend: 2-3 minutes (with caching)
- Code Quality: 1-2 minutes
- **Total**: ~5-7 minutes

**Optimization Tips:**
- Dependencies are cached between runs
- Use `skip-tests` flag during development PRs if needed
- Parallel job execution reduces overall time

## Troubleshooting

### Build Fails on Java Version
Ensure `pom.xml` specifies Java 17+ as target

### npm ci Fails
- Check `package-lock.json` is committed
- Run `npm ci` locally to verify

### Test Failures
- Check logs in GitHub Actions
- Run tests locally: `mvn test` or `npm test`
- Review recent commits for breaking changes

## Security

**Secrets Management:**
- No hardcoded credentials in workflows
- Use GitHub Secrets for sensitive data
- Environment-specific configurations in `.env` files (not committed)

**Best Practices:**
- Scan dependencies with Dependabot
- Keep actions updated to latest versions
- Minimize permissions for tokens/keys

## Future Enhancements

- [ ] SonarQube integration for code analysis
- [ ] Automated dependency updates
- [ ] Deploy to staging on develop merges
- [ ] Deploy to production on main merges
- [ ] Slack notifications
- [ ] Test coverage reporting
