/**
 * This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 *
 * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.illinois.cs.cogcomp.lbjava.learn;

import edu.illinois.cs.cogcomp.lbjava.classify.Feature;
import edu.illinois.cs.cogcomp.lbjava.classify.FeatureVector;
import edu.illinois.cs.cogcomp.lbjava.classify.RealPrimitiveStringFeature;
import edu.illinois.cs.cogcomp.lbjava.classify.ScoreSet;

import java.io.PrintStream;

/**
 * AdaGrad - Adaptive Stochastic Gradient Method
 *
 * AdaGrad alters the update to adapt based on historical information, so that frequent occurring
 * features in the gradients get small learning rates and infrequent features get higher ones. The
 * learner learns slowly from frequent features but "pays attention" to rate but informative
 * features. In practice, this means that infrequently occurring features can be learned effectively
 * along side more frequently occurring features.
 *
 * A good reference for literature is: Duchi, John, Elad Hazan, and Yoram Singer.
 * "Adaptive subgradient methods for online learning and stochastic optimization." The Journal of
 * Machine Learning Research 12 (2011): 2121-2159. http://www.magicbroom.info/Papers/DuchiHaSi10.pdf
 *
 * @author Yiming Jiang (yjiang16@illinois.edu)
 */
public class AdaGrad extends Learner {

    /* eventual value <code>AdaGrad</code> uses */
    protected double learningRateA;

    /* eventual loss function <code>AdaGrad</code> uses */
    protected String lossFunctionA;

    private double[] diagonalVector; // sum of squares of gradients
    private double[] weightVector; // hypothesis vector
    private double[] gradientVector; // gradient vector

    /* default constant learning rate is 0.1 */
    public static final double defaultLearningRate = 0.1;

    /* default loss function is hinge loss */
    public static final String defaultLossFunction = "hinge";

    /* boolean flag to initialize internal data structures */
    private boolean areVectorsInitialized = false;

    /**
     * Constructor
     *
     * The learning rate takes the default value, while the name of the classifier gets the empty
     * string.
     **/
    public AdaGrad() {
        this("");
    }

    /**
     * Constructor
     *
     * Sets the learning rate to the specified value, while the name of the classifier gets the
     * empty string.
     *
     * @param r The desired learning rate value.
     **/
    public AdaGrad(double r) {
        this("", r);
    }

    /**
     * Constructor
     *
     * Sets all member variables to their associated settings.
     *
     * @param p The settings of all parameters.
     **/

    public AdaGrad(Parameters p) {
        this("", p);
    }

    /**
     * Constructor
     *
     * The learning rate takes the default value.
     *
     * @param n The name of the classifier.
     **/
    public AdaGrad(String n) {
        this(n, defaultLearningRate);
    }

    /**
     * Constructor
     *
     * Set desired learning rate
     *
     * @param n The name of the classifier.
     * @param r The desired learning rate value.
     **/
    public AdaGrad(String n, double r) {
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
     * @param n The name of the classifier.
     * @param p The settings of all parameters.
     **/
    public AdaGrad(String n, Parameters p) {
        super(n);
        setParameters(p);
    }

    /**
     * Sets the values of parameters that control the behavior of this learning algorithm.
     *
     * @param p The parameters.
     **/
    public void setParameters(Parameters p) {
        learningRateA = p.learningRateP;
        lossFunctionA = p.lossFunctionP;
    }

    /**
     * Getter - get weight vector
     * 
     * @return weight vector
     */
    public double[] getWeightVector() {
        return weightVector;
    }

    /**
     * Getter - get loss function
     * 
     * @return "hinge" or "lms"
     */
    public String getLossFunction() {
        return lossFunctionA;
    }

    /**
     * Getter - get the constant learning rate
     * 
     * @return learning rate
     */
    public double getConstantLearningRate() {
        return learningRateA;
    }

    /**
     * AdaGrad's Learning Function: Each row of feature vector + label feed in as arguments; Update
     * internal parameters;
     *
     * Note: 1. No bias; No Regularization; are implemented
     *
     * 2. Loss Function used: - Hinge Loss Q((x, y), w) = max(0, 1 - y(w * x)) - Least Mean Square
     * Q((x, y), w) = 1/2 * (y - w * x)^2
     *
     * 3. Notations Explanations: * Feature Vector (exampleValues): feature vector parsed from data
     * set * Label (labelValue): label parsed from data set * Weight Vector (weightVector): weight
     * vector, internal parameter * Gradient (gradientVector): gradient vector, internal parameter
     * for Hinge loss function, g_t = - y_t x_t for LMS loss function, g_t = (w_t * x_t - y_t) x_t
     * where t stands for the t_th iteration * Diagonal Matrix (diagonalVector): diagonal matrix,
     * internal parameter sum of squares of gradients at feature j until time t;
     *
     * @param exampleFeatures indices for feature vector x
     * @param exampleValues values for feature vector x
     * @param exampleLabels index for label y
     * @param labelValues value for label y
     */
    @Override
    public void learn(int[] exampleFeatures, double[] exampleValues, int[] exampleLabels,
            double[] labelValues) {

        /* add an additional dimension to feature dimension on W to reduce computation complexities */
        int featureDimension = exampleFeatures.length + 1;

        if (!areVectorsInitialized) {
            initializeVectors(featureDimension);
            areVectorsInitialized = true;
        }

        double labelValue = labelValues[0];

        /* hinge loss function */

        /* compute (w * x + theta) */
        double wDotProductX = 0.0;
        for (int i = 0; i < featureDimension - 1; i++) {
            wDotProductX += weightVector[i] * exampleValues[i];
        }
        wDotProductX += weightVector[featureDimension - 1];

        /*
         * check if a mistake is made
         * 
         * if y(w * x + theta) > 1, no mistake, g = 0 otherwise, made a mistake, g = -y*x note: for
         * the first n features, the gradient is -yx, for theta, it is -y
         */
        boolean didMakeAMistake = true;

        if (wDotProductX * labelValue > 1) {
            didMakeAMistake = false;
        }

        /* compute gradient vector */
        for (int i = 0; i < featureDimension - 1; i++) {
            if (didMakeAMistake) {
                gradientVector[i] = (-1) * labelValue * exampleValues[i];
            } else {
                gradientVector[i] = 0;
            }
        }
        if (didMakeAMistake) {
            gradientVector[featureDimension - 1] = (-1) * labelValue;
        } else {
            gradientVector[featureDimension - 1] = 0;
        }

        /* compute diagonal vector, aka squares of gradient vector */
        for (int i = 0; i < featureDimension; i++) {

            /* compute G_t = sum from 1 to t (g_t ^2) */
            diagonalVector[i] = diagonalVector[i] + (gradientVector[i] * gradientVector[i]);

            double denominator = Math.sqrt(diagonalVector[i]);
            if (denominator == 0) {
                denominator = Math.pow(10, -100); // avoid denominator being 0
            }

            /* update weight vector */
            if (didMakeAMistake) {
                /* w_(t+1) = w_t - g_t * r/(G_t)^(1/2) */
                weightVector[i] =
                        weightVector[i] - (gradientVector[i] * learningRateA / denominator);
            }
        }
    }

    /**
     * Initialize internal parameters vector
     * 
     * @param size feature dimension
     */
    private void initializeVectors(int size) {
        diagonalVector = new double[size];
        weightVector = new double[size];
        gradientVector = new double[size];
        for (int i = 0; i < size; i++) {
            diagonalVector[i] = 0;
            weightVector[i] = 0;
            gradientVector[i] = 0;
        }
    }

    /**
     * Simply computes the dot product of the weight vector and the example
     *
     * @param exampleFeatures The example's array of feature indices.
     * @param exampleValues The example's array of feature values.
     * @return The computed real value.
     **/
    @Override
    public double realValue(int[] exampleFeatures, double[] exampleValues) {
        double weightDotProductX = 0.0;
        for (int i = 0; i < exampleFeatures.length; i++) {
            weightDotProductX += weightVector[i] * exampleValues[i];
        }
        weightDotProductX += weightVector[weightVector.length - 1];
        return weightDotProductX;
    }

    /**
     * Returns the classification of the given example as a single feature instead of a
     * {@link FeatureVector}.
     *
     * @param f The features array.
     * @param v The values array.
     * @return The classification of the example as a feature.
     **/
    @Override
    public Feature featureValue(int[] f, double[] v) {
        return new RealPrimitiveStringFeature(containingPackage, name, "", realValue(f, v));
    }

    /**
     * Simply computes the dot product of the weight vector and the feature vector extracted from
     * the example object.
     *
     * @param exampleFeatures The example's array of feature indices.
     * @param exampleValues The example's array of feature values.
     * @return The computed feature (in a vector).
     **/
    @Override
    public FeatureVector classify(int[] exampleFeatures, double[] exampleValues) {
        return null;
    }

    /**
     * Produces a set of scores indicating the degree to which each possible discrete classification
     * value is associated with the given example object. Learners that return a <code>real</code>
     * feature or more than one feature may implement this method by simply returning
     * <code>null</code>.
     *
     * @param exampleFeatures The example's array of feature indices
     * @param exampleValues The example's array of values
     * @return A set of scores indicating the degree to which each possible discrete classification
     *         value is associated with the given example object.
     **/
    @Override
    public ScoreSet scores(int[] exampleFeatures, double[] exampleValues) {
        return null;
    }

    /**
     * Writes the learned function's internal representation as text.
     *
     * @param printStream The output stream.
     **/
    @Override
    public void write(PrintStream printStream) {

    }

    /**
     * Returns a string describing the output feature type of this classifier.
     *
     * @return <code>"real"</code>
     **/
    @Override
    public String getOutputType() {
        return "real";
    }

    /**
     * A container for all of <code>AdaGrad</code>'s configurable parameters. Using instances of
     * this class should make code more readable and constructors less complicated.
     *
     * @author Yiming Jiang
     */
    public static class Parameters extends Learner.Parameters {
        /* the rate at which weights are updated */
        public double learningRateP;
        public String lossFunctionP; // "hinge" or "lms"

        /**
         * Constructor for <code>Parameters</code> class
         *
         * use defaultLearningRate if not specified
         */
        public Parameters() {
            learningRateP = defaultLearningRate;
            lossFunctionP = defaultLossFunction;
        }
    }
}
