# Tutorial: 20 Newsgroups

We begin our discussion of the LBJava language with a tutorial that illustrates
its most common usage.  This tutorial is intended for a first time user of the
language.  It introduces the syntax for both single feature and feature
generating hard-coded classifiers, as well as the syntax for declaring a
learner.  Next, it shows how the learner (or any other classifier) declared in
the LBJava source code can be imported and used in the Java application.
Finally, it discusses how to use the LBJava compiler on the command line to fully
compile our learning based program.  Throughout the tutorial, we'll be using
the famous [20 Newsgroups corpus](http://people.csail.mit.edu/jrennie/20Newsgroups) as our
training data.

## Setting Up

Suppose we want to classify newsgroup posts according to the newsgroup to
which each post is best suited.  Such a classifier could be used in a
newsgroup client application to automatically suggest an appropriate
destination for a new post.  It is plausible that these classifications could
be made as a function of the words that appear in them.  For example, the word
`motor` is likely to appear more often in `rec.autos` or `rec.motorcycles` than in 
`alt.atheism`.  However, we do not want to
manually invent these associations one at a time, so we turn to LBJava.

To use LBJava, we first need to decide on an object oriented internal
representation.  In this case, it makes sense to define a class named `Post` 
that stores the contents of a newsgroup post.  The following snippet 
shows a skeleton for such a class.  There, we see that space has been
allocated for several fields that we might expect in a newsgroup post, namely
the `From` and `Subject` fields from the header and the body of the post.
We have chosen to represent the body as a two dimensional array; one dimension
for the lines in the body, and the other for the words in each line.

```java 
public class Post {
    private String newsgroup; // The label of the post.
    private String fromHeader;
    private String subjectHeader;
    private [][] body;
}
```

Finally, we have a field called `newsgroup`.  It may seem counterintuitive
to include a field to store this information since it is exactly the
classification we aim to compute. LBJava's supervised learning algorithms will
need this information, however, since it labels the example object.
Furthermore, at test time, our newsgroup client application may fill this
field with the newsgroup in which the post was encountered or in which the
user intends to post it, and the learned classifier will simply ignore it at
that point.

We'll also need to implement a parser that knows how to create `Post`
objects when given the raw data in a file or files. In LBJava, a parser is any
class that implements the `lbjava.parse.Parser` interface. This is a simple
interface that requires only three methods be defined.  First, the `next()` 
method takes no arguments and returns a single example `Object`
(of any type in general, but in this case, it will be a `Post`). The LBJava
compiler will call this method repeatedly to retrieve training example objects
until it returns `null`.  Next, the `reset()` method rewinds the
parser back to the beginning of the raw data input it has been reading.
Finally, the `close()` method closes any streams the parser may have open
and frees any other system resources it may be using.

The LBJava library comes with several parsers that read plain text. While it
does not include a parser for newsgroup posts, we can still make use of 
`lbjava.parse.LineByLine`, which will at least take care of the boilerplate code
necessary to read text out of a file.  This abstract class also provides
implementations of the `reset()` and `close()` methods. The `NewsgroupParser` class 
in the following snippet, simply extends it to
take advantage of that functionality; it won't be necessary to override `
reset()` or `close()`. `NewsgroupParser` takes as input a file
containing the names of other files, assuming that each of those files
represents a single newgroup post.  For brevity, we have hidden in `Post`'s 
constructor the code that actually does the work of filling the fields
of a `Post` object.

```java
    import lbjava.parse.LineByLine; 

    public class NewsgroupParser extends LineByLine {
        public NewsgroupParser(String file)
        { super(file); } 

        public Object next() { 
            String file = readLine(); 
            if (file == null)
                return null; //  No more examples.
            return new Post(file); 
        }
    }
```

With `Post` and `NewsgroupParser` ready to go, we can now define in
the LBJava source code a hard-coded classifier that identifies which words appear
in each post and a learning classifier that categorizes each post based on
those words.

## Classifier Declarations

Given the internal representation developed in the previous section, the LBJava
code bellow can be used to train a learned newsgroup
classifier.  It involves a single feature extraction classifier named `BagOfWords`, 
a label classifier named `NewsgroupLabel` to provide labels
during training, and a multi-class classifier named `NewsgroupClassifier`
that predicts a newsgroup label.  It also assumes that the `Post` class
and the parser `NewsgroupParser` (or their source files) are available on
the `CLASSPATH`. To see the code in action, [download the source
distribution](http://cogcomp.cs.illinois.edu/software/20news.tgz) 
(it includes the data and all the classes mentioned above -- 
and run `./train.sh`; assuming that LBJava is already on your `CLASSPATH`).  We'll now
take a closer look at how it works.

```java
discrete% BagOfWords(Post post) <- {
    for (int i = 0; i < post.bodySize(); ++i)
        for (int j = 0; j < post.lineSize(i); ++j) {
            String word = post.getBodyWord(i, j);
            if (word.length() > 0 && word.substring(0, 1).matches("[A-Za-z]"))
                sense word;
        }
}

discrete NewsgroupLabel(Post post) <- { return post.getNewsgroup(); }

discrete NewsgroupClassifier(Post post) <-
learn NewsgroupLabel
    using BagOfWords
    from new NewsgroupParser("data/20news.train.shuffled") 40 rounds
    with SparseNetworkLearner {
        SparseAveragedPerceptron.Parameters p =
            new SparseAveragedPerceptron.Parameters();
        p.learningRate = .1;
        p.thickness = 3;
        baseLTU = new SparseAveragedPerceptron(p);
    }
end
```

## Hard-coded classifiers

An LBJava source file is a list of declarations.  The simplest in the LBJava 
of the previous section, is contained entirely on line 13. It consists of the
classifier's *signature* and a hard-coded *classifier expression*
separated by a left arrow indicating assignment.  In the classifier's
signature, we see its return type (a single discrete feature) as well as its
input type (an object of type `Post`).  All LBJava classifiers take a single
object (of any type) as input.  It is up to the programmer to ensure that all
information pertinent to the classifiers is accessible from that object.  The
return type, however, is not quite so restrictive.  Returned features may be
either `discrete` or `real`, and a classifier may return either a
single feature (as on line 13) or multiple features (as indicated on line 3
with the `%` symbol).  When a classifier can return multiple features, we
call it a *feature generator*.

On the right hand side of the left arrow is placed a classifier expression.
There are many types of classifier expression, and the two most common are on
displayed here.  `BagOfWords` and `NewsgroupLabel` are defined
with hard-coded classifier expressions, while `NewsgroupClassifier` is
defined with a learning classifier expression.  When hard-coding the behavior
of a classifier, the programmer has Java 1.4 syntax at his disposal to aid in
computing his features' values, plus some additional syntactic sugar to make
that type of computation easier.
%
% TODO; Forward link to somewhere that talks about syntactic sugar in
% hard-coded classifiers.
%
For example, the `sense` statement on line 9 creates a feature which will
eventually be returned, but execution of the method continues so that multiple
features can be ``sensed.''  Note that only feature generators can use the
`sense` statement, and only classifier returning a single feature can use
Java's `return` statement (as on line 13).

After everything is said and done, we end up with two hard-coded classifiers.
One is a simple, one feature classifier that merely returns the value of the
`Post.newsgroup` field (via the `getNewsgroup()` method, since `Post.newsgroup` 
is private).  The other loops over all the words in the post
returning each as a separate feature.

### Learners

`NewsgroupClassifier` on line 15 of 20 newsgroup example is not
specified in the usual, procedural way, but instead as the output of a
learning algorithm applied to data.  The verbose learning classifier
expression syntax says that this classifier will `learn` to mimic an
oracle (line 16), `using` some feature extraction classifiers (line 17),
`from` some example objects (line 18), `with` a learning algorithm
(lines 19 through 25).  The expression ends with the `end` keyword (line
26).  In this case, the oracle is `NewsgroupLabel`, the only feature
extraction classifier is `BagOfWords`, the example objects come from
`NewsgroupParser`, and the learning algorithm is `SparseNetworkLearner`.
We explore each of these ideas in more detail below.

 - `learn`: We say that `NewsgroupClassifier is trying to mimic `NewsgroupLabel`
    because it will attempt to return features with the same
    values and for the same example objects that `NewsgroupLabel` would have
    returned them.  Note that the particular feature values being returned have
    not been mentioned; they are induced by the learning algorithm from the data.
    We need only make sure that the return type of the label classifier is
    appropriate for the selected learning algorithm.

 - `using`: The argument to the `using` clause is a single classifier expression. As
    we can see from this example code, the name of a classifier qualifies. The
    only restriction is that this classifier expression must have an input type
    that allows it to take instances of `NewsgroupClassifier`'s input type.
    LBJava also provides a comma operator for constructing a feature generator that
    simply returns all the features returned by the classifiers on either side of
    the comma. This way, we can include as many features as we want simply by
    listing classifiers separated by commas.
    
    TODO: forward reference to a section about composite generators

 - `from`:  The `from` clause supplies a data source by instantiating a parser.  The
    objects returned by this parser's `next()` method must be instances of
    `NewsgroupClassifier`'s input type. LBJava can then extract features via the
    using clause and train with the learning algorithm.  This clause also gives
    the programmer the opportunity to iterate over the training data if he so
    desires.  The optional `rounds` clause is part of the `from` clause,
    and it specifies how many times to iterate.

 - `with`: The argument to the `with` clause names a learning algorithm (any class
    extending `lbjava.learn.Learner` accessible on the `CLASSPATH`) and
    allows the programmer to set its parameters.  For example, `learningRate`
    (line 22) and `thickness` (line 23) are parameters of the
    `SparseAveragedPerceptron` learning algorithm, while `baseLTU` (line
    24) is a parameter of the `SparseNetworkLearner` learning algorithm.
    
From these elements, the LBJava compiler generates Java source code that performs
feature extraction, applies that code on the example objects to create
training examples, and trains our learner with them.  The resulting learner
is, in essence, a Java method that takes an example object as input and
returns the predicted newsgroup in a string as output.  Note that the code
does not specify the possible newsgroup names or any other particulars about
the content of our example objects.  The only reason that this LBJava code
results in a newsgroup classifier is because we give it training data that
induces one. If we want a spam detector instead, we need only change data
sources; the LBJava code need not change.(Of course, we may want to
change the names of our classifiers in that case for clarity's sake.)

## Using `NewsgroupClassifier` in a Java Program
TODO 

## Compiling Our Learning Based Program with LBJava
TODO
