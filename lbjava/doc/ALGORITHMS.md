---
title: ALGORITHMS
---

# Learning Algorithms

Here is a list of learning algorithm in LBJava.

### Classification

* [AdaBoost](https://github.com/IllinoisCogComp/lbjava/blob/master/lbjava/src/main/java/edu/illinois/cs/cogcomp/lbjava/learn/AdaBoost.java)
* [AdaGrad]()
* [Binary MIRA](https://github.com/IllinoisCogComp/lbjava/blob/master/lbjava/src/main/java/edu/illinois/cs/cogcomp/lbjava/learn/BinaryMIRA.java)
* [Mux Learner](https://github.com/IllinoisCogComp/lbjava/blob/master/lbjava/src/main/java/edu/illinois/cs/cogcomp/lbjava/learn/MuxLearner.java)
* [Naive Bayes](https://github.com/IllinoisCogComp/lbjava/blob/master/lbjava/src/main/java/edu/illinois/cs/cogcomp/lbjava/learn/NaiveBayes.java)
* [Passive Aggressive](https://github.com/IllinoisCogComp/lbjava/blob/master/lbjava/src/main/java/edu/illinois/cs/cogcomp/lbjava/learn/PassiveAggressive.java)
* [Sparse Averaged Perceptron](https://github.com/IllinoisCogComp/lbjava/blob/master/lbjava/src/main/java/edu/illinois/cs/cogcomp/lbjava/learn/SparseAveragedPerceptron.java)
* [Sparse Confidence Weighted](https://github.com/IllinoisCogComp/lbjava/blob/master/lbjava/src/main/java/edu/illinois/cs/cogcomp/lbjava/learn/SparseConfidenceWeighted.java)
* [Sparse MIRA](https://github.com/IllinoisCogComp/lbjava/blob/master/lbjava/src/main/java/edu/illinois/cs/cogcomp/lbjava/learn/SparseMIRA.java)
* [Support Vector Machine](https://github.com/IllinoisCogComp/lbjava/blob/master/lbjava/src/main/java/edu/illinois/cs/cogcomp/lbjava/learn/SupportVectorMachine.java)
* [Sparse Perceptron](https://github.com/IllinoisCogComp/lbjava/blob/master/lbjava/src/main/java/edu/illinois/cs/cogcomp/lbjava/learn/SparsePerceptron.java)
* [Sparse Winnow](https://github.com/IllinoisCogComp/lbjava/blob/master/lbjava/src/main/java/edu/illinois/cs/cogcomp/lbjava/learn/SparseWinnow.java)
* [Stochastic Gradient Descent]()



### Regression

* [AdaGrad](https://github.com/IllinoisCogComp/lbjava/blob/master/lbjava/src/main/java/edu/illinois/cs/cogcomp/lbjava/learn/AdaGrad.java)
* [Stochastic Gradient Descent](https://github.com/IllinoisCogComp/lbjava/blob/master/lbjava/src/main/java/edu/illinois/cs/cogcomp/lbjava/learn/StochasticGradientDescent.java)

### Class Architecture Structure

* [`Learner`](https://github.com/IllinoisCogComp/lbjava/blob/master/lbjava/src/main/java/edu/illinois/cs/cogcomp/lbjava/learn/Learner.java) (abstract class)
    * [`LinearThresholdUnit`](https://github.com/IllinoisCogComp/lbjava/blob/master/lbjava/src/main/java/edu/illinois/cs/cogcomp/lbjava/learn/LinearThresholdUnit.java) (abstract class)
        * [`PassiveAggressive`](https://github.com/IllinoisCogComp/lbjava/blob/master/lbjava/src/main/java/edu/illinois/cs/cogcomp/lbjava/learn/PassiveAggressive.java)
        * [`SparsePerceptron`](https://github.com/IllinoisCogComp/lbjava/blob/master/lbjava/src/main/java/edu/illinois/cs/cogcomp/lbjava/learn/SparsePerceptron.java)
            * [`BinaryMIRA`](https://github.com/IllinoisCogComp/lbjava/blob/master/lbjava/src/main/java/edu/illinois/cs/cogcomp/lbjava/learn/BinaryMIRA.java)
            * [`SparseAveragedPerceptron`](https://github.com/IllinoisCogComp/lbjava/blob/master/lbjava/src/main/java/edu/illinois/cs/cogcomp/lbjava/learn/SparseAveragedPerceptron.java)
        * [`SparseConfidenceWeighted`](https://github.com/IllinoisCogComp/lbjava/blob/master/lbjava/src/main/java/edu/illinois/cs/cogcomp/lbjava/learn/SparseConfidenceWeighted.java)
        * [`SparseWinnow`](https://github.com/IllinoisCogComp/lbjava/blob/master/lbjava/src/main/java/edu/illinois/cs/cogcomp/lbjava/learn/SparseWinnow.java)
    * [`AdaBoost`](https://github.com/IllinoisCogComp/lbjava/blob/master/lbjava/src/main/java/edu/illinois/cs/cogcomp/lbjava/learn/AdaBoost.java)
    * [`AdaGrad`]()
    * [`MuxLearner`](https://github.com/IllinoisCogComp/lbjava/blob/master/lbjava/src/main/java/edu/illinois/cs/cogcomp/lbjava/learn/MuxLearner.java)
    * [`NaiveBayes`](https://github.com/IllinoisCogComp/lbjava/blob/master/lbjava/src/main/java/edu/illinois/cs/cogcomp/lbjava/learn/NaiveBayes.java)
    * [`SparseMIRA`](https://github.com/IllinoisCogComp/lbjava/blob/master/lbjava/src/main/java/edu/illinois/cs/cogcomp/lbjava/learn/SparseMIRA.java)
    * [`SupportVectorMachine`](https://github.com/IllinoisCogComp/lbjava/blob/master/lbjava/src/main/java/edu/illinois/cs/cogcomp/lbjava/learn/SupportVectorMachine.java)
    * [`SparseNetworkLearner`](https://github.com/IllinoisCogComp/lbjava/blob/master/lbjava/src/main/java/edu/illinois/cs/cogcomp/lbjava/learn/SparseNetworkLearner.java)    
        * [`MultiLabelLearner`](https://github.com/IllinoisCogComp/lbjava/blob/master/lbjava/src/main/java/edu/illinois/cs/cogcomp/lbjava/learn/MultiLabelLearner.java)
        
### Note on Binary & Multiclass Classification

##### Please use [`SparseNetworkLearner`](https://github.com/IllinoisCogComp/lbjava/blob/master/lbjava/src/main/java/edu/illinois/cs/cogcomp/lbjava/learn/SparseNetworkLearner.java) for both binary and multiclass classification.

##### Please avoid using learning algorithms, such as [`SparseWinnow`](https://github.com/IllinoisCogComp/lbjava/blob/master/lbjava/src/main/java/edu/illinois/cs/cogcomp/lbjava/learn/SparseWinnow.java), [`SparsePerceptron`](https://github.com/IllinoisCogComp/lbjava/blob/master/lbjava/src/main/java/edu/illinois/cs/cogcomp/lbjava/learn/SparsePerceptron.java), and [`SparseAveragedPerceptron`](https://github.com/IllinoisCogComp/lbjava/blob/master/lbjava/src/main/java/edu/illinois/cs/cogcomp/lbjava/learn/SparseAveragedPerceptron.java) directly.

The code snippets below demonstrated how to use learning algorithms inside [`SparseNetworkLearner`](https://github.com/IllinoisCogComp/lbjava/blob/master/lbjava/src/main/java/edu/illinois/cs/cogcomp/lbjava/learn/SparseNetworkLearner.java) programmatically, and how to set parameters accordingly.

#### Declarations in `.lbj` file, with only `SparseNetworkLearner`
```java
discrete SparseNetworkClassifier(Post post) <-
    learn NewsgroupLabel
    using BagOfWords

    with SparseNetworkLearner {}

end
```

#### Declarations in `.lbj` file, with `SparseAveragedPerceptron` inside `SparseNetworkLearner`
```java
discrete SAPClassifier(Post post) <-
    learn NewsgroupLabel
    using BagOfWords


    with SparseNetworkLearner {
        SparseAveragedPerceptron.Parameters p = new SparseAveragedPerceptron.Parameters();
        p.learningRate = .1;
        p.thickness = 3;
        baseLTU = new SparseAveragedPerceptron(p);
    }

end
```

### Programmatically use `SparseAveragedPerceptron` inside `SparseNetworkLearner`
```java
SparseNetworkClassifier swn = new SparseNetworkClassifier();
SparseNetworkLearner.Parameters snp = new SparseNetworkLearner.Parameters();
SparseAveragedPerceptron sap = new SparseAveragedPerceptron();
SparseAveragedPerceptron.Parameters sapp = new SparseAveragedPerceptron.Paramters();
sapp.learningRate = .1;
sapp.thickness = 3;
sap.setParameters(sapp);
snp.baseLTU = sap;
swn.setParameters(snp);
```