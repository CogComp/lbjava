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


public class SetCoverSolver {
	public static void main(String[] args){
	
    containsStationConstrained classifier = new containsStationConstrained();
		
		for(String file : args){
			City c = new City(file);
			for (Neighborhood n : c.getNeighborhoods()){
				System.out.println(n.getNumber() + ": " + classifier.discreteValue(n));
			}
		}
	}
}

