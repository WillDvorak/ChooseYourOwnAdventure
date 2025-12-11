# API Quick Reference

This document provides a quick reference for all API endpoints in the TextQuest game.

## Base URL

- **Local Development**: `http://localhost:8080/api/game`
- **Docker**: `http://localhost:8080/api/game`

## Endpoints

### 1. Health Check

Check if the API is running.

```http
GET /api/game/health
```

**Response**: `200 OK`
```
Game API is running!
```

---

### 2. Get Scene

Retrieve a scene by its code with all available choices.

```http
GET /api/game/scene/{sceneCode}?sessionId={sessionId}
```

**Path Parameters**:
- `sceneCode` (required) - Scene identifier (e.g., "intro", "forest", "cave")

**Query Parameters**:
- `sessionId` (optional) - Game session ID to include health information

**Response**: `200 OK`
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
    },
    {
      "id": 2,
      "label": "Walk toward the glow",
      "targetSceneCode": "cave",
      "requiresFlag": "",
      "setsFlag": ""
    }
  ]
}
```

**Example**:
```bash
# Get scene without session
curl http://localhost:8080/api/game/scene/intro

# Get scene with session health
curl http://localhost:8080/api/game/scene/intro?sessionId=1
```

**Error Responses**:
- `404 Not Found` - Scene not found
- `500 Internal Server Error` - Server error

---

### 3. Create Game Session

Create a new game session for a player.

```http
POST /api/game/session/create?playerName={name}&startingScene={scene}
```

**Query Parameters**:
- `playerName` (optional) - Player's name (default: "Player")
- `startingScene` (optional) - Starting scene code (default: "intro")

**Response**: `200 OK`
```json
{
  "sessionId": 1,
  "playerName": "Alice",
  "currentScene": "intro"
}
```

**Example**:
```bash
# Create session with defaults
curl -X POST http://localhost:8080/api/game/session/create

# Create session with custom name and scene
curl -X POST "http://localhost:8080/api/game/session/create?playerName=Alice&startingScene=intro"
```

**Error Responses**:
- `400 Bad Request` - Invalid starting scene or other validation error

---

### 4. Make Choice

Process a player's choice and advance the game.

```http
POST /api/game/session/{sessionId}/choice/{choiceId}
```

**Path Parameters**:
- `sessionId` (required) - Game session ID
- `choiceId` (required) - Choice ID to execute

**Response**: `200 OK`
```json
{
  "code": "forest",
  "title": "Under Tall Pines",
  "body": "The forest hums with life. You notice footprints leading deeper...",
  "isTerminal": false,
  "health": 100,
  "maxHealth": 100,
  "choices": [
    {
      "id": 5,
      "label": "Follow the footprints",
      "targetSceneCode": "cave",
      "requiresFlag": "",
      "setsFlag": ""
    },
    {
      "id": 6,
      "label": "Search for supplies",
      "targetSceneCode": "forest",
      "requiresFlag": "",
      "setsFlag": "torch"
    }
  ]
}
```

**Example**:
```bash
curl -X POST http://localhost:8080/api/game/session/1/choice/1
```

**Error Responses**:
- `400 Bad Request` - Invalid choice, session not found, or game ended
- `404 Not Found` - Session or choice not found

**Notes**:
- This endpoint processes the choice, updates player flags/health, and moves to the next scene
- Health changes are applied automatically based on the choice's `setsFlag` value
- If health drops to 0, the player is redirected to the `death` scene

---

### 5. Get Story Map

Retrieve the complete story structure for visualization.

```http
GET /api/game/story-map
```

**Response**: `200 OK`
```json
{
  "nodes": [
    {
      "id": "intro",
      "label": "The Long One",
      "isTerminal": false
    },
    {
      "id": "forest",
      "label": "Under Tall Pines",
      "isTerminal": false
    },
    {
      "id": "ending1",
      "label": "The Hero's Return",
      "isTerminal": true
    }
  ],
  "edges": [
    {
      "from": "intro",
      "to": "forest",
      "label": "Head into the forest",
      "requiresFlag": "",
      "setsFlag": ""
    },
    {
      "from": "forest",
      "to": "cave",
      "label": "Follow the footprints",
      "requiresFlag": "",
      "setsFlag": ""
    },
    {
      "from": "temple",
      "to": "ending1",
      "label": "Enter the temple",
      "requiresFlag": "door_open",
      "setsFlag": ""
    }
  ]
}
```

**Example**:
```bash
curl http://localhost:8080/api/game/story-map
```

**Use Cases**:
- Visualizing the game's story graph
- Debugging game flow
- Creating story maps in the frontend

---

## Data Models

### Scene Object

```json
{
  "code": "string",
  "title": "string",
  "body": "string",
  "isTerminal": boolean,
  "health": number,
  "maxHealth": number,
  "choices": [Choice]
}
```

### Choice Object

```json
{
  "id": number,
  "label": "string",
  "targetSceneCode": "string",
  "requiresFlag": "string",
  "setsFlag": "string"
}
```

### Game Session Object

```json
{
  "sessionId": number,
  "playerName": "string",
  "currentScene": "string"
}
```

### Story Map Response

```json
{
  "nodes": [Node],
  "edges": [Edge]
}
```

**Node**:
```json
{
  "id": "string",
  "label": "string",
  "isTerminal": boolean
}
```

**Edge**:
```json
{
  "from": "string",
  "to": "string",
  "label": "string",
  "requiresFlag": "string",
  "setsFlag": "string"
}
```

---

## Flag System

Flags represent player state, inventory items, and game progress. They are stored in the `game_sessions.flags_json` field as JSON.

### Common Flags

| Flag | Description | How to Obtain |
|------|-------------|---------------|
| `torch` | Required to descend into cave | Search for supplies in forest |
| `key` | Opens temple door | From treasure or dungeon |
| `amulet` | Opens temple door | From ruins pedestal or treasure |
| `sword` | Reduces combat damage | From dungeon or treasure |
| `knowledge` | Opens temple door, unlocks ending2 | Read symbols in ruins |
| `potion` | Healing item | From village or healing spring |
| `gold` | Currency for negotiation | From treasure |
| `door_open` | Temple door unlocked | Set when using amulet/key/sword/knowledge at temple |

### Health Flags

Health is stored as flags:
- `health` - Current health (0-100)
- `maxHealth` - Maximum health (typically 100)

### Flag Format in Choices

- **`requires_flag`**: Choice is only available if player has this flag
- **`sets_flag`**: Choosing this option grants the player this flag
  - Can also modify health: `health:+25`, `health:-30`, `health:100`

---

## Error Handling

All endpoints may return error responses:

```json
{
  "error": "Error message description"
}
```

### Common Error Scenarios

1. **Scene Not Found**
   - Status: `404 Not Found` or `500 Internal Server Error`
   - Message: "Scene not found: {sceneCode}"

2. **Session Not Found**
   - Status: `400 Bad Request`
   - Message: "Game session not found: {sessionId}"

3. **Invalid Choice**
   - Status: `400 Bad Request`
   - Message: "Invalid choice" or "Choice not available"

4. **Game Ended**
   - Status: `400 Bad Request`
   - Message: "Game has ended"

---

## CORS Configuration

The API is configured to accept requests from:
- `http://localhost:5173` (Vite dev server)
- `http://localhost` (Production frontend)

To add additional origins, modify the `@CrossOrigin` annotation in `GameController.java`.

---

## Testing with cURL

### Complete Game Flow Example

```bash
# 1. Create a new game session
SESSION_RESPONSE=$(curl -s -X POST "http://localhost:8080/api/game/session/create?playerName=TestPlayer")
SESSION_ID=$(echo $SESSION_RESPONSE | jq -r '.sessionId')
echo "Session ID: $SESSION_ID"

# 2. Get the starting scene
curl "http://localhost:8080/api/game/scene/intro?sessionId=$SESSION_ID"

# 3. Make a choice (assuming choice ID 1)
curl -X POST "http://localhost:8080/api/game/session/$SESSION_ID/choice/1"

# 4. Continue making choices...
curl -X POST "http://localhost:8080/api/game/session/$SESSION_ID/choice/5"
```

### Using jq for Pretty Output

```bash
# Install jq if needed: brew install jq (macOS) or apt-get install jq (Linux)

# Get scene with formatted JSON
curl -s "http://localhost:8080/api/game/scene/intro" | jq

# Get story map with formatted JSON
curl -s "http://localhost:8080/api/game/story-map" | jq
```

---

## Rate Limiting

Currently, there is no rate limiting implemented. For production deployments, consider adding rate limiting to prevent abuse.

---

## Versioning

The API does not currently use versioning. Future versions may use URL versioning (e.g., `/api/v1/game/...`).

---

## Additional Resources

- Full documentation: `DOCUMENTATION.md`
- Game design: `sql/GAME_DOCUMENTATION.md`
- Docker setup: `DOCKER.md`

