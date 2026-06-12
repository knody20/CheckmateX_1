package com.checkmatex.pieces;

import com.checkmatex.logic.GameState;
import com.checkmatex.utils.*;
import java.util.ArrayList;                    


public class Bishop extends Piece {

    public Bishop(int color) {
        super(color, Constants.BISHOP,3, color == Constants.WHITE ? "\u2657" : "\u265D");
    }

    @Override
    public ArrayList<Move> getPseudoLegalMoves(GameState state, int r, int c) {
        ArrayList<Move> moves = new ArrayList<>();
        int[][] dirs = {{-1,-1},{-1,1},{1,-1},{1,1}};
        for (int[] d : dirs) {
            int nr = r + d[0], nc = c + d[1];
            while (state.isValid(nr, nc)) {
                Piece target = state.getPiece(nr, nc);
                if (target == null) {                           //no piece exist move
                    moves.add(new Move(r, c, nr, nc));
                } else {
                    if (target.getColor() != this.color) {      //diff color, capture is legal, move
                        moves.add(new Move(r, c, nr, nc));
                    }
                    break;
                }
                nr += d[0]; nc += d[1];
            }
        }
        return moves;
    }

    @Override
    public Piece copy() {
        return new Bishop(this.color);
    }
}
