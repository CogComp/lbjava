#!/bin/bash

# generate each .lbj file
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
    LBJBIN=target/classes
    SRC=src/main/java
    GSP=src/main/java

    if [ ! -d $LBJBIN ]; then mkdir -p $LBJBIN; fi
    CP=$BIN:$LBJBIN:lib/*:lib/illinois-core-utilities-3.0.15.jar:lib/liblinear.jar:../lbjava/target/lbjavaCore.jar

    echo $CP

    $JAVA $SWITCHES -cp $CP edu.illinois.cs.cogcomp.lbjava.Main -x -d $LBJBIN -gsp $GSP -sourcepath $SRC $FILE
    $JAVA $SWITCHES -cp $CP edu.illinois.cs.cogcomp.lbjava.Main -c -d $LBJBIN -gsp $GSP -sourcepath $SRC $FILE
}

# generate all .lbj files
generateAllLBJFiles() {
  # declare -a files=("BadgesClassifier.lbj" "EntityRelation.lbj" "NewsGroupClassifier.lbj" "SentimentClassifier.lbj" "SetCover.lbj" "SpamClassifier.lbj")

  for i in "BadgesClassifier.lbj" "EntityRelation.lbj" "NewsGroupClassifier.lbj" "SentimentClassifier.lbj" "SetCover.lbj" "SpamClassifier.lbj" "RegressionClassifier.lbj"
  do
     echo "Generating output for $i"
     generateLBJFiles "src/main/lbj/"$i
  done
}

# running starts here
if [ $# -eq 0 ]; then
	echo "Error: One argument is needed."
	exit
fi

if [ $# -gt 1 ]; then
	echo "Error: Too many arguments."
	exit
fi

case $1 in
	"all")
		echo "===== Generating output for all =====";
		generateAllLBJFiles
		exit;;
	"badges")
		file_name="BadgesClassifier.lbj";;
	"entity")
		file_name="EntityRelation.lbj";;
	"newsgroup")
		file_name="NewsGroupClassifier.lbj";;
	"sentiment")
		file_name="SentimentClassifier.lbj";;
	"setcover")
		file_name="SetCover.lbj";;
	"spam")
		file_name="SpamClassifier.lbj";;
	"regression")
		file_name="RegressionClassifier.lbj";;
	*)
		echo "Invalid argument: "$1
		exit;;
esac

echo "===== Generating output for $1 =====\n"
generateLBJFiles "src/main/lbj/"$file_name