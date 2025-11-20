package com.textquest.api.service;

import com.textquest.api.entity.GameSession;
import com.textquest.api.entity.Scene;
import com.textquest.api.repository.GameSessionRepository;
import com.textquest.api.repository.SceneRepository;
import com.textquest.api.exception.GameEndedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.flyway.enabled=false"
})
@Transactional
class GameServiceHpTest {

    @Autowired
    private GameService gameService;

    @Autowired
    private GameSessionRepository gameSessionRepository;

    @Autowired
    private SceneRepository sceneRepository;

    private Scene testScene;
    private GameSession testSession;

    @BeforeEach
    void setUp() {
        // Create test scene
        testScene = new Scene("test_scene", "Test Scene", "This is a test scene", false);
        sceneRepository.save(testScene);

        // Create test session
        testSession = gameService.createGameSession("TestPlayer", "test_scene");
    }

    @Test
    void testCreateGameSession_InitializesHpTo100() {
        // Act
        GameSession session = gameService.createGameSession("NewPlayer", "test_scene");

        // Assert
        assertNotNull(session.getId());
        assertEquals(100, session.getHp());
        assertEquals(100, session.getMaxHp());
    }

    @Test
    void testModifyHp_Healing_IncreasesHp() {
        // Arrange - Set HP to 50 first
        gameService.setHp(testSession.getId(), 50);

        // Act
        GameSession updated = gameService.modifyHp(testSession.getId(), 30);

        // Assert
        assertEquals(80, updated.getHp());
        assertEquals(100, updated.getMaxHp());
    }

    @Test
    void testModifyHp_Damage_DecreasesHp() {
        // Arrange - Start with 100 HP
        assertEquals(100, testSession.getHp());

        // Act
        GameSession updated = gameService.modifyHp(testSession.getId(), -25);

        // Assert
        assertEquals(75, updated.getHp());
        assertEquals(100, updated.getMaxHp());
    }

    @Test
    void testModifyHp_ClampsToMaxHp() {
        // Arrange - Start with 100 HP
        assertEquals(100, testSession.getHp());

        // Act - Try to heal beyond max
        GameSession updated = gameService.modifyHp(testSession.getId(), 50);

        // Assert - Should be clamped to max HP
        assertEquals(100, updated.getHp());
        assertEquals(100, updated.getMaxHp());
    }

    @Test
    void testModifyHp_ClampsToZero() {
        // Arrange - Set HP to 10
        gameService.setHp(testSession.getId(), 10);

        // Act - Try to damage below zero
        GameSession updated = gameService.modifyHp(testSession.getId(), -50);

        // Assert - Should be clamped to 0
        assertEquals(0, updated.getHp());
        assertEquals(100, updated.getMaxHp());
    }

    @Test
    void testModifyHp_GameEnded_ThrowsException() {
        // Arrange - Create terminal scene and move session there
        Scene terminalScene = new Scene("terminal", "The End", "Game over", true);
        sceneRepository.save(terminalScene);
        testSession.setCurrentSceneCode("terminal");
        gameSessionRepository.save(testSession);

        // Act & Assert
        assertThrows(GameEndedException.class, () -> {
            gameService.modifyHp(testSession.getId(), -10);
        });
    }

    @Test
    void testSetHp_SetsToSpecificValue() {
        // Act
        GameSession updated = gameService.setHp(testSession.getId(), 75);

        // Assert
        assertEquals(75, updated.getHp());
        assertEquals(100, updated.getMaxHp());
    }

    @Test
    void testSetHp_ClampsToMaxHp() {
        // Act - Try to set HP above max
        GameSession updated = gameService.setHp(testSession.getId(), 150);

        // Assert - Should be clamped to max HP
        assertEquals(100, updated.getHp());
        assertEquals(100, updated.getMaxHp());
    }

    @Test
    void testSetHp_ClampsToZero() {
        // Act - Try to set HP below zero
        GameSession updated = gameService.setHp(testSession.getId(), -10);

        // Assert - Should be clamped to 0
        assertEquals(0, updated.getHp());
        assertEquals(100, updated.getMaxHp());
    }

    @Test
    void testSetHp_GameEnded_ThrowsException() {
        // Arrange - Create terminal scene and move session there
        Scene terminalScene = new Scene("terminal", "The End", "Game over", true);
        sceneRepository.save(terminalScene);
        testSession.setCurrentSceneCode("terminal");
        gameSessionRepository.save(testSession);

        // Act & Assert
        assertThrows(GameEndedException.class, () -> {
            gameService.setHp(testSession.getId(), 50);
        });
    }

    @Test
    void testSetMaxHp_IncreasesMaxHp() {
        // Act
        GameSession updated = gameService.setMaxHp(testSession.getId(), 150, false);

        // Assert
        assertEquals(100, updated.getHp()); // Current HP unchanged
        assertEquals(150, updated.getMaxHp());
    }

    @Test
    void testSetMaxHp_WithAdjustCurrent_ScalesHp() {
        // Arrange - Set HP to 50 (half of 100)
        gameService.setHp(testSession.getId(), 50);

        // Act - Double max HP with adjustment
        GameSession updated = gameService.setMaxHp(testSession.getId(), 200, true);

        // Assert - HP should scale proportionally
        assertEquals(100, updated.getHp()); // 50 * 2 = 100
        assertEquals(200, updated.getMaxHp());
    }

    @Test
    void testSetMaxHp_WithoutAdjustCurrent_ClampsHpIfNeeded() {
        // Arrange - Set HP to 150 (impossible, but test clamping)
        testSession.setHp(150);
        testSession.setMaxHp(200);
        gameSessionRepository.save(testSession);

        // Act - Reduce max HP without adjustment
        GameSession updated = gameService.setMaxHp(testSession.getId(), 100, false);

        // Assert - HP should be clamped to new max
        assertEquals(100, updated.getHp());
        assertEquals(100, updated.getMaxHp());
    }

    @Test
    void testSetMaxHp_InvalidMaxHp_ThrowsException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            gameService.setMaxHp(testSession.getId(), 0, false);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            gameService.setMaxHp(testSession.getId(), -10, false);
        });
    }

    @Test
    void testSetMaxHp_GameEnded_ThrowsException() {
        // Arrange - Create terminal scene and move session there
        Scene terminalScene = new Scene("terminal", "The End", "Game over", true);
        sceneRepository.save(terminalScene);
        testSession.setCurrentSceneCode("terminal");
        gameSessionRepository.save(testSession);

        // Act & Assert
        assertThrows(GameEndedException.class, () -> {
            gameService.setMaxHp(testSession.getId(), 150, false);
        });
    }

    @Test
    void testIsPlayerDead_HpZero_ReturnsTrue() {
        // Arrange
        gameService.setHp(testSession.getId(), 0);

        // Act
        boolean isDead = gameService.isPlayerDead(testSession.getId());

        // Assert
        assertTrue(isDead);
    }

    @Test
    void testIsPlayerDead_HpNegative_ReturnsTrue() {
        // Arrange - Set HP to negative (shouldn't happen but test edge case)
        testSession.setHp(-5);
        gameSessionRepository.save(testSession);

        // Act
        boolean isDead = gameService.isPlayerDead(testSession.getId());

        // Assert
        assertTrue(isDead);
    }

    @Test
    void testIsPlayerDead_HpPositive_ReturnsFalse() {
        // Arrange - HP is 100 by default

        // Act
        boolean isDead = gameService.isPlayerDead(testSession.getId());

        // Assert
        assertFalse(isDead);
    }

    @Test
    void testIsPlayerDead_HpOne_ReturnsFalse() {
        // Arrange
        gameService.setHp(testSession.getId(), 1);

        // Act
        boolean isDead = gameService.isPlayerDead(testSession.getId());

        // Assert
        assertFalse(isDead);
    }

    @Test
    void testHpPersistence_ModifyHp_PersistsCorrectly() {
        // Act
        gameService.modifyHp(testSession.getId(), -30);
        GameSession retrieved = gameService.getGameSession(testSession.getId());

        // Assert
        assertEquals(70, retrieved.getHp());
        assertEquals(100, retrieved.getMaxHp());
    }

    @Test
    void testHpPersistence_SetMaxHp_PersistsCorrectly() {
        // Act
        gameService.setMaxHp(testSession.getId(), 200, false);
        GameSession retrieved = gameService.getGameSession(testSession.getId());

        // Assert
        assertEquals(100, retrieved.getHp());
        assertEquals(200, retrieved.getMaxHp());
    }

    @Test
    void testMultipleHpOperations_SequentialChanges() {
        // Act - Multiple operations
        gameService.modifyHp(testSession.getId(), -20); // 80
        gameService.modifyHp(testSession.getId(), -30); // 50
        gameService.modifyHp(testSession.getId(), 10);  // 60
        gameService.setMaxHp(testSession.getId(), 150, true); // Scale HP proportionally
        GameSession final = gameService.getGameSession(testSession.getId());

        // Assert
        assertEquals(90, final.getHp()); // 60 * 1.5 = 90
        assertEquals(150, final.getMaxHp());
    }
}
