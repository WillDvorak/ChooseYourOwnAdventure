import { render, screen } from '@testing-library/react';
import { test, expect } from 'vitest';
import AboutUsPage from '../components/structural/AboutUsPage';

test('renders page', () => {
  render(<AboutUsPage />);
  expect(screen.getByText(/About Us/i)).toBeInTheDocument();
});

test('shows team name', () => {
  render(<AboutUsPage />);
  expect(screen.getByText(/Controlled Chaos/i)).toBeInTheDocument();
});

test('mentions CS506', () => {
  render(<AboutUsPage />);
  expect(screen.getByText(/CS506/i)).toBeInTheDocument();
});

test('mentions UW Madison', () => {
  render(<AboutUsPage />);
  expect(screen.getByText(/University of Wisconsin - Madison/i)).toBeInTheDocument();
});

