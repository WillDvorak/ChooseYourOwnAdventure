package com.textquest.api.service;

import com.textquest.api.entity.GameSession;
import com.textquest.api.entity.Scene;
import com.textquest.api.repository.GameSessionRepository;
import com.textquest.api.repository.SceneRepository;
import com.textquest.api.exception.GameSessionNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.flyway.enabled=false"
})
@Transactional
class GameServiceSaveSlotTest {

    @Autowired
    private GameService gameService;

    @Autowired
    private GameSessionRepository gameSessionRepository;

    @Autowired
    private SceneRepository sceneRepository;

    private Scene testScene;
    private Scene scene2;
    private GameSession testSession;
    private String playerName = "TestPlayer";

    @BeforeEach
    void setUp() {
        // Create test scenes
        testScene = new Scene("test_scene", "Test Scene", "This is a test scene", false);
        scene2 = new Scene("scene2", "Scene 2", "Second scene", false);
        sceneRepository.save(testScene);
        sceneRepository.save(scene2);

        // Create test session
        testSession = gameService.createGameSession(playerName, "test_scene");
    }

    @Test
    void testSaveToSlot_SavesToSlot() {
        // Act
        GameSession saved = gameService.saveToSlot(testSession.getId(), 1, "My Save", false);

        // Assert
        assertNotNull(saved.getSaveSlot());
        assertEquals(1, saved.getSaveSlot());
        assertEquals("My Save", saved.getSaveName());
        assertFalse(saved.getIsAutoSave());
    }

    @Test
    void testSaveToSlot_AutoSave_SetsFlag() {
        // Act
        GameSession saved = gameService.saveToSlot(testSession.getId(), 1, null, true);

        // Assert
        assertEquals(1, saved.getSaveSlot());
        assertTrue(saved.getIsAutoSave());
    }

    @Test
    void testSaveToSlot_WithoutName_SavesWithNullName() {
        // Act
        GameSession saved = gameService.saveToSlot(testSession.getId(), 2, null, false);

        // Assert
        assertEquals(2, saved.getSaveSlot());
        assertNull(saved.getSaveName());
    }

    @Test
    void testSaveToSlot_InvalidSlot_ThrowsException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            gameService.saveToSlot(testSession.getId(), 0, null, false);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            gameService.saveToSlot(testSession.getId(), 11, null, false);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            gameService.saveToSlot(testSession.getId(), -1, null, false);
        });
    }

    @Test
    void testSaveToSlot_OverwritesExistingSave() {
        // Arrange - Save to slot 1
        gameService.saveToSlot(testSession.getId(), 1, "First Save", false);
        
        // Create another session and save to same slot
        GameSession session2 = gameService.createGameSession(playerName, "scene2");
        
        // Act - Save second session to same slot
        GameSession saved2 = gameService.saveToSlot(session2.getId(), 1, "Second Save", false);

        // Assert - First save should be deleted, second save should exist
        List<GameSession> saves = gameService.listSaveSlots(playerName);
        assertEquals(1, saves.size());
        assertEquals("Second Save", saves.get(0).getSaveName());
        assertEquals("scene2", saves.get(0).getCurrentSceneCode());
    }

    @Test
    void testLoadFromSlot_LoadsCorrectSave() {
        // Arrange - Save to slot
        gameService.saveToSlot(testSession.getId(), 1, "Test Save", false);

        // Act
        GameSession loaded = gameService.loadFromSlot(playerName, 1);

        // Assert
        assertEquals(testSession.getId(), loaded.getId());
        assertEquals(1, loaded.getSaveSlot());
        assertEquals("Test Save", loaded.getSaveName());
        assertEquals("test_scene", loaded.getCurrentSceneCode());
    }

    @Test
    void testLoadFromSlot_InvalidSlot_ThrowsException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            gameService.loadFromSlot(playerName, 0);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            gameService.loadFromSlot(playerName, 11);
        });
    }

    @Test
    void testLoadFromSlot_NonExistentSlot_ThrowsException() {
        // Act & Assert
        assertThrows(GameSessionNotFoundException.class, () -> {
            gameService.loadFromSlot(playerName, 5);
        });
    }

    @Test
    void testListSaveSlots_ReturnsAllSaves() {
        // Arrange - Create multiple saves
        GameSession session1 = gameService.createGameSession(playerName, "test_scene");
        GameSession session2 = gameService.createGameSession(playerName, "scene2");
        GameSession session3 = gameService.createGameSession(playerName, "test_scene");
        
        gameService.saveToSlot(session1.getId(), 1, "Save 1", false);
        gameService.saveToSlot(session2.getId(), 2, "Save 2", false);
        gameService.saveToSlot(session3.getId(), 3, "Save 3", false);

        // Act
        List<GameSession> saves = gameService.listSaveSlots(playerName);

        // Assert
        assertEquals(3, saves.size());
        assertTrue(saves.stream().anyMatch(s -> s.getSaveSlot() == 1));
        assertTrue(saves.stream().anyMatch(s -> s.getSaveSlot() == 2));
        assertTrue(saves.stream().anyMatch(s -> s.getSaveSlot() == 3));
    }

    @Test
    void testListSaveSlots_EmptyList_ReturnsEmpty() {
        // Act
        List<GameSession> saves = gameService.listSaveSlots(playerName);

        // Assert
        assertTrue(saves.isEmpty());
    }

    @Test
    void testListSaveSlots_OnlyActiveSessions_ReturnsEmpty() {
        // Arrange - Create active session (not saved)
        gameService.createGameSession(playerName, "test_scene");

        // Act
        List<GameSession> saves = gameService.listSaveSlots(playerName);

        // Assert - Active sessions (saveSlot = null) should not appear
        assertTrue(saves.isEmpty());
    }

    @Test
    void testGetSaveSlotInfo_ReturnsCorrectInfo() {
        // Arrange
        gameService.saveToSlot(testSession.getId(), 1, "My Save", false);

        // Act
        GameSession info = gameService.getSaveSlotInfo(playerName, 1);

        // Assert
        assertEquals(1, info.getSaveSlot());
        assertEquals("My Save", info.getSaveName());
        assertEquals(playerName, info.getPlayerName());
        assertEquals("test_scene", info.getCurrentSceneCode());
    }

    @Test
    void testGetSaveSlotInfo_InvalidSlot_ThrowsException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            gameService.getSaveSlotInfo(playerName, 0);
        });
    }

    @Test
    void testDeleteSaveSlot_DeletesSave() {
        // Arrange
        gameService.saveToSlot(testSession.getId(), 1, "To Delete", false);

        // Act
        gameService.deleteSaveSlot(playerName, 1);

        // Assert
        assertThrows(GameSessionNotFoundException.class, () -> {
            gameService.loadFromSlot(playerName, 1);
        });
    }

    @Test
    void testDeleteSaveSlot_NonExistentSlot_ThrowsException() {
        // Act & Assert
        assertThrows(GameSessionNotFoundException.class, () -> {
            gameService.deleteSaveSlot(playerName, 5);
        });
    }

    @Test
    void testDeleteSaveSlot_InvalidSlot_ThrowsException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            gameService.deleteSaveSlot(playerName, 0);
        });
    }

    @Test
    void testGetActiveSession_ReturnsActiveSession() {
        // Arrange - Create active session (not saved)
        GameSession active = gameService.createGameSession(playerName, "test_scene");

        // Act
        java.util.Optional<GameSession> found = gameService.getActiveSession(playerName);

        // Assert
        assertTrue(found.isPresent());
        assertEquals(active.getId(), found.get().getId());
        assertNull(found.get().getSaveSlot());
    }

    @Test
    void testGetActiveSession_NoActiveSession_ReturnsEmpty() {
        // Arrange - Only saved sessions exist
        gameService.saveToSlot(testSession.getId(), 1, "Save", false);

        // Act
        java.util.Optional<GameSession> found = gameService.getActiveSession(playerName);

        // Assert
        assertFalse(found.isPresent());
    }

    @Test
    void testGetActiveSession_MultipleActiveSessions_ReturnsMostRecent() {
        // Arrange - Create multiple active sessions
        GameSession session1 = gameService.createGameSession(playerName, "test_scene");
        try {
            Thread.sleep(10); // Ensure different timestamps
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        GameSession session2 = gameService.createGameSession(playerName, "scene2");

        // Act
        java.util.Optional<GameSession> found = gameService.getActiveSession(playerName);

        // Assert - Should return most recent
        assertTrue(found.isPresent());
        assertEquals(session2.getId(), found.get().getId());
    }

    @Test
    void testCreateSessionFromSave_CreatesNewActiveSession() {
        // Arrange - Save to slot
        gameService.saveToSlot(testSession.getId(), 1, "My Save", false);

        // Act
        GameSession newSession = gameService.createSessionFromSave(playerName, 1);

        // Assert
        assertNotNull(newSession.getId());
        assertNotEquals(testSession.getId(), newSession.getId()); // Different session
        assertNull(newSession.getSaveSlot()); // Active session
        assertEquals("test_scene", newSession.getCurrentSceneCode());
        assertEquals(playerName, newSession.getPlayerName());
        
        // Original save should still exist
        GameSession originalSave = gameService.loadFromSlot(playerName, 1);
        assertEquals(testSession.getId(), originalSave.getId());
    }

    @Test
    void testCreateSessionFromSave_PreservesGameState() {
        // Arrange - Save session with flags
        Map<String, Object> flags = new java.util.HashMap<>();
        flags.put("torch", true);
        flags.put("gold", 100);
        gameService.updateFlags(testSession.getId(), flags);
        gameService.saveToSlot(testSession.getId(), 1, "Save", false);

        // Act
        GameSession newSession = gameService.createSessionFromSave(playerName, 1);

        // Assert - Flags should be preserved
        Map<String, Object> newFlags = gameService.getPlayerFlags(newSession.getId());
        assertEquals(true, newFlags.get("torch"));
        assertEquals(100, newFlags.get("gold"));
    }

    @Test
    void testGetSaveSlotCount_ReturnsCorrectCount() {
        // Arrange
        GameSession session1 = gameService.createGameSession(playerName, "test_scene");
        GameSession session2 = gameService.createGameSession(playerName, "scene2");
        
        gameService.saveToSlot(session1.getId(), 1, "Save 1", false);
        gameService.saveToSlot(session2.getId(), 2, "Save 2", false);

        // Act
        long count = gameService.getSaveSlotCount(playerName);

        // Assert
        assertEquals(2, count);
    }

    @Test
    void testGetSaveSlotCount_NoSaves_ReturnsZero() {
        // Act
        long count = gameService.getSaveSlotCount(playerName);

        // Assert
        assertEquals(0, count);
    }

    @Test
    void testSaveToSlot_MultiplePlayers_IndependentSlots() {
        // Arrange
        String player2 = "Player2";
        GameSession session2 = gameService.createGameSession(player2, "test_scene");
        
        gameService.saveToSlot(testSession.getId(), 1, "Player1 Save", false);
        gameService.saveToSlot(session2.getId(), 1, "Player2 Save", false);

        // Act
        GameSession save1 = gameService.loadFromSlot(playerName, 1);
        GameSession save2 = gameService.loadFromSlot(player2, 1);

        // Assert - Both players can have slot 1
        assertEquals("Player1 Save", save1.getSaveName());
        assertEquals("Player2 Save", save2.getSaveName());
    }

    @Test
    void testSaveToSlot_PreservesGameState() {
        // Arrange - Update session state
        gameService.updateFlags(testSession.getId(), Map.of("key", true, "progress", 50));
        testSession = gameService.getGameSession(testSession.getId());
        testSession.setCurrentSceneCode("scene2");
        gameSessionRepository.save(testSession);

        // Act
        GameSession saved = gameService.saveToSlot(testSession.getId(), 1, "Save", false);

        // Assert
        assertEquals("scene2", saved.getCurrentSceneCode());
        Map<String, Object> flags = gameService.getPlayerFlags(saved.getId());
        assertEquals(true, flags.get("key"));
        assertEquals(50, flags.get("progress"));
    }

    @Test
    void testSaveToSlot_UpdatesTimestamp() {
        // Arrange
        java.time.LocalDateTime beforeSave = testSession.getUpdatedAt();
        
        try {
            Thread.sleep(10); // Ensure different timestamp
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Act
        GameSession saved = gameService.saveToSlot(testSession.getId(), 1, "Save", false);

        // Assert
        assertTrue(saved.getUpdatedAt().isAfter(beforeSave) || saved.getUpdatedAt().equals(beforeSave));
    }

    @Test
    void testListSaveSlots_OrderedBySlotNumber() {
        // Arrange - Save in non-sequential order
        GameSession session1 = gameService.createGameSession(playerName, "test_scene");
        GameSession session2 = gameService.createGameSession(playerName, "scene2");
        GameSession session3 = gameService.createGameSession(playerName, "test_scene");
        
        gameService.saveToSlot(session3.getId(), 3, "Save 3", false);
        gameService.saveToSlot(session1.getId(), 1, "Save 1", false);
        gameService.saveToSlot(session2.getId(), 2, "Save 2", false);

        // Act
        List<GameSession> saves = gameService.listSaveSlots(playerName);

        // Assert - Should be ordered by slot number
        assertEquals(3, saves.size());
        assertEquals(1, saves.get(0).getSaveSlot());
        assertEquals(2, saves.get(1).getSaveSlot());
        assertEquals(3, saves.get(2).getSaveSlot());
    }

    @Test
    void testSaveToSlot_MaxSlots_CanSaveToAllSlots() {
        // Arrange - Save to all 10 slots
        for (int i = 1; i <= 10; i++) {
            GameSession session = gameService.createGameSession(playerName, "test_scene");
            gameService.saveToSlot(session.getId(), i, "Save " + i, false);
        }

        // Act
        List<GameSession> saves = gameService.listSaveSlots(playerName);

        // Assert
        assertEquals(10, saves.size());
        assertEquals(10, gameService.getSaveSlotCount(playerName));
    }
}

