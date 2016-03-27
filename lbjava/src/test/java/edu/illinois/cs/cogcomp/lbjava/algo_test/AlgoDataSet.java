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

import java.util.*;

/**
 * This class generate CS 446 HW 3 data set, for classification
 *
 * @author Yiming Jiang
 */
public class AlgoDataSet {

    private double [][] x;
    private double []   y;

    private double trainingToTestingRadio = 0.8;

    private int totalNumberOfExamples = 0;
    private int numberOfTrainingExamples = 0;
    private int numberOfTestingExamples = 0;

    private Random randBinary;
    private Random randShuffle;

    /**
     * Constructor
     * @param l l attributes among m are significant
     * @param m number of attributes
     * @param n total number of attributes
     * @param k number of instances
     * @param isNoisy does the data set contain any noise
     *
     * Note:
     *      1. assume l <= m <= n
     *      2. feature vector x is k * n matrix
     *      3. label vector   y is k * 1 matrix
     */
    public AlgoDataSet(int l, int m, int n, int k, boolean isNoisy) {
        initialize(k, n);

        double [][] xx = new double[k][n];
        double [] yy = new double[k];

        int pNumberOfInstance = k / 2;

        /**
         * Positive Examples
         *
         *  High level description on generating positive examples:
         *
         *      - pick randomly and uniformly l attributes from x_1, ... x_m and set them to 1.
         *      - set other m-l attributes to 0.
         *      - set rest of n-m attributes to 1 uniformly with 0.5 probability
         */

        for (int i = 0; i < pNumberOfInstance; i++) {
            yy[i] =  1;
        }

        for (int i = 0; i < pNumberOfInstance; i++) {
            for (int j = m; j < n; j++) {
                xx[i][j] = generateRandomBinary();
            }
        }

        for (int i = 0; i < pNumberOfInstance; i++) {
            List<Integer> cands = randomPermutation(m);
            if (l >= 0) {
                for (int j = 0; j < l; j++) {
                    xx[i][cands.get(j)] = 1;
                }
            }
        }

        /**
         * Negative Examples
         *
         *  High level description on generating negative examples:
         *      - pick randomly and uniformly l-2 attributes from x_1, ... x_m and set them to 1.
         *      - set other m-l attributes to 0.
         *      - set the rest of n-m attributes to 1 uniformly with 0.5 probability.
         */

        for (int i = pNumberOfInstance; i < k; i++) {
            yy[i] =  -1;
        }

        for (int i = pNumberOfInstance; i < k; i++) {
            for (int j = m; j < n; j++) {
                xx[i][j] = generateRandomBinary();
            }
        }
        for (int i = pNumberOfInstance; i < k; i++) {
            List<Integer> cands = randomPermutation(m);
            if (l >= 3) {
                for (int j = 0; j < l - 2; j++) {
                    xx[i][cands.get(j)] = 1;
                }
            }
        }

        // permute the data set
        List<Integer> idx = randomPermutation(k);

        for (int i = 0; i < k ; i++) {
            y[i] = yy[idx.get(i)];
        }

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < k; j++) {
                x[j][i] = xx[idx.get(j)][i];
            }
        }
    }

    /**
     * Getter for feature vector
     * @return feature vector x
     */
    public double[][] getFeatures() {
        return x;
    }

    /**
     * Getter for label vector
     * @return label vector y
     */
    public double[] getLabels() {
        return y;
    }

    public int getTotalNumberOfExamples() {
        return totalNumberOfExamples;
    }

    public int getNumberOfTrainingExamples() {
        return numberOfTrainingExamples;
    }

    public int getNumberOfTestingExamples() {
        return numberOfTestingExamples;
    }

    /**
     * Initialize all internal variables
     * @param k number of instances
     * @param n number of attributes
     */
    private void initialize(int k, int n) {
        x = new double[k][n];
        y = new double[k];

        totalNumberOfExamples = k;
        numberOfTrainingExamples = (int)(totalNumberOfExamples * trainingToTestingRadio);
        numberOfTestingExamples = totalNumberOfExamples - numberOfTrainingExamples;

        randBinary = new Random(0); // given a seed of 0, to be deterministic
        randShuffle = new Random(0);
    }

    /**
     * Generate a list of random permutation of m
     * @param m the upper bound of the permutation
     * @return list of random permutation of m
     */
    private List<Integer> randomPermutation(int m ) {
        List<Integer> lst = new ArrayList<>();
        for (int i = 0; i < m; i++) {
            lst.add(i);
        }
        Collections.shuffle(lst, randShuffle);
        return lst;
    }

    /**
     * Generate 0 or 1 with 0.5 probability
     * @return either 0 or 1
     */
    private int generateRandomBinary() {
        return randBinary.nextInt(2);
    }

    public void printFeatures() {
        prettyPrint2DArray(x);
    }

    public void printLabels() {
        prettyPrint1DArray(y);
    }

    /**
     * Print 1D array nicely
     * @param array array to be printed
     */
    private void prettyPrint1DArray(double[] array) {
        System.out.println(Arrays.toString(array));
    }

    /**
     * Print 2D array nicely
     * @param array array to be printed
     */
    private void prettyPrint2DArray(double[][] array) {
        System.out.println(Arrays.deepToString(array).replace("], ", "]\n"));
    }
}

