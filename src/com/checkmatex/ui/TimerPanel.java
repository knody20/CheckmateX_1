package com.checkmatex.ui;

import com.checkmatex.logic.TimerManager;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import static com.checkmatex.ui.SidePanel.*;                //directly importing constants from SidePanel

public class TimerPanel extends JPanel {

    private JLabel statusLabel;                 //status like whose turn and check
    private JLabel whiteTimeLabel;
    private JLabel blackTimeLabel;
   
    public TimerPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(BG_CARD);
        setBorder(new CompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(12, 12, 12, 12)
        ));
        setAlignmentX(LEFT_ALIGNMENT);

        // Section heading
        JLabel heading = new JLabel("GAME STATUS");
        heading.setFont(new Font("SansSerif", Font.BOLD, 13));
        heading.setForeground(ACCENT_GOLD);
        heading.setAlignmentX(LEFT_ALIGNMENT);

        // Status / turn label
        statusLabel = new JLabel("White's turn to move");
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        statusLabel.setAlignmentX(LEFT_ALIGNMENT);

        add(heading);
        add(Box.createVerticalStrut(10));
        add(statusLabel);
        add(Box.createVerticalStrut(14));

        // Two timer rows: Black on top, White on bottom
        add(buildTimerRow(false)); // Black
        add(Box.createVerticalStrut(8));
        add(buildTimerRow(true));  // White
        add(Box.createVerticalStrut(4));
    }

    private JPanel buildTimerRow(boolean isWhite) {
        Color rowBg = isWhite ? new Color(0x3A3A2A) : new Color(0x22223A);
        Color tagFg = isWhite ? new Color(0xF0F0F0) : new Color(0xAAAAAA);
        Color tagBg = isWhite ? new Color(0x6A6A4A) : new Color(0x3A3A5A);

        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setBackground(rowBg);
        row.setBorder(new CompoundBorder(
            new LineBorder(isWhite ? new Color(0x7A7A5A) : new Color(0x4A4A6A), 1, true),
            new EmptyBorder(8, 12, 8, 12)
        ));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        row.setAlignmentX(LEFT_ALIGNMENT);

        // Player tag pill
        JLabel tag = new JLabel(isWhite ? "WHITE" : "BLACK");
        tag.setFont(new Font("SansSerif", Font.BOLD, 11));
        tag.setForeground(tagFg);
        tag.setBackground(tagBg);
        tag.setOpaque(true);
        tag.setBorder(new EmptyBorder(2, 8, 2, 8));

        // Time display
        JLabel timeLabel = new JLabel("10:00");
        timeLabel.setFont(new Font("Monospaced", Font.BOLD, 22));
        timeLabel.setForeground(isWhite ? new Color(0xF8F8E8) : new Color(0xAAAAAA));

        row.add(tag, BorderLayout.WEST);
        row.add(timeLabel, BorderLayout.EAST);

        if (isWhite) whiteTimeLabel = timeLabel;
        else         blackTimeLabel = timeLabel;

        return row;
    }

    public void updateTimers(int whiteSeconds, int blackSeconds) {
        whiteTimeLabel.setText(TimerManager.formatTime(whiteSeconds));
        blackTimeLabel.setText(TimerManager.formatTime(blackSeconds));

        // Warn when < 60s i.e red color
        whiteTimeLabel.setForeground(whiteSeconds < 60 ? new Color(0xFF6B6B) : new Color(0xF8F8E8));
        blackTimeLabel.setForeground(blackSeconds < 60 ? new Color(0xFF6B6B) : new Color(0xAAAAAA));
    }

    public void updateStatus(String status, Color color) {
        statusLabel.setText(status);
        statusLabel.setForeground(color);
    }
}
