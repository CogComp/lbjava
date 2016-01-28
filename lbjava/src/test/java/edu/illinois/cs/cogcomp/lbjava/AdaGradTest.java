package edu.illinois.cs.cogcomp.lbjava;

import edu.illinois.cs.cogcomp.lbjava.learn.AdaGrad;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit Test Class for <code>AdaGrad</code> class
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
}