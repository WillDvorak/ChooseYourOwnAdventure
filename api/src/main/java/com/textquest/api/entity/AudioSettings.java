package main.java.com.textquest.api.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Stores global audio settings for a player or for the game system.
 * Includes sound effect volume, background music volume, and mute toggles.
 */
@Entity
@Table(name = "audio_settings")
public class AudioSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "music_muted", nullable = false)
    private boolean musicMuted = false;

    @Column(name = "sfx_muted", nullable = false)
    private boolean soundEffectsMuted = false;

    @Column(name = "music_volume", nullable = false)
    private float musicVolume = 1.0f; // 0–1.0

    @Column(name = "sfx_volume", nullable = false)
    private float soundEffectsVolume = 1.0f; // 0–1.0

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public AudioSettings() {}

    public AudioSettings(boolean musicMuted, boolean soundEffectsMuted, float musicVolume, float soundEffectsVolume) {
        this.musicMuted = musicMuted;
        this.soundEffectsMuted = soundEffectsMuted;
        this.musicVolume = musicVolume;
        this.soundEffectsVolume = soundEffectsVolume;
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

    // Getters / Setters
    public Long getId() {
        return id;
    }

    public boolean isMusicMuted() {
        return musicMuted;
    }

    public void setMusicMuted(boolean musicMuted) {
        this.musicMuted = musicMuted;
    }

    public boolean isSoundEffectsMuted() {
        return soundEffectsMuted;
    }

    public void setSoundEffectsMuted(boolean soundEffectsMuted) {
        this.soundEffectsMuted = soundEffectsMuted;
    }

    public float getMusicVolume() {
        return musicVolume;
    }

    public void setMusicVolume(float musicVolume) {
        this.musicVolume = musicVolume;
    }

    public float getSoundEffectsVolume() {
        return soundEffectsVolume;
    }

    public void setSoundEffectsVolume(float soundEffectsVolume) {
        this.soundEffectsVolume = soundEffectsVolume;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public String toString() {
        return "AudioSettings{" +
                "id=" + id +
                ", musicMuted=" + musicMuted +
                ", soundEffectsMuted=" + soundEffectsMuted +
                ", musicVolume=" + musicVolume +
                ", soundEffectsVolume=" + soundEffectsVolume +
                '}';
    }

    public static class AudioSettingsDTO {
        private boolean musicMuted;
        private boolean soundEffectsMuted;
        private float musicVolume;
        private float soundEffectsVolume;

        public AudioSettingsDTO(boolean musicMuted, boolean soundEffectsMuted, float musicVolume, float soundEffectsVolume) {
            this.musicMuted = musicMuted;
            this.soundEffectsMuted = soundEffectsMuted;
            this.musicVolume = musicVolume;
            this.soundEffectsVolume = soundEffectsVolume;
        }

        public boolean isMusicMuted() {
            return musicMuted;
        }

        public boolean isSoundEffectsMuted() {
            return soundEffectsMuted;
        }

        public float getMusicVolume() {
            return musicVolume;
        }

        public float getSoundEffectsVolume() {
            return soundEffectsVolume;
        }
    }
}
