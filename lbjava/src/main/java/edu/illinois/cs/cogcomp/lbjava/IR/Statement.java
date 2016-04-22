/**
 * This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 *
 * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.illinois.cs.cogcomp.lbjava.IR;


/**
 * Abstract class from which statements are derived. LBJava currently has only one type of
 * statement: the assignment statement.
 *
 * @author Nick Rizzolo
 **/
public abstract class Statement extends ASTNode {
    /**
     * Default constructor.
     *
     * @param line The line on which the source code represented by this node is found.
     * @param byteOffset The byte offset from the beginning of the source file at which the source
     *        code represented by this node is found.
     **/
    Statement(int line, int byteOffset) {
        super(line, byteOffset);
    }
}
