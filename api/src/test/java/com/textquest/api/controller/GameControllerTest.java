package com.textquest.api.controller;

import com.textquest.api.entity.Choice;
import com.textquest.api.entity.Scene;
import com.textquest.api.repository.SceneRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GameController.class)
class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SceneRepository sceneRepository;

    private Scene testScene;
    private List<Choice> testChoices;

    @BeforeEach
    void setUp() {
        testScene = new Scene("test_scene", "Test Scene", "This is a test scene body", false);
        testScene.setId(1L);
        
        testChoices = new ArrayList<>();
    }

    @Test
    void testGetScene_WithChoices_Success() throws Exception {
        // Arrange
        Choice choice1 = new Choice(testScene, "Go left", "scene_left", null, null);
        choice1.setId(1L);
        Choice choice2 = new Choice(testScene, "Go right", "scene_right", null, null);
        choice2.setId(2L);
        testChoices.add(choice1);
        testChoices.add(choice2);
        testScene.setChoices(testChoices);

        when(sceneRepository.findByCode("test_scene")).thenReturn(Optional.of(testScene));

        // Act & Assert
        mockMvc.perform(get("/api/game/scene/test_scene")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("test_scene"))
                .andExpect(jsonPath("$.title").value("Test Scene"))
                .andExpect(jsonPath("$.body").value("This is a test scene body"))
                .andExpect(jsonPath("$.isTerminal").value(false))
                .andExpect(jsonPath("$.choices").isArray())
                .andExpect(jsonPath("$.choices.length()").value(2))
                .andExpect(jsonPath("$.choices[0].id").value(1))
                .andExpect(jsonPath("$.choices[0].label").value("Go left"))
                .andExpect(jsonPath("$.choices[0].targetSceneCode").value("scene_left"))
                .andExpect(jsonPath("$.choices[1].id").value(2))
                .andExpect(jsonPath("$.choices[1].label").value("Go right"))
                .andExpect(jsonPath("$.choices[1].targetSceneCode").value("scene_right"));
    }

    @Test
    void testGetScene_WithNoChoices_ReturnsEmptyChoicesArray() throws Exception {
        // Arrange
        testScene.setChoices(new ArrayList<>());
        when(sceneRepository.findByCode("test_scene")).thenReturn(Optional.of(testScene));

        // Act & Assert
        mockMvc.perform(get("/api/game/scene/test_scene")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("test_scene"))
                .andExpect(jsonPath("$.choices").isArray())
                .andExpect(jsonPath("$.choices.length()").value(0));
    }

    @Test
    void testGetScene_WithChoicesHavingFlags_ReturnsFlagsCorrectly() throws Exception {
        // Arrange
        Choice choice1 = new Choice(testScene, "Secret path", "secret_scene", "has_key", "found_secret");
        choice1.setId(1L);
        Choice choice2 = new Choice(testScene, "Normal path", "normal_scene", null, "visited_normal");
        choice2.setId(2L);
        testChoices.add(choice1);
        testChoices.add(choice2);
        testScene.setChoices(testChoices);

        when(sceneRepository.findByCode("test_scene")).thenReturn(Optional.of(testScene));

        // Act & Assert
        mockMvc.perform(get("/api/game/scene/test_scene")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.choices[0].requiresFlag").value("has_key"))
                .andExpect(jsonPath("$.choices[0].setsFlag").value("found_secret"))
                .andExpect(jsonPath("$.choices[1].requiresFlag").value(""))
                .andExpect(jsonPath("$.choices[1].setsFlag").value("visited_normal"));
    }

    @Test
    void testGetScene_WithChoicesHavingNullFlags_ReturnsEmptyStrings() throws Exception {
        // Arrange
        Choice choice = new Choice(testScene, "Simple choice", "target_scene", null, null);
        choice.setId(1L);
        testChoices.add(choice);
        testScene.setChoices(testChoices);

        when(sceneRepository.findByCode("test_scene")).thenReturn(Optional.of(testScene));

        // Act & Assert
        mockMvc.perform(get("/api/game/scene/test_scene")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.choices[0].requiresFlag").value(""))
                .andExpect(jsonPath("$.choices[0].setsFlag").value(""));
    }

    @Test
    void testGetScene_SceneNotFound_ThrowsRuntimeException() throws Exception {
        // Arrange
        when(sceneRepository.findByCode("non_existent")).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/game/scene/non_existent")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void testHealthEndpoint_ReturnsCorrectMessage() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/game/health")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Game API is running!"));
    }

    @Test
    void testGetScene_TerminalScene_ReturnsTerminalFlag() throws Exception {
        // Arrange
        Scene terminalScene = new Scene("terminal_scene", "The End", "You have reached the end", true);
        terminalScene.setId(2L);
        terminalScene.setChoices(new ArrayList<>());

        when(sceneRepository.findByCode("terminal_scene")).thenReturn(Optional.of(terminalScene));

        // Act & Assert
        mockMvc.perform(get("/api/game/scene/terminal_scene")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("terminal_scene"))
                .andExpect(jsonPath("$.title").value("The End"))
                .andExpect(jsonPath("$.body").value("You have reached the end"))
                .andExpect(jsonPath("$.isTerminal").value(true));
    }

    @Test
    void testGetScene_NonTerminalScene_ReturnsNonTerminalFlag() throws Exception {
        // Arrange
        testScene.setChoices(new ArrayList<>());
        when(sceneRepository.findByCode("test_scene")).thenReturn(Optional.of(testScene));

        // Act & Assert
        mockMvc.perform(get("/api/game/scene/test_scene")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isTerminal").value(false));
    }

    @Test
    void testGetScene_ResponseFormat_AllFieldsPresent() throws Exception {
        // Arrange
        Choice choice = new Choice(testScene, "Test choice", "target", "req_flag", "set_flag");
        choice.setId(1L);
        testChoices.add(choice);
        testScene.setChoices(testChoices);

        when(sceneRepository.findByCode("test_scene")).thenReturn(Optional.of(testScene));

        // Act & Assert
        mockMvc.perform(get("/api/game/scene/test_scene")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").exists())
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.body").exists())
                .andExpect(jsonPath("$.isTerminal").exists())
                .andExpect(jsonPath("$.choices").exists())
                .andExpect(jsonPath("$.choices[0].id").exists())
                .andExpect(jsonPath("$.choices[0].label").exists())
                .andExpect(jsonPath("$.choices[0].targetSceneCode").exists())
                .andExpect(jsonPath("$.choices[0].requiresFlag").exists())
                .andExpect(jsonPath("$.choices[0].setsFlag").exists());
    }

    @Test
    void testGetScene_WithMultipleChoices_ReturnsAllChoices() throws Exception {
        // Arrange
        Choice choice1 = new Choice(testScene, "Choice 1", "scene1", null, null);
        choice1.setId(1L);
        Choice choice2 = new Choice(testScene, "Choice 2", "scene2", "flag1", null);
        choice2.setId(2L);
        Choice choice3 = new Choice(testScene, "Choice 3", "scene3", null, "flag2");
        choice3.setId(3L);
        Choice choice4 = new Choice(testScene, "Choice 4", "scene4", "flag3", "flag4");
        choice4.setId(4L);
        
        testChoices.addAll(Arrays.asList(choice1, choice2, choice3, choice4));
        testScene.setChoices(testChoices);

        when(sceneRepository.findByCode("test_scene")).thenReturn(Optional.of(testScene));

        // Act & Assert
        mockMvc.perform(get("/api/game/scene/test_scene")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.choices.length()").value(4))
                .andExpect(jsonPath("$.choices[0].label").value("Choice 1"))
                .andExpect(jsonPath("$.choices[1].label").value("Choice 2"))
                .andExpect(jsonPath("$.choices[2].label").value("Choice 3"))
                .andExpect(jsonPath("$.choices[3].label").value("Choice 4"))
                .andExpect(jsonPath("$.choices[0].targetSceneCode").value("scene1"))
                .andExpect(jsonPath("$.choices[1].targetSceneCode").value("scene2"))
                .andExpect(jsonPath("$.choices[2].targetSceneCode").value("scene3"))
                .andExpect(jsonPath("$.choices[3].targetSceneCode").value("scene4"));
    }
}

