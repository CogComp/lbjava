package edu.illinois.cs.cogcomp.lbjava;

import edu.illinois.cs.cogcomp.lbjava.classify.Classifier;
import edu.illinois.cs.cogcomp.lbjava.classify.DiscretePrimitiveStringFeature;
import edu.illinois.cs.cogcomp.lbjava.classify.Feature;
import edu.illinois.cs.cogcomp.lbjava.classify.FeatureVector;

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
        return new DiscretePrimitiveStringFeature("", "", "", value,
                valueIndexOf(value), (short) allowableValues().length);
    }

    @Override
    public FeatureVector classify(Object o) {
        return new FeatureVector(featureValue(o));
    }
}
