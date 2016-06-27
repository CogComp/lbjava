/**
 * This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 *
 * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.illinois.cs.cogcomp.lbjava.parse;


/**
 * This parser returns the example objects in an array one at a time.
 *
 * @author Michael Paul
 **/
public class ArrayParser implements Parser {
    /** The pointer to the current cell of the {@link #examples} array. */
    protected int index;
    /** An array of examples, returned one at a time by the parser. */
    protected Object[] examples;


    /**
     * Initializes the parser with an empty example array.
     **/
    public ArrayParser() {
        this(new Object[0]);
    }

    /**
     * Creates the parser with the supplied example array.
     *
     * @param e The array of examples
     **/
    public ArrayParser(Object[] e) {
        index = 0;
        examples = e;
    }


    /** Returns the value of {@link #examples}. */
    public Object[] getExamples() {
        return examples;
    }


    /**
     * Returns the next example in the array and increments the {@link #index} pointer.
     **/
    public Object next() {
        if (index >= examples.length)
            return null;
        return (Object[]) examples[index++];
    }


    /**
     * Resets the {@link #index} pointer to 0.
     **/
    public void reset() {
        index = 0;
    }


    /** Frees any resources this parser may be holding. */
    public void close() {}
}
