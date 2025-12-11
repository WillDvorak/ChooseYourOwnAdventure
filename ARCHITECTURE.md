# System Architecture

High-level architecture of the Text Quest adventure game.

---

## Architecture Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                         CLIENT                              │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │           React Frontend (Vite)                      │   │
│  │  - StoryMap.jsx (SVG visualization)                 │   │
│  │  - Textbox.jsx (story text & choices)               │   │
│  │  - HealthBar.jsx (HP display)                       │   │
│  │  - InventoryBox.jsx (items)                         │   │
│  └─────────────────┬───────────────────────────────────┘   │
│                    │ HTTP Requests (REST API)              │
└────────────────────┼───────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│                      API LAYER                              │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │        Spring Boot REST API (Java 17)               │   │
│  │                                                     │   │
│  │  Controllers:                                       │   │
│  │  - GameController (game logic endpoints)           │   │
│  │                                                     │   │
│  │  Services:                                          │   │
│  │  - GameService (session management, choice logic)  │   │
│  │  - SceneService (scene retrieval)                  │   │
│  │  - ChoiceService (choice validation)               │   │
│  │                                                     │   │
│  │  Repositories:                                      │   │
│  │  - JPA/Hibernate (ORM)                             │   │
│  └─────────────────┬───────────────────────────────────┘   │
│                    │ JDBC Connection                       │
└────────────────────┼───────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│                    DATABASE LAYER                           │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │              MySQL 8.0 Database                      │   │
│  │                                                     │   │
│  │  Tables:                                            │   │
│  │  - scenes (game scenes/locations)                  │   │
│  │  - choices (player decisions)                      │   │
│  │  - game_sessions (player state)                    │   │
│  │  - items (inventory items)                         │   │
│  │                                                     │   │
│  │  Migrations: Flyway (V1__schema, V2__seed)         │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

---

## Technology Stack

### Frontend
- **React 19.1.1** - UI framework
- **Vite 7.1.7** - Build tool and dev server
- **React Bootstrap 2.10.10** - UI components
- **React Router 7.9.6** - Navigation
- **Vitest 4.0.4** - Testing framework

### Backend
- **Spring Boot 3.x** - Application framework
- **Java 17** - Programming language
- **Gradle 8.14.3** - Build tool
- **Spring Data JPA** - ORM layer
- **MySQL Connector** - Database driver

### Database
- **MySQL 8.0** - Relational database
- **Flyway** - Database migrations

### DevOps
- **Docker** - Containerization
- **Docker Compose** - Multi-container orchestration
- **Nginx** - Production web server (frontend)

---

## Data Flow

### Player Makes a Choice

1. **Frontend** → User clicks a choice button
2. **API Request** → `POST /api/game/session/{sessionId}/choice/{choiceId}`
3. **Backend Processing**:
   - Validates choice availability
   - Updates health (if choice affects HP)
   - Adds/removes flags (items, knowledge)
   - Determines next scene
   - Saves session state to database
4. **Database Update** → Session flags_json updated
5. **API Response** → Returns new scene data with updated health
6. **Frontend Update** → React re-renders components:
   - Textbox displays new scene
   - HealthBar updates color/width
   - StoryMap highlights new location
   - Inventory shows new items

---

## Component Interactions

```
AppLayout (Root)
│
├── DisplayBox
│   └── Shows scene title
│
├── InventoryBox
│   ├── Shows collected items
│   └── Opens InventoryModal on click
│
├── StoryMap
│   ├── Fetches /api/game/story-map
│   └── Renders SVG map with current location
│
└── Textbox
    ├── Fetches /api/game/scene/{code}
    ├── Displays scene text
    ├── Renders HealthBar
    └── Renders choice buttons
        └── Sends /api/game/session/{id}/choice/{id}
```

---

## State Management

### Frontend State (React)
- **sceneInfo** - Current scene data (code, title, body, choices)
- **inventory** - Array of item names
- **sessionId** - Game session identifier
- **health** - Current player health

State flows down via props, updates flow up via callbacks.

### Backend State (Database)
- **game_sessions** table stores:
  - `player_name` - Player identifier
  - `current_scene_code` - Current location
  - `flags_json` - JSON object with health, items, flags

---

## API Design Principles

1. **RESTful** - Resource-based URLs
2. **Stateless** - Session ID passed in requests
3. **JSON** - Standard response format
4. **CORS-enabled** - Frontend/backend separation
5. **Error handling** - Consistent error responses

---

## Deployment

### Development
```bash
# Frontend (Vite dev server)
npm run dev  # http://localhost:5173

# Backend (Spring Boot)
./gradlew bootRun  # http://localhost:8080

# Database
docker-compose up db
```

### Production (Docker)
```bash
docker-compose -f docker-compose-full.yml up --build
```

Services:
- **Frontend**: Nginx on port 80
- **Backend**: Spring Boot on port 8080
- **Database**: MySQL on port 3306

---

## Security Considerations

- No authentication currently (add JWT for production)
- CORS restricted to localhost
- SQL injection protected by JPA/Hibernate
- Input validation on all endpoints
- Health stored server-side (prevents cheating)

---

## Scalability

Current limitations:
- Single MySQL instance
- Session data in database (consider Redis for production)
- No load balancing

For production:
- Add Redis for session caching
- Use connection pooling
- Implement horizontal scaling with load balancer
- Separate read/write database replicas

---

## Why These Technologies?

| Technology | Reason |
|------------|--------|
| React | Component-based, popular, great ecosystem |
| Vite | Fast dev server, modern build tool |
| Spring Boot | Enterprise-grade, excellent documentation |
| MySQL | Relational data fits game structure well |
| Docker | Consistent environments, easy deployment |

