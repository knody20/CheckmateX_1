#!/bin/bash
echo "================================"
echo " CheckMate X - Build and Run"
echo " Requires: JDK 11 or above"
echo "================================"

mkdir -p bin

find src -name "*.java" > sources.txt
javac -d bin -cp "lib/sqlite-jdbc.jar" @sources.txt

if [ $? -eq 0 ]; then
    echo "Build successful! Starting game..."
    java -cp "bin:lib/sqlite-jdbc.jar" com.checkmatex.main.Main
else
    echo "Build failed. Check errors above."
fi
