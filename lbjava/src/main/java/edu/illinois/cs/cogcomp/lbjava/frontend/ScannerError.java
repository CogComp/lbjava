/**
 * This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 *
 * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.illinois.cs.cogcomp.lbjava.frontend;


/**
  * This class prints useful error messages for scanner generated errrors.
  *
  * @author Nick Rizzolo
 **/
class ScannerError
{
  /**
    * Signal an error due to an unterminated comment appearing in the token
    * stream.
   **/
  public static void unterminatedCommentError() {
    System.err.println("Unterminated comment.");
  }

  /**
    * Signal an error due to discovering an end-of-comment marker while not
    * scanning a comment.
   **/
  public static void commentEndWithoutBegin() {
    System.err.println("Comment ending encountered without beginning.");
  }

  /**
    * Signal an error due to an invalid character (one which is not specified
    * as being allowed by the language definition) in the source text.
   **/
  public static void illegalCharacterError() {
    System.err.println("Illegal character");
  }

  /**
    * Signal an error in scanning which does not fall into any of the above
    * categories.
   **/
  public static void otherError() {
    System.err.println("Other");
  }
}

