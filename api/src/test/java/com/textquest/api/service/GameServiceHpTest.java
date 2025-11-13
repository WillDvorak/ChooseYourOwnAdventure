package com.textquest.api.service;

import com.textquest.api.entity.GameSession;
import com.textquest.api.entity.Scene;
import com.textquest.api.exception.GameEndedException;
import com.textquest.api.repository.GameSessionRepository;
import com.textquest.api.repository.SceneRepository;
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
        // Create a test scene
        testScene = new Scene("test_scene", "Test Scene", "This is a test scene", false);
        testScene = sceneRepository.save(testScene);

        // Create a test game session
        testSession = new GameSession("TestPlayer", "test_scene");
        testSession = gameSessionRepository.save(testSession);
    }

    @Test
    void testHpInitialization() {
        // Test that new game sessions start with 100 HP
        GameSession session = gameService.createGameSession("NewPlayer", "test_scene");
        
        assertNotNull(session);
        assertEquals(100, session.getHp(), "New sessions should start with 100 HP");
        assertEquals(100, session.getMaxHp(), "New sessions should start with 100 max HP");
    }

    @Test
    void testModifyHp_Damage() {
        // Test taking damage
        GameSession updated = gameService.modifyHp(testSession.getId(), -25);
        
        assertEquals(75, updated.getHp(), "HP should decrease by 25");
        assertEquals(100, updated.getMaxHp(), "Max HP should remain unchanged");
    }

    @Test
    void testModifyHp_Healing() {
        // First damage the player
        gameService.modifyHp(testSession.getId(), -30);
        
        // Then heal
        GameSession updated = gameService.modifyHp(testSession.getId(), 20);
        
        assertEquals(90, updated.getHp(), "HP should be 70 + 20 = 90");
    }

    @Test
    void testModifyHp_CannotExceedMaxHp() {
        // Try to heal beyond max HP
        GameSession updated = gameService.modifyHp(testSession.getId(), 50);
        
        assertEquals(100, updated.getHp(), "HP should not exceed max HP of 100");
        assertEquals(100, updated.getMaxHp());
    }

    @Test
    void testModifyHp_CannotGoBelowZero() {
        // Try to deal massive damage
        GameSession updated = gameService.modifyHp(testSession.getId(), -200);
        
        assertEquals(0, updated.getHp(), "HP should not go below 0");
        assertTrue(gameService.isPlayerDead(testSession.getId()), "Player should be dead");
    }

    @Test
    void testModifyHp_ExactDeath() {
        // Deal exactly 100 damage
        GameSession updated = gameService.modifyHp(testSession.getId(), -100);
        
        assertEquals(0, updated.getHp(), "HP should be exactly 0");
        assertTrue(gameService.isPlayerDead(testSession.getId()), "Player should be dead at 0 HP");
    }

    @Test
    void testSetHp_ValidValue() {
        // Set HP to a specific value
        GameSession updated = gameService.setHp(testSession.getId(), 50);
        
        assertEquals(50, updated.getHp());
        assertEquals(100, updated.getMaxHp());
    }

    @Test
    void testSetHp_ClampedToMax() {
        // Try to set HP above max
        GameSession updated = gameService.setHp(testSession.getId(), 150);
        
        assertEquals(100, updated.getHp(), "HP should be clamped to max HP");
    }

    @Test
    void testSetHp_ClampedToZero() {
        // Try to set HP to negative
        GameSession updated = gameService.setHp(testSession.getId(), -10);
        
        assertEquals(0, updated.getHp(), "HP should be clamped to 0");
        assertTrue(gameService.isPlayerDead(testSession.getId()));
    }

    @Test
    void testSetMaxHp_Increase() {
        // Increase max HP
        GameSession updated = gameService.setMaxHp(testSession.getId(), 150, false);
        
        assertEquals(150, updated.getMaxHp(), "Max HP should be increased to 150");
        assertEquals(100, updated.getHp(), "Current HP should remain 100");
    }

    @Test
    void testSetMaxHp_IncreaseWithProportionalScaling() {
        // First reduce HP
        gameService.setHp(testSession.getId(), 50);
        
        // Increase max HP with proportional scaling
        GameSession updated = gameService.setMaxHp(testSession.getId(), 200, true);
        
        assertEquals(200, updated.getMaxHp(), "Max HP should be 200");
        assertEquals(100, updated.getHp(), "HP should scale proportionally: 50 * (200/100) = 100");
    }

    @Test
    void testSetMaxHp_DecreaseWithScaling() {
        // Set HP to 80
        gameService.setHp(testSession.getId(), 80);
        
        // Decrease max HP with scaling
        GameSession updated = gameService.setMaxHp(testSession.getId(), 50, true);
        
        assertEquals(50, updated.getMaxHp());
        assertEquals(40, updated.getHp(), "HP should scale: 80 * (50/100) = 40");
    }

    @Test
    void testSetMaxHp_CurrentHpExceedsNewMax() {
        // Set HP to 100
        gameService.setHp(testSession.getId(), 100);
        
        // Decrease max HP without scaling
        GameSession updated = gameService.setMaxHp(testSession.getId(), 50, false);
        
        assertEquals(50, updated.getMaxHp());
        assertEquals(50, updated.getHp(), "HP should be clamped to new max when exceeding");
    }

    @Test
    void testSetMaxHp_InvalidMaxHp() {
        // Try to set max HP to 0 or negative
        assertThrows(IllegalArgumentException.class, () -> {
            gameService.setMaxHp(testSession.getId(), 0, false);
        }, "Should throw exception for max HP <= 0");
        
        assertThrows(IllegalArgumentException.class, () -> {
            gameService.setMaxHp(testSession.getId(), -10, false);
        }, "Should throw exception for negative max HP");
    }

    @Test
    void testIsPlayerDead_Alive() {
        // Player with HP > 0 should not be dead
        assertFalse(gameService.isPlayerDead(testSession.getId()), "Player with 100 HP should be alive");
    }

    @Test
    void testIsPlayerDead_Dead() {
        // Kill the player
        gameService.setHp(testSession.getId(), 0);
        
        assertTrue(gameService.isPlayerDead(testSession.getId()), "Player with 0 HP should be dead");
    }

    @Test
    void testIsPlayerDead_AfterDamage() {
        // Damage player to death
        gameService.modifyHp(testSession.getId(), -100);
        
        assertTrue(gameService.isPlayerDead(testSession.getId()), "Player should be dead after taking 100 damage");
    }

    @Test
    void testHpModification_EndedGame() {
        // Create a terminal scene
        Scene terminalScene = new Scene("terminal", "The End", "Game over", true);
        terminalScene = sceneRepository.save(terminalScene);
        
        // Move session to terminal scene
        GameSession endedSession = gameSessionRepository.findById(testSession.getId()).orElseThrow();
        endedSession.setCurrentSceneCode("terminal");
        gameSessionRepository.save(endedSession);
        
        // Try to modify HP in ended game
        assertThrows(GameEndedException.class, () -> {
            gameService.modifyHp(testSession.getId(), -10);
        }, "Should not allow HP changes after game ended");
    }

    @Test
    void testSetHp_EndedGame() {
        // Create a terminal scene
        Scene terminalScene = new Scene("terminal", "The End", "Game over", true);
        terminalScene = sceneRepository.save(terminalScene);
        
        // Move session to terminal scene
        GameSession endedSession = gameSessionRepository.findById(testSession.getId()).orElseThrow();
        endedSession.setCurrentSceneCode("terminal");
        gameSessionRepository.save(endedSession);
        
        // Try to set HP in ended game
        assertThrows(GameEndedException.class, () -> {
            gameService.setHp(testSession.getId(), 50);
        }, "Should not allow HP changes after game ended");
    }

   
}

