package org.example.realengine.demo.mapmenu;

import javax.swing.*;
import java.awt.*;

public class InstructionsLabel extends JLabel {
    public InstructionsLabel() {
        super("↑↓ - Select | ENTER - Load | ESC - Quit game | X - Close program");
        this.setFont(new Font("Verdana", Font.PLAIN, 18));
        this.setForeground(Color.YELLOW);
        this.setHorizontalAlignment(JLabel.CENTER);
        this.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
    }
}
