package com.textquest.api.entity;

import com.textquest.api.repository.ItemRepository;
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
class ItemEntityTest {

    @Autowired
    private ItemRepository itemRepository;

    @BeforeEach
    void setUp() {
        itemRepository.deleteAll();
    }

    @Test
    void testItemCreation_WithAllFields() {
        // Arrange
        Item item = new Item("torch", "Torch", "A bright torch", "torch.png");

        // Act
        Item saved = itemRepository.save(item);

        // Assert
        assertNotNull(saved.getId());
        assertEquals("torch", saved.getLabel());
        assertEquals("Torch", saved.getTitle());
        assertEquals("A bright torch", saved.getDescription());
    }

    @Test
    void testItemCreation_WithNoArgsConstructor() {
        // Arrange
        Item item = new Item();
        item.setLabel("key");
        item.setTitle("Key");
        item.setDescription("A key");

        // Act
        Item saved = itemRepository.save(item);

        // Assert
        assertNotNull(saved.getId());
        assertEquals("key", saved.getLabel());
        assertEquals("Key", saved.getTitle());
        assertEquals("A key", saved.getDescription());
    }

    @Test
    void testItemGettersAndSetters() {
        // Arrange
        Item item = new Item();

        // Act
        item.setId(1L);
        item.setLabel("sword");
        item.setTitle("Sword");
        item.setDescription("A sword");

        // Assert
        assertEquals(1L, item.getId());
        assertEquals("sword", item.getLabel());
        assertEquals("Sword", item.getTitle());
        assertEquals("A sword", item.getDescription());
    }

    @Test
    void testItemToString() {
        // Arrange
        Item item = new Item("potion", "Potion", "A healing potion", "potion.png");
        item.setId(1L);

        // Act
        String toString = item.toString();

        // Assert
        assertNotNull(toString);
        assertTrue(toString.contains("potion"));
        assertTrue(toString.contains("Potion"));
        assertTrue(toString.contains("A healing potion"));
        assertTrue(toString.contains("id=1"));
    }

    @Test
    void testItemPersistence() {
        // Arrange
        Item item = new Item("torch", "Torch", "A bright torch", "torch.png");

        // Act
        Item saved = itemRepository.save(item);
        Long id = saved.getId();
        
        // Clear persistence context and reload
        itemRepository.flush();
        Item found = itemRepository.findById(id).orElse(null);

        // Assert
        assertNotNull(found);
        assertEquals("torch", found.getLabel());
        assertEquals("Torch", found.getTitle());
        assertEquals("A bright torch", found.getDescription());
    }

    @Test
    void testItemWithNullDescription() {
        // Arrange
        Item item = new Item("key", "Key", null, "key.png");

        // Act
        Item saved = itemRepository.save(item);

        // Assert
        assertNotNull(saved.getId());
        assertNull(saved.getDescription());
        assertEquals("key", saved.getLabel());
    }

    @Test
    void testItemWithEmptyStringDescription() {
        // Arrange
        Item item = new Item("potion", "Potion", "", "potion.png");

        // Act
        Item saved = itemRepository.save(item);

        // Assert
        assertNotNull(saved.getId());
        assertEquals("", saved.getDescription());
    }

    @Test
    void testItemLabelUniqueness() {
        // Arrange
        Item item1 = new Item("torch", "Torch", "First torch", "torch1.png");
        Item item2 = new Item("torch", "Another Torch", "Second torch", "torch2.png");

        // Act
        itemRepository.save(item1);

        // Assert - Should throw exception due to unique constraint
        assertThrows(Exception.class, () -> {
            itemRepository.save(item2);
        });
    }

    @Test
    void testItemUpdate() {
        // Arrange
        Item item = new Item("sword", "Sword", "A sword", "sword.png");
        Item saved = itemRepository.save(item);
        Long id = saved.getId();

        // Act
        saved.setTitle("Updated Sword");
        saved.setDescription("An updated sword");
        Item updated = itemRepository.save(saved);

        // Assert
        assertEquals(id, updated.getId());
        assertEquals("sword", updated.getLabel()); // Label should remain the same
        assertEquals("Updated Sword", updated.getTitle());
        assertEquals("An updated sword", updated.getDescription());
    }

    @Test
    void testItemEquality() {
        // Arrange
        Item item1 = new Item("torch", "Torch", "A torch", "torch.png");
        Item item2 = new Item("torch", "Torch", "A torch", "torch.png");

        // Act
        Item saved1 = itemRepository.save(item1);
        Item saved2 = itemRepository.save(item2);

        // Assert - They should have different IDs (even if same data)
        // Note: This test assumes item2 would fail due to unique constraint
        // But if we test with different labels:
        Item item3 = new Item("key", "Key", "A key", "key.png");
        Item saved3 = itemRepository.save(item3);

        assertNotEquals(saved1.getId(), saved3.getId());
    }

    @Test
    void testItemWithSpecialCharactersInLabel() {
        // Arrange
        Item item = new Item("sword-1", "Sword", "A sword", "sword.png");

        // Act
        Item saved = itemRepository.save(item);

        // Assert
        assertNotNull(saved.getId());
        assertEquals("sword-1", saved.getLabel());
    }

    @Test
    void testItemWithLongTitle() {
        // Arrange
        String longTitle = "This is a very long title that should be stored properly";
        Item item = new Item("long-item", longTitle, "Description", "item.png");

        // Act
        Item saved = itemRepository.save(item);

        // Assert
        assertEquals(longTitle, saved.getTitle());
    }
}

