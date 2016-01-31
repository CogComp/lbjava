package edu.illinois.cs.cogcomp.lbjava;

import edu.illinois.cs.cogcomp.lbjava.learn.AdaGrad;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import static org.junit.Assert.*;

/**
 * Unit tests for <code>AdaGrad</code> class
 *
 * @author Yiming Jiang
 */
public class AdaGradTest {

    /* global instance of <code>AdaGrad</code> */
    AdaGrad learner;

    /**
     * Instantiate an instance of <code>AdaGrad</code>
     */
    @Before
    public void setUp() {
        learner = new AdaGrad();
    }

    /**
     * Test default learning rate
     */
    @Test
    public void testDefaultLearningRate() {
        double r = learner.getConstantLearningRate();
        assertEquals(0.1, r, 0);
    }

    /**
     * Test user specified learning rate
     */
    @Test
    public void testCustomLearningRate() {
        AdaGrad.Parameters p = new AdaGrad.Parameters();
        p.learningRateP = 0.0001;
        learner.setParameters(p);

        double r = learner.getConstantLearningRate();
        assertEquals(0.0001, r, 0);
    }

    /**
     * Test default loss function to use
     */
    @Test
    public void testDefaultLossFunction() {
        String loss = learner.getLossFunction();
        assertEquals("hinge", loss);
    }

    /**
     * Test user specified loss function, i.e. "lms" -> least-mean-square, to use
     */
    @Test
    public void testCustomLossFunction() {
        AdaGrad.Parameters p = new AdaGrad.Parameters();
        p.lossFunctionP = "lms";
        learner.setParameters(p);

        String loss = learner.getLossFunction();
        assertEquals("lms", loss);
    }

    /**
     * Test learning algorithm of <code>AdaGrad</code> using <code>hinge loss</code> function
     *
     * Check to see if weights are updated correctly, given some examples
     */
    @Test
    public void testHingeLossLearn() {

        /* set the constant learning rate to 1 */
        AdaGrad.Parameters p = new AdaGrad.Parameters();
        p.learningRateP = 1;
        learner.setParameters(p);

        /* Example 1: features {1, 1}, label {+1}
         *
         * Made a mistake - need to update weight vector
         */
        int[] exampleFeatures1 = {0, 1};
        double[] exampleValues1 = {1, 1};
        int[] exampleLabels1 = {0};
        double[] labelValues1 = {1};

        learner.learn(exampleFeatures1, exampleValues1, exampleLabels1, labelValues1);

        double[] w1 = learner.getWeightVector();
        double[] exp_w1 = {1, 1, 1};

        assertArrayEquals(exp_w1, w1, 0);

        /* Example 2: features {1, 0}, label {-1}
         *
         * Made a mistake - need to update weight vector
         */

        int[] exampleFeatures2 = {0, 1};
        double[] exampleValues2 = {1, 0};
        int[] exampleLabels2 = {0};
        double[] labelValues2 = {-1};

        learner.learn(exampleFeatures2, exampleValues2, exampleLabels2, labelValues2);

        double[] w2 = learner.getWeightVector();
        double[] exp_w2 = {0.292894, 1, 0.292894};

        assertArrayEquals(exp_w2, w2, 0.000001);

        /* Example 3: features {0, 1}, label {1}
         *
         * No mistake - no update on weight vector
         */

        int[] exampleFeatures3 = {0, 1};
        double[] exampleValues3 = {0, 1};
        int[] exampleLabels3 = {0};
        double[] labelValues3 = {1};

        learner.learn(exampleFeatures3, exampleValues3, exampleLabels3, labelValues3);

        double[] w3 = learner.getWeightVector();
        double[] exp_w3 = {0.292894, 1, 0.292894};

        assertArrayEquals(exp_w3, w3, 0.000001);
    }

    /**
     * This is a simple test to test for overfitting
     *
     * The <code>AdaGrad</code> is given with simple data set with 2 features and a label.
     * Train the data set for 30 iterations and
     * see if the algorithm can classify the same data set correctly.
     */
    @Test
    public void overfittingSimpleTest() {
        /**
         * static data set;
         * the first 2 numbers are 2 features and the last one is the label;
         * this data set is linearly separable
         */
        double [][] dataSet = new double[][]{
                {-2, -4, 1},
                {-2, 0, 1},
                {0, 2, 1},
                {-2, 2, 1},
                {0, 4, 1},
                {2, 2, -1},
                {2, -2, -1},
                {0, -4, -1},
                {2, -4, -1},
                {4, -2, -1}
        };

        int[] exampleFeatures = {0, 1};
        int[] exampleLabels = {0};

        double[] exampleValues = {0, 0};
        double[] labelValues = {0};

        /* train <code>AdaGrad</code> for 30 iterations */
        for (int i = 0; i < 30; i++) {
            exampleValues[0] = dataSet[i%10][0];
            exampleValues[1] = dataSet[i%10][1];
            labelValues[0] = dataSet[i%10][2];

            learner.learn(exampleFeatures, exampleValues, exampleLabels, labelValues);
        }

        /* test against the same data set */
        int correctNumber = 0;
        for (int i = 0; i < 10; i++) {
            exampleValues[0] = dataSet[i][0];
            exampleValues[1] = dataSet[i][1];

            double result = learner.realValue(exampleFeatures, exampleValues);

            if (result * dataSet[i][2] > 0) {
                correctNumber ++;
            }
        }
        assertEquals(10, correctNumber);
    }

    /**
     * This is a complete test to test overfitting in <code>AdaGrad</code>
     *
     * Data set consists of 10 examples, each with 2 features;
     * Each feature value is randomly generated from range [0, 10];
     *
     * A "correct" weight vector is randomly generated;
     * Each value is from range [0, 10];
     *
     * The hyperplane is set by taking the medium of w*x;
     * Almost half of examples are labeled as +1; the rest are labeled -1;
     *
     * Thus, the data set is linearly separable, while being random
     *
     * <code>AdaGrad</code> learning algorithm will train on this data set for 100 iterations.
     *
     * Then it will be tested using the same data set to see if all classifications are correct.
     */
    @Test
    public void overfittingCompleteTest() {

        /* set constant learning rate */
        AdaGrad.Parameters p = new AdaGrad.Parameters();
        p.learningRateP = 10;
        learner.setParameters(p);

        /* give a seed to rand */
        Random rand = new Random(0);

        /** create 10 examples, each with 2 features,
         *  with values randomly generated from [0, 10]
         */
        ArrayList<ArrayList<Double>> dataSet = new ArrayList<ArrayList<Double>>();

        for(int i = 0; i < 10; i++) {
            ArrayList<Double> eachExample = new ArrayList<Double>();
            eachExample.add((double) randInt(rand, 0, 10));
            eachExample.add((double) randInt(rand, 0, 10));
            eachExample.add(0.0);
            dataSet.add(eachExample);
        }

        /* randomly generate the "correct" weight vector */
        ArrayList<Double> weightVector = new ArrayList<>();
        weightVector.add((double) randInt(rand, 0, 10));
        weightVector.add((double) randInt(rand, 0, 10));
        weightVector.add((double) randInt(rand, 0, 10));

        /* compute all w*x and set the medium to the decision boundary */
        double[] resultVector = new double[10];
        for (int i = 0; i < 10; i++) {
            resultVector[i] = computeDotProduct(dataSet.get(i), weightVector);
        }
        Arrays.sort(resultVector);

        double medium = resultVector[4];

        /* for which example w*x >= medium, the label is set to +1, otherwise -1 */
        for (int i = 0; i < 10; i++) {
            if (computeDotProduct(dataSet.get(i), weightVector) >= medium) {
                dataSet.get(i).set(2, 1.0);
            }
            else {
                dataSet.get(i).set(2, -1.0);
            }
        }

        int[] exampleFeatures = {0, 1};
        int[] exampleLabels = {0};

        double[] exampleValues = {0, 0};
        double[] labelValues = {0};

        /* train <code>AdaGrad</code> for 100 iterations */
        for (int i = 0; i < 100; i++) {
            exampleValues[0] = dataSet.get(i % 10).get(0);
            exampleValues[1] = dataSet.get(i % 10).get(1);
            labelValues[0] = dataSet.get(i % 10).get(2);

            learner.learn(exampleFeatures, exampleValues, exampleLabels, labelValues);
        }

        /* test against the same data set */
        int correctNumber = 0;
        for (int i = 0; i < 10; i++) {
            exampleValues[0] = dataSet.get(i % 10).get(0);
            exampleValues[1] = dataSet.get(i % 10).get(1);

            double result = learner.realValue(exampleFeatures, exampleValues);

            if (result * dataSet.get(i % 10).get(2) > 0) {
                correctNumber ++;
            }
        }

        /* test if the all classifications are correct */
        assertTrue((correctNumber == 10));
    }

    /**
     * Compute the dot product of weight vector and feature vector
     * @param x feature vector
     * @param w weight vector
     * @return dot product result
     */
    private double computeDotProduct(ArrayList<Double> x, ArrayList<Double> w) {
        double result = 0.0;
        for (int i = 0; i < x.size()-1; i++) {
            result += x.get(i) * w.get(i);
        }
        result += w.get(w.size() - 1);
        return result;
    }

    /**
     * Returns a pseudo-random number between min and max, inclusive.
     * The difference between min and max can be at most
     * <code>Integer.MAX_VALUE - 1</code>.
     *
     * @param rand random instance
     * @param min minimim value
     * @param max maximim value.  Must be greater than min.
     * @return integer between min and max, inclusive.
     * @see java.util.Random#nextInt(int)
     *
     * Reference: http://stackoverflow.com/questions/20389890/generating-a-random-number-between-1-and-10-java
     */
    private int randInt(Random rand, int min, int max) {
        return rand.nextInt((max - min) + 1) + min;
    }
}