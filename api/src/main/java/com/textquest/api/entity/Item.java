package com.textquest.api.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "items")
public class Item {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "code", nullable = false, unique = true, length = 64)
    private String code;
    
    @Column(name = "name", nullable = false, length = 128)
    private String name;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "item_type", nullable = false, length = 64)
    private String itemType;
    
    @Column(name = "effects_json", columnDefinition = "JSON")
    private String effectsJson;
    
    @Column(name = "is_consumable", nullable = false)
    private Boolean isConsumable;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // Constructors
    public Item() {
        this.isConsumable = false;
        this.effectsJson = "{}";
    }
    
    public Item(String code, String name, String description, String itemType, String effectsJson, Boolean isConsumable) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.itemType = itemType;
        this.effectsJson = effectsJson != null ? effectsJson : "{}";
        this.isConsumable = isConsumable != null ? isConsumable : false;
    }
    
    // JPA lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (effectsJson == null) {
            effectsJson = "{}";
        }
        if (isConsumable == null) {
            isConsumable = false;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getItemType() {
        return itemType;
    }
    
    public void setItemType(String itemType) {
        this.itemType = itemType;
    }
    
    public String getEffectsJson() {
        return effectsJson;
    }
    
    public void setEffectsJson(String effectsJson) {
        this.effectsJson = effectsJson != null ? effectsJson : "{}";
    }
    
    public Boolean getIsConsumable() {
        return isConsumable;
    }
    
    public void setIsConsumable(Boolean isConsumable) {
        this.isConsumable = isConsumable;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", itemType='" + itemType + '\'' +
                ", isConsumable=" + isConsumable +
                '}';
    }
}

