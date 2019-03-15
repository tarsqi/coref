package org.timeml.tarsqi.utils;

import java.util.Arrays;


/**
 * File utilities.
 */

public class File {
	
	/**
	 * Return a sorted list of file names in a directory.
	 *
	 * @param dirname The name of the directory
	 * @return An array of filenames, not including the path.
	 */
	public static String[] getFiles(String dirname) {
		java.io.File dir = new java.io.File(dirname);
		String[] files = dir.list();
		Arrays.sort(files);
		return files;
	}
	
}
