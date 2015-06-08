#!/bin/bash

if [ $# -eq 0 ]; 
then
    echo "Usage: compileLBJ <lbj file>"
fi


FILE=$*
JAVA=java
JAVAC=javac

JAVA="nice "$JAVA
SWITCHES="-ea -XX:MaxPermSize=1g -Xmx8g"

BIN=bin/
LBJBIN=bin
SRC=src
GSP=lbjsrc

CP=$BIN:$LBJBIN:lib/*:lib/liblinear.jar:lib/LBJava-1.0.3.jar

echo $CP

$JAVA $SWITCHES -cp $CP edu.illinois.cs.cogcomp.lbjava.Main -d $LBJBIN -gsp $GSP -sourcepath $SRC $FILE

