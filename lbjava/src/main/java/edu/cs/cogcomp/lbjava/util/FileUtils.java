/**
 * This software is released under the University of Illinois/Research and Academic Use License. See
 * the LICENSE file in the root folder for details. Copyright (c) 2016
 *
 * Developed by: The Cognitive Computations Group, University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.cs.cogcomp.lbjava.util;

import java.io.File;

/**
 * Utility methods for handling file paths.
 *
 **/
public class FileUtils {

    /**
     * Update FilePath separator for file paths read from pom.xml config.
     * 
     * @param originalFilePath Path to a file in UNIX separator convention.s
     * @return Platform Independent File Path
     */
    public static String getPlatformIndependentFilePath(String originalFilePath) {
        if (File.separatorChar == '/') {
            return originalFilePath;
        }

        return originalFilePath.replace('/', File.separatorChar);
    }


    /**
     * Escapes the forward slash in Windows. Currently used in the generate code where a model's
     * location string is generated.
     *
     * CAVEAT: This function might break other escaped characters in the originalFilePath string.
     * Use with care.
     * 
     * @param originalFilePath Original File Path
     * @return File Path with the forward slash escaped on Windows.
     */
    public static String escapeFilePath(String originalFilePath) {
        if (File.separatorChar == '/') {
            return originalFilePath;
        }

        // Only update this on Windows.
        return originalFilePath.replace("\\", "\\\\");
    }
}
