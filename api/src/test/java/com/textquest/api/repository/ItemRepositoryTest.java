package com.textquest.api.repository;

import com.textquest.api.entity.Item;
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
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    private Item testItem1;
    private Item testItem2;

    @BeforeEach
    void setUp() {
        // Clear any existing test data
        itemRepository.deleteAll();
        
        // Create test items
        testItem1 = new Item("torch", "Torch", "A bright torch that lights the way", "torch.png");
        testItem2 = new Item("key", "Ancient Key", "A mysterious key that opens ancient doors", "key.png");
    }

    @Test
    void testSaveItem() {
        // Act
        Item saved = itemRepository.save(testItem1);

        // Assert
        assertNotNull(saved.getId());
        assertEquals("torch", saved.getLabel());
        assertEquals("Torch", saved.getTitle());
        assertEquals("A bright torch that lights the way", saved.getDescription());
    }

    @Test
    void testFindByLabel_Success() {
        // Arrange
        itemRepository.save(testItem1);
        itemRepository.save(testItem2);

        // Act
        Optional<Item> found = itemRepository.findByLabel("torch");

        // Assert
        assertTrue(found.isPresent());
        assertEquals("torch", found.get().getLabel());
        assertEquals("Torch", found.get().getTitle());
    }

    @Test
    void testFindByLabel_NotFound() {
        // Arrange
        itemRepository.save(testItem1);

        // Act
        Optional<Item> found = itemRepository.findByLabel("nonexistent");

        // Assert
        assertFalse(found.isPresent());
    }

    @Test
    void testFindByLabel_CaseSensitive() {
        // Arrange
        itemRepository.save(testItem1);

        // Act
        Optional<Item> foundLower = itemRepository.findByLabel("torch");
        Optional<Item> foundUpper = itemRepository.findByLabel("Torch");

        // Assert
        assertTrue(foundLower.isPresent());
        assertFalse(foundUpper.isPresent(), "Label lookup should be case-sensitive");
    }

    @Test
    void testFindAll() {
        // Arrange
        itemRepository.save(testItem1);
        itemRepository.save(testItem2);

        // Act
        List<Item> allItems = itemRepository.findAll();

        // Assert
        assertEquals(2, allItems.size());
        assertTrue(allItems.stream().anyMatch(item -> item.getLabel().equals("torch")));
        assertTrue(allItems.stream().anyMatch(item -> item.getLabel().equals("key")));
    }

    @Test
    void testFindAll_Empty() {
        // Act
        List<Item> allItems = itemRepository.findAll();

        // Assert
        assertTrue(allItems.isEmpty());
    }

    @Test
    void testItemUniqueness() {
        // Arrange
        itemRepository.save(testItem1);

        // Act & Assert - Try to save another item with the same label
        Item duplicate = new Item("torch", "Another Torch", "Different description", "torch2.png");
        
        // This should throw an exception due to unique constraint
        assertThrows(Exception.class, () -> {
            itemRepository.save(duplicate);
        });
    }

    @Test
    void testUpdateItem() {
        // Arrange
        Item saved = itemRepository.save(testItem1);
        Long id = saved.getId();

        // Act
        saved.setTitle("Updated Torch");
        saved.setDescription("Updated description");
        Item updated = itemRepository.save(saved);

        // Assert
        assertEquals(id, updated.getId());
        assertEquals("Updated Torch", updated.getTitle());
        assertEquals("Updated description", updated.getDescription());
        assertEquals("torch", updated.getLabel()); // Label should not change
    }

    @Test
    void testDeleteItem() {
        // Arrange
        Item saved = itemRepository.save(testItem1);
        Long id = saved.getId();

        // Act
        itemRepository.delete(saved);

        // Assert
        Optional<Item> found = itemRepository.findById(id);
        assertFalse(found.isPresent());
    }

    @Test
    void testFindById() {
        // Arrange
        Item saved = itemRepository.save(testItem1);
        Long id = saved.getId();

        // Act
        Optional<Item> found = itemRepository.findById(id);

        // Assert
        assertTrue(found.isPresent());
        assertEquals("torch", found.get().getLabel());
    }

    @Test
    void testFindById_NotFound() {
        // Act
        Optional<Item> found = itemRepository.findById(999L);

        // Assert
        assertFalse(found.isPresent());
    }

    @Test
    void testItemWithNullDescription() {
        // Arrange
        Item item = new Item("potion", "Potion", null, "potion.png");

        // Act
        Item saved = itemRepository.save(item);

        // Assert
        assertNotNull(saved.getId());
        assertNull(saved.getDescription());
        assertEquals("potion", saved.getLabel());
    }

    @Test
    void testItemWithLongDescription() {
        // Arrange
        String longDescription = "This is a very long description that contains many words and should be stored properly in the database without any issues. " +
                "It tests that the TEXT column type can handle longer descriptions without problems.";
        Item item = new Item("scroll", "Ancient Scroll", longDescription, "scroll.png");

        // Act
        Item saved = itemRepository.save(item);

        // Assert
        assertNotNull(saved.getId());
        assertEquals(longDescription, saved.getDescription());
    }

    @Test
    void testMultipleItemsWithDifferentLabels() {
        // Arrange
        Item item1 = new Item("sword", "Sword", "A sharp sword", "sword.png");
        Item item2 = new Item("shield", "Shield", "A sturdy shield", "shield.png");
        Item item3 = new Item("potion", "Potion", "A healing potion", "potion.png");

        // Act
        itemRepository.save(item1);
        itemRepository.save(item2);
        itemRepository.save(item3);

        // Assert
        List<Item> allItems = itemRepository.findAll();
        assertEquals(3, allItems.size());
        
        Optional<Item> foundSword = itemRepository.findByLabel("sword");
        Optional<Item> foundShield = itemRepository.findByLabel("shield");
        Optional<Item> foundPotion = itemRepository.findByLabel("potion");
        
        assertTrue(foundSword.isPresent());
        assertTrue(foundShield.isPresent());
        assertTrue(foundPotion.isPresent());
    }
}

