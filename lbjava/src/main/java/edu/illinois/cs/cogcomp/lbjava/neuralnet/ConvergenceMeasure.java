/**
 * 
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
