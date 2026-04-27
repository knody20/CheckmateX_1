package com.checkmatex.ui;

import com.checkmatex.data.DBHandler;
import com.checkmatex.logic.*;
import com.checkmatex.pieces.Piece;
import com.checkmatex.utils.Constants;
import com.checkmatex.utils.Move;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GameFrame extends JFrame {

    private GameState gameState;
    private UndoRedoManager undoRedoManager;
    private TimerManager timerManager;
    private DBHandler dbHandler;
    private BoardPanel boardPanel;
    private SidePanel sidePanel;

    private boolean gameOver = false;
    private List<String> moveHistory = new ArrayList<>();
    private Move lastMove = null;

    public GameFrame() {
        super("CheckMate X — Professional Edition");

        dbHandler = new DBHandler();
        undoRedoManager = new UndoRedoManager();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(new Color(0x181818));

        // ── Top Header bar ──────────────────────────────────
        add(buildHeader(), BorderLayout.NORTH);

        // ── Initialize game state ───────────────────────────
        gameState = new GameState();
        gameState.setupInitialBoard();

        // ── Board (centre) ──────────────────────────────────
        boardPanel = new BoardPanel(this, gameState);
        JPanel boardWrap = new JPanel(new GridBagLayout()); // GridBagLayout centres perfectly
        boardWrap.setBackground(new Color(0x181818));
        boardWrap.setBorder(new EmptyBorder(15, 20, 15, 10));
        boardWrap.add(boardPanel);
        add(boardWrap, BorderLayout.CENTER);

        // ── Side panel (right) ──────────────────────────────
        sidePanel = new SidePanel(this);
        add(sidePanel, BorderLayout.EAST);

        // ── Footer status bar ───────────────────────────────
        add(buildFooter(), BorderLayout.SOUTH);

        // ── Timer default ───────────────────────────────────
        initTimers(10);

        pack();
        setMinimumSize(getPreferredSize());
        setLocationRelativeTo(null);
        setResizable(true);
        setVisible(true);

        showNewGameDialog();
    }

    // ── Header ─────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(0x121212));
        header.setBorder(new EmptyBorder(10, 16, 10, 16));

        JLabel logo = new JLabel("♔  CheckMate X");
        logo.setFont(new Font("SansSerif", Font.BOLD, 22));
        logo.setForeground(new Color(0xF0C040));

        JLabel sub = new JLabel("Professional Edition");
        sub.setFont(new Font("SansSerif", Font.ITALIC, 13));
        sub.setForeground(new Color(0x888899));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setBackground(new Color(0x121212));
        left.add(logo);
        left.add(sub);

        header.add(left, BorderLayout.WEST);
        return header;
    }

    // ── Footer ─────────────────────────────────────────────────
    private JPanel buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 5));
        footer.setBackground(new Color(0x111111));
        JLabel info = new JLabel("Design & Analysis of Algorithms  |  Java Swing + JDBC + SQLite");
        info.setFont(new Font("SansSerif", Font.PLAIN, 11));
        info.setForeground(new Color(0x555566));
        footer.add(info);
        return footer;
    }

    // ── Timer init ─────────────────────────────────────────────
    private void initTimers(int minutes) {
        if (timerManager != null) timerManager.stop();
        timerManager = new TimerManager(minutes,
            () -> sidePanel.timerPanel.updateTimers(timerManager.getWhiteTime(), timerManager.getBlackTime()),
            this::handleTimeout
        );
        sidePanel.timerPanel.updateTimers(timerManager.getWhiteTime(), timerManager.getBlackTime());
    }

    // ── New Game Dialog ────────────────────────────────────────
    public void confirmNewGame() {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Start a new game? Unsaved progress will be lost.",
                "Confirm New Game",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            showNewGameDialog();
        }
    }

    public void showNewGameDialog() {
        String[] options = {"5 min", "10 min", "15 min"};
        int choice = JOptionPane.showOptionDialog(this,
                "Select Time Control:", "New Game",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options, options[1]);

        if (choice < 0) {
            if (lastMove == null && moveHistory.isEmpty()) {
                timerManager.start(Constants.WHITE);
            }
            return;
        }

        int mins = 10;
        if      (choice == 0) mins = 5;
        else if (choice == 2) mins = 15;

        gameState = new GameState();
        gameState.setupInitialBoard();
        undoRedoManager.clear();
        moveHistory.clear();
        lastMove = null;
        gameOver = false;

        sidePanel.capturedPanel.clear();
        sidePanel.updateMoveHistory(moveHistory);

        initTimers(mins);
        timerManager.start(Constants.WHITE);

        updateUI();
    }

    // ── Execute Move ───────────────────────────────────────────
    public void executeMove(Move move) {
        if (gameOver) return;

        undoRedoManager.saveState(gameState);

        Piece capturedPiece = gameState.getPiece(move.toRow, move.toCol);
        if (move.flag == Constants.FLAG_EN_PASSANT) {
            capturedPiece = gameState.getPiece(move.fromRow, move.toCol);
        }
        if (capturedPiece != null) {
            sidePanel.capturedPanel.addCapturedPiece(capturedPiece);
        }

        moveHistory.add(generateNotation(move));
        sidePanel.updateMoveHistory(moveHistory);

        MoveManager.applyMoveInternal(gameState, move);
        lastMove = move;
        gameState.currentTurn = (gameState.currentTurn == Constants.WHITE) ? Constants.BLACK : Constants.WHITE;

        checkGameEnd();

        if (!gameOver) timerManager.switchTurn(gameState.currentTurn);

        updateUI();
    }

    private void checkGameEnd() {
        if (!MoveValidator.hasAnyLegalMoves(gameState, gameState.currentTurn)) {
            gameOver = true;
            timerManager.stop();
            if (CheckDetector.isKingInCheck(gameState, gameState.currentTurn)) {
                String winner = (gameState.currentTurn == Constants.WHITE) ? "Black" : "White";
                JOptionPane.showMessageDialog(this, "Checkmate! " + winner + " wins!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
                dbHandler.saveMatchHistory(winner, moveHistory.toString(), "N/A", java.time.LocalDateTime.now().toString());
            } else {
                JOptionPane.showMessageDialog(this, "Stalemate! It's a draw.", "Game Over", JOptionPane.INFORMATION_MESSAGE);
                dbHandler.saveMatchHistory("Draw", moveHistory.toString(), "N/A", java.time.LocalDateTime.now().toString());
            }
        }
    }

    private void handleTimeout() {
        gameOver = true;
        String winner = (gameState.currentTurn == Constants.WHITE) ? "Black" : "White";
        JOptionPane.showMessageDialog(this, "Time Out! " + winner + " wins!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
        dbHandler.saveMatchHistory(winner + " (Time)", moveHistory.toString(), "N/A", java.time.LocalDateTime.now().toString());
        updateUI();
    }

    // ── Undo / Redo ────────────────────────────────────────────
    public void undoMove() {
        if (undoRedoManager.canUndo()) {
            gameState = undoRedoManager.undo(gameState);
            if (!moveHistory.isEmpty()) moveHistory.remove(moveHistory.size() - 1);
            lastMove = null;
            sidePanel.updateMoveHistory(moveHistory);
            sidePanel.capturedPanel.clear();
            timerManager.switchTurn(gameState.currentTurn);
            updateUI();
        }
    }

    public void redoMove() {
        if (undoRedoManager.canRedo()) {
            gameState = undoRedoManager.redo(gameState);
            timerManager.switchTurn(gameState.currentTurn);
            updateUI();
        }
    }

    // ── Save / Load ────────────────────────────────────────────
    public void saveGame() {
        dbHandler.saveGame("FEN_placeholder", gameState.currentTurn,
                timerManager.getWhiteTime(), timerManager.getBlackTime(),
                "rights", "ep", "cw", "cb", moveHistory.toString());
        JOptionPane.showMessageDialog(this, "Game Saved!", "Save", JOptionPane.INFORMATION_MESSAGE);
    }

    public void loadGame() {
        DBHandler.SavedGameData data = dbHandler.loadGame();
        if (data != null) {
            JOptionPane.showMessageDialog(this,
                    "Load successful!\n(Full board restoration requires FEN serialisation.)",
                    "Load", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "No saved game found.", "Load", JOptionPane.WARNING_MESSAGE);
        }
    }

    // ── Helpers ────────────────────────────────────────────────
    private String generateNotation(Move move) {
        if (move.flag == Constants.FLAG_CASTLE_KINGSIDE)  return "O-O";
        if (move.flag == Constants.FLAG_CASTLE_QUEENSIDE) return "O-O-O";
        String f = "abcdefgh";
        return "" + f.charAt(move.fromCol) + (8 - move.fromRow)
                + "-" + f.charAt(move.toCol) + (8 - move.toRow);
    }

    private void updateUI() {
        boardPanel.updateState(gameState, lastMove);

        String turnStr = (gameState.currentTurn == Constants.WHITE) ? "White" : "Black";
        boolean inCheck = CheckDetector.isKingInCheck(gameState, gameState.currentTurn);
        Color col = inCheck ? new Color(0xFF5050) : new Color(0xE0E0E0);
        sidePanel.timerPanel.updateStatus(
                turnStr + "'s turn" + (inCheck ? "  — CHECK!" : ""), col);
    }

    public boolean isGameOver() { return gameOver; }
}
