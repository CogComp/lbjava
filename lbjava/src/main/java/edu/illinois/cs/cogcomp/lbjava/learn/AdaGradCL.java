package edu.illinois.cs.cogcomp.lbjava.learn;

import edu.illinois.cs.cogcomp.lbjava.classify.*;
import edu.illinois.cs.cogcomp.lbjava.util.FVector;

import java.io.PrintStream;
import java.util.Arrays;

/**
 * This is the classification version of AdaGrad
 *
 * @author Yiming Jiang
 */
public class AdaGradCL extends Learner{

    /* eventual value <code>AdaGradCL</code> uses */
    protected double learningRateA;

    /* eventual loss function <code>AdaGradCL</code> uses */
    protected String lossFunctionA;

    private double[] diagonalVector;    // sum of squares of gradients
    private double[] weightVector;      // hypothesis vector
    private double[] gradientVector;    // gradient vector

    protected String[] allowableValues; // allowable values for label

    protected double threshold;         // margin

    /* default constant learning rate is 0.1 */
    public static final double defaultLearningRate = 0.1;

    /* default loss function is hinge loss */
    public static final String defaultLossFunction = "hinge";

    /* boolean flag to initialize internal data structures */
    private boolean areVectorsInitialized = false;

    /**
     * Constructor
     *
     * The learning rate takes the default value, while the name of the
     * classifier gets the empty string.
     **/
    public AdaGradCL() {
        this("");
    }

    /**
     * Constructor
     *
     * Sets the learning rate to the specified value, while the name of the
     * classifier gets the empty string.
     *
     * @param r  The desired learning rate value.
     **/
    public AdaGradCL(double r) {
        this("", r);
    }

    /**
     * Constructor
     *
     * Sets all member variables to their associated settings.
     *
     * @param p  The settings of all parameters.
     **/
    public AdaGradCL(Parameters p) {
        this("", p);
    }

    /**
     * Constructor
     *
     * The learning rate takes the default value.
     *
     * @param n  The name of the classifier.
     **/
    public AdaGradCL(String n) {
        this(n, defaultLearningRate);
    }

    /**
     * Constructor
     *
     * Set desired learning rate
     *
     * @param n  The name of the classifier.
     * @param r  The desired learning rate value.
     **/
    public AdaGradCL(String n, double r) {
        super(n);
        Parameters p = new Parameters();
        p.learningRateP = r;
        setParameters(p);
    }

    /**
     * Constructor
     *
     * Sets all member variables to their associated settings.
     *
     * @param n  The name of the classifier.
     * @param p  The settings of all parameters.
     **/
    public AdaGradCL(String n, Parameters p) {
        super(n);
        setParameters(p);
    }

    /**
     * Sets the values of parameters that control the behavior of this learning
     * algorithm.
     *
     * @param p  The parameters.
     **/
    public void setParameters(Parameters p) {
        learningRateA = p.learningRateP;
        lossFunctionA = p.lossFunctionP;
    }

    /**
     * Getter - get weight vector
     * @return weight vector
     */
    public double[] getWeightVector() {
        return weightVector;
    }

    /**
     * Getter - get loss function
     * @return "hinge" or "lms"
     */
    public String getLossFunction() {
        return lossFunctionA;
    }

    /**
     * Getter - get the constant learning rate
     * @return learning rate
     */
    public double getConstantLearningRate() {
        return learningRateA;
    }

    /**
     * Learn function, this is where examples gets computed based on the learning algorithms
     *
     * @param exampleFeatures  The example's array of feature indices.
     * @param exampleValues    The example's array of feature values.
     * @param exampleLabels    The example's label(s).
     * @param labelValues      The values of the labels.
     */
    @Override
    public void learn(int[] exampleFeatures, double[] exampleValues,
                      int[] exampleLabels, double[] labelValues) {

        assert exampleLabels.length == 1
                : "Example must have a single label.";
        assert exampleLabels[0] == 0 || exampleLabels[0] == 1
                : "Example has unallowed label value.";

        /* add an additional dimension to feature dimension on W to reduce computation complexities */
        int featureDimension = exampleFeatures.length + 1;

        if (!areVectorsInitialized) {
            initialize(featureDimension);
            areVectorsInitialized = true;
        }

        double y = 0;

        if (labelValues[0] == 0) {
            y = 1;
        }
        else if (labelValues[0] == 1) {
            y = -1;
        }

        /* hinge loss function */

        /* compute s = (w * x + theta) */
        double s = score(exampleFeatures, exampleValues);

        boolean didMakeAMistake = true;

        if (y * s > 1) {
            didMakeAMistake = false;
        }

        /*
            no mistake, g = 0
            otherwise, made a mistake, g = -y*x
                note: for the first n features, the gradient is -yx, for theta, it is -y
         */

        /* compute gradient vector */
        for (int i = 0; i < featureDimension-1; i++) {
            if (didMakeAMistake) {
                gradientVector[i] = (-1) * y * exampleValues[i];
            }
            else {
                gradientVector[i] = 0;
            }
        }
        if (didMakeAMistake) {
            gradientVector[featureDimension-1] = (-1) * y;
        }
        else {
            gradientVector[featureDimension-1] = 0;
        }

        /* compute diagonal vector, aka squares of gradient vector */
        for (int i = 0; i < featureDimension; i++) {

            /* compute G_t = sum from 1 to t (g_t ^2) */
            diagonalVector[i] = diagonalVector[i] + (gradientVector[i] * gradientVector[i]);

            double denominator = Math.sqrt(diagonalVector[i]);
            if (denominator == 0) {
                denominator = Math.pow(10, -100);               // avoid denominator being 0
            }

            /* update weight vector */
            if (didMakeAMistake) {
                /* w_(t+1) = w_t - g_t * r/(G_t)^(1/2) */
                weightVector[i] = weightVector[i] -
                        (gradientVector[i] * learningRateA / denominator);
            }
        }
    }

    /**
     * Initialize all internal parameters
     * @param size feature dimension
     */
    private void initialize(int size) {
        diagonalVector = new double[size];
        weightVector = new double[size];
        gradientVector = new double[size];
        for (int i = 0; i < size; i++) {
            diagonalVector[i] = 0;
            weightVector[i] = 0;
            gradientVector[i] = 0;
        }
        threshold = 1;
    }

    /**
     * Simply computes the dot product of the weight vector and the feature
     * vector extracted from the example object.
     *
     * @param exampleFeatures  The example's array of feature indices.
     * @param exampleValues    The example's array of feature values.
     * @return The computed feature (in a vector).
     **/
    @Override
    public FeatureVector classify(int[] exampleFeatures, double[] exampleValues) {
        return new FeatureVector(featureValue(exampleFeatures, exampleValues));
    }

    /**
     * Classify into two categories,
     * if >= 0, predict positive
     * if <  0, predict negative
     *
     * @param f  The features array.
     * @param v  The values array.
     * @return feature
     */
    @Override
    public Feature featureValue(int[] f, double[] v) {
        int index = score(f, v) >= 0 ? 0 : 1;
        return predictions.get(index);
    }

    /**
     * Produces a set of scores indicating the degree to which each possible
     * discrete classification value is associated with the given example
     * object.  Learners that return a <code>real</code> feature or more than
     * one feature may implement this method by simply returning
     * <code>null</code>.
     *
     * @param exampleFeatures  The example's array of feature indices
     * @param exampleValues    The example's array of values
     * @return A set of scores indicating the degree to which each possible
     *         discrete classification value is associated with the given
     *         example object.
     **/
    @Override
    public ScoreSet scores(int[] exampleFeatures, double[] exampleValues) {
        return null;
    }

    /**
     * Compute (wT dot x + theta)
     *
     * @param exampleFeatures feature indices
     * @param exampleValues feature values
     * @return the sum
     */
    public double score(int[] exampleFeatures, double[] exampleValues) {
        double weightDotProductX = 0.0;
        for(int i = 0; i < exampleFeatures.length; i++) {
            weightDotProductX += weightVector[i] * exampleValues[i];
        }
        weightDotProductX += weightVector[weightVector.length-1];
        return weightDotProductX;
    }

    /**
     * Compute the score given an object
     * @param example example object
     * @return score
     */
    public double score(Object example) {
        Object[] exampleArray = getExampleArray(example, false);
        return score((int[]) exampleArray[0], (double[]) exampleArray[1]);
    }

    /**
     * Create two labels for classification
     * @param l  A labeling classifier.
     */
    public void setLabeler(Classifier l) {
        if (!(l == null || l.allowableValues().length == 2)) {
            System.err.println(
                    "Error: " + name
                            + ": An LTU must be given a single binary label classifier.");
            new Exception().printStackTrace();
            System.exit(1);
        }

        super.setLabeler(l);
        allowableValues = l == null ? null : l.allowableValues();
        labelLexicon.clear();
        labelLexicon.lookup(
                new DiscretePrimitiveStringFeature(
                        l.containingPackage, l.name, "", allowableValues[0], (short) 0,
                        (short) 2),
                true);
        labelLexicon.lookup(
                new DiscretePrimitiveStringFeature(
                        l.containingPackage, l.name, "", allowableValues[1], (short) 1,
                        (short) 2),
                true);
        predictions = new FVector(2);
        createPrediction(0);
        createPrediction(1);
    }

    /**
     * Create two allowable values
     * @return string
     */
    public String[] allowableValues() {
        if (allowableValues == null) allowableValues = new String[]{ "*", "*" };
        return allowableValues;
    }

    /**
     * Write debug info into printStream
     * @param out  The output stream.
     */
    @Override
    public void write(PrintStream out) {
        out.println("=== AdaGrad Classification Begin ===");
        out.println("threshold: " + threshold);
        out.println("learning rate: " + learningRateA);
        out.println("weight vector");
        out.println(Arrays.toString(weightVector));
        out.println("=== AdaGrad Classification End ===");
    }

    /**
     * A container for all of <code>AdaGrad</code>'s configurable
     * parameters. Using instances of this class should make code
     * more readable and constructors less complicated.
     *
     * @author Yiming Jiang
     */
    public static class Parameters extends Learner.Parameters {
        /* the rate at which weights are updated */
        public double learningRateP;
        public String lossFunctionP; // "hinge" or "lms"

        public Parameters() {
            learningRateP = defaultLearningRate;
            lossFunctionP = defaultLossFunction;
        }
    }
}