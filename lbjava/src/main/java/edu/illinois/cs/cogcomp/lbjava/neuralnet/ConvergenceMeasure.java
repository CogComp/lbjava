/**
 * This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 *
 * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.illinois.cs.cogcomp.lbjava.neuralnet;

/**
 * Measure convergence, simplest implementation may simply run some number of epochs,
 * more sophosticated will look some function of cumulative error going to zero at the 
 * end of an epoch. Conversion is always measured at the end of a training cycle.
 * @author redman
 */
public interface ConvergenceMeasure {

    /**
     * With the given inputs and outputs, evaluate the results of the last iteration, 
     * determine the error, probably store that, and if convergence (what whatever measure)
     * is achieved, return true, else return false.
     * 
     * @param learner the learner being used to train up the neural net, contains the cummulative error.
     * @return true if converged.
     */
    public boolean evaluate(NNTrainingInterface learner);
}
