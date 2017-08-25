/**
 * This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 *
 * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.illinois.cs.cogcomp.lbjava.learn.featurepruning;

import java.util.*;
import java.util.Map.Entry;

import edu.illinois.cs.cogcomp.lbjava.classify.Feature;
import edu.illinois.cs.cogcomp.lbjava.learn.Lexicon;
import edu.illinois.cs.cogcomp.lbjava.learn.SupportVectorMachine;

/**
 * Optimized a support vector machine by discarding any sufficiently low weights.
 * @author redman
 */
public class SupportVectorMachineOptimizer extends LexiconOptimizer {
    
    /** the model we are going to optimize. */
    private SupportVectorMachine svm = null;
    
    /** the number of classes, if the numclasses is two, consider it binary and change to one. */
    public int numberclasses = -1;
    
    /** turn this on to produce TONS of diagnostics, lists what feature are pruned and what remain. */
    final private boolean debug = false;
    
    /** the biasfeatures are 0 for no added bias features, or 1 if bias is added. */
    public int biasfeatures = 0;
    
    /**
     * Take lex and model, and optimize the model by pruning the weights. Any zero weights get pruned.
     * @param lexicon the lexicon with the feature map.
     * @param s the support vector machine.
     */
    public SupportVectorMachineOptimizer(SupportVectorMachine s) {
        super(s.demandLexicon(), s.featurePruningThreshold);
        this.svm = s;
        
        // the numClasses field gets change in the write method to allow for the binary case
        // which is actually two classes to behave as one class (binary).
        if (!s.getSolverType().equals("MCSVM_CS") && s.getNumClasses() <= 2)
            numberclasses = 1;
        else
            numberclasses = s.getNumClasses();
        
        // we need to figure out if we have a bias feature introduced
        this.biasfeatures = svm.getBiasFeatures();
    }
    
    /** 
     * When done, check the results to make sure none of the feature weights have changed.
     */
    public void optimize () {
        class FeatureFeatures {
            Feature feature;
            double processedweight;
            double realweight;
            FeatureFeatures (Feature feature, double pw, double rw, int i) {
                this.feature = feature;
                this.processedweight = pw;
                this.realweight = rw;
            }
            public String toString() {
                return this.feature.toStringNoPackage()+":"+processedweight+":"+realweight;
            }
        }
        /* the feature weights are used to validate the result at the end. */
        ArrayList<FeatureFeatures> featureweights = new ArrayList<>();
        
        // Get all the feature weights so we can make sure they line up when done.
        for (int i = 0; i < lexicon.size();i++)
            featureweights.add(new FeatureFeatures(lexicon.lookupKey(i),getWeight(i),svm.getWeights()[i], i));
        super.optimize();
        
        // get each feature, if it's gone, make sure it sucks, if it's not, ensure it doesn't
        int kept = 0;
        int discarded = 0;
        for (FeatureFeatures entry : featureweights) {
            if (lexicon.contains(entry.feature)) {
                int newindex = lexicon.lookup(entry.feature);
                kept++;
                if (debug)
                    System.out.println("Kept "+entry+" lexicon feature:"+lexicon.lookupKey(newindex).toStringNoPackage()+":"+svm.getWeights()[newindex]);
            } else {
                discarded++;
                if (debug)
                    System.out.println("Discarded "+entry);
            }
        }
        System.out.println("SVM optimization @ t="+this.threshold+" resulted in "+discarded+" discarded features of "+(discarded+kept)+" total features.");
    }
    
    /**
     * Determine if the provided feature has sum of weights greater than a threshold value, 
     * and discard the feature if it falls below.
     * @param lex the lexicon.
     * @param f the feature.
     * @return true if the feature has any value, there is a
     */
    protected boolean hasWeight(Lexicon lex, Feature f) {
        int index = lex.lookup(f);
        return getWeight(index) > this.threshold;
    }
    
    /**
     * Compute the single weight at the index as the sum of all weights for all classes.
     * @param index the index of the feature
     * @return the sum of the absolute value of all weights for the feature.
     */
    private double getWeight(int index) {
        double sum = 0;
        for (int i = 0; i < this.numberclasses; i++) {
            sum += Math.abs(svm.getWeights()[index]);
            index += (this.lexicon.size() + biasfeatures);
        }
        return sum;
    }

    /**
     * @see edu.illinois.cs.cogcomp.lbjava.learn.featurepruning.LexiconOptimizer#identifyUselessFeatures()
     */
    @Override
    protected int[] identifyUselessFeatures() {
        
        // compile the whitelist
        HashSet<Feature> whitelist = compileWhitelist(lexicon);

        // look at each feature in the lexicon, any with zero weights can be safely discarded.
        int [] all = new int [this.lexicon.size()];
        int count = 0;
        for (Object e : lexicon.getMap().entrySet()) {
            @SuppressWarnings("unchecked")
            Entry<Feature, Integer> entry = (Entry<Feature, Integer>) e;
            if (!whitelist.contains(entry.getKey())) {
                int fi = entry.getValue();
                double wt = getWeight(fi);
                if (wt < this.threshold) {
                    all[count] = fi;
                    count++;
                }
            }
        }
        int[] useless = new int[count];
        System.arraycopy(all, 0, useless, 0, count);
        Arrays.sort(useless);
        return useless;
    }

    /**
     * This method returns the number of features. This implementation assumes the 
     * lexicon is populated, but that's not always the case (with SVM for example appears
     * to not always have a populated lexicon). In these cases, this method may be overriden.
     * @return the number of featues.
     */
    protected int getNumberFeatures() {
        return this.svm.getNumFeatures();
    }
    
    /**
     * @see edu.illinois.cs.cogcomp.lbjava.learn.featurepruning.LexiconOptimizer#pruneWeights(int[], int)
     */
    @Override
    public void pruneWeights(int[] uselessfeatures, int originalNumFeatures) {
        this.svm.pruneWeights(uselessfeatures, originalNumFeatures);
    }
}
