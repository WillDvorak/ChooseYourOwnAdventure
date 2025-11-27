// InventoryModal.test.jsx - Unit tests for the InventoryModal component
import { render, screen } from '@testing-library/react';
import { describe, test, expect, vi } from 'vitest';
import userEvent from '@testing-library/user-event';
import InventoryModal from '../components/content/InventoryModal';

// Mock react-bootstrap Modal
vi.mock('react-bootstrap', () => ({
  Modal: ({ show, onHide, children, centered }) => {
    if (!show) return null;
    return (
      <div data-testid="modal" data-centered={centered}>
        <button data-testid="close-button" onClick={onHide}>Close</button>
        {children}
      </div>
    );
  },
  Image: ({ src, alt, fluid, style }) => (
    <img src={src} alt={alt} data-fluid={fluid} style={style} data-testid="item-image" />
  ),
}));

describe('InventoryModal Component', () => {
  
  // TEST 1: Basic rendering
  test('renders modal when item is provided', () => {
    const item = { name: 'Torch', description: 'A bright torch' };
    const onClose = vi.fn();
    
    render(<InventoryModal item={item} onClose={onClose} />);
    
    expect(screen.getByTestId('modal')).toBeInTheDocument();
    expect(screen.getByText('Torch')).toBeInTheDocument();
  });

  // TEST 2: Does not render when item is null
  test('does not render when item is null', () => {
    const onClose = vi.fn();
    
    const { container } = render(<InventoryModal item={null} onClose={onClose} />);
    
    expect(container.firstChild).toBeNull();
  });

  // TEST 3: Does not render when item is undefined
  test('does not render when item is undefined', () => {
    const onClose = vi.fn();
    
    const { container } = render(<InventoryModal item={undefined} onClose={onClose} />);
    
    expect(container.firstChild).toBeNull();
  });

  // TEST 4: Displays item name in modal title
  test('displays item name in modal title', () => {
    const item = { name: 'Ancient Key', description: 'A mysterious key' };
    const onClose = vi.fn();
    
    render(<InventoryModal item={item} onClose={onClose} />);
    
    expect(screen.getByText('Ancient Key')).toBeInTheDocument();
  });

  // TEST 5: Displays item description
  test('displays item description', () => {
    const item = { name: 'Potion', description: 'A healing potion' };
    const onClose = vi.fn();
    
    render(<InventoryModal item={item} onClose={onClose} />);
    
    expect(screen.getByText('A healing potion')).toBeInTheDocument();
  });

  // TEST 6: Uses longDescription if available
  test('uses longDescription if available over description', () => {
    const item = { 
      name: 'Sword', 
      description: 'Short desc',
      longDescription: 'A very long and detailed description of the sword'
    };
    const onClose = vi.fn();
    
    render(<InventoryModal item={item} onClose={onClose} />);
    
    expect(screen.getByText('A very long and detailed description of the sword')).toBeInTheDocument();
    expect(screen.queryByText('Short desc')).not.toBeInTheDocument();
  });

  // TEST 7: Displays item image
  test('displays item image when provided', () => {
    const item = { 
      name: 'Amulet', 
      description: 'A magical amulet',
      image: '/path/to/amulet.png'
    };
    const onClose = vi.fn();
    
    render(<InventoryModal item={item} onClose={onClose} />);
    
    const image = screen.getByTestId('item-image');
    expect(image).toHaveAttribute('src', '/path/to/amulet.png');
    expect(image).toHaveAttribute('alt', 'Amulet');
  });

  // TEST 8: Uses largeImage if available over image
  test('uses largeImage if available over image', () => {
    const item = { 
      name: 'Map', 
      description: 'A treasure map',
      image: '/path/to/map-small.png',
      largeImage: '/path/to/map-large.png'
    };
    const onClose = vi.fn();
    
    render(<InventoryModal item={item} onClose={onClose} />);
    
    const image = screen.getByTestId('item-image');
    expect(image).toHaveAttribute('src', '/path/to/map-large.png');
  });

  // TEST 9: Close button calls onClose handler
  test('close button calls onClose handler', async () => {
    const user = userEvent.setup();
    const item = { name: 'Torch', description: 'A bright torch' };
    const onClose = vi.fn();
    
    render(<InventoryModal item={item} onClose={onClose} />);
    
    const closeButton = screen.getByTestId('close-button');
    await user.click(closeButton);
    
    expect(onClose).toHaveBeenCalledTimes(1);
  });

  // TEST 10: Modal is centered
  test('modal is centered', () => {
    const item = { name: 'Torch', description: 'A bright torch' };
    const onClose = vi.fn();
    
    render(<InventoryModal item={item} onClose={onClose} />);
    
    const modal = screen.getByTestId('modal');
    expect(modal).toHaveAttribute('data-centered', 'true');
  });

  // TEST 11: Handles item with minimal properties
  test('handles item with only name', () => {
    const item = { name: 'Mystery Item' };
    const onClose = vi.fn();
    
    render(<InventoryModal item={item} onClose={onClose} />);
    
    expect(screen.getByText('Mystery Item')).toBeInTheDocument();
  });
});

