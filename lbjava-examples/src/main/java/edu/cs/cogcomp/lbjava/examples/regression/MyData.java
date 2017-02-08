/**
 * This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 *
 * Developed by: The Cognitive Computations Group University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.cs.cogcomp.lbjava.examples.regression;

import java.util.*;

/**
 * Data class containing feature vector and label
 *
 * @author Yiming Jiang
 */
public class MyData {

    /* feature vector */
    private List<Double> features;

    /* label */
    private double label;

    /**
     * Constructor
     * 
     * @param line string contains features and label
     *
     *        Note: - The last element is the label. - A single space seperates each feature and the
     *        label.
     *
     *        Example: line is string "1.0 2.0 3.0 -1" feature vector is [1.0, 2.0, 3.0] label is -1
     */
    public MyData(String line) {
        this.features = new ArrayList<>();

        for (String each : line.split(" ")) {
            features.add(Double.parseDouble(each));
        }

        label = features.get(features.size() - 1);
        features.remove(features.size() - 1);
    }

    /**
     * Getter for feature vector
     * 
     * @return feature vector
     */
    public List<Double> getFeatures() {
        return this.features;
    }

    /**
     * Getter for label
     * 
     * @return label
     */
    public double getLabel() {
        return label;
    }
}
