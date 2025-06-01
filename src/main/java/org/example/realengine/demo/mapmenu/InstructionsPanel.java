package org.example.realengine.demo.mapmenu;

import javax.swing.*;
import java.awt.*;

/**
 * Panel displaying game instructions with a gradient background.
 * Extends JPanel and provides a static method for drawing the background.
 */
public class InstructionsPanel extends JPanel {
    /**
     * Constructs an `InstructionsPanel`.
     * Sets the layout to `BorderLayout` and adds an `InstructionsLabel` to the center.
     */
    public InstructionsPanel() {
        this.setLayout(new BorderLayout());
        this.add(new InstructionsLabel(), BorderLayout.CENTER);
    }

    /**
     * Statically paints a solid color background onto the given Graphics context.
     * This method is used by various panels to draw their backgrounds consistently.
     *
     * @param g     The Graphics context to paint on.
     * @param color The `Color` to fill the background with.
     */
    static void paintBackground(Graphics g, Color color) {
        final Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(color);
        g2d.fillRect(0, 0, g2d.getClipBounds().width, g2d.getClipBounds().height);
    }

    /**
     * Overrides the `paintComponent` method to draw the custom gradient background.
     * Calls the superclass method first, then uses `paintBackground` to draw a dark blue background.
     *
     * @param g The `Graphics` context used for painting.
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        InstructionsPanel.paintBackground(g, new Color(20, 20, 50));
    }
}
