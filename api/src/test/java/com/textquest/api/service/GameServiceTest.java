package com.textquest.api.service;

import com.textquest.api.entity.GameSession;
import com.textquest.api.entity.Scene;
import com.textquest.api.entity.Choice;
import com.textquest.api.repository.GameSessionRepository;
import com.textquest.api.repository.SceneRepository;
import com.textquest.api.exception.GameSessionNotFoundException;
import com.textquest.api.exception.SceneNotFoundException;
import com.textquest.api.exception.GameEndedException;
import com.textquest.api.exception.InvalidChoiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.flyway.enabled=false"
})
@Transactional
class GameServiceTest {

    @Autowired
    private GameService gameService;

    @Autowired
    private GameSessionRepository gameSessionRepository;

    @Autowired
    private SceneRepository sceneRepository;

    @Autowired
    private SceneService sceneService;

    @Autowired
    private ChoiceService choiceService;

    private Scene testScene;
    private Scene targetScene;
    private Choice testChoice;

    @BeforeEach
    void setUp() {
        // Create test scenes
        testScene = new Scene("test_scene", "Test Scene", "This is a test scene", false);
        targetScene = new Scene("target_scene", "Target Scene", "Target scene body", false);
        sceneRepository.save(testScene);
        sceneRepository.save(targetScene);

        // Create a test choice
        testChoice = new Choice(testScene, "Go to target", "target_scene", null, "visited_target");
        choiceService.getChoicesForScene("test_scene").forEach(c -> {
            // Clean up any existing choices
        });
    }

    @Test
    void testCreateGameSession_Success() {
        // Act
        GameSession session = gameService.createGameSession("TestPlayer", "test_scene");

        // Assert
        assertNotNull(session.getId());
        assertEquals("TestPlayer", session.getPlayerName());
        assertEquals("test_scene", session.getCurrentSceneCode());
        assertEquals("{}", session.getFlagsJson());
        assertNotNull(session.getCreatedAt());
    }

    @Test
    void testCreateGameSession_InvalidScene_ThrowsException() {
        // Act & Assert
        assertThrows(SceneNotFoundException.class, () -> {
            gameService.createGameSession("TestPlayer", "non_existent_scene");
        });
    }

    @Test
    void testGetGameSession_Success() {
        // Arrange
        GameSession createdSession = gameService.createGameSession("TestPlayer", "test_scene");
        Long sessionId = createdSession.getId();

        // Act
        GameSession retrievedSession = gameService.getGameSession(sessionId);

        // Assert
        assertNotNull(retrievedSession);
        assertEquals(sessionId, retrievedSession.getId());
        assertEquals("TestPlayer", retrievedSession.getPlayerName());
    }

    @Test
    void testGetGameSession_NotFound_ThrowsException() {
        // Act & Assert
        assertThrows(GameSessionNotFoundException.class, () -> {
            gameService.getGameSession(99999L);
        });
    }

    @Test
    void testUpdateFlags_Success() {
        // Arrange
        GameSession session = gameService.createGameSession("TestPlayer", "test_scene");
        Map<String, Object> newFlags = new HashMap<>();
        newFlags.put("has_key", true);
        newFlags.put("gold", 100);

        // Act
        GameSession updatedSession = gameService.updateFlags(session.getId(), newFlags);

        // Assert
        Map<String, Object> flags = gameService.getPlayerFlags(updatedSession.getId());
        assertTrue(flags.containsKey("has_key"));
        assertEquals(true, flags.get("has_key"));
        assertEquals(100, flags.get("gold"));
    }

    @Test
    void testUpdateFlags_MergesWithExisting() {
        // Arrange
        GameSession session = gameService.createGameSession("TestPlayer", "test_scene");
        Map<String, Object> initialFlags = new HashMap<>();
        initialFlags.put("torch", true);
        gameService.updateFlags(session.getId(), initialFlags);

        Map<String, Object> newFlags = new HashMap<>();
        newFlags.put("has_key", true);

        // Act
        gameService.updateFlags(session.getId(), newFlags);

        // Assert
        Map<String, Object> flags = gameService.getPlayerFlags(session.getId());
        assertTrue(flags.containsKey("torch"));
        assertTrue(flags.containsKey("has_key"));
        assertEquals(true, flags.get("torch"));
        assertEquals(true, flags.get("has_key"));
    }

    @Test
    void testUpdateFlags_GameEnded_ThrowsException() {
        // Arrange
        Scene terminalScene = new Scene("terminal", "The End", "Game over", true);
        sceneRepository.save(terminalScene);
        GameSession session = gameService.createGameSession("TestPlayer", "terminal");

        Map<String, Object> newFlags = new HashMap<>();
        newFlags.put("test", true);

        // Act & Assert
        assertThrows(GameEndedException.class, () -> {
            gameService.updateFlags(session.getId(), newFlags);
        });
    }

    @Test
    void testGetPlayerFlags_ReturnsParsedFlags() {
        // Arrange
        GameSession session = gameService.createGameSession("TestPlayer", "test_scene");
        Map<String, Object> flags = new HashMap<>();
        flags.put("flag1", true);
        flags.put("flag2", false);
        flags.put("number", 42);
        gameService.updateFlags(session.getId(), flags);

        // Act
        Map<String, Object> retrievedFlags = gameService.getPlayerFlags(session.getId());

        // Assert
        assertEquals(3, retrievedFlags.size());
        assertEquals(true, retrievedFlags.get("flag1"));
        assertEquals(false, retrievedFlags.get("flag2"));
        assertEquals(42, retrievedFlags.get("number"));
    }

    @Test
    void testIsGameEnded_NonTerminalScene_ReturnsFalse() {
        // Arrange
        GameSession session = gameService.createGameSession("TestPlayer", "test_scene");

        // Act
        boolean isEnded = gameService.isGameEnded(session.getId());

        // Assert
        assertFalse(isEnded);
    }

    @Test
    void testIsGameEnded_TerminalScene_ReturnsTrue() {
        // Arrange
        Scene terminalScene = new Scene("terminal", "The End", "Game over", true);
        sceneRepository.save(terminalScene);
        GameSession session = gameService.createGameSession("TestPlayer", "terminal");

        // Act
        boolean isEnded = gameService.isGameEnded(session.getId());

        // Assert
        assertTrue(isEnded);
    }

    @Test
    void testEndGameSession_Success() {
        // Arrange
        GameSession session = gameService.createGameSession("TestPlayer", "test_scene");
        Long sessionId = session.getId();

        // Act
        gameService.endGameSession(sessionId);

        // Assert
        assertThrows(GameSessionNotFoundException.class, () -> {
            gameService.getGameSession(sessionId);
        });
    }
}

