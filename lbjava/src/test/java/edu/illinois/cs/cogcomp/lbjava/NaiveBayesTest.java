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
package edu.illinois.cs.cogcomp.lbjava;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import org.junit.After;
import org.junit.Test;

public class NaiveBayesTest {

	@Test
	public void test() throws Exception {
		File lbjFile = new File(getClass().getResource("/lbj/naive-bayes.lbj").getFile());
		
		// The auto-generated code directory needs to be added to classpath
		addPath(getClass().getResource("/lbj/."));
		
		assertTrue(lbjFile.exists());
		
		String[] args = { "-d", lbjFile.getParent(), lbjFile.getPath() };
		Main.main(args);
	}

    @After
    public void cleanup() {
        //Make sure we don't leave our auto-generated files behind
        File lbjDir = new File(Main.generatedSourceDirectory);
        File[] dirFiles = lbjDir.listFiles(new FilenameFilter() {

            public boolean accept(File dir, String name) {
                return !name.endsWith(".lbj");
            }
        });
        for (File file: dirFiles) assert file.delete() : "Could not delete file " + file;
    }
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static void addPath(URL u) throws Exception {
	    URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
	    Class urlClass = URLClassLoader.class;
	    Method method = urlClass.getDeclaredMethod("addURL", URL.class);
	    method.setAccessible(true);
	    method.invoke(urlClassLoader, u);
	}
}
