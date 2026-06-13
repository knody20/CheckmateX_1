package com.checkmatex.pieces;

import com.checkmatex.logic.*;
import com.checkmatex.utils.*;
import java.util.ArrayList;

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

    //getters
    public int getColor() { return color; }
    public int getType() { return type; }
    public int getPointValue() { return pointValue; }
    public String getUnicodeSymbol() { return unicodeSymbol; }
   
    //forcing all subclass to implement this method
    public abstract ArrayList<Move> getPseudoLegalMoves(GameState state, int row, int col);
    
    //safety for undo redo practices - before changing make a copy
    public abstract Piece copy();
}
