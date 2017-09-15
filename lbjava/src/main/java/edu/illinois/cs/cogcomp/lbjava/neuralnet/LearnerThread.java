/**
 * This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 *
 * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.illinois.cs.cogcomp.lbjava.neuralnet;

import java.util.Arrays;

import edu.illinois.cs.cogcomp.lbjava.neuralnet.ThreadedNNTrainer.Range;

/**
 * This thread will compute a single activtion value, for each layer
 * setup must be called to provide the output array, the layer and the
 * input values.
 * @author redman
 */
class LearnerThread extends PushThread {
    
    /** the input error from the next layer being back propogated. */
    float[] error = null;
    
    /** the input labeled data. */
    float[] input = null;
    
    /** the input data. */
    float[] output = null;
    
    /** the result error SHARED ACROSS THREADS, must be synced to update. */
    float [] nextError;
    
    /** the space where updates to the errors will be set, later used to update nextError.*/
    float [] errorWorkspace;
    
    /** the learning rate. */
    float learnRate;
    
    /** the momentum. */
    float momentum;
    
    /** the layer we are operating on. */
    Layer layer = null;;
    
    /** the unique id. */
    private static int inc = 0;
    
    /**
     * The learning rate and momentum will not change, so we will take them initially.
     * @param lR the learning rate.
     * @param m the momentum.
     * @param mux the multiplexer.
     */
    LearnerThread(float lR, float m) {
        super("LearnerThread-"+(inc++));
        this.learnRate = lR;
        this.momentum = m;
    }
    
    /** 
     * before we start a layer, this is called to set up the thread.
     * @param error the error from the next layer, used to calc this layers error.
     * @param input the input data.
     * @param output the result data.
     * @param nextError put the next layers input error here.
     * @param layer the layer we operate on.
     */
    void setup(float [] error, float [] input, float [] output, float[] nextError, Layer layer) {
        this.error = error;
        this.input = input;
        this.output = output;
        this.nextError = nextError;
        this.layer = layer;
        this.errorWorkspace = new float[nextError.length];
        Arrays.fill(this.errorWorkspace, 0);
    }
    
    /**
     * Run till we complete the layer, then finish up.
     */
    public void run() {
        synchronized (this) {
            while(true) {
                
                // wait for the range object to be set.
                Range r = this.getRange();
                if (r == null)
                    return;
                for (int indx = r.start; indx < r.end; indx++) {
                    layer.trainOne(error, input, output, learnRate, momentum, errorWorkspace, indx);
                }
            }
        }
    }
}
