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
package edu.illinois.cs.cogcomp.lbjava.util;

import edu.illinois.cs.cogcomp.lbjava.learn.Learner;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * A sets of debug helper functions
 *
 * @author Yiming Jiang
 */
public class DebugUtils {

    /**
     * Print weight vector info, from a given learner
     * @param learner learner
     */
    public static void printWeightVector(Learner learner) {
        ByteArrayOutputStream sout = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(sout);
        learner.write(out);
        System.out.println(sout.toString());
    }
}
