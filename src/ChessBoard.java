// ChessBoard.java
// This class handles all the chess logic - board state, move generation, check detection
// We use a 2D array to represent the board
// Piece encoding: first digit = color (1=white, 2=black), second digit = type
// Types: 1=Pawn, 2=Knight, 3=Bishop, 4=Rook, 5=Queen, 6=King
// Example: 11 = white pawn, 26 = black king, 0 = empty

import java.util.ArrayList;

public class ChessBoard {

    // the board - 8x8 grid
    int[][] board = new int[8][8];

    // whos turn is it - 1 for white, 2 for black
    int currentTurn = 1;

    // castling rights - can each side still castle?
    boolean whiteCanCastleKing  = true;
    boolean whiteCanCastleQueen = true;
    boolean blackCanCastleKing  = true;
    boolean blackCanCastleQueen = true;

    // en passant - column of pawn that just double moved (-1 if none)
    int enPassantCol = -1;
    int enPassantRow = -1; // row of that pawn

    // store last move for highlighting
    int lastFromRow = -1, lastFromCol = -1;
    int lastToRow   = -1, lastToCol   = -1;

    // constructor - set up pieces
    public ChessBoard() {
        setupBoard();
    }

    // initialize pieces to starting positions
    void setupBoard() {
        // clear board first
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++)
                board[r][c] = 0;

        // black pieces (row 0 and 1)
        board[0][0] = 24; board[0][1] = 22; board[0][2] = 23;
        board[0][3] = 25; board[0][4] = 26;
        board[0][5] = 23; board[0][6] = 22; board[0][7] = 24;
        for (int c = 0; c < 8; c++) board[1][c] = 21; // black pawns

        // white pieces (row 6 and 7)
        for (int c = 0; c < 8; c++) board[6][c] = 11; // white pawns
        board[7][0] = 14; board[7][1] = 12; board[7][2] = 13;
        board[7][3] = 15; board[7][4] = 16;
        board[7][5] = 13; board[7][6] = 12; board[7][7] = 14;

        currentTurn = 1;
        whiteCanCastleKing = whiteCanCastleQueen = true;
        blackCanCastleKing = blackCanCastleQueen = true;
        enPassantCol = -1; enPassantRow = -1;
        lastFromRow = lastFromCol = lastToRow = lastToCol = -1;
    }

    int getColor(int piece) { return piece / 10; }
    int getType(int piece)  { return piece % 10; }

    // get all possible squares this piece can move to (before checking if king is in check)
    // this is called pseudo-legal moves
    ArrayList<int[]> getPseudoMoves(int row, int col) {
        ArrayList<int[]> moves = new ArrayList<>();
        int piece = board[row][col];
        if (piece == 0) return moves;

        int color = getColor(piece);
        int type  = getType(piece);

        if (type == 1) getPawnMoves(moves, row, col, color);
        else if (type == 2) getKnightMoves(moves, row, col, color);
        else if (type == 3) getBishopMoves(moves, row, col, color);
        else if (type == 4) getRookMoves(moves, row, col, color);
        else if (type == 5) getQueenMoves(moves, row, col, color);
        else if (type == 6) getKingMoves(moves, row, col, color);

        return moves;
    }

    void getPawnMoves(ArrayList<int[]> moves, int r, int c, int color) {
        // direction depends on color (white moves up = -1, black moves down = +1)
        int dir = (color == 1) ? -1 : 1;
        int startRow = (color == 1) ? 6 : 1;
        int promoRow = (color == 1) ? 0 : 7;

        // move forward 1
        if (isValid(r + dir, c) && board[r + dir][c] == 0) {
            if (r + dir == promoRow) {
                // pawn promotion - add all 4 options
                moves.add(new int[]{r + dir, c, 5}); // queen
                moves.add(new int[]{r + dir, c, 4}); // rook
                moves.add(new int[]{r + dir, c, 3}); // bishop
                moves.add(new int[]{r + dir, c, 2}); // knight
            } else {
                moves.add(new int[]{r + dir, c});
            }

            // move forward 2 from starting row
            if (r == startRow && board[r + 2 * dir][c] == 0) {
                moves.add(new int[]{r + 2 * dir, c});
            }
        }

        // diagonal captures
        for (int dc : new int[]{-1, 1}) {
            int nc = c + dc;
            if (!isValid(r + dir, nc)) continue;

            // normal capture
            if (board[r + dir][nc] != 0 && getColor(board[r + dir][nc]) != color) {
                if (r + dir == promoRow) {
                    moves.add(new int[]{r + dir, nc, 5});
                    moves.add(new int[]{r + dir, nc, 4});
                    moves.add(new int[]{r + dir, nc, 3});
                    moves.add(new int[]{r + dir, nc, 2});
                } else {
                    moves.add(new int[]{r + dir, nc});
                }
            }

            // en passant capture
            if (enPassantCol == nc && enPassantRow == r) {
                moves.add(new int[]{r + dir, nc, -1}); // -1 = en passant flag
            }
        }
    }

    void getKnightMoves(ArrayList<int[]> moves, int r, int c, int color) {
        // knight moves in L shape
        int[][] jumps = {{-2,-1},{-2,1},{-1,-2},{-1,2},{1,-2},{1,2},{2,-1},{2,1}};
        for (int[] j : jumps) {
            int nr = r + j[0], nc = c + j[1];
            if (isValid(nr, nc) && getColor(board[nr][nc]) != color)
                moves.add(new int[]{nr, nc});
        }
    }

    void getBishopMoves(ArrayList<int[]> moves, int r, int c, int color) {
        // bishop slides diagonally
        int[][] dirs = {{-1,-1},{-1,1},{1,-1},{1,1}};
        slidingMoves(moves, r, c, color, dirs);
    }

    void getRookMoves(ArrayList<int[]> moves, int r, int c, int color) {
        // rook slides horizontally and vertically
        int[][] dirs = {{-1,0},{1,0},{0,-1},{0,1}};
        slidingMoves(moves, r, c, color, dirs);
    }

    void getQueenMoves(ArrayList<int[]> moves, int r, int c, int color) {
        // queen = bishop + rook combined
        int[][] dirs = {{-1,-1},{-1,1},{1,-1},{1,1},{-1,0},{1,0},{0,-1},{0,1}};
        slidingMoves(moves, r, c, color, dirs);
    }

    // helper for sliding pieces (bishop, rook, queen)
    void slidingMoves(ArrayList<int[]> moves, int r, int c, int color, int[][] dirs) {
        for (int[] d : dirs) {
            int nr = r + d[0], nc = c + d[1];
            while (isValid(nr, nc)) {
                if (board[nr][nc] == 0) {
                    moves.add(new int[]{nr, nc});
                } else {
                    if (getColor(board[nr][nc]) != color)
                        moves.add(new int[]{nr, nc}); // can capture
                    break; // blocked, stop sliding
                }
                nr += d[0]; nc += d[1];
            }
        }
    }

    void getKingMoves(ArrayList<int[]> moves, int r, int c, int color) {
        // king moves one step in any direction
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) continue;
                int nr = r + dr, nc = c + dc;
                if (isValid(nr, nc) && getColor(board[nr][nc]) != color)
                    moves.add(new int[]{nr, nc});
            }
        }

        // castling
        int backRow = (color == 1) ? 7 : 0;
        boolean canCastleK = (color == 1) ? whiteCanCastleKing  : blackCanCastleKing;
        boolean canCastleQ = (color == 1) ? whiteCanCastleQueen : blackCanCastleQueen;

        if (r == backRow && c == 4) {
            // kingside castling - need empty squares and rook in place
            if (canCastleK && board[backRow][5] == 0 && board[backRow][6] == 0
                    && getType(board[backRow][7]) == 4) {
                moves.add(new int[]{backRow, 6, 10}); // 10 = castle kingside flag
            }
            // queenside castling
            if (canCastleQ && board[backRow][3] == 0 && board[backRow][2] == 0
                    && board[backRow][1] == 0 && getType(board[backRow][0]) == 4) {
                moves.add(new int[]{backRow, 2, 11}); // 11 = castle queenside flag
            }
        }
    }

    // ---- BACKTRACKING ALGORITHM ----
    // This is the main DAA concept - we generate a candidate move,
    // apply it to the board, check if it leaves the king in check,
    // and if yes we reject it (backtrack). This filters out illegal moves.
    ArrayList<int[]> getLegalMoves(int row, int col) {
        ArrayList<int[]> legal = new ArrayList<>();
        int piece = board[row][col];
        if (piece == 0) return legal;

        int color = getColor(piece);
        ArrayList<int[]> candidates = getPseudoMoves(row, col);

        for (int[] move : candidates) {
            // --- Step 1: Try the move (apply it) ---
            int savedTo      = board[move[0]][move[1]];
            int savedFrom    = board[row][col];
            int savedEPCol   = enPassantCol;
            int savedEPRow   = enPassantRow;
            boolean savedCKW = whiteCanCastleKing;
            boolean savedCQW = whiteCanCastleQueen;
            boolean savedCKB = blackCanCastleKing;
            boolean savedCQB = blackCanCastleQueen;
            int epCapturedPiece = 0;

            applyMoveInternal(row, col, move);

            // --- Step 2: Check constraint (is our king safe?) ---
            boolean kingIsSafe = !isKingInCheck(color);

            // --- Step 3: Undo the move (backtrack) ---
            board[row][col]       = savedFrom;
            board[move[0]][move[1]] = savedTo;
            enPassantCol          = savedEPCol;
            enPassantRow          = savedEPRow;
            whiteCanCastleKing    = savedCKW;
            whiteCanCastleQueen   = savedCQW;
            blackCanCastleKing    = savedCKB;
            blackCanCastleQueen   = savedCQB;

            // restore en passant captured pawn
            if (move.length > 2 && move[2] == -1) {
                board[row][move[1]] = color == 1 ? 21 : 11;
            }
            // restore rook for castling undo
            if (move.length > 2 && move[2] == 10) {
                board[move[0]][7] = board[move[0]][5];
                board[move[0]][5] = 0;
            }
            if (move.length > 2 && move[2] == 11) {
                board[move[0]][0] = board[move[0]][3];
                board[move[0]][3] = 0;
            }

            // --- Step 4: If king was safe, the move is legal ---
            if (kingIsSafe) legal.add(move);
        }
        return legal;
    }

    // actually apply a move to the board (used internally for backtracking check)
    void applyMoveInternal(int fromRow, int fromCol, int[] move) {
        int toRow = move[0], toCol = move[1];
        int piece = board[fromRow][fromCol];
        int color = getColor(piece);
        int type  = getType(piece);
        int flag  = (move.length > 2) ? move[2] : 0;

        board[toRow][toCol]     = piece;
        board[fromRow][fromCol] = 0;

        // en passant: remove the captured pawn
        if (flag == -1) {
            board[fromRow][toCol] = 0;
        }

        // promotion: replace pawn with chosen piece
        if (flag >= 2 && flag <= 5) {
            board[toRow][toCol] = color * 10 + flag;
        }

        // castling: also move the rook
        if (flag == 10) { // kingside
            board[toRow][5] = board[toRow][7];
            board[toRow][7] = 0;
        }
        if (flag == 11) { // queenside
            board[toRow][3] = board[toRow][0];
            board[toRow][0] = 0;
        }

        // update castling rights
        if (type == 6) {
            if (color == 1) { whiteCanCastleKing = false; whiteCanCastleQueen = false; }
            else            { blackCanCastleKing = false; blackCanCastleQueen = false; }
        }
        if (type == 4) {
            if (fromRow == 7 && fromCol == 7) whiteCanCastleKing  = false;
            if (fromRow == 7 && fromCol == 0) whiteCanCastleQueen = false;
            if (fromRow == 0 && fromCol == 7) blackCanCastleKing  = false;
            if (fromRow == 0 && fromCol == 0) blackCanCastleQueen = false;
        }

        // set en passant target if pawn double moved
        if (type == 1 && Math.abs(toRow - fromRow) == 2) {
            enPassantCol = fromCol;
            enPassantRow = toRow;
        } else {
            enPassantCol = -1;
            enPassantRow = -1;
        }
    }

    // make a move officially (called from game controller)
    void makeMove(int fromRow, int fromCol, int[] move) {
        lastFromRow = fromRow; lastFromCol = fromCol;
        lastToRow   = move[0]; lastToCol   = move[1];
        applyMoveInternal(fromRow, fromCol, move);
        currentTurn = (currentTurn == 1) ? 2 : 1;
    }

    // check if the given color's king is under attack
    boolean isKingInCheck(int color) {
        // find king position
        int kingRow = -1, kingCol = -1;
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if (board[r][c] == color * 10 + 6) {
                    kingRow = r; kingCol = c;
                }
            }
        }
        if (kingRow == -1) return false; // king not found (shouldn't happen)

        int opp = (color == 1) ? 2 : 1;
        // check if any opponent piece can attack the king's square
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if (board[r][c] != 0 && getColor(board[r][c]) == opp) {
                    for (int[] m : getPseudoMoves(r, c)) {
                        if (m[0] == kingRow && m[1] == kingCol) return true;
                    }
                }
            }
        }
        return false;
    }

    // check if given color has any legal moves at all
    boolean hasAnyLegalMoves(int color) {
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++)
                if (board[r][c] != 0 && getColor(board[r][c]) == color)
                    if (!getLegalMoves(r, c).isEmpty()) return true;
        return false;
    }

    boolean isValid(int r, int c) {
        return r >= 0 && r < 8 && c >= 0 && c < 8;
    }

    // get all legal moves for a given color (used by AI)
    ArrayList<int[]> getAllLegalMoves(int color) {
        ArrayList<int[]> all = new ArrayList<>();
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++)
                if (board[r][c] != 0 && getColor(board[r][c]) == color)
                    for (int[] m : getLegalMoves(r, c))
                        all.add(new int[]{r, c, m[0], m[1], m.length > 2 ? m[2] : 0});
        return all;
    }

    // deep copy the board (needed for AI so it doesn't modify the real board)
    ChessBoard copy() {
        ChessBoard copy = new ChessBoard();
        for (int r = 0; r < 8; r++)
            copy.board[r] = board[r].clone();
        copy.currentTurn       = currentTurn;
        copy.whiteCanCastleKing  = whiteCanCastleKing;
        copy.whiteCanCastleQueen = whiteCanCastleQueen;
        copy.blackCanCastleKing  = blackCanCastleKing;
        copy.blackCanCastleQueen = blackCanCastleQueen;
        copy.enPassantCol = enPassantCol;
        copy.enPassantRow = enPassantRow;
        return copy;
    }
}
