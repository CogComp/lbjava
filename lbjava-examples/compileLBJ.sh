#!/bin/bash

generateLBJFiles() {
    if [ $# -eq 0 ];
    then
        echo "Usage: compileLBJ <lbj file>"
    fi

    FILE=$*
    JAVA=java
    JAVAC=javac

    JAVA="nice "$JAVA
    SWITCHES="-ea -XX:MaxPermSize=1g -Xmx8g"

    BIN=target
    LBJBIN=target
    SRC=src/main/java
    GSP=src/main/java

    CP=$BIN:$LBJBIN:lib/*:lib/illinois-core-utilities-3.0.5.jar:lib/liblinear.jar:../lbjava/target/LBJava-1.2.2.jar

    echo $CP

    $JAVA $SWITCHES -cp $CP edu.illinois.cs.cogcomp.lbjava.Main -d $LBJBIN -gsp $GSP -sourcepath $SRC $FILE
}

generateAllLBJFiles() {
  # declare -a files=("BadgesClassifier.lbj" "EntityRelation.lbj" "NewsGroupClassifier.lbj" "SentimentClassifier.lbj" "SetCover.lbj" "SpamClassifier.lbj")

  for i in "BadgesClassifier.lbj" "EntityRelation.lbj" "NewsGroupClassifier.lbj" "SentimentClassifier.lbj" "SetCover.lbj" "SpamClassifier.lbj"
  do
     echo "Generating output for $i"
     generateLBJFiles "src/main/lbj/"$i
  done
}

# running starts here
generateAllLBJFiles