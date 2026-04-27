package com.checkmatex.logic;

import com.checkmatex.pieces.Piece;
import com.checkmatex.utils.Constants;

public class GameState {
    public Piece[][] board;
    public int currentTurn;
    public boolean whiteCanCastleKing;
    public boolean whiteCanCastleQueen;
    public boolean blackCanCastleKing;
    public boolean blackCanCastleQueen;
    public int enPassantCol;
    public int enPassantRow;

    public GameState() {
        board = new Piece[8][8];
        currentTurn = Constants.WHITE;
        whiteCanCastleKing = true;
        whiteCanCastleQueen = true;
        blackCanCastleKing = true;
        blackCanCastleQueen = true;
        enPassantCol = -1;
        enPassantRow = -1;
    }

    public void setupInitialBoard() {
        // clear board
        for(int r = 0; r < 8; r++)
            for(int c = 0; c < 8; c++)
                board[r][c] = null;

        // Black Pieces
        board[0][0] = new com.checkmatex.pieces.Rook(Constants.BLACK);
        board[0][1] = new com.checkmatex.pieces.Knight(Constants.BLACK);
        board[0][2] = new com.checkmatex.pieces.Bishop(Constants.BLACK);
        board[0][3] = new com.checkmatex.pieces.Queen(Constants.BLACK);
        board[0][4] = new com.checkmatex.pieces.King(Constants.BLACK);
        board[0][5] = new com.checkmatex.pieces.Bishop(Constants.BLACK);
        board[0][6] = new com.checkmatex.pieces.Knight(Constants.BLACK);
        board[0][7] = new com.checkmatex.pieces.Rook(Constants.BLACK);
        for(int c = 0; c < 8; c++) board[1][c] = new com.checkmatex.pieces.Pawn(Constants.BLACK);

        // White Pieces
        for(int c = 0; c < 8; c++) board[6][c] = new com.checkmatex.pieces.Pawn(Constants.WHITE);
        board[7][0] = new com.checkmatex.pieces.Rook(Constants.WHITE);
        board[7][1] = new com.checkmatex.pieces.Knight(Constants.WHITE);
        board[7][2] = new com.checkmatex.pieces.Bishop(Constants.WHITE);
        board[7][3] = new com.checkmatex.pieces.Queen(Constants.WHITE);
        board[7][4] = new com.checkmatex.pieces.King(Constants.WHITE);
        board[7][5] = new com.checkmatex.pieces.Bishop(Constants.WHITE);
        board[7][6] = new com.checkmatex.pieces.Knight(Constants.WHITE);
        board[7][7] = new com.checkmatex.pieces.Rook(Constants.WHITE);

        currentTurn = Constants.WHITE;
        whiteCanCastleKing = true;
        whiteCanCastleQueen = true;
        blackCanCastleKing = true;
        blackCanCastleQueen = true;
        enPassantCol = -1;
        enPassantRow = -1;
    }

    public Piece getPiece(int row, int col) {
        if (isValid(row, col)) {
            return board[row][col];
        }
        return null;
    }

    public boolean isValid(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }
    
    public GameState copy() {
        GameState copy = new GameState();
        copy.currentTurn = this.currentTurn;
        copy.whiteCanCastleKing = this.whiteCanCastleKing;
        copy.whiteCanCastleQueen = this.whiteCanCastleQueen;
        copy.blackCanCastleKing = this.blackCanCastleKing;
        copy.blackCanCastleQueen = this.blackCanCastleQueen;
        copy.enPassantCol = this.enPassantCol;
        copy.enPassantRow = this.enPassantRow;
        
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if (this.board[r][c] != null) {
                    copy.board[r][c] = this.board[r][c].copy();
                }
            }
        }
        return copy;
    }
}
