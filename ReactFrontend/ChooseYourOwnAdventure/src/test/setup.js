// Setup file that runs before all tests
import { expect, afterEach } from 'vitest';
import { cleanup } from '@testing-library/react';
import '@testing-library/jest-dom/vitest';

// Cleanup after each test (unmount React components)
afterEach(() => {
  cleanup();
});

