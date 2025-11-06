package com.textquest.api.repository;

import com.textquest.api.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    
    /**
     * Find item by code
     */
    Optional<Item> findByCode(String code);
    
    /**
     * Check if item exists by code
     */
    boolean existsByCode(String code);
    
    /**
     * Find items by type
     */
    List<Item> findByItemType(String itemType);
    
    /**
     * Find consumable items
     */
    List<Item> findByIsConsumableTrue();
    
    /**
     * Find non-consumable items
     */
    List<Item> findByIsConsumableFalse();
    
    /**
     * Search items by name (case-insensitive)
     */
    @Query("SELECT i FROM Item i WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Item> findByNameContainingIgnoreCase(@Param("name") String name);
}

