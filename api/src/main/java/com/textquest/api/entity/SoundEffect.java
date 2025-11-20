package main.java.com.textquest.api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a sound effect triggered by a specific in-game action or item interaction.
 *
 * <p>Common examples include:</p>
 * <ul>
 *     <li>Torch ignition or crackling</li>
 *     <li>Sword swing, hit, or block</li>
 *     <li>Button click, UI interaction, inventory open/close</li>
 *     <li>Item pickup, drop, equip, or use</li>
 * </ul>
 *
 * <p>The frontend uses these entries to play context-aware audio that matches player actions.</p>
 */
@Entity
@Table(name = "sound_effects")
public class SoundEffect {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Optional association to an item that triggers this sound.
     * For example: Torch → Torch Ignite, Torch Crackle, etc.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    /**
     * Indicates what action or event triggers this sound.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "trigger_type", nullable = false, length = 64)
    @NotNull
    private TriggerType triggerType;

    /**
     * Human-friendly name of the sound, such as "Torch Crackle" or "Button Click".
     */
    @Column(name = "name", nullable = false, length = 128)
    @NotBlank
    @Size(max = 128)
    private String name;

    /**
     * Path or URL to the audio file (local, CDN, or cloud storage).
     */
    @Column(name = "audio_url", nullable = false, length = 512)
    @NotBlank
    @Size(max = 512)
    private String audioUrl;;}
