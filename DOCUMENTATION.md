# TextQuest - Echoes of the Shattered Realms

## Table of Contents

1. [Project Overview](#project-overview)
2. [Architecture](#architecture)
3. [Technology Stack](#technology-stack)
4. [Project Structure](#project-structure)
5. [Getting Started](#getting-started)
6. [API Documentation](#api-documentation)
7. [Frontend Documentation](#frontend-documentation)
8. [Database Schema](#database-schema)
9. [Game Mechanics](#game-mechanics)
10. [Development Guide](#development-guide)
11. [Testing](#testing)
12. [Deployment](#deployment)
13. [Troubleshooting](#troubleshooting)

---

## Project Overview

**TextQuest** is an interactive text-based choose-your-own-adventure game called "Echoes of the Shattered Realms". Players navigate through a fractured world of five realms, making choices that affect their journey, health, inventory, and ultimately, the game's ending.

### Key Features

- **Interactive Storytelling**: Branching narrative with multiple paths and endings
- **Health System**: Players have 100 HP that can be lost or restored through choices
- **Inventory System**: Collect items (torch, key, amulet, sword, etc.) that unlock new paths
- **Multiple Endings**: Different endings based on player choices and collected items
- **Story Map Visualization**: Visual representation of the game's story structure
- **Session Management**: Save and resume game sessions

### Game Story

The game follows a fractured world of five realms:
- **The Mistwood Forest** - Ancient forest with whispering spirits
- **The Ember City** - Molten metropolis built around a volcano
- **The Crystal Archives** - Library of living crystals storing knowledge
- **The Drowned Sanctum** - Sunken temple city beneath the ocean
- **The Astral Gate** - Nexus of fate where all choices converge

Players must navigate through these realms, making choices that determine their fate and the world's future.

---

## Architecture

### System Architecture

```
┌─────────────────┐
│   React Frontend │  (Port 80/5173)
│   (Vite + React) │
└────────┬────────┘
         │ HTTP/REST
         │
┌────────▼────────┐
│  Spring Boot API │  (Port 8080)
│   (Java 17)     │
└────────┬────────┘
         │ JPA/Hibernate
         │
┌────────▼────────┐
│   MySQL Database │  (Port 3306)
│   (Docker)       │
└─────────────────┘
```

### Component Overview

1. **Frontend (React)**
   - React components for UI
   - React Router for navigation
   - Bootstrap for styling
   - Vite for build tooling

2. **Backend (Spring Boot)**
   - RESTful API endpoints
   - JPA/Hibernate for database access
   - Flyway for database migrations
   - Service layer for business logic

3. **Database (MySQL)**
   - Stores scenes, choices, game sessions, and items
   - Managed via Flyway migrations

---

## Technology Stack

### Frontend
- **React** 19.1.1 - UI framework
- **React Router** 7.9.6 - Client-side routing
- **Bootstrap** 5.3.8 - CSS framework
- **React Bootstrap** 2.10.10 - Bootstrap components for React
- **Vite** 7.1.7 - Build tool and dev server
- **Vitest** 4.0.4 - Testing framework

### Backend
- **Java** 17 - Programming language
- **Spring Boot** 3.5.6 - Application framework
- **Spring Data JPA** - Database access
- **Hibernate** - ORM
- **Flyway** - Database migration tool
- **MySQL Connector** - Database driver

### Infrastructure
- **Docker** - Containerization
- **Docker Compose** - Multi-container orchestration
- **Nginx** - Web server for frontend
- **MySQL** 8.0 - Relational database

---

## Project Structure

```
Project_10/
├── api/                          # Spring Boot backend
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/textquest/api/
│   │   │   │   ├── controller/   # REST controllers
│   │   │   │   ├── service/      # Business logic
│   │   │   │   ├── repository/   # Data access
│   │   │   │   ├── entity/       # JPA entities
│   │   │   │   └── exception/    # Custom exceptions
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   └── test/                 # Unit and integration tests
│   ├── build.gradle              # Gradle build configuration
│   └── Dockerfile                # Backend container definition
│
├── ReactFrontend/
│   └── ChooseYourOwnAdventure/   # React frontend
│       ├── src/
│       │   ├── components/       # React components
│       │   │   ├── content/     # Game content components
│       │   │   └── structural/   # Layout components
│       │   ├── hooks/            # Custom React hooks
│       │   └── main.jsx          # Entry point
│       ├── public/               # Static assets
│       ├── package.json
│       └── Dockerfile            # Frontend container definition
│
├── sql/                          # Database scripts
│   ├── V1__schema.sql            # Database schema
│   ├── V2__seed.sql              # Initial data
│   └── GAME_DOCUMENTATION.md     # Game design docs
│
├── docker-compose-full.yml       # Docker Compose configuration
├── Dockerfile.db                 # Database container definition
├── README.md                     # Project overview
├── DOCKER.md                     # Docker setup guide
├── STYLE.md                      # Coding standards
└── story.txt                     # Game story design
```

---

## Getting Started

### Prerequisites

- **Docker** and **Docker Compose** installed
- **Java 17+** (for local development)
- **Node.js 18+** (for local frontend development)
- **MySQL** (optional, if not using Docker)

### Quick Start with Docker

1. **Clone the repository** (if applicable) or navigate to the project directory:
   ```bash
   cd Project_10
   ```

2. **Start all services**:
   ```bash
   docker-compose -f docker-compose-full.yml up --build
   ```

3. **Access the application**:
   - Frontend: http://localhost
   - API: http://localhost:8080
   - API Health Check: http://localhost:8080/api/game/health

### Local Development Setup

#### Backend Setup

1. **Navigate to the API directory**:
   ```bash
   cd api
   ```

2. **Build the project**:
   ```bash
   ./gradlew build
   ```

3. **Run the application**:
   ```bash
   ./gradlew bootRun
   ```

4. **Run tests**:
   ```bash
   ./gradlew test
   ```

#### Frontend Setup

1. **Navigate to the frontend directory**:
   ```bash
   cd ReactFrontend/ChooseYourOwnAdventure
   ```

2. **Install dependencies**:
   ```bash
   npm install
   ```

3. **Start the development server**:
   ```bash
   npm run dev
   ```

4. **Run tests**:
   ```bash
   npm test
   ```

### Database Setup

The database is automatically set up via Flyway migrations when the Spring Boot application starts. The migrations are located in:
- `sql/V1__schema.sql` - Creates tables
- `sql/V2__seed.sql` - Seeds initial game data

---

## API Documentation

### Base URL

- **Local**: `http://localhost:8080/api/game`
- **Docker**: `http://localhost:8080/api/game`

### Endpoints

#### 1. Get Scene

Retrieve a scene by its code with all available choices.

**Endpoint**: `GET /api/game/scene/{sceneCode}`

**Parameters**:
- `sceneCode` (path) - The code of the scene to retrieve (e.g., "intro", "forest")
- `sessionId` (query, optional) - Game session ID to include health information

**Response**:
```json
{
  "code": "intro",
  "title": "The Long One",
  "body": "You wake at a campfire...",
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

**Example**:
```bash
curl http://localhost:8080/api/game/scene/intro
curl http://localhost:8080/api/game/scene/intro?sessionId=1
```

#### 2. Create Game Session

Create a new game session for a player.

**Endpoint**: `POST /api/game/session/create`

**Parameters**:
- `playerName` (query, optional) - Player's name (default: "Player")
- `startingScene` (query, optional) - Starting scene code (default: "intro")

**Response**:
```json
{
  "sessionId": 1,
  "playerName": "Player",
  "currentScene": "intro"
}
```

**Example**:
```bash
curl -X POST "http://localhost:8080/api/game/session/create?playerName=Alice&startingScene=intro"
```

#### 3. Make Choice

Process a player's choice and advance the game.

**Endpoint**: `POST /api/game/session/{sessionId}/choice/{choiceId}`

**Parameters**:
- `sessionId` (path) - Game session ID
- `choiceId` (path) - Choice ID to execute

**Response**:
```json
{
  "code": "forest",
  "title": "Under Tall Pines",
  "body": "The forest hums with life...",
  "isTerminal": false,
  "health": 100,
  "maxHealth": 100,
  "choices": [...]
}
```

**Example**:
```bash
curl -X POST http://localhost:8080/api/game/session/1/choice/1
```

#### 4. Get Story Map

Retrieve the complete story structure for visualization.

**Endpoint**: `GET /api/game/story-map`

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

**Example**:
```bash
curl http://localhost:8080/api/game/story-map
```

#### 5. Health Check

Check if the API is running.

**Endpoint**: `GET /api/game/health`

**Response**:
```
Game API is running!
```

### Error Responses

All endpoints may return error responses in the following format:

```json
{
  "error": "Error message description"
}
```

Common HTTP status codes:
- `200 OK` - Success
- `400 Bad Request` - Invalid request parameters
- `404 Not Found` - Resource not found
- `500 Internal Server Error` - Server error

---

## Frontend Documentation

### Component Structure

#### Content Components

- **`DisplayBox.jsx`** - Main display area for scene content
- **`Textbox.jsx`** - Text display component
- **`HealthBar.jsx`** - Health indicator component
- **`InventoryBox.jsx`** - Inventory display component
- **`InventoryModal.jsx`** - Modal for viewing inventory
- **`StoryMap.jsx`** - Visual story map component

#### Structural Components

- **`AppLayout.jsx`** - Main application layout
- **`AdventurePage.jsx`** - Main game page
- **`AboutUsPage.jsx`** - About page

### Custom Hooks

- **`useStorage.js`** - Local storage management hook

### API Integration

The frontend communicates with the backend API using `fetch`:

```javascript
// Example: Fetching a scene
const response = await fetch(`http://localhost:8080/api/game/scene/${sceneCode}?sessionId=${sessionId}`);
const data = await response.json();
```

### Environment Configuration

The frontend is configured to connect to:
- **Development**: `http://localhost:8080` (via proxy or direct)
- **Production**: Configured via nginx reverse proxy

---

## Database Schema

### Tables

#### `scenes`

Stores all game scenes (story nodes).

| Column | Type | Description |
|--------|------|-------------|
| `id` | BIGINT UNSIGNED | Primary key |
| `code` | VARCHAR(64) | Unique scene identifier |
| `title` | VARCHAR(128) | Scene title |
| `body` | TEXT | Scene description |
| `is_terminal` | TINYINT(1) | Whether this is an ending scene |
| `created_at` | TIMESTAMP | Creation timestamp |
| `updated_at` | TIMESTAMP | Last update timestamp |

#### `choices`

Stores choices (edges between scenes).

| Column | Type | Description |
|--------|------|-------------|
| `id` | BIGINT UNSIGNED | Primary key |
| `scene_id` | BIGINT UNSIGNED | Foreign key to scenes |
| `label` | VARCHAR(255) | Choice text shown to player |
| `target_scene_code` | VARCHAR(64) | Destination scene code |
| `requires_flag` | VARCHAR(64) | Required flag to show choice (nullable) |
| `sets_flag` | VARCHAR(64) | Flag/item granted by choice (nullable) |
| `created_at` | TIMESTAMP | Creation timestamp |
| `updated_at` | TIMESTAMP | Last update timestamp |

#### `game_sessions`

Stores player game sessions.

| Column | Type | Description |
|--------|------|-------------|
| `id` | BIGINT UNSIGNED | Primary key |
| `player_name` | VARCHAR(64) | Player's name |
| `current_scene_code` | VARCHAR(64) | Current scene code |
| `flags_json` | JSON | Player flags, inventory, health |
| `created_at` | TIMESTAMP | Creation timestamp |
| `updated_at` | TIMESTAMP | Last update timestamp |

#### `items`

Stores game inventory items.

| Column | Type | Description |
|--------|------|-------------|
| `id` | BIGINT UNSIGNED | Primary key |
| `label` | VARCHAR(50) | Unique item identifier |
| `title` | VARCHAR(100) | Item display name |
| `description` | TEXT | Item description |

### Relationships

- `choices.scene_id` → `scenes.id` (Many-to-One)
- `game_sessions.current_scene_code` → `scenes.code` (Many-to-One)

---

## Game Mechanics

### Health System

- **Starting Health**: 100 HP
- **Health Storage**: Stored in `game_sessions.flags_json` as `{"health": 100, "maxHealth": 100}`
- **Death Condition**: When health ≤ 0, player is redirected to `death` scene
- **Health Modifiers**: Choices can modify health using format `health:+25`, `health:-30`, or `health:100` (full heal)

### Flag/Item System

Flags represent player state, inventory items, and game progress:

- **`torch`** - Required to descend into cave
- **`key`** - Opens temple door
- **`amulet`** - Opens temple door
- **`sword`** - Reduces combat damage, can force temple door
- **`map`** - Knowledge item
- **`knowledge`** - Opens temple door, unlocks ending2
- **`potion`** - Healing item
- **`gold`** - Currency for negotiation
- **`door_open`** - Indicates temple door is unlocked

### Choice Mechanics

- **`requires_flag`**: Choice is only available if player has this flag
- **`sets_flag`**: Choosing this option grants the player this flag
- Flags are stored in `game_sessions.flags_json` as JSON

### Game Flow

1. Player starts at `intro` scene
2. Player makes choices that navigate between scenes
3. Choices may require flags (items) or set flags (grant items)
4. Health is tracked and modified by choices
5. Game ends when player reaches a terminal scene (`is_terminal = true`)

### Endings

- **`ending1` - The Hero's Return**: Good ending, requires amulet/key/sword/knowledge
- **`ending2` - The Power Within**: Alternative ending, requires knowledge
- **`death` - Game Over**: Triggered when health reaches 0

For detailed game flow documentation, see `sql/GAME_DOCUMENTATION.md`.

---

## Development Guide

### Backend Development

#### Adding a New Scene

1. Add scene to database via Flyway migration or directly:
   ```sql
   INSERT INTO scenes (code, title, body, is_terminal) 
   VALUES ('new_scene', 'New Scene Title', 'Scene description', 0);
   ```

2. Add choices for the scene:
   ```sql
   INSERT INTO choices (scene_id, label, target_scene_code, requires_flag, sets_flag)
   VALUES ((SELECT id FROM scenes WHERE code = 'new_scene'), 
           'Choice text', 'target_scene', NULL, NULL);
   ```

#### Adding a New API Endpoint

1. Add method to `GameController.java`:
   ```java
   @GetMapping("/new-endpoint")
   public ResponseEntity<?> newEndpoint() {
       // Implementation
   }
   ```

2. Add corresponding service method if needed

#### Running Tests

```bash
cd api
./gradlew test
```

### Frontend Development

#### Adding a New Component

1. Create component file in `src/components/`
2. Import and use in parent component
3. Add tests in `src/test/`

#### Running Tests

```bash
cd ReactFrontend/ChooseYourOwnAdventure
npm test
```

### Code Style

See `STYLE.md` for coding standards and conventions.

---

## Testing

### Backend Tests

Located in `api/src/test/java/`:

- **Unit Tests**: Test individual components
- **Integration Tests**: Test API endpoints
- **Service Tests**: Test business logic

Run all tests:
```bash
cd api
./gradlew test
```

### Frontend Tests

Located in `ReactFrontend/ChooseYourOwnAdventure/src/test/`:

- **Component Tests**: Test React components
- Uses Vitest and React Testing Library

Run all tests:
```bash
cd ReactFrontend/ChooseYourOwnAdventure
npm test
```

### Test Coverage

View test coverage reports:
- Backend: `api/build/reports/tests/test/index.html`
- Frontend: Run `npm run test:coverage`

---

## Deployment

### Docker Deployment

The project is containerized using Docker Compose. See `DOCKER.md` for detailed deployment instructions.

### Production Considerations

1. **Environment Variables**: Configure database credentials and API URLs
2. **Security**: Use environment variables for sensitive data
3. **SSL/TLS**: Configure HTTPS for production
4. **Database Backups**: Implement regular backup strategy
5. **Monitoring**: Set up logging and monitoring

### Build for Production

#### Backend

```bash
cd api
./gradlew build
```

#### Frontend

```bash
cd ReactFrontend/ChooseYourOwnAdventure
npm run build
```

The production build will be in `dist/` directory.

---

## Troubleshooting

### Common Issues

#### Database Connection Issues

**Problem**: API cannot connect to database

**Solutions**:
1. Check database is running: `docker ps`
2. Verify database credentials in `application.properties`
3. Check network connectivity between containers

#### Port Conflicts

**Problem**: Port already in use

**Solutions**:
1. Check what's using the port: `lsof -i :8080`
2. Change port in `docker-compose-full.yml`
3. Stop conflicting services

#### Frontend Cannot Reach Backend

**Problem**: "Could not connect to the realm" error

**Solutions**:
1. Verify API is running: `curl http://localhost:8080/api/game/health`
2. Check CORS configuration in `GameController.java`
3. Verify nginx proxy configuration

#### Database Migration Issues

**Problem**: Flyway migration fails

**Solutions**:
1. Check database connection
2. Verify migration files are in correct location
3. Check migration file naming (V1__, V2__, etc.)
4. Review migration SQL syntax

### Getting Help

- Check logs: `docker-compose -f docker-compose-full.yml logs`
- Review `DOCKER.md` for Docker-specific issues
- Check `sql/GAME_DOCUMENTATION.md` for game design questions

---

## Additional Resources

- **Game Design**: `story.txt` and `sql/GAME_DOCUMENTATION.md`
- **Docker Setup**: `DOCKER.md`
- **Coding Standards**: `STYLE.md`
- **Project Specification**: `README.md`

---

## License

This project is part of CS506 coursework at the University of Wisconsin-Madison.

---

## Team

**Controlled Chaos**

For team roles and responsibilities, see `roles.md`.

