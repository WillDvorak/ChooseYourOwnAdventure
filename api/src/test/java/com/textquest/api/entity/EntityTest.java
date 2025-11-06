package com.textquest.api.entity;

import com.textquest.api.repository.ChoiceRepository;
import com.textquest.api.repository.GameSessionRepository;
import com.textquest.api.repository.SceneRepository;
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
class EntityTest {

    @Autowired
    private SceneRepository sceneRepository;

    @Autowired
    private ChoiceRepository choiceRepository;

    @Autowired
    private GameSessionRepository gameSessionRepository;

    @Test
    void testSceneEntity() {
        Scene scene = new Scene("test_scene", "Test Scene", "This is a test scene body", false);
        Scene savedScene = sceneRepository.save(scene);

        assertNotNull(savedScene.getId());
        assertEquals("test_scene", savedScene.getCode());
        assertEquals("Test Scene", savedScene.getTitle());
        assertEquals("This is a test scene body", savedScene.getBody());
        assertFalse(savedScene.getIsTerminal());
        assertNotNull(savedScene.getCreatedAt());
        assertNotNull(savedScene.getUpdatedAt());

        Optional<Scene> foundScene = sceneRepository.findByCode("test_scene");
        assertTrue(foundScene.isPresent());
        assertEquals("test_scene", foundScene.get().getCode());
    }

    @Test
    void testChoiceEntity() {
        Scene scene = new Scene("parent_scene", "Parent Scene", "Parent scene body", false);
        Scene savedScene = sceneRepository.save(scene);

        Choice choice = new Choice(savedScene, "Go to test", "test_scene", null, "test_flag");
        Choice savedChoice = choiceRepository.save(choice);

        assertNotNull(savedChoice.getId());
        assertEquals("Go to test", savedChoice.getLabel());
        assertEquals("test_scene", savedChoice.getTargetSceneCode());
        assertNull(savedChoice.getRequiresFlag());
        assertEquals("test_flag", savedChoice.getSetsFlag());
        assertNotNull(savedChoice.getCreatedAt());
        assertNotNull(savedChoice.getUpdatedAt());

        List<Choice> choices = choiceRepository.findBySceneCode("parent_scene");
        assertEquals(1, choices.size());
        assertEquals("Go to test", choices.get(0).getLabel());
    }

    @Test
    void testGameSessionEntity() {
        GameSession gameSession = new GameSession("TestPlayer", "intro", "{\"torch\": true, \"gold\": false}");
        GameSession savedSession = gameSessionRepository.save(gameSession);

        assertNotNull(savedSession.getId());
        assertEquals("TestPlayer", savedSession.getPlayerName());
        assertEquals("intro", savedSession.getCurrentSceneCode());
        assertEquals("{\"torch\": true, \"gold\": false}", savedSession.getFlagsJson());
        assertNotNull(savedSession.getCreatedAt());
        assertNotNull(savedSession.getUpdatedAt());

        List<GameSession> sessions = gameSessionRepository.findByPlayerName("TestPlayer");
        assertEquals(1, sessions.size());
        assertEquals("TestPlayer", sessions.get(0).getPlayerName());
    }

    @Test
    void testSceneChoiceRelationship() {
        Scene scene = new Scene("relationship_test", "Relationship Test", "Testing relationships", false);
        Scene savedScene = sceneRepository.save(scene);

        Choice choice1 = new Choice(savedScene, "Choice 1", "scene1", null, "flag1");
        Choice choice2 = new Choice(savedScene, "Choice 2", "scene2", "flag1", "flag2");
        choiceRepository.save(choice1);
        choiceRepository.save(choice2);

        List<Choice> choices = choiceRepository.findBySceneCode("relationship_test");
        assertEquals(2, choices.size());
        assertTrue(choices.stream().anyMatch(c -> c.getLabel().equals("Choice 1")));
        assertTrue(choices.stream().anyMatch(c -> c.getLabel().equals("Choice 2")));
    }

    @Test
    void testRepositoryQueries() {
        Scene scene1 = new Scene("scene1", "Scene 1", "First scene", false);
        Scene scene2 = new Scene("scene2", "Scene 2", "Second scene", true);
        sceneRepository.save(scene1);
        sceneRepository.save(scene2);

        GameSession session1 = new GameSession("Player1", "scene1", "{\"torch\": true}");
        GameSession session2 = new GameSession("Player2", "scene2", "{\"gold\": true}");
        gameSessionRepository.save(session1);
        gameSessionRepository.save(session2);

        List<Scene> terminalScenes = sceneRepository.findByIsTerminalTrue();
        assertTrue(terminalScenes.size() >= 1);
        assertTrue(terminalScenes.stream().anyMatch(s -> s.getCode().equals("scene2")));

        List<Scene> nonTerminalScenes = sceneRepository.findByIsTerminalFalse();
        assertTrue(nonTerminalScenes.size() >= 1);
        assertTrue(nonTerminalScenes.stream().anyMatch(s -> s.getCode().equals("scene1")));

        List<GameSession> player1Sessions = gameSessionRepository.findByPlayerName("Player1");
        assertEquals(1, player1Sessions.size());
        assertEquals("scene1", player1Sessions.get(0).getCurrentSceneCode());

        List<GameSession> scene2Sessions = gameSessionRepository.findByCurrentSceneCode("scene2");
        assertEquals(1, scene2Sessions.size());
        assertEquals("Player2", scene2Sessions.get(0).getPlayerName());
    }

    @Test
    void testEntityValidation() {
        Scene scene = new Scene();
        scene.setCode("test");
        scene.setTitle("Test Title");
        scene.setBody("Test Body");

        Scene savedScene = sceneRepository.save(scene);
        assertNotNull(savedScene.getId());
        assertEquals("test", savedScene.getCode());
        assertEquals("Test Title", savedScene.getTitle());
        assertEquals("Test Body", savedScene.getBody());
    }


    @Test
    void testSceneUpdateTimestamps() throws InterruptedException {
        Scene scene = new Scene("update_test", "Initial Title", "Initial Body", false);
        Scene saved = sceneRepository.save(scene);

        LocalDateTime originalUpdatedAt = saved.getUpdatedAt();
        Thread.sleep(10); // ensure timestamp difference
        saved.setTitle("Updated Title");
        Scene updated = sceneRepository.save(saved);

        assertTrue(updated.getUpdatedAt().isAfter(originalUpdatedAt));
        assertEquals("Updated Title", updated.getTitle());
    }

    @Test
    void testChoiceTargetSceneUpdate() {
        Scene parent = new Scene("choice_parent", "Choice Parent", "Body", false);
        sceneRepository.save(parent);

        Choice choice = new Choice(parent, "Old Choice", "old_scene", null, null);
        choiceRepository.save(choice);

        choice.setTargetSceneCode("new_scene");
        Choice updated = choiceRepository.save(choice);

        assertEquals("new_scene", updated.getTargetSceneCode());
    }

    @Test
    void testDeleteSceneDoesNotCascadeToChoices() {
        Scene scene = new Scene("cascade_test", "Cascade Test", "Body", false);
        Scene savedScene = sceneRepository.save(scene);

        Choice choice = new Choice(savedScene, "Delete Test", "other_scene", null, null);
        choiceRepository.save(choice);

        sceneRepository.delete(savedScene);

        List<Choice> remainingChoices = choiceRepository.findAll();
        assertFalse(remainingChoices.isEmpty(), "Choices should not be deleted when scene is removed");
    }

    @Test
    void testFindNonexistentEntities() {
        Optional<Scene> missingScene = sceneRepository.findByCode("does_not_exist");
        List<GameSession> missingSession = gameSessionRepository.findByPlayerName("NoSuchPlayer");
        List<Choice> missingChoices = choiceRepository.findBySceneCode("no_scene");

        assertTrue(missingScene.isEmpty());
        assertTrue(missingSession.isEmpty());
        assertTrue(missingChoices.isEmpty());
    }

    @Test
    void testGameSessionUpdateSceneAndFlags() {
        GameSession session = new GameSession("UpdatePlayer", "scene_old", "{\"key\": false}");
        gameSessionRepository.save(session);

        session.setCurrentSceneCode("scene_new");
        session.setFlagsJson("{\"key\": true}");
        GameSession updated = gameSessionRepository.save(session);

        assertEquals("scene_new", updated.getCurrentSceneCode());
        assertEquals("{\"key\": true}", updated.getFlagsJson());
    }

    @Test
    void testMultipleSessionsPerPlayer() {
        GameSession s1 = new GameSession("MultiPlayer", "intro", "{}");
        GameSession s2 = new GameSession("MultiPlayer", "second", "{}");
        gameSessionRepository.save(s1);
        gameSessionRepository.save(s2);

        List<GameSession> sessions = gameSessionRepository.findByPlayerName("MultiPlayer");
        assertEquals(2, sessions.size());
        assertTrue(sessions.stream().anyMatch(s -> s.getCurrentSceneCode().equals("intro")));
        assertTrue(sessions.stream().anyMatch(s -> s.getCurrentSceneCode().equals("second")));
    }
}
