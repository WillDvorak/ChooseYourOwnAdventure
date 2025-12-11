# Testing Documentation

Testing strategies and how to run tests for both frontend and backend.

---

## Frontend Testing (Vitest + React Testing Library)

### Running Tests

```bash
# Navigate to frontend directory
cd ReactFrontend/ChooseYourOwnAdventure

# Run all tests
npm test

# Run tests with UI
npm run test:ui

# Generate coverage report
npm run test:coverage

# Watch mode (auto-rerun on changes)
npm test -- --watch
```

### Test Files

Located in `src/test/`:

| Test File | Component | Coverage |
|-----------|-----------|----------|
| `Textbox.test.jsx` | Textbox | Core gameplay, API calls, choice handling |
| `InventoryBox.test.jsx` | InventoryBox | Inventory display, modal trigger |
| `InventoryModal.test.jsx` | InventoryModal | Modal open/close, item display |
| `DisplayBox.test.jsx` | DisplayBox | Scene title rendering |
| `AppLayout.test.jsx` | AppLayout | Layout structure, state management |

### Test Setup

**File**: `src/test/setup.js`

Configures:
- jsdom environment (browser simulation)
- @testing-library/jest-dom matchers
- Global test utilities

### Example Test Structure

```javascript
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import Textbox from '../components/content/Textbox';

describe('Textbox Component', () => {
  it('displays scene text', async () => {
    render(<Textbox theme={mockTheme} />);
    
    await waitFor(() => {
      expect(screen.getByText(/You wake at a campfire/)).toBeInTheDocument();
    });
  });

  it('handles choice clicks', async () => {
    const user = userEvent.setup();
    render(<Textbox theme={mockTheme} />);
    
    const choice = screen.getByText('Head into the forest');
    await user.click(choice);
    
    // Assert new scene loaded
  });
});
```

### Testing Strategy

1. **Unit Tests** - Individual components in isolation
2. **Integration Tests** - Component interactions (e.g., AppLayout with children)
3. **Mock API** - Use `vi.mock()` to stub fetch calls
4. **User Events** - Simulate clicks, typing with `@testing-library/user-event`

---

## Backend Testing (JUnit + Spring Boot Test)

### Running Tests

```bash
# Navigate to API directory
cd api

# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests GameServiceTest

# Run tests with verbose output
./gradlew test --info

# Generate test report (see build/reports/tests/test/index.html)
./gradlew test
```

### Test Files

Located in `src/test/java/com/textquest/api/`:

| Test File | Component | Coverage |
|-----------|-----------|----------|
| `GameServiceTest` | GameService | Session creation, choice processing, health logic |
| `SceneServiceTest` | SceneService | Scene retrieval, validation |
| `ChoiceServiceTest` | ChoiceService | Choice validation, flag checking |
| `GameControllerTest` | GameController | API endpoints, request/response |
| `SceneRepositoryTest` | SceneRepository | Database queries |
| `ChoiceRepositoryTest` | ChoiceRepository | Choice queries |
| `GameSessionRepositoryTest` | GameSessionRepository | Session persistence |
| `DatabaseConnectionTest` | Database | Connection verification |
| `EntityTest` | Entities | JPA mapping validation |

### Test Configuration

**Annotations**:
- `@SpringBootTest` - Full application context
- `@DataJpaTest` - JPA components only (faster)
- `@WebMvcTest` - Controller layer only
- `@MockBean` - Mock dependencies

### Example Test Structure

```java
@SpringBootTest
class GameServiceTest {
    
    @Autowired
    private GameService gameService;
    
    @MockBean
    private SceneRepository sceneRepository;
    
    @Test
    void testCreateGameSession() {
        GameSession session = gameService.createGameSession("Player", "intro");
        
        assertNotNull(session);
        assertEquals("intro", session.getCurrentSceneCode());
        assertEquals(100, session.getHealth());
    }
    
    @Test
    void testHealthDecrease() {
        GameSession session = createTestSession();
        Choice damageChoice = createChoiceWithFlag("health:-30");
        
        gameService.makeChoice(session.getId(), damageChoice.getId());
        
        assertEquals(70, session.getHealth());
    }
}
```

### Testing Strategy

1. **Unit Tests** - Services and repositories in isolation
2. **Integration Tests** - Full flow from controller to database
3. **Repository Tests** - Custom queries and JPA mappings
4. **Controller Tests** - API endpoints with MockMvc

---

## Test Coverage Goals

### Frontend Coverage
- **Target**: 70%+ coverage
- **Priority**: 
  - Textbox (highest priority - core gameplay)
  - HealthBar (critical for game mechanics)
  - Inventory components

### Backend Coverage
- **Target**: 80%+ coverage
- **Priority**:
  - GameService (game logic)
  - GameController (API endpoints)
  - Health calculation logic
  - Flag/item management

---

## Manual Testing Checklist

### Critical User Flows

- [ ] **Start Game**
  1. Open app → sees intro scene
  2. Health bar shows 100/100
  3. 4 choices displayed

- [ ] **Make Choices**
  1. Click choice → new scene loads
  2. Story history updates
  3. Choices update

- [ ] **Health System**
  1. Take damage → health bar decreases
  2. Color changes (green → orange → red)
  3. Reach 0 HP → death scene

- [ ] **Inventory**
  1. Get torch → appears in inventory box
  2. Click inventory → modal opens
  3. See all collected items

- [ ] **Story Map**
  1. Map loads on start
  2. Current location highlighted
  3. Updates on scene change

- [ ] **Terminal Scenes**
  1. Reach ending → no more choices
  2. Terminal flag displayed

- [ ] **Flag Requirements**
  1. Choice requires torch → only shows if player has torch
  2. Choices without requirements always visible

---

## Testing Best Practices

### Frontend
1. ✅ Test user interactions, not implementation
2. ✅ Use semantic queries (`getByRole`, `getByText`)
3. ✅ Mock API calls to avoid flaky tests
4. ✅ Test error states (API failures)
5. ❌ Don't test CSS/styling
6. ❌ Don't test third-party libraries

### Backend
1. ✅ Use test database (H2 or separate MySQL)
2. ✅ Clean database between tests
3. ✅ Test edge cases (null values, invalid IDs)
4. ✅ Test exception handling
5. ❌ Don't test Spring framework itself
6. ❌ Don't test auto-generated code (Lombok getters/setters)

---

## Continuous Integration

### Pre-commit Checks
```bash
# Run before committing
npm test              # Frontend tests
./gradlew test        # Backend tests
npm run lint          # ESLint
./gradlew build       # Build verification
```

### CI Pipeline (Future)
```yaml
# Example GitHub Actions workflow
- Checkout code
- Setup Node.js 22
- Setup Java 17
- Run frontend tests
- Run backend tests
- Generate coverage reports
- Build Docker images
```

---

## Debugging Failed Tests

### Frontend
```bash
# Run single test file
npm test -- Textbox.test.jsx

# Debug mode (inspect in Chrome DevTools)
npm test -- --inspect

# See console logs
npm test -- --reporter=verbose
```

### Backend
```bash
# Run single test
./gradlew test --tests GameServiceTest.testHealthDecrease

# Show stack traces
./gradlew test --stacktrace

# Debug with IDE breakpoints
# In IntelliJ: Right-click test → Debug
```

---

## Known Testing Limitations

1. **No E2E Tests** - No Selenium/Cypress yet
2. **Limited API Mocking** - Some frontend tests hit real endpoints
3. **No Load Testing** - Performance under stress not tested
4. **Manual Docker Testing** - Docker setup not automated in tests

---

## Future Testing Improvements

- [ ] Add Cypress for E2E testing
- [ ] Increase coverage to 90%+
- [ ] Add performance benchmarks
- [ ] Automate Docker testing
- [ ] Add accessibility testing (axe-core)
- [ ] Implement visual regression testing

