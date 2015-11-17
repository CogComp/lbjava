# Set Cover problem 

## Description 
This tarball contains the LBJ implementation of the solution to a set cover
problem.  The particular problem comes from the following web page, in which
the problem is formulated as an Integer Linear Program.  In LBJ, we'll write
the constraints in First Order Logic, and they'll be translated into the same
linear inequalities shown on the web page:

http://mat.gsia.cmu.edu/orclass/integer/node8.html

Classes City and Neighborhood are used as the internal representation of our
problem's data.  An instance of class City will become the "head" object of an
inference problem, which means that it contains all the data needed to
represent that inference problem.  Instances of class Neighborhood are the
ones we need to classify.

The DumbLearner and ContainsStation classes are hand written classes that
implement an unconstrained learner.  Normally, we would use LBJ to learn such
a classifier, but there was nothing to learn in this case.  The SetCover.lbj
code then creates a new classifier called containsStationConstrained that
operates similarly to ContainsStation except that it respects the constraints.

SetCoverSolver is a program that takes raw data representing a City as input
and produces the constrained predictions.

=== How to run === 
To run: 
./test.sh 

To clean: 
./clean.sh


Note: You need to have Gurobi, the optimization package installed on your computer. 
You can download it from: www.gurobi.com, and set a global variable in ~/.bashrc, pointing to the Gurobi: 
	export GUROBI_HOME=/shared/austen/khashab2/Gurobi/gurobi603/linux64
	export PATH="${PATH}:${GUROBI_HOME}/bin"
	export LD_LIBRARY_PATH="${LD_LIBRARY_PATH}:${GUROBI_HOME}/lib"
You also need to get the Gurobi license and put it somewhere on your machine. 
	export GRB_LICENSE_FILE=ADDRESS_TO_LICENSE_FILE


=== Files In This Project === 
├── class
├── clean.sh
├── example.txt
├── lbj
├── lib
│   ├── gurobi-6.0.jar
│   ├── java-cup-0.11a.jar
│   ├── LBJava-1.0.3.jar
│   └── liblinear.jar
├── README
├── SetCover.lbj
├── src
│   └── ilp
│       ├── City.java
│       ├── ContainsStation.java
│       ├── DumbLearner.java
│       ├── Neighborhood.java
│       └── SetCoverSolver.java
└── test.sh

