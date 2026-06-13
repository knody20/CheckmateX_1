package com.checkmatex.pieces;

import com.checkmatex.logic.GameState;
import com.checkmatex.utils.Constants;
import com.checkmatex.utils.Move;
import java.util.ArrayList;


public class King extends Piece {

    public King(int color) {
        super(color, Constants.KING, 1000, color == Constants.WHITE ? "\u2654" : "\u265A");
    }

    @Override
    public ArrayList<Move> getPseudoLegalMoves(GameState state, int r, int c) {
        ArrayList<Move> moves = new ArrayList<>();
        
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) continue;
                int nr = r + dr, nc = c + dc;
                if (state.isValid(nr, nc)) {
                    Piece target = state.getPiece(nr, nc);
                    if (target == null || target.getColor() != this.color) {
                        moves.add(new Move(r, c, nr, nc));
                    }
                }
            }
        }

        // castling
        int backRow = (color == Constants.WHITE) ? 7 : 0;
        boolean canCastleK = (color == Constants.WHITE) ? state.whiteCanCastleKing : state.blackCanCastleKing;
        boolean canCastleQ = (color == Constants.WHITE) ? state.whiteCanCastleQueen : state.blackCanCastleQueen;

        if (r == backRow && c == 4) {
            // kingside
            if (canCastleK && state.getPiece(backRow, 5) == null && state.getPiece(backRow, 6) == null) {
                Piece rook = state.getPiece(backRow, 7);
                if (rook != null && rook.getType() == Constants.ROOK && rook.getColor() == this.color) {
                    moves.add(new Move(r, c, backRow, 6, Constants.FLAG_CASTLE_KINGSIDE));
                }
            }
            // queenside
            if (canCastleQ && state.getPiece(backRow, 3) == null && state.getPiece(backRow, 2) == null && state.getPiece(backRow, 1) == null) {
                Piece rook = state.getPiece(backRow, 0);
                if (rook != null && rook.getType() == Constants.ROOK && rook.getColor() == this.color) {
                    moves.add(new Move(r, c, backRow, 2, Constants.FLAG_CASTLE_QUEENSIDE));
                }
            }
        }

        return moves;
    }

    @Override
    public Piece copy() {
        return new King(this.color);
    }
}
