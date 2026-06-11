package com.checkmatex.ui;

import com.checkmatex.pieces.*;
import com.checkmatex.utils.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.ArrayList;

import static com.checkmatex.ui.SidePanel.*;                //to resuse sidepanel colors 

public class CapturedPanel extends JPanel {

    private ArrayList<Piece> capturedByWhite = new ArrayList<>(); // white captured these black pieces
    private ArrayList<Piece> capturedByBlack = new ArrayList<>(); // black captured these white pieces

    //labels - captured pieces and points gained
    private JLabel whiteSymbolsLabel;
    private JLabel whitePointsLabel;
    private JLabel blackSymbolsLabel;
    private JLabel blackPointsLabel;
    private JLabel advantageLabel;              //material label

    public CapturedPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));           //stacked vertically 
        setBackground(BG_CARD);
        setBorder(new CompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(12, 12, 12, 12)
        ));
        setAlignmentX(LEFT_ALIGNMENT);

        // Heading
        JLabel heading = new JLabel("CAPTURED PIECES");
        heading.setFont(new Font("SansSerif", Font.BOLD, 13));
        heading.setForeground(ACCENT_GOLD);
        heading.setAlignmentX(LEFT_ALIGNMENT);
        add(heading);
        add(Box.createVerticalStrut(10));

        // White row
        add(buildPlayerRow(true));
        add(Box.createVerticalStrut(8));

        // Black row
        add(buildPlayerRow(false));
        add(Box.createVerticalStrut(12));

        // Divider
        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER_COLOR);
        sep.setBackground(BORDER_COLOR);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        add(sep);
        add(Box.createVerticalStrut(10));

        // Material advantage
        advantageLabel = new JLabel("Material: Even");
        advantageLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        advantageLabel.setForeground(new Color(0x50C878)); // emerald green
        advantageLabel.setAlignmentX(LEFT_ALIGNMENT);
        add(advantageLabel);
    }

    private JPanel buildPlayerRow(boolean isWhite) {
        Color rowBg = isWhite ? new Color(0x343424) : new Color(0x242434);
        Color tagFg = isWhite ? new Color(0xF0F0F0) : new Color(0xAAAAAA);
        Color tagBg = isWhite ? new Color(0x5A5A3A) : new Color(0x3A3A5A);

        JPanel row = new JPanel(new BorderLayout(6, 0));
        row.setBackground(rowBg);
        row.setBorder(new CompoundBorder(
            new LineBorder(isWhite ? new Color(0x6A6A4A) : new Color(0x4A4A6A), 1, true),
            new EmptyBorder(8, 10, 8, 10)
        ));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        row.setAlignmentX(LEFT_ALIGNMENT);

        // Tag
        JLabel tag = new JLabel(isWhite ? "WHITE WON" : "BLACK WON");
        tag.setFont(new Font("SansSerif", Font.BOLD, 10));
        tag.setForeground(tagFg);
        tag.setBackground(tagBg);
        tag.setOpaque(true);
        tag.setBorder(new EmptyBorder(2, 6, 2, 6));

        // Symbols + points in a vertical sub-panel
        JPanel rightSide = new JPanel();
        rightSide.setLayout(new BoxLayout(rightSide, BoxLayout.Y_AXIS));
        rightSide.setBackground(rowBg);

        JLabel symbolsLabel = new JLabel("None");
        symbolsLabel.setFont(new Font("Serif", Font.PLAIN, 17));
        symbolsLabel.setForeground(new Color(0xCCCCCC));

        JLabel pointsLabel = new JLabel("(+0)");
        pointsLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
        pointsLabel.setForeground(new Color(0x888899));

        rightSide.add(symbolsLabel);
        rightSide.add(pointsLabel);

        row.add(tag,       BorderLayout.WEST);
        row.add(rightSide, BorderLayout.EAST);

        if (isWhite) { whiteSymbolsLabel = symbolsLabel; whitePointsLabel = pointsLabel; }
        else         { blackSymbolsLabel = symbolsLabel; blackPointsLabel = pointsLabel; }

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
        // White captures
        int wPts = 0;
        StringBuilder wsb = new StringBuilder();
        for (Piece p : capturedByWhite) { wsb.append(p.getUnicodeSymbol()); wPts += p.getPointValue(); }
        whiteSymbolsLabel.setText(wsb.length() == 0 ? "" : wsb.toString());
        whitePointsLabel.setText(wPts > 0 ? "(+" + wPts + ")" : "(+0)");

        // Black captures
        int bPts = 0;
        StringBuilder bsb = new StringBuilder();
        for (Piece p : capturedByBlack) { bsb.append(p.getUnicodeSymbol()); bPts += p.getPointValue(); }
        blackSymbolsLabel.setText(bsb.length() == 0 ? "" : bsb.toString());
        blackPointsLabel.setText(bPts > 0 ? "(+" + bPts + ")" : "(+0)");

        // Advantage
        int diff = wPts - bPts;
        if (diff > 0) {
            advantageLabel.setText("Material: White +" + diff);
            advantageLabel.setForeground(new Color(0xF0F0F0));
        } else if (diff < 0) {
            advantageLabel.setText("Material: Black +" + Math.abs(diff));
            advantageLabel.setForeground(new Color(0xAAAAAA));
        } else {
            advantageLabel.setText("Material: Even");
            advantageLabel.setForeground(new Color(0x50C878));
        }
    }
}
