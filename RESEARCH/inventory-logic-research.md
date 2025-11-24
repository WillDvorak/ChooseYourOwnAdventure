# Research Report  
## Inventory Logic (Backend + Frontend Integration)

### Summary of Work
This research looks into different ways games handle inventory systems, especially how inventory data is stored, updated, and kept in sync between the backend and the frontend. I focused on common database structures, how real-time updates work, and how bigger game engines manage item states. The goal was to understand which approaches fit our project best.

### Motivation
Inventory tracking is a big part of TextQuest, and we need it to work reliably without causing bugs or inconsistent game states. Since our game relies on choices that can add or remove items, I wanted to make sure we understand the best ways to store inventory data and prevent conflicts. Doing this research helps us build a smoother player experience and avoid problems later in development.

### Time Spent
Reading and summarizing online sources ~ 45 minutes  
Looking at schema examples and engine docs ~ 40 minutes  
Watching tutorials on game state systems ~ 20 minutes  

### Results  
**Key Findings**

**Flag-Based vs Relational Inventory Schemas**  
I found that many story-focused games use simple flag-based systems where items are stored as booleans or small integers. This makes it really fast to check whether the player has something or not.  
More complex inventory systems use relational models (like an inventory table connected to an items table). This is more flexible but takes more effort to set up. For our project, a hybrid approach seems like a good balance.[^1]

**Real-Time Sync Between Frontend and Backend**  
A lot of sources say that keeping the frontend and backend aligned is important, especially in games where state changes fast. We don’t need full real-time WebSockets because our game is turn-based, but we should still call the backend after each update and refresh the local inventory so the UI never gets out of sync.[^2]

**Transactional Updates Prevent Conflicts**  
One major pattern that kept coming up was the idea of transactional or atomic updates. These prevent weird issues like duplicated items or lost progress. Using transactions makes sure that whenever an item is added or removed, the entire update is processed safely as one complete action.[^3]

**How Game Engines Handle Item State**  
Unity and Godot both treat inventory as a consistent “single source of truth,” usually stored in a structured format (like JSON). They recommend updating inventory through controlled functions instead of changing variables all over the place. This helps prevent bugs and keeps the game state predictable.[^4]

### Practical Recommendations for the Team
- Use a hybrid schema: relational tables for item definitions + simple flags for ownership.  
- Make inventory updates atomic so we don’t get inconsistent player data.  
- Refresh the inventory on the frontend after every update request.  
- Store player state in an organized format like JSON since it works well with autosaving.  
- Follow game engine practices by routing all inventory changes through controlled backend functions.

### Sources  
[^1]: https://stackoverflow.com/questions/71899672/inventory-on-text-based-game  
[^2]: https://www.cis.upenn.edu/~ccb/publications/masters-theses/Anna-Orosz-masters-thesis-2021.pdf  
[^3]: https://www.mongodb.com/developer/products/mongodb/transactions-explained/  
[^4]: https://www.gamedev.net/forums/topic/674979-text-based-game-help-with-inventory/  
