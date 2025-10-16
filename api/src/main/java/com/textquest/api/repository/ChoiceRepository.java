package com.textquest.api.repository;

import com.textquest.api.entity.Choice;
import com.textquest.api.entity.Scene;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChoiceRepository extends JpaRepository<Choice, Long> {
    
    /**
     * Find all choices for a specific scene
     */
    List<Choice> findByScene(Scene scene);
    
    /**
     * Find all choices for a scene by scene ID
     */
    List<Choice> findBySceneId(Long sceneId);
    
    /**
     * Find all choices for a scene by scene code
     */
    @Query("SELECT c FROM Choice c WHERE c.scene.code = :sceneCode")
    List<Choice> findBySceneCode(@Param("sceneCode") String sceneCode);
    
    /**
     * Find choices that require a specific flag
     */
    List<Choice> findByRequiresFlag(String requiresFlag);
    
    /**
     * Find choices that set a specific flag
     */
    List<Choice> findBySetsFlag(String setsFlag);
    
    /**
     * Find choices that don't require any flag (null or empty)
     */
    @Query("SELECT c FROM Choice c WHERE c.requiresFlag IS NULL OR c.requiresFlag = ''")
    List<Choice> findByRequiresFlagIsNullOrEmpty();
    
    /**
     * Find choices by label containing the given text (case-insensitive)
     */
    List<Choice> findByLabelContainingIgnoreCase(String label);
    
    /**
     * Find choices that target a specific scene code
     */
    List<Choice> findByTargetSceneCode(String targetSceneCode);
    
    /**
     * Find available choices for a scene (no flag requirements or player has the required flag)
     * This would need to be implemented with a custom query that checks player flags
     */
    @Query("SELECT c FROM Choice c WHERE c.scene.code = :sceneCode AND (c.requiresFlag IS NULL OR c.requiresFlag = '')")
    List<Choice> findAvailableChoicesForScene(@Param("sceneCode") String sceneCode);
}
