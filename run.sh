#!/bin/bash

# Ensure out directory exists
mkdir -p out

# Compile all Java files into out/
javac -d out -cp "lib/*" src/*.java

# Run the GUI app from out
java -cp "out:lib/*" BookCounterGUI

# Navigate back to the parent directory
cd ..
