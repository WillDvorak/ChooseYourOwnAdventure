package main.java.com.textquest.api.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Represents a sound effect linked to a specific item or user action.
 * For example:
 *  - Torch ignition or crackle
 *  - Sword swing
 *  - Button click or inventory open
 * 
 * The frontend can query this to play context-specific sound effects.
 */
@Entity
@Table(name = "sound_effects")
public class SoundEffect {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Optional association to an item in the game (e.g., "Torch", "Key", etc.)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    // The type of trigger for the sound (e.g. ITEM_USE, BUTTON_CLICK, INVENTORY_OPEN)
    @Enumerated(EnumType.STRING)
    @Column(name = "trigger_type", nullable = false, length = 64)
    private TriggerType triggerType;

    // Display name of the sound (e.g., "Torch Crackle", "Button Click")
    @Column(name = "name", nullable = false, length = 128)
    private String name;

    // URL or file path to the sound file
    @Column(name = "audio_url", nullable = false, length = 512)
    private String audioUrl;

    // Volume of the sound (0.0 = silent, 1.0 = full)
    @Column(name = "volume", nullable = false)
    private float volume = 1.0f;

    // Whether to loop the sound (e.g., continuous torch sound)
    @Column(name = "loop", nullable = false)
    private boolean loop = false;

    // Audit timestamps
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Constructors
    public SoundEffect() {}

    public SoundEffect(TriggerType triggerType, String name, String audioUrl) {
        this.triggerType = triggerType;
        this.name = name;
        this.audioUrl = audioUrl;
    }

    public SoundEffect(Item item, TriggerType triggerType, String name, String audioUrl) {
        this.item = item;
        this.triggerType = triggerType;
        this.name = name;
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

    // --- Business Logic ---
    /**
     * Returns a DTO for frontend playback.
     */
    public SoundPlaybackInfo getPlaybackInfo() {
        return new SoundPlaybackInfo(name, audioUrl, volume, loop);
    }

    // --- Getters and Setters ---
    public Long getId() {
        return id;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public TriggerType getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(TriggerType triggerType) {
        this.triggerType = triggerType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public boolean isLoop() {
        return loop;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public String toString() {
        return "SoundEffect{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", triggerType=" + triggerType +
                ", item=" + (item != null ? item.getName() : null) +
                ", audioUrl='" + audioUrl + '\'' +
                ", volume=" + volume +
                ", loop=" + loop +
                '}';
    }

    // --- Inner DTO class for frontend playback ---
    public static class SoundPlaybackInfo {
        private String name;
        private String audioUrl;
        private float volume;
        private boolean loop;

        public SoundPlaybackInfo(String name, String audioUrl, float volume, boolean loop) {
            this.name = name;
            this.audioUrl = audioUrl;
            this.volume = volume;
            this.loop = loop;
        }

        public String getName() {
            return name;
        }

        public String getAudioUrl() {
            return audioUrl;
        }

        public float getVolume() {
            return volume;
        }

        public boolean isLoop() {
            return loop;
        }
    }

    // --- Trigger Type Enum ---
    public enum TriggerType {
        ITEM_USE,
        ITEM_EQUIP,
        ITEM_DROP,
        TORCH_IGNITE,
        TORCH_BURN,
        BUTTON_CLICK,
        INVENTORY_OPEN,
        INVENTORY_CLOSE
    }
}
