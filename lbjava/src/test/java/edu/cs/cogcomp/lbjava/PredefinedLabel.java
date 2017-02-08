/**
 * This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 *
 * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.cs.cogcomp.lbjava;

import edu.cs.cogcomp.lbjava.classify.Classifier;
import edu.cs.cogcomp.lbjava.classify.DiscretePrimitiveStringFeature;
import edu.cs.cogcomp.lbjava.classify.Feature;
import edu.cs.cogcomp.lbjava.classify.FeatureVector;

import java.util.Vector;

/**
 * A predefined label used for testing
 *
 * @author Christos Christodoulopoulos
 */
public class PredefinedLabel extends Classifier {

    @Override
    public String getOutputType() {
        return "discrete";
    }

    @Override
    public String discreteValue(Object o) {
        return "" + ((Double) ((Vector) o).get(0) > 0.5);
    }

    @Override
    public Feature featureValue(Object o) {
        String value = discreteValue(o);
        return new DiscretePrimitiveStringFeature("", "", "", value, valueIndexOf(value),
                (short) allowableValues().length);
    }

    @Override
    public FeatureVector classify(Object o) {
        return new FeatureVector(featureValue(o));
    }
}
