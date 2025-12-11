# API Documentation

REST API for the Text Quest adventure game built with Spring Boot.

**Base URL**: `http://localhost:8080/api/game`

---

## Endpoints

### 1. Health Check
**GET** `/api/game/health`

Check if the API is running.

**Response**:
```json
"Game API is running!"
```

---

### 2. Create Game Session
**POST** `/api/game/session/create`

Create a new game session for a player.

**Query Parameters**:
- `playerName` (optional, default: "Player") - Player's name
- `startingScene` (optional, default: "intro") - Starting scene code

**Example**:
```bash
POST /api/game/session/create?playerName=Adventurer&startingScene=intro
```

**Response**:
```json
{
  "sessionId": 1,
  "playerName": "Adventurer",
  "currentScene": "intro"
}
```

---

### 3. Get Scene
**GET** `/api/game/scene/{sceneCode}`

Retrieve scene details with available choices.

**Path Parameters**:
- `sceneCode` (required) - Scene identifier (e.g., "intro", "forest", "cave")

**Query Parameters**:
- `sessionId` (optional) - Game session ID for personalized data

**Example**:
```bash
GET /api/game/scene/intro?sessionId=1
```

**Response**:
```json
{
  "code": "intro",
  "title": "The Long One",
  "body": "You wake at a campfire. A narrow path leads into a pine forest...",
  "isTerminal": false,
  "health": 100,
  "maxHealth": 100,
  "choices": [
    {
      "id": 1,
      "label": "Head into the forest",
      "targetSceneCode": "forest",
      "requiresFlag": "",
      "setsFlag": ""
    }
  ]
}
```

---

### 4. Make Choice
**POST** `/api/game/session/{sessionId}/choice/{choiceId}`

Process a player's choice and advance the game.

**Path Parameters**:
- `sessionId` (required) - Game session ID
- `choiceId` (required) - Choice ID to execute

**Example**:
```bash
POST /api/game/session/1/choice/5
```

**Response**:
```json
{
  "code": "forest",
  "title": "Under Tall Pines",
  "body": "The forest hums with life...",
  "isTerminal": false,
  "health": 85,
  "maxHealth": 100,
  "choices": [...]
}
```

**Side Effects**:
- Updates player health based on choice
- Adds/removes items from inventory
- Updates game session state

---

### 5. Get Story Map
**GET** `/api/game/story-map`

Get complete game structure for visualization.

**Response**:
```json
{
  "nodes": [
    {
      "id": "intro",
      "label": "The Long One",
      "isTerminal": false
    }
  ],
  "edges": [
    {
      "from": "intro",
      "to": "forest",
      "label": "Head into the forest",
      "requiresFlag": "",
      "setsFlag": ""
    }
  ]
}
```

---

## Error Handling

All endpoints return appropriate HTTP status codes:
- **200 OK** - Success
- **400 Bad Request** - Invalid input
- **404 Not Found** - Scene/session not found
- **500 Internal Server Error** - Server error

**Error Response Format**:
```json
{
  "error": "Error message description"
}
```

---

## Game Mechanics

### Flags System
Flags represent items, knowledge, or game state:
- `torch`, `key`, `amulet`, `sword` - Items
- `knowledge`, `map` - Knowledge
- `gold`, `potion` - Resources
- `door_open` - State flags

### Health System
- Starting health: 100 HP
- Health stored in session flags
- Reaches 0 → automatic redirect to "death" scene
- Format: `health:-30` (damage) or `health:+25` (healing)

### Choice Requirements
Some choices require specific flags:
```json
{
  "requiresFlag": "torch",  // Player must have torch
  "setsFlag": "key"         // Choice grants key
}
```

---

## CORS Configuration

Allowed origins:
- `http://localhost:5173` (Vite dev server)
- `http://localhost` (Docker frontend)

---

## Database

Uses MySQL 8.0 with Flyway migrations:
- **V1__schema.sql** - Database schema
- **V2__seed.sql** - Game content (scenes, choices)

See `sql/GAME_DOCUMENTATION.md` for complete game flow.

