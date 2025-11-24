package com.textquest.api.controller;

import com.textquest.api.entity.Scene;
import com.textquest.api.entity.Choice;
import com.textquest.api.entity.GameSession;
import com.textquest.api.repository.SceneRepository;
import com.textquest.api.service.GameService;
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
    private GameService gameService;
    
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
    
    /**
     * Endpoint: GET /api/game/session/{sessionId}
     * Get a game session with HP information
     */
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
    
    /**
     * Endpoint: POST /api/game/session
     * Create a new game session
     */
    @PostMapping("/session")
    public ResponseEntity<?> createGameSession(@RequestBody Map<String, String> request) {
        String playerName = request.get("playerName");
        String startingSceneCode = request.getOrDefault("startingSceneCode", "intro");
        
        GameSession session = gameService.createGameSession(playerName, startingSceneCode);
        return ResponseEntity.ok(Map.of(
            "id", session.getId(),
            "playerName", session.getPlayerName(),
            "currentSceneCode", session.getCurrentSceneCode(),
            "hp", session.getHp(),
            "maxHp", session.getMaxHp(),
            "flags", gameService.getPlayerFlags(session.getId())
        ));
    }
    
    /**
     * Endpoint: PATCH /api/game/session/{sessionId}/hp
     * Modify HP (damage or heal)
     * Body: { "hpChange": -10 } for damage, { "hpChange": 20 } for healing
     */
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
    
    /**
     * Endpoint: PUT /api/game/session/{sessionId}/hp
     * Set HP to a specific value
     * Body: { "hp": 50 }
     */
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
    
    /**
     * Endpoint: PUT /api/game/session/{sessionId}/maxHp
     * Set max HP
     * Body: { "maxHp": 150, "adjustCurrent": true }
     */
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
}

