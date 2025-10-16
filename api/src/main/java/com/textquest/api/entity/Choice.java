package com.textquest.api.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "choices")
public class Choice {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scene_id", nullable = false)
    private Scene scene;
    
    @Column(name = "label", nullable = false, length = 255)
    private String label;
    
    @Column(name = "target_scene_code", nullable = false, length = 64)
    private String targetSceneCode;
    
    @Column(name = "requires_flag", length = 64)
    private String requiresFlag;
    
    @Column(name = "sets_flag", length = 64)
    private String setsFlag;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // Constructors
    public Choice() {}
    
    public Choice(Scene scene, String label, String targetSceneCode, String requiresFlag, String setsFlag) {
        this.scene = scene;
        this.label = label;
        this.targetSceneCode = targetSceneCode;
        this.requiresFlag = requiresFlag;
        this.setsFlag = setsFlag;
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
    
    public Scene getScene() {
        return scene;
    }
    
    public void setScene(Scene scene) {
        this.scene = scene;
    }
    
    public String getLabel() {
        return label;
    }
    
    public void setLabel(String label) {
        this.label = label;
    }
    
    public String getTargetSceneCode() {
        return targetSceneCode;
    }
    
    public void setTargetSceneCode(String targetSceneCode) {
        this.targetSceneCode = targetSceneCode;
    }
    
    public String getRequiresFlag() {
        return requiresFlag;
    }
    
    public void setRequiresFlag(String requiresFlag) {
        this.requiresFlag = requiresFlag;
    }
    
    public String getSetsFlag() {
        return setsFlag;
    }
    
    public void setSetsFlag(String setsFlag) {
        this.setsFlag = setsFlag;
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
        return "Choice{" +
                "id=" + id +
                ", label='" + label + '\'' +
                ", targetSceneCode='" + targetSceneCode + '\'' +
                ", requiresFlag='" + requiresFlag + '\'' +
                ", setsFlag='" + setsFlag + '\'' +
                '}';
    }
}
