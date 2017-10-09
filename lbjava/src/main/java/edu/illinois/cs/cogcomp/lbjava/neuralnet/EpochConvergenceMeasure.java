/**
 * This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 *
 * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.illinois.cs.cogcomp.lbjava.neuralnet;

/**
 * Terminate agfter so many iterations.
 * @author redman
 */
public class EpochConvergenceMeasure implements ConvergenceMeasure {

    /** the current epoch count. */
    private int epoch = 0;
    
    /** the current epoch count. */
    private int max;
    
    /**
     * Takes the number of iterations.
     * @param m the max iterations.
     */
    public EpochConvergenceMeasure(int m) {
        this.max = m;
    }
    
    /**
     * @see edu.illinois.cs.cogcomp.lbjava.neuralnet.ConvergenceMeasure#evaluate(edu.illinois.cs.cogcomp.lbjava.neuralnet.NNTrainingInterface)
     */
    @Override
    public boolean evaluate(NNTrainingInterface learner) {
        epoch++;
        if (epoch > max) {
            return true;
        } else
            return false;
    }

}
