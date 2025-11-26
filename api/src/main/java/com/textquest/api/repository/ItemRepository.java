package com.textquest.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.textquest.api.entity.Item;

import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Optional<Item> findByLabel(String label);
}

