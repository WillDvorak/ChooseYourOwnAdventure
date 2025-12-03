import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;


public class SoundEffectTest {


@Test
public void testPlaybackInfo() {
    SoundEffect effect = new SoundEffect(
    SoundEffect.TriggerType.BUTTON_CLICK,
    "Click",
    "http://example.com/click.mp3"
    );


    effect.setVolume(0.7f);
    effect.setLoop(true);


    SoundEffect.SoundPlaybackInfo info = effect.getPlaybackInfo();


    assertEquals("Click", info.getName());
    assertEquals("http://example.com/click.mp3", info.getAudioUrl());
    assertEquals(0.7f, info.getVolume());
    assertTrue(info.isLoop());
}


@Test
public void testTimestampsOnCreate() {
    SoundEffect effect = new SoundEffect();
    effect.onCreate();


    assertNotNull(effect.getCreatedAt());
    assertNotNull(effect.getUpdatedAt());
    assertEquals(effect.getCreatedAt(), effect.getUpdatedAt());
}


@Test
public void testTimestampsOnUpdate() {
    SoundEffect effect = new SoundEffect();
    effect.onCreate();
    LocalDateTime created = effect.getCreatedAt();


    effect.onUpdate();
    assertTrue(effect.getUpdatedAt().isAfter(created));
    }
}