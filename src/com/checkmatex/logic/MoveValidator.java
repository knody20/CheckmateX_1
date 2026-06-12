package com.checkmatex.logic;

import com.checkmatex.pieces.Piece;
import com.checkmatex.utils.Move;
import java.util.ArrayList;

public class MoveValidator {

    public static ArrayList<Move> getLegalMoves(GameState state, int row, int col) {
        ArrayList<Move> legalMoves = new ArrayList<>();
        Piece piece = state.getPiece(row, col);
        if (piece == null) return legalMoves;

        ArrayList<Move> candidates = piece.getPseudoLegalMoves(state, row, col);

        for (Move move : candidates) {
            //Clone state and apply move
            GameState tempState = state.copy();
            MoveManager.applyMoveInternal(tempState, move);

            //Check if king is safe
            if (!CheckDetector.isKingInCheck(tempState, piece.getColor())) {
                legalMoves.add(move);
            }
        }
        return legalMoves;              //returns all legal moves
    }
    
    //to check whether a player has atleast one legal move
    public static boolean hasAnyLegalMoves(GameState state, int color) {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = state.getPiece(r, c);
                if (p != null && p.getColor() == color) {
                    if (!getLegalMoves(state, r, c).isEmpty()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
