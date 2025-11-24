package com.textquest.api.repository;

import com.textquest.api.entity.Scene;
import org.junit.jupiter.api.BeforeEach;
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
class SceneRepositoryTest {

    @Autowired
    private SceneRepository sceneRepository;

    @BeforeEach
    void setUp() {
        sceneRepository.deleteAll();
    }

    @Test
    void testSaveScene() {
        // Test saving a new scene
        Scene scene = new Scene("test_save", "Test Save", "Test body content", false);
        Scene savedScene = sceneRepository.save(scene);
        
        assertNotNull(savedScene.getId());
        assertEquals("test_save", savedScene.getCode());
        assertEquals("Test Save", savedScene.getTitle());
        assertEquals("Test body content", savedScene.getBody());
        assertFalse(savedScene.getIsTerminal());
        assertNotNull(savedScene.getCreatedAt());
        assertNotNull(savedScene.getUpdatedAt());
    }

    @Test
    void testFindByCode() {
        // Test finding a scene by its unique code
        Scene scene = new Scene("find_by_code", "Find By Code", "Test body", false);
        sceneRepository.save(scene);
        
        Optional<Scene> foundScene = sceneRepository.findByCode("find_by_code");
        assertTrue(foundScene.isPresent());
        assertEquals("find_by_code", foundScene.get().getCode());
        assertEquals("Find By Code", foundScene.get().getTitle());
    }

    @Test
    void testFindByIsTerminalTrue() {
        // Test finding all terminal scenes
        Scene terminal1 = new Scene("terminal1", "Terminal 1", "End scene 1", true);
        Scene terminal2 = new Scene("terminal2", "Terminal 2", "End scene 2", true);
        Scene nonTerminal = new Scene("non_terminal", "Non Terminal", "Not end", false);
        
        sceneRepository.save(terminal1);
        sceneRepository.save(terminal2);
        sceneRepository.save(nonTerminal);
        
        List<Scene> terminalScenes = sceneRepository.findByIsTerminalTrue();
        assertEquals(2, terminalScenes.size());
        assertTrue(terminalScenes.stream().allMatch(Scene::getIsTerminal));
    }

    @Test
    void testFindByIsTerminalFalse() {
        // Test finding all non-terminal scenes
        Scene terminal = new Scene("terminal", "Terminal", "End scene", true);
        Scene nonTerminal1 = new Scene("non_terminal1", "Non Terminal 1", "Not end 1", false);
        Scene nonTerminal2 = new Scene("non_terminal2", "Non Terminal 2", "Not end 2", false);
        
        sceneRepository.save(terminal);
        sceneRepository.save(nonTerminal1);
        sceneRepository.save(nonTerminal2);
        
        List<Scene> nonTerminalScenes = sceneRepository.findByIsTerminalFalse();
        assertEquals(2, nonTerminalScenes.size());
        assertTrue(nonTerminalScenes.stream().noneMatch(Scene::getIsTerminal));
    }

    @Test
    void testExistsByCode() {
        // Test checking if a scene exists by code
        Scene scene = new Scene("exists_test", "Exists Test", "Test body", false);
        sceneRepository.save(scene);
        
        assertTrue(sceneRepository.existsByCode("exists_test"));
        assertFalse(sceneRepository.existsByCode("does_not_exist"));
    }

    @Test
    void testFindByTitleContainingIgnoreCase() {
        // Test finding scenes by title containing text (case-insensitive)
        Scene scene1 = new Scene("scene1", "Adventure Quest", "Body 1", false);
        Scene scene2 = new Scene("scene2", "Dark Adventure", "Body 2", false);
        Scene scene3 = new Scene("scene3", "Quest for Glory", "Body 3", false);
        Scene scene4 = new Scene("scene4", "Mystery Story", "Body 4", false);
        
        sceneRepository.save(scene1);
        sceneRepository.save(scene2);
        sceneRepository.save(scene3);
        sceneRepository.save(scene4);
        
        List<Scene> adventureScenes = sceneRepository.findByTitleContainingIgnoreCase("adventure");
        assertEquals(2, adventureScenes.size());
        assertTrue(adventureScenes.stream().anyMatch(s -> s.getCode().equals("scene1")));
        assertTrue(adventureScenes.stream().anyMatch(s -> s.getCode().equals("scene2")));
        
        // Test case-insensitive
        List<Scene> questScenes = sceneRepository.findByTitleContainingIgnoreCase("QUEST");
        assertEquals(2, questScenes.size());
    }

    @Test
    void testFindByBodyContainingIgnoreCase() {
        // Test finding scenes by body containing text (case-insensitive)
        Scene scene1 = new Scene("scene1", "Title 1", "You find a magical sword", false);
        Scene scene2 = new Scene("scene2", "Title 2", "The sword glows brightly", false);
        Scene scene3 = new Scene("scene3", "Title 3", "You see a dragon", false);
        
        sceneRepository.save(scene1);
        sceneRepository.save(scene2);
        sceneRepository.save(scene3);
        
        List<Scene> swordScenes = sceneRepository.findByBodyContainingIgnoreCase("sword");
        assertEquals(2, swordScenes.size());
        assertTrue(swordScenes.stream().anyMatch(s -> s.getCode().equals("scene1")));
        assertTrue(swordScenes.stream().anyMatch(s -> s.getCode().equals("scene2")));
    }

    @Test
    void testFindByCodeWithChoices() {
        // Test custom query to find scene with choices loaded
        Scene scene = new Scene("scene_with_choices", "Scene With Choices", "Test body", false);
        Scene savedScene = sceneRepository.save(scene);
        
        Optional<Scene> sceneWithChoices = sceneRepository.findByCodeWithChoices("scene_with_choices");
        assertTrue(sceneWithChoices.isPresent());
        assertEquals("scene_with_choices", sceneWithChoices.get().getCode());
        assertNotNull(sceneWithChoices.get().getChoices());
    }

    @Test
    void testFindAllWithChoices() {
        // Test finding all scenes with choices loaded
        Scene scene1 = new Scene("scene1", "Scene 1", "Body 1", false);
        Scene scene2 = new Scene("scene2", "Scene 2", "Body 2", false);
        Scene scene3 = new Scene("scene3", "Scene 3", "Body 3", true);
        
        sceneRepository.save(scene1);
        sceneRepository.save(scene2);
        sceneRepository.save(scene3);
        
        List<Scene> allScenesWithChoices = sceneRepository.findAllWithChoices();
        assertEquals(3, allScenesWithChoices.size());
        allScenesWithChoices.forEach(scene -> {
            assertNotNull(scene.getChoices());
        });
    }

    @Test
    void testUpdateScene() {
        // Test updating an existing scene
        Scene scene = new Scene("update_test", "Original Title", "Original body", false);
        Scene savedScene = sceneRepository.save(scene);
        
        savedScene.setTitle("Updated Title");
        savedScene.setBody("Updated body");
        savedScene.setIsTerminal(true);
        
        Scene updatedScene = sceneRepository.save(savedScene);
        assertEquals("Updated Title", updatedScene.getTitle());
        assertEquals("Updated body", updatedScene.getBody());
        assertTrue(updatedScene.getIsTerminal());
        assertNotNull(updatedScene.getUpdatedAt());
    }
}
