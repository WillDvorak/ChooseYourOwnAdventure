// InventoryModal.test.jsx
import { render, screen, fireEvent } from '@testing-library/react';
import { describe, test, expect, vi } from 'vitest';
import InventoryModal from '../components/content/InventoryModal';


describe('InventoryModal Component', () => {
  // TEST 1: Can the component render at all with a valid item?
  test('renders component without crashing when item is provided', () => {
    const item = {
      label: 'torch',
      title: 'Magic Torch',
      description: 'A very bright torch.',
    };

    const itemImages = {
      torch: '/figures/AI_Torch.png',
    };

    render(
      <InventoryModal
        item={item}
        onClose={() => {}}
        itemImages={itemImages}
      />
    );

    // If we got here without errors, and the title is present, test passes
    expect(screen.getByText('Magic Torch')).toBeInTheDocument();
  });

  // TEST 2: Returns null when no item is passed
  test('returns null / renders nothing when item is null', () => {
    const { container } = render(
      <InventoryModal
        item={null}
        onClose={() => {}}
        itemImages={{}}
      />
    );

    // Component early-returns null, so there should be no children
    expect(container.firstChild).toBeNull();
  });

  // TEST 3: Shows the item title and description
  test('displays item title and description', () => {
    const item = {
      label: 'torch',
      title: 'Magic Torch',
      description: 'A very bright torch.',
    };

    const itemImages = {
      torch: '/figures/AI_Torch.png',
    };

    render(
      <InventoryModal
        item={item}
        onClose={() => {}}
        itemImages={itemImages}
      />
    );

    expect(screen.getByText('Magic Torch')).toBeInTheDocument();
    expect(screen.getByText('A very bright torch.')).toBeInTheDocument();
  });

  // TEST 4: Uses the correct image based on label / itemImages map
  test('renders the correct image for the item label', () => {
    const item = {
      label: 'torch',
      title: 'Magic Torch',
      description: 'A very bright torch.',
    };

    const itemImages = {
      torch: '/figures/AI_Torch.png',
    };

    render(
      <InventoryModal
        item={item}
        onClose={() => {}}
        itemImages={itemImages}
      />
    );

    const img = screen.getByAltText('image of torch');
    expect(img).toBeInTheDocument();
    expect(img).toHaveAttribute('src', '/figures/AI_Torch.png');
  });

  // TEST 5: Clicking Close button calls onClose
  test('calls onClose when Close button is clicked', () => {
    const item = {
      label: 'torch',
      title: 'Magic Torch',
      description: 'A very bright torch.',
    };

    const itemImages = {
      torch: '/figures/AI_Torch.png',
    };

    const handleClose = vi.fn();

    render(
      <InventoryModal
        item={item}
        onClose={handleClose}
        itemImages={itemImages}
      />
    );

    const closeButton = screen.getByText('Close');
    fireEvent.click(closeButton);

    expect(handleClose).toHaveBeenCalledTimes(1);
  });
});
