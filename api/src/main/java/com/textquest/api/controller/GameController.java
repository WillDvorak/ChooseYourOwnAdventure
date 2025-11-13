package com.textquest.api.controller;

import com.textquest.api.entity.Scene;
import com.textquest.api.entity.Choice;
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
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Game API is running!");
    }
}

