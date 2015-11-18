# The LBJ Library 

The LBJ programming framework is supported by a library of interfaces, learning algorithms,
and implementations of the building blocks described in Chapter 4. This chapter gives a general
overview of each of those codes. .

The library is currently organized into five packages. `LBJ2.classify` contains classes related
to features and classification. `LBJ2.learn` contains learner implementations and supporting
classes. `LBJ2.infer` contains inference algorithm implementations and internal representations
for constraints and inference structures. `LBJ2.parse` contains the Parser interface and some
general purpose internal representation classes. Finally, `LBJ2.nlp` contains some basic natural
language processing internal representations and parsing routines. In the future, we plan to
expand this library, adding more varieties of learners and domain specific parsers and internal
representations.

## `LBJ2.classify`

The most important class in LBJ’s library is `LBJ2.classify.Classifier`. This abstract class
is the interface through which the application accesses the classifiers defined in the LBJ source
file. However, the programmer should, in general, only have need to become familiar with a few
of the methods defined there.

One other class that may be of broad interest is the `LBJ2.classify.TestDiscrete` class
(discussed in Section 5.1.8), which can automate the performance evaluation of a discrete learning
classifier on a labeled test set. The other classes in this package are designed mainly for internal
use by LBJ’s compiler and can be safely ignored by the casual user. More advanced users who
writes their own learners or inference algorithms in the application, for instance, will need to
become familiar with them.

## `LBJ2.classify.Classifier`

