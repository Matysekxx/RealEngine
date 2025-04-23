package org.example.realengine;

import org.example.realengine.map.RMap;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MapMenuPanel extends JPanel {
    private final JFrame parentFrame;
    private final GamePanel gamePanel;
    private final JList<String> mapList;
    private final DefaultListModel<String> listModel;
    private final List<String> mapPaths = new ArrayList<>();

    public MapMenuPanel(JFrame parentFrame, GamePanel gamePanel) {
        this.parentFrame = parentFrame;
        this.gamePanel = gamePanel;

        setLayout(new BorderLayout());
        setBackground(new Color(20, 20, 50));
        setPreferredSize(parentFrame.getSize());
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        JLabel titleLabel = new JLabel("MAPS");
        titleLabel.setFont(new Font("Verdana", Font.BOLD, 32));
        titleLabel.setForeground(Color.YELLOW);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titleLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, Color.YELLOW),
                BorderFactory.createEmptyBorder(20, 0, 20, 0)));
        JPanel titlePanel = getPanel();
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        listModel = new DefaultListModel<>();
        mapList = new JList<>(listModel);
        mapList.setFont(new Font("Verdana", Font.PLAIN, 22));
        mapList.setBackground(new Color(30, 30, 60));
        mapList.setForeground(Color.YELLOW);
        mapList.setSelectionBackground(new Color(60, 60, 120));
        mapList.setSelectionForeground(Color.YELLOW);
        mapList.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mapList.setCellRenderer(new DefaultListCellRenderer() {
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
        });
        mapList.setSelectionModel(new DefaultListSelectionModel() {
            @Override
            public void setSelectionInterval(int index0, int index1) {
                if (!getValueIsAdjusting()) {
                    super.setSelectionInterval(index0, index1);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(mapList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        scrollPane.setBackground(new Color(20, 20, 50));
        scrollPane.getViewport().setBackground(new Color(30, 30, 60));
        JPanel instructionsPanel = getJPanel();

        add(titlePanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(instructionsPanel, BorderLayout.SOUTH);
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                if (keyCode == KeyEvent.VK_ESCAPE || keyCode == KeyEvent.VK_L) {
                    returnToGame();
                } else if (keyCode == KeyEvent.VK_ENTER) {
                    int selectedIndex = mapList.getSelectedIndex();
                    if (selectedIndex >= 0 && selectedIndex < mapPaths.size()) {
                        loadSelectedMap(mapPaths.get(selectedIndex));
                    }
                } else if (keyCode == KeyEvent.VK_UP) {
                    int selectedIndex = mapList.getSelectedIndex();
                    if (selectedIndex > 0) {
                        mapList.setSelectedIndex(selectedIndex - 1);
                        mapList.ensureIndexIsVisible(selectedIndex - 1);
                    }
                } else if (keyCode == KeyEvent.VK_DOWN) {
                    int selectedIndex = mapList.getSelectedIndex();
                    if (selectedIndex < mapList.getModel().getSize() - 1) {
                        mapList.setSelectedIndex(selectedIndex + 1);
                        mapList.ensureIndexIsVisible(selectedIndex + 1);
                    }
                }
            }
        });
        mapList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int selectedIndex = mapList.getSelectedIndex();
                    if (selectedIndex >= 0 && selectedIndex < mapPaths.size()) {
                        loadSelectedMap(mapPaths.get(selectedIndex));
                    }
                }
            }
        });

        loadMapList();
    }

    @NotNull
    private JPanel getPanel() {
        JPanel titlePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth();
                int h = getHeight();
                Color color1 = new Color(40, 40, 80);
                Color color2 = new Color(20, 20, 50);
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        titlePanel.setLayout(new BorderLayout());
        return titlePanel;
    }

    @NotNull
    private JPanel getJPanel() {
        JPanel instructionsPanel = getInstructionsPanel();
        JLabel instructionsLabel = new JLabel("↑↓ - Select | ENTER - Load | ESC - Back");
        instructionsLabel.setFont(new Font("Verdana", Font.BOLD, 20));
        instructionsLabel.setForeground(Color.YELLOW);
        instructionsPanel.add(instructionsLabel);
        return instructionsPanel;
    }

    @NotNull
    private JPanel getInstructionsPanel() {
        JPanel instructionsPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth();
                int h = getHeight();
                Color color1 = new Color(20, 20, 50);
                Color color2 = new Color(40, 40, 80);
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        instructionsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        return instructionsPanel;
    }

    public void loadMapList() {
        listModel.clear();
        mapPaths.clear();
        File customMapsDir = new File("f:\\RealEngine\\maps");
        if (!customMapsDir.exists()) {
            customMapsDir.mkdirs();
        }
        loadMapsFromDirectory(customMapsDir);
        if (listModel.isEmpty()) {
            listModel.addElement("No maps found");
        }
    }

    /**
     * Načte mapy z daného adresáře do seznamu.
     *
     * @param directory Adresář, ze kterého se mají načíst mapy
     */
    private void loadMapsFromDirectory(File directory) {
        if (directory.exists() && directory.isDirectory()) {
            File[] mapFiles = directory.listFiles((_, name) -> name.toLowerCase().endsWith(".png"));
            if (mapFiles != null) {
                for (File mapFile : mapFiles) {
                    String mapName = mapFile.getName();
                    listModel.addElement(mapName);
                    mapPaths.add(mapFile.getAbsolutePath());
                }
            }
        }
    }

    private void loadSelectedMap(String mapPath) {
        try {
            RMap newMap = RMap.loadFromFiles(mapPath);
            gamePanel.loadMap(newMap);
            returnToGame();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Failed to load map: " + e.getMessage(),
                    "Map Loading Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void returnToGame() {
        parentFrame.getContentPane().remove(this);
        parentFrame.getContentPane().add(gamePanel);
        gamePanel.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(
                new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB),
                new Point(0, 0),
                "invisible"));

        gamePanel.requestFocus();
        gamePanel.resumeGame();
        parentFrame.revalidate();
        parentFrame.repaint();
    }
}