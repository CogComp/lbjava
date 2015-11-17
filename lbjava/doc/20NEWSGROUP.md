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
classification we aim to compute.  LBJava's supervised learning algorithms will
need this information, however, since it labels the example object.
Furthermore, at test time, our newsgroup client application may fill this
field with the newsgroup in which the post was encountered or in which the
user intends to post it, and the learned classifier will simply ignore it at
that point.



We'll also need to implement a parser that knows how to create {\tt Post}
objects when given the raw data in a file or files.  In LBJava, a parser is any
class that implements the {\tt lbjava.parse.Parser} interface.  This is a simple
interface that requires only three methods be defined.  First, the {\tt
next()} method takes no arguments and returns a single example {\tt Object}
(of any type in general, but in this case, it will be a {\tt Post}).  The LBJava
compiler will call this method repeatedly to retrieve training example objects
until it returns {\tt null}.  Next, the {\tt reset()} method rewinds the
parser back to the beginning of the raw data input it has been reading.
Finally, the {\tt close()} method closes any streams the parser may have open
and frees any other system resources it may be using.

The LBJava library comes with several parsers that read plain text.  While it
does not include a parser for newsgroup posts, we can still make use of {\tt
lbjava.parse.LineByLine}, which will at least take care of the boilerplate code
necessary to read text out of a file.  This abstract class also provides
implementations of the {\tt reset()} and {\tt close()} methods.  The {\tt
NewsgroupParser} class in Figure \ref{fig:newsParser} simply extends it to
take advantage of that functionality; it won't be necessary to override {\tt
reset()} or {\tt close()}.  {\tt NewsgroupParser} takes as input a file
containing the names of other files, assuming that each of those files
represents a single newgroup post.  For brevity, we have hidden in {\tt
Post}'s constructor the code that actually does the work of filling the fields
of a {\tt Post} object.

\begin{figure}[t]
\begin{code}
1. \>{\color{Purple} import} lbjava.parse.LineByLine; \\
2. \>\\
3. \>{\color{ForestGreen} public class} NewsgroupParser
         {\color{ForestGreen} extends} LineByLine \{ \\
4. \>\>{\color{ForestGreen} public}
         NewsgroupParser({\color{BrickRed} String} file)
         \{ {\color{ForestGreen} super}(file); \} \\
5. \>\\
6. \>\>{\color{ForestGreen} public} {\color{BrickRed} Object} next() \{ \\
7. \>\>\>{\color{BrickRed} String} file = readLine(); \\
8. \>\>\>{\color{YellowOrange} if} (file == {\color{BrickRed} null})
           {\color{YellowOrange} return} {\color{BrickRed} null};
           \Comment{No more examples.} \\
9. \>\>\>{\color{YellowOrange} return new} Post(file); \\
10.\>\>\} \\
11.\>\}
\end{code}
\caption{Class {\tt NewsgroupParser} instantiates {\tt Post} objects and
returns them one at a time via the {\tt next()} method.}
\label{fig:newsParser}
\end{figure}

With {\tt Post} and {\tt NewsgroupParser} ready to go, we can now define in
the LBJava source code a hard-coded classifier that identifies which words appear
in each post and a learning classifier that categorizes each post based on
those words.

\section{Classifier Declarations}

Given the internal representation developed in the previous section, the LBJava
code in Figure \ref{fig:lbj20news} can be used to train a learned newsgroup
classifier.  It involves a single feature extraction classifier named {\tt
BagOfWords}, a label classifier named {\tt NewsgroupLabel} to provide labels
during training, and a multi-class classifier named {\tt NewsgroupClassifier}
that predicts a newsgroup label.  It also assumes that the {\tt Post} class
and the parser {\tt NewsgroupParser} (or their source files) are available on
the {\tt CLASSPATH}.  To see the code in action, download the source
distribution\footnote{{\tt
http://cogcomp.cs.illinois.edu/software/20news.tgz}} from our website -- it
includes the data and all the classes mentioned above -- and run {\tt
./train.sh} (assuming that LBJava is already on your {\tt CLASSPATH}).  We'll now
take a closer look at how it works.

\begin{figure}[t]
\begin{code}
1. \>{\color{Purple} import} lbjava.nlp.seg.Token;\\
2. \>\\
3. \>{\color{ForestGreen} discrete}\% BagOfWords(Post post) <- \{\\
4. \>\>{\color{YellowOrange} for} ({\color{ForestGreen} int} i =
      {\color{BrickRed} 0}; i < post.bodySize(); ++i)\\
5. \>\>\>{\color{YellowOrange} for} ({\color{ForestGreen} int} j =
        {\color{BrickRed} 0}; j < post.lineSize(i); ++j) \{\\
6. \>\>\>\>Token word = post.getBodyWord(i, j);\\
7. \>\>\>\>{\color{BrickRed} String} form = word.form;\\
8. \>\>\>\>{\color{YellowOrange} if} (form.length() > {\color{BrickRed} 0}
        \&\& form.substring({\color{BrickRed} 0},
            {\color{BrickRed} 1}).matches({\color{BrickRed} "[A-Za-z]"})) \\
9. \>\>\>\>\>{\color{YellowOrange} sense} form;\\
10.\>\>\>\}\\
11.\>\}\\
12.\>\\
13.\>{\color{ForestGreen} discrete} NewsgroupLabel(Post post) <-
     \{ {\color{YellowOrange} return} post.getNewsgroup(); \}\\
14.\>\\
15.\>{\color{ForestGreen} discrete} NewsgroupClassifier(Post post) <-\\
16.\>{\color{RoyalBlue} learn} NewsgroupLabel\\
17.\>\>{\color{RoyalBlue} using} BagOfWords\\
18.\>\>{\color{RoyalBlue} from} {\color{YellowOrange} new}
    NewsgroupParser({\color{BrickRed} "data/20news.train.shuffled"})
    {\color{BrickRed} 40} {\color{RoyalBlue} rounds}\\
19.\>\>{\color{RoyalBlue} with} SparseNetworkLearner \{\\
20.\>\>\>SparseAveragedPerceptron.Parameters p =\\
21.\>\>\>\>{\color{YellowOrange} new} SparseAveragedPerceptron.Parameters();\\
22.\>\>\>p.learningRate = {\color{BrickRed} .1};\\
23.\>\>\>p.thickness = {\color{BrickRed} 3};\\
24.\>\>\>baseLTU = {\color{YellowOrange} new} SparseAveragedPerceptron(p);\\
25.\>\>\}\\
26.\>{\color{RoyalBlue} end}
\end{code}
\caption{A simple, learned newsgroup classifier.}
\label{fig:lbj20news}
\end{figure}

\subsection{Hard-coded classifiers}

An LBJava source file is a list of declarations.  The simplest in Figure
\ref{fig:lbj20news} is contained entirely on line 13.  It consists of the
classifier's \emph{signature} and a hard-coded \emph{classifier expression}
separated by a left arrow indicating assignment.  In the classifier's
signature, we see its return type (a single discrete feature) as well as its
input type (an object of type {\tt Post}).  All LBJava classifiers take a single
object (of any type) as input.  It is up to the programmer to ensure that all
information pertinent to the classifiers is accessible from that object.  The
return type, however, is not quite so restrictive.  Returned features may be
either {\tt discrete} or {\tt real}, and a classifier may return either a
single feature (as on line 13) or multiple features (as indicated on line 3
with the {\tt \%} symbol).  When a classifier can return multiple features, we
call it a \emph{feature generator}.

On the right hand side of the left arrow is placed a classifier expression.
There are many types of classifier expression, and the two most common are on
display in this figure.  {\tt BagOfWords} and {\tt NewsgroupLabel} are defined
with hard-coded classifier expressions, while {\tt NewsgroupClassifier} is
defined with a learning classifier expression.  When hard-coding the behavior
of a classifier, the programmer has Java 1.4 syntax at his disposal to aid in
computing his features' values, plus some additional syntactic sugar to make
that type of computation easier.
%
% TODO; Forward link to somewhere that talks about syntactic sugar in
% hard-coded classifiers.
%
For example, the {\tt sense} statement on line 9 creates a feature which will
eventually be returned, but execution of the method continues so that multiple
features can be ``sensed.''  Note that only feature generators can use the
{\tt sense} statement, and only classifier returning a single feature can use
Java's {\tt return} statement (as on line 13).

After everything is said and done, we end up with two hard-coded classifiers.
One is a simple, one feature classifier that merely returns the value of the
{\tt Post.newsgroup} field (via the {\tt getNewsgroup()} method, since {\tt
Post.newsgroup} is private).  The other loops over all the words in the post
returning each as a separate feature.

\subsection{Learners}

{\tt NewsgroupClassifier} on line 15 of Figure \ref{fig:lbj20news} is not
specified in the usual, procedural way, but instead as the output of a
learning algorithm applied to data.  The verbose learning classifier
expression syntax says that this classifier will {\tt learn} to mimic an
oracle (line 16), {\tt using} some feature extraction classifiers (line 17),
{\tt from} some example objects (line 18), {\tt with} a learning algorithm
(lines 19 through 25).  The expression ends with the {\tt end} keyword (line
26).  In this case, the oracle is {\tt NewsgroupLabel}, the only feature
extraction classifier is {\tt BagOfWords}, the example objects come from {\tt
NewsgroupParser}, and the learning algorithm is {\tt SparseNetworkLearner}.
We explore each of these ideas in more detail below.

\begin{description}

\item[{\tt learn}]\hfill\\
%
{\justify We say that {\tt NewsgroupClassifier} is trying to mimic {\tt
NewsgroupLabel} because it will attempt to return features with the same
values and for the same example objects that \path{NewsgroupLabel} would have
returned them.  Note that the particular feature values being returned have
not been mentioned; they are induced by the learning algorithm from the data.
We need only make sure that the return type of the label classifier is
appropriate for the selected learning algorithm.}

\item[{\tt using}]\hfill\\
%
The argument to the {\tt using} clause is a single classifier expression.  As
we can see from this example code, the name of a classifier qualifies.  The
only restriction is that this classifier expression must have an input type
that allows it to take instances of {\tt NewsgroupClassifier}'s input type.
LBJava also provides a comma operator for constructing a feature generator that
simply returns all the features returned by the classifiers on either side of
the comma.  This way, we can include as many features as we want simply by
listing classifiers separated by commas.
%
% TODO: forward reference to a section about composite generators

\item[{\tt from}]\hfill\\
%
The {\tt from} clause supplies a data source by instantiating a parser.  The
objects returned by this parser's {\tt next()} method must be instances of
{\tt NewsgroupClassifier}'s input type.  LBJava can then extract features via the
using clause and train with the learning algorithm.  This clause also gives
the programmer the opportunity to iterate over the training data if he so
desires.  The optional {\tt rounds} clause is part of the {\tt from} clause,
and it specifies how many times to iterate.

\item[{\tt with}]\hfill\\
%
The argument to the \path{with} clause names a learning algorithm (any class
extending \path{lbjava.learn.Learner} accessible on the \path{CLASSPATH}) and
allows the programmer to set its parameters.  For example, \path{learningRate}
(line 22) and \path{thickness} (line 23) are parameters of the
\path{SparseAveragedPerceptron} learning algorithm, while \path{baseLTU} (line
24) is a parameter of the \path{SparseNetworkLearner} learning algorithm.

\end{description}

From these elements, the LBJava compiler generates Java source code that performs
feature extraction, applies that code on the example objects to create
training examples, and trains our learner with them.  The resulting learner
is, in essence, a Java method that takes an example object as input and
returns the predicted newsgroup in a string as output.  Note that the code
does not specify the possible newsgroup names or any other particulars about
the content of our example objects.  The only reason that this LBJava code
results in a newsgroup classifier is because we give it training data that
induces one.  If we want a spam detector instead, we need only change data
sources; the LBJava code need not change.\footnote{Of course, we may want to
change the names of our classifiers in that case for clarity's sake.}

%\section{Importing an External Classifier} <- maybe this goes with the above.

\section{Using {\tt NewsgroupClassifier} in a Java Program}

\section{Compiling Our Learning Based Program with LBJava}

