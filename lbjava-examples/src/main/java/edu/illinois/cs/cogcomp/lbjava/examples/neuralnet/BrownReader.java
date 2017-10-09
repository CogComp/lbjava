/**
 * This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 *
 * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.illinois.cs.cogcomp.lbjava.examples.neuralnet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import edu.illinois.cs.cogcomp.lbjava.parse.Parser;

/**
 * @author redman
 */
public class BrownReader implements Parser{
	
	/** the input data. */
	float [][] inputs;
	
	/** the labels. */
	float [][] outputs;
	
	/** indexes the current example. */
	int index = 0;
	
	/** the maximum number of input features. */
    int inputCardinality = -1;

	/** the maximum integer classification. */
    int outputCardinality = 1;

	/**
	 * read input data from the input file, the output data from the out file.
	 * @param infile the input data.
	 * @param outfile the output data.
	 * @throws IOException 
	 */
	public BrownReader (String infile) {
		try {
			inputs = getExampleInputs(infile);
			this.inputCardinality = inputs[0].length;
			outputs = getExampleOutputs(infile);
			if (inputs.length != outputs.length)
				throw new RuntimeException("Need the same number of inputs and outputs.");
		} catch (IOException e) {
			throw new RuntimeException("Could not read example data.",e);
		}
	}
	
	/**
	 * read input data from the input file, the output data from the out file.
	 * @param infile the input data.
	 * @param trainingInputs the previously read training inputs.
	 * @throws IOException 
	 */
	public BrownReader (String infile, int numberInputFeatures, int numberExamples) {
		try {
			this.inputCardinality = numberInputFeatures;
			inputs = getExampleInputs(infile, numberInputFeatures);
			outputs = getExampleOutputs(infile, inputs.length, numberExamples);
			if (inputs.length != outputs.length)
				throw new RuntimeException("Need the same number of inputs and outputs.");
		} catch (IOException e) {
			throw new RuntimeException("Could not read example data.",e);
		}
	}

	@Override
	public void close() {	
		index = 0;
	}
	
	@Override
	public Object next() {
		NeuralNetExample nne = null;
		if (index < inputs.length) {
			nne = new NeuralNetExample(inputs[index], outputs[index]);
			index++;
		}
		return nne;
	}
	
	@Override
	public void reset() {
		index = 0;
	}

    /**
     * get the examples form an NIST dataset, return everything at once. There are
     * 60k examples, at 28x28 pixel values per example, so 60000 x 28 x 28 floats = 
     * 47 million floats. These are input examples, so they are image data.
     * @param filename
     * @return the input examples.
     * @throws IOException 
     */
    private float[][] getExampleInputs(String filename) throws IOException {
        int count = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(new File(filename)))) {
            String line;
            while ((line=br.readLine()) != null) {
                count++;
                String[] splits = line.split("[,:]");
                for (int i = 1; i < splits.length; i++) {
                    int featureindex = Integer.parseInt(splits[i]);
                    if (featureindex > this.inputCardinality) 
                    	this.inputCardinality = featureindex;
                }
            }
        }
        float[][] data = new float[count][++this.inputCardinality];
        for (float[] a : data)
            Arrays.fill(a, 0.0f);
        
        try (BufferedReader br = new BufferedReader(new FileReader(new File(filename)))) {
            String line;
            count = 0;
            while ((line=br.readLine()) != null) {
                String[] splits = line.split("[,:]");
                for (int i = 0; i < splits.length; i++) {
                    int featureindex = Integer.parseInt(splits[i]);
                    data[count][featureindex] = 1.0f;
                }
                count++;
            }
        }
        return data;
    }
    
    /**
     * scale the range of input feature vector to the provided example set, of data to train on.
     * @param string
     * @param examples
     * @return the testing input deck.
     * @throws IOException 
     * @throws FileNotFoundException 
     */
    private float[][] getExampleInputs(String filename, int cardinality) throws FileNotFoundException, IOException {
        int count = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(new File(filename)))) {
            String line;
            while ((line=br.readLine()) != null) {
                count++;
                String[] splits = line.split("[,:]");
                for (int i = 1; i < splits.length; i++) {
                    int featureindex = Integer.parseInt(splits[i]);
                    if (featureindex > this.inputCardinality) 
                    	this.inputCardinality = featureindex;
                }
            }
        }
        float[][] data = new float[count][cardinality];
        for (float[] a : data)
            Arrays.fill(a, 0.0f);
        try (BufferedReader br = new BufferedReader(new FileReader(new File(filename)))) {
            String line;
            count = 0;
            while ((line=br.readLine()) != null) {
                String[] splits = line.split("[,:]");
                for (int i = 0; i < splits.length; i++) {
                    int featureindex = Integer.parseInt(splits[i]);
                    data[count][featureindex] = 1.0f;
                }
                count++;
            }
        }
        return data;
    }

    /**
     * get the examples form an NIST dataset, return everything at once. There are
     * 60k examples, at 28x28 pixel values per example, so 60000 x 28 x 28 floats = 
     * 47 million floats. These are input examples, so they are image data.
     * @param filename
     * @return the input examples.
     * @throws IOException 
     */
    private float[][] getExampleOutputs(String filename) throws IOException {
        int count = 0;
        this.outputCardinality = -1;
        try (BufferedReader br = new BufferedReader(new FileReader(new File(filename)))) {
            String line = null;
            while ((line=br.readLine()) != null) {
                count++;
                String[] splits = line.split("[,:]");
                int label = Integer.parseInt(splits[0]);
                if (label > this.outputCardinality) 
                	this.outputCardinality = label;
            }
        }
        float[][] data = new float[count][1];
        for (float[] a : data)
            Arrays.fill(a, 0.0f);
        try (BufferedReader br = new BufferedReader(new FileReader(new File(filename)))) {
            String line;
            count = 0;
            float range = this.outputCardinality;
            while ((line=br.readLine()) != null) {
                String[] splits = line.split("[,:]");
                int featureindex = Integer.parseInt(splits[0]);
                data[count][0] = featureindex/range;
                count++;
            }
        }
        return data;
    }
    
    /** 
     * get the example outputs.
     * @param filename file with the values.
     * @param outputs the training examples.
     * @return the testing examples.
     * @throws FileNotFoundException
     * @throws IOException
     */
    private float[][] getExampleOutputs(String filename, int numouts, int card) throws FileNotFoundException, IOException {
        float[][] data = new float[numouts][1];
        try (BufferedReader br = new BufferedReader(new FileReader(new File(filename)))) {
            String line;
            int count = 0;
            float range = card;
            while ((line=br.readLine()) != null) {
                String[] splits = line.split("[,:]");
                int featureindex = Integer.parseInt(splits[0]);
                // convert to a number 0 - 1, then to a number -1 to 1.
                data[count][0] = featureindex/range;
                count++;
            }
        }
        return data;
    }
}
