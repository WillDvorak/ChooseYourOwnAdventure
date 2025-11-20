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
    
    List<PlayerInventory> findByGameSession(GameSession gameSession);
    
    @Query("SELECT pi FROM PlayerInventory pi WHERE pi.gameSession.id = :sessionId")
    List<PlayerInventory> findBySessionId(@Param("sessionId") Long sessionId);
    
    Optional<PlayerInventory> findByGameSessionAndItem(GameSession gameSession, Item item);
    
    @Query("SELECT pi FROM PlayerInventory pi WHERE pi.gameSession.id = :sessionId AND pi.item.id = :itemId")
    Optional<PlayerInventory> findBySessionIdAndItemId(@Param("sessionId") Long sessionId, @Param("itemId") Long itemId);
    
    @Query("SELECT pi FROM PlayerInventory pi WHERE pi.gameSession.id = :sessionId AND pi.item.code = :itemCode")
    Optional<PlayerInventory> findBySessionIdAndItemCode(@Param("sessionId") Long sessionId, @Param("itemCode") String itemCode);
    
    boolean existsByGameSessionAndItem(GameSession gameSession, Item item);
    
    @Query("SELECT COUNT(pi) FROM PlayerInventory pi WHERE pi.gameSession.id = :sessionId")
    long countBySessionId(@Param("sessionId") Long sessionId);
    
    void deleteByGameSession(GameSession gameSession);
}

