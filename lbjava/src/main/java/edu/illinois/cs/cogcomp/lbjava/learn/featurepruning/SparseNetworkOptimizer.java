/**
 * This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 *
 * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.illinois.cs.cogcomp.lbjava.learn.featurepruning;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map.Entry;

import edu.illinois.cs.cogcomp.core.datastructures.vectors.OVector;
import edu.illinois.cs.cogcomp.lbjava.classify.Feature;
import edu.illinois.cs.cogcomp.lbjava.learn.Lexicon;
import edu.illinois.cs.cogcomp.lbjava.learn.LinearThresholdUnit;
import edu.illinois.cs.cogcomp.lbjava.learn.SparseAveragedPerceptron;
import edu.illinois.cs.cogcomp.lbjava.learn.SparseNetworkLearner;
import gnu.trove.set.hash.TIntHashSet;

/**
 * This class will optimize the SparseNetworkLearner by discarding all features
 * associated with no sufficiently high weight values. For the network learner, we
 * much check the weights across all the binary learners to determin the value 
 * of a particular feature.
 * @author redman
 */
public class SparseNetworkOptimizer extends LexiconOptimizer {

    /** the network learner we want to optimize. */
    private SparseNetworkLearner networkLearner;

    /**
     * Given the sparse net learner to optimize.
     * @param snl the sparse net learner.
     */
    public SparseNetworkOptimizer(SparseNetworkLearner snl) {
        super(snl.demandLexicon(), snl.getBaseLTU().featurePruningThreshold);
        networkLearner = snl;
    }

    /**
     * Determine if the provided feature has sum of weights greater than a threshold value, 
     * and discard the feature if it falls below.
     * @param lex the lexicon.
     * @param f the feature.
     * @return true if the feature has any value, there is a
     */
    protected boolean hasWeight(Lexicon lex, Feature f) {
        OVector net = networkLearner.getNetwork();
        if (net.size() == 0) 
            return false;
        int numberclasses = net.size();
        int i = 0;
        double sum = 0;
        int featureindex = lex.lookup(f);
        
        // we assume each element of the network is of the same type, if that type is sparse averaged 
        // perceptron, we check both the averaged and current weight
        if (net.get(0) instanceof SparseAveragedPerceptron) {
            for (; i < numberclasses; ++i) {
                SparseAveragedPerceptron sap = (SparseAveragedPerceptron) net.get(i);
                double wt = sap.getWeightVector().getRawWeights().get(featureindex);
                double avg = sap.getAveragedWeightVector().getRawWeights().get(featureindex);
                sum += Math.abs(wt);
                sum += Math.abs(avg);
                
                // if the value is sufficiently large, then we have a good weight and should keep.
                if (sum >= this.threshold)
                    return true;
            }
        } else {
            for (; i < numberclasses; ++i) {
                LinearThresholdUnit ltu = (LinearThresholdUnit) net.get(i);
                double wt = ltu.getWeightVector().getRawWeights().get(featureindex);
                sum += Math.abs(wt);
                
                // if the value is sufficiently large, then we have a good weight and should keep.
                if (sum >= this.threshold)
                    return true;
            }
        }
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
        Lexicon lex = networkLearner.demandLexicon();
        if (lex != null) {

            // we have the conjunctive features, if left, right, or the parent itself has a non zero weight,
            // consider non of the features (parent, left or right) useless, whitelist them.
            HashSet<Feature> whitelist = compileWhitelist(lex);
            int count = 0;
            int numberfeatures = lex.size();
            int[] all = new int[numberfeatures];
            TIntHashSet defunct = new TIntHashSet();
            
            // For each feature, determin it's value. We will interate over a map with features as key
            // and the integer index of the feature. If the feature is whitelisted, we keep, otherwise
            // check for uselessness and if so, add to the list.
            for (Object e : lex.getMap().entrySet()) {
                Entry<Feature, Integer> entry = (Entry<Feature, Integer>) e;
                if (!whitelist.contains(entry.getKey())) {
                    int fi = entry.getValue();
                    if (!hasWeight(lexicon, entry.getKey())) {
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
     * Not we remove the useless weights from ALL weight vectors. There must be the same number
     * of entries in each weight vector as there is in the lexicon.
     * @see edu.illinois.cs.cogcomp.lbjava.learn.featurepruning.LexiconOptimizer#pruneWeights(int[])
     */
    @Override
    public void pruneWeights(int[] uselessfeatures, int origNumFeatures) {
        OVector ltus = networkLearner.getNetwork();
        for (int i = 0; i < ltus.size(); i++) {
            LinearThresholdUnit ltu = (LinearThresholdUnit) ltus.get(i);
            ltu.pruneWeights(uselessfeatures, origNumFeatures);
        }
    }
}