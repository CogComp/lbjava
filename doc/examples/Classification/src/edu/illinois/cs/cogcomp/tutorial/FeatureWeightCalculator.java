package edu.illinois.cs.cogcomp.tutorial;

import LBJ2.classify.Feature;
import LBJ2.classify.FeatureVector;
import LBJ2.classify.ScoreSet;
import LBJ2.learn.Lexicon;

public class FeatureWeightCalculator {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BadgeClassifier bc = new BadgeClassifier();
		Lexicon lc = bc.getLexicon();
		for (int i = 0; i < lc.size(); i++) {
			Feature f = lc.lookupKey(i);
			System.out.println(f);
			int[] id = new int[1];
			double[] val = new double[1];
			id[0] = i;
			val[0] = 1;
			ScoreSet ss = bc.scores(id, val);
			System.out.println(ss.get("positive"));
		}
		System.out.println("threshold:" + bc.getThreshold());
	}

}
