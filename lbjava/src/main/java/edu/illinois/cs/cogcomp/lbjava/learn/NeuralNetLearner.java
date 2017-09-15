/**
 * This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 *
 * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.illinois.cs.cogcomp.lbjava.learn;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Random;

import edu.illinois.cs.cogcomp.lbjava.classify.Feature;
import edu.illinois.cs.cogcomp.lbjava.classify.FeatureVector;
import edu.illinois.cs.cogcomp.lbjava.classify.RealPrimitiveStringFeature;
import edu.illinois.cs.cogcomp.lbjava.classify.ScoreSet;
import edu.illinois.cs.cogcomp.lbjava.neuralnet.Layer;
import edu.illinois.cs.cogcomp.lbjava.neuralnet.SimpleNNTrainer;

/**
 * This class will leverage the Neural Net implementation {@see edu.illinois.cs.cogcomp.lbjava.neuralnet.NeuralNetwork}
 * to allow creation and use of a backprop neural net implementation including momentum, bias, and back propogation 
 * for learning. There is a threaded learner that works quite well ONLY where there are a large number of weights 
 * between layers.
 * <p>
 * This class is really just a wrapper for a SimpleNNTrainer which does all the work of training.
 * @author redman
 */
public class NeuralNetLearner extends Learner {

	/** computed */
	private static final long serialVersionUID = -3369861028861092661L;
	
	/** the parameters for learning and stuff. */
    private Parameters parameters = new Parameters();
    
    /** This is the object that will train the neural net up. It uses it own
     * interal mechanism and data representation for efficiency. */
    private SimpleNNTrainer trainer = null;
    
    /** 
     * our props include not only number of rounds (epochs), also a learning rate and momentum.
     * @author redman
     */
    public static class Parameters extends Learner.Parameters {
    	/** default */
		private static final long serialVersionUID = 1L;

		/** the learning rate. */
    	public float learningRate = 0.5f;
    	
    	/** the momentum value. */
    	public float momentum = 0.5f;
    	
    	/** the momentum value. */
    	public int seed = -1;
    	
    	/** the number of inputs */
    	public int inputCount = 0;
    	
    	/** the number of outputs */
    	public int outputCount = 1;
    	
    	/** the number of outputs from the single hidden layer */
    	public int hiddenCount = 100;
    	
        /** the layers of the neural network. */
        private Layer[] layers;
        
        /**
         * Copy properties from the provided properties.
         * @param p the props to copy.
         */
        public Parameters(Parameters p) {
        	this.learningRate = p.learningRate;
        	this.momentum = p.momentum;
        	this.seed = p.seed;
        	this.inputCount = p.inputCount;
        	this.outputCount = p.outputCount;
        	this.hiddenCount = p.hiddenCount;
        }
        /**
         * Copy properties from the provided properties.
         * @param p the props to copy.
         */
        public Parameters() {
        	this.learningRate = 0.5f;
        	this.momentum = 0.5f;
        	this.seed = -1;
        	this.inputCount = 0;
        	this.hiddenCount = 100;
        	this.outputCount = 1;
        }

    }
    
    /** used to store inputs so we don't realloc these arrays over and over. This is an optimization
     * only possible because we know this guys is not multithreaded. */
    private float inputs[] = null;
    
    /** used to store inputs so we don't realloc these arrays over and over. This is an optimization
     * only possible because we know this guys is not multithreaded. */
    private float outputs[] = null;
    
    /** number of neurons in each layer, including input and output layers.*/
    private int[] layerSizes = null;
    
	/**
	 * Init the neural network learner by providing array with number of neurons in each layer, including 
	 * the input layer. The caller will need to determin the number of inputs, the number of outputs and the number
	 * of hidden layers, and the neurons in that layer. The first index in teh layerSizes indicates the number of inputs,
	 * the middle layers sizes are determined by the middle integer sizes, and the number of outputs is the last number
	 * of neurons.
	 * @param layerSizes the number of neurons in each layer.
	 */
	public NeuralNetLearner () {
		super("Howdy");
		this.layerSizes = new int[3];
	}
	
	/**
	 * given arguments for initialization parameters.
	 * @param p the parameters.
	 */
	public NeuralNetLearner(Parameters p) {
		super("Howdy");
		this.parameters = p;
	}
	
    /**
     * The learning rate takes the default value.
     * @param n The name of the classifier.
     */
    public NeuralNetLearner(String n) {
        super(n);
    }

	/**
	 * Init the neural network learner by providing array with number of neurons in each layer, including 
	 * the input layer. The caller will need to determin the number of inputs, the number of outputs and the number
	 * of hidden layers, and the neurons in that layer. The first index in teh layerSizes indicates the number of inputs,
	 * the middle layers sizes are determined by the middle integer sizes, and the number of outputs is the last number
	 * of neurons.
	 * @param layerSizes the number of neurons in each layer.
	 */
	public NeuralNetLearner (int[] layerSizes, Parameters p, boolean training) {
		super("Howdy");
		parameters = p;
		parameters.layers = new Layer[layerSizes.length-1];
		this.layerSizes = layerSizes;
		this.forget();
	}
	
    /** 
     * Resets the weight vector to all zeros. 
     */
    public void forget() {
        super.forget();
        if (this.getInputCount() != -1) {
    		this.layerSizes = new int[3];
    		this.layerSizes[0] = this.getInputCount();
    		this.layerSizes[1] = this.getHiddenCount();
    		this.layerSizes[2] = this.getOutputCount();
    		parameters.layers = new Layer[layerSizes.length-1];
			Layer[] l = this.parameters.layers;
	        Random r = new Random (1234);
	        for (int i = 0; i < layerSizes.length-1; i++) {
	            l[i] = new Layer(layerSizes[i], layerSizes[i+1], r);
	        }
			inputs = new float[l[0].getNumberInputs()];
			outputs = new float[l[l.length-1].getNumberOutputs()];
			trainer = new SimpleNNTrainer(parameters.layers, parameters.learningRate, parameters.momentum);
        }
    }

    /**
     * Returns a string describing the output feature type of this classifier.
     * @return <code>"real"</code>
     **/
    public String getOutputType() {
        return "real";
    }
    /**
     * Writes the learned function's internal representation in binary form.
     * @param out The output stream.
     
    public void write(ExceptionlessOutputStream out) {
        super.write(out);
        out.writeFloat(this.parameters.learningRate);
        out.writeFloat(this.parameters.momentum);
        out.writeInt(this.parameters.rounds);
        if (this.layerSizes == null)
        	out.writeInt(0);
        else {
	        out.writeInt(this.layerSizes.length);
	        for (int neurons : this.layerSizes)
	        	out.writeInt(neurons);
	        for (Layer l : this.parameters.layers) {
	        	l.write(out);
	        }
        }
    }

    /**
     * Reads the binary representation of a learner with this object's run-time type, overwriting
     * any and all learned or manually specified parameters as well as the label lexicon but without
     * modifying the feature lexicon.
     * @param in The input stream.
     
    public void read(ExceptionlessInputStream in) {
        super.read(in);
        this.parameters.learningRate = in.readFloat();
        this.parameters.momentum = in.readFloat();
        this.parameters.rounds = in.readInt();
        int layers = in.readInt();
        if (layers != 0) {
	        int[] szs = new int[layers];
	        for (int i = 0 ; i < szs.length; i++)
	        	szs[i] = in.readInt();
	        this.layerSizes = szs;
	        Random r = new Random (1234);
	        for (int i = 0; i < layerSizes.length-1; i++) {
	            this.parameters.layers[i] = new Layer(layerSizes[i], layerSizes[i+1], r);
	        }
			trainer = new SimpleNNTrainer(parameters.layers, parameters.learningRate, parameters.momentum);
	        for (Layer l : this.parameters.layers) {
	        	l.read(in);
	        }
        }
    }


    /**
     * Populate the input and output vectors with the values for only those 
     * features that are represented.
     */
    final private void populateNNVector(int[] exampleFeatures, double[] exampleValues, int[] exampleLabels,
            double[] labelValues) {
		Arrays.fill(inputs,0.0f);
		Arrays.fill(outputs,0.0f);
		for (int i = 0; i < exampleFeatures.length; i++)
			inputs[exampleFeatures[i]] = (float)exampleValues[i];
		if (exampleLabels != null)
			for (int i = 0; i < exampleLabels.length; i++)
				outputs[exampleLabels[i]] = (float)labelValues[i];

    }
    
    /**
     * Trains the learning algorithm given an object as an example.
     * @param exampleFeatures The example's array of feature indices.
     * @param exampleValues The example's array of feature values.
     * @param exampleLabels The example's label(s).
     * @param labelValues The labels' values.
     **/
    public void learn(int[] exampleFeatures, double[] exampleValues, int[] exampleLabels,
            double[] labelValues) {
    	this.populateNNVector(exampleFeatures, exampleValues, exampleLabels, labelValues);
		this.trainer.train(inputs, outputs);
    }

    /**
     * @param exampleFeatures The example's array of feature indices.
     * @param exampleValues The example's array of feature values.
     * @return <code>null</code>
     **/
    public ScoreSet scores(int[] exampleFeatures, double[] exampleValues) {
    	return null;
    }

    /**
     * Returns the classification of the given example as a single feature instead of a
     * {@link FeatureVector}.
     * @param f The features array.
     * @param v The values array.
     * @return The classification of the example as a feature.
     **/
    public Feature featureValue(int[] f, double[] v) {
    	this.populateNNVector(f, v, null, null);
    	
    	// this returns the activation energies for ALL layers, we only wan the output layer
    	float[][] results = this.trainer.activate(inputs);

    	// the last vector contains the score, this is the output of the last layer.
    	return  new RealPrimitiveStringFeature(containingPackage, name, "", results [results.length-1][0]);
    }

    /**
     * Simply computes the dot product of the weight vector and the example
     *
     * @param exampleFeatures The example's array of feature indices.
     * @param exampleValues The example's array of feature values.
     * @return The computed real value.
     **/
    public double realValue(int[] exampleFeatures, double[] exampleValues) {
    	this.populateNNVector(exampleFeatures, exampleValues, null, null);
    	return (double) this.trainer.activate(inputs)[0][0];
    }

    /**
     * Simply computes the dot product of the weight vector and the feature vector extracted from
     * the example object.
     *
     * @param exampleFeatures The example's array of feature indices.
     * @param exampleValues The example's array of feature values.
     * @return The computed feature (in a vector).
     **/
    public FeatureVector classify(int[] exampleFeatures, double[] exampleValues) {
        return new FeatureVector(featureValue(exampleFeatures, exampleValues));
    }

    /**
     * Writes the algorithm's internal representation as text. In the first line of output, the name
     * of the classifier is printed, followed by {@link #learningRate} and {@link #bias}.
     * @param out The output stream.
     */
    public void write(PrintStream out) {
        out.println(name + ": " + this.parameters.learningRate + ", " + this.parameters.momentum + ", " + this.parameters.rounds);
        for (Layer l : this.parameters.layers) {
        	l.write(out);
        }
    }

    /** 
     * Returns a deep clone of this learning algorithm. 
     * TODO
     */
    public Object clone() {
        NeuralNetLearner clone = null;
        try {
            clone = (NeuralNetLearner) super.clone();
        } catch (Exception e) {
            System.err.println("Error cloning StochasticGradientDescent: " + e);
            System.exit(1);
        }
        return clone;
    }

	/**
	 * @return the seed to seed all random number gen.
	 */
	public int getSeed() {
		return this.parameters.seed;
	}

	/**
	 * @param seed the seed to set
	 */
	public void setSeed(int seed) {
		this.parameters.seed = seed;
	}

	/**
	 * @return the number of total inputs
	 */
	public int getInputCount() {
		return this.parameters.inputCount;
	}

	/**
	 * @param inputCount the inputCount to set
	 */
	public void setInputCount(int inputCount) {
		this.parameters.inputCount = inputCount;
	}

	/**
	 * @return the outputCount
	 */
	public int getOutputCount() {
		return this.parameters.outputCount;
	}

	/**
	 * @param outputCount the outputCount to set
	 */
	public void setOutputCount(int outputCount) {
		this.parameters.outputCount = outputCount;
	}

	/**
	 * @return the hiddenCount
	 */
	public int getHiddenCount() {
		return this.parameters.hiddenCount;
	}

	/**
	 * @param hiddenCount the hiddenCount to set
	 */
	public void setHiddenCount(int hiddenCount) {
		this.parameters.hiddenCount = hiddenCount;
	}

	/**
	 * @return the learning rate used to throttle the rate at wich the weight parameters change.
	 */
	public float getLearningRate() {
		return parameters.learningRate;
	}

	/**
	 * set the learning rate at which the weight parameters change.
	 * @param learningRate the learning rate at which the weight parameters change.
	 */
	public void setLearningRate(float learningRate) {
		this.parameters.learningRate = learningRate;
	}

	public float getMomentum() {
		return parameters.momentum;
	}

	/**
	 * set the value used to prevent convergence against local minimum.
	 * @param momentum used to prevent convergence against local minimum.
	 */
	public void setMomentum(float momentum) {
		this.parameters.momentum = momentum;
	}

	/**
	 * Get the number of epochs.
	 * @return number of epochs to train.
	 */
	public int getEpochs() {
		return parameters.rounds;
	}
	
	/**
	 * set the number of training iterations. More should yield better results, until overfit.
	 * @param learningRate set the number of training iterations.
	 */
	public void setEpochs(int epochs) {
		this.parameters.rounds = epochs;
	}
	
    /**
     * Retrieves the parameters that are set in this learner.
     * @return An object containing all the values of the parameters that control the behavior of
     *         this learning algorithm.
     **/
    public Learner.Parameters getParameters() {
    	return parameters;
    }
}
