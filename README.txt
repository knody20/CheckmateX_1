============================================================
  CheckmateX - Chess Game
  DAA Project (PBL) - B.Tech CSE 2nd Year
============================================================
Team ID: DAA-IV-T046

Team Members:
  1. Prashant Shah         
  2. Anchal jadora         
  3. Janmejai Pratap Tonk  
  4. Abhishek Singh

Subject: Design and Analysis of Algorithms (DAA)
Mentor:  Anamika Sharma

------------------------------------------------------------
ABOUT THE PROJECT
------------------------------------------------------------

CheckMate X is a chess game we built to demonstrate DAA
concepts like Backtracking and Minimax with Alpha-Beta
Pruning. The game has a working AI opponent and follows all
standard chess rules.

We chose chess because:
- It uses backtracking naturally (testing moves and undoing)
- Minimax is the classic algorithm for 2-player games
- The search space is huge (10^120 possible games) which
  makes pruning very important

------------------------------------------------------------
HOW TO RUN
------------------------------------------------------------

Windows:
  Double-click run.bat
  (or) Open CMD in this folder and type: run.bat

Linux / Mac:
  chmod +x run.sh
  ./run.sh

Manual:
  mkdir bin
  javac -d bin src/*.java
  java -cp bin CheckmateX

Requirements: Java JDK 11 or higher (no extra libraries needed)

------------------------------------------------------------
FILES EXPLAINED
------------------------------------------------------------

src/CheckmateX.java  -> main() method, launches the window
src/ChessBoard.java  -> all chess logic (board, moves, rules)
src/ChessAI.java     -> AI player (Minimax + Alpha-Beta)
src/GameWindow.java  -> the Swing UI (board drawing, clicks)

------------------------------------------------------------
DAA ALGORITHMS IMPLEMENTED
------------------------------------------------------------

1. BACKTRACKING - Legal Move Filtering
   File: ChessBoard.java -> getLegalMoves() method

   When a user clicks a piece, we first generate all
   "candidate" moves (geometrically valid). Then for each:
     - Apply the move to the board
     - Check if the king is in check
     - If yes: reject the move (backtrack - undo it)
     - If no: add to legal moves list

   This is backtracking because we try a move, test a
   constraint, and undo it if the constraint is violated.
   It filters out all moves that would leave the king in
   check.

2. MINIMAX ALGORITHM
   File: ChessAI.java -> minimax() method

   The AI builds a game tree:
   - At each level, it tries every possible move
   - White tries to maximize the board score
   - Black tries to minimize the board score
   - The AI picks the move that leads to the best outcome

   We implemented depths 1, 2, and 3 (Easy/Medium/Hard)
   At depth 3: up to 30^3 = 27000 positions are searched

3. ALPHA-BETA PRUNING
   File: ChessAI.java -> minimax() method (same function)

   This is an optimization of Minimax.
   - alpha = best score White can guarantee
   - beta  = best score Black can guarantee
   - If beta <= alpha: stop searching this branch

   Why it works: if we already found a path that's better,
   the opponent will never let us reach a worse branch, so
   we can skip it entirely.

   It reduces the number of nodes from O(b^d) to roughly
   O(b^(d/2)) in the best case. For depth 3 that goes from
   ~27000 nodes to ~164 nodes.

------------------------------------------------------------
HOW WE BUILT IT (Implementation Order)
------------------------------------------------------------

We built it step by step, testing each part before moving on.
See the full description in the submission document.

Step 1: Drew the chess board using Swing (just colors)
Step 2: Placed pieces using unicode symbols
Step 3: Click to select a piece (highlight selected square)
Step 4: Added basic movement rules for each piece type
Step 5: Showed legal move dots on screen (green hints)
Step 6: Added check detection
Step 7: Implemented backtracking to filter illegal moves
Step 8: Added checkmate and stalemate detection
Step 9: At this point 2-player game was working
Step 10: Added board evaluation function (material + position)
Step 11: Implemented Minimax for the AI
Step 12: Added Alpha-Beta Pruning to make AI faster
Step 13: Added captured pieces display, move history, UI polish

------------------------------------------------------------
FEATURES
------------------------------------------------------------

- Full chess rules (castling, en passant, pawn promotion)
- Legal move highlighting (green dots)
- Check highlighting (red king background)
- Last move highlighting
- AI opponent with 3 difficulty levels
- 2-player local mode
- Move history panel
- Captured pieces display
- Checkmate and stalemate detection
- New game option with mode selection

------------------------------------------------------------
THINGS WE WANTED TO ADD BUT COULDN'T
------------------------------------------------------------

- Undo move button (we didn't have time to implement it)
- Move timer / clock
- Save/load game
- Better AI with opening book

------------------------------------------------------------
REFERENCES
------------------------------------------------------------

- Introduction to Algorithms (CLRS) - Backtracking chapter
- Chess Programming Wiki: chessprogramming.org
- GeeksForGeeks Minimax article
- Java Swing tutorial (Oracle docs)

------------------------------------------------------------
