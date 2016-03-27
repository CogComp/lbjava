/**
 * This software is released under the University of Illinois/Research and
 *  Academic Use License. See the LICENSE file in the root folder for details.
 * Copyright (c) 2016
 *
 * Developed by:
 * The Cognitive Computations Group
 * University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.illinois.cs.cogcomp.lbjava.algo_test.main;

import edu.illinois.cs.cogcomp.lbjava.algo_test.AlgoDataSet;
import edu.illinois.cs.cogcomp.lbjava.algo_test.AlgoDiscreteLabel;
import edu.illinois.cs.cogcomp.lbjava.algo_test.AlgoParser;
import edu.illinois.cs.cogcomp.lbjava.algo_test.classifiers.AdaGradCLClassifier;
import edu.illinois.cs.cogcomp.lbjava.classify.Classifier;
import edu.illinois.cs.cogcomp.lbjava.classify.TestDiscrete;
import edu.illinois.cs.cogcomp.lbjava.learn.BatchTrainer;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * This is main file to use <code>AdaGradCL</code> class
 */
public class AdaGradCLMain {
    public static void main(String [] args) {
        AlgoDataSet dataSet = new AlgoDataSet(10, 100, 500, 50000, false);
        AlgoParser trainingSet = new AlgoParser(dataSet, true);

        AdaGradCLClassifier adgcl = new AdaGradCLClassifier();
//        AdaGradCL.Parameters para = new AdaGradCL.Parameters();
//        para.learningRateP = 0.1;
//        adgcl.setParameters(para);

        BatchTrainer adgclTrainer = new BatchTrainer(adgcl, trainingSet);
        adgclTrainer.train(10);

        ByteArrayOutputStream sout = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(sout);
        adgcl.write(out);
        System.out.println(sout.toString());

        AlgoParser testingSet = new AlgoParser(dataSet, false);
        Classifier oracle = new AlgoDiscreteLabel();
        TestDiscrete.testDiscrete(new TestDiscrete(), adgcl, oracle, testingSet, true, 1000);
    }
}
