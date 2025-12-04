// AboutUsPage.test.jsx - Unit tests for the AboutUsPage component
import { render, screen } from '@testing-library/react';
import { describe, test, expect } from 'vitest';
import AboutUsPage from '../components/structural/AboutUsPage';

describe('AboutUsPage Component', () => {
  
  // TEST 1: Basic rendering
  test('renders component without crashing', () => {
    render(<AboutUsPage />);
    expect(screen.getByText(/About Us/i)).toBeInTheDocument();
  });

  // TEST 2: Displays team name
  test('displays team name', () => {
    render(<AboutUsPage />);
    expect(screen.getByText('Controlled Chaos')).toBeInTheDocument();
  });

  // TEST 3: Displays project description
  test('displays project description', () => {
    render(<AboutUsPage />);
    expect(screen.getByText(/CS506: Software Engineering/i)).toBeInTheDocument();
  });

  // TEST 4: Displays university information
  test('displays university information', () => {
    render(<AboutUsPage />);
    expect(screen.getByText(/University of Wisconsin - Madison/i)).toBeInTheDocument();
  });

  // TEST 5: Displays semester information
  test('displays semester information', () => {
    render(<AboutUsPage />);
    expect(screen.getByText(/fall 2025 semester/i)).toBeInTheDocument();
  });

  // TEST 6: Displays technologies/practices mentioned
  test('displays technologies and practices mentioned', () => {
    render(<AboutUsPage />);
    expect(screen.getByText(/Scrum and Agile principles/i)).toBeInTheDocument();
    expect(screen.getByText(/GitLab/i)).toBeInTheDocument();
    expect(screen.getByText(/containerization/i)).toBeInTheDocument();
    expect(screen.getByText(/pipelines/i)).toBeInTheDocument();
  });

  // TEST 7: Renders in a Container
  test('renders in a Container component', () => {
    const { container } = render(<AboutUsPage />);
    // Bootstrap Container should be present
    expect(container.querySelector('.container')).toBeInTheDocument();
  });

  // TEST 8: Renders in a Card
  test('renders in a Card component', () => {
    const { container } = render(<AboutUsPage />);
    // Bootstrap Card should be present
    expect(container.querySelector('.card')).toBeInTheDocument();
  });

  // TEST 9: Has proper heading structure
  test('has proper heading structure', () => {
    render(<AboutUsPage />);
    expect(screen.getByRole('heading', { level: 1, name: /About Us/i })).toBeInTheDocument();
    expect(screen.getByRole('heading', { level: 2, name: 'Controlled Chaos' })).toBeInTheDocument();
  });

  // TEST 10: Contains multiple paragraphs
  test('contains multiple paragraphs', () => {
    render(<AboutUsPage />);
    const paragraphs = screen.getAllByText(/This/i);
    expect(paragraphs.length).toBeGreaterThan(0);
  });
});

