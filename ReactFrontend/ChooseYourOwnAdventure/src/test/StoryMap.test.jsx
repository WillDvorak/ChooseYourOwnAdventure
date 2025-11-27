import { render, screen, waitFor } from '@testing-library/react';
import { test, expect, beforeEach, vi } from 'vitest';
import StoryMap from '../components/content/StoryMap';

beforeEach(() => {
  global.fetch = vi.fn();
});

test('shows loading initially', () => {
  global.fetch.mockReturnValue(new Promise(() => {}));
  render(<StoryMap currentScene={{ code: 'intro' }} />);
  expect(screen.getByText(/Loading map/i)).toBeInTheDocument();
});

test('loads map from API', async () => {
  global.fetch.mockResolvedValue({
    ok: true,
    json: async () => ({ nodes: [], edges: [] })
  });
  
  render(<StoryMap currentScene={{ code: 'intro' }} />);
  await waitFor(() => {
    expect(screen.getByText(/Story Map/i)).toBeInTheDocument();
  });
});

test('handles API error', async () => {
  global.fetch.mockRejectedValue(new Error('API error'));
  
  render(<StoryMap currentScene={{ code: 'intro' }} />);
  await waitFor(() => {
    expect(screen.queryByText(/Loading map/i)).not.toBeInTheDocument();
  });
});

test('shows current scene marker', async () => {
  global.fetch.mockResolvedValue({
    ok: true,
    json: async () => ({ 
      nodes: [{ id: 'intro', isTerminal: false }], 
      edges: [] 
    })
  });
  
  render(<StoryMap currentScene={{ code: 'intro' }} />);
  await waitFor(() => {
    expect(screen.getByText(/intro 📍/i)).toBeInTheDocument();
  });
});

