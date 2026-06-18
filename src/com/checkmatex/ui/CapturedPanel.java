package com.checkmatex.ui;

import com.checkmatex.pieces.*;
import com.checkmatex.utils.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.ArrayList;

import static com.checkmatex.ui.SidePanel.*;

public class CapturedPanel extends JPanel {

    private final ArrayList<Piece> capturedByWhite = new ArrayList<>();
    private final ArrayList<Piece> capturedByBlack = new ArrayList<>();

    private JLabel whiteSymbolsLabel;
    private JLabel whitePointsLabel;
    private JLabel blackSymbolsLabel;
    private JLabel blackPointsLabel;
    private JLabel advantageLabel;

    public CapturedPanel() {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(BG_CARD);

        setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(15, 15, 15, 15)
        ));

        setAlignmentX(LEFT_ALIGNMENT);

        JLabel heading = new JLabel("♟ CAPTURED PIECES");
        heading.setFont(new Font("SansSerif", Font.BOLD, 15));
        heading.setForeground(ACCENT_GOLD);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);

        add(heading);
        add(Box.createVerticalStrut(12));

        add(buildPlayerRow(true));

        add(Box.createVerticalStrut(10));

        add(buildPlayerRow(false));

        add(Box.createVerticalStrut(12));

        JSeparator separator = new JSeparator();
        separator.setForeground(BORDER_COLOR);
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        add(separator);

        add(Box.createVerticalStrut(12));

        advantageLabel = new JLabel("Material Advantage : Even");
        advantageLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        advantageLabel.setForeground(new Color(80, 200, 120));
        advantageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        add(advantageLabel);
    }

    private JPanel buildPlayerRow(boolean whiteSide) {

        Color background = whiteSide
                ? new Color(54, 54, 42)
                : new Color(42, 42, 54);

        JPanel row = new JPanel(new BorderLayout(10, 0));

        row.setBackground(background);

        row.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));

        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));

        JLabel title = new JLabel(
                whiteSide ? "WHITE CAPTURED" : "BLACK CAPTURED"
        );

        title.setFont(new Font("SansSerif", Font.BOLD, 11));
        title.setForeground(Color.WHITE);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(background);

        JLabel symbols = new JLabel("");
        symbols.setFont(new Font("Serif", Font.PLAIN, 20));
        symbols.setForeground(Color.WHITE);

        JLabel points = new JLabel("(+0)");
        points.setFont(new Font("SansSerif", Font.BOLD, 11));
        points.setForeground(new Color(180, 180, 180));

        infoPanel.add(symbols);
        infoPanel.add(points);

        row.add(title, BorderLayout.WEST);
        row.add(infoPanel, BorderLayout.EAST);

        if (whiteSide) {
            whiteSymbolsLabel = symbols;
            whitePointsLabel = points;
        } else {
            blackSymbolsLabel = symbols;
            blackPointsLabel = points;
        }

        return row;
    }

    public void addCapturedPiece(Piece piece) {

        if (piece.getColor() == Constants.BLACK) {
            capturedByWhite.add(piece);
        } else {
            capturedByBlack.add(piece);
        }

        updateDisplay();
    }

    public void clear() {

        capturedByWhite.clear();
        capturedByBlack.clear();

        updateDisplay();
    }

    private void updateDisplay() {

        int whiteScore = 0;
        StringBuilder whitePieces = new StringBuilder();

        for (Piece piece : capturedByWhite) {
            whitePieces.append(piece.getUnicodeSymbol()).append(" ");
            whiteScore += piece.getPointValue();
        }

        whiteSymbolsLabel.setText(whitePieces.toString());
        whitePointsLabel.setText("(+" + whiteScore + ")");

        int blackScore = 0;
        StringBuilder blackPieces = new StringBuilder();

        for (Piece piece : capturedByBlack) {
            blackPieces.append(piece.getUnicodeSymbol()).append(" ");
            blackScore += piece.getPointValue();
        }

        blackSymbolsLabel.setText(blackPieces.toString());
        blackPointsLabel.setText("(+" + blackScore + ")");

        int difference = whiteScore - blackScore;

        if (difference > 0) {

            advantageLabel.setText("Material Advantage : White +" + difference);
            advantageLabel.setForeground(new Color(144, 238, 144));

        } else if (difference < 0) {

            advantageLabel.setText("Material Advantage : Black +" + Math.abs(difference));
            advantageLabel.setForeground(new Color(255, 182, 193));

        } else {

            advantageLabel.setText("Material Advantage : Even");
            advantageLabel.setForeground(new Color(135, 206, 250));
        }
    }
}