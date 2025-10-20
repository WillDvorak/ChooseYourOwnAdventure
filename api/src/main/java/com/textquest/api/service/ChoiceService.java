package com.textquest.api.service;

import com.textquest.api.entity.Choice;
import com.textquest.api.repository.ChoiceRepository;
import com.textquest.api.exception.InvalidChoiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class ChoiceService {
    
    @Autowired
    private ChoiceRepository choiceRepository;
    
    /**
     * Get a choice by ID
     */
    @Transactional(readOnly = true)
    public Choice getChoiceById(Long choiceId) {
        return choiceRepository.findById(choiceId)
                .orElseThrow(() -> new InvalidChoiceException("Choice not found: " + choiceId));
    }
    
    /**
     * Check if a player can make a specific choice based on their flags
     */
    @Transactional(readOnly = true)
    public boolean canMakeChoice(Long choiceId, Map<String, Object> playerFlags) {
        Choice choice = getChoiceById(choiceId);
        
        // If choice has no requirements, it's always available
        if (choice.getRequiresFlag() == null || choice.getRequiresFlag().trim().isEmpty()) {
            return true;
        }
        
        // Check if player has the required flag
        return playerFlags.containsKey(choice.getRequiresFlag()) && 
               Boolean.TRUE.equals(playerFlags.get(choice.getRequiresFlag()));
    }
    
    /**
     * Validate that a choice can be made
     */
    @Transactional(readOnly = true)
    public void validateChoice(Long choiceId, Map<String, Object> playerFlags) {
        if (!canMakeChoice(choiceId, playerFlags)) {
            Choice choice = getChoiceById(choiceId);
            throw new InvalidChoiceException("Choice requirements not met. Required flag: " + choice.getRequiresFlag());
        }
    }
    
    /**
     * Process the effects of making a choice (update flags)
     */
    public Map<String, Object> processChoiceEffects(Long choiceId, Map<String, Object> currentFlags) {
        Choice choice = getChoiceById(choiceId);
        
        // Create a copy of current flags to avoid modifying the original
        Map<String, Object> updatedFlags = new java.util.HashMap<>(currentFlags);
        
        // If choice sets a flag, add it to the flags
        if (choice.getSetsFlag() != null && !choice.getSetsFlag().trim().isEmpty()) {
            updatedFlags.put(choice.getSetsFlag(), true);
        }
        
        return updatedFlags;
    }
    
    /**
     * Get all choices for a specific scene
     */
    @Transactional(readOnly = true)
    public List<Choice> getChoicesForScene(String sceneCode) {
        return choiceRepository.findBySceneCode(sceneCode);
    }
    
    /**
     * Get all choices for a scene by scene ID
     */
    @Transactional(readOnly = true)
    public List<Choice> getChoicesForScene(Long sceneId) {
        return choiceRepository.findBySceneId(sceneId);
    }
    
    /**
     * Get choices that require a specific flag
     */
    @Transactional(readOnly = true)
    public List<Choice> getChoicesRequiringFlag(String flagName) {
        return choiceRepository.findByRequiresFlag(flagName);
    }
    
    /**
     * Get choices that set a specific flag
     */
    @Transactional(readOnly = true)
    public List<Choice> getChoicesSettingFlag(String flagName) {
        return choiceRepository.findBySetsFlag(flagName);
    }
    
    /**
     * Get choices that don't require any flags
     */
    @Transactional(readOnly = true)
    public List<Choice> getChoicesWithoutRequirements() {
        return choiceRepository.findByRequiresFlagIsNullOrEmpty();
    }
    
    /**
     * Search choices by label
     */
    @Transactional(readOnly = true)
    public List<Choice> searchChoicesByLabel(String label) {
        return choiceRepository.findByLabelContainingIgnoreCase(label);
    }
    
    /**
     * Get choices that target a specific scene
     */
    @Transactional(readOnly = true)
    public List<Choice> getChoicesTargetingScene(String targetSceneCode) {
        return choiceRepository.findByTargetSceneCode(targetSceneCode);
    }
    
    /**
     * Get available choices for a scene (no flag requirements or player has required flags)
     */
    @Transactional(readOnly = true)
    public List<Choice> getAvailableChoicesForScene(String sceneCode, Map<String, Object> playerFlags) {
        List<Choice> allChoices = getChoicesForScene(sceneCode);
        
        return allChoices.stream()
                .filter(choice -> canMakeChoice(choice.getId(), playerFlags))
                .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * Check if a choice exists
     */
    @Transactional(readOnly = true)
    public boolean choiceExists(Long choiceId) {
        return choiceRepository.existsById(choiceId);
    }
    
    /**
     * Get the target scene code for a choice
     */
    @Transactional(readOnly = true)
    public String getTargetSceneCode(Long choiceId) {
        Choice choice = getChoiceById(choiceId);
        return choice.getTargetSceneCode();
    }
    
    /**
     * Get the flag that a choice requires
     */
    @Transactional(readOnly = true)
    public String getRequiredFlag(Long choiceId) {
        Choice choice = getChoiceById(choiceId);
        return choice.getRequiresFlag();
    }
    
    /**
     * Get the flag that a choice sets
     */
    @Transactional(readOnly = true)
    public String getSetsFlag(Long choiceId) {
        Choice choice = getChoiceById(choiceId);
        return choice.getSetsFlag();
    }
    
    /**
     * Check if a choice has any requirements
     */
    @Transactional(readOnly = true)
    public boolean hasRequirements(Long choiceId) {
        Choice choice = getChoiceById(choiceId);
        return choice.getRequiresFlag() != null && !choice.getRequiresFlag().trim().isEmpty();
    }
    
    /**
     * Check if a choice sets any flags
     */
    @Transactional(readOnly = true)
    public boolean setsFlags(Long choiceId) {
        Choice choice = getChoiceById(choiceId);
        return choice.getSetsFlag() != null && !choice.getSetsFlag().trim().isEmpty();
    }
}
