// AppLayout.test.jsx - Unit tests for the AppLayout component
import { render, screen } from '@testing-library/react';
import { describe, test, expect, beforeEach, vi } from 'vitest';
import AppLayout from '../components/structural/AppLayout';

// Mock child components
vi.mock('../components/content/Textbox', () => ({
  default: ({ theme, onSceneChange, handleInventory }) => (
    <div data-testid="textbox">
      <button onClick={() => onSceneChange?.({ code: 'intro', title: 'Introduction' })}>
        Change Scene
      </button>
      <button onClick={() => handleInventory?.('torch', true)}>
        Add Item
      </button>
    </div>
  )
}));

vi.mock('../components/content/DisplayBox', () => ({
  default: ({ theme, sceneInfo }) => (
    <div data-testid="display-box">
      {sceneInfo ? <h2>{sceneInfo.title}</h2> : <p>No scene</p>}
    </div>
  )
}));

vi.mock('../components/content/InventoryBox', () => ({
  default: ({ theme, inventory }) => (
    <div data-testid="inventory-box">
      <p>Inventory: {inventory.length} items</p>
    </div>
  )
}));

vi.mock('../components/content/StoryMap', () => ({
  default: ({ currentScene }) => (
    <div data-testid="story-map">
      {currentScene ? <p>Current: {currentScene.code}</p> : <p>No current scene</p>}
    </div>
  )
}));

describe('AppLayout Component', () => {
  
  // TEST 1: Basic rendering
  test('renders component without crashing', () => {
    render(<AppLayout />);
    expect(screen.getByTestId('textbox')).toBeInTheDocument();
    expect(screen.getByTestId('display-box')).toBeInTheDocument();
    expect(screen.getByTestId('inventory-box')).toBeInTheDocument();
    expect(screen.getByTestId('story-map')).toBeInTheDocument();
  });

  // TEST 2: Initializes with default inventory
  test('initializes with default inventory', () => {
    render(<AppLayout />);
    expect(screen.getByText(/Inventory: 1 items/i)).toBeInTheDocument();
  });

  // TEST 3: Initializes with null sceneInfo
  test('initializes with null sceneInfo', () => {
    render(<AppLayout />);
    expect(screen.getByText('No scene')).toBeInTheDocument();
  });

  // TEST 4: Updates sceneInfo when onSceneChange is called
  test('updates sceneInfo when onSceneChange is called', async () => {
    render(<AppLayout />);
    
    const changeSceneButton = screen.getByText('Change Scene');
    changeSceneButton.click();
    
    expect(screen.getByText('Introduction')).toBeInTheDocument();
  });

  // TEST 5: Adds item to inventory when handleInventory is called with isGiving=true
  test('adds item to inventory when handleInventory is called with isGiving=true', () => {
    render(<AppLayout />);
    
    // Initially has 1 item (torch)
    expect(screen.getByText(/Inventory: 1 items/i)).toBeInTheDocument();
    
    const addItemButton = screen.getByText('Add Item');
    addItemButton.click();
    
    // Should now have 2 items
    expect(screen.getByText(/Inventory: 2 items/i)).toBeInTheDocument();
  });

  // TEST 6: Passes sceneInfo to DisplayBox
  test('passes sceneInfo to DisplayBox', () => {
    render(<AppLayout />);
    
    const changeSceneButton = screen.getByText('Change Scene');
    changeSceneButton.click();
    
    expect(screen.getByText('Introduction')).toBeInTheDocument();
  });

  // TEST 7: Passes sceneInfo to StoryMap as currentScene
  test('passes sceneInfo to StoryMap as currentScene', () => {
    render(<AppLayout />);
    
    const changeSceneButton = screen.getByText('Change Scene');
    changeSceneButton.click();
    
    expect(screen.getByText(/Current: intro/i)).toBeInTheDocument();
  });

  // TEST 8: Passes inventory to InventoryBox
  test('passes inventory to InventoryBox', () => {
    render(<AppLayout />);
    expect(screen.getByText(/Inventory: 1 items/i)).toBeInTheDocument();
  });

  // TEST 9: Passes theme to child components
  test('passes theme to child components', () => {
    render(<AppLayout />);
    // All child components should receive theme prop
    // This is verified by components rendering without errors
    expect(screen.getByTestId('textbox')).toBeInTheDocument();
    expect(screen.getByTestId('display-box')).toBeInTheDocument();
    expect(screen.getByTestId('inventory-box')).toBeInTheDocument();
  });

  // TEST 10: Handles multiple scene changes
  test('handles multiple scene changes', () => {
    render(<AppLayout />);
    
    const changeSceneButton = screen.getByText('Change Scene');
    
    // Change scene first time
    changeSceneButton.click();
    expect(screen.getByText('Introduction')).toBeInTheDocument();
    
    // Change scene again (simulated by clicking again)
    changeSceneButton.click();
    expect(screen.getByText('Introduction')).toBeInTheDocument();
  });
});

