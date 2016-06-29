/**
 * This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 *
 * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.illinois.cs.cogcomp.lbjava.classify;

import edu.illinois.cs.cogcomp.core.datastructures.vectors.ExceptionlessInputStream;
import edu.illinois.cs.cogcomp.core.datastructures.vectors.ExceptionlessOutputStream;
import edu.illinois.cs.cogcomp.lbjava.learn.ChildLexicon;
import edu.illinois.cs.cogcomp.lbjava.learn.Lexicon;
import edu.illinois.cs.cogcomp.lbjava.util.ByteString;

/**
 * A referring real feature is one that has its own identifier, but whose value comes from a
 * separate feature that it refers to.
 *
 * @author Nick Rizzolo
 **/
public abstract class RealReferrer extends RealFeature {
    /** The feature being referred to. */
    protected RealFeature referent;


    /**
     * For internal use only.
     *
     * @see Feature#readFeature(ExceptionlessInputStream)
     **/
    protected RealReferrer() {}

    /**
     * Sets both the identifier and the referent.
     *
     * @param c The classifier that produced this feature.
     * @param r The real feature referred to by this new feature.
     **/
    public RealReferrer(Classifier c, RealFeature r) {
        this(c.containingPackage, c.name, r);
    }

    /**
     * Sets both the identifier and the referent.
     *
     * @param p The new real feature's package.
     * @param c The name of the classifier that produced this feature.
     * @param r The real feature referred to by this new feature.
     **/
    public RealReferrer(String p, String c, RealFeature r) {
        super(p, c);
        referent = r;
    }


    /**
     * Determines if this feature is a referring feature.
     *
     * @return <code>true</code> iff this feature is a referring feature.
     **/
    public boolean isReferrer() {
        return true;
    }


    /** Returns the value of {@link #referent}. */
    public RealFeature getReferent() {
        return referent;
    }


    /**
     * The depth of a feature is one more than the maximum depth of any of its children, or 0 if it
     * has no children.
     *
     * @return The depth of this feature as described above.
     **/
    public int depth() {
        return referent.depth() + 1;
    }


    /** Simply returns the strength of {@link #referent}. */
    public double getStrength() {
        return referent.getStrength();
    }


    /**
     * Takes care of any feature-type-specific tasks that need to be taken care of when removing a
     * feature of this type from a {@link ChildLexicon}, in particular updating parent counts and
     * removing children of this feature if necessary.
     *
     * @param lex The child lexicon this feature is being removed from.
     **/
    public void removeFromChildLexicon(ChildLexicon lex) {
        lex.decrementParentCounts(referent);
    }


    /**
     * Does a feature-type-specific lookup of this feature in the given {@link ChildLexicon}.
     *
     * @param lex The child lexicon this feature is being looked up in.
     * @param label The label of the example containing this feature, or -1 if we aren't doing per
     *        class feature counting.
     * @return The index of <code>f</code> in this lexicon.
     **/
    public int childLexiconLookup(ChildLexicon lex, int label) {
        return lex.childLexiconLookup(this, label);
    }


    /**
     * The hash code of a <code>RealReferrer</code> is the sum of the hash codes of the containing
     * package, the identifier, and the referent feature.
     *
     * @return The hash code for this feature.
     **/
    public int hashCode() {
        return 17 * super.hashCode() + referent.hashCode();
    }


    /**
     * Used to sort features into an order that is convenient both to page through and for the
     * lexicon to read off disk.
     *
     * @param o An object to compare with.
     * @return Integers appropriate for sorting features first by package, then by identifier, then
     *         by value.
     **/
    public int compareTo(Object o) {
        int d = compareNameStrings(o);
        if (d != 0)
            return d;
        RealReferrer r = (RealReferrer) o;
        if (d != 0)
            return d;
        return referent.compareTo(r.referent);
    }


    /**
     * Writes a string representation of this <code>Feature</code> to the specified buffer.
     *
     * @param buffer The buffer to write to.
     **/
    public void write(StringBuffer buffer) {
        writeNameString(buffer);
        buffer.append("->");
        referent.write(buffer);
    }


    /**
     * Writes a string representation of this <code>Feature</code> to the specified buffer, omitting
     * the package name.
     *
     * @param buffer The buffer to write to.
     **/
    public void writeNoPackage(StringBuffer buffer) {
        String p = containingPackage;
        containingPackage = null;
        writeNameString(buffer);
        buffer.append("->");
        referent.writeNoPackage(buffer);
        containingPackage = p;
    }


    /**
     * Writes a complete binary representation of the feature.
     *
     * @param out The output stream.
     **/
    public void write(ExceptionlessOutputStream out) {
        super.write(out);
        referent.write(out);
    }


    /**
     * Reads the representation of a feaeture with this object's run-time type from the given
     * stream, overwriting the data in this object.
     *
     * @param in The input stream.
     **/
    public void read(ExceptionlessInputStream in) {
        super.read(in);
        referent = (RealFeature) Feature.readFeature(in);
    }


    /**
     * Writes a binary representation of the feature intended for use by a lexicon, omitting
     * redundant information when possible.
     *
     * @param out The output stream.
     * @param lex The lexicon out of which this feature is being written.
     * @param c The fully qualified name of the assumed class. The runtime class of this feature
     *        won't be written if it's equivalent to <code>c</code>.
     * @param p The assumed package string. This feature's package string won't be written if it's
     *        equivalent to <code>p</code>.
     * @param g The assumed classifier name string. This feature's classifier name string won't be
     *        written if it's equivalent to <code>g</code>.
     * @param si The assumed identifier as a string. If this feature has a string identifier, it
     *        won't be written if it's equivalent to <code>si</code>.
     * @param bi The assumed identifier as a byte string. If this feature has a byte string
     *        identifier, it won't be written if it's equivalent to <code>bi</code>.
     * @return The name of the runtime type of this feature.
     **/
    public String lexWrite(ExceptionlessOutputStream out, Lexicon lex, String c, String p,
            String g, String si, ByteString bi) {
        String result = super.lexWrite(out, lex, c, p, g, si, bi);
        out.writeInt(lex.lookupChild(referent));
        return result;
    }


    /**
     * Reads the representation of a feature with this object's run-time type as stored by a
     * lexicon, overwriting the data in this object.
     *
     * <p>
     * This method is appropriate for reading features as written by
     * {@link #lexWrite(ExceptionlessOutputStream,Lexicon,String,String,String,String,ByteString)}.
     *
     * @param in The input stream.
     * @param lex The lexicon we are reading in to.
     * @param p The assumed package string. If no package name is given in the input stream, the
     *        instantiated feature is given this package.
     * @param g The assumed classifier name string. If no classifier name is given in the input
     *        stream, the instantiated feature is given this classifier name.
     * @param si The assumed identifier as a string. If the feature being read has a string
     *        identifier field and no identifier is given in the input stream, the feature is given
     *        this identifier.
     * @param bi The assumed identifier as a byte string. If the feature being read has a byte
     *        string identifier field and no identifier is given in the input stream, the feature is
     *        given this identifier.
     **/
    public void lexRead(ExceptionlessInputStream in, Lexicon lex, String p, String g, String si,
            ByteString bi) {
        super.lexRead(in, lex, p, g, si, bi);
        referent = (RealFeature) lex.lookupKey(in.readInt());
    }
}
