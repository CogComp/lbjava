package edu.illinois.cs.cogcomp.lbjava;

import static org.junit.Assert.*;

import edu.illinois.cs.cogcomp.lbjava.learn.AdaGrad;
import org.junit.Test;

/**
 * Unit test for AdaGrad
 *
 * @author Yiming Jiang
 */
public class AdaGradTest {

    @Test
    public void testLearn() throws Exception {
        AdaGrad learner = new AdaGrad();

        // feed in features (1000, 1200, 900) and label -1
        int[] exampleFeatures = {0, 1, 2};
        double[] exampleValues = {1000, 1200, 900, -1};
        int[] exampleLabels = {0};
        double[] labelValues = {-1};

        learner.learn(exampleFeatures, exampleValues, exampleLabels, labelValues);

        // compare weight vector
        double[] w = learner.getWeightVector();
        double[] exp = {-0.01, -0.01, -0.01};

        assertArrayEquals(exp, w, 0.0);
    }
}
