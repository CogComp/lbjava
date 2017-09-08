---
title: 20NEWSGROUP
---

# 3. Tutorial: 20 Newsgroups

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

## 3.1 Setting Up

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
take advantage of that functionality; it won't be necessary to override `reset()`
or `close()`. `NewsgroupParser` takes as input a file
containing the names of other files, assuming that each of those files
represents a single newsgroup post.  For brevity, we have hidden in `Post`'s 
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

## 3.2 Classifier Declarations

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

### 3.2.1 Hard-coded classifiers

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

### 3.2.2 Learners

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

 - `learn`: We say that `NewsgroupClassifier` is trying to mimic `NewsgroupLabel`
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

## 3.3 Using `NewsgroupClassifier` in a Java Program
Now that we’ve specified a learned classifier, the next step is to write a pure Java application that
will use it once it’s been trained. This section first introduces the methods every automatically
generated LBJava classifier makes available within pure Java code. These methods comprise a simple
interface for predicting, online learning, and testing with a classifier.

### 3.3.1 Getting Started

We assume here that all learning will take place during the LBJava compilation phase, which we’ll
discuss in Section 3.4. (It is also possible to learn online, i.e. while the application is running,
which we’ll discuss in Section 3.3.3.) To gain access to the learned classifier within your Java
program, simply instantiate an object of the classifier’s generated class, which has the same name
as the classifier.

```java 
NewsgroupClassifier ngClassifier = new NewsgroupClassifier();
```

The classifier is now ready to make predictions on example objects. `NewsgroupClassifier`
was defined to take Post objects as input and to make a discrete prediction as output. Thus, if
we have a `Post` object available, we can retrieve `NewsgroupClassifier`’s prediction like this:

```java 
Post post = String prediction = ngClassifier.discreteValue(post);
```

The prediction made by the classifier will be one of the string labels it observed during
training. And that’s it! The programmer is now free to use the classifier’s predictions however
s/he chooses. 

There's one important technical point to be aware of here. The instance we just created of
class `NewsgroupClassifier` above does not actually contain the model that LBJava learned for us.
It is merely a "clone" object that contains internally a reference to the real classifier. Thus, if
our Java application creates instances of this class in different places and performs any operation
that modifies the behavior of the classifier (like online learning), all instances will appear to be
affected by the changes. For simple use cases, this will not be an issue, but see Section 3.3.4 for
details on gaining direct access to the model.

### 3.3.2 Prediction Confidence 

We’ve already seen how to get the prediction from a discrete valued classifier. This technique
will work no matter how the classifier was defined; be it hard-coded, learned, or what have you.
When the classifier is learned, it can go further than merely providing the prediction value it
likes the best. In addition, it can provide a score for every possible prediction it chose amongst,
thereby giving an indication of how confident the classifier is in its prediction. The prediction
with the highest score is the one selected.

Scores are returned by a classifier in a `ScoreSet` object by calling the `score(Object)` method,
passing in the same example object that you would have passed to `discreteValue(Object)`.
Once you have a `ScoreSet` you can get the score for any particular prediction value using the
`get(String)` method, which returns a `double`. Alternatively, you can retrieve all scores in an
array and iterate over them, like this:

```java 
ScoreSet scores = ngClassifier.scores(post);
Score[] scoresArray = scores.toArray();
for (Score score : scoresArray)
    System.out.println("prediction: " + score.value + ", score: " + score.score);
```

Finally, LBJava also lets you define real valued classifiers which return doubles in the Java
application. If you have such a classifier, you can retreive its prediction on an example object
by calling the `realValue(Object)` method:

```java 
double prediction = realClassifier.realValue(someExampleObject);
```

### 3.3.3 Learning 

As mentioned above, most classifiers are learned during the LBJava phase of compilation (see
Section 3.4 below). In addition, a classifier generated by the LBJava compiler can also continue
learning from labeled examples in the Java application. Since `NewsgroupClassifier` takes a
`Post` object as input, we merely have to get our hands on such an object, stick the label in the
newsgroup field (since that’s where the `NewsgroupLabel` classifier will look for it), and pass it
to the classifier’s `learn(Object)` method.

Now that we know how to get our classifier to learn, let’s see how to make it forget.
The contents of a classifier can be completely cleared out by calling the `forget()` method.
After this method is called, the classifier returns to the state it was in before it observed any
training examples. One reason to forget everything a classifier has learned is to try new learning
algorithm parameters (e.g. learning rates, thresholds, etc.). All LBJava learning algorithms provide
an inner class named `Parameters` that contains default settings for all their parameters. Simply
instantiate such an object, overwrite the parameters that need to be updated, and call the
`setParameters(Parameters)` method. For example:

```java 
ngClassifier.forget();
SparseAveragedPerceptron.Parameters ltuParameters = new SparseAveragedPerceptron.Parameters();
ltuParameters.thickness = 12;
NewsgroupClassifier.Parameters parameters = new NewsgroupClassifier.Parameters();
parameters.baseLTU = new SparseAveragedPerceptron(ltuParameters);
ngClassifier.setParameters(parameters);
```

This particular example is complicated by the fact that our newsgroup classifier is learned
using `SparseNetworkLearner`, an algorithm that uses another learning algorithm with its own
parameters as a subroutine. But the technique is the same. At this point, the classifier is
re-initialized with a new `thickness` setting and is ready for new training examples.

### 3.3.4 Saving Your Work 

If we’ve done any `forget()`ing and/or `learn()`ing within our Java application, we’ll probably
be interested in saving what we learned at some point. No problem; simply call the `save()`
method.

```java 
classifier.save();
```

This operation overwrites the model and lexicon files that were originally generated by the
LBJava compiler. A model file stores the values of the learned parameters (not to be confused
with the manually set learning algorithm parameters mentioned above). A lexicon file stores
the classifier’s feature index, used for quick access to the learnable parameters when training for
multiple rounds. These files are written by the LBJava compiler and by the `save()` method (though 
only initially; see below) in the same directory where the `NewsgroupClassifier.class` file is
written.    

We may also wish to train several versions of our classifier; perhaps each version will use different
manually set parameters. But how can we do this if each instance of our `NewsgroupClassifier`
class is actually a ”clone”, merely pointing to the real classifier object? Easy: just use the
`NewsgroupClassifier` constructor that takes model and lexicon filenames as input:

```java 
NewsgroupClassifier c2 = new NewsgroupClassifier( "myModel.lc", "myLexicon.lex");
```
This instance of our classifier is not a clone, simply by virtue of our chosen constructor.
It has its own completely independent learnable parameters. Furthermore, if `myModel.lc` and
`myLexicon.lex` exist, they will be read from disk into `c2`. If not, then calling this constructor
creates them. Either way, we can now train our classifier however we choose and then simply
call `c2.save()` to save everything into those files.

## 3.4 Compiling Our Learning Based Program with LBJava

Referring once again to this 
[newsgroup classifier’s source distribution](http://cogcomp.cs.illinois.edu/software/20news.tgz), 
we first examine our
chosen directory structure starting from the root directory of the distribution.

```
$ ls
20news.LBJava class LBJava test.sh
README data src train.sh
$ ls src/dssi/news
NewsgroupParser.java NewsgroupPrediction.java Post.java
```

We see there is an LBJava source file `20news.LBJava` in the root directory, and in `src/dssi/news`
we find plain Java source files implementing our internal representation (`Post.java`), a parser
that instantiates our internal representation (`NewsgroupParser.java`), and a program intended
use our trained classifier to make predictions about newsgroups (`NewsgroupPrediction.java`).
Note that the LBJava source file and all these plain Java source files declare `package dssi.news;`.
The root directory also contains two directories `class` and `LBJava` which are initially empty. They
will be used to store all compiled Java class files and all Java source files generated by the LBJava
compiler respectively. Keeping all these files in separate directories is not a requirement, but
many developers find it useful to reduce clutter around the source files they are editing

To compile the LBJava source file using all these directories as intended, we run the following
command:

```
$ java -Xmx512m -cp $CLASSPATH:class LBJ2.Main \
        -sourcepath src \  
        -gsp LBJava \
        -d class \
        20news.LBJava
```

This command runs the LBJava compiler on `20news.LBJava`, generating a new Java source file
for each of the classifiers declared therein. Since `20news.LBJava` mentions both the `Post` and
`NewsgroupParser` classes, their definitions (either compiled class files or their original source
files) must be available within a directory structure that mirrors their package names. We
have provided their source files using the `-sourcepath src` command line flag. The `-gsp LBJava`
(generated source path) flag tells LBJava to put the new Java source files it generates in the `LBJava`
directory, and the `-d class` flag tells LBJava to put class files in the `class` directory. For more
information on the LBJava compiler’s command line usage, see Chapter 6.

But the command does more than that; it also trains any learning classifiers on the specified
training data, so that the compiled class files are ready to be used in new Java programs just
like any other class can be. The fact that their implementations came from data is immaterial;
the new Java program that uses these learned classifiers is agnostic to whether the functions it
is calling are learned or hard-coded. Its output will look like this:

```
Generating code for BagOfWords
Generating code for NewsgroupLabel
Generating code for NewsgroupClassifier
Compiling generated code
Training NewsgroupClassifier
NewsgroupClassifier, pre-extract: 0 examples at Sun Mar 31 10:48...
NewsgroupClassifier, pre-extract: 16828 examples at Sun Mar 31 10:49...
NewsgroupClassifier: Round 1, 0 examples processed at Sun Mar 31 10:49...
NewsgroupClassifier: Round 1, 16828 examples processed at Sun Mar 31 10:49...
NewsgroupClassifier: Round 2, 0 examples processed at Sun Mar 31 10:49...
...
Writing NewsgroupClassifier
Compiling generated code
```

The compiler tells us which classifiers it is generating code for and which it is training.
Because we have specified `progressOutput 20000` in `NewsgroupClassifier`’s specification (see
the distribution’s `20news.LBJava` file), we also get messages updating us on the progress being
made during training. We can see here that the first stage of training is a “pre-extraction” stage
in which a feature index is compiled, and all `Post` objects in our training set are converted to
feature vectors based on the index. Then the classifier is trained over those vectors for 40 rounds.
The entire process should take under 2 minutes on a modern machine.

If you’re curious, you can also look at the files that have been generated:

```
$ ls LBJava/dssi/news
BagOfWords.java NewsgroupClassifier.java
NewsgroupClassifier.ex NewsgroupLabel.java
$ ls class/dssi/news
BagOfWords.class NewsgroupLabel.class
NewsgroupClassifier$Parameters.class NewsgroupParser.class
NewsgroupClassifier.class Post$1.class
NewsgroupClassifier.lc Post.class
NewsgroupClassifier.lex
```

The LBJava directory now contains a `dssi/news` subdirectory containing our classifier’s Java
implementations, as well as the pre-extracted feature vectors in the `NewsgroupClassifier.ex`
file. In the `class/dssi/news` directory, we find the class files compiled from all our hard-coded
and generated Java source files, as well as `NewsgroupClassifier.lc` and `NewsgroupClassifier`.
lex, which contain `NewsgroupClassifier`’s learned parameters and its feature index (a.k.a.
lexicon) respectively.

Finally, it’s time to compile `NewsgroupPrediction.java`, the program that calls our learned
classifier to make predictions about new posts.

```
$ javac -cp $CLASSPATH:class \
    -sourcepath src \
    -d class \
    src/dssi/news/NewsgroupPrediction.java
```

Notice that the command line flags we gave to the LBJava compiler previously are very similar to
those we give the Java compiler now. We can test out our new program like this:

```
$ java -Xmx512m -cp $CLASSPATH:class dssi.news.NewsgroupPrediction \
$(head data/20news.test.shuffled)
data/alt.atheism/53531: alt.atheism
data/talk.politics.mideast/76075: talk.politics.mideast
data/sci.med/59050: sci.med
data/rec.sport.baseball/104591: rec.sport.baseball
data/comp.windows.x/67088: comp.windows.x
data/rec.motorcycles/103131: rec.autos
data/sci.crypt/15215: sci.crypt
data/talk.religion.misc/84195: talk.religion.misc
data/sci.electronics/54094: sci.electronics
data/comp.os.ms-windows.misc/10793: comp.os.ms-windows.misc
```

Post `rec.motorcycles/103131` was misclassified as `rec.autos`, but other than that, things are
going well.


## 3.5 Testing a Discrete Classifier
When a learned classifier returns discrete values, LBJava provides the handy `TestDiscrete`
class  for measuring the classifier’s prediction performance. This class can be used either as a standalone
program or as a library for use inside a Java application. In either case, we’ll need to
provide `TestDiscrete` with the following three items:

 - The classifier whose performance we’re measuring (e.g. `NewsgroupClassifier`).
 - An oracle classifier that knows the true labels (e.g. `NewsgroupLabel`).
 - A parser (i.e., any class implementing the `Parser` interface) that returns objects of our
classifiers’ input type.

### 3.5.1 On the Command Line 

If we’d like to use `TestDiscrete` on the command line, the parser must provide a constructor that
takes a single String argument (which could be, e.g., a file name) as input. `NewsgroupClassifier`
uses the `NewsgroupParser` parser, which meets this requirement, so we can test our classifier on
the command line like this:

```
$ java -Xmx512m -cp $CLASSPATH:class LBJ2.classify.TestDiscrete \
    dssi.news.NewsgroupClassifier \
    dssi.news.NewsgroupLabel \
    dssi.news.NewsgroupParser \
    data/20news.test
```

The output of this program is a table of the classifier’s performance statistics broken down
by label. For a given label `l`, the statistics are based on the quantity of examples with that gold
truth label `c_l`, the quantity of examples predicted to have that label by the classifier `\hat{c}_l`, and the
overlap of these two sets, denoted `c_l ∧ \hat{c}_l` (i.e., the quantity of examples correctly predicted to
have that label). Based on these definitions, the table has the following columns:

 1. the label `l`,
 2. the classifier’s precision on `l`, `p_l =c_l ∧ \hat{c}_l / \hat{c}_l * × 100%`
 3. the classifier’s recall on `l`, `r_l = c_l ∧ \hat{c}_l / c_l × 100%`
 4. the classifier’s F1 on `l`, `F1(l) = 2 p_l r_l / (p_l+r_l)× 100%`
 5. the label count `c_l`, and
 6. the prediction count `\hat{c}_l`.
 
At the bottom of the table will always be the overall accuracy of the classifier. For the
 `NewsgroupClassifier`, we get this output:
 
 <img width="503" alt="screen shot 2015-11-17 at 3 46 18 am" src="https://cloud.githubusercontent.com/assets/2441454/11207928/d9eb9f3a-8cdd-11e5-8f34-989f3ddebd78.png">

 The `TestDiscrete` class also supports the notion of a null label, which is a label intended to
 represent the absence of a prediction. The 20 Newsgroups task doesn’t make use of this concept,
 but if our task were, e.g., named entity classification in which every phrase is potentially a named
 entity, then the classifier will likely output a prediction we interpret as meaning “this phrase is
 not a named entity.” In that case, we will also be interested in overall precision, recall, and F1
 scores aggregated over the non-null labels. On the `TestDiscrete` command line, all arguments
 after the four we’ve already seen are optional null labels. The output with a single null label
 “O” might look like this (note the `Overall` row at the bottom):
 
 <img width="382" alt="screen shot 2015-11-17 at 3 48 14 am" src="https://cloud.githubusercontent.com/assets/2441454/11207980/14dbda1a-8cde-11e5-91af-559ba4fdda41.png">

### 3.5.2 In a Java Program 

Alternatively, we can call `TestDiscrete` from within our Java application. This comes in handy
if our parser’s constructor isn’t so simple, or when we’d like to do further processing with the
performance numbers themselves. The simplest way to do so is to pass instances of our classifier,
labeler, and parser to `TestDiscrete`, like this:

```java
NewsgroupLabel oracle = new NewsgroupLabel();
Parser parser = new NewsgroupParser("data/20news.test");
TestDiscrete tester = TestDiscrete.testDiscrete(classifier, oracle, parser);
tester.printPerformance(System.out);
```
 
This Java code does exactly the same thing as the command line above. We can also
exert more fine grained control over the computed statistics. Starting from a new instance of
`TestDiscrete`, we can call `reportPrediction(String,String)` every time we acquire both a
prediction value and a label. Then we can either call the `printPerformance(PrintStream)`
method to produce the standard output in table form or any of the methods whose names start
with `get` to retrieve individual statistics. The example code below retrieves the overall precision,
recall, F1, and accuracy measures in an array.
 
```java
TestDiscrete tester = new TestDiscrete();
...
tester.reportPrediction(classifier.discreteValue(ngPost),
                                   oracle.discreteValue(ngPost));
...
double[] performance = tester.getOverallStats();
System.out.println("Overall Accuracy: " + performance[3]);
```
