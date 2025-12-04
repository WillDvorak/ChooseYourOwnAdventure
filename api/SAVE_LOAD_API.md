# Save/Load Game Functionality API Documentation

## Overview

The save/load functionality allows players to save their game progress and resume from saved games. Game sessions are automatically persisted to the database, so every game action is automatically saved.

## API Endpoints

### 1. List Saved Games

**Endpoint:** `GET /api/game/saves/{playerName}`

**Description:** Retrieves all saved games for a specific player, sorted by most recently updated first.

**Path Parameters:**
- `playerName` (String): The name of the player

**Response:**
```json
{
  "playerName": "Player1",
  "count": 2,
  "saves": [
    {
      "id": 1,
      "playerName": "Player1",
      "currentSceneCode": "temple",
      "currentSceneTitle": "The Sacred Temple",
      "health": 75,
      "maxHealth": 100,
      "isEnded": false,
      "itemCount": 3,
      "createdAt": "2024-01-15T10:30:00",
      "updatedAt": "2024-01-15T14:45:00"
    },
    {
      "id": 2,
      "playerName": "Player1",
      "currentSceneCode": "forest",
      "currentSceneTitle": "Under Tall Pines",
      "health": 100,
      "maxHealth": 100,
      "isEnded": false,
      "itemCount": 1,
      "createdAt": "2024-01-15T09:00:00",
      "updatedAt": "2024-01-15T09:30:00"
    }
  ]
}
```

**Example:**
```bash
curl -X GET "http://localhost:8080/api/game/saves/Player1"
```

---

### 2. Load Saved Game

**Endpoint:** `GET /api/game/saves/{playerName}/{sessionId}`

**Description:** Loads a specific saved game session. Returns the current scene, available choices, health, and all player flags.

**Path Parameters:**
- `playerName` (String): The name of the player
- `sessionId` (Long): The ID of the game session to load

**Response:**
```json
{
  "sessionId": 1,
  "playerName": "Player1",
  "code": "temple",
  "title": "The Sacred Temple",
  "body": "A grand temple stands before you...",
  "isTerminal": false,
  "health": 75,
  "maxHealth": 100,
  "flags": {
    "health": 75,
    "maxHealth": 100,
    "torch": true,
    "key": true,
    "amulet": true
  },
  "choices": [
    {
      "id": 45,
      "label": "Use the amulet to open the door",
      "targetSceneCode": "temple",
      "requiresFlag": "amulet",
      "setsFlag": ""
    },
    {
      "id": 46,
      "label": "Enter the temple (if door is open)",
      "targetSceneCode": "ending1",
      "requiresFlag": "amulet",
      "setsFlag": ""
    }
  ],
  "updatedAt": "2024-01-15T14:45:00"
}
```

**Example:**
```bash
curl -X GET "http://localhost:8080/api/game/saves/Player1/1"
```

**Error Responses:**
- `400 Bad Request`: If session doesn't exist or doesn't belong to player
- `404 Not Found`: If session not found

---

### 3. Delete Saved Game

**Endpoint:** `DELETE /api/game/saves/{playerName}/{sessionId}`

**Description:** Deletes a saved game session. Only allows deletion if the session belongs to the specified player.

**Path Parameters:**
- `playerName` (String): The name of the player
- `sessionId` (Long): The ID of the game session to delete

**Response:**
```json
{
  "message": "Save game deleted successfully",
  "sessionId": 1
}
```

**Example:**
```bash
curl -X DELETE "http://localhost:8080/api/game/saves/Player1/1"
```

**Error Responses:**
- `400 Bad Request`: If session doesn't exist or doesn't belong to player

---

### 4. Get Session Summary

**Endpoint:** `GET /api/game/session/{sessionId}/summary`

**Description:** Gets a summary of a game session without requiring player name. Useful for displaying save information.

**Path Parameters:**
- `sessionId` (Long): The ID of the game session

**Response:**
```json
{
  "id": 1,
  "playerName": "Player1",
  "currentSceneCode": "temple",
  "currentSceneTitle": "The Sacred Temple",
  "health": 75,
  "maxHealth": 100,
  "isEnded": false,
  "itemCount": 3,
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T14:45:00"
}
```

**Example:**
```bash
curl -X GET "http://localhost:8080/api/game/session/1/summary"
```

---

## How It Works

### Automatic Saving

Game sessions are **automatically saved** to the database whenever:
- A new game session is created
- A player makes a choice (advances the game)
- Player flags are updated
- Health changes

No explicit "save" action is needed - the game state is always persisted.

### Loading a Game

To resume a game:
1. List all saves for the player using `GET /api/game/saves/{playerName}`
2. Select a save by its `sessionId`
3. Load the save using `GET /api/game/saves/{playerName}/{sessionId}`
4. Use the returned `sessionId` for all subsequent game actions

### Game State Persistence

Each game session stores:
- **Player name**: Identifies the owner
- **Current scene code**: Where the player is in the game
- **Flags JSON**: All game state including:
  - Health and max health
  - Inventory items (torch, key, amulet, sword, etc.)
  - Game progress flags
- **Timestamps**: When the session was created and last updated

### Security

- Players can only load/delete their own saved games
- The API validates that `sessionId` belongs to the specified `playerName`
- Attempting to access another player's save will return an error

---

## Frontend Integration Example

```javascript
// List all saves for a player
async function listSaves(playerName) {
  const response = await fetch(`/api/game/saves/${playerName}`);
  const data = await response.json();
  return data.saves;
}

// Load a specific save
async function loadSave(playerName, sessionId) {
  const response = await fetch(`/api/game/saves/${playerName}/${sessionId}`);
  const data = await response.json();
  return data;
}

// Delete a save
async function deleteSave(playerName, sessionId) {
  const response = await fetch(`/api/game/saves/${playerName}/${sessionId}`, {
    method: 'DELETE'
  });
  return await response.json();
}

// Usage
const saves = await listSaves("Player1");
const saveToLoad = saves[0]; // Most recent save
const gameState = await loadSave("Player1", saveToLoad.id);
// Use gameState.sessionId for subsequent game actions
```

---

## Notes

- **Multiple Saves**: Players can have multiple saved games. Each new game session creates a new save.
- **No Save Slots**: Currently, there's no limit on the number of saves per player. Consider implementing a limit in the future.
- **Save Names**: Currently, saves are identified by scene title and timestamp. Consider adding custom save names in the future.
- **Ended Games**: Games that have reached terminal scenes (ending1, ending2, death) are marked as `isEnded: true` but can still be loaded for viewing.

