package edu.illinois.cs.cogcomp.lbjava.examples.setCover;

import java.util.TreeSet;

public class Neighborhood implements Comparable<Neighborhood> {

  City parent;
	Integer number;
	TreeSet<Neighborhood> neighbors = new TreeSet<Neighborhood>();
	
	public Neighborhood(Integer n, City p){
		number = n;
    parent = p;
	}
	
	public Integer getNumber(){
		return number;
	}

  public TreeSet<Neighborhood> getNeighbors() { return neighbors; }

	public void addNeighbor(Neighborhood n){
		neighbors.add(n);
	}

  public City getParentCity() { return parent; }

  public int compareTo(Neighborhood n) {
    return number.compareTo(n.number);
  }

  public int hashCode() { return number.hashCode(); }

  public boolean equals(Object o) {
    if (!(o instanceof Neighborhood)) return false;
    return ((Neighborhood) o).number.equals(number);
  }

  public String toString() { return "neighborhood #" + number; }
}

