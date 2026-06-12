package com.checkmatex.pieces;

import com.checkmatex.logic.GameState;
import com.checkmatex.utils.Constants;
import com.checkmatex.utils.Move;
import java.util.ArrayList;


public class Rook extends Piece {

    public Rook(int color) {
        super(color, Constants.ROOK, 5, color == Constants.WHITE ? "\u2656" : "\u265C");
    }

    @Override
    public ArrayList<Move> getPseudoLegalMoves(GameState state, int r, int c) {
        ArrayList<Move> moves = new ArrayList<>();
        int[][] dirs = {{-1,0},{1,0},{0,-1},{0,1}};
        for (int[] d : dirs) {
            int nr = r + d[0], nc = c + d[1];
            while (state.isValid(nr, nc)) {
                Piece target = state.getPiece(nr, nc);
                if (target == null) {
                    moves.add(new Move(r, c, nr, nc));
                } else {
                    if (target.getColor() != this.color) {
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
        return new Rook(this.color);
    }
}
