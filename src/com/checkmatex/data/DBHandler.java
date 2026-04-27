package com.checkmatex.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DBHandler {

    private static final String DB_URL = "jdbc:sqlite:checkmate.db";

    public DBHandler() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC Driver not found in classpath.");
        }
        initDatabase();
    }

    private void initDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            // Table for saving a game in progress
            String sqlSavedGames = "CREATE TABLE IF NOT EXISTS saved_games (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "board_state TEXT NOT NULL," +
                    "current_turn INTEGER NOT NULL," +
                    "white_time INTEGER NOT NULL," +
                    "black_time INTEGER NOT NULL," +
                    "castling_rights TEXT," +
                    "en_passant TEXT," +
                    "captured_white TEXT," +
                    "captured_black TEXT," +
                    "move_history TEXT" +
                    ")";
            stmt.execute(sqlSavedGames);

            // Table for match history
            String sqlMatchHistory = "CREATE TABLE IF NOT EXISTS match_history (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "winner TEXT," +
                    "moves TEXT," +
                    "duration TEXT," +
                    "date_time TEXT" +
                    ")";
            stmt.execute(sqlMatchHistory);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveMatchHistory(String winner, String moves, String duration, String dateTime) {
        String sql = "INSERT INTO match_history(winner, moves, duration, date_time) VALUES(?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, winner);
            pstmt.setString(2, moves);
            pstmt.setString(3, duration);
            pstmt.setString(4, dateTime);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<GameHistory> getMatchHistory() {
        List<GameHistory> history = new ArrayList<>();
        String sql = "SELECT * FROM match_history ORDER BY id DESC";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                history.add(new GameHistory(
                        rs.getInt("id"),
                        rs.getString("winner"),
                        rs.getString("moves"),
                        rs.getString("duration"),
                        rs.getString("date_time")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return history;
    }

    public void saveGame(String boardState, int currentTurn, int whiteTime, int blackTime, 
                         String castlingRights, String enPassant, String capturedWhite, 
                         String capturedBlack, String moveHistory) {
        // We will just keep one save slot for simplicity. We can clear the table first.
        String clearSql = "DELETE FROM saved_games";
        String insertSql = "INSERT INTO saved_games(board_state, current_turn, white_time, black_time, castling_rights, en_passant, captured_white, captured_black, move_history) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
            
            stmt.execute(clearSql); // clear previous save

            pstmt.setString(1, boardState);
            pstmt.setInt(2, currentTurn);
            pstmt.setInt(3, whiteTime);
            pstmt.setInt(4, blackTime);
            pstmt.setString(5, castlingRights);
            pstmt.setString(6, enPassant);
            pstmt.setString(7, capturedWhite);
            pstmt.setString(8, capturedBlack);
            pstmt.setString(9, moveHistory);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // A simple container class to return saved game data
    public static class SavedGameData {
        public String boardState;
        public int currentTurn;
        public int whiteTime;
        public int blackTime;
        public String castlingRights;
        public String enPassant;
        public String capturedWhite;
        public String capturedBlack;
        public String moveHistory;
    }

    public SavedGameData loadGame() {
        String sql = "SELECT * FROM saved_games LIMIT 1";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                SavedGameData data = new SavedGameData();
                data.boardState = rs.getString("board_state");
                data.currentTurn = rs.getInt("current_turn");
                data.whiteTime = rs.getInt("white_time");
                data.blackTime = rs.getInt("black_time");
                data.castlingRights = rs.getString("castling_rights");
                data.enPassant = rs.getString("en_passant");
                data.capturedWhite = rs.getString("captured_white");
                data.capturedBlack = rs.getString("captured_black");
                data.moveHistory = rs.getString("move_history");
                return data;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
