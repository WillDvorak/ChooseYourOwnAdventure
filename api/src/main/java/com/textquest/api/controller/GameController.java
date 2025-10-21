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
@CrossOrigin(origins = "http://localhost:5173")
public class GameController {
    
    @Autowired
    private SceneRepository sceneRepository;
    
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
}

