package com.textquest.api.service;

import com.textquest.api.entity.GameSession;
import com.textquest.api.entity.Scene;
import com.textquest.api.entity.Choice;
import com.textquest.api.repository.GameSessionRepository;
import com.textquest.api.repository.SceneRepository;
import com.textquest.api.repository.ChoiceRepository;
import com.textquest.api.exception.GameSessionNotFoundException;
import com.textquest.api.exception.SceneNotFoundException;
import com.textquest.api.exception.InvalidChoiceException;
import com.textquest.api.exception.GameEndedException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class GameService {
    
    @Autowired
    private GameSessionRepository gameSessionRepository;
    
    @Autowired
    private SceneRepository sceneRepository;
    
    @Autowired
    private ChoiceRepository choiceRepository;
    
    @Autowired
    private SceneService sceneService;
    
    @Autowired
    private ChoiceService choiceService;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * Create a new game session for a player
     */
    public GameSession createGameSession(String playerName, String startingSceneCode) {
        // Validate starting scene exists
        if (!sceneRepository.existsByCode(startingSceneCode)) {
            throw new SceneNotFoundException("Starting scene not found: " + startingSceneCode);
        }
        
        // Create new game session
        GameSession gameSession = new GameSession(playerName, startingSceneCode);
        return gameSessionRepository.save(gameSession);
    }
    
    /**
     * Get a game session by ID
     */
    @Transactional(readOnly = true)
    public GameSession getGameSession(Long sessionId) {
        return gameSessionRepository.findById(sessionId)
                .orElseThrow(() -> new GameSessionNotFoundException("Game session not found: " + sessionId));
    }
    
    /**
     * Get the latest game session for a player
     */
    @Transactional(readOnly = true)
    public Optional<GameSession> getLatestGameSession(String playerName) {
        return gameSessionRepository.findLatestByPlayerName(playerName);
    }
    
    /**
     * Get the current scene for a game session with available choices
     */
    @Transactional(readOnly = true)
    public Scene getCurrentScene(Long sessionId) {
        GameSession gameSession = getGameSession(sessionId);
        return sceneService.getSceneWithChoices(gameSession.getCurrentSceneCode());
    }
    
    /**
     * Get available choices for the current scene based on player flags
     */
    @Transactional
    public List<Choice> getAvailableChoices(Long sessionId) {
        GameSession gameSession = getGameSession(sessionId);
        Map<String, Object> playerFlags = parseFlags(gameSession.getFlagsJson());
        
        // Update relic count flag if player is at Astral Gate and persist it
        if ("astral_gate".equals(gameSession.getCurrentSceneCode())) {
            updateRelicCountFlag(playerFlags);
            // Persist the updated flags
            gameSession.setFlagsJson(serializeFlags(playerFlags));
            gameSessionRepository.save(gameSession);
        }
        
        return sceneService.getAvailableChoices(gameSession.getCurrentSceneCode(), playerFlags);
    }
    
    /**
     * Process a player's choice and advance the game
     */
    public GameSession makeChoice(Long sessionId, Long choiceId) {
        GameSession gameSession = getGameSession(sessionId);
        
        // Check if game has ended
        if (isGameEnded(gameSession)) {
            throw new GameEndedException("Game has already ended");
        }
        
        // Get the choice
        Choice choice = choiceService.getChoiceById(choiceId);
        
        // Validate the choice is available for current scene
        if (!choice.getScene().getCode().equals(gameSession.getCurrentSceneCode())) {
            throw new InvalidChoiceException("Choice is not available for current scene");
        }
        
        // Validate choice requirements
        Map<String, Object> playerFlags = parseFlags(gameSession.getFlagsJson());
        if (!choiceService.canMakeChoice(choiceId, playerFlags)) {
            throw new InvalidChoiceException("Choice requirements not met");
        }
        
        // Process choice effects (update flags)
        Map<String, Object> updatedFlags = choiceService.processChoiceEffects(choiceId, playerFlags);
        
        // Update game session
        gameSession.setCurrentSceneCode(choice.getTargetSceneCode());
        
        // Check if player reached Astral Gate and has enough relics for restoration
        if ("astral_gate".equals(choice.getTargetSceneCode())) {
            updateRelicCountFlag(updatedFlags);
        }
        
        gameSession.setFlagsJson(serializeFlags(updatedFlags));
        
        return gameSessionRepository.save(gameSession);
    }
    
    /**
     * Update player flags directly
     */
    public GameSession updateFlags(Long sessionId, Map<String, Object> newFlags) {
        GameSession gameSession = getGameSession(sessionId);
        
        if (isGameEnded(gameSession)) {
            throw new GameEndedException("Game has already ended");
        }
        
        Map<String, Object> currentFlags = parseFlags(gameSession.getFlagsJson());
        currentFlags.putAll(newFlags);
        
        gameSession.setFlagsJson(serializeFlags(currentFlags));
        return gameSessionRepository.save(gameSession);
    }
    
    /**
     * Get current player flags
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getPlayerFlags(Long sessionId) {
        GameSession gameSession = getGameSession(sessionId);
        return parseFlags(gameSession.getFlagsJson());
    }
    
    /**
     * End a game session
     */
    public void endGameSession(Long sessionId) {
        GameSession gameSession = getGameSession(sessionId);
        gameSessionRepository.delete(gameSession);
    }
    
    /**
     * Check if the game has ended (reached a terminal scene)
     */
    @Transactional(readOnly = true)
    public boolean isGameEnded(Long sessionId) {
        GameSession gameSession = getGameSession(sessionId);
        return isGameEnded(gameSession);
    }
    
    /**
     * Check if a game session has ended
     */
    private boolean isGameEnded(GameSession gameSession) {
        Optional<Scene> currentScene = sceneRepository.findByCode(gameSession.getCurrentSceneCode());
        return currentScene.map(Scene::getIsTerminal).orElse(false);
    }
    
    /**
     * Parse JSON flags string into Map
     */
    private Map<String, Object> parseFlags(String flagsJson) {
        try {
            if (flagsJson == null || flagsJson.trim().isEmpty()) {
                return new HashMap<>();
            }
            return objectMapper.readValue(flagsJson, new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            // If parsing fails, return empty map
            return new HashMap<>();
        }
    }
    
    /**
     * Serialize flags Map to JSON string
     */
    private String serializeFlags(Map<String, Object> flags) {
        try {
            return objectMapper.writeValueAsString(flags);
        } catch (JsonProcessingException e) {
            // If serialization fails, return empty JSON object
            return "{}";
        }
    }
    
    /**
     * Count relics and set flag if player has 3+ relics
     * Relics include: forest_sigil, firebrand, portal_shard, reality_shard, ancient_artifacts, water_power
     */
    private void updateRelicCountFlag(Map<String, Object> flags) {
        int relicCount = 0;
        
        // List of relic flags
        String[] relicFlags = {
            "forest_sigil", "firebrand", "portal_shard", "reality_shard", 
            "ancient_artifacts", "water_power"
        };
        
        // Count relics
        for (String relic : relicFlags) {
            if (flags.containsKey(relic) && Boolean.TRUE.equals(flags.get(relic))) {
                relicCount++;
            }
        }
        
        // Set flag if player has 3+ relics (required for Restore All Realms ending)
        if (relicCount >= 3) {
            flags.put("has_3_relics", true);
        } else {
            flags.put("has_3_relics", false);
        }
    }
}
