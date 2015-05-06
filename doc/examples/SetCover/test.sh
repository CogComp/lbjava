#!/bin/bash

JAVA=java
JAVAC=javac

$JAVA -cp lib/*:class  edu.illinois.cs.cogcomp.lbjava.Main -sourcepath src -gsp lbj -d class SetCover.lbj
$JAVAC -cp lib/*:class -sourcepath src:lbj -d class src/ilp/SetCoverSolver.java



# run: 
$JAVA -cp lib/*:class -Xmx1g ilp.SetCoverSolver example.txt


