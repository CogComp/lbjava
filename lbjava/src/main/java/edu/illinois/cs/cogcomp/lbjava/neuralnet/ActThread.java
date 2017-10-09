/**
 * This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 *
 * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.illinois.cs.cogcomp.lbjava.neuralnet;

import edu.illinois.cs.cogcomp.lbjava.neuralnet.ThreadedNNTrainer.Range;

/**
 * This thread will compute a single activtion value, for each layer
 * setup must be called to provide the output array, the layer and the
 * input values.
 * @author redman
 */
class ActThread extends PushThread {
    
    /** the input data. */
    float[] currentInputs = null;
    
    /** the layer we are operating on. */
    Layer layer = null;
    
    /** the resulting outputs are stored here, this array is shared
     * by all threads activating on this layer. */
    float [] layerActivations = null;
    
    /** used to make the name of the thread unique. */
    private static int inc = 0;
    
    /**
     * init with a mux.
     * @param m the multiplexer.
     */
    ActThread() {
        super("ActThread-"+(inc++));
    }
    
    /** 
     * before we start a layer, this is called to set up the thread.
     * @param ci the input data.
     * @param l the layer.
     * @param la the layer actvation values.
     * @param mux the multiplexer.
     */
    void setup(float[] ci, Layer l, float[] la) {
        this.currentInputs = ci;
        this.layer = l;
        this.layerActivations = la;
    }
        
    /**
     * Run forever never quite.
     */
    public void run() {
        synchronized (this) {
            while(true) {
                
                // wait for the range object to be set.
                Range r = this.getRange();
                if (r == null)
                    return;
                for (int indx = r.start; indx < r.end; indx++) {
                    layerActivations[indx] = layer.computeOneOutput(indx, currentInputs);
                }
            }
        }
    }
}
