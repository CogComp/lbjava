.PHONY : all check clean

JAVA ?= java
JAVAC ?= javac

all :
	@mkdir -p class lbj
	$(JAVA) -cp $(CLASSPATH):class LBJ2.Main -sourcepath src -gsp lbj -d class SetCover.lbj
	$(JAVAC) -cp $(CLASSPATH):class -sourcepath src:lbj -d class src/ilp/SetCoverSolver.java

check :
	$(JAVA) -cp $(CLASSPATH):class -Xmx1g ilp.SetCoverSolver example.txt

clean :
	rm -rf class/ilp lbj/ilp

