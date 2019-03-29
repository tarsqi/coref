package org.timeml.tarsqi.utils;

import java.util.ArrayList;
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
	public static String[] getFileNames(String dirname) {
		java.io.File dir = new java.io.File(dirname);
		String[] filenames = dir.list();
		Arrays.sort(filenames);
		return filenames;
	}

	/**
	 * Return a sorted list of File instances in a directory.
	 *
	 * @param dirname The name of the directory
	 * @return An array of filenames, not including the path.
	 */
    
    // TODO: this has not been used yet, it should be tested
	public static ArrayList getFiles(String dirname) {
		String[] filenames = getFileNames(dirname);
		ArrayList files = new ArrayList<>();
		for (String filename : filenames) {
			String path = dirname + "/" + filename;
			files.add(new java.io.File(path)); }
		return files;
	}
}
