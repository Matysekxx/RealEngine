package org.example.realengine.demo.mapmenu;

import javax.swing.*;
import java.awt.*;

public class TitleLabel extends JLabel {
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
