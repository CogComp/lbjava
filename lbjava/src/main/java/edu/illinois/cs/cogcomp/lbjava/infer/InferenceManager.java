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
package edu.illinois.cs.cogcomp.lbjava.infer;

import java.util.LinkedHashMap;


/**
  * The inference manager is a cache of <code>Inference</code> objects
  * accessed via their names and head objects.  However, only one
  * <code>Inference</code> object is stored per <code>Inference</code> class.
  * For example, if the LBJava source file contains the following two
  * <code>inference</code>s:
  *
  * <blockquote>
  *   <code>inference Foo head MyClass { ... }</code> <br>
  *   <code>inference Bar head MyClass { ... }</code>
  * </blockquote>
  *
  * then this <code>InferenceManager</code> will store a maximum of one
  * <code>Foo</code> object and one <code>Bar</code> object.
  *
  * @author Nick Rizzolo
 **/
public class InferenceManager
{
  /**
    * The cache of <code>Inference</code> objects, indexed by
    * <code>Key</code>s.
   **/
  private static final LinkedHashMap cache = new LinkedHashMap();


  /**
    * Adds the given <code>Inference</code> object to the cache, indexed its
    * fully qualified name.
    *
    * @param i  The inference object.
   **/
  public static void put(Inference i) {
    cache.put(i.getClass().getName(), i);
  }

  /**
    * Adds the given <code>Inference</code> object to the cache, indexed by an
    * arbitrary name (NB: Don't use unless you know what you're doing).
    *
    * @param name The (arbitrary) name of the inference object.
    * @param i    The inference object.
   **/
  public static void put(String name, Inference i) {
    cache.put(name, i);
  }

  /**
    * Retrieves the <code>Inference</code> object whose fully qualified name
    * and head object are specified.
    *
    * @param n  The fully qualified name of the inference.
    * @param h  The head object of the inference.
    * @return The <code>Inference</code> object corresponding to the given
    *         parameters, or <code>null</code> if no <code>Inference</code> is
    *         associated with them.
   **/
  public static Inference get(String n, Object h) {
    Inference result = (Inference) cache.get(n);
    if (result != null && result.getHead() == h) return result;
    return null;
  }


  /**
    * Removes the inference object with the given name.
    *
    * @param n  The name of the unwanted inference.
   **/
  public static void remove(String n) { cache.remove(n); }


  /**
    * Objects of this class are used as the keys of the <code>cache</code>
    * map.  They are distinguished from each other by comparing the contents
    * of the <code>String</code>s storing their names and by their head
    * objects, which must actually be exactly the same object for the two keys
    * to be equivalent.
    *
    * @author Nick Rizzolo
   ** /
  private static class Key
  {
    /** The name of the inference. * /
    public String name;
    /** The inference's head object. * /
    public Object head;


    /**
      * Initializing constructor.
      *
      * @param n  The name of the inference.
      * @param h  The inference's head object.
     ** /
    public Key(String n, Object h) {
      name = n;
      head = h;
    }


    /**
      * The hash code of a <code>Key</code> is the hash code of its name plus
      * the system's hash code for the head object.
     ** /
    public int hashCode() {
      return name.hashCode() + System.identityHashCode(head);
    }


    /** Two <code>Key</code>s are equivalent as described above. * /
    public boolean equals(Object o) {
      Key k = (Key) o;
      return name.equals(k.name) && head == k.head;
    }
  }
  */
}

