package com.textquest.api.service;

import com.textquest.api.entity.Scene;
import com.textquest.api.entity.Choice;
import com.textquest.api.repository.SceneRepository;
import com.textquest.api.repository.ChoiceRepository;
import com.textquest.api.exception.SceneNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.flyway.enabled=false"
})
@Transactional
class SceneServiceTest {

    @Autowired
    private SceneService sceneService;

    @Autowired
    private SceneRepository sceneRepository;

    @Autowired
    private ChoiceRepository choiceRepository;

    @Autowired
    private ChoiceService choiceService;

    private Scene testScene;
    private Scene terminalScene;
    private Choice availableChoice;
    private Choice restrictedChoice;

    @BeforeEach
    void setUp() {
        // Create test scenes
        testScene = new Scene("test_scene", "Test Scene", "This is a test scene", false);
        terminalScene = new Scene("terminal", "The End", "Game over", true);
        sceneRepository.save(testScene);
        sceneRepository.save(terminalScene);

        // Create test choices
        availableChoice = new Choice(testScene, "Available choice", "target1", null, null);
        restrictedChoice = new Choice(testScene, "Restricted choice", "target2", "has_key", null);
        choiceRepository.save(availableChoice);
        choiceRepository.save(restrictedChoice);
    }

    @Test
    void testGetSceneByCode_Success() {
        // Act
        Scene scene = sceneService.getSceneByCode("test_scene");

        // Assert
        assertNotNull(scene);
        assertEquals("test_scene", scene.getCode());
        assertEquals("Test Scene", scene.getTitle());
        assertEquals("This is a test scene", scene.getBody());
        assertFalse(scene.getIsTerminal());
    }

    @Test
    void testGetSceneByCode_NotFound_ThrowsException() {
        // Act & Assert
        assertThrows(SceneNotFoundException.class, () -> {
            sceneService.getSceneByCode("non_existent");
        });
    }

    @Test
    void testGetSceneWithChoices_LoadsChoices() {
        // Act
        Scene scene = sceneService.getSceneWithChoices("test_scene");

        // Assert
        assertNotNull(scene);
        assertNotNull(scene.getChoices());
        assertTrue(scene.getChoices().size() >= 2);
    }

    @Test
    void testIsTerminalScene_TerminalScene_ReturnsTrue() {
        // Act
        boolean isTerminal = sceneService.isTerminalScene("terminal");

        // Assert
        assertTrue(isTerminal);
    }

    @Test
    void testIsTerminalScene_NonTerminalScene_ReturnsFalse() {
        // Act
        boolean isTerminal = sceneService.isTerminalScene("test_scene");

        // Assert
        assertFalse(isTerminal);
    }

    @Test
    void testSceneExists_ExistingScene_ReturnsTrue() {
        // Act
        boolean exists = sceneService.sceneExists("test_scene");

        // Assert
        assertTrue(exists);
    }

    @Test
    void testSceneExists_NonExistentScene_ReturnsFalse() {
        // Act
        boolean exists = sceneService.sceneExists("non_existent");

        // Assert
        assertFalse(exists);
    }

    @Test
    void testValidateSceneExists_ExistingScene_DoesNotThrow() {
        // Act & Assert
        assertDoesNotThrow(() -> {
            sceneService.validateSceneExists("test_scene");
        });
    }

    @Test
    void testValidateSceneExists_NonExistentScene_ThrowsException() {
        // Act & Assert
        assertThrows(SceneNotFoundException.class, () -> {
            sceneService.validateSceneExists("non_existent");
        });
    }

    @Test
    void testGetAvailableChoices_FiltersByFlags() {
        // Arrange
        Map<String, Object> flags = new HashMap<>();
        flags.put("has_key", true);

        // Act
        List<Choice> availableChoices = sceneService.getAvailableChoices("test_scene", flags);

        // Assert - should include both choices since player has the key
        assertTrue(availableChoices.size() >= 2);
        assertTrue(availableChoices.stream().anyMatch(c -> c.getLabel().equals("Available choice")));
        assertTrue(availableChoices.stream().anyMatch(c -> c.getLabel().equals("Restricted choice")));
    }

    @Test
    void testGetAvailableChoices_WithoutRequiredFlag_FiltersRestricted() {
        // Arrange
        Map<String, Object> flags = new HashMap<>();

        // Act
        List<Choice> availableChoices = sceneService.getAvailableChoices("test_scene", flags);

        // Assert - should only include choice with no requirements
        assertTrue(availableChoices.size() >= 1);
        assertTrue(availableChoices.stream().anyMatch(c -> c.getLabel().equals("Available choice")));
        assertFalse(availableChoices.stream().anyMatch(c -> c.getLabel().equals("Restricted choice")));
    }

    @Test
    void testGetAllChoicesForScene_ReturnsAllChoices() {
        // Act
        List<Choice> allChoices = sceneService.getAllChoicesForScene("test_scene");

        // Assert
        assertTrue(allChoices.size() >= 2);
        assertTrue(allChoices.stream().anyMatch(c -> c.getLabel().equals("Available choice")));
        assertTrue(allChoices.stream().anyMatch(c -> c.getLabel().equals("Restricted choice")));
    }

    @Test
    void testGetTerminalScenes_ReturnsOnlyTerminalScenes() {
        // Act
        List<Scene> terminalScenes = sceneService.getTerminalScenes();

        // Assert
        assertTrue(terminalScenes.size() >= 1);
        assertTrue(terminalScenes.stream().anyMatch(s -> s.getCode().equals("terminal")));
        assertTrue(terminalScenes.stream().allMatch(Scene::getIsTerminal));
    }

    @Test
    void testGetNonTerminalScenes_ReturnsOnlyNonTerminalScenes() {
        // Act
        List<Scene> nonTerminalScenes = sceneService.getNonTerminalScenes();

        // Assert
        assertTrue(nonTerminalScenes.size() >= 1);
        assertTrue(nonTerminalScenes.stream().anyMatch(s -> s.getCode().equals("test_scene")));
        assertTrue(nonTerminalScenes.stream().noneMatch(Scene::getIsTerminal));
    }

    @Test
    void testHasAvailableChoices_WithAvailableChoices_ReturnsTrue() {
        // Arrange
        Map<String, Object> flags = new HashMap<>();
        flags.put("has_key", true);

        // Act
        boolean hasChoices = sceneService.hasAvailableChoices("test_scene", flags);

        // Assert
        assertTrue(hasChoices);
    }

    @Test
    void testHasAvailableChoices_NoAvailableChoices_ReturnsFalse() {
        // Arrange
        Scene emptyScene = new Scene("empty_scene", "Empty", "No choices", false);
        sceneRepository.save(emptyScene);
        Map<String, Object> flags = new HashMap<>();

        // Act
        boolean hasChoices = sceneService.hasAvailableChoices("empty_scene", flags);

        // Assert
        assertFalse(hasChoices);
    }

    @Test
    void testGetAvailableChoiceCount_ReturnsCorrectCount() {
        // Arrange
        Map<String, Object> flags = new HashMap<>();
        flags.put("has_key", true);

        // Act
        int count = sceneService.getAvailableChoiceCount("test_scene", flags);

        // Assert
        assertTrue(count >= 2);
    }
}

