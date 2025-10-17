package com.textquest.api.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "game_sessions")
public class GameSession {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "player_name", nullable = false, length = 64)
    private String playerName;
    
    @Column(name = "current_scene_code", nullable = false, length = 64)
    private String currentSceneCode;
    
    @Column(name = "flags_json", nullable = false, columnDefinition = "JSON")
    private String flagsJson;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // Constructors
    public GameSession() {
        this.flagsJson = "{}"; // Default empty JSON object
    }
    
    public GameSession(String playerName, String currentSceneCode) {
        this.playerName = playerName;
        this.currentSceneCode = currentSceneCode;
        this.flagsJson = "{}"; // Default empty JSON object
    }
    
    public GameSession(String playerName, String currentSceneCode, String flagsJson) {
        this.playerName = playerName;
        this.currentSceneCode = currentSceneCode;
        this.flagsJson = flagsJson != null ? flagsJson : "{}";
    }
    
    // JPA lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
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
    
    public String getPlayerName() {
        return playerName;
    }
    
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
    
    public String getCurrentSceneCode() {
        return currentSceneCode;
    }
    
    public void setCurrentSceneCode(String currentSceneCode) {
        this.currentSceneCode = currentSceneCode;
    }
    
    public String getFlagsJson() {
        return flagsJson;
    }
    
    public void setFlagsJson(String flagsJson) {
        this.flagsJson = flagsJson != null ? flagsJson : "{}";
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
        return "GameSession{" +
                "id=" + id +
                ", playerName='" + playerName + '\'' +
                ", currentSceneCode='" + currentSceneCode + '\'' +
                ", flagsJson='" + flagsJson + '\'' +
                '}';
    }
}
