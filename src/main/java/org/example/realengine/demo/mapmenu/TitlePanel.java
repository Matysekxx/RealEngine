package org.example.realengine.demo.mapmenu;

import javax.swing.*;
import java.awt.*;

/**
 * The {@code TitlePanel} class is a {@link JPanel} designed to display the title within the map menu.
 * It incorporates a gradient background and adds a {@link TitleLabel} to present the title text.
 * This panel utilizes a shared background painting method from {@link InstructionsPanel}.
 */
public class TitlePanel extends JPanel {
    /**
     * Constructs a new {@code TitlePanel}.
     * Sets the layout to {@link BorderLayout} and adds a new {@link TitleLabel} to the center
     * of this panel.
     */
    public TitlePanel() {
        this.setLayout(new BorderLayout());
        this.add(new TitleLabel(), BorderLayout.CENTER);
    }

    /**
     * Overrides the {@code paintComponent} method to draw a custom background for the panel.
     * It delegates the actual background painting to the {@link InstructionsPanel#paintBackground(Graphics, Color)}
     * method, ensuring a consistent gradient appearance.
     *
     * @param g The {@link Graphics} context used for painting.
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        InstructionsPanel.paintBackground(g, new Color(20, 20, 50));
    }
}
