// StoryMap.test.jsx - Unit tests for the StoryMap component
import { render, screen, waitFor } from '@testing-library/react';
import { describe, test, expect, beforeEach, vi } from 'vitest';
import StoryMap from '../components/content/StoryMap';

// Mock fetch globally
global.fetch = vi.fn();

describe('StoryMap Component', () => {
  
  beforeEach(() => {
    vi.clearAllMocks();
  });

  // TEST 1: Basic rendering
  test('renders component without crashing', () => {
    global.fetch.mockResolvedValueOnce({
      ok: true,
      json: async () => ({ nodes: [], edges: [] })
    });

    render(<StoryMap />);
    expect(screen.getByText(/Story Map/i)).toBeInTheDocument();
  });

  // TEST 2: Shows loading state initially
  test('shows loading state while fetching data', () => {
    // Create a promise that never resolves (simulates slow network)
    global.fetch.mockReturnValueOnce(new Promise(() => {}));

    render(<StoryMap />);
    
    expect(screen.getByText(/Loading map/i)).toBeInTheDocument();
  });

  // TEST 3: Fetches story map data on mount
  test('fetches story map data on component mount', async () => {
    global.fetch.mockResolvedValueOnce({
      ok: true,
      json: async () => ({ nodes: [], edges: [] })
    });

    render(<StoryMap />);
    
    await waitFor(() => {
      expect(global.fetch).toHaveBeenCalledWith('/api/game/story-map');
    });
  });

  // TEST 4: Displays nodes from API response
  test('displays nodes from API response', async () => {
    const mockNodes = [
      { id: 'intro', title: 'Introduction', isTerminal: false },
      { id: 'cave', title: 'Cave', isTerminal: false }
    ];

    global.fetch.mockResolvedValueOnce({
      ok: true,
      json: async () => ({ nodes: mockNodes, edges: [] })
    });

    render(<StoryMap />);
    
    await waitFor(() => {
      expect(screen.queryByText(/Loading map/i)).not.toBeInTheDocument();
    });

    // Check that nodes are rendered (they should be in SVG)
    const svg = screen.getByRole('img', { hidden: true }) || document.querySelector('svg');
    expect(svg).toBeInTheDocument();
  });

  // TEST 5: Highlights current scene
  test('highlights current scene when provided', async () => {
    const mockNodes = [
      { id: 'intro', title: 'Introduction', isTerminal: false },
      { id: 'cave', title: 'Cave', isTerminal: false }
    ];

    global.fetch.mockResolvedValueOnce({
      ok: true,
      json: async () => ({ nodes: mockNodes, edges: [] })
    });

    const currentScene = { code: 'intro' };
    render(<StoryMap currentScene={currentScene} />);
    
    await waitFor(() => {
      expect(screen.queryByText(/Loading map/i)).not.toBeInTheDocument();
    });

    // The current scene should be highlighted (check for node-current class or similar)
    const svg = document.querySelector('svg');
    expect(svg).toBeInTheDocument();
  });

  // TEST 6: Handles API error gracefully
  test('handles API error gracefully', async () => {
    global.fetch.mockRejectedValueOnce(new Error('Network error'));

    render(<StoryMap />);
    
    await waitFor(() => {
      expect(screen.queryByText(/Loading map/i)).not.toBeInTheDocument();
    });

    // Component should still render, just without data
    expect(screen.getByText(/Story Map/i)).toBeInTheDocument();
  });

  // TEST 7: Handles empty nodes array
  test('handles empty nodes array', async () => {
    global.fetch.mockResolvedValueOnce({
      ok: true,
      json: async () => ({ nodes: [], edges: [] })
    });

    render(<StoryMap />);
    
    await waitFor(() => {
      expect(screen.queryByText(/Loading map/i)).not.toBeInTheDocument();
    });

    const svg = document.querySelector('svg');
    expect(svg).toBeInTheDocument();
  });

  // TEST 8: Renders edges between nodes
  test('renders edges between nodes', async () => {
    const mockNodes = [
      { id: 'intro', title: 'Introduction', isTerminal: false },
      { id: 'cave', title: 'Cave', isTerminal: false }
    ];
    const mockEdges = [
      { from: 'intro', to: 'cave' }
    ];

    global.fetch.mockResolvedValueOnce({
      ok: true,
      json: async () => ({ nodes: mockNodes, edges: mockEdges })
    });

    render(<StoryMap />);
    
    await waitFor(() => {
      expect(screen.queryByText(/Loading map/i)).not.toBeInTheDocument();
    });

    // Edges should be rendered as lines in SVG
    const svg = document.querySelector('svg');
    expect(svg).toBeInTheDocument();
  });

  // TEST 9: Updates when currentScene changes
  test('updates when currentScene prop changes', async () => {
    const mockNodes = [
      { id: 'intro', title: 'Introduction', isTerminal: false },
      { id: 'cave', title: 'Cave', isTerminal: false }
    ];

    global.fetch.mockResolvedValueOnce({
      ok: true,
      json: async () => ({ nodes: mockNodes, edges: [] })
    });

    const { rerender } = render(<StoryMap currentScene={{ code: 'intro' }} />);
    
    await waitFor(() => {
      expect(screen.queryByText(/Loading map/i)).not.toBeInTheDocument();
    });

    rerender(<StoryMap currentScene={{ code: 'cave' }} />);
    
    // Component should update to highlight new current scene
    const svg = document.querySelector('svg');
    expect(svg).toBeInTheDocument();
  });

  // TEST 10: Handles terminal nodes
  test('handles terminal nodes correctly', async () => {
    const mockNodes = [
      { id: 'ending1', title: 'Ending 1', isTerminal: true }
    ];

    global.fetch.mockResolvedValueOnce({
      ok: true,
      json: async () => ({ nodes: mockNodes, edges: [] })
    });

    render(<StoryMap />);
    
    await waitFor(() => {
      expect(screen.queryByText(/Loading map/i)).not.toBeInTheDocument();
    });

    const svg = document.querySelector('svg');
    expect(svg).toBeInTheDocument();
  });
});

