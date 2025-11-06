package com.textquest.api.controller;

import com.textquest.api.entity.Scene;
import com.textquest.api.entity.Choice;
import com.textquest.api.entity.GameSession;
import com.textquest.api.repository.SceneRepository;
import com.textquest.api.repository.GameSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/game")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost"})
public class GameController {
    
    @Autowired
    private SceneRepository sceneRepository;
    
    @Autowired
    private GameSessionRepository gameSessionRepository;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * Endpoint: GET /api/game/scene/{sceneCode}
     * Get a scene by its code with all available choices
     */
    @GetMapping("/scene/{sceneCode}")
    public ResponseEntity<?> getScene(@PathVariable String sceneCode) {
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
        
        return ResponseEntity.ok(Map.of(
            "code", scene.getCode(),
            "title", scene.getTitle(),
            "body", scene.getBody(),
            "isTerminal", scene.getIsTerminal(),
            "choices", formattedChoices
        ));
    }
    
    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Game API is running!");
    }
    
    /**
     * Endpoint: POST /api/game/session/start
     * Start a new game session
     */
    @PostMapping("/session/start")
    public ResponseEntity<?> startSession(@RequestBody Map<String, String> request) {
        String playerName = request.getOrDefault("playerName", "Player");
        
        // Create new session starting at intro
        GameSession session = new GameSession(playerName, "intro", "{}");
        GameSession saved = gameSessionRepository.save(session);
        
        return ResponseEntity.ok(Map.of(
            "sessionId", saved.getId(),
            "playerName", saved.getPlayerName(),
            "currentScene", saved.getCurrentSceneCode(),
            "flags", parseFlags(saved.getFlagsJson())
        ));
    }
    
    /**
     * Endpoint: POST /api/game/session/{sessionId}/choice
     * Make a choice and update the session
     */
    @PostMapping("/session/{sessionId}/choice")
    public ResponseEntity<?> makeChoice(
        @PathVariable Long sessionId,
        @RequestBody Map<String, Object> request
    ) {
        String targetSceneCode = (String) request.get("targetSceneCode");
        String setsFlag = (String) request.get("setsFlag");
        
        // Get session
        GameSession session = gameSessionRepository.findById(sessionId)
            .orElseThrow(() -> new RuntimeException("Session not found: " + sessionId));
        
        // Update scene
        session.setCurrentSceneCode(targetSceneCode);
        
        // Update flags if choice sets one
        if (setsFlag != null && !setsFlag.isEmpty()) {
            Map<String, Object> flags = parseFlags(session.getFlagsJson());
            flags.put(setsFlag, true);
            session.setFlagsJson(serializeFlags(flags));
        }
        
        gameSessionRepository.save(session);
        
        // Get new scene
        Scene scene = sceneRepository.findByCode(targetSceneCode)
            .orElseThrow(() -> new RuntimeException("Scene not found: " + targetSceneCode));
        
        // Format scene data
        Map<String, Object> sceneData = formatScene(scene);
        
        return ResponseEntity.ok(Map.of(
            "scene", sceneData,
            "flags", parseFlags(session.getFlagsJson())
        ));
    }
    
    /**
     * Endpoint: GET /api/game/session/{sessionId}/flags
     * Get current inventory/flags for a session
     */
    @GetMapping("/session/{sessionId}/flags")
    public ResponseEntity<?> getFlags(@PathVariable Long sessionId) {
        GameSession session = gameSessionRepository.findById(sessionId)
            .orElseThrow(() -> new RuntimeException("Session not found: " + sessionId));
        
        return ResponseEntity.ok(parseFlags(session.getFlagsJson()));
    }
    
    /**
     * Endpoint: GET /api/game/session/{sessionId}
     * Get current session state
     */
    @GetMapping("/session/{sessionId}")
    public ResponseEntity<?> getSession(@PathVariable Long sessionId) {
        GameSession session = gameSessionRepository.findById(sessionId)
            .orElseThrow(() -> new RuntimeException("Session not found: " + sessionId));
        
        // Get current scene
        Scene scene = sceneRepository.findByCode(session.getCurrentSceneCode())
            .orElseThrow(() -> new RuntimeException("Scene not found: " + session.getCurrentSceneCode()));
        
        return ResponseEntity.ok(Map.of(
            "sessionId", session.getId(),
            "playerName", session.getPlayerName(),
            "scene", formatScene(scene),
            "flags", parseFlags(session.getFlagsJson())
        ));
    }
    
    // ========== Helper Methods ==========
    
    /**
     * Format a scene with its choices
     */
    private Map<String, Object> formatScene(Scene scene) {
        List<Choice> choices = scene.getChoices();
        
        List<Map<String, Object>> formattedChoices = choices.stream()
            .map(choice -> {
                Map<String, Object> choiceMap = new HashMap<>();
                choiceMap.put("id", choice.getId());
                choiceMap.put("label", choice.getLabel());
                choiceMap.put("targetSceneCode", choice.getTargetSceneCode());
                choiceMap.put("requiresFlag", choice.getRequiresFlag() != null ? choice.getRequiresFlag() : "");
                choiceMap.put("setsFlag", choice.getSetsFlag() != null ? choice.getSetsFlag() : "");
                return choiceMap;
            })
            .collect(Collectors.toList());
        
        Map<String, Object> sceneData = new HashMap<>();
        sceneData.put("code", scene.getCode());
        sceneData.put("title", scene.getTitle());
        sceneData.put("body", scene.getBody());
        sceneData.put("isTerminal", scene.getIsTerminal());
        sceneData.put("choices", formattedChoices);
        
        return sceneData;
    }
    
    /**
     * Parse JSON flags string into Map
     */
    private Map<String, Object> parseFlags(String flagsJson) {
        try {
            return objectMapper.readValue(flagsJson, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            return new HashMap<>();
        }
    }
    
    /**
     * Serialize flags Map into JSON string
     */
    private String serializeFlags(Map<String, Object> flags) {
        try {
            return objectMapper.writeValueAsString(flags);
        } catch (Exception e) {
            return "{}";
        }
    }
}

