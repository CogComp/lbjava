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
package edu.illinois.cs.cogcomp.lbjava.examples.setCover;

import java.util.Collection;
import java.util.TreeMap;
import edu.illinois.cs.cogcomp.lbjava.parse.LineByLine;

public class City {

  TreeMap<Integer, Neighborhood> neighborhoods =
    new TreeMap<Integer, Neighborhood>();

  public City(String file){
    LineByLine parser = new LineByLine(file) {
      public Object next() {
        return readLine();
      }
    };

    String line = null;
    while((line = (String) parser.next()) != null){
      String[] definition = line.split("\\s+");
      Integer index = new Integer(definition[0]);
      Neighborhood n = getNeighborhood(index);
      for(int i = 1; i < definition.length; i++){
        n.addNeighbor(getNeighborhood(new Integer(definition[i])));
      }
    }
  }

  public Collection<Neighborhood> getNeighborhoods(){
    return neighborhoods.values();
  }

  public Neighborhood getNeighborhood(Integer index) {
    Neighborhood n = neighborhoods.get(index);
    if (n == null) {
      n = new Neighborhood(index, this);
      neighborhoods.put(index, n);
    }

    return n;
  }
}

