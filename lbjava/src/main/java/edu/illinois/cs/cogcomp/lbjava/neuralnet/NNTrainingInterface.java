/**
 * 
 */
package edu.illinois.cs.cogcomp.lbjava.neuralnet;

/**
 * @author redman
 */
public interface NNTrainingInterface {
	
    /**
     * Given a set of examples, and a set of desired outputs, train the network
     * represented by the provided network layers the provided number of epochs.
     * @param inputs the input data to train against.
     * @param outputs the desired outputs.
     * @param epochs the number of training iterations to run.
     */
    public void train(float[][] inputs, float[][]outputs, int epochs);
}