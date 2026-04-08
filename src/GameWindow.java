// GameWindow.java
// This is the main window of CheckMate X
// It handles the UI - drawing the board, panels, buttons etc.
// We used Java Swing because we learned it in our Java course

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class GameWindow extends JFrame {

    // colors for the board
    static final Color LIGHT_SQUARE  = new Color(238, 216, 183);
    static final Color DARK_SQUARE   = new Color(171, 122,  72);
    static final Color SELECTED_SQ   = new Color(100, 180, 100);
    static final Color MOVE_HINT     = new Color(60, 60, 60, 70);
    static final Color CAPTURE_HINT  = new Color(180, 50, 50, 110);
    static final Color CHECK_SQ      = new Color(220, 50, 50, 170);
    static final Color LAST_MOVE_CLR = new Color(205, 185, 80, 130);
    static final Color BG_COLOR      = new Color(40, 40, 40);
    static final Color PANEL_COLOR   = new Color(50, 50, 60);
    static final Color TEXT_COLOR    = new Color(220, 220, 200);
    static final Color GOLD          = new Color(200, 160, 60);

    // board square size in pixels
    static final int SQ_SIZE = 70;

    ChessBoard chessBoard;
    //ChessAI    ai;
    boolean    vsAI   = true;
    boolean    gameOver = false;
    boolean    aiThinking = false;

    // which square is selected right now
    int selectedRow = -1, selectedCol = -1;
    ArrayList<int[]> legalMovesForSelected = new ArrayList<>();

    // move history - list of strings like "e2-e4"
    ArrayList<String> moveHistory = new ArrayList<>();

    // captured pieces
    ArrayList<Integer> capturedByWhite = new ArrayList<>(); // pieces white captured
    ArrayList<Integer> capturedByBlack = new ArrayList<>(); // pieces black captured

    // UI components we need to update
    JLabel statusLabel;
    JTextArea moveListArea;
    JLabel capturedWhiteLabel;
    JLabel capturedBlackLabel;
    JPanel boardPanel;

    // piece unicode symbols
    // index 0 unused, 1=pawn, 2=knight, 3=bishop, 4=rook, 5=queen, 6=king
    String[] whiteSymbols = {"", "\u2659", "\u2658", "\u2657", "\u2656", "\u2655", "\u2654"};
    String[] blackSymbols = {"", "\u265F", "\u265E", "\u265D", "\u265C", "\u265B", "\u265A"};

    public GameWindow() {
        super("CheckMateX");
        chessBoard = new ChessBoard();
        //ai = new ChessAI(2); // depth 2 by default

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(5, 5));
        getContentPane().setBackground(BG_COLOR);

        buildUI();

        pack();
        setLocationRelativeTo(null); // center on screen
        setResizable(true);
        setVisible(true);
    }

    void buildUI() {
        // top label
        JLabel title = new JLabel("  CheckMate X", SwingConstants.LEFT);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(GOLD);
        title.setBackground(new Color(30, 30, 30));
        title.setOpaque(true);
        title.setBorder(new EmptyBorder(8, 10, 8, 10));

        // JLabel subtitle = new JLabel("DAA Project  |  Backtracking + Minimax + Alpha-Beta Pruning  ", SwingConstants.RIGHT);
        // subtitle.setFont(new Font("Arial", Font.PLAIN, 11));
        // subtitle.setForeground(new Color(130, 130, 130));
        // subtitle.setBackground(new Color(30, 30, 30));
        // subtitle.setOpaque(true);
        // subtitle.setBorder(new EmptyBorder(8, 10, 8, 10));

        // JPanel headerPanel = new JPanel(new BorderLayout());
        // headerPanel.setBackground(new Color(30, 30, 30));
        // headerPanel.add(title, BorderLayout.WEST);
        // headerPanel.add(subtitle, BorderLayout.EAST);
        // add(headerPanel, BorderLayout.NORTH);

        // board panel (center)
        boardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBoard(g);
            }
        };
        boardPanel.setPreferredSize(new Dimension(SQ_SIZE * 8, SQ_SIZE * 8));
        boardPanel.setBackground(BG_COLOR);
        boardPanel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (gameOver || aiThinking) return;
                if (vsAI && chessBoard.currentTurn == 2) return; // AI's turn
                int col = e.getX() / SQ_SIZE;
                int row = e.getY() / SQ_SIZE;
                if (col >= 0 && col < 8 && row >= 0 && row < 8)
                    handleSquareClick(row, col);
            }
        });

        JPanel boardWrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        boardWrap.setBackground(BG_COLOR);
        boardWrap.setBorder(new EmptyBorder(10, 15, 10, 15));
        boardWrap.add(boardPanel);
        add(boardWrap, BorderLayout.CENTER);

        // right side panel
        JPanel rightPanel = buildRightPanel();
        add(rightPanel, BorderLayout.EAST);

        // bottom status bar
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        statusBar.setBackground(new Color(30, 30, 30));
        statusLabel = new JLabel("White's turn to move");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 12));
        statusLabel.setForeground(TEXT_COLOR);
        statusBar.add(statusLabel);
        add(statusBar, BorderLayout.SOUTH);
    }

    JPanel buildRightPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG_COLOR);
        panel.setPreferredSize(new Dimension(200, 560));
        panel.setBorder(new EmptyBorder(10, 5, 10, 10));

        // buttons
        JButton newGameBtn = makeButton("New Game");
        newGameBtn.addActionListener(e -> showNewGameDialog());
        // JButton undoBtn = makeButton("Undo");
        // undoBtn.addActionListener(e -> {
        //     // simple undo - just restart (we didn't implement full undo stack to keep it simple)
        //     JOptionPane.showMessageDialog(this, "Undo not implemented yet.\nStart a new game instead.", "Undo", JOptionPane.INFORMATION_MESSAGE);
        // });

        panel.add(newGameBtn);
        panel.add(Box.createVerticalStrut(10));
        //panel.add(undoBtn);
        panel.add(Box.createVerticalStrut(15));

        // captured pieces
        JLabel capTitle = makeLabel("Captured Pieces", true);
        capturedWhiteLabel = makeLabel("White captured: -", false);
        capturedBlackLabel = makeLabel("Black captured: -", false);
        capturedWhiteLabel.setForeground(new Color(180, 180, 180));
        capturedBlackLabel.setForeground(new Color(180, 180, 180));

        panel.add(capTitle);
        panel.add(Box.createVerticalStrut(4));
        panel.add(capturedWhiteLabel);
        panel.add(Box.createVerticalStrut(3));
        panel.add(capturedBlackLabel);
        panel.add(Box.createVerticalStrut(15));

        // move history
        JLabel moveTitle = makeLabel("Move History", true);
        panel.add(moveTitle);
        panel.add(Box.createVerticalStrut(4));

        moveListArea = new JTextArea(15, 14);
        moveListArea.setEditable(false);
        moveListArea.setBackground(new Color(35, 35, 45));
        moveListArea.setForeground(new Color(200, 200, 200));
        moveListArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        moveListArea.setLineWrap(false);
        moveListArea.setBorder(new EmptyBorder(4, 4, 4, 4));

        JScrollPane scrollPane = new JScrollPane(moveListArea);
        scrollPane.setPreferredSize(new Dimension(185, 220));
        scrollPane.setMaximumSize(new Dimension(185, 220));
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80)));
        panel.add(scrollPane);

        panel.add(Box.createVerticalStrut(15));

        // info about algorithms
        // JLabel algoTitle = makeLabel("Algorithms Used", true);
        // panel.add(algoTitle);
        // panel.add(Box.createVerticalStrut(4));

        // String[] algoLines = {
        //     "Backtracking:",
        //     "  Legal move filter",
        //     "Minimax:",
        //     "  AI game tree",
        //     "Alpha-Beta:",
        //     "  Prune tree branches",
        // };
        // for (String line : algoLines) {
        //     JLabel l = new JLabel(line);
        //     l.setForeground(line.startsWith(" ") ? new Color(150, 150, 150) : GOLD);
        //     l.setFont(new Font("Arial", Font.PLAIN, 10));
        //     panel.add(l);
        // }

        return panel;
    }

    // ---- DRAWING THE BOARD ----
    void drawBoard(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                int x = col * SQ_SIZE;
                int y = row * SQ_SIZE;
                boolean isLight = (row + col) % 2 == 0;

                // base square color
                g2d.setColor(isLight ? LIGHT_SQUARE : DARK_SQUARE);
                g2d.fillRect(x, y, SQ_SIZE, SQ_SIZE);

                // last move highlight
                if ((row == chessBoard.lastFromRow && col == chessBoard.lastFromCol) ||
                    (row == chessBoard.lastToRow   && col == chessBoard.lastToCol)) {
                    g2d.setColor(LAST_MOVE_CLR);
                    g2d.fillRect(x, y, SQ_SIZE, SQ_SIZE);
                }

                // selected square highlight
                if (row == selectedRow && col == selectedCol) {
                    g2d.setColor(SELECTED_SQ);
                    g2d.fillRect(x, y, SQ_SIZE, SQ_SIZE);
                }

                // king in check - red highlight
                if (chessBoard.isKingInCheck(chessBoard.currentTurn)) {
                    // find king and highlight it red
                    if (chessBoard.board[row][col] == chessBoard.currentTurn * 10 + 6) {
                        g2d.setColor(CHECK_SQ);
                        g2d.fillRect(x, y, SQ_SIZE, SQ_SIZE);
                    }
                }

                // legal move dots
                boolean isLegalTarget = false;
                for (int[] m : legalMovesForSelected) {
                    if (m[0] == row && m[1] == col) { isLegalTarget = true; break; }
                }
                if (isLegalTarget) {
                    if (chessBoard.board[row][col] != 0) {
                        // capture - draw a ring
                        g2d.setColor(CAPTURE_HINT);
                        g2d.setStroke(new BasicStroke(4));
                        g2d.drawOval(x + 4, y + 4, SQ_SIZE - 8, SQ_SIZE - 8);
                        g2d.setStroke(new BasicStroke(1));
                    } else {
                        // empty square - small dot in center
                        g2d.setColor(MOVE_HINT);
                        int dotSize = 20;
                        g2d.fillOval(x + (SQ_SIZE - dotSize) / 2, y + (SQ_SIZE - dotSize) / 2, dotSize, dotSize);
                    }
                }

                // coordinate labels (file letters bottom row, rank numbers left col)
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

                // draw piece
                int piece = chessBoard.board[row][col];
                if (piece != 0) {
                    drawPiece(g2d, piece, x, y);
                }
            }
        }
    }

    void drawPiece(Graphics2D g, int piece, int x, int y) {
        int color = chessBoard.getColor(piece);
        int type  = chessBoard.getType(piece);

        String symbol = (color == 1) ? whiteSymbols[type] : blackSymbols[type];

        // set a big font for pieces
        g.setFont(new Font("Serif", Font.PLAIN, 46));
        FontMetrics fm = g.getFontMetrics();

        int tx = x + (SQ_SIZE - fm.stringWidth(symbol)) / 2;
        int ty = y + (SQ_SIZE + fm.getAscent() - fm.getDescent()) / 2 - 2;

        // draw shadow
        g.setColor(new Color(0, 0, 0, 90));
        g.drawString(symbol, tx + 1, ty + 1);

        // draw piece
        if (color == 1) {
            // white piece - draw outline then fill
            g.setColor(new Color(40, 30, 20));
            g.drawString(symbol, tx - 1, ty);
            g.drawString(symbol, tx + 1, ty);
            g.drawString(symbol, tx, ty - 1);
            g.drawString(symbol, tx, ty + 1);
            g.setColor(Color.WHITE);
            g.drawString(symbol, tx, ty);
        } else {
            // black piece
            g.setColor(new Color(15, 15, 15));
            g.drawString(symbol, tx, ty);
        }
    }

    // ---- CLICK HANDLING ----
    void handleSquareClick(int row, int col) {
        int piece = chessBoard.board[row][col];

        if (selectedRow == -1) {
            // nothing selected yet - select a piece if it belongs to current player
            if (piece != 0 && chessBoard.getColor(piece) == chessBoard.currentTurn) {
                selectedRow = row;
                selectedCol = col;
                legalMovesForSelected = chessBoard.getLegalMoves(row, col);
            }
        } else {
            // something is selected - check if clicked square is a legal move
            int[] chosenMove = null;
            for (int[] m : legalMovesForSelected) {
                if (m[0] == row && m[1] == col) {
                    chosenMove = m;
                    break;
                }
            }

            if (chosenMove != null) {
                // it's a valid move - make it
                executeMove(selectedRow, selectedCol, chosenMove);
                selectedRow = -1; selectedCol = -1;
                legalMovesForSelected.clear();
            } else if (piece != 0 && chessBoard.getColor(piece) == chessBoard.currentTurn) {
                // clicked another own piece - switch selection
                selectedRow = row; selectedCol = col;
                legalMovesForSelected = chessBoard.getLegalMoves(row, col);
            } else {
                // clicked empty or enemy (not a legal move) - deselect
                selectedRow = -1; selectedCol = -1;
                legalMovesForSelected.clear();
            }
        }

        boardPanel.repaint();
    }

    void executeMove(int fromRow, int fromCol, int[] move) {
        int piece   = chessBoard.board[fromRow][fromCol];
        int toRow   = move[0], toCol = move[1];
        int flag    = move.length > 2 ? move[2] : 0;
        int captured = chessBoard.board[toRow][toCol];

        // handle pawn promotion for human player
        if (chessBoard.getType(piece) == 1 && (toRow == 0 || toRow == 7)) {
            if (flag == 0 || (flag >= 2 && flag <= 5)) {
                // ask user what to promote to
                String[] options = {"Queen", "Rook", "Bishop", "Knight"};
                int choice = JOptionPane.showOptionDialog(this,
                    "Choose promotion piece:", "Pawn Promotion",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                    null, options, options[0]);
                if (choice < 0) choice = 0;
                // find the matching promotion move
                int[] promoTypes = {5, 4, 3, 2};
                int promoType = promoTypes[choice];
                for (int[] m2 : legalMovesForSelected) {
                    if (m2[0] == toRow && m2[1] == toCol && m2.length > 2 && m2[2] == promoType) {
                        move = m2; flag = promoType; break;
                    }
                }
            }
        }

        // track captured piece
        if (captured != 0) {
            if (chessBoard.currentTurn == 1) capturedByWhite.add(chessBoard.getType(captured));
            else capturedByBlack.add(chessBoard.getType(captured));
        }
        // en passant capture
        if (flag == -1) {
            int capturedType = 1; // pawn
            if (chessBoard.currentTurn == 1) capturedByWhite.add(capturedType);
            else capturedByBlack.add(capturedType);
        }

        // record move in history (simple format)
        String files = "abcdefgh";
        String from = "" + files.charAt(fromCol) + (8 - fromRow);
        String to   = "" + files.charAt(toCol)   + (8 - toRow);
        String notation = from + "-" + to;
        if (flag == 10) notation = "O-O";
        if (flag == 11) notation = "O-O-O";
        moveHistory.add(notation);

        // make the move
        chessBoard.makeMove(fromRow, fromCol, move);

        updateUI();

        // check game result
        int nextTurn = chessBoard.currentTurn;
        if (!chessBoard.hasAnyLegalMoves(nextTurn)) {
            if (chessBoard.isKingInCheck(nextTurn)) {
                String winner = (nextTurn == 1) ? "Black" : "White";
                statusLabel.setText("Checkmate! " + winner + " wins!");
                statusLabel.setForeground(new Color(220, 80, 80));
                JOptionPane.showMessageDialog(this, "Checkmate! " + winner + " wins!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
            } else {
                statusLabel.setText("Stalemate! It's a draw.");
                JOptionPane.showMessageDialog(this, "Stalemate! Draw.", "Game Over", JOptionPane.INFORMATION_MESSAGE);
            }
            gameOver = true;
            boardPanel.repaint();
            return;
        }

        // update status
        String turnStr = (chessBoard.currentTurn == 1) ? "White" : "Black";
        statusLabel.setText(turnStr + "'s turn" + (chessBoard.isKingInCheck(chessBoard.currentTurn) ? " - CHECK!" : ""));
        statusLabel.setForeground(chessBoard.isKingInCheck(chessBoard.currentTurn) ? new Color(220, 80, 80) : TEXT_COLOR);

        boardPanel.repaint();

        // trigger AI move if needed
        // if (vsAI && chessBoard.currentTurn == 2 && !gameOver) {
        //     aiThinking = true;
        //     statusLabel.setText("AI is thinking...");
        //     SwingWorker<int[], Void> worker = new SwingWorker<>() {
        //         protected int[] doInBackground() {
        //             return ai.findBestMove(chessBoard);
        //         }
        //         protected void done() {
        //             try {
        //                 int[] aiMove = get();
        //                 aiThinking = false;
        //                 if (aiMove != null) {
        //                     executeMove(aiMove[0], aiMove[1], new int[]{aiMove[2], aiMove[3], aiMove[4]});
        //                 }
        //             } catch (Exception ex) {
        //                 aiThinking = false;
        //                 ex.printStackTrace();
        //             }
        //         }
        //     };
        //     worker.execute();
        // }
    }

    void updateUI() {
        // update move list
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < moveHistory.size(); i++) {
            if (i % 2 == 0) sb.append((i / 2 + 1)).append(". ");
            sb.append(moveHistory.get(i)).append("  ");
            if (i % 2 == 1) sb.append("\n");
        }
        moveListArea.setText(sb.toString());
        // scroll to bottom
        moveListArea.setCaretPosition(moveListArea.getDocument().getLength());

        // update captured pieces labels
        capturedWhiteLabel.setText("White captured: " + buildCapturedStr(capturedByWhite, 2));
        capturedBlackLabel.setText("Black captured: " + buildCapturedStr(capturedByBlack, 1));
    }

    String buildCapturedStr(ArrayList<Integer> types, int piecesOfColor) {
        if (types.isEmpty()) return "-";
        StringBuilder sb = new StringBuilder();
        for (int t : types) {
            sb.append(piecesOfColor == 1 ? whiteSymbols[t] : blackSymbols[t]);
        }
        return sb.toString();
    }

    // void showNewGameDialog() {
    //     String[] options = {"vs AI (Easy)", "vs AI (Medium)", "vs AI (Hard)", "2 Players (PvP)"};
    //     int choice = JOptionPane.showOptionDialog(this,
    //         "Select game mode:", "New Game",
    //         JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
    //         null, options, options[1]);
    //     if (choice < 0) return;

    //     chessBoard = new ChessBoard();
    //     moveHistory.clear();
    //     capturedByWhite.clear();
    //     capturedByBlack.clear();
    //     selectedRow = selectedCol = -1;
    //     legalMovesForSelected.clear();
    //     gameOver = false; aiThinking = false;

    //     if (choice == 3) {
    //         vsAI = false;
    //     } else {
    //         vsAI = true;
    //         ai.depth = choice + 1; // 1, 2, or 3
    //     }

    //     statusLabel.setText("White's turn to move");
    //     statusLabel.setForeground(TEXT_COLOR);
    //     updateUI();
    //     boardPanel.repaint();
    // }

    //only for human vs human remove this part when ai included
    //--------------------------------------------------------
    void showNewGameDialog() {
    String[] options = {"2 Players (PvP)"};

    int choice = JOptionPane.showOptionDialog(
        this,
        "Start a new game:",
        "New Game",
        JOptionPane.DEFAULT_OPTION,
        JOptionPane.QUESTION_MESSAGE,
        null,
        options,
        options[0]
    );

    if (choice < 0) return;

    // Reset game
    chessBoard = new ChessBoard();
    moveHistory.clear();
    capturedByWhite.clear();
    capturedByBlack.clear();
    selectedRow = selectedCol = -1;
    legalMovesForSelected.clear();
    gameOver = false;
    aiThinking = false;

    // Always PvP
    vsAI = false;

    statusLabel.setText("White's turn to move");
    statusLabel.setForeground(TEXT_COLOR);
    updateUI();
    boardPanel.repaint();
}
//--------------------------------------------------------------------------
    // helper to make styled buttons
    JButton makeButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setBackground(new Color(60, 90, 60));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(7, 12, 7, 12));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(185, 35));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        return btn;
    }

    JLabel makeLabel(String text, boolean isTitle) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Arial", isTitle ? Font.BOLD : Font.PLAIN, isTitle ? 12 : 11));
        l.setForeground(isTitle ? GOLD : TEXT_COLOR);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }
}
