package org.example.realengine.demo;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * The {@code Audio} class manages audio playback within the RealEngine demo application.
 * It provides functionality to load, play, and stop audio clips, including background music
 * for different game maps.
 */
public class Audio {
    /**
     * Represents the default audio track, typically used for title screens or general background.
     */
    public static final Audio DEFAULT_AUDIO = new Audio("resources/audio/02. Title.wav");
    /**
     * Represents the overworld audio track.
     */
    public static final Audio OVERWORLD = new Audio("resources/audio/12.20Overworld.wav");
    /**
     * Represents the cave audio track.
     */
    public static final Audio CAVE = new Audio("resources/audio/21.20Underground2028Yoshi29.wav");
    /**
     * Represents the lava cave audio track.
     */
    public static final Audio LAVA_CAVE = new Audio("resources/audio/48. World 8 (Part 2).wav");
    /**
     * A static map associating specific game map file paths with their corresponding {@code Audio} objects.
     * This allows for dynamic music changes based on the currently loaded map.
     */
    public final static Map<String, Audio> musicMap = new HashMap<>(Map.of(
            "resources\\maps\\map_1.png", OVERWORLD,
            "resources\\maps\\map_2.png", OVERWORLD,
            "resources\\maps\\map_3.png", CAVE,
            "resources\\maps\\map_4.png", CAVE,
            "resources\\maps\\map_5.png", OVERWORLD,
            "resources\\maps\\map_6.png", LAVA_CAVE,
            "resources\\maps\\map_7.png", LAVA_CAVE
    ));
    /**
     * The file path to the audio resource.
     */
    private final String filePath;
    /**
     * A boolean flag indicating whether the audio should loop infinitely.
     */
    private final boolean infiniteLoop = true;
    /**
     * The {@code Clip} object used for playing the audio.
     */
    private Clip clip;

    /**
     * Constructs a new {@code Audio} object with the specified file path.
     *
     * @param filePath The path to the audio file (e.g., "resources/audio/music.wav").
     */
    public Audio(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Starts playing the audio in a new thread to prevent blocking the main application thread.
     * If an audio clip is already playing, it will be stopped and closed before starting the new one.
     * The audio will loop continuously if {@code infiniteLoop} is set to true.
     * Any exceptions during audio loading or playback are caught and their messages printed to standard error.
     */
    public void startAudio() {
        final Thread playThread = new Thread(() -> {
            try {
                if (this.clip != null) {
                    this.clip.stop();
                    this.clip.close();
                }
                final AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(this.filePath));
                this.clip = AudioSystem.getClip();
                clip.open(audioInputStream);
                if (infiniteLoop) clip.loop(Clip.LOOP_CONTINUOUSLY);
                clip.start();
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        });
        playThread.start();
    }

    /**
     * Stops the currently playing audio clip and releases its resources.
     * If no clip is currently playing, this method does nothing.
     */
    public void stopMusic() {
        if (clip != null) {
            clip.stop();
            clip.close();
        }
    }
}