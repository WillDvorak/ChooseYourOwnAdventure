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

