package org.example.realengine.demo;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Audio {
    public static final Audio DEFAULT_AUDIO = new Audio("resources/audio/02. Title.wav");
    public static final Audio OVERWORLD = new Audio("resources/audio/12.20Overworld.wav");
    public static final Audio CAVE = new Audio("resources/audio/21.20Underground2028Yoshi29.wav");
    public final static Map<String, Audio> musicMap = new HashMap<>(Map.of(
            "resources\\maps\\map_1.png", OVERWORLD,
            "resources\\maps\\map_2.png", OVERWORLD,
            "resources\\maps\\map_3.png", CAVE,
            "resources\\maps\\map_4.png", CAVE
    ));
    private final String filePath;
    private Clip clip;
    private boolean infiniteLoop = false;
    private AudioInputStream audioInputStream;

    public Audio(String filePath) {
        this.filePath = filePath;
    }

    @Deprecated
    public Audio(String filePath, boolean infiniteLoop) {
        this.filePath = filePath;
        this.infiniteLoop = infiniteLoop;
    }

    public void startAudio() {
        final Thread playThread = new Thread(() -> {
            try {
                if (audioInputStream == null || clip == null) {
                    this.audioInputStream = AudioSystem.getAudioInputStream(new File(this.filePath));
                    this.clip = AudioSystem.getClip();
                }
                clip.open(audioInputStream);
                if (infiniteLoop) clip.loop(Clip.LOOP_CONTINUOUSLY);
                clip.start();
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        });
        playThread.start();
    }

    public void stopMusic() {
        if (clip != null) this.clip.stop();
    }
}