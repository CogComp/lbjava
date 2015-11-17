package edu.illinois.cs.cogcomp.lbjava;

import edu.illinois.cs.cogcomp.lbjava.parse.LineByLine;

import java.util.Vector;

/**
 * File reader used for testing.
 * @author Nick Rizzolo
 * @author Christos Christodoulopoulos
 *
 */
public class VectorParser extends LineByLine {

	public VectorParser(String file) { super(file); }

	public Object next() {
		String line = readLine();
		if (line == null) return null;

		String[] features = line.split(", ");
		Vector<Double> result = new Vector<Double>();
		for (int i = 0; i < 101; ++i) result.add(null);

		for (String feature1 : features) {
			int paren = feature1.indexOf('(');
			int feature = Integer.parseInt(feature1.substring(0, paren));
			double value = Double.parseDouble(feature1.substring(paren + 1, feature1.length() - 1));
			result.set(feature, value);
		}

		return result;
	}
}