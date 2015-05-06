Learning Based Java (LBJava)

************************************************************  
This project was started by Nicholas Rizzolo (rizzolo@gmail.com).
It was ported to Maven as LBJava (v.1.0) by Christos Christodoulopoulos (christod@illinois.edu)

Licensing
--------
To see the full license for this software, see LICENSE (in doc directory) or visit the download 
page for this software and press 'Download'. The next screen displays the license. 

************************************************************  
Contents of this README file:
     Part 1: Description
     Part 2: How to compile the software.
************************************************************  

************************************************************
       Part 1: Description
************************************************************
Learning Based Java is a modeling language for the rapid development of software systems with 
one or more learned functions, designed for use with the Java programming language. 
LBJava offers a convenient, declarative syntax for classifier and constraint definition directly in 
terms of the objects in the programmer's application. With LBJava, the details of feature 
extraction, learning, model evaluation, and inference are all abstracted away from the programmer, 
leaving him to reason more directly about his application.

************************************************************  
        Part 2: How to compile the software
************************************************************ 
JBJava uses the Gurobi solver for inference and therefore the Gurobi library needs to be installed 
prior to compilation. To download and install Gurobi visit http://www.gurobi.com/

Make sure to include Gurobi in your PATH and LD_LIBRARY variables
export GUROBI_HOME="PATH-TO-GUROBI/linux64"
export PATH="${PATH}:${GUROBI_HOME}/bin"
export LD_LIBRARY_PATH="${LD_LIBRARY_PATH}:${GUROBI_HOME}/lib"


To compile and package the LBJava code simply run:
mvn install

It will create the jar package in target/LBJava-1.0.jar as well as install it in your local Maven repo.

NB: If you need to run mvn clean at any point, make sure to create "target/classes" directory before 
running mvn compile/install since it is required for the java-source compilation process. 