package com.textquest.api;

import com.textquest.api.entity.GameSession;
import com.textquest.api.entity.Scene;
import com.textquest.api.entity.Choice;
import com.textquest.api.repository.GameSessionRepository;
import com.textquest.api.repository.SceneRepository;
import com.textquest.api.repository.ChoiceRepository;
import com.textquest.api.service.GameService;
import com.textquest.api.service.SceneService;
import com.textquest.api.service.ChoiceService;
import com.textquest.api.exception.InvalidChoiceException;
import com.textquest.api.exception.GameEndedException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for the V2 seed data (15 scenes)
 * Tests cover scene structure, transitions, flags, health, and game flow
 */
@SpringBootTest
@TestPropertySource(properties = {
    "spring.flyway.enabled=true"
})
@Transactional
class DatabaseSeedDataTest {

    @Autowired
    private SceneRepository sceneRepository;

    @Autowired
    private ChoiceRepository choiceRepository;

    @Autowired
    private GameSessionRepository gameSessionRepository;

    @Autowired
    private GameService gameService;

    @Autowired
    private SceneService sceneService;

    @Autowired
    private ChoiceService choiceService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void test1_All15ScenesLoaded() {
        // Test that all 15 scenes from seed data are present
        List<Scene> allScenes = sceneRepository.findAll();
        
        assertEquals(15, allScenes.size(), "Should have exactly 15 scenes");
        
        // Verify specific scenes exist
        assertTrue(sceneRepository.findByCode("intro").isPresent(), "intro scene should exist");
        assertTrue(sceneRepository.findByCode("forest").isPresent(), "forest scene should exist");
        assertTrue(sceneRepository.findByCode("cave").isPresent(), "cave scene should exist");
        assertTrue(sceneRepository.findByCode("ruins").isPresent(), "ruins scene should exist");
        assertTrue(sceneRepository.findByCode("bridge").isPresent(), "bridge scene should exist");
        assertTrue(sceneRepository.findByCode("village").isPresent(), "village scene should exist");
        assertTrue(sceneRepository.findByCode("dungeon").isPresent(), "dungeon scene should exist");
        assertTrue(sceneRepository.findByCode("temple").isPresent(), "temple scene should exist");
        assertTrue(sceneRepository.findByCode("treasure").isPresent(), "treasure scene should exist");
        assertTrue(sceneRepository.findByCode("danger").isPresent(), "danger scene should exist");
        assertTrue(sceneRepository.findByCode("danger2").isPresent(), "danger2 scene should exist");
        assertTrue(sceneRepository.findByCode("heal").isPresent(), "heal scene should exist");
        assertTrue(sceneRepository.findByCode("ending1").isPresent(), "ending1 scene should exist");
        assertTrue(sceneRepository.findByCode("ending2").isPresent(), "ending2 scene should exist");
        assertTrue(sceneRepository.findByCode("death").isPresent(), "death scene should exist");
        
        // Verify terminal scenes
        Scene ending1 = sceneRepository.findByCode("ending1").get();
        Scene ending2 = sceneRepository.findByCode("ending2").get();
        Scene death = sceneRepository.findByCode("death").get();
        assertTrue(ending1.getIsTerminal(), "ending1 should be terminal");
        assertTrue(ending2.getIsTerminal(), "ending2 should be terminal");
        assertTrue(death.getIsTerminal(), "death should be terminal");
    }

    @Test
    void test2_IntroSceneHasFourChoices() {
        // Test that intro scene has 4 choices as specified in seed data
        Scene intro = sceneRepository.findByCode("intro").orElseThrow();
        List<Choice> choices = choiceRepository.findBySceneCode("intro");
        
        assertEquals(4, choices.size(), "intro should have 4 choices");
        
        // Verify all 4 choices exist
        boolean hasForest = choices.stream().anyMatch(c -> c.getTargetSceneCode().equals("forest"));
        boolean hasCave = choices.stream().anyMatch(c -> c.getTargetSceneCode().equals("cave"));
        boolean hasRuins = choices.stream().anyMatch(c -> c.getTargetSceneCode().equals("ruins"));
        boolean hasBridge = choices.stream().anyMatch(c -> c.getTargetSceneCode().equals("bridge"));
        
        assertTrue(hasForest, "intro should have choice to forest");
        assertTrue(hasCave, "intro should have choice to cave");
        assertTrue(hasRuins, "intro should have choice to ruins");
        assertTrue(hasBridge, "intro should have choice to bridge");
    }

    @Test
    void test3_TorchRequirementForCaveTreasure() {
        // Test that descending in cave requires torch flag
        Scene cave = sceneRepository.findByCode("cave").orElseThrow();
        List<Choice> caveChoices = choiceRepository.findBySceneCode("cave");
        
        // Find the choice that goes to treasure
        Optional<Choice> descendChoice = caveChoices.stream()
            .filter(c -> c.getTargetSceneCode().equals("treasure"))
            .findFirst();
        
        assertTrue(descendChoice.isPresent(), "cave should have choice to treasure");
        Choice choice = descendChoice.get();
        assertEquals("torch", choice.getRequiresFlag(), "Descending to treasure should require torch");
        assertEquals("key", choice.getSetsFlag(), "Descending should grant key");
    }

    @Test
    void test4_ForestTorchAcquisition() {
        // Test that searching for supplies in forest grants torch
        GameSession session = gameService.createGameSession("TestPlayer", "forest");
        Map<String, Object> initialFlags = gameService.getPlayerFlags(session.getId());
        assertFalse(initialFlags.containsKey("torch"), "Should not have torch initially");
        
        // Get choices for forest
        List<Choice> choices = sceneService.getAvailableChoices("forest", initialFlags);
        Choice searchChoice = choices.stream()
            .filter(c -> c.getLabel().contains("Search for supplies"))
            .findFirst()
            .orElseThrow();
        
        // Make the choice
        GameSession updatedSession = gameService.makeChoice(session.getId(), searchChoice.getId());
        Map<String, Object> updatedFlags = gameService.getPlayerFlags(updatedSession.getId());
        
        assertTrue(updatedFlags.containsKey("torch"), "Should have torch after searching");
        assertEquals(true, updatedFlags.get("torch"), "Torch flag should be true");
        assertEquals("forest", updatedSession.getCurrentSceneCode(), "Should still be in forest (self-loop)");
    }

    @Test
    void test5_HealthSystemWorks() {
        // Test health loss and restoration
        GameSession session = gameService.createGameSession("TestPlayer", "danger");
        
        // Set initial health
        Map<String, Object> healthFlag = new java.util.HashMap<>();
        healthFlag.put("health", 100);
        gameService.updateFlags(session.getId(), healthFlag);
        
        // Get choices for danger scene
        Map<String, Object> flags = gameService.getPlayerFlags(session.getId());
        List<Choice> choices = sceneService.getAvailableChoices("danger", flags);
        
        // Find "Run away carefully" choice (-10 HP)
        Choice runChoice = choices.stream()
            .filter(c -> c.getLabel().contains("Run away carefully"))
            .findFirst()
            .orElseThrow();
        
        // Make choice
        GameSession updatedSession = gameService.makeChoice(session.getId(), runChoice.getId());
        Map<String, Object> updatedFlags = gameService.getPlayerFlags(updatedSession.getId());
        
        assertEquals(90, updatedFlags.get("health"), "Health should be reduced by 10");
        assertEquals("forest", updatedSession.getCurrentSceneCode(), "Should be in forest after running");
    }

    @Test
    void test6_TerminalScenesPreventFurtherActions() {
        // Test that terminal scenes prevent making choices
        GameSession session = gameService.createGameSession("TestPlayer", "ending1");
        
        // Verify game is ended
        assertTrue(gameService.isGameEnded(session.getId()), "Game should be ended at terminal scene");
        
        // Try to update flags - should throw exception
        Map<String, Object> flags = new java.util.HashMap<>();
        flags.put("test", true);
        
        assertThrows(GameEndedException.class, () -> {
            gameService.updateFlags(session.getId(), flags);
        }, "Should not be able to update flags when game is ended");
    }

    @Test
    void test7_TempleDoorOpeningLogic() {
        // Test temple door opening with amulet (self-loop) and entering with amulet
        GameSession session = gameService.createGameSession("TestPlayer", "temple");
        
        // Give player amulet
        Map<String, Object> flags = new java.util.HashMap<>();
        flags.put("amulet", true);
        gameService.updateFlags(session.getId(), flags);
        
        // Get available choices
        Map<String, Object> currentFlags = gameService.getPlayerFlags(session.getId());
        List<Choice> choices = sceneService.getAvailableChoices("temple", currentFlags);
        
        // Find "Use the amulet to open the door" choice (self-loop)
        Choice openDoorChoice = choices.stream()
            .filter(c -> c.getLabel().contains("Use the amulet to open the door"))
            .findFirst()
            .orElseThrow();
        
        assertEquals("amulet", openDoorChoice.getRequiresFlag(), "Should require amulet");
        assertEquals("temple", openDoorChoice.getTargetSceneCode(), "Should loop back to temple");
        
        // Find "Enter the temple" choice (requires amulet)
        Choice enterChoice = choices.stream()
            .filter(c -> c.getTargetSceneCode().equals("ending1"))
            .findFirst()
            .orElseThrow();
        
        assertEquals("amulet", enterChoice.getRequiresFlag(), "Enter should require amulet");
        
        // Make the enter choice to reach ending
        GameSession finalSession = gameService.makeChoice(session.getId(), enterChoice.getId());
        assertEquals("ending1", finalSession.getCurrentSceneCode(), "Should reach ending1");
    }

    @Test
    void test8_MultiplePathsToEnding1() {
        // Test that ending1 can be reached via key path (alternative to amulet)
        GameSession session = gameService.createGameSession("TestPlayer", "temple");
        
        // Give player key (alternative path to ending1)
        Map<String, Object> flags = new java.util.HashMap<>();
        flags.put("key", true);
        gameService.updateFlags(session.getId(), flags);
        
        // Get available choices
        Map<String, Object> currentFlags = gameService.getPlayerFlags(session.getId());
        List<Choice> choices = sceneService.getAvailableChoices("temple", currentFlags);
        
        // Find "Enter the temple" choice that requires key
        Choice enterChoice = choices.stream()
            .filter(c -> c.getTargetSceneCode().equals("ending1") && 
                        "key".equals(c.getRequiresFlag()))
            .findFirst()
            .orElseThrow();
        
        assertEquals("key", enterChoice.getRequiresFlag(), "Enter choice should require key");
        
        // Make choice to reach ending
        GameSession finalSession = gameService.makeChoice(session.getId(), enterChoice.getId());
        
        assertEquals("ending1", finalSession.getCurrentSceneCode(), "Should reach ending1");
        assertTrue(gameService.isGameEnded(finalSession.getId()), "Game should be ended");
    }

    @Test
    void test9_ItemAcquisitionLoopsHaveExitPaths() {
        // Test that self-loops for item acquisition have alternative exits
        // Test ruins scene - should have loops for amulet/knowledge but also exits
        Scene ruins = sceneRepository.findByCode("ruins").orElseThrow();
        List<Choice> ruinsChoices = choiceRepository.findBySceneCode("ruins");
        
        // Verify there are self-loops
        boolean hasAmuletLoop = ruinsChoices.stream()
            .anyMatch(c -> c.getTargetSceneCode().equals("ruins") && 
                         c.getSetsFlag() != null && c.getSetsFlag().equals("amulet"));
        boolean hasKnowledgeLoop = ruinsChoices.stream()
            .anyMatch(c -> c.getTargetSceneCode().equals("ruins") && 
                         c.getSetsFlag() != null && c.getSetsFlag().equals("knowledge"));
        
        assertTrue(hasAmuletLoop, "ruins should have amulet acquisition loop");
        assertTrue(hasKnowledgeLoop, "ruins should have knowledge acquisition loop");
        
        // Verify there are exit paths (not just loops)
        boolean hasTempleExit = ruinsChoices.stream()
            .anyMatch(c -> c.getTargetSceneCode().equals("temple"));
        boolean hasIntroExit = ruinsChoices.stream()
            .anyMatch(c -> c.getTargetSceneCode().equals("intro"));
        boolean hasBridgeExit = ruinsChoices.stream()
            .anyMatch(c -> c.getTargetSceneCode().equals("bridge"));
        
        assertTrue(hasTempleExit || hasIntroExit || hasBridgeExit, 
                   "ruins should have at least one exit path besides loops");
    }

    @Test
    void test10_InvalidChoicesRejected() {
        // Test that choices requiring flags are not available without those flags
        GameSession session = gameService.createGameSession("TestPlayer", "cave");
        
        // Player should not have torch initially
        Map<String, Object> flags = gameService.getPlayerFlags(session.getId());
        assertFalse(flags.containsKey("torch"), "Should not have torch initially");
        
        // Get available choices
        List<Choice> availableChoices = sceneService.getAvailableChoices("cave", flags);
        
        // Find choice to treasure (requires torch)
        Optional<Choice> treasureChoice = availableChoices.stream()
            .filter(c -> c.getTargetSceneCode().equals("treasure"))
            .findFirst();
        
        // Should not be available without torch
        assertFalse(treasureChoice.isPresent(), 
                   "Choice to treasure should not be available without torch");
        
        // Now give player torch
        Map<String, Object> newFlags = new java.util.HashMap<>();
        newFlags.put("torch", true);
        gameService.updateFlags(session.getId(), newFlags);
        
        // Get choices again
        Map<String, Object> updatedFlags = gameService.getPlayerFlags(session.getId());
        List<Choice> newAvailableChoices = sceneService.getAvailableChoices("cave", updatedFlags);
        
        // Now treasure choice should be available
        Optional<Choice> treasureChoiceWithTorch = newAvailableChoices.stream()
            .filter(c -> c.getTargetSceneCode().equals("treasure"))
            .findFirst();
        
        assertTrue(treasureChoiceWithTorch.isPresent(), 
                  "Choice to treasure should be available with torch");
    }
}

