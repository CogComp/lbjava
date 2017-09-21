/**
 * This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 *
 * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.illinois.cs.cogcomp.lbjava.examples.neuralnet;

import edu.illinois.cs.cogcomp.lbjava.classify.Classifier;

/**
 * This was used for debugging during development, thought it might be useful in the future
 * although it is completely useless right now. 
 * @author redman
 */
@SuppressWarnings("unused")
public class Debug {
	/** running ANN by default. */
	static private final String NN = "NeuralNet";
	
	/** running gradient descent. */
	static private final String SGD = "StoichasticGradientDescent";
	
	/** the method we are running. */
    static private String method = NN;
    
    /** scales the weight deltas for each iteration. */
    static private float learningRate = .3f;
    
    /** this prevents local minimum capture. */
    static private float momentum = .6f;

    /** this prevents local minimum capture. */
    static private int hiddenLayerSize = 20;

    /** this prevents local minimum capture. */
    static private int epochs = 100;

    /** The number of threads to support. */
    @SuppressWarnings("unused")
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
			else if (args[0].equals("-gd"))
				method = SGD;
            else if (args[i].equals("-help")) {
                System.out.println("-t the number of threads to deploy.\n"
                		+ "-l the learning rate.\n"
                		+ "-m momentum.\n"
                		+ "-e number of epochs.\n"
                		+ "-h hidden layer size.\n"
                		+ "-gd use gradient descent.\n"
                		+ "-help this output.");
                System.exit(0);
            } else
                System.out.println("Unexpected argument : "+args[i]);
        }
    }

    /**
     * Print a set of any pair of floating point arrays, labels can be passed in, if
     * null is passed for the ol parameter, no second array is printed.
     * @param il the input label.
     * @param input the input vector.
     * @param ol the output label.
     * @param output the output vector.
     */
    static void printInOut(String il, float[] input, String ol, float[] output) {
        System.out.print(il+" ");
        for (float in : input) {
            System.out.format(" %.18f",in);
        }
        if (ol!=null) {
            System.out.print(" "+ol+" ");
            for (float in : output) {
                System.out.format(" %.18f",in);
            }
        }
        System.out.println();
    }
    
    /**
     * Print the input and outputs all on one line.
     * @param il the input label.
     * @param input the input vector.
     * @param ol the output label.
     * @param output the output vector.
     */
    static void printInOutC(String il, float[] input, String ol, float[] output) {
        System.out.println(il+" ");
        int c = 0;
        for (float in : input) {
            System.out.format(c+il+": %.18f\n",in);
            c++;
        }
        if (ol!=null) {
            System.out.println(" "+ol+" ");
            c = 0;
            for (float in : output) {
                System.out.format(c+ol+": %.18f\n",in);
                c++;
            }
        }
    }
    
    /**
     * Compute the value, compare to the label, and accumulate predicted error.
     * @param br the brown data reader.
     * @param classifier the learner.
     */
    static double computeHits (BrownReader br, Classifier classifier) {
    	int i = 0;
    	int bads = 0;
    	while (true) {
    		NeuralNetExample nne = (NeuralNetExample)br.next();
    		if (nne == null) {
    			// done;
                return (1.0f - ((double)bads/(double)i)) * 100f;
    		} else {
    			double value = classifier.realValue(nne);
    			double tru = nne.getOutputLabels()[0];
    			double abserr = Math.abs(value - tru);
    			if (abserr > .25) {
                    bads++;
    			}
                i++;
    		}
    		
    	}
    }
    
	/**
	 * @param args
	 
	public static void main(String[] args) {
		parseArgs(args);
		if (method == NN) {
			// read the data to know how many input features there are.
			BrownReader br = new BrownReader("data/brown/their-brown80.feat");
			
			// first create the classifier and train it up.
			NNBrownDataClassifier nn = new NNBrownDataClassifier();
			nn.setInputCount(br.inputCardinality);
			nn.setHiddenCount(hiddenLayerSize);
			nn.setOutputCount(1);
			nn.setEpochs(epochs);
			nn.setMomentum(momentum);
			nn.setLearningRate(learningRate);
			nn.forget();

			int epochs = nn.getEpochs();
			long time = System.currentTimeMillis();
			// read training data. 
			try  {
				// train.
				ArrayList<Object> trainingExamples = new ArrayList<>();
				while(true) {
					Object o = br.next();
					trainingExamples.add(o);
					if (o == null)
						break;
					nn.learn(o);
				}
				Random r = new Random();
				for(int i = 0 ; i < epochs-1; i++) {
					for (int j = 0; j < trainingExamples.size(); j++) {
						int oidx = r.nextInt(trainingExamples.size());
						Object o = trainingExamples.get(oidx);
						if (o == null)
							break;
						nn.learn(o);
					}
				}

			} finally {
				br.close();
			}
			
			// now we have a trained up model, let's test it.
			br = new BrownReader("data/brown/their-brown20.feat",br.inputs[0].length, br.outputCardinality);
			double accuracy = computeHits(br, nn);
			double seconds = ((System.currentTimeMillis() - time)/1000.0);
			
			// epochs, rate, momentum, hiddens, accuracy, time
            System.out.format("%d,%.2f,%.2f,%d,%.4f,%.4f\n",epochs,learningRate,momentum,hiddenLayerSize,accuracy,seconds);
		} else {
			
			// first create the classifier and train it up.
			SGDBrownDataClassifier sdg = new SGDBrownDataClassifier();
			sdg.forget();
			Learner.Parameters p = sdg.getParameters();
			p.rounds = epochs;
			
			System.out.println("Reading data SGD");
			BrownReader br = new BrownReader("data/brown/their-brown80.feat");
			ArrayList<Object> trainingExamples = new ArrayList<>();
			while(true) {
				Object o = br.next();
				trainingExamples.add(o);
				if (o == null)
					break;
				sdg.learn(o);
			}
			System.out.println("Training SGD");
			Random r = new Random();
			for(int i = 0 ; i < p.rounds-1; i++) {
				for (int j = 0; j < trainingExamples.size(); j++) {
					int oidx = r.nextInt(trainingExamples.size());
					Object o = trainingExamples.get(oidx);
					if (o == null)
						break;
					sdg.learn(o);
				}
			}
			System.out.println("Training up done.");
			
			// now we have a trained up model, let's test it.
			br = new BrownReader("data/brown/their-brown20.feat",br.inputs[0].length, br.inputs.length);
			computeHits(br, sdg);
		}
	}*/
}
