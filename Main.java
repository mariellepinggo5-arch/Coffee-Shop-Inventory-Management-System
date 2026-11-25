package com.inventory;

import com.inventory.util.UITheme;
import com.inventory.view.NewJFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {

    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            NewJFrame frame = new NewJFrame();
            frame.setVisible(true);
        });
    }
}
