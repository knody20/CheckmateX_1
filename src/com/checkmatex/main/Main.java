package com.checkmatex.main;

import com.checkmatex.ui.GameFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        // Set cross-platform Java L&F (also called "Metal")
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // ignore
        }

        SwingUtilities.invokeLater(() -> {
            new GameFrame();
        });
    }
}
