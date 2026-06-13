package com.checkmatex.pieces;

import com.checkmatex.logic.GameState;
import com.checkmatex.utils.Constants;
import com.checkmatex.utils.Move;
import java.util.ArrayList;


public class Knight extends Piece {

    public Knight(int color) {
        super(color, Constants.KNIGHT, 3, color == Constants.WHITE ? "\u2658" : "\u265E");
    }

    @Override
    public ArrayList<Move> getPseudoLegalMoves(GameState state, int r, int c) {
        ArrayList<Move> moves = new ArrayList<>();
        int[][] jumps = {{-2,-1},{-2,1},{-1,-2},{-1,2},{1,-2},{1,2},{2,-1},{2,1}};
        for (int[] j : jumps) {
            int nr = r + j[0], nc = c + j[1];
            if (state.isValid(nr, nc)) {
                Piece target = state.getPiece(nr, nc);
                if (target == null || target.getColor() != this.color) {
                    moves.add(new Move(r, c, nr, nc));
                }
            }
        }
        return moves;
    }

    @Override
    public Piece copy() {
        return new Knight(this.color);
    }
}
