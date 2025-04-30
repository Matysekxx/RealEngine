package org.example.realengine.demo;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Audio {
    private Clip clip;
    private final String filePath;
    private boolean infiniteLoop = false;
    private Thread playThread;

    public static Map<String, String> musicMap = new HashMap<>(Map.of(
            "C:\\Users\\chalo\\IdeaProjects\\RealEngine\\maps\\map_1.png", "resources/audio/12.20Overworld.wav"));

    public Audio(String filePath) {
        this.filePath = filePath;
    }

    public void playMusic() {
        this.playThread = new Thread(() -> {
            try {
                final AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File(this.filePath));
                this.clip = AudioSystem.getClip();
                clip.open(audioStream);
                if (infiniteLoop) clip.loop(Clip.LOOP_CONTINUOUSLY);
                clip.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        this.playThread.start();
    }

    public void stopMusic() {
        this.clip.stop();
    }
}
