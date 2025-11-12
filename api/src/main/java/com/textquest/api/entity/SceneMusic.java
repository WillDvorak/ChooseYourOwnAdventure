package main.java.com.textquest.api.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Represents background music associated with a specific Scene.
 * This allows the frontend to fetch and play the right track when the player enters a scene.
 */
@Entity
@Table(name = "scene_music")
public class SceneMusic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Each music entry is tied to a specific scene
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scene_id", nullable = false, unique = true)
    private Scene scene;

    // The display name of the track (e.g., "Haunted Theme", "Peaceful Meadow")
    @Column(name = "title", nullable = false, length = 128)
    private String title;

    // URL or file path to the music file (could be a hosted asset or S3 bucket link)
    @Column(name = "audio_url", nullable = false, length = 512)
    private String audioUrl;

    // Optional: volume or looping metadata for frontend playback control
    @Column(name = "loop", nullable = false)
    private boolean loop = true;

    @Column(name = "volume", nullable = false)
    private float volume = 1.0f; // 1.0 = 100%

    // Timestamps for auditing
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Constructors
    public SceneMusic() {}

    public SceneMusic(Scene scene, String title, String audioUrl) {
        this.scene = scene;
        this.title = title;
        this.audioUrl = audioUrl;
    }

    // Lifecycle hooks
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public MusicPlaybackInfo getPlaybackInfo() {
        return new MusicPlaybackInfo(title, audioUrl, loop, volume);
    }

    // --- Getters and Setters ---
    public Long getId() {
        return id;
    }

    public Scene getScene() {
        return scene;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public boolean isLoop() {
        return loop;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public String toString() {
        return "SceneMusic{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", audioUrl='" + audioUrl + '\'' +
                ", loop=" + loop +
                ", volume=" + volume +
                ", sceneCode='" + (scene != null ? scene.getCode() : null) + '\'' +
                '}';
    }

    // --- Inner DTO class for frontend playback ---
    public static class MusicPlaybackInfo {
        private String title;
        private String audioUrl;
        private boolean loop;
        private float volume;

        public MusicPlaybackInfo(String title, String audioUrl, boolean loop, float volume) {
            this.title = title;
            this.audioUrl = audioUrl;
            this.loop = loop;
            this.volume = volume;
        }

        public String getTitle() {
            return title;
        }

        public String getAudioUrl() {
            return audioUrl;
        }

        public boolean isLoop() {
            return loop;
        }

        public float getVolume() {
            return volume;
        }
    }
}
