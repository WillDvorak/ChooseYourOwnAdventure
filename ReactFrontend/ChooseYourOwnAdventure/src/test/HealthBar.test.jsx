import { render, screen } from '@testing-library/react';
import { test, expect } from 'vitest';
import HealthBar from '../components/content/HealthBar';

test('shows full health', () => {
  render(<HealthBar health={100} maxHealth={100} />);
  expect(screen.getByText('100/100')).toBeInTheDocument();
});

test('shows damaged health', () => {
  render(<HealthBar health={50} maxHealth={100} />);
  expect(screen.getByText('50/100')).toBeInTheDocument();
});

test('shows low health', () => {
  render(<HealthBar health={10} maxHealth={100} />);
  expect(screen.getByText('10/100')).toBeInTheDocument();
});

test('shows zero health', () => {
  render(<HealthBar health={0} maxHealth={100} />);
  expect(screen.getByText('0/100')).toBeInTheDocument();
});

test('handles default values', () => {
  render(<HealthBar />);
  expect(screen.getByText('100/100')).toBeInTheDocument();
});

