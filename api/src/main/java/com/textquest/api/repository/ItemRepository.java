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
    
    Optional<Item> findByCode(String code);
    boolean existsByCode(String code);
    List<Item> findByItemType(String itemType);
    List<Item> findByIsConsumableTrue();
    List<Item> findByIsConsumableFalse();
    
    @Query("SELECT i FROM Item i WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Item> findByNameContainingIgnoreCase(@Param("name") String name);
}

