package edu.illinois.cs.cogcomp.lbjava.classify;

import edu.illinois.cs.cogcomp.lbjava.parse.Parser;

/**
 * This class is a program that can evaluate any {@link Classifier}
 * against an oracle {@link Classifier} on the objects returned from
 * a {@link Parser}.
 *
 * In particular, this class is for {@code real} type, i.e. regression.
 * For {@code discrete} type, refer class {@link TestDiscrete}.
 *
 * @author Yiming Jiang
 */
public class TestReal {

    /**
     * Tests the given {@code real} classifier against the given oracle
     * using the given {@link Parser} to provide the {@code real} labeled testing data.
     *
     * This method uses root-mean-square error as the evaluation criteria.
     *
     * @param classifier The classifier to be tested
     * @param oracle The classifier to test against
     * @param parser The parser supplying the labeled example objects
     * @param output Whether or not to produce output on {@code stdout}
     */
    public static void testReal(Classifier classifier,
                                Classifier oracle,
                                Parser parser,
                                boolean output) {

        double sum = 0;
        int count = 0;
        double gold, prediction;
        Object example = parser.next();

        for (; example != null; example = parser.next()) {

            gold = oracle.realValue(example);
            prediction = classifier.realValue(example);

            sum += Math.pow((prediction-gold), 2);
            count ++;

            if (output) {
                System.out.printf("[example %d] Gold: %f \t Oracle: %f\n\n", count, gold, prediction);
            }
        }
        double rmse = Math.sqrt(sum/count);
        System.out.printf("Root-mean-square error is: %f\n", rmse);
    }
}