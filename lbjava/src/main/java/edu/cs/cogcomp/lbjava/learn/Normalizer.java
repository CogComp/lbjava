/**
 * This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 *
 * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.cs.cogcomp.lbjava.learn;

import edu.cs.cogcomp.lbjava.classify.ScoreSet;


/**
 * A normalizer is a function of a <code>ScoreSet</code> producing normalized scores. It is left to
 * the implementing subclass to define the term "normalized".
 *
 * @author Nick Rizzolo
 **/
public abstract class Normalizer {
    /**
     * Normalizes the given <code>ScoreSet</code>; its scores are modified in place before it is
     * returned.
     *
     * @param scores The set of scores to normalize.
     * @return The normalized set of scores.
     **/
    abstract public ScoreSet normalize(ScoreSet scores);
}
