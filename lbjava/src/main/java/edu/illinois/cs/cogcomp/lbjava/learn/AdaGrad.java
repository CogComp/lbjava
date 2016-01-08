import edu.illinois.cs.cogcomp.lbjava.classify.Feature;
import edu.illinois.cs.cogcomp.lbjava.classify.FeatureVector;
import edu.illinois.cs.cogcomp.lbjava.classify.RealPrimitiveStringFeature;
import edu.illinois.cs.cogcomp.lbjava.classify.ScoreSet;
import edu.illinois.cs.cogcomp.lbjava.learn.Learner;

import java.io.PrintStream;
import java.util.Arrays;

/**
 * AdaGrad - Adaptive Stochastic Gradient Method
 *
 *  AdaGrad alters the update to adapt based on historical information,
 *      so that frequent occurring features in the gradients get small learning rates
 *      and infrequent features get higher ones. The learner learns slowly from frequent
 *      features but "pays attention" to rate but informative features. In practice, this
 *      means that infrequently occurring features can be learned effectively along side
 *      more frequently occurring features.
 *
 *  A good reference for literature is:
 *      Duchi, John, Elad Hazan, and Yoram Singer.
 *      "Adaptive subgradient methods for online learning and stochastic optimization."
 *      The Journal of Machine Learning Research 12 (2011): 2121-2159.
 *      http://www.magicbroom.info/Papers/DuchiHaSi10.pdf
 *
 *  @author Yiming Jiang (yjiang16@illinois.edu)
 */
public class AdaGrad extends Learner{

    private double constantLearningRate = Math.pow(10, -2);
    private boolean areVectorsInitialized = false;

    /* variables associated with convergence check */
    private int numberOfExamplesSeen = 0;
    private double sumOfCostFunction = 0.0;
    private boolean isCheckConvergenceOn = false;

    private double[] diagonalVector;    // internal parameters: sum of squares of gradients
    private static double[] weightVector;      // internal parameters: hypothesis vector

    /**
     * AdaGrad's Learning Function:
     *      Each row of feature vector + label feed in as arguments;
     *      Update internal parameters;
     *
     *      Note:
     *      1. No bias; No Regularization; are implemented
     *      2. Loss Function used: Least Mean Square
     *          Q((x,y), w) = 1/2 * (y - w* x)^2
     *      3. Notations Explanations:
     *          * Feature Vector (exampleValues): feature vector parsed from data set
     *          * Label (labelValue): label parsed from data set
     *          * Weight Vector (weightVector): weight vector, internal parameter
     *          * Gradient (gradientVector): gradient vector, internal parameter
     *                  for LMS loss function, g_t = (w_t * x_t - y_t) x_t, where t stands for the t_th iteration
     *          * Diagonal Matrix (diagonalVector): diagonal matrix, internal parameter
     *                  sum of squares of gradients at feature j until time t;
     *
     * @param exampleFeatures indices for feature vector x
     * @param exampleValues values for feature vector x
     * @param exampleLabels index for label y
     * @param labelValues value for label y
     */
    @Override
    public void learn(int[] exampleFeatures, double[] exampleValues,
                      int[] exampleLabels, double[] labelValues) {

        System.out.println(Arrays.toString(exampleValues));

        int featureDimension = exampleFeatures.length;

        if (!areVectorsInitialized) {
            initializeVectors(featureDimension);
            areVectorsInitialized = true;
        }

        double labelValue = labelValues[0];

        double[] gradientVector = new double[featureDimension];

        /* Code block used to check convergence */
        int numberOfTrainingExamples = 1203;
        int outputGranularity = 100;

        if (numberOfExamplesSeen % (numberOfTrainingExamples*outputGranularity) == 0) {
            isCheckConvergenceOn = true;
            System.out.format("[iteration: %d]: %f\n",
                (numberOfExamplesSeen / numberOfTrainingExamples), sumOfCostFunction);
            sumOfCostFunction = 0.0;
        }

        if (isCheckConvergenceOn) {
            sumOfCostFunction += realValue(exampleFeatures, exampleValues) - labelValue;
        }

        numberOfExamplesSeen ++;
        /* Code block used to check convergence */

        double wDotProductX = 0.0;
        for(int i = 0; i < featureDimension; i++) {
            wDotProductX += weightVector[i] * exampleValues[i];  //compute w_t * x_t
        }
        double multiplier = wDotProductX - labelValue;          // compute w_t * x_t - y_t

        for(int i = 0; i < featureDimension; i++) {
            gradientVector[i] = multiplier * exampleValues[i];  // compute g_t = (w_t * x_t - y_t) x_t
            diagonalVector[i] = diagonalVector[i] + (gradientVector[i] * gradientVector[i]); // compute G_t = sum from 1 to t (g_t ^2)

            double denominator = Math.sqrt(diagonalVector[i]);
            if (denominator == 0) {
                denominator = Math.pow(10, -100);               // avoid denominator being 0
            }

            weightVector[i] = weightVector[i] -                               // update weight vector
                    (gradientVector[i] * constantLearningRate / denominator); // w_(t+1) = w_t - g_t * r/(G_t)^(1/2)
        }
    }

    /**
     * Simply computes the dot product of the weight vector and the example
     *
     * @param exampleFeatures  The example's array of feature indices.
     * @param exampleValues    The example's array of feature values.
     * @return The computed real value.
     **/
    @Override
    public double realValue(int[] exampleFeatures, double[] exampleValues) {
        double weightDotProductX = 0.0;
        for(int i = 0; i < exampleFeatures.length; i++) {
            weightDotProductX += weightVector[i] * exampleValues[i];
        }
        return weightDotProductX;
    }

    /**
     * Returns the classification of the given example as a single feature
     * instead of a {@link FeatureVector}.
     *
     * @param f  The features array.
     * @param v  The values array.
     * @return The classification of the example as a feature.
     **/
    @Override
    public Feature featureValue(int[] f, double[] v) {
        return
                new RealPrimitiveStringFeature(containingPackage, name, "",
                        realValue(f, v));
    }

    /**
     * Initialize internal parameters vector
     * @param size feature dimension
     */
    private void initializeVectors(int size) {
        diagonalVector = new double[size];
        weightVector = new double[size];
        for (int i = 0; i < size; i++) {
            diagonalVector[i] = 0;
            weightVector[i] = 0;
        }
    }

    /**
     * Getter - get weight vector
     * @return weight vector
     */
    public double[] getWeightVector() {
        return weightVector;
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
        return null;
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
     * Writes the learned function's internal representation as text.
     *
     * @param printStream  The output stream.
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


    public static final double defaultLearningRate = 0.1;       // constant default learning rate

    /**
     * The learning rate takes the default value, while the name of the
     * classifier gets the empty string.
     **/
    public AdaGrad() {
        this("");
    }

    /**
     * Sets the learning rate to the specified value, while the name of the
     * classifier gets the empty string.
     *
     * @param r  The desired learning rate value.
     **/
    public AdaGrad(double r) {
        this("", r);
    }

    /**
     * Constructor
     *
     * Sets all member variables to their associated settings.
     *
     * @param p  The settings of all parameters.
     **/

    public AdaGrad(Parameters p) {
        this("", p);
    }

    /**
     * Constructor
     *
     * The learning rate takes the default value.
     *
     * @param n  The name of the classifier.
     **/
    public AdaGrad(String n) {
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
    public AdaGrad(String n, double r) {
        super(n);
        Parameters p = new Parameters();
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
    public AdaGrad(String n, Parameters p) {
        super(n);
        setParameters(p);
    }

    /**
     * Sets the values of parameters that control the behavior of this learning
     * algorithm.
     *
     * @param p  The parameters.
     **/
    @Override
    public void setParameters(Parameters p) {

    }
}
