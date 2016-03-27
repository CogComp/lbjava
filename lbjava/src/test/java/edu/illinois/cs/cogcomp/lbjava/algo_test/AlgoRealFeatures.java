package edu.illinois.cs.cogcomp.lbjava.algo_test;

import edu.illinois.cs.cogcomp.lbjava.classify.*;

/**
 * This class is for <code>Real</code> type features
 * Features are dense and take literal values
 *
 * @author Yiming Jiang
 */
public class AlgoRealFeatures extends Classifier
{
    /**
     * Constructor
     */
    public AlgoRealFeatures()
    {
        containingPackage = "";
        name = "AlgoRealFeatures";
    }

    /**
     * Input type is <code>AlgoData</code>
     * @return input type
     */
    public String getInputType() { return "AlgoData"; }

    /**
     * Output type is <code>real[]</code>
     * @return output type
     */
    public String getOutputType() { return "real[]"; }

    /**
     * Classification given an example
     * @param __example example
     * @return feature vector
     */
    public FeatureVector classify(Object __example)
    {
        if (!(__example instanceof AlgoData))
        {
            String type = __example == null ? "null" : __example.getClass().getName();
            System.err.println("Classifier 'AlgoRealFeatures(AlgoData)' defined on line 8 of CL.lbj received '" + type + "' as input.");
            new Exception().printStackTrace();
            System.exit(1);
        }

        AlgoData d = (AlgoData) __example;

        FeatureVector __result;
        __result = new FeatureVector();
        int __featureIndex = 0;
        double __value;

        for (int i = 0; i < d.getFeatures().length; i++)
        {
            __value = d.getFeatures()[i];
            __result.addFeature(new RealArrayStringFeature(this.containingPackage, this.name, "", __value, __featureIndex++, 0));
        }

        for (int __i = 0; __i < __result.featuresSize(); ++__i)
            __result.getFeature(__i).setArrayLength(__featureIndex);

        return __result;
    }

    /**
     * Array of real values
     * @param __example example
     * @return real value
     */
    public double[] realValueArray(Object __example)
    {
        return classify(__example).realValueArray();
    }

    /**
     * Classification of an example to a feature vector
     * @param examples example vector
     * @return feature vector
     */
    public FeatureVector[] classify(Object[] examples)
    {
        if (!(examples instanceof AlgoData[]))
        {
            String type = examples == null ? "null" : examples.getClass().getName();
            System.err.println("Classifier 'AlgoRealFeatures(AlgoData)' defined on line 8 of CL.lbj received '" + type + "' as input.");
            new Exception().printStackTrace();
            System.exit(1);
        }

        return super.classify(examples);
    }

    public int hashCode() { return "AlgoRealFeatures".hashCode(); }
    public boolean equals(Object o) { return o instanceof AlgoRealFeatures; }
}
