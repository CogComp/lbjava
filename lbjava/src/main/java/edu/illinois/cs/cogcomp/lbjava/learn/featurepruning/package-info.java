/**
 * This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 *
 * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
/**
 * <p>For sparse learners, it is often the case that the array of features you learn 
 * contains only a subset of useful features. When we leave these features in the lexicon,
 * we end up with bloated lexicons and weight vectors. This leads to larger than necessary
 * models.</p>
 * 
 * <p>This package contains an interface that defines the life cycle for the feature pruning
 * process, as well as some implementations, one that takes multiple weight vectors (for
 * multi-class network learners), and some that takes only one weight vector.</p>
 * 
 * <p>All optimizers should subclass @see LexiconOptimizer which implements most of the
 * optimization. Subclass will need to provide methods to compute the weight value to compare
 * against the threshold, a method to identify the useless features, and a method to prune
 * those features.</p>
 * 
 * <p>The optimizers are invoked by the {@link edu.illinois.cs.cogcomp.lbjava.learn.Learner#doneTraining}
 * method of the Learner class when all learning is complete. For those learners that include a feature
 * pruning implementation, they must override this method to invoke the optimizer. In this way, during the
 * normal LBJava compile and model build cycle, the optimization is performed automatically. For those
 * who have build their own training procedure, they are required to invoke the doneTraining and 
 * {@link edu.illinois.cs.cogcomp.lbjava.learn.Learner#startTraining} method at appropriate points during
 * their training process.</p>
 * 
 * <p>The learner classes typically have a parameter that can be set to change the default feature
 * pruning threshold to any the user might choose, or it can be set to 0.0 to disable. </p>
 * 
 * <p>The pruning threshold value is provided by the specific learner, and should be, in one way or 
 * another, parameterized.</p>
 * @author redman
 */
package edu.illinois.cs.cogcomp.lbjava.learn.featurepruning;