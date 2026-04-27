@echo off
echo ================================
echo  CheckMate X - Build and Run
echo  Requires: JDK 11 or above
echo ================================

if not exist bin mkdir bin

dir /s /B src\*.java > sources.txt
javac -d bin -cp "lib\sqlite-jdbc.jar" @sources.txt

if %ERRORLEVEL% EQU 0 (
    echo Build successful! Starting game...
    java -cp "bin;lib\sqlite-jdbc.jar" com.checkmatex.main.Main
) else (
    echo Build failed. Check the errors above.
    pause
)
