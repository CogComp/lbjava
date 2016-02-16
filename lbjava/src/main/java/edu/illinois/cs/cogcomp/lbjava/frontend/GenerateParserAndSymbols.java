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
package edu.illinois.cs.cogcomp.lbjava.frontend;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import java_cup.Main;

/**
 * A wrapper for running {@link java_cup.Main} to generate the {@link parser}
 * and {@link sym} classes, as well as {@link SymbolNames}.
 * <br/>
 * This replaces the old automake configuration (and perl scripts)
 * with a Maven compatible version. 
 * <br/>
 * <b>NB:</b> java_cup.Main was last updated in 1996 back when people 
 * didn't capitalize their class names. Maybe an update is due? 
 * @author Christos Christodoulopoulos
 *
 */
public class GenerateParserAndSymbols {

	public GenerateParserAndSymbols() {
		// Assuming we are in /target/classes
		String sourcePath = getClass().getClassLoader().getResource("").getPath() + "../../src/main/";
		String classPath = getClass().getResource("").getPath();
		String packageName = classPath.substring(classPath.indexOf("classes/") + 8);
		String packagePath = sourcePath + "java/" + packageName;
		String cupFile = sourcePath + "lex/parser.cup";

		assert new File(cupFile).exists();		
		try {
			String[] javaCupArgs = { cupFile };
			// Step 1: Run CUP to generate parser.java and sys.java
			Main.main(javaCupArgs);

			// Step 2: Move the generated files to the appropriate directory
			File parserFile = new File("parser.java");
			File symFile = new File("sym.java");
			File newSymFile = new File(packagePath + "sym.java");
			assert parserFile.exists() && symFile.exists();
			parserFile.renameTo(new File(packagePath + "parser.java"));
			symFile.renameTo(newSymFile);

			// Step 3: Generate the SymbolNames class
			File symNamesFile = new File(packagePath + "SymbolNames.java");
			// Simply read the symbol names from sym.java and create a table
			BufferedReader in = new BufferedReader(new InputStreamReader(
					new FileInputStream(newSymFile)));
			String names = "";
			String line;
			while ((line = in.readLine()) != null) {
				if (!line.trim().startsWith("public static final int")) continue;
				String name = line.substring(line.indexOf("int") + 4, line.indexOf('=') - 1);
				names += "\"" + name + "\", ";
			}
			in.close();
			
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(symNamesFile)));
			String output = "package " + 
					packageName.replaceAll("/", ".").substring(0, packageName.length() - 1) + ";\n\n" +
					"public class SymbolNames {\n"+
					"\tpublic static String nameTable[] = {\n"+
					"\t\t" + names.substring(0, names.length() - 2) + "\n" +
					"\t};\n" +
					"}";
			out.write(output);
			out.close();
		}
		catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public static void main(String[] args) {
		new GenerateParserAndSymbols();
	}

}
