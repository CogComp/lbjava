/**
 * This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 *
 * Developed by: The Cognitive Computations Group University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.illinois.cs.cogcomp.lbjava.examples.setCover;

import edu.illinois.cs.cogcomp.lbjava.classify.ScoreSet;

public class ContainsStation extends DumbLearner {
    public ContainsStation() {
        super("edu.illinois.cs.cogcomp.lbjava.examples.setCover.ContainsStation");
    }

    public String getInputType() {
        return "edu.illinois.cs.cogcomp.lbjava.examples.setCover.Neighborhood";
    }

    public String[] allowableValues() {
        return new String[] {"false", "true"};
    }

    public ScoreSet scores(Object example) {
        ScoreSet result = new ScoreSet();
        result.put("false", 0);
        result.put("true", -1);
        return result;
    }
}
