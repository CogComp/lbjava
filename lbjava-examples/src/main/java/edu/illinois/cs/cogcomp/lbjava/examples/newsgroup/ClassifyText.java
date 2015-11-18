/**
 * 
 */
package edu.illinois.cs.cogcomp.lbjava.examples.newsgroup;

import edu.illinois.cs.cogcomp.lbjava.examples.Document;

import java.io.File;
import java.io.IOException;

/**
 * An application takes a file as an input and assigns a label it
 * 
 * @author Vivek Srikumar
 */
public class ClassifyText {

	public static void main(String[] args) throws IOException {

		if (args.length != 1) {
			System.out.println("Usage: ClassifyText file");
			System.exit(-1);
		}

		// This is the black box text classifier
		NewsGroupClassifier classifier = new NewsGroupClassifier();

		Document document = new Document(new File(args[0]));

		// Ask the classifier to label the document
		String label = classifier.discreteValue(document);
		System.out.println(label);
	}
}
