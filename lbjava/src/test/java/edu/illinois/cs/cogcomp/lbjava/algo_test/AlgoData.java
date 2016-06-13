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
package edu.illinois.cs.cogcomp.lbjava.algo_test;

import java.util.Arrays;

/**
 * This is the data structure for each example, with feature vector and a label
 *
 * This is only for classification
 *
 * @author Yiming Jiang
 */
public class AlgoData {

    private double [] featuresVector;
    private String label;

    /**
     * Constructor
     * @param f feature vector
     * @param l label value
     */
    public AlgoData(double[] f, double l) {
        featuresVector = f;
        label = String.valueOf(l);
    }

    /**
     * Getter, get feature vector
     * @return feature vector
     */
    public double[] getFeatures() {
        return featuresVector;
    }

    /**
     * Getter, get label value
     * @return label value
     */
    public String getLabel() {
        return label;
    }

    /**
     * print feature vector
     */
    public void printFeatures() {
        System.out.println(Arrays.toString(featuresVector));
    }

    /**
     * print label value
     */
    public void printLabel() {
        System.out.println(label);
    }
}
