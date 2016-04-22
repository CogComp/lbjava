/**
 * This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 *
 * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.illinois.cs.cogcomp.lbjava.learn;

import edu.illinois.cs.cogcomp.lbjava.classify.Classifier;
import edu.illinois.cs.cogcomp.lbjava.classify.TestDiscrete;
import edu.illinois.cs.cogcomp.lbjava.parse.Parser;


/**
  * Returns the accuracy of a discrete classifier with respect to the oracle
  * as the fraction of examples for which its prediction was correct.
  *
  * @author Dan Muriello
 **/
public class Accuracy implements TestingMetric
{
  /**
    * Whether or not to print a table of results to <code>STDOUT</code> when
    * {@link #test(Classifier,Classifier,Parser)} is called.
   **/
  private boolean print;


  /**
    * Creates an <code>Accuracy</code> testing metric that does not print a
    * table of results.
   **/
  public Accuracy() { this(false); }

  /**
    * Creates an <code>Accuracy</code> testing metric that prints a table of
    * results if requested.
    *
    * @param p  Whether or not to print a table of results when
    *           {@link #test(Classifier,Classifier,Parser)} is called.
   **/
  public Accuracy(boolean p) { print = p; }


  /** Returns the name of the testing metric. */
  public String getName() { return "Accuracy"; }


  /**
    * Evaluates a classifier against an oracle on the data provided by a
    * parser.
    *
    * @param classifier The classifier whose accuracy is being measured.
    * @param oracle     A classifier that returns the label of each example.
    * @param parser     A parser to supply the example objects.
    * @return The fraction of examples for which the classifier's prediction
    *         was correct.
   **/
  public double test(Classifier classifier, Classifier oracle, Parser parser)
  {
    TestDiscrete tester =
      TestDiscrete.testDiscrete(classifier, oracle, parser);
    if (print) tester.printPerformance(System.out);
    return tester.getOverallStats()[0];
  }
}

