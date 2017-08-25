/**
 * This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 *
 * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
/**
 * <p>
 * For sparse learners, it is often the case that the array of features you learn 
 * contains only a subset of useful features. When we leave these features in the lexicon,
 * we end up with bloated lexicons and weight vectors. This leads to larger than necessary
 * models.<p>
 * 
 * This package contains an interface that defines the life cycle for the pruning
 * process, as well as some implementations, one that takes multiple weight vectors (for
 * multi-class network learners), and some that takes only one weight vector.<p>
 * 
 * All optimizers should subclass @see LexiconOptimizer which implements most of the
 * optimization. Subclass will need to provide methods to compute the weight value to compare
 * against the threshold, a method to identify the useless features, and a method to prune
 * those features.<p>
 * 
 * The optimizers are invoked by the doneTraining method of the Learner class when all learning
 * is complete. For those who have build their own training procedure, they are required to invoke 
 * the doneTraining and startTraining method during their training process.<p>
 * 
 * The pruning threshold value is provided by the specific learner, and should be, in one way or 
 * another, parameterized.<p>
 * @author redman
 */
package edu.illinois.cs.cogcomp.lbjava.learn.featurepruning;