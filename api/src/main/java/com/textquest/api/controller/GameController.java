package com.textquest.api.controller;

import com.textquest.api.entity.Scene;
import com.textquest.api.entity.Choice;
import com.textquest.api.entity.GameSession;
import com.textquest.api.entity.Item;
import com.textquest.api.entity.PlayerInventory;
import com.textquest.api.repository.SceneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/game")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost"})
public class GameController {
    
    @Autowired
    private SceneRepository sceneRepository;
    
    @Autowired
    private com.textquest.api.service.GameService gameService;
    
    /**
     * Endpoint: GET /api/game/scene/{sceneCode}
     * Get a scene by its code with all available choices
     */
    @GetMapping("/scene/{sceneCode}")
    public ResponseEntity<?> getScene(@PathVariable String sceneCode,
                                       @RequestParam(required = false) Long sessionId) {
        Scene scene = sceneRepository.findByCode(sceneCode)
            .orElseThrow(() -> new RuntimeException("Scene not found: " + sceneCode));
        
        // Get choices for this scene
        List<Choice> choices = scene.getChoices();
        
        // Format choices for frontend
        List<Map<String, Object>> formattedChoices = choices.stream()
            .map(choice -> {
                Map<String, Object> choiceMap = new java.util.HashMap<>();
                choiceMap.put("id", choice.getId());
                choiceMap.put("label", choice.getLabel());
                choiceMap.put("targetSceneCode", choice.getTargetSceneCode());
                choiceMap.put("requiresFlag", choice.getRequiresFlag() != null ? choice.getRequiresFlag() : "");
                choiceMap.put("setsFlag", choice.getSetsFlag() != null ? choice.getSetsFlag() : "");
                return choiceMap;
            })
            .collect(Collectors.toList());
        
        Map<String, Object> response = new java.util.HashMap<>();
        response.put("code", scene.getCode());
        response.put("title", scene.getTitle());
        response.put("body", scene.getBody());
        response.put("isTerminal", scene.getIsTerminal());
        response.put("choices", formattedChoices);
        
        // Add health info if sessionId provided
        if (sessionId != null) {
            try {
                GameSession session = gameService.getGameSession(sessionId);
                response.put("health", session.getHp());
                response.put("maxHealth", session.getMaxHp());
            } catch (Exception e) {
                // Default if session not found
                response.put("health", 100);
                response.put("maxHealth", 100);
            }
        } else {
            // Default health values when no session
            response.put("health", 100);
            response.put("maxHealth", 100);
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Endpoint: POST /api/game/session/{sessionId}/choice/{choiceId}
     * Process a player's choice and advance the game
     */
    @PostMapping("/session/{sessionId}/choice/{choiceId}")
    public ResponseEntity<?> makeChoice(@PathVariable Long sessionId, @PathVariable Long choiceId) {
        try {
            // Process the choice through GameService (updates health, flags, etc.)
            com.textquest.api.entity.GameSession updatedSession = gameService.makeChoice(sessionId, choiceId);
            
            // Get the updated scene with health data
            Scene newScene = sceneRepository.findByCode(updatedSession.getCurrentSceneCode())
                .orElseThrow(() -> new RuntimeException("Scene not found"));
            
            // Get choices for the new scene
            List<Choice> choices = newScene.getChoices();
            List<Map<String, Object>> formattedChoices = choices.stream()
                .map(choice -> {
                    Map<String, Object> choiceMap = new java.util.HashMap<>();
                    choiceMap.put("id", choice.getId());
                    choiceMap.put("label", choice.getLabel());
                    choiceMap.put("targetSceneCode", choice.getTargetSceneCode());
                    choiceMap.put("requiresFlag", choice.getRequiresFlag() != null ? choice.getRequiresFlag() : "");
                    choiceMap.put("setsFlag", choice.getSetsFlag() != null ? choice.getSetsFlag() : "");
                    return choiceMap;
                })
                .collect(Collectors.toList());
            
            // Get updated health from session
            GameSession updatedSession = gameService.getGameSession(sessionId);
            
            Map<String, Object> response = new java.util.HashMap<>();
            response.put("code", newScene.getCode());
            response.put("title", newScene.getTitle());
            response.put("body", newScene.getBody());
            response.put("isTerminal", newScene.getIsTerminal());
            response.put("choices", formattedChoices);
            response.put("health", updatedSession.getHp());
            response.put("maxHealth", updatedSession.getMaxHp());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Endpoint: POST /api/game/session/create
     * Create a new game session
     */
    @PostMapping("/session/create")
    public ResponseEntity<?> createSession(@RequestParam(defaultValue = "Player") String playerName,
                                            @RequestParam(defaultValue = "intro") String startingScene) {
        try {
            com.textquest.api.entity.GameSession session = gameService.createGameSession(playerName, startingScene);
            return ResponseEntity.ok(Map.of(
                "sessionId", session.getId(),
                "playerName", session.getPlayerName(),
                "currentScene", session.getCurrentSceneCode()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Endpoint: GET /api/game/story-map
     * Get the complete story structure for visualization
     */
    @GetMapping("/story-map")
    public ResponseEntity<?> getStoryMap() {
        List<Scene> allScenes = sceneRepository.findAll();
        
        // Build nodes
        List<Map<String, Object>> nodes = allScenes.stream()
            .map(scene -> {
                Map<String, Object> node = new java.util.HashMap<>();
                node.put("id", scene.getCode());
                node.put("label", scene.getTitle());
                node.put("isTerminal", scene.getIsTerminal());
                return node;
            })
            .collect(Collectors.toList());
        
        // Build edges
        List<Map<String, Object>> edges = new java.util.ArrayList<>();
        for (Scene scene : allScenes) {
            for (Choice choice : scene.getChoices()) {
                Map<String, Object> edge = new java.util.HashMap<>();
                edge.put("from", scene.getCode());
                edge.put("to", choice.getTargetSceneCode());
                edge.put("label", choice.getLabel());
                edge.put("requiresFlag", choice.getRequiresFlag() != null ? choice.getRequiresFlag() : "");
                edge.put("setsFlag", choice.getSetsFlag() != null ? choice.getSetsFlag() : "");
                edges.add(edge);
            }
        }
        
        return ResponseEntity.ok(Map.of("nodes", nodes, "edges", edges));
    }
    
    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Game API is running!");
    }
    
    // ==================== HP ENDPOINTS ====================
    
    @GetMapping("/session/{sessionId}")
    public ResponseEntity<?> getGameSession(@PathVariable Long sessionId) {
        GameSession session = gameService.getGameSession(sessionId);
        return ResponseEntity.ok(Map.of(
            "id", session.getId(),
            "playerName", session.getPlayerName(),
            "currentSceneCode", session.getCurrentSceneCode(),
            "hp", session.getHp(),
            "maxHp", session.getMaxHp(),
            "flags", gameService.getPlayerFlags(sessionId),
            "isGameEnded", gameService.isGameEnded(sessionId),
            "isPlayerDead", gameService.isPlayerDead(sessionId)
        ));
    }
    
    @PatchMapping("/session/{sessionId}/hp")
    public ResponseEntity<?> modifyHp(@PathVariable Long sessionId, @RequestBody Map<String, Integer> request) {
        Integer hpChange = request.get("hpChange");
        if (hpChange == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "hpChange is required"));
        }
        GameSession session = gameService.modifyHp(sessionId, hpChange);
        return ResponseEntity.ok(Map.of(
            "id", session.getId(),
            "hp", session.getHp(),
            "maxHp", session.getMaxHp(),
            "isPlayerDead", gameService.isPlayerDead(sessionId)
        ));
    }
    
    @PutMapping("/session/{sessionId}/hp")
    public ResponseEntity<?> setHp(@PathVariable Long sessionId, @RequestBody Map<String, Integer> request) {
        Integer hp = request.get("hp");
        if (hp == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "hp is required"));
        }
        GameSession session = gameService.setHp(sessionId, hp);
        return ResponseEntity.ok(Map.of(
            "id", session.getId(),
            "hp", session.getHp(),
            "maxHp", session.getMaxHp(),
            "isPlayerDead", gameService.isPlayerDead(sessionId)
        ));
    }
    
    @PutMapping("/session/{sessionId}/maxHp")
    public ResponseEntity<?> setMaxHp(@PathVariable Long sessionId, @RequestBody Map<String, Object> request) {
        Object maxHpObj = request.get("maxHp");
        if (maxHpObj == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "maxHp is required"));
        }
        int maxHp = maxHpObj instanceof Integer ? (Integer) maxHpObj : Integer.parseInt(maxHpObj.toString());
        boolean adjustCurrent = request.containsKey("adjustCurrent") && Boolean.TRUE.equals(request.get("adjustCurrent"));
        GameSession session = gameService.setMaxHp(sessionId, maxHp, adjustCurrent);
        return ResponseEntity.ok(Map.of(
            "id", session.getId(),
            "hp", session.getHp(),
            "maxHp", session.getMaxHp()
        ));
    }
    
    // ==================== INVENTORY ENDPOINTS ====================
    
    @GetMapping("/session/{sessionId}/inventory")
    public ResponseEntity<?> getInventory(@PathVariable Long sessionId) {
        List<PlayerInventory> inventory = gameService.getPlayerInventory(sessionId);
        List<Map<String, Object>> formattedInventory = inventory.stream()
            .map(inv -> {
                Map<String, Object> itemMap = new java.util.HashMap<>();
                itemMap.put("id", inv.getId());
                itemMap.put("itemId", inv.getItem().getId());
                itemMap.put("itemCode", inv.getItem().getCode());
                itemMap.put("itemName", inv.getItem().getName());
                itemMap.put("itemDescription", inv.getItem().getDescription());
                itemMap.put("itemType", inv.getItem().getItemType());
                itemMap.put("isConsumable", inv.getItem().getIsConsumable());
                itemMap.put("quantity", inv.getQuantity());
                return itemMap;
            })
            .collect(Collectors.toList());
        return ResponseEntity.ok(Map.of("inventory", formattedInventory));
    }
    
    @PostMapping("/session/{sessionId}/inventory")
    public ResponseEntity<?> addItem(@PathVariable Long sessionId, @RequestBody Map<String, Object> request) {
        Integer quantity = request.get("quantity") != null ? 
            (request.get("quantity") instanceof Integer ? (Integer) request.get("quantity") : 
             Integer.parseInt(request.get("quantity").toString())) : 1;
        PlayerInventory inventory;
        if (request.containsKey("itemId")) {
            Long itemId = request.get("itemId") instanceof Long ? (Long) request.get("itemId") : 
                         Long.parseLong(request.get("itemId").toString());
            inventory = gameService.addItem(sessionId, itemId, quantity);
        } else if (request.containsKey("itemCode")) {
            inventory = gameService.addItemByCode(sessionId, request.get("itemCode").toString(), quantity);
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", "itemId or itemCode is required"));
        }
        return ResponseEntity.ok(Map.of(
            "id", inventory.getId(),
            "itemId", inventory.getItem().getId(),
            "itemCode", inventory.getItem().getCode(),
            "itemName", inventory.getItem().getName(),
            "quantity", inventory.getQuantity()
        ));
    }
    
    @DeleteMapping("/session/{sessionId}/inventory/{itemId}")
    public ResponseEntity<?> removeItem(@PathVariable Long sessionId, @PathVariable Long itemId, 
                                       @RequestParam(required = false) Integer quantity) {
        gameService.removeItem(sessionId, itemId, quantity);
        return ResponseEntity.ok(Map.of("message", "Item removed from inventory"));
    }
    
    @DeleteMapping("/session/{sessionId}/inventory/code/{itemCode}")
    public ResponseEntity<?> removeItemByCode(@PathVariable Long sessionId, @PathVariable String itemCode,
                                             @RequestParam(required = false) Integer quantity) {
        gameService.removeItemByCode(sessionId, itemCode, quantity);
        return ResponseEntity.ok(Map.of("message", "Item removed from inventory"));
    }
    
    @PutMapping("/session/{sessionId}/inventory/{itemId}")
    public ResponseEntity<?> setItemQuantity(@PathVariable Long sessionId, @PathVariable Long itemId,
                                             @RequestBody Map<String, Integer> request) {
        Integer quantity = request.get("quantity");
        if (quantity == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "quantity is required"));
        }
        PlayerInventory inventory = gameService.setItemQuantity(sessionId, itemId, quantity);
        return ResponseEntity.ok(Map.of(
            "id", inventory.getId(),
            "itemId", inventory.getItem().getId(),
            "itemCode", inventory.getItem().getCode(),
            "itemName", inventory.getItem().getName(),
            "quantity", inventory.getQuantity()
        ));
    }
    
    @PostMapping("/session/{sessionId}/inventory/{itemId}/use")
    public ResponseEntity<?> useItem(@PathVariable Long sessionId, @PathVariable Long itemId) {
        Map<String, Object> result = gameService.useItem(sessionId, itemId);
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/session/{sessionId}/inventory/code/{itemCode}/use")
    public ResponseEntity<?> useItemByCode(@PathVariable Long sessionId, @PathVariable String itemCode) {
        Map<String, Object> result = gameService.useItemByCode(sessionId, itemCode);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/items")
    public ResponseEntity<?> getAllItems() {
        List<Item> items = gameService.getAllItems();
        List<Map<String, Object>> formattedItems = items.stream()
            .map(item -> {
                Map<String, Object> itemMap = new java.util.HashMap<>();
                itemMap.put("id", item.getId());
                itemMap.put("code", item.getCode());
                itemMap.put("name", item.getName());
                itemMap.put("description", item.getDescription());
                itemMap.put("itemType", item.getItemType());
                itemMap.put("isConsumable", item.getIsConsumable());
                itemMap.put("effectsJson", item.getEffectsJson());
                return itemMap;
            })
            .collect(Collectors.toList());
        return ResponseEntity.ok(Map.of("items", formattedItems));
    }
    
    @GetMapping("/items/{itemCode}")
    public ResponseEntity<?> getItemByCode(@PathVariable String itemCode) {
        Item item = gameService.getItemByCode(itemCode);
        return ResponseEntity.ok(Map.of(
            "id", item.getId(),
            "code", item.getCode(),
            "name", item.getName(),
            "description", item.getDescription(),
            "itemType", item.getItemType(),
            "isConsumable", item.getIsConsumable(),
            "effectsJson", item.getEffectsJson()
        ));
    }
    
    @GetMapping("/session/{sessionId}/inventory/has/{itemCode}")
    public ResponseEntity<?> hasItem(@PathVariable Long sessionId, @PathVariable String itemCode) {
        boolean hasItem = gameService.hasItemByCode(sessionId, itemCode);
        int quantity = gameService.getItemQuantityByCode(sessionId, itemCode);
        return ResponseEntity.ok(Map.of(
            "hasItem", hasItem,
            "quantity", quantity
        ));
    }
}

