package com.checkmatex.pieces;

import com.checkmatex.logic.GameState;
import com.checkmatex.utils.Move;
import java.util.List;

public abstract class Piece {
    protected int color;
    protected int type;
    protected int pointValue;
    protected String unicodeSymbol;

    public Piece(int color, int type, int pointValue, String unicodeSymbol) {
        this.color = color;
        this.type = type;
        this.pointValue = pointValue;
        this.unicodeSymbol = unicodeSymbol;
    }

    public int getColor() { return color; }
    public int getType() { return type; }
    public int getPointValue() { return pointValue; }
    public String getUnicodeSymbol() { return unicodeSymbol; }

    public abstract List<Move> getPseudoLegalMoves(GameState state, int row, int col);
    
    public abstract Piece copy();
}
