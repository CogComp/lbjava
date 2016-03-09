package edu.illinois.cs.cogcomp.lbjava.examples.regression;

import edu.illinois.cs.cogcomp.lbjava.classify.Classifier;
import edu.illinois.cs.cogcomp.lbjava.classify.TestReal;
import edu.illinois.cs.cogcomp.lbjava.learn.BatchTrainer;
import edu.illinois.cs.cogcomp.lbjava.learn.Learner;
import edu.illinois.cs.cogcomp.lbjava.learn.StochasticGradientDescent;

public class SGDMain {
    public static void main(String[] args) {
        MyDataReader train = new MyDataReader(System.getProperty("user.dir")+"/data/regression/train.txt");

        StochasticGradientDescent learner = new SGDClassifier();
        StochasticGradientDescent.Parameters p = new StochasticGradientDescent.Parameters();
        p.learningRate = Math.pow(10, -11);
        learner.setParameters(p);

        BatchTrainer trainer = new BatchTrainer(learner, train);
        trainer.train(1000);

        MyDataReader test = new MyDataReader(System.getProperty("user.dir")+"/data/regression/test.txt");
        Classifier oracle = new MyLabel();
        TestReal.testReal(learner, oracle, test, true);
    }
}
