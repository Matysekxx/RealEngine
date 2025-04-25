package org.example.realengine.demo;

import javax.swing.*;
import java.awt.*;

public class ListCellRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value,
                                                  int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(
                list, value, index, isSelected, cellHasFocus);
        if (isSelected) {
            label.setText("▶ " + value);
            label.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(1, 0, 1, 0, new Color(255, 215, 0, 100)),
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        } else {
            label.setText("  " + value);
            label.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        }
        return label;
    }
}
