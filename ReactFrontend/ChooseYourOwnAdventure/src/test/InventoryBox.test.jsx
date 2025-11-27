// InventoryBox.test.jsx
import { render, screen, within } from '@testing-library/react';
import { describe, test, expect } from 'vitest';
import InventoryBox from '../components/content/InventoryBox';

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

beforeEach(() => {
  // Mock the global fetch function
  global.fetch = vi.fn();
});

describe('InventoryBox Component', () => {
  
  // TEST 1: Most basic test - can the component render at all?
  test('renders component without crashing', async () => {

    // Render the component
    render(<InventoryBox theme={theme} inventory={[]}/>)
    
    // If we get here without errors, the test passes!
    expect(true).toBe(true);
  });

  test('shows the "Inventory:" heading', () => {
    render(<InventoryBox theme={theme} inventory={[]}/>);

    expect(screen.getByText(/Inventory:/i)).toBeInTheDocument();
  });

  test('if empty, shows correct message' , () => {
    render(<InventoryBox theme={theme} inventory={[]}/>)

    expect(screen.getByText(/You have nothing in your inventory.../i)).toBeInTheDocument
  })

  test('lists the items it receives via props', () => {
    render(<InventoryBox theme={theme} inventory={['torch', 'rock']} />);
    expect(screen.getByText('torch')).toBeInTheDocument();
    expect(screen.getByText('rock')).toBeInTheDocument();
  });

  test('updates when the inventory prop changes', () => {
    const { rerender } = render(<InventoryBox theme={theme} inventory={['torch']} />);
    expect(screen.getByText('torch')).toBeInTheDocument();

    rerender(<InventoryBox theme={theme} inventory={['torch', 'rock']} />);
    expect(screen.getByText('rock')).toBeInTheDocument();
  });

  test('handles null inventory', () => {
    render(<InventoryBox theme={theme} inventory={null} />);
    expect(screen.getByText(/nothing in your inventory/i)).toBeInTheDocument();
  });

  test('handles undefined inventory', () => {
    render(<InventoryBox theme={theme} />);
    expect(screen.getByText(/nothing in your inventory/i)).toBeInTheDocument();
  });

  test('handles many items', () => {
    const items = ['torch', 'sword', 'potion', 'key', 'map'];
    render(<InventoryBox theme={theme} inventory={items} />);
    items.forEach(item => {
      expect(screen.getByText(item)).toBeInTheDocument();
    });
  });

})