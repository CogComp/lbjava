package edu.illinois.cs.cogcomp.lbjava;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import org.junit.Test;

public class NaiveBayesTest {

	@Test
	public void test() throws Exception {
		//Make sure the lbj dir is clean
		File lbjDir = new File(getClass().getResource("/lbj/.").getPath());
		File[] dirFiles = lbjDir.listFiles(new FilenameFilter() {
			
			public boolean accept(File dir, String name) {
				return !name.endsWith(".lbj");
			}
		}); 
		for (File file: dirFiles) file.delete();
		
		File lbjFile = new File(getClass().getResource("/lbj/naive-bayes.lbj").getFile());
		
		// The auto-generated code directory needs to be added to classpath
		addPath(getClass().getResource("/lbj/."));
		
		assertTrue(lbjFile.exists());
		
		String[] args = { "-d", lbjFile.getParent(), lbjFile.getPath() };
		Main.main(args);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static void addPath(URL u) throws Exception {
	    URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
	    Class urlClass = URLClassLoader.class;
	    Method method = urlClass.getDeclaredMethod("addURL", new Class[]{URL.class});
	    method.setAccessible(true);
	    method.invoke(urlClassLoader, new Object[]{u});
	}
}
