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
package edu.illinois.cs.cogcomp;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.io.File;
import java.util.List;

/**
 * Translates LBJava code to Java
 * 
 */
@Mojo(name = "generate", requiresDependencyResolution = ResolutionScope.COMPILE)
public class GenerateMojo extends AbstractMojo {

	/**
	 * This will be ${project.build.outputDirectory} if not specified.
	 */
	@Parameter(defaultValue = "${project.build.outputDirectory}")
	private String dFlag;

	/**
	 * This is maven default (src/main/java) if not specified.
	 */
	@Parameter(defaultValue = "${project.basedir}/src/main/java")
	private String gspFlag;

	/**
	 * This is maven default (src/main/java) if not specified.
	 */
	@Parameter(defaultValue = "${project.basedir}/src/main/java")
	private String sourcepathFlag;

	/**
	 * The only required parameter.
	 */
	@Parameter(required = true)
	private String[] lbjavaInputFileList;

	@Parameter(property = "project.compileClasspathElements", required = true, readonly = true)
	private List<String> classpath;

	@Parameter(property = "project.build.outputDirectory", required = true, readonly = true)
	private String outputdir;

	public void execute() throws MojoExecutionException {

		classpath.add(dFlag);
		classpath.add(gspFlag);
		String newpath = StringUtils.join(classpath, ":");

		// If these directories don't exist, make them.
		new File(dFlag).mkdirs();
		new File(gspFlag).mkdirs();

		for (String lbjInputFile : lbjavaInputFileList) {
			getLog().info("Calling Java edu.illinois.cs.cogcomp.lbjava.Main...");
			try {
				String[] args = new String[] { "java", "-cp", newpath, "edu.illinois.cs.cogcomp.lbjava.Main",
                        "-c", "-d", dFlag, "-gsp", gspFlag, "-sourcepath", sourcepathFlag, lbjInputFile };

				ProcessBuilder pr = new ProcessBuilder(args);
				pr.inheritIO();
				Process p = pr.start();
				p.waitFor();

			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Yeah, an error.");
			}
		}

	}
}
