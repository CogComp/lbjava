package edu.illinois.cs.cogcomp.lbjava.examples.regression;

import edu.illinois.cs.cogcomp.lbjava.classify.Classifier;
import edu.illinois.cs.cogcomp.lbjava.classify.TestReal;
import edu.illinois.cs.cogcomp.lbjava.learn.BatchTrainer;
import edu.illinois.cs.cogcomp.lbjava.learn.Learner;

public class SGDMain {
    public static void main(String[] args) {
        MyDataReader train = new MyDataReader(System.getProperty("user.dir")+"/data/regression/train.txt");

        // training
        Learner learner = new SGDClassifier();
        BatchTrainer trainer = new BatchTrainer(learner, train);
        trainer.train(1000);

        MyDataReader test = new MyDataReader(System.getProperty("user.dir")+"/data/regression/test.txt");
        Classifier oracle = new MyLabel();
        TestReal.testReal(learner, oracle, test, true);

//        MyDataReader train = new MyDataReader(System.getProperty("user.dir")+"/data/regression/train.txt");
//
//        for (MyData d = (MyData) train.next(); d != null; d = (MyData) train.next()) {
//            d.printData();
//        }
//        System.out.println("Working Directory = " + System.getProperty("user.dir"));
    }
}
