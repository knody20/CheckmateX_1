package com.checkmatex.pieces;

import com.checkmatex.logic.GameState;
import com.checkmatex.utils.Constants;
import com.checkmatex.utils.Move;
import java.util.ArrayList;
import java.util.List;

public class Queen extends Piece {

    public Queen(int color) {
        super(color, Constants.QUEEN, 9, color == Constants.WHITE ? "\u2655" : "\u265B");
    }

    @Override
    public List<Move> getPseudoLegalMoves(GameState state, int r, int c) {
        List<Move> moves = new ArrayList<>();
        int[][] dirs = {{-1,-1},{-1,1},{1,-1},{1,1},{-1,0},{1,0},{0,-1},{0,1}};
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
        return new Queen(this.color);
    }
}
