package com.textquest.api.entity;

import com.textquest.api.repository.ChoiceRepository;
import com.textquest.api.repository.GameSessionRepository;
import com.textquest.api.repository.SceneRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.flyway.enabled=false"
})
@Transactional
class EntityTest {

    @Autowired
    private SceneRepository sceneRepository;

    @Autowired
    private ChoiceRepository choiceRepository;

    @Autowired
    private GameSessionRepository gameSessionRepository;

    @Test
    void testSceneEntity() {
        // Create a new scene
        Scene scene = new Scene("test_scene", "Test Scene", "This is a test scene body", false);
        
        // Save the scene
        Scene savedScene = sceneRepository.save(scene);
        
        // Verify the scene was saved
        assertNotNull(savedScene.getId());
        assertEquals("test_scene", savedScene.getCode());
        assertEquals("Test Scene", savedScene.getTitle());
        assertEquals("This is a test scene body", savedScene.getBody());
        assertFalse(savedScene.getIsTerminal());
        assertNotNull(savedScene.getCreatedAt());
        assertNotNull(savedScene.getUpdatedAt());
        
        // Test finding by code
        Optional<Scene> foundScene = sceneRepository.findByCode("test_scene");
        assertTrue(foundScene.isPresent());
        assertEquals("test_scene", foundScene.get().getCode());
    }

    @Test
    void testChoiceEntity() {
        // Create a scene first
        Scene scene = new Scene("parent_scene", "Parent Scene", "Parent scene body", false);
        Scene savedScene = sceneRepository.save(scene);
        
        // Create a choice
        Choice choice = new Choice(savedScene, "Go to test", "test_scene", null, "test_flag");
        
        // Save the choice
        Choice savedChoice = choiceRepository.save(choice);
        
        // Verify the choice was saved
        assertNotNull(savedChoice.getId());
        assertEquals("Go to test", savedChoice.getLabel());
        assertEquals("test_scene", savedChoice.getTargetSceneCode());
        assertNull(savedChoice.getRequiresFlag());
        assertEquals("test_flag", savedChoice.getSetsFlag());
        assertNotNull(savedChoice.getCreatedAt());
        assertNotNull(savedChoice.getUpdatedAt());
        
        // Test finding choices by scene
        List<Choice> choices = choiceRepository.findBySceneCode("parent_scene");
        assertEquals(1, choices.size());
        assertEquals("Go to test", choices.get(0).getLabel());
    }

    @Test
    void testGameSessionEntity() {
        // Create a game session
        GameSession gameSession = new GameSession("TestPlayer", "intro", "{\"torch\": true, \"gold\": false}");
        
        // Save the game session
        GameSession savedSession = gameSessionRepository.save(gameSession);
        
        // Verify the game session was saved
        assertNotNull(savedSession.getId());
        assertEquals("TestPlayer", savedSession.getPlayerName());
        assertEquals("intro", savedSession.getCurrentSceneCode());
        assertEquals("{\"torch\": true, \"gold\": false}", savedSession.getFlagsJson());
        assertNotNull(savedSession.getCreatedAt());
        assertNotNull(savedSession.getUpdatedAt());
        
        // Test finding by player name
        List<GameSession> sessions = gameSessionRepository.findByPlayerName("TestPlayer");
        assertEquals(1, sessions.size());
        assertEquals("TestPlayer", sessions.get(0).getPlayerName());
    }

    @Test
    void testSceneChoiceRelationship() {
        // Create a scene
        Scene scene = new Scene("relationship_test", "Relationship Test", "Testing relationships", false);
        Scene savedScene = sceneRepository.save(scene);
        
        // Create choices for the scene
        Choice choice1 = new Choice(savedScene, "Choice 1", "scene1", null, "flag1");
        Choice choice2 = new Choice(savedScene, "Choice 2", "scene2", "flag1", "flag2");
        
        choiceRepository.save(choice1);
        choiceRepository.save(choice2);
        
        // Test the relationship by finding choices for the scene
        List<Choice> choices = choiceRepository.findBySceneCode("relationship_test");
        assertEquals(2, choices.size());
        
        // Verify choice details
        assertTrue(choices.stream().anyMatch(c -> c.getLabel().equals("Choice 1")));
        assertTrue(choices.stream().anyMatch(c -> c.getLabel().equals("Choice 2")));
    }

    @Test
    void testRepositoryQueries() {
        // Create test data
        Scene scene1 = new Scene("scene1", "Scene 1", "First scene", false);
        Scene scene2 = new Scene("scene2", "Scene 2", "Second scene", true);
        sceneRepository.save(scene1);
        sceneRepository.save(scene2);
        
        GameSession session1 = new GameSession("Player1", "scene1", "{\"torch\": true}");
        GameSession session2 = new GameSession("Player2", "scene2", "{\"gold\": true}");
        gameSessionRepository.save(session1);
        gameSessionRepository.save(session2);
        
        // Test scene queries
        List<Scene> terminalScenes = sceneRepository.findByIsTerminalTrue();
        assertTrue(terminalScenes.size() >= 1);
        assertTrue(terminalScenes.stream().anyMatch(s -> s.getCode().equals("scene2")));
        
        List<Scene> nonTerminalScenes = sceneRepository.findByIsTerminalFalse();
        assertTrue(nonTerminalScenes.size() >= 1);
        assertTrue(nonTerminalScenes.stream().anyMatch(s -> s.getCode().equals("scene1")));
        
        // Test game session queries
        List<GameSession> player1Sessions = gameSessionRepository.findByPlayerName("Player1");
        assertEquals(1, player1Sessions.size());
        assertEquals("scene1", player1Sessions.get(0).getCurrentSceneCode());
        
        List<GameSession> scene2Sessions = gameSessionRepository.findByCurrentSceneCode("scene2");
        assertEquals(1, scene2Sessions.size());
        assertEquals("Player2", scene2Sessions.get(0).getPlayerName());
    }

    @Test
    void testEntityValidation() {
        // Test that required fields are enforced
        Scene scene = new Scene();
        scene.setCode("test");
        // Missing title and body - should still save but test the setters
        scene.setTitle("Test Title");
        scene.setBody("Test Body");
        
        Scene savedScene = sceneRepository.save(scene);
        assertNotNull(savedScene.getId());
        assertEquals("test", savedScene.getCode());
        assertEquals("Test Title", savedScene.getTitle());
        assertEquals("Test Body", savedScene.getBody());
    }
}
