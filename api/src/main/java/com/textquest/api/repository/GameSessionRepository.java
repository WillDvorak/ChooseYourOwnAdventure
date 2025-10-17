package com.textquest.api.repository;

import com.textquest.api.entity.GameSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameSessionRepository extends JpaRepository<GameSession, Long> {
    
    /**
     * Find game sessions by player name
     */
    List<GameSession> findByPlayerName(String playerName);
    
    /**
     * Find the most recent game session for a player
     */
    @Query("SELECT gs FROM GameSession gs WHERE gs.playerName = :playerName ORDER BY gs.updatedAt DESC")
    Optional<GameSession> findLatestByPlayerName(@Param("playerName") String playerName);
    
    /**
     * Find game sessions by current scene code
     */
    List<GameSession> findByCurrentSceneCode(String currentSceneCode);
    
    /**
     * Find game sessions that have a specific flag in their JSON (simplified)
     */
    @Query("SELECT gs FROM GameSession gs WHERE gs.flagsJson LIKE %:flagValue%")
    List<GameSession> findByFlag(@Param("flagValue") String flagValue);
    
    /**
     * Find game sessions that don't have a specific flag in their JSON (simplified)
     */
    @Query("SELECT gs FROM GameSession gs WHERE gs.flagsJson NOT LIKE %:flagValue%")
    List<GameSession> findByFlagNotExists(@Param("flagValue") String flagValue);
    
    /**
     * Check if a player has an active game session
     */
    boolean existsByPlayerName(String playerName);
    
    /**
     * Count game sessions by player name
     */
    long countByPlayerName(String playerName);
    
    /**
     * Find game sessions created after a specific date
     */
    @Query("SELECT gs FROM GameSession gs WHERE gs.createdAt > :date")
    List<GameSession> findCreatedAfter(@Param("date") java.time.LocalDateTime date);
    
    /**
     * Find game sessions updated after a specific date
     */
    @Query("SELECT gs FROM GameSession gs WHERE gs.updatedAt > :date")
    List<GameSession> findUpdatedAfter(@Param("date") java.time.LocalDateTime date);
    
    /**
     * Find all game sessions ordered by most recently updated
     */
    @Query("SELECT gs FROM GameSession gs ORDER BY gs.updatedAt DESC")
    List<GameSession> findAllOrderByUpdatedAtDesc();
}
