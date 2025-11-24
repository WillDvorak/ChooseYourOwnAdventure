package com.textquest.api.service;

import com.textquest.api.entity.Choice;
import com.textquest.api.entity.Scene;
import com.textquest.api.repository.ChoiceRepository;
import com.textquest.api.repository.SceneRepository;
import com.textquest.api.exception.InvalidChoiceException;
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
class ChoiceServiceTest {

    @Autowired
    private ChoiceService choiceService;

    @Autowired
    private ChoiceRepository choiceRepository;

    @Autowired
    private SceneRepository sceneRepository;

    private Scene testScene;
    private Choice choiceWithNoRequirements;
    private Choice choiceWithRequirement;
    private Choice choiceThatSetsFlag;

    @BeforeEach
    void setUp() {
        // Create test scene
        testScene = new Scene("test_scene", "Test Scene", "Test scene body", false);
        sceneRepository.save(testScene);

        // Create test choices
        choiceWithNoRequirements = new Choice(testScene, "Free choice", "target1", null, null);
        choiceWithRequirement = new Choice(testScene, "Requires key", "target2", "has_key", null);
        choiceThatSetsFlag = new Choice(testScene, "Sets flag", "target3", null, "visited_target3");
        
        choiceRepository.save(choiceWithNoRequirements);
        choiceRepository.save(choiceWithRequirement);
        choiceRepository.save(choiceThatSetsFlag);
    }

    @Test
    void testCanMakeChoice_NoRequirements_ReturnsTrue() {
        // Arrange
        Map<String, Object> emptyFlags = new HashMap<>();

        // Act
        boolean canMake = choiceService.canMakeChoice(choiceWithNoRequirements.getId(), emptyFlags);

        // Assert
        assertTrue(canMake);
    }

    @Test
    void testCanMakeChoice_HasRequiredFlag_ReturnsTrue() {
        // Arrange
        Map<String, Object> flags = new HashMap<>();
        flags.put("has_key", true);

        // Act
        boolean canMake = choiceService.canMakeChoice(choiceWithRequirement.getId(), flags);

        // Assert
        assertTrue(canMake);
    }

    @Test
    void testCanMakeChoice_MissingRequiredFlag_ReturnsFalse() {
        // Arrange
        Map<String, Object> flags = new HashMap<>();
        flags.put("other_flag", true);

        // Act
        boolean canMake = choiceService.canMakeChoice(choiceWithRequirement.getId(), flags);

        // Assert
        assertFalse(canMake);
    }

    @Test
    void testCanMakeChoice_RequiredFlagIsFalse_ReturnsFalse() {
        // Arrange
        Map<String, Object> flags = new HashMap<>();
        flags.put("has_key", false);

        // Act
        boolean canMake = choiceService.canMakeChoice(choiceWithRequirement.getId(), flags);

        // Assert
        assertFalse(canMake);
    }

    @Test
    void testProcessChoiceEffects_SetsFlag() {
        // Arrange
        Map<String, Object> currentFlags = new HashMap<>();
        currentFlags.put("existing_flag", true);

        // Act
        Map<String, Object> updatedFlags = choiceService.processChoiceEffects(
            choiceThatSetsFlag.getId(), currentFlags);

        // Assert
        assertTrue(updatedFlags.containsKey("existing_flag"));
        assertTrue(updatedFlags.containsKey("visited_target3"));
        assertEquals(true, updatedFlags.get("visited_target3"));
    }

    @Test
    void testProcessChoiceEffects_DoesNotModifyOriginal() {
        // Arrange
        Map<String, Object> currentFlags = new HashMap<>();
        currentFlags.put("original", true);

        // Act
        Map<String, Object> updatedFlags = choiceService.processChoiceEffects(
            choiceThatSetsFlag.getId(), currentFlags);

        // Assert - original should not have the new flag
        assertFalse(currentFlags.containsKey("visited_target3"));
        assertTrue(updatedFlags.containsKey("visited_target3"));
    }

    @Test
    void testProcessChoiceEffects_NoFlagToSet_ReturnsSameFlags() {
        // Arrange
        Map<String, Object> currentFlags = new HashMap<>();
        currentFlags.put("existing", true);

        // Act
        Map<String, Object> updatedFlags = choiceService.processChoiceEffects(
            choiceWithNoRequirements.getId(), currentFlags);

        // Assert
        assertEquals(currentFlags.size(), updatedFlags.size());
        assertTrue(updatedFlags.containsKey("existing"));
    }

    @Test
    void testValidateChoice_CanMakeChoice_DoesNotThrow() {
        // Arrange
        Map<String, Object> flags = new HashMap<>();
        flags.put("has_key", true);

        // Act & Assert - should not throw
        assertDoesNotThrow(() -> {
            choiceService.validateChoice(choiceWithRequirement.getId(), flags);
        });
    }

    @Test
    void testValidateChoice_CannotMakeChoice_ThrowsException() {
        // Arrange
        Map<String, Object> flags = new HashMap<>();

        // Act & Assert
        assertThrows(InvalidChoiceException.class, () -> {
            choiceService.validateChoice(choiceWithRequirement.getId(), flags);
        });
    }

    @Test
    void testGetChoicesForScene_ReturnsAllChoices() {
        // Act
        List<Choice> choices = choiceService.getChoicesForScene("test_scene");

        // Assert
        assertTrue(choices.size() >= 3);
        assertTrue(choices.stream().anyMatch(c -> c.getLabel().equals("Free choice")));
        assertTrue(choices.stream().anyMatch(c -> c.getLabel().equals("Requires key")));
        assertTrue(choices.stream().anyMatch(c -> c.getLabel().equals("Sets flag")));
    }

    @Test
    void testGetAvailableChoicesForScene_FiltersByFlags() {
        // Arrange
        Map<String, Object> flags = new HashMap<>();
        flags.put("has_key", true);

        // Act
        List<Choice> availableChoices = choiceService.getAvailableChoicesForScene("test_scene", flags);

        // Assert - should include choice with no requirements and choice with met requirement
        assertTrue(availableChoices.size() >= 2);
        assertTrue(availableChoices.stream().anyMatch(c -> c.getLabel().equals("Free choice")));
        assertTrue(availableChoices.stream().anyMatch(c -> c.getLabel().equals("Requires key")));
    }

    @Test
    void testHasRequirements_ChoiceWithRequirement_ReturnsTrue() {
        // Act
        boolean hasRequirements = choiceService.hasRequirements(choiceWithRequirement.getId());

        // Assert
        assertTrue(hasRequirements);
    }

    @Test
    void testHasRequirements_ChoiceWithoutRequirement_ReturnsFalse() {
        // Act
        boolean hasRequirements = choiceService.hasRequirements(choiceWithNoRequirements.getId());

        // Assert
        assertFalse(hasRequirements);
    }

    @Test
    void testSetsFlags_ChoiceThatSetsFlag_ReturnsTrue() {
        // Act
        boolean setsFlags = choiceService.setsFlags(choiceThatSetsFlag.getId());

        // Assert
        assertTrue(setsFlags);
    }

    @Test
    void testSetsFlags_ChoiceThatDoesNotSetFlag_ReturnsFalse() {
        // Act
        boolean setsFlags = choiceService.setsFlags(choiceWithNoRequirements.getId());

        // Assert
        assertFalse(setsFlags);
    }
}

