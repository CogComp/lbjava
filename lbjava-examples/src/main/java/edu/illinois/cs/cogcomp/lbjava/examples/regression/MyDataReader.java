/**
 * This software is released under the University of Illinois/Research and
 *  Academic Use License. See the LICENSE file in the root folder for details.
 * Copyright (c) 2016
 *
 * Developed by:
 * The Cognitive Computations Group
 * University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.illinois.cs.cogcomp.lbjava.examples.regression;

import edu.illinois.cs.cogcomp.lbjava.parse.Parser;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Parser
 *
 * It reads through a file and parse each example as <code>MyData</code>
 *
 * Note: assuming each line consists of features and label for each example,
 *       please see <code>MyData</code> for more information on the format
 *
 * @author Yiming Jiang
 */
public class MyDataReader implements Parser {

    /* a list holding all lines in the file */
    private final List<String> lines;

    /* keep track of where we are */
    private int currentLineNumber;

    /**
     * Constructor
     * @param filePath the path to the file
     */
    public MyDataReader(String filePath) {
        this.lines = new ArrayList<>();
        this.currentLineNumber = 0;

        Reader reader;
        try {
            reader = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String eachLine;
            while ((eachLine = bufferedReader.readLine()) != null) {
                lines.add(eachLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Iterator method
     * @return the next example in <code>MyData</code>
     */
    @Override
    public Object next() {
        if (currentLineNumber < lines.size()) {
            MyData ret = new MyData(lines.get(currentLineNumber));
            this.currentLineNumber ++;
            return ret;
        }
        return null;
    }

    @Override
    public void close() {
    }

    /**
     * Reset the line number tracker
     */
    @Override
    public void reset() {
        this.currentLineNumber = 0;
    }
}