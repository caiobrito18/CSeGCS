# Testing Guide

## Backend Testing

### Run All Tests
```bash
cd backend
mvn test
```

### Run Specific Test
```bash
mvn test -Dtest=YourTestClass
```

### Test Coverage
```bash
mvn jacoco:report
```

Coverage reports will be in `target/site/jacoco/index.html`

### Unit Tests
Test files are located in `backend/src/test/java` following the same structure as source files.

### Integration Tests
Marked with `@SpringBootTest` annotation

## Frontend Testing

### Run All Tests
```bash
cd frontend
npm test
```

### Run Tests Once
```bash
npm run test -- --watch=false
```

### Test Coverage
```bash
npm test -- --code-coverage
```

Coverage reports will be in `coverage/` directory

## Continuous Integration

All tests are automatically run on:
- Every push to `main` or `develop` branches
- Every pull request to `main` or `develop`

Check GitHub Actions for test results: Settings → Actions

## Writing Tests

### Backend (JUnit 5 + Mockito)
```java
@SpringBootTest
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    
    @Test
    void testFindById() {
        // Arrange, Act, Assert
    }
}
```

### Frontend (Jasmine + Karma)
```typescript
describe('UserComponent', () => {
    let component: UserComponent;
    let fixture: ComponentFixture<UserComponent>;
    
    beforeEach(async () => {
        await TestBed.configureTestingModule({
            declarations: [ UserComponent ]
        }).compileComponents();
    });
    
    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
```

## Coverage Requirements

- **Minimum**: 50% code coverage
- **Target**: 70%+ for critical paths
- **Excluded**: Generated code, configuration classes
