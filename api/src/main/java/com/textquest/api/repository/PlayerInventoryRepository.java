package com.textquest.api.repository;

import com.textquest.api.entity.PlayerInventory;
import com.textquest.api.entity.GameSession;
import com.textquest.api.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerInventoryRepository extends JpaRepository<PlayerInventory, Long> {
    
    /**
     * Find all inventory items for a game session
     */
    List<PlayerInventory> findByGameSession(GameSession gameSession);
    
    /**
     * Find all inventory items for a game session ID
     */
    @Query("SELECT pi FROM PlayerInventory pi WHERE pi.gameSession.id = :sessionId")
    List<PlayerInventory> findBySessionId(@Param("sessionId") Long sessionId);
    
    /**
     * Find a specific item in a player's inventory
     */
    Optional<PlayerInventory> findByGameSessionAndItem(GameSession gameSession, Item item);
    
    /**
     * Find a specific item in a player's inventory by session ID and item ID
     */
    @Query("SELECT pi FROM PlayerInventory pi WHERE pi.gameSession.id = :sessionId AND pi.item.id = :itemId")
    Optional<PlayerInventory> findBySessionIdAndItemId(@Param("sessionId") Long sessionId, @Param("itemId") Long itemId);
    
    /**
     * Find a specific item in a player's inventory by session ID and item code
     */
    @Query("SELECT pi FROM PlayerInventory pi WHERE pi.gameSession.id = :sessionId AND pi.item.code = :itemCode")
    Optional<PlayerInventory> findBySessionIdAndItemCode(@Param("sessionId") Long sessionId, @Param("itemCode") String itemCode);
    
    /**
     * Check if player has an item
     */
    boolean existsByGameSessionAndItem(GameSession gameSession, Item item);
    
    /**
     * Count total items in inventory for a session
     */
    @Query("SELECT COUNT(pi) FROM PlayerInventory pi WHERE pi.gameSession.id = :sessionId")
    long countBySessionId(@Param("sessionId") Long sessionId);
    
    /**
     * Delete all inventory for a session
     */
    void deleteByGameSession(GameSession gameSession);
}

