package edu.illinois.cs.cogcomp.lbjava.neuralnet;

/**
 * Implementations will activate all the layers of the net and 
 * produce a set of outputs. The one required method will return 
 * all the output values. 
 * @author redman
 */
public interface Activator {
    
    /**
     * Activate the provided layer, return the resulting outputs.
     * @param inputs the input data.
     * @param layer the layer to supply the inputs to.
     * @return the output values.
     */
    public float[] prediction(float[] inputs);
}