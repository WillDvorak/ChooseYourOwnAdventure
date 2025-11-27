// HealthBar.test.jsx - Unit tests for the HealthBar component
import { render, screen } from '@testing-library/react';
import { describe, test, expect } from 'vitest';
import HealthBar from '../components/content/HealthBar';

describe('HealthBar Component', () => {
  
  // TEST 1: Basic rendering
  test('renders component without crashing', () => {
    render(<HealthBar health={100} maxHealth={100} />);
    expect(screen.getByText('HP')).toBeInTheDocument();
  });

  // TEST 2: Displays health values correctly
  test('displays health and max health values', () => {
    render(<HealthBar health={75} maxHealth={100} />);
    expect(screen.getByText('75/100')).toBeInTheDocument();
  });

  // TEST 3: Uses default values when props are not provided
  test('uses default values when props are missing', () => {
    render(<HealthBar />);
    expect(screen.getByText('100/100')).toBeInTheDocument();
  });

  // TEST 4: Calculates percentage correctly
  test('calculates health percentage correctly', () => {
    const { container } = render(<HealthBar health={50} maxHealth={100} />);
    const fillBar = container.querySelector('.health-bar-fill');
    expect(fillBar).toHaveStyle({ width: '50%' });
  });

  // TEST 5: Shows green color when health is above 60%
  test('shows green color when health is above 60%', () => {
    const { container } = render(<HealthBar health={80} maxHealth={100} />);
    const fillBar = container.querySelector('.health-bar-fill');
    expect(fillBar).toHaveStyle({ backgroundColor: '#4caf50' });
  });

  // TEST 6: Shows orange color when health is between 30% and 60%
  test('shows orange color when health is between 30% and 60%', () => {
    const { container } = render(<HealthBar health={50} maxHealth={100} />);
    const fillBar = container.querySelector('.health-bar-fill');
    expect(fillBar).toHaveStyle({ backgroundColor: '#ff9800' });
  });

  // TEST 7: Shows red color when health is below 30%
  test('shows red color when health is below 30%', () => {
    const { container } = render(<HealthBar health={20} maxHealth={100} />);
    const fillBar = container.querySelector('.health-bar-fill');
    expect(fillBar).toHaveStyle({ backgroundColor: '#f44336' });
  });

  // TEST 8: Handles edge case - health at exactly 30%
  test('handles health at exactly 30% threshold', () => {
    const { container } = render(<HealthBar health={30} maxHealth={100} />);
    const fillBar = container.querySelector('.health-bar-fill');
    expect(fillBar).toHaveStyle({ backgroundColor: '#ff9800' });
  });

  // TEST 9: Handles edge case - health at exactly 60%
  test('handles health at exactly 60% threshold', () => {
    const { container } = render(<HealthBar health={60} maxHealth={100} />);
    const fillBar = container.querySelector('.health-bar-fill');
    expect(fillBar).toHaveStyle({ backgroundColor: '#4caf50' });
  });

  // TEST 10: Clamps health percentage to 0-100%
  test('clamps health percentage to 0% minimum', () => {
    const { container } = render(<HealthBar health={-10} maxHealth={100} />);
    const fillBar = container.querySelector('.health-bar-fill');
    expect(fillBar).toHaveStyle({ width: '0%' });
  });

  // TEST 11: Clamps health percentage to 100% maximum
  test('clamps health percentage to 100% maximum', () => {
    const { container } = render(<HealthBar health={150} maxHealth={100} />);
    const fillBar = container.querySelector('.health-bar-fill');
    expect(fillBar).toHaveStyle({ width: '100%' });
  });

  // TEST 12: Handles zero health
  test('handles zero health correctly', () => {
    render(<HealthBar health={0} maxHealth={100} />);
    expect(screen.getByText('0/100')).toBeInTheDocument();
    const { container } = render(<HealthBar health={0} maxHealth={100} />);
    const fillBar = container.querySelector('.health-bar-fill');
    expect(fillBar).toHaveStyle({ width: '0%' });
    expect(fillBar).toHaveStyle({ backgroundColor: '#f44336' });
  });

  // TEST 13: Handles different max health values
  test('handles different max health values', () => {
    render(<HealthBar health={50} maxHealth={200} />);
    expect(screen.getByText('50/200')).toBeInTheDocument();
    const { container } = render(<HealthBar health={50} maxHealth={200} />);
    const fillBar = container.querySelector('.health-bar-fill');
    expect(fillBar).toHaveStyle({ width: '25%' });
  });

  // TEST 14: Updates when props change
  test('updates when health prop changes', () => {
    const { rerender } = render(<HealthBar health={100} maxHealth={100} />);
    expect(screen.getByText('100/100')).toBeInTheDocument();

    rerender(<HealthBar health={50} maxHealth={100} />);
    expect(screen.getByText('50/100')).toBeInTheDocument();
  });
});

