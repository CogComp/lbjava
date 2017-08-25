/**
 * This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 *
 * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.illinois.cs.cogcomp.lbjava.learn.featurepruning;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map.Entry;

import edu.illinois.cs.cogcomp.lbjava.classify.DiscreteConjunctiveFeature;
import edu.illinois.cs.cogcomp.lbjava.classify.Feature;
import edu.illinois.cs.cogcomp.lbjava.classify.RealConjunctiveFeature;
import edu.illinois.cs.cogcomp.lbjava.learn.Lexicon;

/**
 * This class defines the life cycle methods for pruning useless features
 * from a lexicon. Features for example that carry zero weights with them are 
 * not useful to the model, so can be eliminated saving space and execution time, without
 * affecting accuracy (much).
 * @author redman
 */
abstract public class LexiconOptimizer {
    
    /** any weight less than this is considered irrelevant. This is for prunning. */
    private static final double PRUNING_THRESHOLD = 0.000001;

	/** lexicon contains the features we will operate on. */
	protected Lexicon lexicon;

    /** this also for testing, save feature names we will delete, check the names when we do. */
    final protected ArrayList<Feature> uselessFeatureNames = new ArrayList<Feature>();

    /** this is the threshold we use to discard useless features. */
    protected double threshold = PRUNING_THRESHOLD;
    
    /**
	 * We must have a lexicon to perform this operation.
     * @param lexicon the lexicon object.
     * @param threshold the feature pruning threshold.
	 */
	protected LexiconOptimizer(Lexicon lexicon, double threshold) {
		this.lexicon = lexicon;
		this.threshold = threshold;
	}
	
    /**
     * Determine if the provided feature has sum of weights greater than a threshold value, 
     * and discard the feature if it falls below.
     * @param lex the lexicon.
     * @param f the feature.
     * @return true if the feature has any value, there is a
     */
    abstract protected boolean hasWeight(Lexicon lex, Feature f);

    /**
     * This method returns the number of features. This implementation assumes the 
     * lexicon is populated, but that's not always the case (with SVM for example appears
     * to not always have a populated lexicon). In these cases, this method may be overriden.
     * @return the number of featues.
     */
    protected int getNumberFeatures() {
        return lexicon.size();
    }
    
	/** 
	 * do the optimization
	 */
	public void optimize () {
		
		int originalNumFeatures = this.getNumberFeatures();
		int [] uselessfeatures = identifyUselessFeatures();
		pruneWeights(uselessfeatures, originalNumFeatures);
		pruneLexicon(uselessfeatures);
		
		System.out.println("LexiconOptimizer optimization complete, pruned "
		                +uselessfeatures.length+" features of "+originalNumFeatures+", leaving "+(originalNumFeatures - uselessfeatures.length)+
		                " at threshold of "+threshold);
	}

    /**
     * @param f the feature.
     * @return true if the feature is conjunctive.
     */
    static private boolean isConjunctive(Feature f) {
        return (f instanceof DiscreteConjunctiveFeature || f instanceof RealConjunctiveFeature);
    }

    /**
     * If this conjunctive feature has weight, add it and all it's children to the white list.
     * @param lex the lexicon maps feature to index.
     * @param whitelist the white list we will add to.
     * @param f the conjunctive feature.
     */
    private void traverseConjunctiveTree(HashSet<Feature> whitelist, Feature f) {

        // add the conjunctive feature.
        whitelist.add(f);

        if (f instanceof DiscreteConjunctiveFeature) {

            // add it's direct children
            DiscreteConjunctiveFeature dcf = (DiscreteConjunctiveFeature) f;
            whitelist.add(dcf.getLeft());
            whitelist.add(dcf.getRight());

            // possible add any children of children.
            if (isConjunctive(dcf.getLeft()))
                traverseConjunctiveTree(whitelist, dcf.getLeft());
            if (isConjunctive(dcf.getRight()))
                traverseConjunctiveTree(whitelist, dcf.getRight());
        } else {

            // add it's direct children
            RealConjunctiveFeature rcf = (RealConjunctiveFeature) f;
            whitelist.add(rcf.getLeft());
            whitelist.add(rcf.getRight());

            // possible add any children of children.
            if (isConjunctive(rcf.getLeft()))
                traverseConjunctiveTree(whitelist, rcf.getLeft());
            if (isConjunctive(rcf.getRight()))
                traverseConjunctiveTree(whitelist, rcf.getRight());
        }
    }

    /**
     * Find all features we must whitelist. For each conjunctive feature that has weight, we must keep
     * all it's children, regardless of weight, and the rest of the tree from there on down.
     * @param lex the lexicon.
     * @return the conjunctive features.
     */
    protected HashSet<Feature> compileWhitelist(Lexicon lex) {
        HashSet<Feature> whitelist = new HashSet<Feature>();
        for (Object e : lex.getMap().entrySet()) {
            @SuppressWarnings("unchecked")
            Entry<Feature, Integer> entry = (Entry<Feature, Integer>) e;
            Feature f = entry.getKey();
            if (isConjunctive(f) && this.hasWeight(lex, f)) {

                // add this conjunctive feature and all it's kids to the whitelist.
                traverseConjunctiveTree(whitelist, f);
            }
        }
        return whitelist;
    }


	/**
	 * Given a list of useless features, prune the entries from the lexicon.
	 * @param uselessfeatures
	 */
	protected void pruneLexicon(int[] uselessfeatures) {
		lexicon.discardPrunedFeatures(uselessfeatures);
        for (Feature f : this.uselessFeatureNames) {
            if (lexicon.contains(f)) {
                throw new RuntimeException("The features were not correctly removed from the lexicon : " + f.getStringIdentifier());
            }
        }
	}

	/**
	 * This method selects the features to be pruned. If weights
	 * are needed, they must be passed to the constructor and stored in fields of
	 * the implementing class. In this way, we make no assumptions about the 
	 * structure of the weight classes.
	 * @return
	 */
	abstract protected int[] identifyUselessFeatures();
	
	/**
	 * Once we have identified the useless entries, we need to optimize the
	 * model components.
	 * @param uselessfeatures the indices of those features with no significant weights.
	 * @param originalNumFeatures the number of features in the original lexicon.
	 */
	abstract public void pruneWeights(int[] uselessfeatures, int originalNumFeatures);
}
