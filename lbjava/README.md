# Learning Based Java (LBJava)

Learning Based Java is a modeling language for the rapid development of software systems with 
one or more learned functions, designed for use with the Java programming language. 
LBJava offers a convenient, declarative syntax for classifier and constraint definition directly in 
terms of the objects in the programmer's application. With LBJava, the details of feature 
extraction, learning, model evaluation, and inference are all abstracted away from the programmer, 
leaving him to reason more directly about his application.

# Tutorial 
Visit each link for its content 
 1. [Introduction](doc/INTRO.md) 
 2. [Basics and definitions](doc/DEFINITIONS.md)
 3. [A working example: classifiying newsgroup documents into topics](doc/20NEWSGROUP.md)
 4. [Syntax of LBJava](doc/LBJLANGUAGE.md)
 5. [LBJava library](doc/LBJLIBRARY.md)
 6. [Installation and Commandline options](doc/INSTALLATION.md)
 7. [A working example: regression](doc/REGRESSION.md)
 8. [Learning Algorithms](doc/ALGORITHMS.md)
 
**Note**

LBJava uses the Gurobi solver for inference and therefore the Gurobi library needs to be installed 
prior to compilation. To download and install Gurobi visit [http://www.gurobi.com/](http://www.gurobi.com/)

Make sure to include Gurobi in your PATH and LD_LIBRARY variables
```
    export GUROBI_HOME="PATH-TO-GUROBI/linux64"
    export PATH="${PATH}:${GUROBI_HOME}/bin"
    export LD_LIBRARY_PATH="${LD_LIBRARY_PATH}:${GUROBI_HOME}/lib"
```
To compile and package the LBJava code simply run:

    mvn install

It will create the jar package in target/LBJava-1.0.jar as well as install it in your local Maven repo.

NB: If you need to run `mvn clean` at any point, make sure to create `target/classes` directory before 
running `mvn compile/install` since it is required for the java-source compilation process.

## Credits 
This project was started by [Nicholas Rizzolo](mailto:rizzolo@gmail.com).
It was ported to Maven as LBJava (v.1.0) by [Christos Christodoulopoulos](mailto:christod@illinois.edu). 
