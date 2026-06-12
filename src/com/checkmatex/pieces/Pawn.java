package com.checkmatex.pieces;

import com.checkmatex.logic.GameState;
import com.checkmatex.utils.Constants;
import com.checkmatex.utils.Move;
import java.util.ArrayList;


public class Pawn extends Piece {

    public Pawn(int color) {
        super(color, Constants.PAWN, 1, color == Constants.WHITE ? "\u2659" : "\u265F");
    }

    @Override
    public ArrayList<Move> getPseudoLegalMoves(GameState state, int r, int c) {
        ArrayList<Move> moves = new ArrayList<>();
        int dir = (color == Constants.WHITE) ? -1 : 1;
        int startRow = (color == Constants.WHITE) ? 6 : 1;
        int promoRow = (color == Constants.WHITE) ? 0 : 7;

        // move forward 1
        if (state.isValid(r + dir, c) && state.getPiece(r + dir, c) == null) {
            if (r + dir == promoRow) {
                moves.add(new Move(r, c, r + dir, c, Constants.QUEEN));
                moves.add(new Move(r, c, r + dir, c, Constants.ROOK));
                moves.add(new Move(r, c, r + dir, c, Constants.BISHOP));
                moves.add(new Move(r, c, r + dir, c, Constants.KNIGHT));
            } else {
                moves.add(new Move(r, c, r + dir, c));
            }

            // move forward 2 from starting row
            if (r == startRow && state.getPiece(r + 2 * dir, c) == null) {
                moves.add(new Move(r, c, r + 2 * dir, c));
            }
        }

        // diagonal captures
        for (int dc : new int[]{-1, 1}) {
            int nc = c + dc;
            if (!state.isValid(r + dir, nc)) continue;

            // normal capture
            Piece target = state.getPiece(r + dir, nc);
            if (target != null && target.getColor() != this.color) {
                if (r + dir == promoRow) {
                    moves.add(new Move(r, c, r + dir, nc, Constants.QUEEN));
                    moves.add(new Move(r, c, r + dir, nc, Constants.ROOK));
                    moves.add(new Move(r, c, r + dir, nc, Constants.BISHOP));
                    moves.add(new Move(r, c, r + dir, nc, Constants.KNIGHT));
                } else {
                    moves.add(new Move(r, c, r + dir, nc));
                }
            }

            // en passant capture
            if (state.enPassantCol == nc && state.enPassantRow == r) {
                moves.add(new Move(r, c, r + dir, nc, Constants.FLAG_EN_PASSANT));
            }
        }
        return moves;
    }

    @Override
    public Piece copy() {
        return new Pawn(this.color);
    }
}
