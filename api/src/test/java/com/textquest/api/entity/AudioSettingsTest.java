import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;


public class AudioSettingsTest {


@Test
public void testDefaults() {
    AudioSettings settings = new AudioSettings();


    assertEquals(1.0f, settings.getMusicVolume());
    assertEquals(1.0f, settings.getSfxVolume());
    assertFalse(settings.isMusicMuted());
    assertFalse(settings.isSfxMuted());
    }


@Test
public void testMuteToggle() {
    AudioSettings settings = new AudioSettings();


    settings.setMusicMuted(true);
    settings.setSfxMuted(true);


    assertTrue(settings.isMusicMuted());
    assertTrue(settings.isSfxMuted());
    }


@Test
public void testVolumeSetting() {
    AudioSettings settings = new AudioSettings();


    settings.setMusicVolume(0.3f);
    settings.setSfxVolume(0.8f);


    assertEquals(0.3f, settings.getMusicVolume());
    assertEquals(0.8f, settings.getSfxVolume());
    }


@Test
public void testTimestampsOnCreate() {
    AudioSettings settings = new AudioSettings();
    settings.onCreate();


    assertNotNull(settings.getCreatedAt());
    assertNotNull(settings.getUpdatedAt());
    assertEquals(settings.getCreatedAt(), settings.getUpdatedAt());
    }


@Test
public void testTimestampOnUpdate() {
    AudioSettings settings = new AudioSettings();
    settings.onCreate();
    LocalDateTime created = settings.getCreatedAt();


    settings.onUpdate();
    assertTrue(settings.getUpdatedAt().isAfter(created));
    }
}