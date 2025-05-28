package org.example.realengine.demo.mapmenu;

import javax.swing.*;
import java.awt.*;

/**
 * A custom Swing JLabel that displays game instructions.
 * This label is styled with a specific font, color, alignment, and border.
 */
public class InstructionsLabel extends JLabel {
    /**
     * Constructs an `InstructionsLabel` with predefined text and styling.
     * The text provides instructions for map selection and game control.
     */
    public InstructionsLabel() {
        super("↑↓ - Select | ENTER - Load | ESC - Quit game & SAVE");
        this.setFont(new Font("Verdana", Font.PLAIN, 18));
        this.setForeground(Color.YELLOW);
        this.setHorizontalAlignment(JLabel.CENTER);
        this.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
    }
}
