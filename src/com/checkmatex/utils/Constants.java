package com.checkmatex.utils;

public class Constants {
    // Player Constants
    public static final int WHITE = 1;
    public static final int BLACK = 2;

    // Piece Types
    public static final int PAWN = 1;
    public static final int KNIGHT = 2;
    public static final int BISHOP = 3;
    public static final int ROOK = 4;
    public static final int QUEEN = 5;
    public static final int KING = 6;

    // Move Flags
    public static final int FLAG_NONE = 0;
    public static final int FLAG_EN_PASSANT = -1;
    public static final int FLAG_CASTLE_KINGSIDE = 10;
    public static final int FLAG_CASTLE_QUEENSIDE = 11;
    // Promotion flags use piece types (2, 3, 4, 5)
}
