package com.textquest.api.service;

import com.textquest.api.entity.Scene;
import com.textquest.api.entity.Choice;
import com.textquest.api.repository.SceneRepository;
import com.textquest.api.repository.ChoiceRepository;
import com.textquest.api.exception.SceneNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class SceneService {
    
    @Autowired
    private SceneRepository sceneRepository;
    
    @Autowired
    private ChoiceRepository choiceRepository;
    
    @Autowired
    private ChoiceService choiceService;
    
    /**
     * Get a scene by its code
     */
    @Transactional(readOnly = true)
    public Scene getSceneByCode(String sceneCode) {
        return sceneRepository.findByCode(sceneCode)
                .orElseThrow(() -> new SceneNotFoundException("Scene not found: " + sceneCode));
    }
    
    /**
     * Get a scene by its code with choices loaded
     */
    @Transactional(readOnly = true)
    public Scene getSceneWithChoices(String sceneCode) {
        return sceneRepository.findByCodeWithChoices(sceneCode)
                .orElseThrow(() -> new SceneNotFoundException("Scene not found: " + sceneCode));
    }
    
    /**
     * Get all available choices for a scene based on player flags
     */
    @Transactional(readOnly = true)
    public List<Choice> getAvailableChoices(String sceneCode, Map<String, Object> playerFlags) {
        Scene scene = getSceneByCode(sceneCode);
        List<Choice> allChoices = choiceRepository.findBySceneCode(sceneCode);
        
        // Filter choices based on player flags
        return allChoices.stream()
                .filter(choice -> choiceService.canMakeChoice(choice.getId(), playerFlags))
                .collect(Collectors.toList());
    }
    
    /**
     * Get all choices for a scene (without filtering)
     */
    @Transactional(readOnly = true)
    public List<Choice> getAllChoicesForScene(String sceneCode) {
        return choiceRepository.findBySceneCode(sceneCode);
    }
    
    /**
     * Check if a scene is terminal (end scene)
     */
    @Transactional(readOnly = true)
    public boolean isTerminalScene(String sceneCode) {
        Scene scene = getSceneByCode(sceneCode);
        return scene.getIsTerminal();
    }
    
    /**
     * Check if a scene exists
     */
    @Transactional(readOnly = true)
    public boolean sceneExists(String sceneCode) {
        return sceneRepository.existsByCode(sceneCode);
    }
    
    /**
     * Validate that a target scene exists
     */
    @Transactional(readOnly = true)
    public void validateSceneExists(String sceneCode) {
        if (!sceneExists(sceneCode)) {
            throw new SceneNotFoundException("Target scene not found: " + sceneCode);
        }
    }
    
    /**
     * Get all scenes
     */
    @Transactional(readOnly = true)
    public List<Scene> getAllScenes() {
        return sceneRepository.findAll();
    }
    
    /**
     * Get all terminal scenes
     */
    @Transactional(readOnly = true)
    public List<Scene> getTerminalScenes() {
        return sceneRepository.findByIsTerminalTrue();
    }
    
    /**
     * Get all non-terminal scenes
     */
    @Transactional(readOnly = true)
    public List<Scene> getNonTerminalScenes() {
        return sceneRepository.findByIsTerminalFalse();
    }
    
    /**
     * Search scenes by title
     */
    @Transactional(readOnly = true)
    public List<Scene> searchScenesByTitle(String title) {
        return sceneRepository.findByTitleContainingIgnoreCase(title);
    }
    
    /**
     * Search scenes by body content
     */
    @Transactional(readOnly = true)
    public List<Scene> searchScenesByBody(String body) {
        return sceneRepository.findByBodyContainingIgnoreCase(body);
    }
    
    /**
     * Get a scene by ID
     */
    @Transactional(readOnly = true)
    public Optional<Scene> getSceneById(Long sceneId) {
        return sceneRepository.findById(sceneId);
    }
    
    /**
     * Get all scenes with their choices loaded
     */
    @Transactional(readOnly = true)
    public List<Scene> getAllScenesWithChoices() {
        return sceneRepository.findAllWithChoices();
    }
    
    /**
     * Check if a scene has any choices available
     */
    @Transactional(readOnly = true)
    public boolean hasAvailableChoices(String sceneCode, Map<String, Object> playerFlags) {
        List<Choice> availableChoices = getAvailableChoices(sceneCode, playerFlags);
        return !availableChoices.isEmpty();
    }
    
    /**
     * Get the number of available choices for a scene
     */
    @Transactional(readOnly = true)
    public int getAvailableChoiceCount(String sceneCode, Map<String, Object> playerFlags) {
        return getAvailableChoices(sceneCode, playerFlags).size();
    }
}
