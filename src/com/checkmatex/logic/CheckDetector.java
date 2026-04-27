package com.checkmatex.logic;

import com.checkmatex.pieces.Piece;
import com.checkmatex.utils.Constants;
import com.checkmatex.utils.Move;
import java.util.List;

public class CheckDetector {

    public static boolean isKingInCheck(GameState state, int color) {
        int kingRow = -1, kingCol = -1;
        
        // Find king
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = state.getPiece(r, c);
                if (p != null && p.getType() == Constants.KING && p.getColor() == color) {
                    kingRow = r;
                    kingCol = c;
                    break;
                }
            }
        }
        
        if (kingRow == -1) return false;

        int oppColor = (color == Constants.WHITE) ? Constants.BLACK : Constants.WHITE;
        
        // Check if any opponent piece attacks the king
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = state.getPiece(r, c);
                if (p != null && p.getColor() == oppColor) {
                    List<Move> pseudoMoves = p.getPseudoLegalMoves(state, r, c);
                    for (Move m : pseudoMoves) {
                        if (m.toRow == kingRow && m.toCol == kingCol) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
