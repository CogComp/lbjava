/**
 * This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 *
 * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.cs.cogcomp.lbjava.parse;


import edu.cs.cogcomp.lbjava.learn.Learner;

/**
 * Any parser that extends this interface can be sent to a <code>Learner</code> for batch training.
 *
 * @see Learner
 * @author Nick Rizzolo
 **/
public interface Parser {
    /**
     * Use this method to retrieve the next object parsed from the raw input data.
     *
     * @return The next object parsed from the input data.
     **/
    Object next();


    /** Sets this parser back to the beginning of the raw data. */
    void reset();


    /** Frees any resources this parser may be holding. */
    void close();
}
