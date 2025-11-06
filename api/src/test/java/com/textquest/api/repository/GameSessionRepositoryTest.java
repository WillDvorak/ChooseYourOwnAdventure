package com.textquest.api.repository;

import com.textquest.api.entity.GameSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.flyway.enabled=false"
})
@Transactional
class GameSessionRepositoryTest {

    @Autowired
    private GameSessionRepository gameSessionRepository;

    @BeforeEach
    void setUp() {
        gameSessionRepository.deleteAll();
    }

    @Test
    void testSaveGameSession() {
        // Test saving a new game session
        GameSession session = new GameSession("TestPlayer", "intro", "{\"torch\": true}");
        GameSession savedSession = gameSessionRepository.save(session);
        
        assertNotNull(savedSession.getId());
        assertEquals("TestPlayer", savedSession.getPlayerName());
        assertEquals("intro", savedSession.getCurrentSceneCode());
        assertEquals("{\"torch\": true}", savedSession.getFlagsJson());
        assertNotNull(savedSession.getCreatedAt());
        assertNotNull(savedSession.getUpdatedAt());
    }

    @Test
    void testFindByPlayerName() {
        // Test finding game sessions by player name
        GameSession session1 = new GameSession("Player1", "intro", "{\"torch\": true}");
        GameSession session2 = new GameSession("Player1", "scene2", "{\"gold\": true}");
        GameSession session3 = new GameSession("Player2", "intro", "{\"torch\": false}");
        
        gameSessionRepository.save(session1);
        gameSessionRepository.save(session2);
        gameSessionRepository.save(session3);
        
        List<GameSession> player1Sessions = gameSessionRepository.findByPlayerName("Player1");
        assertEquals(2, player1Sessions.size());
        assertTrue(player1Sessions.stream().allMatch(s -> s.getPlayerName().equals("Player1")));
    }

    @Test
    void testFindLatestByPlayerName() {
        // Test finding the most recent game session for a player
        GameSession session1 = new GameSession("Player1", "intro", "{\"torch\": true}");
        GameSession savedSession1 = gameSessionRepository.save(session1);
        
        // Wait a moment to ensure different timestamps
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        GameSession session2 = new GameSession("Player1", "scene2", "{\"gold\": true}");
        GameSession savedSession2 = gameSessionRepository.save(session2);
        
        Optional<GameSession> latestSession = gameSessionRepository.findLatestByPlayerName("Player1");
        assertTrue(latestSession.isPresent());
        assertEquals("scene2", latestSession.get().getCurrentSceneCode());
        assertEquals(savedSession2.getId(), latestSession.get().getId());
    }

    @Test
    void testFindByCurrentSceneCode() {
        // Test finding game sessions by current scene code
        GameSession session1 = new GameSession("Player1", "intro", "{\"torch\": true}");
        GameSession session2 = new GameSession("Player2", "intro", "{\"torch\": false}");
        GameSession session3 = new GameSession("Player3", "scene2", "{\"gold\": true}");
        
        gameSessionRepository.save(session1);
        gameSessionRepository.save(session2);
        gameSessionRepository.save(session3);
        
        List<GameSession> introSessions = gameSessionRepository.findByCurrentSceneCode("intro");
        assertEquals(2, introSessions.size());
        assertTrue(introSessions.stream().allMatch(s -> s.getCurrentSceneCode().equals("intro")));
    }

    @Test
    void testExistsByPlayerName() {
        // Test checking if a player has an active game session
        GameSession session = new GameSession("TestPlayer", "intro", "{\"torch\": true}");
        gameSessionRepository.save(session);
        
        assertTrue(gameSessionRepository.existsByPlayerName("TestPlayer"));
        assertFalse(gameSessionRepository.existsByPlayerName("NonExistentPlayer"));
    }

    @Test
    void testCountByPlayerName() {
        // Test counting game sessions by player name
        GameSession session1 = new GameSession("Player1", "intro", "{\"torch\": true}");
        GameSession session2 = new GameSession("Player1", "scene2", "{\"gold\": true}");
        GameSession session3 = new GameSession("Player1", "scene3", "{\"sword\": true}");
        GameSession session4 = new GameSession("Player2", "intro", "{\"torch\": false}");
        
        gameSessionRepository.save(session1);
        gameSessionRepository.save(session2);
        gameSessionRepository.save(session3);
        gameSessionRepository.save(session4);
        
        long player1Count = gameSessionRepository.countByPlayerName("Player1");
        assertEquals(3, player1Count);
        
        long player2Count = gameSessionRepository.countByPlayerName("Player2");
        assertEquals(1, player2Count);
    }

    @Test
    void testFindCreatedAfter() {
        // Test finding game sessions created after a specific date
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(1);
        
        GameSession session1 = new GameSession("Player1", "intro", "{\"torch\": true}");
        GameSession session2 = new GameSession("Player2", "intro", "{\"torch\": false}");
        
        gameSessionRepository.save(session1);
        gameSessionRepository.save(session2);
        
        List<GameSession> recentSessions = gameSessionRepository.findCreatedAfter(cutoffTime);
        assertTrue(recentSessions.size() >= 2);
        assertTrue(recentSessions.stream().allMatch(s -> 
            s.getCreatedAt().isAfter(cutoffTime)));
    }

    @Test
    void testFindUpdatedAfter() {
        // Test finding game sessions updated after a specific date
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(1);
        
        GameSession session1 = new GameSession("Player1", "intro", "{\"torch\": true}");
        GameSession session2 = new GameSession("Player2", "intro", "{\"torch\": false}");
        
        gameSessionRepository.save(session1);
        gameSessionRepository.save(session2);
        
        // Update one session
        session1.setCurrentSceneCode("scene2");
        gameSessionRepository.save(session1);
        
        List<GameSession> recentSessions = gameSessionRepository.findUpdatedAfter(cutoffTime);
        assertTrue(recentSessions.size() >= 2);
        assertTrue(recentSessions.stream().allMatch(s -> 
            s.getUpdatedAt().isAfter(cutoffTime)));
    }

    @Test
    void testFindAllOrderByUpdatedAtDesc() {
        // Test finding all game sessions ordered by most recently updated
        GameSession session1 = new GameSession("Player1", "intro", "{\"torch\": true}");
        GameSession session2 = new GameSession("Player2", "intro", "{\"torch\": false}");
        GameSession session3 = new GameSession("Player3", "scene2", "{\"gold\": true}");
        
        GameSession savedSession1 = gameSessionRepository.save(session1);
        
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        GameSession savedSession2 = gameSessionRepository.save(session2);
        
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        GameSession savedSession3 = gameSessionRepository.save(session3);
        
        List<GameSession> allSessions = gameSessionRepository.findAllOrderByUpdatedAtDesc();
        assertTrue(allSessions.size() >= 3);
        
        // Verify ordering (most recent first)
        for (int i = 0; i < allSessions.size() - 1; i++) {
            assertTrue(allSessions.get(i).getUpdatedAt()
                .isAfter(allSessions.get(i + 1).getUpdatedAt()) ||
                allSessions.get(i).getUpdatedAt()
                .isEqual(allSessions.get(i + 1).getUpdatedAt()));
        }
    }

    @Test
    void testUpdateGameSession() {
        // Test updating an existing game session
        GameSession session = new GameSession("UpdatePlayer", "intro", "{\"torch\": true}");
        GameSession savedSession = gameSessionRepository.save(session);
        
        savedSession.setPlayerName("UpdatedPlayer");
        savedSession.setCurrentSceneCode("scene2");
        savedSession.setFlagsJson("{\"torch\": true, \"gold\": true}");
        
        GameSession updatedSession = gameSessionRepository.save(savedSession);
        assertEquals("UpdatedPlayer", updatedSession.getPlayerName());
        assertEquals("scene2", updatedSession.getCurrentSceneCode());
        assertEquals("{\"torch\": true, \"gold\": true}", updatedSession.getFlagsJson());
        assertNotNull(updatedSession.getUpdatedAt());
    }
}
