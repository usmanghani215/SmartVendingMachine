@echo off
echo Compiling Smart Vending Machine...
javac -encoding UTF-8 *.java
if errorlevel 1 (
  echo Compilation failed!
  pause
) else (
  echo Running...
  java Main
)
