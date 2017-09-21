/**
 * This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 *
 * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.illinois.cs.cogcomp.lbjava.examples.neuralnet;

import java.io.IOException;
import java.util.Random;

import edu.illinois.cs.cogcomp.lbjava.neuralnet.Activator;
import edu.illinois.cs.cogcomp.lbjava.neuralnet.Layer;
import edu.illinois.cs.cogcomp.lbjava.neuralnet.NNTrainingInterface;
import edu.illinois.cs.cogcomp.lbjava.neuralnet.SimpleNNTrainer;
import edu.illinois.cs.cogcomp.lbjava.neuralnet.ThreadedNNTrainer;

/**
 * This class will manage a neural network, it will train it up if necessary, create
 * and manage all the layers and nodes internally, and respond to activations.
 * @author redman
 */
public class NeuralNetwork implements Activator {

    /** debug flag. */
    static final boolean debug = false;
    
    /** the layers of the neural network. */
    private Layer[] layers;
    
    /** scales the weight deltas for each iteration. */
    static private float learningRate = .3f;
    
    /** this prevents local minimum capture. */
    static private float momentum = .6f;

    /** this prevents local minimum capture. */
    static private int hiddenLayerSize = 20;

    /** this prevents local minimum capture. */
    static private int epochs = 100;

    /** this prevents local minimum capture. */
    static private int threads = 1;

    /** 
     * parse the arguments.
     * @param args the command arguments.
     */
    static private void parseArgs(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-l"))
                learningRate = Float.parseFloat(args[++i]);
            else if (args[i].equals("-m"))
                momentum = Float.parseFloat(args[++i]);
            else if (args[i].equals("-e"))
                epochs = Integer.parseInt(args[++i]);
            else if (args[i].equals("-t"))
                threads = Integer.parseInt(args[++i]);
            else if (args[i].equals("-h"))
                hiddenLayerSize = Integer.parseInt(args[++i]);
            else if (args[i].equals("-help")) {
                System.out.println("-t the number of threads to deploy.\n-l the learning rate.\n-m momentum.\n-e number of epochs.\n-h hidden layer size.");
                System.exit(0);
            } else
                System.out.println("Unexpected argument : "+args[i]);
        }
    }
    /**
     * Given the number of input layers and outputs, and the sizes of all layers, 
     * set up an untrained neural net.
     * @param layerSizes the number of neurons in each layer, also corresponds to the number of outputs of that layer.
     * @param learningRate the learning rage.
     * @param momentum the momentum.
     */
    NeuralNetwork(int[] layerSizes) {
        layers = new Layer[layerSizes.length-1];

        // each layer has a number of inputs defined by the outputs of the previous layer, or
        // the number inputs passed in, outputs is the number of neurons in the layer since each 
        // neuron produces one output.
        Random r = new Random (1234);
        for (int i = 0; i < layerSizes.length-1; i++) {
            this.layers[i] = new Layer(layerSizes[i], layerSizes[i+1], r);
        }
    }
    
    /**
     * @see edu.illinois.cs.cogcomp.lbjava.neuralnet.Activator#activateLayers(float[], edu.illinois.cs.cogcomp.lbjava.neuralnet.Layer[])
     */
    @Override
    public float[] prediction(float[] inputs) {
        // set up our counts.
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
     * Train up the NN model given training data, a learner algorith,
     * and convergence criteria.
     * @param inputs the input data.
     * @param outputs the desired output.
     * @param learner the learning algorithm.
     * @param epochs number of iterations to run.
     * @param converge the convergence criteria.
     */
    public void train(float[][] inputs, float[][]outputs, NNTrainingInterface learner, int epochs) {
        if (inputs.length != outputs.length)
            throw new RuntimeException("There must be the same number of input data records and output data records to train.");
        learner.train(inputs, outputs, epochs);
    }
    
    /**
     * Test will try learning an XOR model.
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        parseArgs(args);
        float[][] examples = null;
        float[][] outputs = null;
        float[][] texamples = null;
        float[][] toutputs = null;
        int [] hls = null;
        int outputrange = 0;
        if (args.length != 0) {
            int [] thls = {28*28, hiddenLayerSize, 1};
            hls = thls;
            System.out.println("reading data from disk.");
            /*examples = DatasetReader.getExampleInputs("./data/NIST/train-images-idx3-ubyte");
            outputs = DatasetReader.getExampleOutputs("./data/NIST/train-labels-idx1-ubyte");
            texamples = DatasetReader.getExampleInputs("./data/NIST/t10k-images-idx3-ubyte");
            toutputs = DatasetReader.getExampleOutputs("./data/NIST/t10k-labels-idx1-ubyte");
            */
            BrownReader br = new BrownReader("data/brown/their-brown80.feat");
            examples = br.inputs;
            outputs = br.outputs;
            outputrange = br.outputCardinality;
            br = new BrownReader("data/brown/their-brown20.feat", examples[0].length, br.outputCardinality);
            texamples = br.inputs;
            toutputs = br.outputs;
            thls[0] = examples[0].length;
            thls[2] = outputs[0].length;
        } else {
            int [] thls = {2, 2, 1};
            hls = thls;
            examples = new float[][] { new float[] { 0, 0 }, new float[] { 0, 1 }, new float[] { 1, 0 }, new float[] { 1, 1 } };
            outputs = new float[][] { new float[] { 0 }, new float[] { 1 }, new float[] { 1 }, new float[] { 0 } };
            texamples = new float[][] { new float[] { 0, 0 }, new float[] { 0, 1 }, new float[] { 1, 0 }, new float[] { 1, 1 } };
            toutputs = new float[][] { new float[] { 0 }, new float[] { 1 }, new float[] { 1 }, new float[] { 0 } };
        }
        int good = 0;
        {
            System.out.println("Start run: epochs="+epochs+" lr="+learningRate+" mom="+momentum+" hidden="+hiddenLayerSize+" threads:"+threads);
            NeuralNetwork nn = new NeuralNetwork(hls);
            NNTrainingInterface learner = null;
            if (threads <= 1) {
            	    learner = new SimpleNNTrainer(nn.layers, learningRate, momentum);
            } else {
            	    learner = new ThreadedNNTrainer(nn.layers, learningRate, momentum);
            }
            long time = System.currentTimeMillis();
            learner.train(examples, outputs, epochs);
            time = (System.currentTimeMillis() - time)/1000l;
            System.out.format("Took %d to train up a simple model, on to testing.\n",time);
            System.out.println("\nCompute accuracy against training");
            
            // provide some output now.
            for (int inputIdx = 0; inputIdx < examples.length; inputIdx++) {
                float[] outs = nn.prediction(examples[inputIdx]);
                float pred = outs[0]*outputrange;
                float label = outputs[inputIdx][0]*outputrange;
                if (Math.round(pred) == Math.round(label)) {
                    good++;
                }
            }
            System.out.format("Of %d, %d were good, accuracy %.4f",examples.length, good, ((float)good/(float)examples.length));
            good = 0;
            System.out.println("\nCompute accuracy against hold out set.");
            
            // provide some output now.
            for (int inputIdx = 0; inputIdx < texamples.length; inputIdx++) {
                float[] outs = nn.prediction(texamples[inputIdx]);
                float pred = outs[0]*outputrange;
                float label = toutputs[inputIdx][0]*outputrange;
                if (Math.round(pred) == Math.round(label)) {
                    System.out.format("+ %d label %.10f pred %.10f\n", inputIdx,label,pred);
                    good++;
                } else {
                    System.out.format("- %d label %.10f pred %.10f\n", inputIdx,label,pred);
                }
            }
            System.out.format("Of %d, %d were good, accuracy %.4f",texamples.length, good, ((float)good/(float)texamples.length));
        }
    }
}
