//BOARD PANEL
package com.checkmatex.ui;

import com.checkmatex.logic.*;
import com.checkmatex.pieces.*;
import com.checkmatex.utils.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;



public class BoardPanel extends JPanel {

    static final Color LIGHT_SQUARE = new Color(238, 216, 183);            //chessboard colors
    static final Color DARK_SQUARE = new Color(171, 122, 72);
    static final Color SELECTED_SQ = new Color(100, 180, 100);             //selected piece highlight - green
    static final Color MOVE_HINT = new Color(60, 60, 60, 70);           //possible move dots
    static final Color CAPTURE_HINT = new Color(180, 50, 50, 110);      //red capture circles
    static final Color CHECK_SQ = new Color(220, 50, 50, 170);          //king in check highlight
    static final Color LAST_MOVE_CLR = new Color(205, 185, 80, 130);    //last move highlight

    static final int SQ_SIZE = 70;

    private GameState gameState;
    private GameFrame gameFrame;

    private int selectedRow = -1;
    private int selectedCol = -1;
    private ArrayList<Move> legalMovesForSelected = new ArrayList<>();
    private Move lastMove = null;

    public BoardPanel(GameFrame gameFrame, GameState gameState) {
        this.gameFrame = gameFrame;
        this.gameState = gameState;
        setPreferredSize(new Dimension(SQ_SIZE * 8, SQ_SIZE * 8));
        setBackground(new Color(40, 40, 40));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (gameFrame.isGameOver()) return;
                int col = e.getX() / SQ_SIZE;
                int row = e.getY() / SQ_SIZE;
                if (col >= 0 && col < 8 && row >= 0 && row < 8) {
                    handleSquareClick(row, col);
                }
            }
        });
    }

    public void updateState(GameState newState, Move lastMove) {
        this.gameState = newState;
        this.lastMove = lastMove;
        this.selectedRow = -1;
        this.selectedCol = -1;
        this.legalMovesForSelected.clear();
        repaint();
    }

    private void handleSquareClick(int row, int col) {
        Piece piece = gameState.getPiece(row, col);

        if (selectedRow == -1) {            //First click
            if (piece != null && piece.getColor() == gameState.currentTurn) {
                selectedRow = row;
                selectedCol = col;
                legalMovesForSelected = MoveValidator.getLegalMoves(gameState, row, col);
            }
        } else {                            //all legal moves
            Move chosenMove = null;
            for (Move m : legalMovesForSelected) {
                if (m.toRow == row && m.toCol == col) {
                    chosenMove = m;
                    break;
                }
            }

            if (chosenMove != null) {
                // handle promotion - pawn reaches last row
                if (piece != null && piece.getType() == Constants.PAWN && (row == 0 || row == 7)) {
                    String[] options = {"Queen", "Rook", "Bishop", "Knight"};
                    int choice = JOptionPane.showOptionDialog(this,
                            "Choose promotion piece:", "Pawn Promotion",
                            JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                            null, options, options[0]);
                    if (choice < 0) choice = 0;
                    int[] promoTypes = {Constants.QUEEN, Constants.ROOK, Constants.BISHOP, Constants.KNIGHT};
                    int promoType = promoTypes[choice];
                    for (Move m2 : legalMovesForSelected) {
                        if (m2.toRow == row && m2.toCol == col && m2.flag == promoType) {
                            chosenMove = m2;
                            break;
                        }
                    }
                }
                gameFrame.executeMove(chosenMove);

            
            } else {
                selectedRow = -1;
                selectedCol = -1;
                legalMovesForSelected.clear();
            }
        }
        repaint();
    }
    //advanced graphics - called automatically whenever swing redraws the board
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        for (int row = 0; row < 8; row++) {                     //draws board
            for (int col = 0; col < 8; col++) {
                int x = col * SQ_SIZE;                          //grid position to pixels
                int y = row * SQ_SIZE;
                boolean isLight = (row + col) % 2 == 0;         //pattern logic sum is even - light square else dark square                   

                g2d.setColor(isLight ? LIGHT_SQUARE : DARK_SQUARE);
                g2d.fillRect(x, y, SQ_SIZE, SQ_SIZE);

                if (lastMove != null && ((row == lastMove.fromRow && col == lastMove.fromCol) || (row == lastMove.toRow && col == lastMove.toCol))) {
                    g2d.setColor(LAST_MOVE_CLR);
                    g2d.fillRect(x, y, SQ_SIZE, SQ_SIZE);
                }

                if (row == selectedRow && col == selectedCol) {
                    g2d.setColor(SELECTED_SQ);
                    g2d.fillRect(x, y, SQ_SIZE, SQ_SIZE);
                }

                if (CheckDetector.isKingInCheck(gameState, gameState.currentTurn)) {
                    Piece p = gameState.getPiece(row, col);
                    if (p != null && p.getType() == Constants.KING && p.getColor() == gameState.currentTurn) {
                        g2d.setColor(CHECK_SQ);
                        g2d.fillRect(x, y, SQ_SIZE, SQ_SIZE);
                    }
                }

                boolean isLegalTarget = false;
                for (Move m : legalMovesForSelected) {
                    if (m.toRow == row && m.toCol == col) {
                        isLegalTarget = true;
                        break;
                    }
                }
                if (isLegalTarget) {
                    if (gameState.getPiece(row, col) != null) {
                        g2d.setColor(CAPTURE_HINT);
                        g2d.setStroke(new BasicStroke(4));
                        g2d.drawOval(x + 4, y + 4, SQ_SIZE - 8, SQ_SIZE - 8);
                        g2d.setStroke(new BasicStroke(1));
                    } else {
                        g2d.setColor(MOVE_HINT);                        //hint color logic
                        int dotSize = 20;
                        g2d.fillOval(x + (SQ_SIZE - dotSize) / 2, y + (SQ_SIZE - dotSize) / 2, dotSize, dotSize);
                    }
                }

                g2d.setFont(new Font("Arial", Font.BOLD, 10));
                Color coordColor = isLight ? DARK_SQUARE.darker() : LIGHT_SQUARE.darker();
                g2d.setColor(coordColor);
                if (col == 0) {
                    g2d.drawString(String.valueOf(8 - row), x + 3, y + 14);
                }
                if (row == 7) {
                    String letter = String.valueOf("abcdefgh".charAt(col));
                    g2d.drawString(letter, x + SQ_SIZE - 13, y + SQ_SIZE - 4);
                }

                Piece piece = gameState.getPiece(row, col);
                if (piece != null) {
                    drawPiece(g2d, piece, x, y);
                }
            }
        }
    }

    private void drawPiece(Graphics2D g, Piece piece, int x, int y) {
        String symbol = piece.getUnicodeSymbol();
        g.setFont(new Font("Serif", Font.PLAIN, 46));
        FontMetrics fm = g.getFontMetrics();

        int tx = x + (SQ_SIZE - fm.stringWidth(symbol)) / 2;
        int ty = y + (SQ_SIZE + fm.getAscent() - fm.getDescent()) / 2 - 2;

        g.setColor(new Color(0, 0, 0, 90));
        g.drawString(symbol, tx + 1, ty + 1);

        if (piece.getColor() == Constants.WHITE) {
            g.setColor(new Color(40, 30, 20));
            g.drawString(symbol, tx - 1, ty);
            g.drawString(symbol, tx + 1, ty);
            g.drawString(symbol, tx, ty - 1);
            g.drawString(symbol, tx, ty + 1);
            g.setColor(Color.WHITE);
            g.drawString(symbol, tx, ty);
        } else {
            g.setColor(new Color(15, 15, 15));
            g.drawString(symbol, tx, ty);
        }
    }
}
