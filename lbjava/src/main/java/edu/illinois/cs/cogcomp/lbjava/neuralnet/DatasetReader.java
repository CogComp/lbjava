/**
 * This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 *
 * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.illinois.cs.cogcomp.lbjava.neuralnet;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author redman
 *
 */
public class DatasetReader {
    
    /**
     * flip the byte order.
     * @param is input stream.
     * @return the integer.
     * @throws IOException 
     */
    private static int readInt(InputStream is) throws IOException {
        int i0 = is.read();
        int i1 = is.read();
        int i2 = is.read();
        int i3 = is.read();
        return (i0<<24) + (i1<<16) + (i2<<8) + i3;
    }
    
    /**
     * get the examples form an NIST dataset, return everything at once. There are
     * 60k examples, at 28x28 pixel values per example, so 60000 x 28 x 28 floats = 
     * 47 million floats. These are input examples, so they are image data.
     * @param filename
     * @return the input examples.
     * @throws IOException 
     */
    public static float[][] getExampleInputs(String filename) throws IOException {
        InputStream dis = new BufferedInputStream(new FileInputStream(new File(filename)));
        int m1 = readInt(dis);
        if (m1 != 2051) 
            throw new IOException("That was not an example file! magic code = "+m1);
        int numExamples = readInt(dis);
        if (numExamples != 60000) 
            System.out.println("We expecting 60k examples "+m1);
        int numRows = readInt(dis);
        if (numRows != 28) 
            System.out.println("We expecting 28 rows "+numRows);
        int numColumns = readInt(dis);
        if (numColumns != 28) 
            System.out.println("We expecting 28 columns "+numColumns);
        int totalpixels = numRows*numColumns;
        float [][] examples = new float [numExamples][totalpixels];
        for (int i = 0 ; i < examples.length; i++) {
            for (int j = 0; j < totalpixels; j++) {
                examples[i][j] = (float)(dis.read()/128f) - 1f;
            }
        }
        return examples;
    }
    
    /**
     * get the examples form an NIST dataset, return everything at once. There are
     * 60k examples, at 28x28 pixel values per example, so 60000 x 28 x 28 floats = 
     * 47 million floats. These are input examples, so they are image data.
     * @param filename
     * @return the output examples.
     * @throws IOException 
     */
    public static float[][] getExampleOutputs(String filename) throws IOException {
        InputStream dis = new BufferedInputStream(new FileInputStream(new File(filename)));
        int m1 = readInt(dis);
        if (m1 != 2049) 
            throw new IOException("That was not an example file! magic code = "+m1);
        int numExamples = readInt(dis);
        float [][] examples = new float [numExamples][1];
        for (int i = 0 ; i < numExamples; i++) {
            examples[i][0] = (float)(dis.read()/5f) - 1f;
        }
        return examples;
    }

    /**
     * @param a
     * @throws IOException
     */
    @SuppressWarnings("unused")
    public static void main(String[]a) throws IOException {
        float[][] examples = getExampleInputs("/Users/redman/Desktop/NNTrainingData/train-images-idx3-ubyte");
        float[][] labels = getExampleOutputs("/Users/redman/Desktop/NNTrainingData/train-labels-idx1-ubyte");
    }
}
