/**
 * This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 *
 * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.illinois.cs.cogcomp.lbjava.learn.featurepruning;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map.Entry;

import edu.illinois.cs.cogcomp.lbjava.classify.Feature;
import edu.illinois.cs.cogcomp.lbjava.learn.Lexicon;
import edu.illinois.cs.cogcomp.lbjava.learn.LinearThresholdUnit;
import edu.illinois.cs.cogcomp.lbjava.learn.SparseAveragedPerceptron;
import gnu.trove.set.hash.TIntHashSet;

/**
 * This class will optimize any working LinearThresholdUnit subclass by pruning
 * low value features.
 * @author redman
 */
public class LinearThresholdUnitOptimizer extends LexiconOptimizer {

    /** the LTU learner we want to optimize. */
    private LinearThresholdUnit ltuLearner;

    /** this also for testing, save feature names we will delete, check the names when we do. */
    final ArrayList<Feature> uselessFeatureNames = new ArrayList<Feature>();

    /**
     * Given the LTU learner to optimize.
     * @param snl the LTU learner.
     */
    public LinearThresholdUnitOptimizer(LinearThresholdUnit ltu) {
        super(ltu.demandLexicon(), ltu.featurePruningThreshold);
        ltuLearner = ltu;
    }

    /**
     * Determine if the provided feature has sum of weights greater than a threshold value, 
     * and discard the feature if it falls below.
     * @param lex the lexicon.
     * @param f the feature.
     * @return true if the feature has any value, there is a
     */
    protected boolean hasWeight(Lexicon lex, Feature f) {
        int featureindex = lex.lookup(f);
        
        // we assume each element of the network is of the same type, if that type is sparse averaged 
        // perceptron, we check both the averaged and current weight
        double sum;
        if (this.ltuLearner instanceof SparseAveragedPerceptron) {
            SparseAveragedPerceptron sap = (SparseAveragedPerceptron) this.ltuLearner;
            double wt = sap.getWeightVector().getRawWeights().get(featureindex);
            double avg = sap.getAveragedWeightVector().getRawWeights().get(featureindex);
            sum = Math.abs(wt);
            sum += Math.abs(avg);
        } else {
            double wt = this.ltuLearner.getWeightVector().getRawWeights().get(featureindex);
            sum = Math.abs(wt);
        }
    
        // if the value is sufficiently large, then we have a good weight and should keep.
        if (sum > this.threshold)
            return true;
        else
            return false;
    }

    /**
     * In this case, we must check, for each feature, the associated set of weight in each weight
     * vector, if they are all very small, it is useless. The array returned is sorted ascending.
     * @return the set of useless features.
     * @see edu.illinois.cs.cogcomp.lbjava.learn.featurepruning.LexiconOptimizer#identifyUselessFeatures()
     */
    @SuppressWarnings("unchecked")
    @Override
    protected int[] identifyUselessFeatures() {
        Lexicon lex = this.ltuLearner.demandLexicon();
        if (lex != null) {
            HashSet<Feature> whitelist = compileWhitelist(lex);

            // we have the conjunctive features, if left, right, or the parent itself has a non zero weight,
            // consider non of the features (parent, left or right) useless, whitelist them.
            int count = 0;
            int numberfeatures = lex.size();
            int[] all = new int[numberfeatures];
            TIntHashSet defunct = new TIntHashSet();
            for (Object e : lex.getMap().entrySet()) {
                Entry<Feature, Integer> entry = (Entry<Feature, Integer>) e;
                int fi = entry.getValue();
                if (!whitelist.contains(entry.getKey())) {
                    double wt = Math.abs(this.ltuLearner.getWeightVector().getRawWeights().get(fi));

                    // if the value is sufficiently large, then we have a good weight and should keep.
                    if (wt < this.threshold) {
                        
                        // This is a useless feature
                        all[count] = fi;
                        if (defunct.contains(fi)) {
                            System.err.println("There was a feature discarded twice during feature pruning!");
                        } else {
                            defunct.add(fi);
                        }
                        this.uselessFeatureNames.add(entry.getKey());
                        count++;
                    }
                }
            }

            int[] useless = new int[count];
            System.arraycopy(all, 0, useless, 0, count);
            Arrays.sort(useless);
            return useless;
        } else
            return new int[0];
    }

    /**
     * Check it out when done, make sure it worked.
     */
    protected void pruneLexicon(int[] uselessfeatures) {
        super.pruneLexicon(uselessfeatures);
        for (Feature f : this.uselessFeatureNames) {
            if (lexicon.contains(f)) {
                throw new RuntimeException("The features were not correctly removed from the lexicon : " + f.getStringIdentifier());
            }
        }
    }

    /**
     * Not we remove the useless weights from ALL weight vectors. There must be the same number
     * of entries in each weight vector as there is in the lexicon.
     * @see edu.illinois.cs.cogcomp.lbjava.learn.featurepruning.LexiconOptimizer#pruneWeights(int[])
     */
    @Override
    public void pruneWeights(int[] uselessfeatures, int origNumFeatures) {
        this.ltuLearner.pruneWeights(uselessfeatures, origNumFeatures);
    }
}