/**
 * This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 *
 * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
/**
 * <p>
 * For sparse learners, it is often the case that the array of features you learn against
 * contains only a subset of useful features. When we leave these features in the lexicon,
 * we end up with bloated lexicons and weight vectors. This leads to larger than necessary
 * models.<p>
 * This package contains an interface that defines the lifecycle for the pruning
 * process, as well as some implementations, one that takes multiple weight vectors (for
 * multi-class network learners), and one that takes only one weight vector.
 * @author redman
 */
package edu.illinois.cs.cogcomp.lbjava.learn.featurepruning;