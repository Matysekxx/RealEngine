package org.example.realengine.demo.mapmenu;

import javax.swing.*;
import java.awt.*;

/**
 * Panel pro zobrazení titulku v menu s gradientním pozadím.
 * Dědí z JPanel a využívá metodu pro vykreslení pozadí z InstructionsPanel.
 */
public class TitlePanel extends JPanel {
    public TitlePanel() {
        this.setLayout(new BorderLayout());
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        InstructionsPanel.paintBackground(g, new Color(20, 20, 50));
    }
}
