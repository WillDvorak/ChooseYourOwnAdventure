package com.textquest.api.controller;

import com.textquest.api.entity.Scene;
import com.textquest.api.entity.Choice;
import com.textquest.api.repository.SceneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
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
                Map<String, Object> flags = gameService.getPlayerFlags(sessionId);
                response.put("health", flags.getOrDefault("health", 100));
                response.put("maxHealth", flags.getOrDefault("maxHealth", 100));
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
            Map<String, Object> flags = gameService.getPlayerFlags(sessionId);
            
            Map<String, Object> response = new java.util.HashMap<>();
            response.put("code", newScene.getCode());
            response.put("title", newScene.getTitle());
            response.put("body", newScene.getBody());
            response.put("isTerminal", newScene.getIsTerminal());
            response.put("choices", formattedChoices);
            response.put("health", flags.getOrDefault("health", 100));
            response.put("maxHealth", flags.getOrDefault("maxHealth", 100));
            
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
    
    // ==================== SAVE SLOT ENDPOINTS ====================
    
    /**
     * Endpoint: POST /api/game/session/{sessionId}/save/{slot}
     * Save current game session to a specific save slot (1-10)
     */
    @PostMapping("/session/{sessionId}/save/{slot}")
    public ResponseEntity<?> saveToSlot(@PathVariable Long sessionId, 
                                        @PathVariable Integer slot,
                                        @RequestParam(required = false) String saveName,
                                        @RequestParam(defaultValue = "false") boolean isAutoSave) {
        try {
            GameSession saved = gameService.saveToSlot(sessionId, slot, saveName, isAutoSave);
            return ResponseEntity.ok(Map.of(
                "sessionId", saved.getId(),
                "saveSlot", saved.getSaveSlot(),
                "saveName", saved.getSaveName() != null ? saved.getSaveName() : "",
                "isAutoSave", saved.getIsAutoSave(),
                "currentScene", saved.getCurrentSceneCode(),
                "updatedAt", saved.getUpdatedAt().toString()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Endpoint: GET /api/game/player/{playerName}/saves
     * Get all save slots for a player
     */
    @GetMapping("/player/{playerName}/saves")
    public ResponseEntity<?> listSaveSlots(@PathVariable String playerName) {
        List<GameSession> saves = gameService.listSaveSlots(playerName);
        List<Map<String, Object>> formattedSaves = saves.stream()
            .map(save -> {
                Map<String, Object> saveMap = new java.util.HashMap<>();
                saveMap.put("sessionId", save.getId());
                saveMap.put("saveSlot", save.getSaveSlot());
                saveMap.put("saveName", save.getSaveName() != null ? save.getSaveName() : "");
                saveMap.put("isAutoSave", save.getIsAutoSave());
                saveMap.put("currentScene", save.getCurrentSceneCode());
                saveMap.put("createdAt", save.getCreatedAt().toString());
                saveMap.put("updatedAt", save.getUpdatedAt().toString());
                return saveMap;
            })
            .collect(Collectors.toList());
        return ResponseEntity.ok(Map.of("saves", formattedSaves, "count", formattedSaves.size()));
    }
    
    /**
     * Endpoint: GET /api/game/player/{playerName}/save/{slot}
     * Get information about a specific save slot
     */
    @GetMapping("/player/{playerName}/save/{slot}")
    public ResponseEntity<?> getSaveSlotInfo(@PathVariable String playerName, @PathVariable Integer slot) {
        try {
            GameSession save = gameService.getSaveSlotInfo(playerName, slot);
            return ResponseEntity.ok(Map.of(
                "sessionId", save.getId(),
                "saveSlot", save.getSaveSlot(),
                "saveName", save.getSaveName() != null ? save.getSaveName() : "",
                "isAutoSave", save.getIsAutoSave(),
                "playerName", save.getPlayerName(),
                "currentScene", save.getCurrentSceneCode(),
                "flags", gameService.getPlayerFlags(save.getId()),
                "createdAt", save.getCreatedAt().toString(),
                "updatedAt", save.getUpdatedAt().toString()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Endpoint: POST /api/game/player/{playerName}/save/{slot}/load
     * Load a save slot and create a new active session from it
     */
    @PostMapping("/player/{playerName}/save/{slot}/load")
    public ResponseEntity<?> loadFromSave(@PathVariable String playerName, @PathVariable Integer slot) {
        try {
            GameSession newSession = gameService.createSessionFromSave(playerName, slot);
            return ResponseEntity.ok(Map.of(
                "sessionId", newSession.getId(),
                "playerName", newSession.getPlayerName(),
                "currentScene", newSession.getCurrentSceneCode(),
                "flags", gameService.getPlayerFlags(newSession.getId()),
                "message", "Save loaded successfully"
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Endpoint: DELETE /api/game/player/{playerName}/save/{slot}
     * Delete a specific save slot
     */
    @DeleteMapping("/player/{playerName}/save/{slot}")
    public ResponseEntity<?> deleteSaveSlot(@PathVariable String playerName, @PathVariable Integer slot) {
        try {
            gameService.deleteSaveSlot(playerName, slot);
            return ResponseEntity.ok(Map.of("message", "Save slot " + slot + " deleted successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Endpoint: GET /api/game/player/{playerName}/active
     * Get the active (unsaved) session for a player
     */
    @GetMapping("/player/{playerName}/active")
    public ResponseEntity<?> getActiveSession(@PathVariable String playerName) {
        Optional<GameSession> activeSession = gameService.getActiveSession(playerName);
        if (activeSession.isPresent()) {
            GameSession session = activeSession.get();
            return ResponseEntity.ok(Map.of(
                "sessionId", session.getId(),
                "playerName", session.getPlayerName(),
                "currentScene", session.getCurrentSceneCode(),
                "flags", gameService.getPlayerFlags(session.getId()),
                "createdAt", session.getCreatedAt().toString(),
                "updatedAt", session.getUpdatedAt().toString()
            ));
        } else {
            return ResponseEntity.ok(Map.of("sessionId", null, "message", "No active session found"));
        }
    }
    
    /**
     * Endpoint: GET /api/game/player/{playerName}/saves/count
     * Get count of save slots for a player
     */
    @GetMapping("/player/{playerName}/saves/count")
    public ResponseEntity<?> getSaveSlotCount(@PathVariable String playerName) {
        long count = gameService.getSaveSlotCount(playerName);
        return ResponseEntity.ok(Map.of("count", count, "maxSlots", 10));
    }
}

