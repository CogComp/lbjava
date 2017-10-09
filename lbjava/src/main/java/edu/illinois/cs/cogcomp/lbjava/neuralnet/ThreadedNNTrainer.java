/**
 * This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 *
 * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.illinois.cs.cogcomp.lbjava.neuralnet;

import java.util.Arrays;
import java.util.Random;

/**
 * This class will simply learn up the NeuralNet layers, single threaded.
 * @author redman
 */
public class ThreadedNNTrainer implements NNTrainingInterface {

    /** the layers of the neural network. */
    private Layer[] layers;

    /** scales the weight deltas for each iteration. */
    private float learningRate = .3f;

    /** this prevents local minimum capture. */
    private float momentum = .6f;

    /** this is the number of threads we will use, by default, number of processors on the machine. */
    private int numThreads = Runtime.getRuntime().availableProcessors();

    /**
     * Need the layer data, learning rate and momentum.
     * @param l the layers of the neural net.
     * @param rate the learning rate.
     * @param mom the momentum.
     */
    public ThreadedNNTrainer(Layer[] l, float rate, float mom) {
        this.layers = l;
        this.learningRate = rate;
        this.momentum = mom;
    }

    /**
     * Need the layer data, learning rate and momentum.
     * @param l the layers of the neural net.
     * @param rate the learning rate.
     * @param mom the momentum.
     * @param numThreads number of threads to deploy.
     */
    public ThreadedNNTrainer(Layer[] l, float rate, float mom, int numThreads) {
        this.layers = l;
        this.learningRate = rate;
        this.momentum = mom;
        this.numThreads = numThreads;
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
     * Execute the given number of epochs, then exit whatever the error.
     * @param inputs the input examples.
     * @param outputs the labels.
     * @param epochs the number of iterations to perform.
     */
    @Override
    final public void train(float[][] inputs, float[][] outputs, int epochs) {
        // error checking. 
        if (inputs.length != outputs.length)
            throw new RuntimeException("There must be the same number of input data records and output data records.");

        // iterate this number of times.
        int numExamples = inputs.length;

        // For each layer, compute the ranges of indices to operate on. This will allow us to 
        // continue computing on a thread without handshakes.
        int ll = layers.length;
        Range[][] ranges = new Range[ll][];
        for (int i = 0; i < ll ; i++) {
            Layer l = layers[i];
            int no = l.getNumberOutputs();
            int increment = no / numThreads;
            int onsies;
            if (increment == 0) {
                onsies = no;
                ranges[i] = new Range[onsies];
            } else {
                onsies = no % numThreads;
                ranges[i] = new Range[numThreads];
            }
            int start = 0;
            for (int j = 0 ; j < ranges[i].length && start < no; j++) {
                int end = start + increment;
                if (onsies != 0) {
                    end++;
                    onsies--;
                }
                ranges[i][j] = new Range(start, end);
                start = end;
            }
        }
        
        // create the threads to run against the activation mux.
        ActThread[] actThreads = new ActThread[numThreads];
        for (int i = 0; i < numThreads; i++) {
            actThreads[i] = new ActThread();
            actThreads[i].start();
        }

        // create the threads to run against the activation mux.
        LearnerThread[] learnerThreads = new LearnerThread[numThreads];
        for (int i = 0; i < numThreads; i++) {
            learnerThreads[i] = new LearnerThread(this.learningRate, this.momentum);
            learnerThreads[i].start();
        }

        // set up our counts.
        int layerCount = layers.length;

        // storage for each output of each layer, and the error computed for each activation.
        float[][] activations = new float[layerCount][];
        for (int i = 0; i < layerCount; i++) {
            activations[i] = new float[layers[i].getNumberOutputs()];
        }

        Thread.yield();
        Thread.yield();
        Thread.yield();
        Random r = new Random(34565);

       // do the specified number of epochs.
        for (int epoch = 0; epoch < epochs; epoch++) {
            for (int inindx = 0; inindx < numExamples; inindx++) {                
                int iI = r.nextInt(numExamples);

                // zero activations
                for (int i = 0; i < layerCount; i++) {
                    Arrays.fill(activations[i], 0.0f);
                }

                // This array contains inputs from previous layer output
                float[] currentinputs = inputs[iI];

                // for each layer, do the activations.
                for (int i = 0; i < layerCount; i++) {
                    Layer layer = layers[i];

                    // set up the threads
                    float[] acts = activations[i];
                    int rl = ranges[i].length;
                    for (int j = 0; j < rl; j++) {
                        actThreads[j].setup(currentinputs, layer, acts);
                        actThreads[j].setRange(ranges[i][j]);
                    }

                    // wait for them to finish.
                    for (int j = 0; j < rl; j++) {
                        actThreads[j].waitIdle();
                    }
                    currentinputs = acts;
                }

                //////////////////////////////////
                // compute output errors.
                // now we have all the activations, lets do error propogation.
                float[] calcOut = activations[layerCount - 1];                
                int errlen = calcOut.length;
                float[] error = new float[errlen];
                for (int i = 0; i < errlen; i++) {
                    error[i] = outputs[iI][i] - calcOut[i]; // negative error
                }

                //////////////////////////////////
                // propogate the errors back and adjust weights.
                // now learn from out errors.
                for (int i = layerCount - 1; i > 0; i--) {
                    Layer layer = layers[i];
                    int nI = layer.getNumberInputs() + 1/*for the bias*/;
                    float[] nextError = new float[nI];

                    // set up the threads
                    int rl = ranges[i].length;
                    for (int j = 0; j < rl; j++) {
                        learnerThreads[j].setup(error, activations[i - 1], activations[i], nextError, layer);
                        learnerThreads[j].setRange(ranges[i][j]);
                    }

                    // wait for complete, then set up next layer run.
                    // wait for them to finish.
                    for (int j = 0; j < rl; j++) {
                        learnerThreads[j].waitIdle();
                    }

                    // now we must sum all the errors for each of the threads.
                    int esize = nextError.length;
                    for (int ei = 0; ei < esize; ei++) {
                        for (int j = 0; j < rl; j++) {
                            nextError[ei] += learnerThreads[j].errorWorkspace[ei];
                        }
                    }
                    error = nextError;
                }

                // The setup for the first layer is computed using the actual inputs, so we do this
                // a bit differently.
                Layer layer = layers[0];
                int rl = ranges[0].length;
                int nI = layer.getNumberInputs() + 1/*for the bias*/;
                float[] nextError = new float[nI];
                for (int j = 0; j < rl; j++) {
                    learnerThreads[j].setup(error, inputs[iI], activations[0], nextError, layer);
                    learnerThreads[j].setRange(ranges[0][j]);
                }

                // wait for complete, then set up next layer run.
                // wait for them to finish.
                for (int j = 0; j < rl; j++) {
                    learnerThreads[j].waitIdle();
                }
            }            
            
            // check for convergence.
            float sumerr = 0;
            for (int inputIdx = 0; inputIdx < outputs.length; inputIdx++) {
                
                // storage for each output of each layer, and the error computed for each activation.
                float [][] a = this.activate(inputs[inputIdx]);
                float[] outs = a[layerCount-1];                
                float pred = outs[0];
                float label = outputs[inputIdx][0];
                sumerr = pred > label ? pred - label : label - pred;
            }
            System.out.format("%d) error = %.18f\n",epoch,(sumerr/(float)outputs.length));          
        }
    }

    /** just holds range of datums to operate on. */
    static class Range {
        int start;
        int end;
        Range(int s, int e) {
            start = s;
            end = e;
        }
        public String toString() {
            return start+"-"+end;
        }
    }
    
    /**
     * this class coordinates the activities of a set of threads by handing
     * out indexes that need operated on in a threadsafe way. If a request is made
     * for an index, and non are available, the thread will wait until notified.
     * @author redman
     */
    static class Multiplexer {

        /** these are the ranges for the layer we operate on, these inited once and reused each epoch. */
        private Range[] ranges = null;
        
        /** the number of elements we are counting down from. */
        private int count = 0;

        /** number of threads operating. */
        private int waiting = 0;

        /** the number of threads sharing this multiplexer. */
        private int numThreads = 0;
        
        /**
         * We need the number of elements in the layer to operate on.
         * @param numThreads the total number of threads.
         */
        Multiplexer(int numThreads) {
            this.numThreads = numThreads;
        }

        /**
         * Start this process. This should be called by the main thread where 
         * coordination occures. This will be accessed by the done method.
         * @param ranges the range of indices to operate on.
         * @param compLock use this as a semaphor
         */
        synchronized void startAndWait(Range[] ranges) {
            this.count = 0;
            this.ranges = ranges;
            this.waiting = 0;
            this.notifyAll();
            while (waiting != numThreads) {
                try {
                    this.wait();
                } catch (InterruptedException e1) {
                }
            }
        }

        /**
         * get the next available index, or block till one is available.
         * @return the index.
         */
        synchronized Range getNextIndex() {
            while (ranges == null || count == ranges.length) {
                try {
                    this.waiting++;
                    if (waiting == numThreads)
                        this.notifyAll();
                    this.wait();
                    this.waiting--;
                } catch (InterruptedException e) {
                }
            }
            return ranges[count++];
        }
    }
}
