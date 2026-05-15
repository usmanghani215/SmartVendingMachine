#!/bin/bash
echo "Compiling Smart Vending Machine..."
javac -encoding UTF-8 *.java
if [ $? -eq 0 ]; then
    echo "Running..."
    java Main
else
    echo "Compilation failed!"
fi
