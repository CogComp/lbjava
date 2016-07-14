/**
 * 
 */
package edu.illinois.cs.cogcomp.lbjava.neuralnet;

/**
 * Data container for LBJava.
 * @author redman
 */
public class NeuralNetExample {
	
	/** the inputs. */
	public float[] inputs;
	
	/** the labeled data. */
	public float[] outputs;
	
	/** 
	 * create with inputs and outputs.
	 * 
	 * @param ins
	 * @param outs
	 */
	NeuralNetExample(float[] ins, float [] outs) {
		this.inputs = ins;
		this.outputs = outs;
	}
	
	/**
	 * Get the input features.
	 * @return input features.
	 */
	public float[] getInputFeatures() {
		return inputs;
	}

	/**
	 * @return the output features(truth data).
	 */
	public float[] getOutputLabels() {
		return outputs;
	}

}
