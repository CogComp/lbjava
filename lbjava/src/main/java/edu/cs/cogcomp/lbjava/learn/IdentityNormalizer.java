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
 * This <code>Normalizer</code> simply returns the same <code>ScoreSet</code> it was passed as input
 * without modifying anything.
 *
 * @author Nick Rizzolo
 **/
public class IdentityNormalizer extends Normalizer {
    /**
     * Simply returns the argument.
     *
     * @param scores The set of scores to normalize.
     * @return The normalized set of scores.
     **/
    public ScoreSet normalize(ScoreSet scores) {
        return scores;
    }
}
