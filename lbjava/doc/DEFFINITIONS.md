# LBJava Definitions

The terms defined below are used throughout this manual. Their definitions
have been carefully formulated to facilitate the design of a modeling language
that uses them as building blocks.  They are intended to disambiguate among
the terms' various usages in the literature, and in doing so they encapsulate
the LBJava programming philosophy.  For those experienced with ML and programming
with learning algorithms, the biggest differences from a more typical ML
programming philosophy are:

 - The word "classifier" does not imply learning.  Naive Bayes, SVM,
Perceptron, etc. are *not* classifiers; they are learning algorithms.

 - Classifiers (learned and otherwise) should work directly with (internal
representations of) raw data (e.g. text, images, etc.), and they should return
values that are directly useful to the application.

- Any interaction among classifiers' computations should be completely
transparent to the programmer.  In particular, when classifiers' outputs are
constrained with respect to each other, the classifiers should automatically
return values that respect the constraints.

### Feature
A feature is a data type which has both a name and a value.  There are two
types of features in LBJava; discrete and real.  The name of the feature in
either case is always a `String`.  A discrete feature has a value of type
`String` assumed to come from some finite set of unordered values
associated with the feature's name (although it is not necessary to know what
values that set contains ahead of time).  A real feature has a value of type
`double.

Most learning algorithms use features to index the parameters whose values are
determined during training. (An exception to this rule would be a
decision tree learning algorithm, which doesn't really have parameters, per
se.)  In the case of real features, the name of the feature alone identifies
the corresponding parameter.  In the case of discrete features, the
corresponding parameter is identified by the name and value
together. (Note again that a decision tree learning algorithm would
not need to "split up'' the values of a discrete feature so that they
represent separate features in this way.  It would simply use a single
branching point with many branches.  Anyway, it's a mute point, since LBJava does
not currenlty provide any decision tree learning algorithm implementations.)
The only exception to this rule is when a discrete feature is known to allow
only two different values.  Such a feature is equivalent to a real feature
that can take only the values `0` and `1`; in particular, its name alone
identifies the corresponding learned parameter.

### Classifier
A classifier is a method that takes exactly one object from the application
domain's internal representation as input and produces zero or more features
as output.  When defining a classifier, LBJava's syntax requires the programmer
to specify the classifier's input type, what type of feature(s) are produced,
and whether or not multiple features may be produced.  Depending on how the
classifier is declared, the programmer can be given a greater degree of
control over the features:


- A classifier may be defined to produce exactly one feature.  In this case, the
name of the feature produced is taken to be the name of the classifier,
leaving only the value to be computed by the classifier.

- A classifier may also be defined as a *feature generator*.  In this case,
both the names and the values of the produced features are computed by the
classifier.  The name of the classifier will also be stored inside the feature
to disambiguate with similar features produced by other classifiers.

A classifier may be coded explicitly using arbitrary Java, it may be composed
from other classifiers using LBJava's classifier operators, or it may be learned
from data as a function of the features produced by other classifiers with a
special syntax discussed later.

###  A learner or learning classifier is a classifier capable of changing its
implementation with experience.  The methods used for effecting that change
are commonly referred to as the *learning algorithm*.  In general, the
programmer is encouraged to simply call on a learning algorithm implemented in
LBJava's library, but it is also possible to implement a new learning algorithm
in a separate Java source code and to call it from LBJava source code in the same
way.

### Examples and Example Objects
An example object is simply an instance from the internal representation of
the raw data.  It can be any object whatsoever.  Examples are collections of
features extracted from a given example object using classifiers.  They are
taken as input by a learning algorithm both at training time and at testing
time.  Thus, as we will see, every learner starts with an example object,
applies a set of classifiers to that object to create an example, and then
sends that example to its learning algorithm for processing.

An example may or may not come with a *label*, which is just a feature
that indicates the correct classification of the corresponding example object.
Labels are extracted by classifiers, just like any other feature.  The only
difference is that a classifier designated as a label extractor will only be
called by a learner during training.

### Parser
A parser is a function that takes raw data as input and instantiates example
objects as output.  If a learner is associated with a parser inside an LBJava
source code, the LBJava will train that learner using the example objects
produced by the parser at LBJava compile-time.  There are several domain specific
parsers defined in the LBJava library, but the programmer will often need to
implement his own in a separate Java source file.

### Inference
Inference is the process through which discrete classifications made about
objects are reconciled with global constraints over those classifications.
Constraints are written by the programmer using first order logic and may
involve any number of classifiers and objects.  LBJava automatically translates
those constraints to linear inequalities for use in an Integer Linear Program
at run-time.

### Application
The application is the data processing code written in pure Java that works
directly with the (internal representation of) the raw data and has need to
classify elements of that data.  Because it calls classifiers defined only in
the LBJava source code, it typically cannot be compiled until after LBJava has
compiled the LBJava source file.

