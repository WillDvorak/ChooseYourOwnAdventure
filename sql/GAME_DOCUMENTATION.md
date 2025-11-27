# Game Documentation: Scenes and Transitions

This document describes all scenes, transitions, and game mechanics in the text-based adventure game.

## Table of Contents
- [Scenes Overview](#scenes-overview)
- [Scene Details](#scene-details)
- [Transitions and Choices](#transitions-and-choices)
- [Game Flow Diagram](#game-flow-diagram)
- [Visual Diagram Files](#visual-diagram-files)
- [Flags and Items System](#flags-and-items-system)
- [Health System](#health-system)

---

## Scenes Overview

The game consists of **15 scenes**:

| Scene Code | Title | Type | Terminal? |
|------------|-------|------|-----------|
| `intro` | The Long One | Starting | No |
| `forest` | Under Tall Pines | Exploration | No |
| `cave` | Cave Mouth | Exploration | No |
| `ruins` | Ancient Ruins | Exploration | No |
| `bridge` | The Old Bridge | Exploration | No |
| `village` | The Quiet Village | Exploration | No |
| `dungeon` | The Dark Dungeon | Exploration | No |
| `temple` | The Sacred Temple | Exploration | No |
| `treasure` | Hidden Cache | Goal | No |
| `danger` | Dangerous Path | Combat | No |
| `danger2` | The Ambush | Combat | No |
| `heal` | Healing Spring | Rest | No |
| `ending1` | The Hero's Return | Ending | Yes |
| `ending2` | The Power Within | Ending | Yes |
| `death` | Game Over | Ending | Yes |

---

## Scene Details

### 1. `intro` - The Long One
- **Type**: Starting Scene
- **Description**: "You wake at a campfire. A narrow path leads into a pine forest. A faint glow flickers to the east. To the west, you see ancient ruins silhouetted against the moon."
- **Terminal**: No
- **Purpose**: The game's entry point where players begin their adventure. Offers four different paths.

### 2. `forest` - Under Tall Pines
- **Type**: Exploration Scene
- **Description**: "The forest hums with life. You notice footprints leading deeper, a torn map scrap on the ground, and hear distant howling."
- **Terminal**: No
- **Purpose**: Central hub with multiple exploration options. Players can search for supplies, follow paths, or encounter dangers.

### 3. `cave` - Cave Mouth
- **Type**: Exploration Scene
- **Description**: "A damp cave descends into darkness. It smells of iron and something ancient. Your torch sputters, revealing strange markings on the walls."
- **Terminal**: No
- **Purpose**: Leads to treasure (requires torch) or dungeon. Can also be used to study markings for a map.

### 4. `ruins` - Ancient Ruins
- **Type**: Exploration Scene
- **Description**: "Crumbling stone pillars rise from the earth. Ancient symbols glow faintly. A pedestal in the center holds something mysterious."
- **Terminal**: No
- **Purpose**: Players can obtain amulet or knowledge here. Leads to temple or bridge.

### 5. `bridge` - The Old Bridge
- **Type**: Exploration Scene
- **Description**: "A rickety wooden bridge spans a chasm. Below, a river roars. The bridge looks unstable, but it's the only way across."
- **Terminal**: No
- **Purpose**: Connects intro area to village. Crossing quickly risks health loss.

### 6. `village` - The Quiet Village
- **Type**: Exploration Scene
- **Description**: "A small village lies ahead. Smoke rises from chimneys, but you see no one. Something feels wrong here."
- **Terminal**: No
- **Purpose**: Players can investigate, search for survivors (get potion), or continue to temple.

### 7. `dungeon` - The Dark Dungeon
- **Type**: Exploration Scene
- **Description**: "You descend into a dungeon. Torches line the walls, casting eerie shadows. You hear something moving in the darkness ahead."
- **Terminal**: No
- **Purpose**: Can fight creatures (needs sword), sneak past, or search for weapons. Leads to treasure or danger2.

### 8. `temple` - The Sacred Temple
- **Type**: Exploration Scene
- **Description**: "A grand temple stands before you. Its doors are sealed with ancient magic. Strange symbols pulse with power."
- **Terminal**: No
- **Purpose**: Final location before endings. Requires amulet, key, sword, or knowledge to open. Leads to ending1 or ending2.

### 9. `treasure` - Hidden Cache
- **Type**: Goal Scene
- **Description**: "You pry open a chest: old coins, a small brass key, and a glowing amulet. The amulet pulses with ancient power."
- **Terminal**: No
- **Purpose**: Contains key items (key, amulet, gold, sword). Players can take items and continue exploring or head to temple.

### 10. `danger` - Dangerous Path
- **Type**: Combat Scene
- **Description**: "You stumble upon a pack of wolves! They bare their teeth menacingly and begin to circle you."
- **Terminal**: No
- **Purpose**: Tests player's risk management. All choices result in health loss, but some provide shortcuts.

### 11. `danger2` - The Ambush
- **Type**: Combat Scene
- **Description**: "Bandits leap from the shadows! They brandish weapons and demand your valuables."
- **Terminal**: No
- **Purpose**: Second combat encounter. Players can fight, negotiate with gold, or run away.

### 12. `heal` - Healing Spring
- **Type**: Rest Scene
- **Description**: "You discover a clear spring. The water glows faintly and appears magical. You feel its restorative power."
- **Terminal**: No
- **Purpose**: Provides health restoration options to help players recover. Can also get potion.

### 13. `ending1` - The Hero's Return
- **Type**: Ending Scene
- **Description**: "You return to the village with the ancient artifacts. The people celebrate your victory. You have saved the realm!"
- **Terminal**: Yes
- **Purpose**: Good ending - reached by entering temple with amulet or key.

### 14. `ending2` - The Power Within
- **Type**: Ending Scene
- **Description**: "You absorb the ancient power. You feel yourself transforming, becoming something more than human. The world will never be the same."
- **Terminal**: Yes
- **Purpose**: Alternative ending - reached by absorbing temple's power with knowledge.

### 15. `death` - Game Over
- **Type**: Ending Scene
- **Description**: "Your health has dropped to zero. Your adventure ends here. The darkness claims you."
- **Terminal**: Yes
- **Purpose**: Game over condition when health reaches zero.

---

## Transitions and Choices

### From `intro` (Starting Scene)

| Choice | Target Scene | Requires Flag | Sets Flag | Notes |
|--------|--------------|---------------|-----------|-------|
| Head into the forest | `forest` | None | None | Safe path to exploration |
| Walk toward the glow | `cave` | None | None | Direct path to cave |
| Explore the ancient ruins | `ruins` | None | None | Leads to ruins |
| Cross the old bridge | `bridge` | None | None | Leads to bridge/village |

### From `forest` (Exploration Hub)

| Choice | Target Scene | Requires Flag | Sets Flag | Notes |
|--------|--------------|---------------|-----------|-------|
| Follow the footprints | `cave` | None | None | Direct path to cave |
| Search for supplies | `forest` | None | `torch` | Self-loop, grants torch |
| Take the dangerous path (risky!) | `danger` | None | None | Leads to combat |
| Look for a healing spring | `heal` | None | None | Leads to healing |
| Follow the path to the village | `village` | None | None | Leads to village |

### From `cave` (Cave Exploration)

| Choice | Target Scene | Requires Flag | Sets Flag | Notes |
|--------|--------------|---------------|-----------|-------|
| Descend deeper (needs torch) | `treasure` | `torch` | `key` | **Victory path** - requires torch |
| Retreat to the campfire | `intro` | None | None | Return to start |
| Explore a side passage | `dungeon` | None | None | Leads to dungeon |
| Study the wall markings | `cave` | None | `map` | Self-loop, grants map |

### From `ruins` (Ancient Ruins)

| Choice | Target Scene | Requires Flag | Sets Flag | Notes |
|--------|--------------|---------------|-----------|-------|
| Examine the pedestal | `ruins` | None | `amulet` | Self-loop, grants amulet |
| Read the ancient symbols | `ruins` | None | `knowledge` | Self-loop, grants knowledge |
| Head to the temple | `temple` | None | None | Direct path to temple |
| Return to the campfire | `intro` | None | None | Return to start |
| Head to the bridge | `bridge` | None | None | Leads to bridge |

### From `bridge` (The Old Bridge)

| Choice | Target Scene | Requires Flag | Sets Flag | Notes |
|--------|--------------|---------------|-----------|-------|
| Cross carefully | `village` | None | None | Safe crossing |
| Run across quickly (risky!) | `village` | None | `health:-15` | Fast but risky |
| Turn back | `intro` | None | None | Return to start |

### From `village` (The Quiet Village)

| Choice | Target Scene | Requires Flag | Sets Flag | Notes |
|--------|--------------|---------------|-----------|-------|
| Search for survivors | `village` | None | `potion` | Self-loop, grants potion |
| Continue to the temple | `temple` | None | None | Direct path to temple |
| Return to the forest | `forest` | None | None | Return to forest |

### From `dungeon` (The Dark Dungeon)

| Choice | Target Scene | Requires Flag | Sets Flag | Notes |
|--------|--------------|---------------|-----------|-------|
| Fight the creature (needs sword) | `treasure` | `sword` | `key` | Requires sword, grants key |
| Sneak past carefully | `treasure` | None | None | Safe but no reward |
| Retreat (risky!) | `danger2` | None | None | Leads to bandits |
| Search for a weapon | `dungeon` | None | `sword` | Self-loop, grants sword |

### From `temple` (The Sacred Temple)

| Choice | Target Scene | Requires Flag | Sets Flag | Notes |
|--------|--------------|---------------|-----------|-------|
| Use the amulet to open the door | `temple` | `amulet` | `door_open` | Opens door, sets door_open flag |
| Use the key to unlock the door | `temple` | `key` | `door_open` | Opens door, sets door_open flag |
| Force the door open (needs sword, -25 HP) | `temple` | `sword` | `door_open` | Opens door but costs 25 HP |
| Study the symbols (needs knowledge) | `temple` | `knowledge` | `door_open` | Opens door, sets door_open flag |
| Enter the temple (if door is open) | `ending1` | `door_open` | None | **Good ending** - requires door_open |
| Absorb the temple's power | `ending2` | `knowledge` | None | **Alternative ending** |

### From `treasure` (Hidden Cache)

| Choice | Target Scene | Requires Flag | Sets Flag | Notes |
|--------|--------------|---------------|-----------|-------|
| Take the key and amulet | `forest` | None | `key` | Grants key and amulet |
| Take only the gold | `forest` | None | `gold` | Grants gold |
| Explore further | `dungeon` | None | None | Leads to dungeon |
| Search for a weapon | `treasure` | None | `sword` | Self-loop, grants sword |
| Head to the temple | `temple` | None | None | Direct path to temple |

### From `danger` (Wolves Encounter)

| Choice | Target Scene | Requires Flag | Sets Flag | Notes |
|--------|--------------|---------------|-----------|-------|
| Fight the wolves (-30 HP) | `forest` | None | `health:-30` | Moderate damage |
| Run away carefully (-10 HP) | `forest` | None | `health:-10` | Minimal damage |
| Use your sword to fight (if you have it) | `forest` | `sword` | `health:-10` | Less damage with sword |
| Try to befriend them (risky! -50 HP) | `cave` | None | `health:-50` | High damage, but reaches cave |

### From `danger2` (Bandit Ambush)

| Choice | Target Scene | Requires Flag | Sets Flag | Notes |
|--------|--------------|---------------|-----------|-------|
| Fight the bandits | `village` | None | `health:-20` | Moderate damage |
| Fight with your sword (if you have it) | `village` | `sword` | `health:-5` | Less damage with sword |
| Negotiate with them | `village` | `gold` | None | Requires gold, no damage |
| Run away | `forest` | None | `health:-15` | Escape with damage |

### From `heal` (Healing Spring)

| Choice | Target Scene | Requires Flag | Sets Flag | Notes |
|--------|--------------|---------------|-----------|-------|
| Drink from the spring (+25 HP) | `forest` | None | `health:25` | Moderate healing |
| Bathe in the spring (full heal!) | `forest` | None | `health:100` | Full restoration |
| Fill a container with the water | `forest` | None | `potion` | Grants potion |
| Leave without drinking | `forest` | None | None | No healing |

---

## Game Flow Diagram

```
                    [intro]
                   /  |  \
          forest  cave  ruins  bridge
          /  |  \   |     |      |
    danger  heal  cave  temple  village
       |     |     |     |        |
    forest  forest  |  ending1  temple
       |            |     |        |
  (health)      treasure ending2  |
       |            |              |
    [death]      dungeon         ending1
                    |
                 danger2
```

## Visual Diagram Files

For better visualization, see the following diagram files in this directory:

1. **`game_flow_simple.txt`** - ASCII art diagram
   - Plain text format, viewable in any text editor
   - Includes detailed path descriptions and legend

### How to View the Diagrams

**Text (`.txt`):**
- Open in any text editor or terminal

### Key Paths:

1. **Path to Victory (Ending1 - Hero's Return)**:
   - `intro` → `ruins` → Examine pedestal → `ruins` (get amulet) → `temple` → Use amulet (sets door_open) → Enter temple (requires door_open) → `ending1` ✅
   - OR: `intro` → `forest` → Search supplies → `forest` (get torch) → `cave` → Descend with torch → `treasure` (get key) → `temple` → Use key (sets door_open) → Enter temple (requires door_open) → `ending1` ✅
   - OR: `intro` → `cave` → Explore side passage → `dungeon` → Search for weapon → `dungeon` (get sword) → Fight creature → `treasure` (get key) → `temple` → Use key (sets door_open) → Enter temple (requires door_open) → `ending1` ✅
   - OR: `intro` → (get sword) → `temple` → Force door open (sets door_open, -25 HP) → Enter temple (requires door_open) → `ending1` ✅

2. **Path to Alternative Ending (Ending2 - Power Within)**:
   - `intro` → `ruins` → Read symbols → `ruins` (get knowledge) → `temple` → Study symbols (sets door_open) → Absorb power → `ending2` ✅

3. **Dangerous Path**:
   - `intro` → `forest` → Dangerous path → `danger` → (lose health) → `forest` → (continue or heal)

4. **Healing Loop**:
   - `intro` → `forest` → Look for healing spring → `heal` → (restore health) → `forest`

5. **Dungeon Path**:
   - `intro` → `cave` → Explore side passage → `dungeon` → Search for weapon → `dungeon` (get sword) → Fight creature → `treasure` (get key) → `temple` → Use key (sets door_open) → Enter temple (requires door_open) → `ending1`

---

## Flags and Items System

### Available Flags/Items

| Flag Name | Description | How to Obtain |
|-----------|-------------|---------------|
| `torch` | Required to safely descend into the cave | From `forest`: "Search for supplies" |
| `key` | Used to open temple door | - From `cave` → `treasure` (requires torch)<br>- From `dungeon` → `treasure` (requires sword)<br>- From `treasure`: "Take the key and amulet" |
| `amulet` | Used to open temple door | - From `ruins`: "Examine the pedestal"<br>- From `treasure`: "Take the key and amulet" |
| `sword` | Reduces combat damage, can force temple door open | - From `dungeon`: "Search for a weapon"<br>- From `treasure`: "Search for a weapon" |
| `map` | Knowledge item | From `cave`: "Study the wall markings" |
| `knowledge` | Opens temple door, unlocks ending2 | From `ruins`: "Read the ancient symbols" |
| `potion` | Healing item | - From `village`: "Search for survivors"<br>- From `heal`: "Fill a container with the water" |
| `gold` | Currency for negotiation | From `treasure`: "Take only the gold" |
| `door_open` | Indicates temple door is unlocked | Set when using amulet, key, sword, or knowledge at temple |

### Flag Mechanics

- **`requires_flag`**: A choice is only available if the player has this flag
- **`sets_flag`**: Choosing this option grants the player this flag
- Flags are stored in the `game_sessions.flags_json` field as JSON

### Example Flag Usage

- **Torch Requirement**: The choice "Descend deeper (needs torch)" in `cave` requires the `torch` flag
- **Temple Access**: Multiple ways to open temple - use amulet, key, sword (with health cost), or knowledge to set `door_open` flag, then enter with `door_open` flag
- **Sword Advantage**: Having a sword reduces damage in combat encounters
- **Door Opening**: Temple door-opening choices set the `door_open` flag, which is then required to enter the temple

---

## Health System

### Health Mechanics

- **Starting Health**: 100 HP (default)
- **Health Storage**: Stored in `flags_json` as `{"health": 100}`
- **Death Condition**: When health ≤ 0, player is redirected to `death` scene

### Health Modifiers

| Scene | Choice | Health Change | Result |
|-------|--------|---------------|--------|
| `bridge` | Run across quickly | -15 HP | Risky crossing |
| `danger` | Fight the wolves | -30 HP | Moderate risk |
| `danger` | Run away carefully | -10 HP | Safe option |
| `danger` | Use sword to fight | -10 HP | Less damage with sword |
| `danger` | Try to befriend them | -50 HP | High risk, but reaches cave |
| `danger2` | Fight the bandits | -20 HP | Moderate risk |
| `danger2` | Fight with sword | -5 HP | Much less damage with sword |
| `danger2` | Run away | -15 HP | Escape with damage |
| `temple` | Force door open | -25 HP | Opens door (sets door_open) but costs health |
| `heal` | Drink from spring | +25 HP | Moderate healing |
| `heal` | Bathe in spring | +100 HP | Full restoration |

### Health Format

Health changes are specified in the `sets_flag` field using the format:
- `health:+25` - Adds 25 health
- `health:-30` - Subtracts 30 health
- `health:100` - Sets health to 100 (full heal)

### Health Management Strategy

1. **Early Game**: Avoid `danger` and `danger2` scenes if possible
2. **Mid Game**: Use `heal` scene to restore health before risky paths
3. **Risk Management**: The "Try to befriend them" option in `danger` is very risky (-50 HP) but provides a shortcut to `cave`
4. **Sword Advantage**: Obtaining a sword significantly reduces combat damage
5. **Temple Access**: Force opening temple door costs 25 HP - consider getting key/amulet instead

---

## Winning Conditions

### Primary Victory Paths

**Ending1 - Hero's Return:**
1. Start at `intro`
2. Get amulet (from `ruins`) OR get key (from `treasure` via `cave` with torch OR `dungeon` with sword) OR get sword
3. Navigate to `temple`
4. Use amulet/key/sword/knowledge to open door (sets `door_open` flag)
5. Enter temple (requires `door_open`) → `ending1` ✅

**Ending2 - Power Within:**
1. Start at `intro`
2. Go to `ruins`
3. Read ancient symbols (get `knowledge`)
4. Navigate to `temple`
5. Study symbols (requires `knowledge`)
6. Absorb temple's power → `ending2` ✅

### Alternative Paths
- Players can take dangerous paths through `danger`/`danger2` to reach locations faster, but risk losing significant health
- Players can loop through `heal` to maintain health while exploring
- Multiple routes to obtain required items (key, amulet, knowledge)

---

## Database Schema Reference

### Scenes Table
- `code`: Unique identifier (VARCHAR 64)
- `title`: Display title (VARCHAR 128)
- `body`: Scene description (TEXT)
- `is_terminal`: Boolean flag for ending scenes (TINYINT 1)

### Choices Table
- `scene_id`: Foreign key to scenes table
- `label`: Choice text shown to player (VARCHAR 255)
- `target_scene_code`: Destination scene code (VARCHAR 64)
- `requires_flag`: Required flag to show this choice (VARCHAR 64, nullable)
- `sets_flag`: Flag/item granted by this choice (VARCHAR 64, nullable)

---

## Notes for Developers

1. **Terminal Scenes**: When a player reaches a terminal scene (`is_terminal = 1`), the game ends
2. **Flag Checking**: The backend checks `requires_flag` before displaying choices
3. **Health Processing**: Health changes in `sets_flag` are parsed and applied by the `GameService`
4. **Death Handling**: If health drops to 0, the player is automatically redirected to the `death` scene
5. **Self-Loops**: Some choices loop back to the same scene but set flags (e.g., "Search for supplies" in `forest`). These are intentional for item acquisition and always have alternative exit paths.
6. **Multiple Endings**: Two different endings based on player choices - `ending1` (hero) and `ending2` (power)
7. **Temple Access**: Temple can be opened multiple ways - use amulet, key, sword (costs health), or knowledge to set `door_open` flag, then enter with that flag
8. **Infinite Loop Prevention**: All self-loops have alternative exit paths. The useless "Investigate empty houses" loop in village was removed to prevent player frustration.

---

## Future Expansion Ideas

- Add more scenes (mountain, valley, etc.)
- Add more items and item combinations
- Add more endings based on collected items
- Add branching storylines based on player choices
- Add inventory management for multiple items
- Add item-based puzzles
