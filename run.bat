@echo off
echo ================================
echo  CheckMate X - Build and Run
echo  Requires: JDK 11 or above
echo ================================

if not exist bin mkdir bin

javac -d bin src\CheckmateX.java src\ChessBoard.java src\ChessAI.java src\GameWindow.java

if %ERRORLEVEL% EQU 0 (
    echo Build successful! Starting game...
    java -cp bin CheckmateX
) else (
    echo Build failed. Check the errors above.
    pause
)
