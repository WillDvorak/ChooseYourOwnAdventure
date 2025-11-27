import { test, expect } from 'vitest';
import InventoryModal from '../components/content/InventoryModal';

test('component exists', () => {
  // Just verify the component can be imported
  expect(InventoryModal).toBeDefined();
});

test('component is a function', () => {
  expect(typeof InventoryModal).toBe('function');
});

