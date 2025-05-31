package org.example.realengine.demo.mapmenu;

import javax.swing.*;
import java.awt.*;

/**
 * The {@code TitleLabel} class extends {@link JLabel} to provide a custom-styled title label
 * specifically for the map menu in the RealEngine demo application.
 * It displays the text "MAPS" with a distinct font, color, and border.
 */
public class TitleLabel extends JLabel {
    /**
     * Constructs a new {@code TitleLabel}.
     * Initializes the label with the text "MAPS", sets its font to Verdana, bold, size 32,
     * sets the foreground color to yellow, centers the text horizontally, and applies
     * a compound border for visual separation and padding.
     */
    public TitleLabel() {
        super("MAPS");
        this.setFont(new Font("Verdana", Font.BOLD, 32));
        this.setForeground(Color.YELLOW);
        this.setHorizontalAlignment(JLabel.CENTER);
        this.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, Color.YELLOW),
                BorderFactory.createEmptyBorder(20, 0, 20, 0)));
    }
}
