package com.checkmatex.ui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;               //hover events

public class SidePanel extends JPanel {

    // Change theme colors
static final Color BG_MAIN      = new Color(30, 32, 40);
static final Color BG_CARD      = new Color(48, 52, 66);
static final Color ACCENT_GOLD  = new Color(255, 196, 0);
static final Color BORDER_COLOR = new Color(75, 78, 95);

// Change button colors
static final Color BTN_NEW_GAME = new Color(52, 152, 219);
static final Color BTN_UNDO     = new Color(46, 204, 113);
static final Color BTN_REDO     = new Color(155, 89, 182);

    private GameFrame gameFrame;
    public TimerPanel timerPanel;
    public CapturedPanel capturedPanel;
    private JTextArea moveListArea;

    public SidePanel(GameFrame gameFrame) {
        this.gameFrame = gameFrame;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(BG_MAIN);
        setPreferredSize(new Dimension(250, 600));
        setBorder(new EmptyBorder(12, 10, 12, 10));

        add(buildSectionLabel("CONTROLS"));
        add(Box.createVerticalStrut(8));
        add(buildButtonPanel());
        add(Box.createVerticalStrut(16));

        timerPanel = new TimerPanel();
        add(timerPanel);
        add(Box.createVerticalStrut(16));

        capturedPanel = new CapturedPanel();
        add(capturedPanel);
        add(Box.createVerticalStrut(16));

        add(buildMoveHistoryCard());
    }

    private JLabel buildSectionLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 11));
        lbl.setForeground(new Color(0x888899));
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        return lbl;
    }

    private JPanel buildButtonPanel() {

        JButton newBtn  = makeButton("New Game", BTN_NEW_GAME);
        JButton undoBtn = makeButton("Undo", BTN_UNDO);
        JButton redoBtn = makeButton("Redo", BTN_REDO);
    //button actions
        newBtn.addActionListener(e -> gameFrame.confirmNewGame());
        undoBtn.addActionListener(e -> gameFrame.undoMove());
        redoBtn.addActionListener(e -> gameFrame.redoMove());

        JPanel topRow = new JPanel(new GridLayout(1, 1));
        topRow.setBackground(BG_MAIN);
        topRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        topRow.add(newBtn);

        JPanel grid = new JPanel(new GridLayout(1, 2, 8, 8));
        grid.setBackground(BG_MAIN);
        grid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 92));
        grid.add(undoBtn);
        grid.add(redoBtn);

        JPanel wrap = new JPanel();
        wrap.setLayout(new BoxLayout(wrap, BoxLayout.Y_AXIS));
        wrap.setBackground(BG_MAIN);
        wrap.add(topRow);
        wrap.add(Box.createVerticalStrut(8));
        wrap.add(grid);

        return wrap;
    }

    private JPanel buildMoveHistoryCard() {

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(BG_CARD);
        card.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(10, 10, 10, 10)
        ));
        card.setAlignmentX(LEFT_ALIGNMENT);

        JLabel title = new JLabel("MOVE HISTORY");
        title.setFont(new Font("SansSerif", Font.BOLD, 13));
        title.setForeground(ACCENT_GOLD);
        card.add(title);
        card.add(Box.createVerticalStrut(8));

        moveListArea = new JTextArea();
        moveListArea.setEditable(false);
        moveListArea.setBackground(new Color(0x1A1A28));
        moveListArea.setForeground(new Color(0xCCCCCC));
        moveListArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        moveListArea.setLineWrap(false);
        moveListArea.setBorder(new EmptyBorder(6, 6, 6, 6));

        JScrollPane scroll = new JScrollPane(moveListArea);
        scroll.setPreferredSize(new Dimension(228, 155));
        scroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 155));
        scroll.setBorder(new LineBorder(BORDER_COLOR, 1));

        card.add(scroll);
        return card;
    }

    public void updateMoveHistory(java.util.List<String> history) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < history.size(); i++) {
            if (i % 2 == 0) sb.append((i / 2 + 1)).append(". ");
            sb.append(history.get(i)).append(" ");
            if (i % 2 == 1) sb.append("\n");
        }

        moveListArea.setText(sb.toString());
        moveListArea.setCaretPosition(moveListArea.getDocument().getLength());   //auto scroll to bottom
    }

    private JButton makeButton(String text, Color baseColor) {

        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };

        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(baseColor);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 8, 10, 8));

        Color hoverColor = baseColor.brighter();

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(hoverColor);
                btn.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(baseColor);
                btn.repaint();
            }
        });

        return btn;
    }
}