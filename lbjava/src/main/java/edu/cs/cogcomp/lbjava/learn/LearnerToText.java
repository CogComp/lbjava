/**
 * This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 *
 * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.cs.cogcomp.lbjava.learn;

import java.io.BufferedOutputStream;
import java.io.PrintStream;

import edu.cs.cogcomp.lbjava.util.ClassUtils;


/**
 * This extremely simple class can be used to print a textual representation of a trained learner to
 * <code>STDOUT</code>. This is achieved with the following line of code:
 *
 * <blockquote><code> learner.write(System.out); </code></blockquote>
 *
 * <h3>Usage</h3> <blockquote> <code>
 *     java edu.cs.cogcomp.lbjava.learn.LearnerToText &lt;learner&gt;
 *   </code> </blockquote>
 *
 * <h4>Input</h4> The <code>&lt;learner&gt;</code> parameter must be a fully qualified class name
 * (e.g. <code>myPackage.myName</code>) referring to a class that extends {@link Learner}. Every
 * learning classifier specified in an LBJava source code satisfies this requirement.
 *
 * <h4>Output</h4> A textual representation of the specified learning classifier is produced on
 * <code>STDOUT</code>.
 *
 * @author Nick Rizzolo
 **/
public class LearnerToText {
    public static void main(String[] args) {
        String learnerName = null;

        try {
            learnerName = args[0];
            if (args.length > 1)
                throw new Exception();
        } catch (Exception e) {
            System.err
                    .println("usage: java edu.cs.cogcomp.lbjava.learn.LearnerToText <learner>");
            System.exit(1);
        }

        Learner learner = ClassUtils.getLearner(learnerName);
        learner.demandLexicon();
        PrintStream out = new PrintStream(new BufferedOutputStream(System.out));
        learner.write(out);
        out.close();
    }
}
