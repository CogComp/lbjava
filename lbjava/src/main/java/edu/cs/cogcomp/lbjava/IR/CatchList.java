/**
 * This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 *
 * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.cs.cogcomp.lbjava.IR;

import java.util.Iterator;

import edu.cs.cogcomp.lbjava.Pass;


/**
 * Currently, this is just a wrapper class for <code>LinkedList</code>. The code that uses it looks
 * a little cleaner when casts are all taken care of automatically.
 *
 * @author Nick Rizzolo
 **/
public class CatchList extends List {
    /** Default constructor. */
    public CatchList() {
        super(-1, -1, " ");
    }

    /**
     * Initializing constructor. Requires its argument to be non-<code>null</code>.
     *
     * @param s A single <code>CatchClause</code> with which to initialize this list.
     **/
    public CatchList(CatchClause s) {
        super(s.line, s.byteOffset, " ");
        list.add(s);
    }


    /**
     * Adds another <code>CatchClause</code> to the end of the list.
     *
     * @param s A reference to the <code>CatchClause</code> to be added.
     **/
    public void add(CatchClause s) {
        list.add(s);
    }


    /**
     * Adds all the <code>CatchClause</code>s in another <code>CatchList</code> to the end of this
     * <code>CatchList</code>.
     *
     * @param s The list to be added.
     **/
    public void addAll(CatchList s) {
        list.addAll(s.list);
    }


    /**
     * Transforms the list into an array of statements.
     *
     * @return An array of statements containing references to every statement in the list.
     **/
    public CatchClause[] toArray() {
        return (CatchClause[]) list.toArray(new CatchClause[list.size()]);
    }


    /**
     * Returns an iterator used specifically to access the elements of this list.
     *
     * @return An iterator used specifically to access the elements of this list.
     **/
    public CatchListIterator listIterator() {
        return new CatchListIterator();
    }


    /**
     * Returns an iterator used to successively access the children of this node.
     *
     * @return An iterator used to successively access the children of this node.
     **/
    public ASTNodeIterator iterator() {
        return listIterator();
    }


    /**
     * Creates a new object with the same primitive data, and recursively creates new member data
     * objects as well.
     *
     * @return The clone node.
     **/
    public Object clone() {
        CatchList clone = new CatchList();
        for (Iterator i = list.iterator(); i.hasNext();)
            clone.list.add(((CatchClause) i.next()).clone());
        return clone;
    }


    /**
     * Ensures that the correct <code>run()</code> method is called for this type of node.
     *
     * @param pass The pass whose <code>run()</code> method should be called.
     **/
    public void runPass(Pass pass) {
        pass.run(this);
    }


    /**
     * Used to iterate though the children of a list of AST nodes. The entire interface of
     * <code>java.util.ListIterator</code> is exposed through this class.
     *
     * @author Nick Rizzolo
     **/
    public class CatchListIterator extends NodeListIterator {
        /**
         * Returns the next AST node in the list. This method may be called repeatedly to iterate
         * through the list, or intermixed with calls to <code>previous()</code> to go back and
         * forth. (Note that alternating calls to <code>next()</code> and <code>previous()</code>
         * will return the same element repeatedly.)
         *
         * @return The next AST node in the list.
         **/
        public CatchClause nextItem() {
            return (CatchClause) I.next();
        }


        /**
         * Returns the previous element in the list. This method may be called repeatedly to iterate
         * through the list backwards, or intermixed with calls to next to go back and forth. (Note
         * that alternating calls to next and previous will return the same element repeatedly.)
         *
         * @return The previous AST node in the list.
         **/
        public CatchClause previousItem() {
            return (CatchClause) I.previous();
        }
    }
}
