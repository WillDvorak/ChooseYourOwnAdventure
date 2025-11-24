package com.textquest.api.repository;

import com.textquest.api.entity.Choice;
import com.textquest.api.entity.Scene;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.flyway.enabled=false"
})
@Transactional
class ChoiceRepositoryTest {

    @Autowired
    private ChoiceRepository choiceRepository;

    @Autowired
    private SceneRepository sceneRepository;

    private Scene testScene;

    @BeforeEach
    void setUp() {
        choiceRepository.deleteAll();
        sceneRepository.deleteAll();
        
        // Create a test scene for use in tests
        testScene = new Scene("test_scene", "Test Scene", "Test body", false);
        sceneRepository.save(testScene);
    }

    @Test
    void testSaveChoice() {
        // Test saving a new choice
        Choice choice = new Choice(testScene, "Go to next scene", "next_scene", null, null);
        Choice savedChoice = choiceRepository.save(choice);
        
        assertNotNull(savedChoice.getId());
        assertEquals("Go to next scene", savedChoice.getLabel());
        assertEquals("next_scene", savedChoice.getTargetSceneCode());
        assertNull(savedChoice.getRequiresFlag());
        assertNull(savedChoice.getSetsFlag());
        assertNotNull(savedChoice.getCreatedAt());
        assertNotNull(savedChoice.getUpdatedAt());
        assertEquals(testScene.getId(), savedChoice.getScene().getId());
    }

    @Test
    void testFindByScene() {
        // Test finding choices by Scene entity
        Scene scene1 = new Scene("scene1", "Scene 1", "Body 1", false);
        Scene scene2 = new Scene("scene2", "Scene 2", "Body 2", false);
        Scene savedScene1 = sceneRepository.save(scene1);
        Scene savedScene2 = sceneRepository.save(scene2);
        
        Choice choice1 = new Choice(savedScene1, "Choice 1", "target1", null, "flag1");
        Choice choice2 = new Choice(savedScene1, "Choice 2", "target2", null, "flag2");
        Choice choice3 = new Choice(savedScene2, "Choice 3", "target3", null, null);
        
        choiceRepository.save(choice1);
        choiceRepository.save(choice2);
        choiceRepository.save(choice3);
        
        List<Choice> scene1Choices = choiceRepository.findByScene(savedScene1);
        assertEquals(2, scene1Choices.size());
        assertTrue(scene1Choices.stream().allMatch(c -> c.getScene().getId().equals(savedScene1.getId())));
    }

    @Test
    void testFindBySceneCode() {
        // Test finding choices by scene code
        Scene scene = new Scene("scene_code_test", "Scene Code Test", "Body", false);
        Scene savedScene = sceneRepository.save(scene);
        
        Choice choice1 = new Choice(savedScene, "Choice 1", "target1", null, null);
        Choice choice2 = new Choice(savedScene, "Choice 2", "target2", null, null);
        
        choiceRepository.save(choice1);
        choiceRepository.save(choice2);
        
        List<Choice> choices = choiceRepository.findBySceneCode("scene_code_test");
        assertEquals(2, choices.size());
        assertTrue(choices.stream().allMatch(c -> c.getScene().getCode().equals("scene_code_test")));
    }

    @Test
    void testFindByRequiresFlag() {
        // Test finding choices that require a specific flag
        Scene scene = new Scene("flag_test", "Flag Test", "Body", false);
        Scene savedScene = sceneRepository.save(scene);
        
        Choice choice1 = new Choice(savedScene, "Choice 1", "target1", "torch", null);
        Choice choice2 = new Choice(savedScene, "Choice 2", "target2", "gold", null);
        Choice choice3 = new Choice(savedScene, "Choice 3", "target3", "torch", null);
        Choice choice4 = new Choice(savedScene, "Choice 4", "target4", null, null);
        
        choiceRepository.save(choice1);
        choiceRepository.save(choice2);
        choiceRepository.save(choice3);
        choiceRepository.save(choice4);
        
        List<Choice> torchChoices = choiceRepository.findByRequiresFlag("torch");
        assertEquals(2, torchChoices.size());
        assertTrue(torchChoices.stream().allMatch(c -> "torch".equals(c.getRequiresFlag())));
    }

    @Test
    void testFindBySetsFlag() {
        // Test finding choices that set a specific flag
        Scene scene = new Scene("sets_flag_test", "Sets Flag Test", "Body", false);
        Scene savedScene = sceneRepository.save(scene);
        
        Choice choice1 = new Choice(savedScene, "Choice 1", "target1", null, "torch");
        Choice choice2 = new Choice(savedScene, "Choice 2", "target2", null, "gold");
        Choice choice3 = new Choice(savedScene, "Choice 3", "target3", null, "torch");
        Choice choice4 = new Choice(savedScene, "Choice 4", "target4", null, null);
        
        choiceRepository.save(choice1);
        choiceRepository.save(choice2);
        choiceRepository.save(choice3);
        choiceRepository.save(choice4);
        
        List<Choice> torchChoices = choiceRepository.findBySetsFlag("torch");
        assertEquals(2, torchChoices.size());
        assertTrue(torchChoices.stream().allMatch(c -> "torch".equals(c.getSetsFlag())));
    }

    @Test
    void testFindByRequiresFlagIsNullOrEmpty() {
        // Test finding choices that don't require any flag
        Scene scene = new Scene("no_flag_test", "No Flag Test", "Body", false);
        Scene savedScene = sceneRepository.save(scene);
        
        Choice choice1 = new Choice(savedScene, "Choice 1", "target1", null, null);
        Choice choice2 = new Choice(savedScene, "Choice 2", "target2", "", null);
        Choice choice3 = new Choice(savedScene, "Choice 3", "target3", "torch", null);
        
        choiceRepository.save(choice1);
        choiceRepository.save(choice2);
        choiceRepository.save(choice3);
        
        List<Choice> noFlagChoices = choiceRepository.findByRequiresFlagIsNullOrEmpty();
        assertEquals(2, noFlagChoices.size());
        assertTrue(noFlagChoices.stream().allMatch(c -> 
            c.getRequiresFlag() == null || c.getRequiresFlag().isEmpty()));
    }

    @Test
    void testFindByLabelContainingIgnoreCase() {
        // Test finding choices by label containing text (case-insensitive)
        Scene scene = new Scene("label_test", "Label Test", "Body", false);
        Scene savedScene = sceneRepository.save(scene);
        
        Choice choice1 = new Choice(savedScene, "Attack the dragon", "target1", null, null);
        Choice choice2 = new Choice(savedScene, "Run away", "target2", null, null);
        Choice choice3 = new Choice(savedScene, "Attack with sword", "target3", null, null);
        Choice choice4 = new Choice(savedScene, "Talk to dragon", "target4", null, null);
        
        choiceRepository.save(choice1);
        choiceRepository.save(choice2);
        choiceRepository.save(choice3);
        choiceRepository.save(choice4);
        
        List<Choice> attackChoices = choiceRepository.findByLabelContainingIgnoreCase("attack");
        assertEquals(2, attackChoices.size());
        assertTrue(attackChoices.stream().anyMatch(c -> c.getLabel().equals("Attack the dragon")));
        assertTrue(attackChoices.stream().anyMatch(c -> c.getLabel().equals("Attack with sword")));
        
        // Test case-insensitive
        List<Choice> dragonChoices = choiceRepository.findByLabelContainingIgnoreCase("DRAGON");
        assertEquals(2, dragonChoices.size());
    }

    @Test
    void testFindByTargetSceneCode() {
        // Test finding choices that target a specific scene code
        Scene scene1 = new Scene("scene1", "Scene 1", "Body 1", false);
        Scene scene2 = new Scene("scene2", "Scene 2", "Body 2", false);
        Scene savedScene1 = sceneRepository.save(scene1);
        Scene savedScene2 = sceneRepository.save(scene2);
        
        Choice choice1 = new Choice(savedScene1, "Choice 1", "target_scene", null, null);
        Choice choice2 = new Choice(savedScene1, "Choice 2", "target_scene", null, null);
        Choice choice3 = new Choice(savedScene2, "Choice 3", "other_scene", null, null);
        
        choiceRepository.save(choice1);
        choiceRepository.save(choice2);
        choiceRepository.save(choice3);
        
        List<Choice> targetChoices = choiceRepository.findByTargetSceneCode("target_scene");
        assertEquals(2, targetChoices.size());
        assertTrue(targetChoices.stream().allMatch(c -> c.getTargetSceneCode().equals("target_scene")));
    }

    @Test
    void testFindAvailableChoicesForScene() {
        // Test finding available choices for a scene (no flag requirements)
        Scene scene = new Scene("available_test", "Available Test", "Body", false);
        Scene savedScene = sceneRepository.save(scene);
        
        Choice choice1 = new Choice(savedScene, "Choice 1", "target1", null, null);
        Choice choice2 = new Choice(savedScene, "Choice 2", "target2", "", null);
        Choice choice3 = new Choice(savedScene, "Choice 3", "target3", "torch", null);
        Choice choice4 = new Choice(savedScene, "Choice 4", "target4", "gold", null);
        
        choiceRepository.save(choice1);
        choiceRepository.save(choice2);
        choiceRepository.save(choice3);
        choiceRepository.save(choice4);
        
        List<Choice> availableChoices = choiceRepository.findAvailableChoicesForScene("available_test");
        assertEquals(2, availableChoices.size());
        assertTrue(availableChoices.stream().allMatch(c -> 
            c.getRequiresFlag() == null || c.getRequiresFlag().isEmpty()));
    }

    @Test
    void testUpdateChoice() {
        // Test updating an existing choice
        Choice choice = new Choice(testScene, "Original Label", "original_target", null, null);
        Choice savedChoice = choiceRepository.save(choice);
        
        savedChoice.setLabel("Updated Label");
        savedChoice.setTargetSceneCode("updated_target");
        savedChoice.setRequiresFlag("torch");
        savedChoice.setSetsFlag("flag1");
        
        Choice updatedChoice = choiceRepository.save(savedChoice);
        assertEquals("Updated Label", updatedChoice.getLabel());
        assertEquals("updated_target", updatedChoice.getTargetSceneCode());
        assertEquals("torch", updatedChoice.getRequiresFlag());
        assertEquals("flag1", updatedChoice.getSetsFlag());
        assertNotNull(updatedChoice.getUpdatedAt());
    }
}
