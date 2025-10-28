// Textbox.test.jsx - Unit tests for the Textbox component
import { render, screen, waitFor } from '@testing-library/react';
import { describe, test, expect, beforeEach, vi } from 'vitest';
import userEvent from '@testing-library/user-event';
import Textbox from './Textbox';

// This runs before each test
beforeEach(() => {
  // Mock the global fetch function
  global.fetch = vi.fn();
});

// Group related tests together
describe('Textbox Component', () => {
  
  // TEST 1: Most basic test - can the component render at all?
  test('renders component without crashing', async () => {
    // Create a simple mock that returns a resolved promise
    global.fetch.mockResolvedValueOnce({
      ok: true,
      json: async () => ({
        code: 'intro',
        body: 'Test scene',
        choices: []
      })
    });

    // Render the component
    render(<Textbox />);
    
    // Wait for the loading to complete (this handles the async state updates)
    await waitFor(() => {
      expect(screen.queryByText(/Consulting the ancient scrolls/i)).not.toBeInTheDocument();
    });
    
    // If we get here without errors, the test passes!
    expect(true).toBe(true);
  });

  // TEST 2: Does the component display the scene text from the API?
  test('displays scene body text after loading', async () => {
    // Mock the API response with specific text
    const mockSceneBody = 'You wake at a campfire. A narrow path leads into a pine forest.';
    
    global.fetch.mockResolvedValueOnce({
      ok: true,
      json: async () => ({
        code: 'intro',
        body: mockSceneBody,
        choices: []
      })
    });

    // Render the component
    render(<Textbox />);
    
    // Wait for the API call to complete and text to appear
    await waitFor(() => {
      expect(screen.getByText(mockSceneBody)).toBeInTheDocument();
    });
    
    // Verify the text is actually on screen
    const sceneText = screen.getByText(mockSceneBody);
    expect(sceneText).toBeInTheDocument();
  });

  // TEST 3: Does the component display choice buttons?
  test('displays choice buttons from API response', async () => {
    // Mock API with choices - use 'label' and 'targetSceneCode' like the real API
    global.fetch.mockResolvedValueOnce({
      ok: true,
      json: async () => ({
        code: 'intro',
        body: 'You are at a crossroads.',
        choices: [
          { id: 1, label: 'Go left', targetSceneCode: 'left' },
          { id: 2, label: 'Go right', targetSceneCode: 'right' }
        ]
      })
    });

    render(<Textbox />);
    
    // Wait for choices to appear - use regex for flexible matching
    // The /i flag makes it case-insensitive
    await waitFor(() => {
      expect(screen.getByText(/Go left/i)).toBeInTheDocument();
    });
    
    // Check both buttons exist using getByRole (more reliable)
    // getByRole finds by accessibility role and can search within the text
    const leftButton = screen.getByRole('button', { name: /Go left/i });
    const rightButton = screen.getByRole('button', { name: /Go right/i });
    
    expect(leftButton).toBeInTheDocument();
    expect(rightButton).toBeInTheDocument();
    
    // Extra check: make sure there are exactly 3 buttons (2 choices + 1 Send button)
    const allButtons = screen.getAllByRole('button');
    expect(allButtons).toHaveLength(3);
  });

  // TEST 4: Does loading state appear initially?
  test('shows loading state while fetching data', () => {
    // Create a promise that never resolves (simulates slow network)
    global.fetch.mockReturnValueOnce(new Promise(() => {}));

    render(<Textbox />);
    
    // The loading message should be visible
    expect(screen.getByText(/Consulting the ancient scrolls/i)).toBeInTheDocument();
    
    // The spinner should exist
    const spinner = screen.getByRole('status');
    expect(spinner).toBeInTheDocument();
  });

  // TEST 5: EDGE CASE - What happens when the API fails?
  test('displays error message when API call fails', async () => {
    // Mock a failed API response
    global.fetch.mockResolvedValueOnce({
      ok: false,
      status: 500,
      json: async () => ({ error: 'Server error' })
    });

    render(<Textbox />);
    
    // Wait for error message to appear
    await waitFor(() => {
      expect(screen.getByText(/Error: Could not connect to the realm/i)).toBeInTheDocument();
    });
    
    // Verify error message is displayed
    const errorMessage = screen.getByText(/Error: Could not connect to the realm/i);
    expect(errorMessage).toBeInTheDocument();
  });

  // TEST 6: ADVANCED - Can users click choices?
  test('handles choice button clicks', async () => {
    // Setup user event (simulates real user interactions)
    const user = userEvent.setup();
    
    // Mock #1: Initial scene load - use real API format
    global.fetch.mockResolvedValueOnce({
      ok: true,
      json: async () => ({
        code: 'intro',
        body: 'You are at a crossroads.',
        choices: [
          { id: 1, label: 'Go left', targetSceneCode: 'left' },
          { id: 2, label: 'Go right', targetSceneCode: 'right' }
        ]
      })
    });

    // Mock #2: The scene loaded AFTER clicking "Go left"
    global.fetch.mockResolvedValueOnce({
      ok: true,
      json: async () => ({
        code: 'left',
        body: 'You went left and found a treasure!',
        choices: []
      })
    });

    render(<Textbox />);
    
    // Wait for buttons to appear
    await waitFor(() => {
      expect(screen.getByRole('button', { name: /Go left/i })).toBeInTheDocument();
    });
    
    // Find and click the "Go left" button
    const leftButton = screen.getByRole('button', { name: /Go left/i });
    await user.click(leftButton);
    
    // After clicking, verify the new scene loaded
    // (Note: the component replaces messages when loading a new scene)
    await waitFor(() => {
      expect(screen.getByText(/You went left and found a treasure!/i)).toBeInTheDocument();
    });
    
    // Verify the scene text is displayed
    expect(screen.getByText(/You went left and found a treasure!/i)).toBeInTheDocument();
    
    // Verify the old scene is gone (replaced by new one)
    expect(screen.queryByText(/You are at a crossroads/i)).not.toBeInTheDocument();
  });
  
});

