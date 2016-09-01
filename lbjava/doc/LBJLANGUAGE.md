---
title: LBJLANGUAGE
---

# 4 The LBJ Language
Now that we have defined the building blocks of classifier computation, we next describe LBJ’s
syntax and semantics for programming with these building blocks.

Like a Java source file, an LBJ source file begins with an optional package declaration and
an optional list of import declarations. Next follow the definitions of classifiers, constraints, and
inferences. Each will be translated by the LBJ compiler into a Java class of the same name. If
the package declaration is present, those Java classes will all become members of that package.
Import declarations perform the same function in an LBJ source file as in a Java source file.

## 4.1 Classifiers 

In LBJ, a classifier can be defined with Java code or composed from the definitions of other
classifiers using special operators. As such, the syntax of classifier specification allows the programmer
to treat classifiers as expressions and assign them to names. This section defines the
syntax of classifier specification more precisely, including the syntax of classifiers learned from
data. It also details the behavior of the LBJ compiler when classifiers are specified in terms of
training data and when changes are made to an LBJ source file. 

### 4.1.1 Classifier Declarations 

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
instructs the classifier to cache its output in the specified field of the input object. For example,
if the parameter of the classifier is specified as `Word w`, then `w.partOfSpeech` may appear as
the `field-access`. The `cached` keyword instructs the classifier to store the output values it
computes in a hash table. As such, the implementations of the `hashCode()` and `equals(Object)`
methods in the input type’s class play an important role in the behavior of a cached classifier.
If two input objects return the same value from their `hashCode()` methods and are equivalent
according to `equals(Object)`, they will receive the same classification from this classifier. 
(Because this type of classifier caching is implemented with a java.util.WeakHashMap, it is possible for this
statement to be violated if the two objects are not alive in the heap simultaneously. For more information, see the
Java API javadoc)

A cached classifier (using either type of caching) will first check the specified appropriate
location to see if a value has already been computed for the given input object. If it has, it is
simply returned. Otherwise, the classifier computes the value and stores it in the location before
returning it. A discrete classifier will store its value as a `String`. When caching in a field, it
will assume that `null` represents the absence of a computed value. A real classifier will store
its value as a double. When caching in a field, it will assume that `Double.NaN` represents the
absence of a computed value. Array returning classifiers will store a value as an array of the
appropriate type and assume that `null` represents the absence of a computed value. Generators
may not be cached with either keyword. Last but not least, learning classifiers cached with either
keyword will not load their (potentially large) internal representations from disk until necessary
(i.e., until an object is encountered whose cache location is not filled in). See Section 4.1.2.6 for
more information about learning classifiers. 


Semantically, every named classifier is a static method. In an LBJ source file, references to
classifiers are manipulated and passed to other syntactic constructs, similarly to a functional
programming language. The LBJ compiler implements this behavior by storing a classifier’s definition
in a static method of a Java class of the same name and providing access to that method
through objects of that class. As we will see, learning classifiers are capable of modifying their
definition, and by the semantics of classifier declarations, these modifications are local to the
currently executing process, but not to any particular object. In other words, when the application
continues to train a learning classifier on-line, the changes are immediately visible through
every object of the classifier’s class. 

Figure 4.1 gives several examples of classifier declarations. These examples illustrate some
key principles LBJ. First, the features produced by a classifier are either discrete or real. If a
feature is discrete, the set of allowable values may optionally be specified, contained in curly
braces. Any literal values including `int`s, `String`s, and `boolean`s may be used in this set.  
(Internally, they’ll all be converted to `String`s.)

```java 
discrete{false, true} highRisk(Patient p) <- {
    return p.historyOfCancer() && p.isSmoker();
}

discrete prefix(Word w) cachedin w.prefix <- {
    if (w.spelling > 5) return w.spelling.substring(0, 3);
    return w.spelling;
}

real[] dimensions(Cube c) <- {
    sense c.length;
    sense c.width;
    sense c.height;
}

discrete bigram(Word w) <- spellingTarget && spellingOneAfter
```

Next, every classifier takes exactly one object as input and returns one or more features
as output. The input object will most commonly be an object from the programmer-designed,
object-oriented internal representation of the application domain’s data. When the classifier has
made its classification(s), it returns one or more features representing those decisions. A complete
list of feature return types follows:
 
 - `discrete`
 - `discrete{ value-list }`
 - `real`
 - `discrete[]`
 - `discrete{ value-list }[]`
 - `real[]`
 - `discrete%`
 - `discrete{ value-list }%`
 - `real%`
 - `mixed%`

Feature return types ending with square brackets indicate that an array of features is produced
by this classifier. The user can expect the feature at a given index of the array to be the
same feature with a differing value each time the classifier is called on a different input object.
Feature return types ending with a percent sign indicate that this classifier is a feature generator.
A feature generator may return zero or more features in any order when it is called, and there
is no guarantee that the same features will be produced when called on different input objects.
Finally, the `mixed%` feature return type indicates that the classifier is a generator of both discrete
and real features.

```java 
discrete learnMe(InputObject o) <-
    learn labelingClassifier
    using c1, c2, c3
    from new UserDefinedParser(data)
    with new PreDefinedLearner(parameters)
end
```

As illustrated by the fourth classifier in Figure 4.1, a classifier may be composed from other
classifiers with classifier operators. The names `spellingTarget` and `spellingOneAfter` here
refer to `classifiers` that were either defined elsewhere in the same source file or that were imported
from some other package. In this case, the classifier `bigram` will return a discrete feature
whose value is the conjunction of the values of the features produced by `spellingTarget` and
`spellingOneAfter`.

All of the classifiers in Figure 4.1 are examples of explicitly coded classifiers. The first three
use Java method bodies to compute the values of the returned features. In each of these cases,
the names of the returned features are known directly from the classifier declaration’s header,
so their values are all that is left to compute. In the first two examples, the header indicates
that a single feature will be returned. Thus, the familiar `return` statement is used to indicate
the feature’s value. In the third example, the square brackets in the header indicate that this
classifier produces an array of features. Return statements are disallowed in this context since we
must return multiple values. Instead, the `sense` statement is used whenever the next feature’s
value is computed.


For our final example, we will demonstrate the specification of a learning classifier. The
`learnMe` learning classifier in Figure 4.2 is supervised by a classifier named `labelingClassifier`
whose features will be interpreted as labels of an input training object. Next, after the `using`
clause appears a comma separated list of classifier names. These classifiers perform feature
extraction. The optional `from` clause designates a parser used to provide training objects to
learnMe at compile-time. Finally, the optional `with` clause designates a particular learner for
`learnMe` to utilize.

### 4.1.2 Classifier Expressions 

As was alluded to above, the right hand side of a classifier declaration is actually a single classifier
expression. A classifier expression is one of the following syntactic constructs:

 - a classifier name
 - a method body (i.e., a list of Java statements in between curly braces)
 - a classifier cast expression
 - a conjunction of two classifier expressions
 - a comma separated list of classifier expressions
 - a learning classifier expression
 - an inference invocation expression

We have already explored examples of almost all of these. More precise definitions of each follow.

### 4.1.2.1 Classifier Names 

The name of a classifier defined either externally or in the same source file may appear wherever
a classifier expression is expected. If the named classifier’s declaration is found in the same
source file, it may occur anywhere in that source file (in other words, a classifier need not be
defined before it is used). If the named classifier has an external declaration it must either be
fully qualified (e.g., `myPackage.myClassifier`) or it must be imported by an import declaration
at the top of the source file. The class file or Java source file containing the implementation
of an imported classifier must exist prior to running the LBJ compiler on the source file that
imports it. 

### 4.1.2.2 Method Bodies 

A method body is a list of Java statements enclosed in curly braces explicitly implementing a
classifier. When the classifier implemented by the method body returns a single feature, the
`return` statement is used to provide that feature’s value. If the feature `return` type is `real`,
then the `return` statement’s expression must evaluate to a double. Otherwise, it can evaluate to
anything - even an object - and the resulting value will be converted to a `String`. Each method
body takes its argument and feature `return` type from the header of the classifier declaration it
is contained in (except when in the presence of a classifier cast expression, discussed in Section
4.1.2.3). For more information on method bodies in LBJ, see Section 4.1.3.

#### 4.1.2.3 Classifier Cast Expressions 

When the programmer wishes for a classifier sub-expression on the right hand side of a classifier
declaration to be implemented with a feature return type differing from that defined in the
header, a classifier cast expression is the solution. For example, the following classifier declaration
exhibits a learning classifier (see Section 4.1.2.6) with a real valued feature return type. One of
the classifiers it uses as a feature extractor is hard-coded on the fly, but it returns a discrete
feature. A classifier cast expression is employed to achieve the desired affect.

```java 
real dummyClassifier(InputObject o) <-
learn labeler
    using c1, c2, (discrete) { return o.value == 4; }
end
```

Of course, we can see that the hard-coded classifier defined on the fly in this example returns
a discrete (`boolean`) value. Without the cast in front of this method body, the LBJ compiler
would have assumed it to have a real valued feature return type, and an error would have been produced. 

When a classifier cast expression is applied to a classifier expression that contains other
classifier expressions, the cast propagates down to those classifier expressions recursively as well.


#### 4.1.2.4 Conjunctions

A conjunction is written with the double ampersand operator (`&&`) in between two classifier
expressions (see Figure 4.1 for an example). The conjunction of two classifiers results in a new
classifier that combines the values of the features returned by its argument classifiers. The nature
of the combination depends on the feature return types of the argument classifiers. Table bellow
enumerates all possibilities and gives the feature return type of the resulting conjunctive classifier.


<img width="458" alt="screen shot 2015-11-17 at 2 34 16 pm" src="https://cloud.githubusercontent.com/assets/2441454/11223998/69e9b662-8d38-11e5-9a71-645d362ec27f.png">

In general, the following rules apply. Two discrete features are combined simply through
concatenation of their values. One discrete and one real feature are combined by creating a new
real valued feature whose name is a function of the discrete feature’s value and whose value is
equal to the real feature’s value. When two real features are combined, their values are multiplied.

The conjunction of two classifiers that return a single feature is a classifier returning a
single feature. When a classifier returning an array is conjuncted with a either a single feature
classifier or a classifier returning an array, the result is an array classifier whose returned array
will contain the combinations of every pairing of features from the two argument classifiers.
Finally, the conjunction of a feature generator with any other classifier will result in a feature
generator producing features representing the combination of every pairing of features from the
two argument classifiers.

#### 4.1.2.5 Composite Generators 

“Composite generator” is LBJ terminology for a comma separated list of classifier expressions.
When classifier expressions are listed separated by commas, the result is a feature generator that
simply returns all the features returned by each classifier in the list.

#### 4.1.2.6 Learning Classifier Expressions 

Learning classifier expressions have the following syntax:

```
learn [classifier-expression ]                  // Labeler
    using classifier-expression                 // Feature extractors
    [from instance-creation-expression [int ]]  // Parser
    [with instance-creation-expression ]        // Learning algorithm
    [evaluate Java-expression ]                 // Alternate eval method
    [cval [int ] split-strategy ]               // K-Fold Cross Validation
        [alpha double ]                         // Confidence Parameter
            [testingMetric
            instance-creation-expression ]]     // Testing Function
    [preExtract boolean ]                       // Feature Pre-Extraction
    [progressOutput int ]                       // Progress Output Frequency
```

The first classifier expression represents a classifier that will provide label features for a supervised
learning algorithm. It need not appear when the learning algorithm is unsupervised. The
classifier expression in the `using` clause does all the feature extraction on each object, during
both training and evaluation. It will often be a composite generator.

The instance creation expression in the `from` clause should create an object of a class that
implements the `LBJ2.parser.Parser` interface in the library (see Section 5.4.1). This clause
is optional. If it appears, the LBJ compiler will automatically perform training on the learner
represented by this learning classifier expression at compile-time. Whether it appears or not, the
programmer may continue training the learner on-line in the application via methods defined in
`LBJ2.learn.Learner` in the library (see Section 5.2.1).

When the `from` clause appears, the LBJ compiler retrieves objects from the specified parser
until it finally returns `null`. One at a time, the feature extraction classifier is applied to each
object, and the results are sent to the learning algorithm for processing. However, many learning
algorithms perform much better after being given multiple opportunities to learn from each
training object. This is the motivation for the integer addendum to this clause. The integer
specifies a number of rounds, or the number of passes over the training data to be performed by
the classifier during training.

The instance creation expression in the `with` clause should create an object of a class derived
from the `LBJ2.learn.Learner` class in the library. This clause is also optional. If it appears, the
generated Java class implementing this learning classifier will be derived from the class named
in the `with` clause. Otherwise, the default learner for the declared return type of this learning
classifier will be substituted with default parameter settings.

The `evaluate` clause is used to specify an alternate method for evaluation of the learned
classifier. For example, the `SparseNetworkLearner` learner is a multi-class learner that, during
evaluation, predicts the label for which it computes the highest score. However, it also provides
the `valueOf(Object, java.util.Collection)` method which restricts the prediction to one of
the labels in the specified collection. In the application, it’s easy enough to call this method in
place of `discreteValue(Object)` (discussed in Section 5.1.1), but when this classifier is invoked
elsewhere in an LBJ source file, it translates to an invocation of `discreteValue(Object)`. The
evaluate clause (e.g., `evaluate valueOf(o, MyClass.getCollection())`) changes the behavior
of `discreteValue(Object)` (or `realValue(Object)` as appropriate) so that it uses the specified
Java-expression to produce the prediction. Note that `Java-expression` will be used only
during the evaluation and not the training of the learner specifying the `evaluate` clause.

The cval clause enables LBJ’s built-in K-fold cross validation system. K-fold cross validation
is a statistical technique for assessing the performance of a learned classifier by partitioning the
user’s set of training data into K subsets such that a single subset is held aside for testing while
the others are used for training. LBJ automates this process in order to alleviate the need for
the user to perform his own testing methodologies. The optional `split-strategy` argument to
the cval clause can be used to specify the method with which LBJ will split the data set into
subsets (folds). If the `split-strategy` argument is not provided, the default value taken is
`sequential`. The user may choose from the following four split strategies:

 - `sequential`- The `sequential` split strategy attempts to partition the set of examples
 into K equally sized subsets based on the order in which they are returned from the
 user’s parser. Given that there are `T` examples in the data set, the first `T / K` examples
 encountered are considered to be the first subset, while the examples between the `(T /K +
 1)`’th example and the `(2T /K)`’th example are considered to be the second subset, and so
 on.
 i.e. `[ — 1 — | — 2 — | ... | — K — ]`

 - `kth` - The `kth` split strategy also attempts to partition the set of examples in to `K` equally
   sized subsets with a round-robin style assignement scheme. The `x`’th example encountered 
    is assigned to the `(x%K)`’th subset.
    i.e. `[ 1 2 3 4 ... K 1 2 3 4 ... K ... ]`
    
 - `random` - The `random` split strategy begins with the assignment given by the `kth` split
   strategy, and simply mixes the subset assignments. This ensures that the subsets produced
   are as equally sized as possible.
      
 - `manual` - The user may write their parser so that it returns the unique instance of the
   `LBJ2.parse.FoldSeparator` class (see the `separator` field) wherever a fold boundary is
   desired. Each time this object appears, it represents a partition between two folds. Thus,
   if the k-fold cross validation is desired, it should appear k − 1 times. The integer provided
   after the `cval` keyword is ignored and may be omitted in this case. 

The `testingMetric` and `alpha` clauses are sub-clauses of `cval`, and, consequently, have no
effect when the `cval` clause is not present. The `testingMetric` clause gives the user the opportunity
to provide a custom testing methodology. The object provided to the `testingMetric`
clause must implement the `LBJ2.learn.TestingMetric` interface. If this clause is not provided,
then it will default to the `LBJ2.learn.Accuracy` metric, which simply returns the ratio of correct
predictions made by the classifier on the testing fold to the total number of examples contained
within said fold.

LBJ’s cross validation system provides a confidence interval according to the measurements
made by the testing function. With the `alpha` clause, the user may define the width of this
confidence interval. The double-precision argument provided to the alpha clause causes LBJ to
calculate a (1 − a)% confidence interval. For example, `alpha .07` causes LBJ to print a 93%
confidence interval, according to the testing measurements made. If this clause is not provided,
the default value taken is .05, resulting in a 95% confidence interval.

The `preExtract` clause enables or disables the pre-extraction of features from examples.
When the argument to this clause is `true`, feature extraction is only performed once, the results
of which are recorded in two files. First, an array of all `Feature` objects (see Section 5.1.2) observed
during training is serialized and written to a file whose name is the same as the learning
classifier’s and whose extension is `.lex`. This file is referred to as the lexicon. Second, the integer
indexes, as they are found in this array, of all features corresponding to each training object are
written to a file whose name is the same as the learning classifier’s and whose extension is `.ex`.
This file is referred to as the example file. It is re-read from disk during each training round
during both cross validation and final training, saving time when feature extraction is expensive,
which is often the case.
If this clause is not provided, the default value taken is `false`.

The `progressOutput` clause defines how often to produce an output message during training.
The argument to this clause is an integer which represents the number of examples to process
between progress messages. This variable can also be set via a command line parameter using the
`-t` option. If a value is provided in both places, the one defined here in the Learning Classifier
Expression takes precedence. If no value is provided, then the default value taken is 0, causing
progress messages to be given only at the beginning and end of each training pass.

When the LBJ compiler finally processes a learning classifier expression, it generates not
only a Java source file implementing the classifier, but also a file containing the results of the
computations done during training. This file will have the same name as the classifier but with
a `.lc` extension (“`lc`” stands for “learning classifier”). The directory in which this file and also
the lexicon and example files mentioned earlier are written depends on the appearance of certain
command line parameters discussed in Section 6.2.

#### 4.1.2.7 Inference Invocations 
    
Inference is the process through which classifiers constrained in terms of each other reconcile
their outputs. More information on the specification of constraints and inference procedures can
be found in Sections 4.2 and 4.3 respectively. In LBJ, the application of an inference to a learning
classifier participating in that inference results in a new classifier whose output respects the
inference’s constraints. Inferences are applied to learning classifiers via the inference invocation,
which looks just like a method invocation with a single argument.
    
For example, assume that `LocalChunkType` is the name of a discrete learning classifier involved
in an inference procedure named `ChunkInference`. Then a new version of `LocalChunkType`
that respects the constraints of the inference may be named as follows:

```
discrete ChunkType(Chunk c) <- ChunkInference(LocalChunkType)
```

### 4.1.3 Method Bodies 
    
Depending on the feature `return` type, the programmer will have differing needs when designing
a method body. If the feature `return` type is either `discrete` or `real`, then only the value of
the single feature is returned through straight forward use of the return statement. Otherwise,
another mechanism will be required to return multiple feature values in the case of an array
`return` type, or multiple feature names and values in the case of a feature generator. That
mechanism is the `sense` statement, described in Section 4.1.3.1.
    
When a classifier’s only purpose is to provide information to a `Learner` (see Section 5.2.1),
the `Feature` data type (see Section 5.1.2) is the most appropriate mode of communication.
However, in any LBJ source file, the programmer will inevitably design one or more classifiers
intended to provide information within the programmer’s own code, either in the application or
in other classifier method bodies. In these situations, the features’ values (and not their names)
are the data of interest. Section 4.1.3.2 discusses a special semantics for classifier invocation.    
    
#### 4.1.3.1 The `sense` Statement    

 The `sense` statement is used to indicate that the name and/or value of a feature has been
 detected when computing an array of features or a feature generator. In these contexts, any
 number of features may be sensed, and they are returned in the order in which they were sensed.
 
 The syntax of a `sense` statement in an array returning classifier is simply

```
 sense expression ;
```
 
The expression is interpreted as the value of the next feature sensed. No name need be supplied,
as the feature’s name is simply the concatenation of the classifier’s name with the index this
feature will take in the array. This expression must evaluate to a `double` if the method body’s
feature return type is `real[]`. Otherwise, it can evaluate to anything - even an object - and the
resulting value will be converted to a `String`.
 
 The syntax of a `sense` statement in a feature generator is
 
 ```
 sense expression : expression ;
 ```
 
 The first expression may evaluate to anything. Its `String` value will be appended to the name
 of the method body to create the name of the feature. The second expression will be interpreted
 as that feature’s value. It must evaluate to a `double` if the method body’s feature return type
 is `real%`. Otherwise, it can evaluate to anything and the resulting value will be converted to a
 `String`.
 
 The single expression form of the `sense` statement may also appear in a feature generator
 method body. In this case, the expression represents the feature’s name, and that feature is
 assumed to be Boolean with a value of `true`.
 
#### 4.1.3.2 Invoking Classifiers  
 Under the right circumstances, any classifier may be invoked inside an LBJ method body just as
 if it were a method. The syntax of a classifier invocation is simply `name (object )`, where `object`
 is the object to be classified and `name` follows the same rules as when a classifier is named in
 a classifier expression (see Section 4.1.2.1). In general, the semantics of such an invocation are
 such that the value(s) and not the names of the produced features are returned at the call site.
 
 More specifically:
 
 - A classifier defined to return exactly one feature may be invoked anywhere within a method
 body. If it has feature return type `discrete`, a `String` will be returned at the call site.
 Otherwise, a `double` will be returned.
 - Classifiers defined to return an array of features may also be invoked anywhere within
 a method body. Usually, they will return either `String[]` or `double[]` at the call site
 when the classifier has feature return type `discrete[]` or `real[]` respectively. The only
 exception to this rule is discussed next.
 - When a `sense` statement appears in a method body defined to return an array of features,
 the lone argument to that `sense` statement may be an invocation of another array returning
 classifier of the same feature return type. In this case, all of the features returned by the
 invoked classifier are returned by the invoking classifier, renamed to take the invoking
 classifier’s name and indexes.
 - Feature generators may only be invoked when that invocation is the entire expression on
 the right of the colon in a sense statement contained in another feature generator of the
 same feature return type 
 (Any feature generator may be invoked in this context in a classifier whose feature return type is mixed)
 In this case, this single `sense` statement will return every
 feature produced by the invoked generator with the following modification. The name of
 the containing feature generator and the `String` value of the expression on the left of
 the colon are prepended to every feature’s name. Thus, an entire set of features can be
 translated to describe a different context with a single `sense` statement.
 
#### 4.1.3.3 Syntax Limitations 
 
 When the exact computation is known, LBJ intends to allow the programmer to explicitly
 define a classifier using arbitrary Java. However, the current version of LBJ suffers from one
 major limitation. All J2SE 1.4.2 statement and expression syntax is accepted, excluding class
 and interface definitions. In particular, this means that anonymous classes currently cannot be
 defined or instantiated inside an LBJ method body. 
 
## 4.2 Constraints 
Many modern applications involve the repeated application of one or more learning classifiers in
a coordinated decision making process. Often, the nature of this decision making process restricts
the output of each learning classifier on a call by call basis to make all these outputs coherent
with respect to each other. For example, a classification task may involve classifying some set of
objects, at most one of which is allowed to take a given label. If the learned classifier is left to its
own devices, there is no guarantee that this constraint will be respected. Using LBJ’s constraint
and inference syntax, constraints such as these are resolved automatically in a principled manner.

More specifically, Integer Linear Programming (ILP) is applied to resolve the constraints
such that the expected number of correct predictions made by each learning classifier involved is
maximized. The details of how ILP works are beyond the scope of this user’s manual. See
(Punyakanok, Roth, & Yih , 2008) for more details.

This section covers the syntax and semantics of constraint declarations and statements. However,
simply declaring an LBJ constraint has no effect on the classifiers involved. Section 4.3
introduces the syntax and semantics of LBJ inference procedures, which can then be invoked (as
described in Section 4.1.2.7) to produce new classifiers that respect the constraints.

### 4.2.1 Constraint Statements 

LBJ constraints are written as arbitrary first order Boolean logic expressions in terms of learning
classifiers and the objects in a Java application. The LBJ constraint statement syntax is
parameterized by Java expressions, so that general constraints may be expressed in terms of the
objects of an internal representation whose exact shape is not known until run-time. The usual
operators and quantifiers are provided, as well as the `atleast` and `atmost` quantifiers, which are
described below. The only two predicates in the constraint syntax are equality and inequality
(meaning string comparison), however their arguments may be arbitrary Java expressions (which
will be converted to strings).

Each declarative constraint statement contains a single constraint expression and ends in a
semicolon. Constraint expressions take one of the following forms:

 - An equality predicate `Java-expression :: Java-expression`
 - An inequality predicate `Java-expression !: Java-expression`
 - A constraint invocation `@name (Java-expression )`
    where the expression must evaluate to an object and `name` follows similar rules as 
    classifier names when they are invoked. In particular, if `MyConstraint` is already declared in
    `SomeOtherPackage`, it may be invoked with `@SomeOtherPackage.MyConstraint(object)`.
 - The negation of an LBJ constraint `!constraint`
 - The conjunction of two LBJ constraints `constraint /\ constraint`
 - The disjunction of two LBJ constraints `constraint \/ constraint`
 - An implication `constraint => constraint`
 - The equivalence of two LBJ constraints `constraint <=> constraint`
 - A universal quantifier `forall (type name in Java-expression )` constraint
    where the expression must evaluate to a Java Collection containing objects of the specified
    type, and the constraint may be written in terms of name .
 - An existential quantifier `exists (type name in Java-expression )` constraint
 - An “at least” quantifier `atleast Java-expression of (type name in Java-expression )` constraint
    where the first expression must evaluate to an an `int`, and the other parameters play
    similar roles to those in the universal quantifier.
 - An “at most” quantifier
    `atmost Java-expression of (type name in Java-expression )` constraint


Above, the operators have been listed in decreasing order of precedence. Note that this can
require parentheses around quantifiers to achieve the desired effect. For example, the conjunction
of two quantifiers can be written like this:

```
(exists (Word w in sentence) someLearner(w) :: "good") 
            /\ (exists (Word w in sentence) otherLearner(w) :: "better")
```

The arguments to the equality and inequality predicates are treated specially. If any of these
arguments is an invocation of a learning classifier, that classifier and the object it classifies
become an inference variable, so that the value produced by the classifier on that object is
subject to change by the inference procedure. The values of all other expressions that appear as
an argument to either type of predicate are constants in the inference procedure. This includes,
in particular, expressions that include a learning classifier invocation as a subexpression. These
learning classifier invocations are not treated as inference variables.

### 4.2.2 Constraint Declarations 

An LBJ constraint declaration declares a Java method whose purpose is to locate the objects
involved in the inference and generate the constraints. Syntactically, an LBJ constraint declaration
starts with a header indicating the name of the constraint and the type of object it takes
as input, similar to a method declaration with a single parameter:

```
constraint name (type name ) method-body
```

where `method-body` may contain arbitrary Java code interspersed with constraint statements all
enclosed in curly braces. When invoked with the `@` operator (discussed in Section 4.2.1), the
occurrence of a constraint statement in the body of a constraint declaration signifies not that
the constraint expression will be evaluated in place, but instead that a first order representation
of the constraint expression will be constructed for an inference algorithm to manipulate. The
final result produced by the constraint is the conjunction of all constraint statements encountered
while executing the constraint.

In addition, constraints declared in this way may also be used as Boolean classifiers as if they
had been declared:

```
discrete{"false", "true"} name (type name )
```

Thus, a constraint may be invoked as if it were a Java method (i.e., without the `@` symbol described
in Section 4.2.1) anywhere in an LBJ source file, just like a classifier. Such an invocation
will evaluate the constraint in place, rather than constructing its first order representation.

## 4.3 Inference 

The syntax of an LBJ inference has the following form:

```java 
inference name head type name
{
    [type name method-body ]+           // "Head-finder" methods
    [[name ] normalizedby name ;]∗      // How to normalize scores
    subjectto method-body               // Constraints
    with instance-creation-expression   // Names the algorithm
}
```

This structure manages the functions, run-time objects, and constraints involved in an inference.
Its header indicates the name of the inference and its head parameter. The head parameter
(or head object) is an object from which all objects involved in the inference can be reached
at run-time. This object need not have the same type as the input parameter of any learned
function involved in the inference. It also need not have the same type as the input parameter
of any constraint involved in the inference, although it often will.

After the header, curly braces surround the body of the inference. The body contains the
following four elements. First, it contains at least one “head finder” method. Head finder methods
are used to locate the head object given an object involved in the inference. Whenever the
programmer wishes to use the inference to produce the constrained version of a learning classifier
involved in the inference, that learning classifier’s input type must have a head finder method in
the inference body. Head finder methods are usually very simple. For example:

```
Word w { return w.getSentence(); }
```

might be an appropriate head finder method when the head object has type `Sentence` and one
of the classifiers involved in the inference takes `Words` as input. 

Second, the body specifies how the scores produced by each learning classifier should be
normalized. The LBJ library contains a set of normalizing functions that may be named here. It
is not strictly necessary to use normalization methods, but doing so ensures that the scores computed
for each possible prediction may be treated as a probability distribution by the inference
algorithm. Thus, we may then reason about the inference procedure as optimizing the expected
number of correct predictions.

The syntax of normalizer clauses enables the programmer to specify a different normalization
method for each learning classifier involved in the inference. It also allows for the declaration of
a default normalizer to be used by learning classifiers which were not given normalizers individually.
For example:

```java 
SomeLearner normalizedby Sigmoid;
normalizedby Softmax;
```

These normalizer clauses written in any order specify that the `SomeLearner` learning classifier
should have its scores normalized with the `Sigmoid` normalization method and that all other
learning classifiers involved in the inference should be normalized by `Softmax`.

Third, the subjectto clause is actually a constraint declaration (see Section 4.2) whose input
parameter is the head object. For example, let’s say an inference named `MyInference` is declared
like this:

```java 
inference MyInference head Sentence s
```

and suppose also that several other constraints have been declared named (boringly) `Constraint1`,
`Constraint2`, and `Constraint3`. Then an appropriate `subjectto` clause for `MyInference` might
look like this:

```
subjectto { @Constraint1(s) /\ @Constraint2(s) /\ @Constraint3(s); }
```

The `subjectto` clause may also contain arbitrary Java, just like any other constraint declaration.

Finally, the `with` clause specifies which inference algorithm to use. It functions similarly to
the `with` clause of a learning classifier expression (see Section 4.1.2.6).

## 4.4 “Makefile” Behavior 

An LBJ source file also functions as a makefile in the following sense. First, code will only be
generated for a classifier definition when it is determined that a change has been made5
in the
LBJ source for that classifier since the last time the compiler was executed 
(When the file(s) containing the translated code for a given classifier do not exist, this is, of course, also interpreted
as a change having been made). 
Second, a learning
classifier will only be trained if it is determined that the changes made affect the results of learning.
More precisely, any classifier whose definition has changed lexically is deemed “affected”.
Furthermore, any classifier that makes use of an affected classifier is also affected. This includes
method bodies that invoke affected classifiers and conjunctions and learning classifiers involving
at least one affected classifier. A learning classifier will be trained if and only if a change has been
made to its own source code or it is affected. Thus, when an LBJ source contains many learning
classifiers and a change is made, time will not be wasted re-training those that are unaffected.

In addition, the LBJ compiler will automatically compile any Java source files that it depends
on, so long as the locations of those source files are indicated with the appropriate command
line parameters (see Section 6.2). For example, if the classifiers in an LBJ source file are defined
to take classes from the programmer’s internal representation as input, the LBJ compiler will
automatically compile the Java source files containing those class’ implementations if their class
files don’t already exist or are out of date.
