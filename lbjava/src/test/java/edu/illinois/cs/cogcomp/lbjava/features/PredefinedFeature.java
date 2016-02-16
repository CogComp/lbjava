package edu.illinois.cs.cogcomp.lbjava.features;

import edu.illinois.cs.cogcomp.lbjava.classify.Classifier;
import edu.illinois.cs.cogcomp.lbjava.classify.DiscretePrimitiveStringFeature;
import edu.illinois.cs.cogcomp.lbjava.classify.Feature;
import edu.illinois.cs.cogcomp.lbjava.classify.FeatureVector;

import java.util.Vector;

/**
 * A predefined feature extractor used for testing
 *
 * @author Christos Christodoulopoulos
 */
public class PredefinedFeature extends Classifier {
    private double threshold;

    public static final PredefinedFeature testFeature1 = new PredefinedFeature(0.5);
    public static final PredefinedFeature testFeature2 = new PredefinedFeature(0.7);

    public PredefinedFeature() {}

    public PredefinedFeature(double threshold) {
        this.threshold = threshold;
    }

    @Override
    public String getOutputType() {
        return "discrete%";
    }

    @Override
    public FeatureVector classify(Object o) {
        Vector instance = (Vector) o;

        FeatureVector featureVector = new FeatureVector();
        for (int i = 0; i < 100; ++i) {
            String id = "" + (i + 1);
            if (instance.get(i + 1) == null) continue;
            String value = getFeature((Double) instance.get(i + 1));
            Feature feature = new DiscretePrimitiveStringFeature("", "", id, value, valueIndexOf(value), (short) 0);
            featureVector.addFeature(feature);
        }

        return featureVector;
    }

    private String getFeature(double value) {
        return "" + (value > threshold);
    }
}
