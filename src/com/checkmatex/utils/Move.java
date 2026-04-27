package com.checkmatex.utils;

public class Move {
    public int fromRow;
    public int fromCol;
    public int toRow;
    public int toCol;
    public int flag;

    public Move(int fromRow, int fromCol, int toRow, int toCol) {
        this(fromRow, fromCol, toRow, toCol, Constants.FLAG_NONE);
    }

    public Move(int fromRow, int fromCol, int toRow, int toCol, int flag) {
        this.fromRow = fromRow;
        this.fromCol = fromCol;
        this.toRow = toRow;
        this.toCol = toCol;
        this.flag = flag;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Move move = (Move) obj;
        return fromRow == move.fromRow &&
               fromCol == move.fromCol &&
               toRow == move.toRow &&
               toCol == move.toCol &&
               flag == move.flag;
    }

    @Override
    public String toString() {
        return "Move{" +
                "from=" + fromRow + "," + fromCol +
                ", to=" + toRow + "," + toCol +
                ", flag=" + flag +
                '}';
    }
}
