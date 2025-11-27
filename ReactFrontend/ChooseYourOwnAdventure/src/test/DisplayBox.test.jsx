// DisplayBox.test.jsx
import { render, screen } from '@testing-library/react';
import { describe, it, expect } from 'vitest';
import DisplayBox from '../components/content/DisplayBox';

const theme = {
        background: 'linear-gradient(135deg, #1a0933 0%, #2d1b4e 100%)',
        containerBorder: '3px solid #d4af37',
        cardBg: 'rgba(13, 5, 26, 0.9)',
        messageText: '#f0e6d2',
        messageBorder: '1px solid #4a3775',
        messageBg: 'rgba(74, 55, 117, 0.3)',
        buttonBg: '#d4af37',
        buttonText: '#1a0933',
        buttonHover: '#ffd700',
        inputBg: 'rgba(74, 55, 117, 0.5)',
        inputText: '#f0e6d2',
        inputBorder: '#d4af37',
        fontFamily: 'Georgia, serif',
        fontSize: '1.1rem',
        borderRadius: '12px',
    };

describe('DisplayBox', () => {

  test('renders without crashing', () => {
    render(<DisplayBox theme={theme} />);
    expect(true).toBe(true);
  });

  test('shows fallback text when no sceneInfo is provided', () => {
    render(<DisplayBox theme={theme} />);
    // Just check one of the HARDCODE elements is present
    expect(screen.getAllByText('HARDCODE')[0]).toBeInTheDocument();
  });

  it('shows scene title and code when sceneInfo is provided', () => {
    const sceneInfo = { title: 'Forest Entrance', code: 'forest_1' };
    render(<DisplayBox theme={theme} sceneInfo={sceneInfo} />);

    expect(screen.getByRole('heading', { level: 2, name: 'Forest Entrance' }))
      .toBeInTheDocument();
    expect(screen.getByRole('heading', { level: 3, name: 'forest_1' }))
      .toBeInTheDocument();
  });

  it('updates when sceneInfo prop changes', () => {
    const { rerender } = render(
      <DisplayBox theme={theme} sceneInfo={{ title: 'Intro', code: 'intro' }} />
    );
    expect(screen.getByText('Intro')).toBeInTheDocument();

    rerender(<DisplayBox theme={theme} sceneInfo={{ title: 'Cave', code: 'cave_2' }} />);
    expect(screen.getByText('Cave')).toBeInTheDocument();
    expect(screen.getByText('cave_2')).toBeInTheDocument();
  });

  test('handles missing sceneInfo', () => {
    render(<DisplayBox theme={theme} sceneInfo={null} />);
    expect(screen.getAllByText('HARDCODE')[0]).toBeInTheDocument();
  });

  test('handles health values', () => {
    render(<DisplayBox theme={theme} sceneInfo={{ title: 'Test', code: 'test', health: 50, maxHealth: 100 }} />);
    expect(screen.getByText('50/100')).toBeInTheDocument();
  });
});
