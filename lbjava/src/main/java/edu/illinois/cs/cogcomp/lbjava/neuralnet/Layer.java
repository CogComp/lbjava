/**
 * This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 *
 * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.illinois.cs.cogcomp.lbjava.neuralnet;

import java.io.PrintStream;
import java.util.Random;

/**
 * This is a layer in a neural net. it is characterized by a number of inputs
 * and a number of outputs. The neurons (perceptrons) are hidden within, I see
 * no reason to expose them. this allows the layer class to do all computations
 * across the entire layer in one pass, which is very efficient. Downside; Nothing
 * in this implementation will allow you to assign per neuron attributes. Also, the 
 * weights are represented by a primitive array, so only 32 bit indices meaning no 
 * more than 2 ^ 32 weights are allowed.
 * @author redman
 */
public class Layer {
	
	/** number of inputs to this layer. */
	private int numberInputs;
	
	/** the number of outputs from this layer. */
	private int numberOutputs;
	
    /** the neuron weights. */
    private float[] weights;
    
    /** the derived outputs. */
    private float[] dweights;
    
    /** collects output values. */
    private float[] outputs;

    /**
     * The layer constructed.
     * @param numIn the number of inputs.
     * @param numOut the number of outputs.
     */
    public Layer(int numIn, int numOut) {
        this(numIn, numOut, new Random());
        outputs = new float[numOut];
    }

    /**
     * The layer constructed.
     * @param numIn the number of inputs.
     * @param numOut the number of outputs.
     * @param r the random num generator.
     */
    public Layer(int numIn, int numOut, Random r) {
        this.numberInputs = numIn;
        this.numberOutputs = numOut;
        int wl = (numIn+1)*numOut;
        weights = new float[wl];
        dweights = new float[wl];
        for (int i = 0; i < wl; i++)
            weights [i] = (r.nextFloat() - 0.5f) * 4f;
        outputs = new float[numOut];
    }
    
    /**
     * Compute the sigmoid first derivative.
     * @param x the input value
     * @return the sigmoid
     */
    final private float sigmoid(float x) {
        return (float) (1.0 / (1.0 + Math.exp(-x)));
    }

    /**
     * @return the weights
     */
    public float[] getWeights() {
        return weights;
    }

    /**
     * @param weights the weights to set
     */
    public void setWeights(float[] weights) {
        this.weights = weights;
    }

    /**
     * @return the numberInputs
     */
    public int getNumberInputs() {
        return numberInputs;
    }

    /**
     * @param numberInputs the numberInputs to set
     */
    public void setNumberInputs(int numberInputs) {
        this.numberInputs = numberInputs;
    }

    /**
     * @return the numberOutputs
     */
    public int getNumberOutputs() {
        return numberOutputs;
    }

    /**
     * @param numberOutputs the numberOutputs to set
     */
    public void setNumberOutputs(int numberOutputs) {
        this.numberOutputs = numberOutputs;
    }
    
    /**
     * This granularity of method invocation is only necessary so parallelize
     * the process.
     * @param index the index of the input to compute the output for.
     * @param inputs the inputs.
     * @return the activation output.
     */
    final float computeOneOutput(int index, float[] inputs) {
        float result = 0.0f;
        int nI = this.numberInputs;
        int start = index * (nI+1);
        for (int k = 0 ; k < nI ; k++) {
            result += weights[start+k] * inputs[k];
        }
        result += weights[start+nI];
        return (float) sigmoid(result);
    }
    
    /**
     * Given a set of inputs, produce the set of activation
     * values.
     * @param inputs the inputs to produce the predictions for.
     * @return the set of predictions.
     */
    final public float[] activate(float[] inputs) {
        int nO = this.numberOutputs;
        float[] o = this.outputs;
        for (int j = 0 ; j < nO ; j++) {
        	o[j] = this.computeOneOutput(j, inputs);
        }
        return outputs;
    }

    /**
     * train up weights for just one output. Thread safety must be noted here, since everybody will be
     * updating the nextError array at the same time. To avoid doing repeated synchronizations which are 
     * expensive here, for multithreaded trainer, we pass in a dummy error array, update at will, then
     * the caller is responsible for synchronizing on the real one and updating the shared sum error array.
     * @param error the activation errors used to compute the backprop value.
     * @param input the input date.
     * @param output the computed output data.
     * @param learningRate the learning rate.
     * @param momentum the momentum
     * @param nextError the array where the error values will be updated
     * @param outIndex the output index;
     */
    final public void trainOne(float[] error, float[] input, float[] output, float learningRate, float momentum, float[] nextError, int outIndex) {
        int woffset = (this.numberInputs+1) * outIndex;
        float d = error[outIndex] * (output[outIndex] * (1 - output[outIndex]));
        for (int j = 0; j < this.numberInputs; j++) {
            int windx = woffset + j;
            nextError[j] += weights[windx] * d;
            float dw = input[j] * d * learningRate;
            weights[windx] += dweights[windx] * momentum + dw;
            dweights[windx] = dw;
        }
        
        // compute the error for the bias, the fake bias input is always 1.
        int windx = woffset + input.length;
        nextError[input.length] += weights[windx] * d;
        float dw = d * learningRate;
        weights[windx] += dweights[windx] * momentum + dw;
        dweights[windx] = dw;
    }
    
    /**
     * given a set of errors (errors from the next layer on), and adjust the weights
     * to do a gradient descent.
     * @param error the output errors.
     * @param input the input data.
     * @param output the desired output.
     * @param learningRate the rate of learning.
     * @param momentum helps to avoid local minima.
     * @return the errors from this layer.
     */
    final public float[] train(float[] error, float[] input, float[] output, float learningRate, float momentum) {
        int nI = this.numberInputs+1/*for the bias*/;
        float[] nextError = new float[nI];
        for (int i = 0; i < this.numberOutputs; i++) {
            //this.trainOne(error, input, output, learningRate, momentum, nextError, i);
            
            int woffset = nI * i;
            float d = error[i] * (output[i] * (1 - output[i]));
            for (int j = 0; j < this.numberInputs; j++) {
                int windx = woffset + j;
                nextError[j] += weights[windx] * d;
                float dw = input[j] * d * learningRate;
                weights[windx] += dweights[windx] * momentum + dw;
                dweights[windx] = dw;
            }
            
            // compute the error for the bias, the fake bias input is always 1.
            int windx = woffset + input.length;
            nextError[input.length] += weights[windx] * d;
            float dw = d * learningRate;
            weights[windx] += dweights[windx] * momentum + dw;
            dweights[windx] = dw;
        }
        return nextError;
    }
    
    /**
     * print out the weights.
     */
    public void print() {
        System.out.print(this.numberInputs+":"+this.numberOutputs);
        System.out.print(" ");
        for (float w : weights) {
            System.out.format(" %.8f",w);
        }
        System.out.print(" (");
        for (float w : dweights) {
            System.out.format(" %.8f",w);
        }
        System.out.println(")");
    }

    /**
     * @return the dweights
     */
    public float[] getDweights() {
        return dweights;
    }

    /**
     * @param dweights the dweights to set
     */
    public void setDweights(float[] dweights) {
        this.dweights = dweights;
    }

    /**
     * used for reporting mostely.
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("in : "+this.numberInputs+" out : "+this.numberOutputs);
        sb.append("\n");
        for (int i = 0; i < weights.length;) {
            for (int j = 0; j < this.numberInputs;j++,i++) {
                sb.append("  "+weights[i]);
                sb.append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

	/**
	 * Write the representation to a digital output stream.
	 * @param out the output stream for serialization.
	 */
	public void write(PrintStream out) {
		out.print(numberInputs);
		out.print(numberOutputs);
		out.print(weights.length);
        for (int i = 0; i < weights.length; ++i)
            out.print(weights[i]);
	}
}
