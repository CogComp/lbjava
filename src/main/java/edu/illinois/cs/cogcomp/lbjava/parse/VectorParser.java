package edu.illinois.cs.cogcomp.lbjava.parse;

import java.util.Vector;

import edu.illinois.cs.cogcomp.lbjava.parse.LineByLine;

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

		for (int i = 0; i < features.length; ++i) {
			int paren = features[i].indexOf('(');
			int feature = Integer.parseInt(features[i].substring(0, paren));
			double value =
					Double.parseDouble(features[i].substring(paren + 1,
							features[i].length() - 1));
			result.set(feature, new Double(value));
		}

		return result;
	}
}