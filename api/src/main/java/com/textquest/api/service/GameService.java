package com.textquest.api.service;

import com.textquest.api.entity.GameSession;
import com.textquest.api.entity.Scene;
import com.textquest.api.entity.Choice;
import com.textquest.api.entity.Item;
import com.textquest.api.entity.PlayerInventory;
import com.textquest.api.repository.GameSessionRepository;
import com.textquest.api.repository.SceneRepository;
import com.textquest.api.repository.ChoiceRepository;
import com.textquest.api.repository.ItemRepository;
import com.textquest.api.repository.PlayerInventoryRepository;
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
    
    @Autowired
    private ItemRepository itemRepository;
    
    @Autowired
    private PlayerInventoryRepository playerInventoryRepository;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * Create a new game session for a player
     */
    public GameSession createGameSession(String playerName, String startingSceneCode) {
        // Validate starting scene exists
        if (!sceneRepository.existsByCode(startingSceneCode)) {
            throw new SceneNotFoundException("Starting scene not found: " + startingSceneCode);
        }
        
        // Create new game session (HP initialized to 100/100 in constructor)
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
    @Transactional(readOnly = true)
    public List<Choice> getAvailableChoices(Long sessionId) {
        GameSession gameSession = getGameSession(sessionId);
        Map<String, Object> playerFlags = parseFlags(gameSession.getFlagsJson());
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
        
        // Apply health changes if the choice sets a health modifier
        if (choice.getSetsFlag() != null && choice.getSetsFlag().startsWith("health:")) {
            try {
                int change = Integer.parseInt(choice.getSetsFlag().substring(7)); // Remove "health:"
                modifyHp(gameSession.getId(), change);
                gameSession = getGameSession(gameSession.getId()); // Refresh to get updated HP
            } catch (Exception e) {
                // Invalid health modifier, ignore
            }
        }
        
        // Check for death
        if (gameSession.getHp() <= 0) {
            // Redirect to death scene if it exists, otherwise just set health to 0
            if (sceneRepository.existsByCode("death")) {
                gameSession.setCurrentSceneCode("death");
            } else {
                gameSession.setCurrentSceneCode(choice.getTargetSceneCode());
            }
        } else {
            // Update game session with new scene
            gameSession.setCurrentSceneCode(choice.getTargetSceneCode());
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
    
    // ==================== HP METHODS ====================
    
    /**
     * Modify HP (for damage or healing)
     */
    public GameSession modifyHp(Long sessionId, int hpChange) {
        GameSession gameSession = getGameSession(sessionId);
        if (isGameEnded(gameSession)) {
            throw new GameEndedException("Game has already ended");
        }
        int newHp = gameSession.getHp() + hpChange;
        newHp = Math.max(0, Math.min(newHp, gameSession.getMaxHp()));
        gameSession.setHp(newHp);
        return gameSessionRepository.save(gameSession);
    }
    
    /**
     * Set HP to a specific value
     */
    public GameSession setHp(Long sessionId, int hp) {
        GameSession gameSession = getGameSession(sessionId);
        if (isGameEnded(gameSession)) {
            throw new GameEndedException("Game has already ended");
        }
        hp = Math.max(0, Math.min(hp, gameSession.getMaxHp()));
        gameSession.setHp(hp);
        return gameSessionRepository.save(gameSession);
    }
    
    /**
     * Set max HP and optionally adjust current HP
     */
    public GameSession setMaxHp(Long sessionId, int maxHp, boolean adjustCurrent) {
        GameSession gameSession = getGameSession(sessionId);
        if (isGameEnded(gameSession)) {
            throw new GameEndedException("Game has already ended");
        }
        if (maxHp <= 0) {
            throw new IllegalArgumentException("Max HP must be greater than 0");
        }
        int oldMaxHp = gameSession.getMaxHp();
        gameSession.setMaxHp(maxHp);
        if (adjustCurrent && oldMaxHp > 0) {
            double ratio = (double) maxHp / oldMaxHp;
            int newHp = (int) Math.round(gameSession.getHp() * ratio);
            gameSession.setHp(Math.min(newHp, maxHp));
        } else {
            if (gameSession.getHp() > maxHp) {
                gameSession.setHp(maxHp);
            }
        }
        return gameSessionRepository.save(gameSession);
    }
    
    /**
     * Check if player is dead (HP <= 0)
     */
    @Transactional(readOnly = true)
    public boolean isPlayerDead(Long sessionId) {
        GameSession gameSession = getGameSession(sessionId);
        return gameSession.getHp() <= 0;
    }
    
    // ==================== INVENTORY METHODS ====================
    
    @Transactional(readOnly = true)
    public List<PlayerInventory> getPlayerInventory(Long sessionId) {
        getGameSession(sessionId);
        return playerInventoryRepository.findBySessionId(sessionId);
    }
    
    public PlayerInventory addItem(Long sessionId, Long itemId, Integer quantity) {
        GameSession gameSession = getGameSession(sessionId);
        if (isGameEnded(gameSession)) {
            throw new GameEndedException("Game has already ended");
        }
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found: " + itemId));
        Optional<PlayerInventory> existing = playerInventoryRepository.findByGameSessionAndItem(gameSession, item);
        if (existing.isPresent()) {
            PlayerInventory inventory = existing.get();
            inventory.setQuantity(inventory.getQuantity() + (quantity != null ? quantity : 1));
            return playerInventoryRepository.save(inventory);
        } else {
            PlayerInventory inventory = new PlayerInventory(gameSession, item, quantity != null ? quantity : 1);
            return playerInventoryRepository.save(inventory);
        }
    }
    
    public PlayerInventory addItemByCode(Long sessionId, String itemCode, Integer quantity) {
        Item item = itemRepository.findByCode(itemCode)
                .orElseThrow(() -> new RuntimeException("Item not found: " + itemCode));
        return addItem(sessionId, item.getId(), quantity);
    }
    
    public void removeItem(Long sessionId, Long itemId, Integer quantity) {
        GameSession gameSession = getGameSession(sessionId);
        if (isGameEnded(gameSession)) {
            throw new GameEndedException("Game has already ended");
        }
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found: " + itemId));
        Optional<PlayerInventory> inventory = playerInventoryRepository.findByGameSessionAndItem(gameSession, item);
        if (inventory.isPresent()) {
            PlayerInventory inv = inventory.get();
            int removeQuantity = quantity != null ? quantity : 1;
            if (inv.getQuantity() <= removeQuantity) {
                playerInventoryRepository.delete(inv);
            } else {
                inv.setQuantity(inv.getQuantity() - removeQuantity);
                playerInventoryRepository.save(inv);
            }
        }
    }
    
    public void removeItemByCode(Long sessionId, String itemCode, Integer quantity) {
        Item item = itemRepository.findByCode(itemCode)
                .orElseThrow(() -> new RuntimeException("Item not found: " + itemCode));
        removeItem(sessionId, item.getId(), quantity);
    }
    
    public PlayerInventory setItemQuantity(Long sessionId, Long itemId, Integer quantity) {
        GameSession gameSession = getGameSession(sessionId);
        if (isGameEnded(gameSession)) {
            throw new GameEndedException("Game has already ended");
        }
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found: " + itemId));
        Optional<PlayerInventory> existing = playerInventoryRepository.findByGameSessionAndItem(gameSession, item);
        if (existing.isPresent()) {
            PlayerInventory inventory = existing.get();
            inventory.setQuantity(quantity);
            return playerInventoryRepository.save(inventory);
        } else {
            PlayerInventory inventory = new PlayerInventory(gameSession, item, quantity);
            return playerInventoryRepository.save(inventory);
        }
    }
    
    public Map<String, Object> useItem(Long sessionId, Long itemId) {
        GameSession gameSession = getGameSession(sessionId);
        if (isGameEnded(gameSession)) {
            throw new GameEndedException("Game has already ended");
        }
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found: " + itemId));
        Optional<PlayerInventory> inventory = playerInventoryRepository.findByGameSessionAndItem(gameSession, item);
        if (!inventory.isPresent() || inventory.get().getQuantity() <= 0) {
            throw new RuntimeException("Item not in inventory or quantity is 0");
        }
        Map<String, Object> effects = parseEffects(item.getEffectsJson());
        Map<String, Object> result = new HashMap<>();
        if (effects.containsKey("hp_change")) {
            int hpChange = ((Number) effects.get("hp_change")).intValue();
            modifyHp(sessionId, hpChange);
            result.put("hp_change", hpChange);
        }
        if (effects.containsKey("max_hp_change")) {
            int maxHpChange = ((Number) effects.get("max_hp_change")).intValue();
            GameSession updated = getGameSession(sessionId);
            int newMaxHp = updated.getMaxHp() + maxHpChange;
            if (newMaxHp > 0) {
                setMaxHp(sessionId, newMaxHp, false);
                result.put("max_hp_change", maxHpChange);
            }
        }
        if (item.getIsConsumable()) {
            removeItem(sessionId, itemId, 1);
            result.put("consumed", true);
        } else {
            result.put("consumed", false);
        }
        return result;
    }
    
    public Map<String, Object> useItemByCode(Long sessionId, String itemCode) {
        Item item = itemRepository.findByCode(itemCode)
                .orElseThrow(() -> new RuntimeException("Item not found: " + itemCode));
        return useItem(sessionId, item.getId());
    }
    
    @Transactional(readOnly = true)
    public boolean hasItem(Long sessionId, Long itemId) {
        GameSession gameSession = getGameSession(sessionId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found: " + itemId));
        Optional<PlayerInventory> inventory = playerInventoryRepository.findByGameSessionAndItem(gameSession, item);
        return inventory.isPresent() && inventory.get().getQuantity() > 0;
    }
    
    @Transactional(readOnly = true)
    public boolean hasItemByCode(Long sessionId, String itemCode) {
        Item item = itemRepository.findByCode(itemCode)
                .orElseThrow(() -> new RuntimeException("Item not found: " + itemCode));
        return hasItem(sessionId, item.getId());
    }
    
    @Transactional(readOnly = true)
    public int getItemQuantity(Long sessionId, Long itemId) {
        GameSession gameSession = getGameSession(sessionId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found: " + itemId));
        Optional<PlayerInventory> inventory = playerInventoryRepository.findByGameSessionAndItem(gameSession, item);
        return inventory.map(PlayerInventory::getQuantity).orElse(0);
    }
    
    @Transactional(readOnly = true)
    public int getItemQuantityByCode(Long sessionId, String itemCode) {
        Item item = itemRepository.findByCode(itemCode)
                .orElseThrow(() -> new RuntimeException("Item not found: " + itemCode));
        return getItemQuantity(sessionId, item.getId());
    }
    
    @Transactional(readOnly = true)
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Item getItemByCode(String itemCode) {
        return itemRepository.findByCode(itemCode)
                .orElseThrow(() -> new RuntimeException("Item not found: " + itemCode));
    }
    
    @Transactional(readOnly = true)
    public Item getItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found: " + itemId));
    }
    
    private Map<String, Object> parseEffects(String effectsJson) {
        try {
            if (effectsJson == null || effectsJson.trim().isEmpty()) {
                return new HashMap<>();
            }
            return objectMapper.readValue(effectsJson, new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            return new HashMap<>();
        }
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
}
