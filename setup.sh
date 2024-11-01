#!/bin/bash

if [ "$#" -ne 1 ]; then
	echo "ERROR" >&2
	echo "One parameter is needed" >&2
	echo "c: only compile" >&2
	echo "r: run" >&2
	echo "cr: compile and run" >&2
	exit 1
fi

# compile

if [ "$1" == "c" ]; then
	javac ./program/Edge.java ./program/FPAnalyzer.java ./program/Node.java ./program/TypeOfFunction.java -d ./binaries/
	exit 0

elif [ "$1" == "r" ]; then
	cd ./binaries/
	java program.FPAnalyzer
	exit 0

elif [ "$1" == "cr" ]; then
	javac ./program/Edge.java ./program/FPAnalyzer.java ./program/Node.java ./program/TypeOfFunction.java -d ./binaries/
	cd ./binaries/
	java program.FPAnalyzer
	exit 0

else
	echo "Something is wrong with the arguments" >&2
	exit 2
fi
