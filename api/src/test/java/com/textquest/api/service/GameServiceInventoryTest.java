package com.textquest.api.service;

import com.textquest.api.entity.GameSession;
import com.textquest.api.entity.Item;
import com.textquest.api.entity.PlayerInventory;
import com.textquest.api.entity.Scene;
import com.textquest.api.repository.ItemRepository;
import com.textquest.api.repository.PlayerInventoryRepository;
import com.textquest.api.repository.SceneRepository;
import com.textquest.api.exception.GameEndedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.flyway.enabled=false"
})
@Transactional
class GameServiceInventoryTest {

    @Autowired
    private GameService gameService;

    @Autowired
    private GameSessionRepository gameSessionRepository;

    @Autowired
    private SceneRepository sceneRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private PlayerInventoryRepository playerInventoryRepository;

    private Scene testScene;
    private GameSession testSession;
    private Item testItem;
    private Item consumableItem;

    @BeforeEach
    void setUp() {
        // Create test scene
        testScene = new Scene("test_scene", "Test Scene", "This is a test scene", false);
        sceneRepository.save(testScene);

        // Create test session
        testSession = gameService.createGameSession("TestPlayer", "test_scene");

        // Create test items
        testItem = new Item("test_sword", "Test Sword", "A test sword", "weapon", "{}", false);
        itemRepository.save(testItem);

        consumableItem = new Item("health_potion", "Health Potion", "Restores 20 HP", "consumable", 
            "{\"hp_change\": 20}", true);
        itemRepository.save(consumableItem);
    }

    @Test
    void testGetPlayerInventory_EmptyInventory_ReturnsEmptyList() {
        // Act
        List<PlayerInventory> inventory = gameService.getPlayerInventory(testSession.getId());

        // Assert
        assertNotNull(inventory);
        assertTrue(inventory.isEmpty());
    }

    @Test
    void testAddItem_NewItem_CreatesInventoryEntry() {
        // Act
        PlayerInventory inventory = gameService.addItem(testSession.getId(), testItem.getId(), 1);

        // Assert
        assertNotNull(inventory.getId());
        assertEquals(testItem.getId(), inventory.getItem().getId());
        assertEquals(1, inventory.getQuantity());
        assertEquals(testSession.getId(), inventory.getGameSession().getId());
    }

    @Test
    void testAddItem_ExistingItem_IncreasesQuantity() {
        // Arrange - Add item first time
        gameService.addItem(testSession.getId(), testItem.getId(), 1);

        // Act - Add same item again
        PlayerInventory inventory = gameService.addItem(testSession.getId(), testItem.getId(), 2);

        // Assert
        assertEquals(3, inventory.getQuantity()); // 1 + 2 = 3
    }

    @Test
    void testAddItem_ByCode_AddsItem() {
        // Act
        PlayerInventory inventory = gameService.addItemByCode(testSession.getId(), "test_sword", 1);

        // Assert
        assertNotNull(inventory.getId());
        assertEquals("test_sword", inventory.getItem().getCode());
        assertEquals(1, inventory.getQuantity());
    }

    @Test
    void testAddItem_DefaultQuantity_AddsOne() {
        // Act
        PlayerInventory inventory = gameService.addItem(testSession.getId(), testItem.getId(), null);

        // Assert
        assertEquals(1, inventory.getQuantity());
    }

    @Test
    void testAddItem_GameEnded_ThrowsException() {
        // Arrange - Create terminal scene and move session there
        Scene terminalScene = new Scene("terminal", "The End", "Game over", true);
        sceneRepository.save(terminalScene);
        testSession.setCurrentSceneCode("terminal");
        gameSessionRepository.save(testSession);

        // Act & Assert
        assertThrows(GameEndedException.class, () -> {
            gameService.addItem(testSession.getId(), testItem.getId(), 1);
        });
    }

    @Test
    void testRemoveItem_RemovesQuantity() {
        // Arrange - Add 5 items
        gameService.addItem(testSession.getId(), testItem.getId(), 5);

        // Act - Remove 2
        gameService.removeItem(testSession.getId(), testItem.getId(), 2);

        // Assert
        int quantity = gameService.getItemQuantity(testSession.getId(), testItem.getId());
        assertEquals(3, quantity);
    }

    @Test
    void testRemoveItem_RemovesAll_DeletesEntry() {
        // Arrange - Add 1 item
        gameService.addItem(testSession.getId(), testItem.getId(), 1);

        // Act - Remove 1
        gameService.removeItem(testSession.getId(), testItem.getId(), 1);

        // Assert
        int quantity = gameService.getItemQuantity(testSession.getId(), testItem.getId());
        assertEquals(0, quantity);
        assertFalse(gameService.hasItem(testSession.getId(), testItem.getId()));
    }

    @Test
    void testRemoveItem_ByCode_RemovesItem() {
        // Arrange
        gameService.addItem(testSession.getId(), testItem.getId(), 3);

        // Act
        gameService.removeItemByCode(testSession.getId(), "test_sword", 2);

        // Assert
        int quantity = gameService.getItemQuantityByCode(testSession.getId(), "test_sword");
        assertEquals(1, quantity);
    }

    @Test
    void testRemoveItem_DefaultQuantity_RemovesOne() {
        // Arrange
        gameService.addItem(testSession.getId(), testItem.getId(), 3);

        // Act
        gameService.removeItem(testSession.getId(), testItem.getId(), null);

        // Assert
        int quantity = gameService.getItemQuantity(testSession.getId(), testItem.getId());
        assertEquals(2, quantity);
    }

    @Test
    void testSetItemQuantity_SetsSpecificQuantity() {
        // Arrange
        gameService.addItem(testSession.getId(), testItem.getId(), 5);

        // Act
        PlayerInventory inventory = gameService.setItemQuantity(testSession.getId(), testItem.getId(), 10);

        // Assert
        assertEquals(10, inventory.getQuantity());
    }

    @Test
    void testSetItemQuantity_NewItem_CreatesEntry() {
        // Act
        PlayerInventory inventory = gameService.setItemQuantity(testSession.getId(), testItem.getId(), 5);

        // Assert
        assertNotNull(inventory.getId());
        assertEquals(5, inventory.getQuantity());
    }

    @Test
    void testSetItemQuantity_InvalidQuantity_ThrowsException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            gameService.setItemQuantity(testSession.getId(), testItem.getId(), 0);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            gameService.setItemQuantity(testSession.getId(), testItem.getId(), -5);
        });
    }

    @Test
    void testUseItem_Consumable_AppliesEffectsAndRemoves() {
        // Arrange - Add consumable item
        gameService.addItem(testSession.getId(), consumableItem.getId(), 1);
        int initialHp = testSession.getHp();

        // Act
        Map<String, Object> result = gameService.useItem(testSession.getId(), consumableItem.getId());

        // Assert
        assertTrue((Boolean) result.get("consumed"));
        assertEquals(20, result.get("hp_change"));
        GameSession updated = gameService.getGameSession(testSession.getId());
        assertEquals(initialHp + 20, updated.getHp());
        assertFalse(gameService.hasItem(testSession.getId(), consumableItem.getId()));
    }

    @Test
    void testUseItem_NonConsumable_AppliesEffectsButDoesNotRemove() {
        // Arrange - Create non-consumable item with effects
        Item nonConsumable = new Item("ring", "Magic Ring", "Increases max HP", "equipment", 
            "{\"max_hp_change\": 20}", false);
        itemRepository.save(nonConsumable);
        gameService.addItem(testSession.getId(), nonConsumable.getId(), 1);
        int initialMaxHp = testSession.getMaxHp();

        // Act
        Map<String, Object> result = gameService.useItem(testSession.getId(), nonConsumable.getId());

        // Assert
        assertFalse((Boolean) result.get("consumed"));
        assertEquals(20, result.get("max_hp_change"));
        GameSession updated = gameService.getGameSession(testSession.getId());
        assertEquals(initialMaxHp + 20, updated.getMaxHp());
        assertTrue(gameService.hasItem(testSession.getId(), nonConsumable.getId()));
    }

    @Test
    void testUseItem_ByCode_UsesItem() {
        // Arrange
        gameService.addItem(testSession.getId(), consumableItem.getId(), 1);

        // Act
        Map<String, Object> result = gameService.useItemByCode(testSession.getId(), "health_potion");

        // Assert
        assertTrue((Boolean) result.get("consumed"));
        assertEquals(20, result.get("hp_change"));
    }

    @Test
    void testUseItem_NotInInventory_ThrowsException() {
        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            gameService.useItem(testSession.getId(), testItem.getId());
        });
    }

    @Test
    void testUseItem_ZeroQuantity_ThrowsException() {
        // Arrange - Add then remove all
        gameService.addItem(testSession.getId(), testItem.getId(), 1);
        gameService.removeItem(testSession.getId(), testItem.getId(), 1);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            gameService.useItem(testSession.getId(), testItem.getId());
        });
    }

    @Test
    void testHasItem_ItemExists_ReturnsTrue() {
        // Arrange
        gameService.addItem(testSession.getId(), testItem.getId(), 1);

        // Act
        boolean hasItem = gameService.hasItem(testSession.getId(), testItem.getId());

        // Assert
        assertTrue(hasItem);
    }

    @Test
    void testHasItem_ItemNotExists_ReturnsFalse() {
        // Act
        boolean hasItem = gameService.hasItem(testSession.getId(), testItem.getId());

        // Assert
        assertFalse(hasItem);
    }

    @Test
    void testHasItem_ByCode_ChecksByCode() {
        // Arrange
        gameService.addItem(testSession.getId(), testItem.getId(), 1);

        // Act
        boolean hasItem = gameService.hasItemByCode(testSession.getId(), "test_sword");

        // Assert
        assertTrue(hasItem);
    }

    @Test
    void testGetItemQuantity_ItemExists_ReturnsQuantity() {
        // Arrange
        gameService.addItem(testSession.getId(), testItem.getId(), 5);

        // Act
        int quantity = gameService.getItemQuantity(testSession.getId(), testItem.getId());

        // Assert
        assertEquals(5, quantity);
    }

    @Test
    void testGetItemQuantity_ItemNotExists_ReturnsZero() {
        // Act
        int quantity = gameService.getItemQuantity(testSession.getId(), testItem.getId());

        // Assert
        assertEquals(0, quantity);
    }

    @Test
    void testGetItemQuantity_ByCode_ReturnsQuantity() {
        // Arrange
        gameService.addItem(testSession.getId(), testItem.getId(), 3);

        // Act
        int quantity = gameService.getItemQuantityByCode(testSession.getId(), "test_sword");

        // Assert
        assertEquals(3, quantity);
    }

    @Test
    void testGetAllItems_ReturnsAllItems() {
        // Act
        List<Item> items = gameService.getAllItems();

        // Assert
        assertTrue(items.size() >= 2); // At least our test items
        assertTrue(items.stream().anyMatch(i -> i.getCode().equals("test_sword")));
        assertTrue(items.stream().anyMatch(i -> i.getCode().equals("health_potion")));
    }

    @Test
    void testGetItemByCode_ReturnsItem() {
        // Act
        Item item = gameService.getItemByCode("test_sword");

        // Assert
        assertNotNull(item);
        assertEquals("test_sword", item.getCode());
        assertEquals("Test Sword", item.getName());
    }

    @Test
    void testGetItemById_ReturnsItem() {
        // Act
        Item item = gameService.getItemById(testItem.getId());

        // Assert
        assertNotNull(item);
        assertEquals(testItem.getId(), item.getId());
        assertEquals("test_sword", item.getCode());
    }

    @Test
    void testGetPlayerInventory_MultipleItems_ReturnsAll() {
        // Arrange - Add multiple items
        gameService.addItem(testSession.getId(), testItem.getId(), 2);
        gameService.addItem(testSession.getId(), consumableItem.getId(), 3);

        // Act
        List<PlayerInventory> inventory = gameService.getPlayerInventory(testSession.getId());

        // Assert
        assertEquals(2, inventory.size());
        assertTrue(inventory.stream().anyMatch(inv -> inv.getItem().getCode().equals("test_sword")));
        assertTrue(inventory.stream().anyMatch(inv -> inv.getItem().getCode().equals("health_potion")));
    }

    @Test
    void testUseItem_WithHpChange_ModifiesHp() {
        // Arrange
        gameService.setHp(testSession.getId(), 50);
        gameService.addItem(testSession.getId(), consumableItem.getId(), 1);

        // Act
        gameService.useItem(testSession.getId(), consumableItem.getId());

        // Assert
        GameSession updated = gameService.getGameSession(testSession.getId());
        assertEquals(70, updated.getHp()); // 50 + 20
    }

    @Test
    void testUseItem_WithMaxHpChange_ModifiesMaxHp() {
        // Arrange - Create item that increases max HP
        Item maxHpItem = new Item("vitality_ring", "Vitality Ring", "Increases max HP", "equipment",
            "{\"max_hp_change\": 50}", true);
        itemRepository.save(maxHpItem);
        gameService.addItem(testSession.getId(), maxHpItem.getId(), 1);
        int initialMaxHp = testSession.getMaxHp();

        // Act
        gameService.useItem(testSession.getId(), maxHpItem.getId());

        // Assert
        GameSession updated = gameService.getGameSession(testSession.getId());
        assertEquals(initialMaxHp + 50, updated.getMaxHp());
    }

    @Test
    void testInventoryPersistence_AddItem_PersistsCorrectly() {
        // Act
        gameService.addItem(testSession.getId(), testItem.getId(), 5);
        List<PlayerInventory> inventory = gameService.getPlayerInventory(testSession.getId());

        // Assert
        assertEquals(1, inventory.size());
        assertEquals(5, inventory.get(0).getQuantity());
    }
}

