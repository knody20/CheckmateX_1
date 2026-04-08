#!/bin/bash
echo "================================"
echo " CheckMate X - Build and Run"
echo " Requires: JDK 11 or above"
echo "================================"

mkdir -p bin

javac -d bin src/CheckmateX.java src/ChessBoard.java src/GameWindow.java

if [ $? -eq 0 ]; then
    echo "Build successful! Starting game..."
    java -cp bin CheckmateX
else
    echo "Build failed. Check errors above."
fi
