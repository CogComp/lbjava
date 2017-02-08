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
 6. [A working example: regression](doc/REGRESSION.md)
 7. [Learning Algorithms](doc/ALGORITHMS.md)
 
## Using it as a dependency  
To include LBJava in your Maven project, add the following snippet with the
   `#version` entry replaced with the version listed in this project's pom.xml file. 
   Note that you also add to need the
   `<repository>` element for the CogComp maven repository in the `<repositories>` element.
    
```xml 
    <dependencies>
         ...
        <dependency>
            <groupId>edu.cs.cogcomp</groupId>
            <artifactId>LBJava</artifactId>
            <version>#version#</version>
        </dependency>
        ...
    </dependencies>
    ...
    <repositories>
        <repository>
            <id>CogcompSoftware</id>
            <name>CogcompSoftware</name>
            <url>http://cogcomp.cs.illinois.edu/m2repo/</url>
        </repository>
    </repositories>
```

## Compiling LBJava code in your projects
To compile the `.lbj` files into Java code you will need to use the [LBJava maven plugin](../lbjava-mvn-plugin/README.md).
Briefly, you need to add the `<pluginRepositories>` and `<build>` snippets into your pom.xml file:
```xml 
<pluginRepositories>
    <pluginRepository>
        <id>CogcompSoftware</id>
        <name>CogcompSoftware</name>
        <url>http://cogcomp.cs.illinois.edu/m2repo/</url>
    </pluginRepository>
</pluginRepositories>
...
<build>
 <plugins>
   <plugin>
     <groupId>edu.cs.cogcomp</groupId>
     <artifactId>lbjava-maven-plugin</artifactId>
     <version>LBJAVA-VERSION</version>
     <configuration>
       <lbjavaInputFileList>
         <param>lbjava/MyClassifier.lbj</param>
       </lbjavaInputFileList>
     </configuration>
   </plugin>
 </plugins>
</build>
```
 
## Compiling the LBJava-core code 
To compile and package the LBJava code simply run:

    mvn install

NB: If you need to run `mvn clean` at any point, make sure to create `target/classes` directory before 
running `mvn compile/install` since it is required for the java-source compilation process.

## Using ILP inference 
LBJava uses the the solvers included in [illinois-inference](https://github.com/IllinoisCogComp/illinois-cogcomp-nlp/tree/master/inference) for 
inference. We refer the interested reader to the aforementioned library, 
for more details and instructions on how to install these libraries. 

## Credits 
This project was started by [Nicholas Rizzolo](mailto:rizzolo@gmail.com).
It was ported to Maven as LBJava (v.1.0) by [Christos Christodoulopoulos](mailto:christod@illinois.edu). 
