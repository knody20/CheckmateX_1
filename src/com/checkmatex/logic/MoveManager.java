package com.checkmatex.logic;

import com.checkmatex.pieces.*;
import com.checkmatex.utils.Constants;
import com.checkmatex.utils.Move;

public class MoveManager {

    public static void applyMoveInternal(GameState state, Move move) {
        Piece piece = state.board[move.fromRow][move.fromCol];
        if (piece == null) return;

        state.board[move.toRow][move.toCol] = piece;
        state.board[move.fromRow][move.fromCol] = null;

        // En Passant Capture
        if (move.flag == Constants.FLAG_EN_PASSANT) {
            state.board[move.fromRow][move.toCol] = null;
        }

        // Promotion
        if (move.flag >= 2 && move.flag <= 5) {
            int color = piece.getColor();
            if (move.flag == Constants.QUEEN) state.board[move.toRow][move.toCol] = new Queen(color);
            else if (move.flag == Constants.ROOK) state.board[move.toRow][move.toCol] = new Rook(color);
            else if (move.flag == Constants.BISHOP) state.board[move.toRow][move.toCol] = new Bishop(color);
            else if (move.flag == Constants.KNIGHT) state.board[move.toRow][move.toCol] = new Knight(color);
        }

        // Castling
        if (move.flag == Constants.FLAG_CASTLE_KINGSIDE) {
            state.board[move.toRow][5] = state.board[move.toRow][7];
            state.board[move.toRow][7] = null;
        }
        if (move.flag == Constants.FLAG_CASTLE_QUEENSIDE) {
            state.board[move.toRow][3] = state.board[move.toRow][0];
            state.board[move.toRow][0] = null;
        }

        // Update castling rights
        if (piece.getType() == Constants.KING) {
            if (piece.getColor() == Constants.WHITE) {
                state.whiteCanCastleKing = false;
                state.whiteCanCastleQueen = false;
            } else {
                state.blackCanCastleKing = false;
                state.blackCanCastleQueen = false;
            }
        }
        if (piece.getType() == Constants.ROOK) {
            if (move.fromRow == 7 && move.fromCol == 7) state.whiteCanCastleKing = false;
            if (move.fromRow == 7 && move.fromCol == 0) state.whiteCanCastleQueen = false;
            if (move.fromRow == 0 && move.fromCol == 7) state.blackCanCastleKing = false;
            if (move.fromRow == 0 && move.fromCol == 0) state.blackCanCastleQueen = false;
        }

        // Set en passant target
        if (piece.getType() == Constants.PAWN && Math.abs(move.toRow - move.fromRow) == 2) {
            state.enPassantCol = move.fromCol;
            state.enPassantRow = move.toRow;
        } else {
            state.enPassantCol = -1;
            state.enPassantRow = -1;
        }
    }
}
