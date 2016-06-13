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

import edu.illinois.cs.cogcomp.lbjava.classify.*;

/**
 * This is the discrete label class, extends from Classifier
 *
 * @author Yiming Jiang
 */
public class AlgoDiscreteLabel extends Classifier
{
    /**
     * Constructor
     */
    public AlgoDiscreteLabel()
    {
        containingPackage = "";
        name = "AlgoDiscreteLabel";
    }

    /**
     * Input type is <code>AlgoData</code>
     * @return input type
     */
    public String getInputType() { return "AlgoData"; }

    /**
     * Output type is <code>discrete</code>
     * @return output type
     */
    public String getOutputType() { return "discrete"; }

    /**
     * Two allowable values, +1 and -1
     */
    private static String[] __allowableValues = new String[]{ "1.0", "-1.0" };
    public static String[] getAllowableValues() { return __allowableValues; }
    public String[] allowableValues() { return __allowableValues; }

    /**
     * Classification given an example
     * @param __example example
     * @return feature vector
     */
    public FeatureVector classify(Object __example)
    {
        return new FeatureVector(featureValue(__example));
    }

    /**
     * Feature value
     * @param __example example
     * @return feature
     */
    public Feature featureValue(Object __example)
    {
        String result = discreteValue(__example);
        return new DiscretePrimitiveStringFeature(containingPackage, name, "", result, valueIndexOf(result), (short) allowableValues().length);
    }

    /**
     * Compute discrete value
     * @param __example example
     * @return the predicted label string
     */
    public String discreteValue(Object __example)
    {
        if (!(__example instanceof AlgoData))
        {
            String type = __example == null ? "null" : __example.getClass().getName();
            System.err.println("Classifier 'AlgoDiscreteLabel(AlgoData)' defined on line 14 of CL.lbj received '" + type + "' as input.");
            new Exception().printStackTrace();
            System.exit(1);
        }

        String __cachedValue = _discreteValue(__example);

        if (valueIndexOf(__cachedValue) == -1)
        {
            System.err.println("Classifier 'AlgoDiscreteLabel' defined on line 14 of CL.lbj produced '" + __cachedValue  + "' as a feature value, which is not allowable.");
            System.exit(1);
        }

        return __cachedValue;
    }

    /**
     * Get discrete value from data set
     * @param __example example
     * @return gold label value
     */
    private String _discreteValue(Object __example)
    {
        AlgoData d = (AlgoData) __example;

        return "" + (d.getLabel());
    }

    /**
     * Classify into feature vector
     * @param examples examples
     * @return feature vector
     */
    public FeatureVector[] classify(Object[] examples)
    {
        if (!(examples instanceof AlgoData[]))
        {
            String type = examples == null ? "null" : examples.getClass().getName();
            System.err.println("Classifier 'AlgoDiscreteLabel(AlgoData)' defined on line 14 of CL.lbj received '" + type + "' as input.");
            new Exception().printStackTrace();
            System.exit(1);
        }

        return super.classify(examples);
    }

    public int hashCode() { return "AlgoDiscreteLabel".hashCode(); }
    public boolean equals(Object o) { return o instanceof AlgoDiscreteLabel; }
}
