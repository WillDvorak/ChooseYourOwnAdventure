public class SceneMusicTest {


@Test
public void testPlaybackInfo() {
    Scene scene = new Scene();
    scene.setCode("TEST_SCENE");


    SceneMusic music = new SceneMusic(scene, "Test Track", "http://example.com/audio.mp3");
    music.setLoop(false);
    music.setVolume(0.5f);


    SceneMusic.MusicPlaybackInfo info = music.getPlaybackInfo();


    assertEquals("Test Track", info.getTitle());
    assertEquals("http://example.com/audio.mp3", info.getAudioUrl());
    assertFalse(info.isLoop());
    assertEquals(0.5f, info.getVolume());
    }


@Test
public void testTimestampsOnCreate() 
    {
    SceneMusic music = new SceneMusic();
    music.onCreate();


    assertNotNull(music.getCreatedAt());
    assertNotNull(music.getUpdatedAt());
    assertEquals(music.getCreatedAt(), music.getUpdatedAt());
    }


@Test
public void testTimestampOnUpdate() {
    SceneMusic music = new SceneMusic();
    music.onCreate();
    LocalDateTime created = music.getCreatedAt();


    music.onUpdate();
    assertTrue(music.getUpdatedAt().isAfter(created));
    }
}