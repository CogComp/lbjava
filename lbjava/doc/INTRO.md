#1 Introduction

Learning Based Java is a modeling language for the rapid development of
software systems with one or more learned functions, designed for use with the
Java programming language.  LBJava offers a convenient, declarative syntax for
classifier and constraint definition directly in terms of the objects in the
programmer's application.  With LBJava, the details of feature extraction,
learning, model evaluation, and inference are all abstracted away from the
programmer, leaving him to reason more directly about his application.

##1.1 Motivation

Many software systems are in need of functions that are simple to describe but
that no one knows how to implement.  Recently, more and more designers of such
systems have turned to machine learning to plug these gaps.  Given data, a
discriminative machine learning algorithm yields a function that classifies
instances from some problem domain into one of a set of categories.  For
example, given an instance from the domain of email messages (i.e., given an
email), we may desire a function that classifies that email as either "spam"
or "not spam".  Given data (in particular, a set of emails for which the
correct classification is known), a machine learning algorithm can provide
such a function.  We call systems that utilize machine learning technology
learning based programs.

Modern learning based programs often involve several learning components (or,
at least a single learning component applied repeatedly) whose classifications
are dependent on each other.  There are many approaches to designing such
programs; here, we focus on the following approach.  Given data, the various
learning components are trained entirely independently of each other, each
optimizing its own loss function.  Then, when the learned functions are
applied in the wild, the independent predictions made by each function are
reconciled according to user specified constraints.  This approach has been
applied successfully to complicated domains such as Semantic Role Labeling.

##1.2 LBJava

Learning Based Java (LBJava) is a modeling language that expedites the
development of learning based programs, designed for use with the JavaTM
programming language.  The LBJava compiler accepts the programmer's classifier
and constraint specifications as input, automatically generating efficient
Java code and applying learning algorithms (i.e., performing training) as
necessary to implement the classifiers' entire computation from raw data
(i.e., text, images, etc.) to output decision (i.e., part of speech tag, type
of recognized object, etc.). The details of feature extraction, learning,
model evaluation, and inference (i.e., reconciling the predictions in terms of
the constraints at runtime) are abstracted away from the programmer.

Under the LBJava programming philosophy, the designer of a learning based program
will first design an object-oriented internal representation (IR) of the
application's raw data using pure Java.  For example, if we wish to write
software dealing with emails, then we may wish to define a Java class named
Email.  An LBJava source file then allows the programmer to define classifiers
that take Emails as input.  A classifier is merely any method that produces
one or more discrete or real valued classifications when given a single object
from the programmer's IR.  It might be hard-coded using explicit Java code
(usually for use as a feature extractor), or learned from data (e.g., labeled
example Emails) using other classifiers as feature extractors.

Feature extraction and learning typically produce several different
intermediate representations of the data they process.  The LBJava compiler
automates these processes, managing all of their intermediate representations
automatically.  An LBJava source file also acts as a Makefile of sorts.  When you
make a change to your LBJava source file, LBJava knows which operations need to be
repeated.  For example, when you change the code in a hard-coded classifier,
only those learned classifiers that use it as a feature will be retrained.
When you change only a learning algorithm parameter, LBJava skips feature
extraction and goes straight to learning.

LBJava is supported by a library of interfaces and classes that implement a
standardized functionality for features and classifiers.  The library includes
learning and inference algorithm implementations, general purpose and domain
specific internal representations, and domain specific parsers.
