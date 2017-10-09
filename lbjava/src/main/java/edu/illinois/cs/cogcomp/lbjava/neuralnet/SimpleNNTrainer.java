/**
 * This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 *
 * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.illinois.cs.cogcomp.lbjava.neuralnet;

import java.util.Random;

/**
 * This class will simply learn up the NeuralNet layers, single threaded.
 * @author redman
 */
public class SimpleNNTrainer implements NNTrainingInterface {
    
    /** the layers of the neural network. */
    private Layer[] layers;
    
    /** scales the weight deltas for each iteration. */
    private float learningRate = .3f;
    
    /** this prevents local minimum capture. */
    private float momentum = .6f;

    /**
     * Need the layer data, learning rate and momentum.
     * @param l the layers of the neural net.
     * @param rate the learning rate.
     * @param mom the momentum.
     */
    public SimpleNNTrainer(Layer[] l, float rate, float mom) {
        this.layers = l;
        this.learningRate = rate;
        this.momentum = mom;
    }
    
    /**
     * given an input set of example, compute the output values, also return all the
     * activation values in between, return them all. The results will be in the last 
     * vector in the returned array.
     * @param inputs the inputs.
     * @return the activation energies from all layers/
     */
    public final float [] classify(float[] inputs) {
        
        int layerCount = layers.length;
        
        // storage for each output of each layer, and the error computed for each activation.
        float [][] activations = new float[layerCount][];
        
        // This array contains inputs from previous layer
        float [] currentinputs = inputs;
        for (int i = 0 ; i < layerCount ; i++) {
            
            // compute the activations for this layer.
            Layer layer = layers[i];
            activations[i] = layer.activate(currentinputs);
            currentinputs = activations[i];
        }
        return activations[layerCount-1];
    }
    
    /**
     * given an input set of example, compute the output values, also return all the
     * activation values in between, return them all. The results will be in the last 
     * vector in the returned array.
     * @param inputs the inputs.
     * @return the activation energies from all layers/
     */
    public final float [][] activate(float[] inputs) {
        
        int layerCount = layers.length;
        
        // storage for each output of each layer, and the error computed for each activation.
        float [][] activations = new float[layerCount][];
        
        // This array contains inputs from previous layer
        float [] currentinputs = inputs;
        for (int i = 0 ; i < layerCount ; i++) {
            
            // compute the activations for this layer.
            Layer layer = layers[i];
            activations[i] = layer.activate(currentinputs);
            currentinputs = activations[i];
        }
        return activations;
    }
    
    /**
     * Train with one example.
     * @param inputs input data.
     * @param outputs the labeled data.
     * @param epochs
     */
    public void train(float[] inputs, float[]outputs) {
        // storage for each output of each layer, and the error computed for each activation.
        float [][] activations = this.activate(inputs);

        // now we have all the activations.
        float[] calcOut = activations[activations.length-1];
        int errlen = calcOut.length;
        float [] error = new float[errlen];
        for (int i = 0; i < errlen; i++) {
            error[i] = outputs[i] - calcOut[i]; // negative error
        }
        for (int i = layers.length - 1; i > 0; i--) {
            error = layers[i].train(error,activations[i-1],activations[i], this.learningRate, this.momentum);
        }
        error = layers[0].train(error,inputs, activations[0], this.learningRate, this.momentum);
    }
    /**
     * Execute the given number of epochs, then exit whatever the error.
     * @param inputs the input examples.
     * @param outputs the labels.
     * @param layers
     */
    @Override
    public void train(float[][] inputs, float[][]outputs, int epochs) {

        // error checking. 
        if (inputs.length != outputs.length)
            throw new RuntimeException("There must be the same number of input data records and output data records.");
        int totalInputs = inputs.length;
        
        // set up our counts.
        int layerCount = layers.length;
        Random r = new Random(34565);
        for (int epoch = 0; epoch < epochs; epoch++) {
            for (int inindx = 0; inindx < totalInputs; inindx++) {
                int iI = r.nextInt(totalInputs);
                // storage for each output of each layer, and the error computed for each activation.
                float [][] activations = this.activate(inputs[iI]);

                // now we have all the activations.
                float[] calcOut = activations[layerCount-1];
                int errlen = calcOut.length;
                float [] error = new float[errlen];
                for (int i = 0; i < errlen; i++) {
                    error[i] = outputs[iI][i] - calcOut[i]; // negative error
                }
                for (int i = layers.length - 1; i > 0; i--) {
                    error = layers[i].train(error,activations[i-1],activations[i], this.learningRate, this.momentum);
                }
                error = layers[0].train(error,inputs[iI],activations[0], this.learningRate, this.momentum);
            }
        }
    }
}
