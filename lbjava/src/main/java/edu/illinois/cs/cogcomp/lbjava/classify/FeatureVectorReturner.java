/**
 * This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 *
 * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.illinois.cs.cogcomp.lbjava.classify;


/**
 * This classifier expects <code>FeatureVector</code>s as input, and it simply returns them as
 * output.
 *
 * @author Nick Rizzolo
 **/
public class FeatureVectorReturner extends Classifier {
    /** Default constructor. */
    public FeatureVectorReturner() {
        super("FeatureVectorReturner");
    }


    /**
     * This method makes one or more decisions about a single object, returning those decisions as
     * <code>Feature</code>s in a vector.
     *
     * @param o The object to make decisions about.
     * @return A vector of <code>Feature</code>s about the input object.
     **/
    public FeatureVector classify(Object o) {
        FeatureVector result = null;

        try {
            result = (FeatureVector) o;
        } catch (ClassCastException e) {
            System.err.println("LBJava ERROR: FeatureVectorReturner received a '"
                    + o.getClass().getName() + "' as input.");
            System.exit(1);
        }

        return result;
    }


    /**
     * Returns a string describing the input type of this classifier.
     *
     * @return A string describing the input type of this classifier.
     **/
    public String getInputType() {
        return "edu.illinois.cs.cogcomp.lbjava.classify.FeatureVector";
    }


    /**
     * Returns a string describing the output type of this classifier.
     *
     * @return A string describing the output type of this classifier.
     **/
    public String getOutputType() {
        return "mixed%";
    }


    /** Simply returns the string <code>"FeatureVectorReturner"</code>. */
    public String toString() {
        return "FeatureVectorReturner";
    }
}
