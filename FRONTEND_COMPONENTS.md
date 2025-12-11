# Frontend Components Documentation

React component structure and implementation details.

---

## Component Hierarchy

```
App (main.jsx)
│
└── AppLayout
    │
    ├── DisplayBox
    │   └── Shows current scene title
    │
    ├── InventoryBox
    │   ├── Shows item icons
    │   └── InventoryModal (on click)
    │       └── Shows detailed item list
    │
    ├── StoryMap
    │   └── SVG map visualization
    │
    └── Textbox
        ├── HealthBar
        │   └── Dynamic HP display
        └── Choice buttons
```

---

## Core Components

### 1. AppLayout
**Path**: `src/components/structural/AppLayout.jsx`

**Purpose**: Root layout container managing global state.

**State**:
- `sceneInfo` - Current scene data (code, title, body, choices)
- `inventory` - Array of item names

**Functions**:
- `handleSetInventory(item, isGiving)` - Add/remove inventory items

**Layout**:
- Left column (3/12): DisplayBox, InventoryBox, StoryMap
- Right column (9/12): Textbox (main gameplay area)

**Theme**:
Centralized theme object with colors, fonts, borders for consistent styling.

---

### 2. Textbox
**Path**: `src/components/content/Textbox.jsx`

**Purpose**: Main gameplay component displaying story text and choices.

**Props**:
- `theme` - Styling object
- `onSceneChange(sceneInfo)` - Callback to update scene
- `handleInventory(item, isGiving)` - Callback for inventory changes

**State**:
- `sessionId` - Current game session
- `scene` - Scene data from API
- `storyHistory` - Array of previous scene texts

**Key Features**:
- Fetches `/api/game/session/create` on mount
- Fetches `/api/game/scene/{code}` for each scene
- Sends `/api/game/session/{id}/choice/{id}` on choice click
- Displays HealthBar at top
- Shows scrollable story history
- Filters choices based on required flags

**Item Detection**:
Parses `setsFlag` from choices to add items like `torch`, `key`, `sword`, etc.

---

### 3. HealthBar
**Path**: `src/components/content/HealthBar.jsx`

**Purpose**: Visual health indicator with dynamic color.

**Props**:
- `health` (default: 100) - Current HP
- `maxHealth` (default: 100) - Maximum HP

**Implementation**:
```javascript
// Calculate percentage
const percentage = (health / maxHealth) * 100;

// Color logic
if (percentage > 60) return green;
if (percentage > 30) return orange;
return red;
```

**Features**:
- Green bar when healthy (>60%)
- Orange bar when cautious (30-60%)
- Red bar when danger (<30%)
- Shows numeric display: "HP 85/100"
- Width animates with CSS transitions

---

### 4. StoryMap
**Path**: `src/components/content/StoryMap.jsx`

**Purpose**: SVG visualization of game structure.

**Props**:
- `currentScene` - Current scene object

**State**:
- `nodes` - Array of scenes with x,y coordinates
- `edges` - Array of connections between scenes
- `loading` - Loading state

**Implementation**:
1. Fetches `/api/game/story-map` on mount
2. Positions nodes in 2-column grid layout
3. Draws SVG lines (edges) between connected nodes
4. Highlights current node with pulsing circle + 📍 emoji
5. Shows terminal nodes with 🏁 emoji

**SVG Elements**:
- `<line>` - Connections with arrowheads
- `<rect>` - Scene boxes
- `<circle>` - Pulsing highlight for current scene
- `<text>` - Scene labels

**CSS Classes**:
- `.node-current` - Highlighted current location
- `.node-terminal` - Ending scenes
- `.node-pulse` - Animation effect

---

### 5. DisplayBox
**Path**: `src/components/content/DisplayBox.jsx`

**Purpose**: Shows current scene title at top of sidebar.

**Props**:
- `theme` - Styling object
- `sceneInfo` - Scene data

**Display**:
```
─────────────────
  Scene Title
     (code)
─────────────────
```

Simple text display with centered styling.

---

### 6. InventoryBox
**Path**: `src/components/content/InventoryBox.jsx`

**Purpose**: Displays collected items as icons.

**Props**:
- `theme` - Styling object
- `inventory` - Array of item names

**State**:
- `showModal` - Boolean for modal visibility

**Features**:
- Shows title "Inventory:"
- Displays item icons (PNG images from `/figures/`)
- Clickable → opens InventoryModal
- Shows "You have nothing in your inventory..." when empty

**Item Icons**:
- `torch` → AI_Torch.png
- `key` → AI_Key.png
- `sword` → AI_Sword.png
- `amulet` → AI_Amulet.png
- etc.

---

### 7. InventoryModal
**Path**: `src/components/content/InventoryModal.jsx`

**Purpose**: Detailed inventory view in modal.

**Props**:
- `show` - Boolean visibility
- `handleClose()` - Close callback
- `inventory` - Array of item names

**Features**:
- Bootstrap modal overlay
- Grid display of items with names
- Larger icons than InventoryBox
- Close button

---

## Data Flow Example

### User clicks "Head into the forest" choice:

1. **Textbox** → `handleChoice(choiceId)` called
2. **API Call** → `POST /api/game/session/1/choice/5`
3. **Backend** → Processes choice, updates database
4. **Response** → `{ code: "forest", health: 85, choices: [...] }`
5. **Textbox** → Updates state, adds to `storyHistory`
6. **AppLayout** → `onSceneChange()` updates `sceneInfo`
7. **Re-render**:
   - DisplayBox shows "Under Tall Pines"
   - HealthBar adjusts to 85/100
   - StoryMap highlights "forest" node
   - Textbox shows new choices

---

## Styling Approach

### Theme System
Centralized theme object in AppLayout:
```javascript
const theme = {
  background: 'linear-gradient(135deg, #1a0933 0%, #2d1b4e 100%)',
  containerBorder: '3px solid #d4af37',
  buttonBg: '#d4af37',
  fontFamily: 'Georgia, serif',
  // ...
};
```

Passed as prop to all components for consistent styling.

### CSS Files
- `HealthBar.css` - Health bar animations
- `StoryMap.css` - SVG styling, pulse animations
- `InventoryModalStyles.css` - Modal layout
- `index.css` - Global styles

---

## Testing

Test files in `src/test/`:
- `Textbox.test.jsx` - Core gameplay logic
- `InventoryBox.test.jsx` - Inventory display
- `InventoryModal.test.jsx` - Modal interactions
- `DisplayBox.test.jsx` - Title display
- `AppLayout.test.jsx` - Layout structure

Run tests:
```bash
npm test           # Run tests
npm run test:ui    # Visual test UI
npm run test:coverage  # Coverage report
```

---

## State Management Strategy

**Why not Redux/Context?**
- Small app with limited shared state
- Props drilling is manageable (2-3 levels max)
- Keeps code simple and maintainable

**State locations**:
- **AppLayout** - Global game state (scene, inventory)
- **Textbox** - Session and story history
- **StoryMap** - Map structure (fetched once)

**Callbacks** flow state up:
- `onSceneChange` - Updates scene in AppLayout
- `handleInventory` - Updates inventory in AppLayout

---

## Performance Considerations

1. **Memoization** - Could add `React.memo()` to DisplayBox (rarely changes)
2. **Story History** - Array grows indefinitely (could limit to last N scenes)
3. **SVG Rendering** - StoryMap renders once, doesn't re-fetch
4. **Image Loading** - Item icons load on demand

---

## Future Improvements

- Add loading states for API calls
- Implement error boundaries
- Add animations for scene transitions
- Save game functionality (localStorage)
- Sound effects for choices
- Accessibility improvements (ARIA labels)

