# The LBJ Language 
Now that we have defined the building blocks of classifier computation, we next describe LBJ’s
syntax and semantics for programming with these building blocks.

Like a Java source file, an LBJ source file begins with an optional package declaration and
an optional list of import declarations. Next follow the definitions of classifiers, constraints, and
inferences. Each will be translated by the LBJ compiler into a Java class of the same name. If
the package declaration is present, those Java classes will all become members of that package.
Import declarations perform the same function in an LBJ source file as in a Java source file.

##  Classifiers 

In LBJ, a classifier can be defined with Java code or composed from the definitions of other
classifiers using special operators. As such, the syntax of classifier specification allows the programmer
to treat classifiers as expressions and assign them to names. This section defines the
syntax of classifier specification more precisely, including the syntax of classifiers learned from
data. It also details the behavior of the LBJ compiler when classifiers are specified in terms of
training data and when changes are made to an LBJ source file. 

###  Classifier Declarations 

Classifier declarations are used to name classifier expressions (discussed in Section 4.1.2). The
syntax of a classifier declaration has the following form: 

```
feature-type name (type name )
    [cached | cachedin field-access ] <-
    classifier-expression
```

A classifier declaration names a classifier and specifies its input and output types in its header,
which is similar to a Java method header. It ends with a left arrow indicating assignment and a
classifier expression which is assigned to the named classifier.

The optional `cached` and `cachedin` keywords are used to indicate that the result of this classifier’s
computation will be cached in association with the input object. The `cachedin` keyword 

