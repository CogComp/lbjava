/**
 * This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 *
 * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.illinois.cs.cogcomp.lbjava.classify;

import edu.illinois.cs.cogcomp.lbjava.parse.Parser;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.io.PrintStream;
import java.util.Date;

/**
 * This class is a program that can evaluate any {@link Classifier} against an oracle
 * {@link Classifier} on the objects returned from a {@link Parser}, with different statistical
 * metrics.
 *
 * In particular, this class is for {@code real} type, i.e. regression. For {@code discrete} type,
 * refer class {@link TestDiscrete}.
 *
 * @author Yiming Jiang
 */
public class TestReal {

    // sum of (y - y_hat)^2
    protected double sumOfSquareOfGoldSubtractPrediction = 0;

    // array of (y - y_hat)
    protected DescriptiveStatistics goldSubtractPredictionStats = new DescriptiveStatistics();

    // array of abs(y - y_hat)
    protected DescriptiveStatistics goldSubtractPredictionAbsoluteStats =
            new DescriptiveStatistics();

    // array of y
    protected DescriptiveStatistics goldStats = new DescriptiveStatistics();

    /**
     * Tests the given {@code real} classifier against the given oracle using the given
     * {@link Parser} to provide the {@code real} labeled testing data.
     *
     * This method uses root-mean-square error as the evaluation criteria.
     *
     * @param tester An object of this class
     * @param classifier The classifier to be tested.
     * @param oracle The classifier to test against.
     * @param parser The parser supplying the labeled example objects.
     * @param output Whether or not to produce output on {@code stdout}.
     * @param outputGranularity The number of examples processed in between time stamp messages.
     */
    public static void testReal(TestReal tester, Classifier classifier, Classifier oracle,
            Parser parser, boolean output, int outputGranularity) {

        int processed = 1;
        long totalTime = 0;
        Runtime runtime = null;

        if (output && outputGranularity > 0) {
            runtime = Runtime.getRuntime();
            System.out.println("0 examples tested at " + new Date());
            System.out.println("Total memory before first example: " + runtime.totalMemory());

            Object example = parser.next();
            if (example == null)
                return;

            totalTime -= System.currentTimeMillis();
            double prediction = classifier.realValue(example);
            totalTime += System.currentTimeMillis();
            System.out.println("First example processed in " + (totalTime / 1000.0) + " seconds.");
            System.out.println("Total memory after first example: " + runtime.totalMemory());

            double gold = oracle.realValue(example);
            tester.reportPrediction(prediction, gold);

            for (example = parser.next(); example != null; example = parser.next(), ++processed) {
                if (processed % outputGranularity == 0) {
                    System.out.println(processed + " examples tested at " + new Date());
                }

                totalTime -= System.currentTimeMillis();
                prediction = classifier.realValue(example);
                totalTime += System.currentTimeMillis();

                gold = oracle.realValue(example);

                tester.reportPrediction(prediction, gold);
            }

            System.out.println(processed + " examples tested at " + new Date() + "\n");
        } else {
            if (output) {
                runtime = Runtime.getRuntime();
                System.out.println("Total memory before first example: " + runtime.totalMemory());
            }

            Object example = parser.next();
            if (example == null)
                return;

            totalTime -= System.currentTimeMillis();
            double prediction = classifier.realValue(example);
            totalTime += System.currentTimeMillis();

            if (output) {
                System.out.println("First example processed in " + (totalTime / 1000.0)
                        + " seconds.");
                System.out.println("Total memory after first example: " + runtime.totalMemory());
            }

            double gold = oracle.realValue(example);

            tester.reportPrediction(prediction, gold);

            for (example = parser.next(); example != null; example = parser.next(), ++processed) {
                totalTime -= System.currentTimeMillis();
                prediction = classifier.realValue(example);
                totalTime += System.currentTimeMillis();

                gold = oracle.realValue(example);

                tester.reportPrediction(prediction, gold);
            }
        }

        if (output) {
            System.out.println("Average evaluation time: " + (totalTime / (1000.0 * processed))
                    + " seconds\n");
            tester.printPerformace(System.out, processed);
        }
    }

    /**
     * Update internal book keeping of each prediction and gold
     * 
     * @param prediction prediction value
     * @param gold gold value
     */
    public void reportPrediction(double prediction, double gold) {
        // keep track of root mean squared error sum
        sumOfSquareOfGoldSubtractPrediction += Math.pow((prediction - gold), 2);

        // add y-y_hat into list
        goldSubtractPredictionStats.addValue(gold - prediction);

        // add |y-y_hat| into list
        goldSubtractPredictionAbsoluteStats.addValue(Math.abs(gold - prediction));

        // add y into list
        goldStats.addValue(gold);
    }

    /**
     * Write to PrintStream, with statistical information
     * 
     * @param out printstream
     * @param processed number of testing examples
     */
    public void printPerformace(PrintStream out, int processed) {
        out.println("Statistical Metrics");

        out.printf("Root Mean Squared Error: %f\n", getRootMeanSquaredError(processed));
        out.printf("Mean Squared Error: %f\n", getMeanSquaredError(processed));
        out.printf("Mean Absolute Error: %f\n", getMeanAbsoluteError(processed));
        out.printf("Median Absolute Error: %f\n", getMedianAbsoluteError());
        out.printf("Explained Variance: %f\n", getExplainedVariance());
        out.printf("R2 Score: %f\n", getR2Score());
    }

    /**
     * Compute Root Mean Squared Error
     * 
     * @param processed number of testing examples
     * @return RMSE
     */
    private double getRootMeanSquaredError(int processed) {
        return Math.sqrt(sumOfSquareOfGoldSubtractPrediction / processed);
    }

    /**
     * Compute Mean Squared Error
     * 
     * @param processed number of testing examples
     * @return MSE
     */
    private double getMeanSquaredError(int processed) {
        return sumOfSquareOfGoldSubtractPrediction / processed;
    }

    /**
     * Compute Mean Absolute Error
     * 
     * @param processed number of testing examples
     * @return MAE
     */
    private double getMeanAbsoluteError(int processed) {
        return goldSubtractPredictionAbsoluteStats.getSum() / processed;
    }

    /**
     * Compute Median Absolute Error
     * 
     * @return MedAE
     */
    private double getMedianAbsoluteError() {
        return goldSubtractPredictionAbsoluteStats.getPercentile(50);
    }

    /**
     * Compute Explained Variance The best possible score is 1.0, lower values are worse.
     * 
     * @return EV
     */
    private double getExplainedVariance() {
        double varianceOfGoldSubtractPredict = goldSubtractPredictionStats.getVariance();
        double varianceOfGold = goldStats.getVariance();
        return 1 - (varianceOfGoldSubtractPredict / varianceOfGold);
    }

    /**
     * Compute R2 score, also called Coefficient of Determination Best possible score is 1.0 and it
     * can be negative (because the model can be arbitrarily worse). A constant model that always
     * predicts the expected value of y, disregarding the input features, would get a R^2 score of
     * 0.0.
     * 
     * @return R2 score
     */
    private double getR2Score() {
        double sum = 0;
        double mean = goldStats.getMean();

        for (int i = 0; i < goldStats.getN(); i++) {
            sum += Math.pow((goldStats.getElement(i) - mean), 2);
        }

        return 1 - (sumOfSquareOfGoldSubtractPrediction / sum);
    }
}
