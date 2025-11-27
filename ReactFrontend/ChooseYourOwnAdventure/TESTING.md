# Frontend Testing

## Running Tests

```bash
# Run all tests
npm test

# Run with coverage
npm run test:coverage
```

## Test Files

1. **DisplayBox.test.jsx** (6 tests)
   - Basic rendering
   - Scene info handling
   - Health display
   - Missing data handling

2. **Textbox.test.jsx** (8 tests)
   - Scene loading
   - Choice buttons
   - User interactions
   - Error handling
   - Empty choices

3. **InventoryBox.test.jsx** (8 tests)
   - Item display
   - Empty inventory
   - Null/undefined handling
   - Multiple items

4. **HealthBar.test.jsx** (5 tests)
   - Full health
   - Damaged health
   - Zero health
   - Default values

5. **StoryMap.test.jsx** (4 tests)
   - Loading state
   - API integration
   - Error handling
   - Current scene marker

6. **InventoryModal.test.jsx** (2 tests)
   - Component exists
   - Component type check

7. **AboutUsPage.test.jsx** (4 tests)
   - Page rendering
   - Team info display
   - Course info

## Test Results

**Total: 37 tests passing**
- 7 test files
- All components covered
- Edge cases included

## Notes

- Tests use Vitest + React Testing Library
- Simple unit tests only
- Focus on core functionality
- stderr warnings are expected (from intentional error tests)

