package com.textquest.api.controller;

import com.textquest.api.entity.Item;
import com.textquest.api.repository.ItemRepository;

import java.util.List;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/items")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost"})
public class ItemController {

    private final ItemRepository itemRepository;

    public ItemController(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @GetMapping("/all")
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    @GetMapping("/{label}")
    public Item getByLabel(@PathVariable String label) {
        return itemRepository.findByLabel(label)
                .orElseThrow(() -> new RuntimeException("Item not found: " + label));
    }
}