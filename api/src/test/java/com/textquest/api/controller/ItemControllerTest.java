package com.textquest.api.controller;

import com.textquest.api.entity.Item;
import com.textquest.api.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRepository itemRepository;

    private Item testItem1;
    private Item testItem2;
    private List<Item> allItems;

    @BeforeEach
    void setUp() {
        testItem1 = new Item("torch", "Torch", "A bright torch that lights the way", "torch.png");
        testItem1.setId(1L);
        
        testItem2 = new Item("key", "Ancient Key", "A mysterious key that opens ancient doors", "key.png");
        testItem2.setId(2L);
        
        allItems = Arrays.asList(testItem1, testItem2);
    }

    @Test
    void testGetAllItems_Success() throws Exception {
        // Arrange
        when(itemRepository.findAll()).thenReturn(allItems);

        // Act & Assert
        mockMvc.perform(get("/api/items/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].label").value("torch"))
                .andExpect(jsonPath("$[0].title").value("Torch"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].label").value("key"));
    }

    @Test
    void testGetAllItems_EmptyList() throws Exception {
        // Arrange
        when(itemRepository.findAll()).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/api/items/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void testGetByLabel_Success() throws Exception {
        // Arrange
        when(itemRepository.findByLabel("torch")).thenReturn(Optional.of(testItem1));

        // Act & Assert
        mockMvc.perform(get("/api/items/torch"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.label").value("torch"))
                .andExpect(jsonPath("$.title").value("Torch"))
                .andExpect(jsonPath("$.description").value("A bright torch that lights the way"));
    }

    @Test
    void testGetByLabel_NotFound() throws Exception {
        // Arrange
        when(itemRepository.findByLabel("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/items/nonexistent"))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void testGetByLabel_WithSpecialCharacters() throws Exception {
        // Arrange
        Item specialItem = new Item("sword-1", "Sword", "A special sword", "sword.png");
        specialItem.setId(3L);
        when(itemRepository.findByLabel("sword-1")).thenReturn(Optional.of(specialItem));

        // Act & Assert
        mockMvc.perform(get("/api/items/sword-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.label").value("sword-1"));
    }

    @Test
    void testGetByLabel_CaseSensitive() throws Exception {
        // Arrange
        when(itemRepository.findByLabel("Torch")).thenReturn(Optional.empty());
        when(itemRepository.findByLabel("torch")).thenReturn(Optional.of(testItem1));

        // Act & Assert - lowercase should work
        mockMvc.perform(get("/api/items/torch"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.label").value("torch"));

        // Uppercase should fail if not found
        mockMvc.perform(get("/api/items/Torch"))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void testGetByLabel_MultipleItems() throws Exception {
        // Arrange
        when(itemRepository.findByLabel("key")).thenReturn(Optional.of(testItem2));

        // Act & Assert
        mockMvc.perform(get("/api/items/key"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.label").value("key"))
                .andExpect(jsonPath("$.title").value("Ancient Key"));
    }

    @Test
    void testCorsHeaders() throws Exception {
        // Arrange
        when(itemRepository.findAll()).thenReturn(allItems);

        // Act & Assert
        mockMvc.perform(get("/api/items/all")
                .header("Origin", "http://localhost:5173"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Access-Control-Allow-Origin"));
    }
}

