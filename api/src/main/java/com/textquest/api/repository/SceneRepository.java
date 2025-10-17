package com.textquest.api.repository;

import com.textquest.api.entity.Scene;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SceneRepository extends JpaRepository<Scene, Long> {
    
    /**
     * Find a scene by its unique code
     */
    Optional<Scene> findByCode(String code);
    
    /**
     * Find all terminal scenes (end scenes)
     */
    List<Scene> findByIsTerminalTrue();
    
    /**
     * Find all non-terminal scenes
     */
    List<Scene> findByIsTerminalFalse();
    
    /**
     * Check if a scene exists by code
     */
    boolean existsByCode(String code);
    
    /**
     * Find scenes by title containing the given text (case-insensitive)
     */
    List<Scene> findByTitleContainingIgnoreCase(String title);
    
    /**
     * Find scenes by body containing the given text (case-insensitive)
     */
    List<Scene> findByBodyContainingIgnoreCase(String body);
    
    /**
     * Custom query to find scenes with their choices loaded
     */
    @Query("SELECT s FROM Scene s LEFT JOIN FETCH s.choices WHERE s.code = :code")
    Optional<Scene> findByCodeWithChoices(@Param("code") String code);
    
    /**
     * Find all scenes with their choices loaded
     */
    @Query("SELECT s FROM Scene s LEFT JOIN FETCH s.choices")
    List<Scene> findAllWithChoices();
}
