/**
 * 
 */
package edu.illinois.cs.cogcomp.tutorial;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Vivek Srikumar
 * 
 */
public class Document {

	private final String label;
	private final List<String> words;
	private String guid;

	/**
	 * Create a new document
	 * 
	 * @throws IOException
	 */
	public Document(File file, String label) throws IOException {
		this.label = label;
		BufferedReader reader = new BufferedReader(new FileReader(file));

		words = new ArrayList<String>();
		String line = null;
		while ((line = reader.readLine()) != null) {
			for (String word : line.split("\\s+"))
				words.add(word.trim());
		}

		reader.close();
	}

	public Document(File file) throws IOException {
		this(file, "unknown");
	}

	public Document(List<String> words) {

		this(words, "unknown");
	}

	public void setGUID(String guid) {
		this.guid = guid;
	}
	
	public String getGUID(){
		return this.guid;
	}
	

	public Document(List<String> words, String label) {
		this.words = words;
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public List<String> getWords() {
		return Collections.unmodifiableList(words);
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return label + ", " + words;
	}

}
