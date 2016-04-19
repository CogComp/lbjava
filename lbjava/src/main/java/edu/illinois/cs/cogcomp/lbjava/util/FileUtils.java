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
package edu.illinois.cs.cogcomp.lbjava.util;

import java.io.File;

/**
 * Utility methods for handling file paths.
 *
 **/
public class FileUtils {
    public static String getPlatformIndependentFilePath(String configFilePath) {
        if (File.separatorChar == '/') {
            return configFilePath;
        }

        return configFilePath.replace('/', File.separatorChar);
    }
}
