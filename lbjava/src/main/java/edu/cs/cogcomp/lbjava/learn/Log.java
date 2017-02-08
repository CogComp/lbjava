/**
 * This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 *
 * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.cs.cogcomp.lbjava.learn;

import edu.cs.cogcomp.lbjava.classify.Score;
import edu.cs.cogcomp.lbjava.classify.ScoreSet;


/**
 * Simply turns each score <i>s</i> in the {@link ScoreSet} returned by the specified
 * {@link Normalizer} into <i>log(s)</i>.
 *
 * @author Nick Rizzolo
 **/
public class Log extends Normalizer {
    /** This normalizer runs before applying the <i>log</i> function. */
    protected Normalizer first;


    /** This constructor provided for use by the LBJava compiler only. */
    public Log() {}

    /**
     * Initializing constructor.
     *
     * @param n This normalizer runs before applying the <i>log</i> function.
     **/
    public Log(Normalizer n) {
        first = n;
    }


    /**
     * Normalizes the given <code>ScoreSet</code>; its scores are modified in place before it is
     * returned.
     *
     * @param scores The set of scores to normalize.
     * @return The normalized set of scores.
     **/
    public ScoreSet normalize(ScoreSet scores) {
        Score[] array = first.normalize(scores).toArray();
        for (int i = 0; i < array.length; ++i)
            array[i].score = Math.log(array[i].score);
        return scores;
    }
}
