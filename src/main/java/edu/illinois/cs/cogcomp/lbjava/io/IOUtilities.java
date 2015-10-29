package edu.illinois.cs.cogcomp.lbjava.io;

import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class IOUtilities {

	@SuppressWarnings("rawtypes")
	public static boolean existsInClasspath(Class clazz, String fileName) {
		URL dirURL = clazz.getResource("/" + fileName);
		return dirURL != null;
	}

	@SuppressWarnings("rawtypes")
	public static URL loadFromClasspath(Class clazz, String fileName) {
		URL dirURL = clazz.getResource("/" + fileName);
		if (dirURL == null) return null;

		URL url = null;
		try {
			String dirPath = dirURL.getPath();

			if (dirURL.getProtocol().equals("jar")) {
				int exclamation = dirPath.indexOf("!");
				String jarPath = dirPath.substring(5, exclamation);
				String jarRoot = dirPath.substring(0, exclamation + 1);

				JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
				Enumeration<JarEntry> entries = jar.entries();

				while (entries.hasMoreElements()) {
					JarEntry element = entries.nextElement();
					String name = element.getName();
					if (name.equals(fileName)) {
						url = new URL("jar:" + jarRoot + "/" + name);
					}
				}
				jar.close();
			}
		}
		catch (Exception e) {
			System.err.println("ERROR: Can't read file : " + fileName + "\n" + e);
			e.printStackTrace();
			System.exit(1);
		}
		return url;
	}
}
