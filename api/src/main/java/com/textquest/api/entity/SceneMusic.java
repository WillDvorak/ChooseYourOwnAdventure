package main.java.com.textquest.api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.Objects;


@Entity
@Table(name = "scene_music")
public class SceneMusic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The Scene this music belongs to. Each scene may define exactly one music track.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scene_id", nullable = false, unique = true)
    @NotNull
    private Scene scene;

    /**
     * Display name of the track.
     */
    @Column(name = "title", nullable = false, length = 128)
    @NotBlank
    @Size(max = 128)
    private String title;

    /**
     * Direct link or path to the audio asset (file system, CDN URL, S3 bucket, etc.)
     */
    @Column(name = "audio_url", nullable = false, length = 512)
    @NotBlank
    @Size(max = 512)
    private String audioUrl;

    /**
     * Whether the track should loop continuously during scene playback.
     */
    @Column(name = "loop", nullable = false)
    private boolean loop = true;

    /**
     * Playback volume (0.0 = silent, 1.0 = normal, up to optional amplification).
     */
    @Column(name = "volume", nullable = false)
    @DecimalMin("0.0")
    @DecimalMax("3.0")   // allows slight amplification but prevents extremes
    private float volume = 1.0f;

    /**
     * Optional fade-in duration in milliseconds.
     * Used to smooth transitions when the player enters a scene.
     */
    @Column(name = "fade_in_ms")
    @Min(0)
    private Integer fadeInMs;

    /**
     * Optional fade-out duration in milliseconds.
     */
    @Column(name = "fade_out_ms")
    @Min(0)
    private Integer fadeOutMs;

    /**
     * Timestamps for auditing and history tracking.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // ---------------------------------------------------------
    // Constructors
    // ---------------------------------------------------------

    public SceneMusic() {}

    public SceneMusic(Scene scene, String title, String audioUrl) {
        this.scene = scene;
        this.title = title;
        this.audioUrl = audioUrl;
    }

    // Builder for clean object creation
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final SceneMusic m = new SceneMusic();

        public Builder scene(Scene s) { m.setScene(s); return this; }
        public Builder title(String t) { m.setTitle(t); return this; }
        public Builder audioUrl(String url) { m.setAudioUrl(url); return this; }
        public Builder loop(boolean val) { m.setLoop(val); return this; }
        public Builder volume(float val) { m.setVolume(val); return this; }
        public Builder fadeInMs(Integer ms) { m.setFadeInMs(ms); return this; }
        public Builder fadeOutMs(Integer ms) { m.setFadeOutMs(ms); return this; }

        public SceneMusic build() { return m; }
    }

    // Lifecycle Hooks

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // DTO for frontend playback info

    /**
     * Creates a lightweight DTO for client playback.
     */
    public MusicPlaybackInfo toPlaybackInfo() {
        return new MusicPlaybackInfo(title, audioUrl, loop, volume, fadeInMs, fadeOutMs);
    }

    public static class MusicPlaybackInfo {
        private final String title;
        private final String audioUrl;
        private final boolean loop;
        private final float volume;
        private final Integer fadeInMs;
        private final Integer fadeOutMs;

        public MusicPlaybackInfo(
                String title,
                String audioUrl,
                boolean loop,
                float volume,
                Integer fadeInMs,
                Integer fadeOutMs
        ) {
            this.title = title;
            this.audioUrl = audioUrl;
            this.loop = loop;
            this.volume = volume;
            this.fadeInMs = fadeInMs;
            this.fadeOutMs = fadeOutMs;
        }

        public String getTitle() { return title; }
        public String getAudioUrl() { return audioUrl; }
        public boolean isLoop() { return loop; }
        public float getVolume() { return volume; }
        public Integer getFadeInMs() { return fadeInMs; }
        public Integer getFadeOutMs() { return fadeOutMs; }
    }

    // Getters & Setters

    public Long getId() { return id; }

    public Scene getScene() { return scene; }

    public void setScene(Scene scene) {
        if (scene == null) throw new IllegalArgumentException("Scene cannot be null.");
        this.scene = scene;
    }

    public String getTitle() { return title; }

    public void setTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be blank.");
        }
        this.title = title;
    }

    public String getAudioUrl() { return audioUrl; }

    public void setAudioUrl(String audioUrl) {
        if (audioUrl == null || audioUrl.isBlank()) {
            throw new IllegalArgumentException("Audio URL cannot be blank.");
        }
        this.audioUrl = audioUrl;
    }

    public boolean isLoop() { return loop; }

    public void setLoop(boolean loop) { this.loop = loop; }

    public float getVolume() { return volume; }

    public void setVolume(float volume) {
        if (volume < 0f || volume > 3f) {
            throw new IllegalArgumentException("Volume must be between 0.0 and 3.0.");
        }
        this.volume = volume;
    }

    public Integer getFadeInMs() { return fadeInMs; }

    public void setFadeInMs(Integer fadeInMs) {
        if (fadeInMs != null && fadeInMs < 0) {
            throw new IllegalArgumentException("Fade-in time must be >= 0.");
        }
        this.fadeInMs = fadeInMs;
    }

    public Integer getFadeOutMs() { return fadeOutMs; }

    public void setFadeOutMs(Integer fadeOutMs) {
        if (fadeOutMs != null && fadeOutMs < 0) {
            throw new IllegalArgumentException("Fade-out time must be >= 0.");
        }
        this.fadeOutMs = fadeOutMs;
    }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // equals & hashCode

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SceneMusic)) return false;
        SceneMusic that = (SceneMusic) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    // toString

    @Override
    public String toString() {
        return "SceneMusic{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", audioUrl='" + audioUrl + '\'' +
                ", loop=" + loop +
                ", volume=" + volume +
                ", fadeInMs=" + fadeInMs +
                ", fadeOutMs=" + fadeOutMs +
                ", sceneCode='" + (scene != null ? scene.getCode() : null) + '\'' +
                '}';
    }
}
