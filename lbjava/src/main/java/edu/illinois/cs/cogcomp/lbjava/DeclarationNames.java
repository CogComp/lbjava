/**
 * This software is released under the University of Illinois/Research and
 *  Academic Use License. See the LICENSE file in the root folder for details.
 * Copyright (c) 2016
 *
 * Developed by:
 * The Cognitive Computations Group
 * University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.illinois.cs.cogcomp.lbjava;

import edu.illinois.cs.cogcomp.lbjava.IR.AST;
import edu.illinois.cs.cogcomp.lbjava.IR.Declaration;
import edu.illinois.cs.cogcomp.lbjava.IR.DeclarationList;


/**
  *
  * @author Nick Rizzolo
 **/
public class DeclarationNames extends Pass
{
  /**
    * Instantiates a pass that runs on an entire {@link AST}.
    *
    * @param ast  The program to run this pass on.
   **/
  public DeclarationNames(AST ast) { super(ast); }


  /**
    * Runs this pass on all nodes of the indicated type.
    *
    * @param list The node to process.
   **/
  public void run(DeclarationList list) {
    for (DeclarationList.DeclarationListIterator I = list.listIterator();
         I.hasNext(); ) {
      Declaration d = I.nextItem();
      System.out.println(d.name);
    }
  }
}

