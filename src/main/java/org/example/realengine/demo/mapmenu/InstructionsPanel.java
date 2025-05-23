package org.example.realengine.demo.mapmenu;

import javax.swing.*;
import java.awt.*;

/**
 * Panel zobrazující instrukce pro ovládání hry s gradientním pozadím.
 * Dědí z JPanel a poskytuje statickou metodu pro vykreslení pozadí.
 */
public class InstructionsPanel extends JPanel {
    public InstructionsPanel() {
        this.setLayout(new BorderLayout());
        this.add(new InstructionsLabel(), BorderLayout.CENTER);
    }

    static void paintBackground(Graphics g, Color color) {
        final Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(color);
        g2d.fillRect(0, 0, g2d.getClipBounds().width, g2d.getClipBounds().height);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        paintBackground(g, new Color(20, 20, 50));
    }
}
