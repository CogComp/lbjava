/**
 * This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 *
 * Developed by: The Cognitive Computations Group University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.illinois.cs.cogcomp.lbjava.examples.setCover;

import java.io.PrintStream;
import edu.illinois.cs.cogcomp.lbjava.classify.DiscretePrimitiveStringFeature;
import edu.illinois.cs.cogcomp.lbjava.classify.FeatureVector;
import edu.illinois.cs.cogcomp.lbjava.classify.ScoreSet;
import edu.illinois.cs.cogcomp.lbjava.learn.Learner;

public abstract class DumbLearner extends Learner {
    public DumbLearner() {
        this("");
    }

    public DumbLearner(String n) {
        super(n);
    }

    public void learn(int[] exampleFeatures, double[] exampleValues, int[] exampleLabels,
            double[] labelValues) {}

    public FeatureVector classify(int[] exampleFeatures, double[] exampleValues) {
        return null;
    }

    public ScoreSet scores(int[] f, double[] v) {
        return scores(null);
    }

    public FeatureVector classify(Object example) {
        String prediction = scores(example).highScoreValue();
        return new FeatureVector(new DiscretePrimitiveStringFeature(containingPackage, name, "",
                prediction, valueIndexOf(prediction), (short) allowableValues().length));
    }

    public void write(PrintStream out) {}

    public int hashCode() {
        return name.hashCode();
    }

    public boolean equals(Object o) {
        return getClass().equals(o.getClass());
    }
}
