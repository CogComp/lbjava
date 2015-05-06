package edu.illinois.cs.cogcomp.tutorial;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class SpamClassifierApplication {

	public static void main(String[] args) {
            // Here we call the classifier we have created, just like
            // any other java object.
            SpamClassifier sc = new SpamClassifier();
            
            System.out.print("Enter text to be classified (Ctrl-D to quit):\n>> ");
                
            // Read data in
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNextLine()) {
                String email = scanner.nextLine();
                String[] words = email.split(" ");
                
                List<String> docwords = new ArrayList<String>();
                docwords.addAll(Arrays.asList(words));

                // Recall: the SpamClassifier understands how
                // to deal with Documents, so we create one.
                Document doc = new Document(docwords);

                // Now we predict the label of that Document. 
                String label = sc.discreteValue(doc);

                // Hopefully this is correct!
                System.out.println("Classified as: " + label);
                System.out.print("Enter text to be classified (Ctrl-D to quit):\n>> ");
            }
	}   
}
