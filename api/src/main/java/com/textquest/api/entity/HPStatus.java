package com.textquest.api.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "hp_status")
public class HPStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Each HP status belongs to a specific game session
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_session_id", nullable = false)
    private GameSession gameSession;

    // Current health points of the player
    @Column(name = "current_hp", nullable = false)
    private int currentHp;

    // Maximum possible health points
    @Column(name = "max_hp", nullable = false)
    private int maxHp;

    // Timestamp tracking
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public HPStatus() {}

    public HPStatus(GameSession gameSession, int maxHp) {
        this.gameSession = gameSession;
        this.maxHp = maxHp;
        this.currentHp = maxHp; // start full health
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void takeDamage(int amount) {
        this.currentHp = Math.max(0, this.currentHp - amount);
    }

    public void heal(int amount) {
        this.currentHp = Math.min(this.maxHp, this.currentHp + amount);
    }

    public double getHealthPercentage() {
        if (maxHp == 0) return 0;
        return ((double) currentHp / maxHp) * 100;
    }

    public Long getId() {
        return id;
    }

    public GameSession getGameSession() {
        return gameSession;
    }

    public void setGameSession(GameSession gameSession) {
        this.gameSession = gameSession;
    }

    public int getCurrentHp() {
        return currentHp;
    }

    public void setCurrentHp(int currentHp) {
        this.currentHp = Math.max(0, Math.min(currentHp, this.maxHp));
    }

    public int getMaxHp() {
        return maxHp;
    }

    public void setMaxHp(int maxHp) {
        this.maxHp = maxHp;
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
        return "HPStatus{" +
                "id=" + id +
                ", currentHp=" + currentHp +
                ", maxHp=" + maxHp +
                ", gameSessionId=" + (gameSession != null ? gameSession.getId() : null) +
                '}';
    }
}
