package org.example.realengine.demo;

import javax.swing.*;

/**
 * <p>The {@code Main} class serves as the entry point for the RealEngine game application.</p>
 * <p>It initializes the game by invoking the {@code Execute.run} method within the Swing event dispatch thread.</p>
 */
public class Main {

    /**
     * <p>The main method that starts the RealEngine demo game.</p>
     * <p>It ensures that the game's initialization and execution occur on the Swing event dispatch thread
     * to maintain thread safety for UI operations.</p>
     * @param args Command line arguments (not used in this application).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Execute.run);
    }
}