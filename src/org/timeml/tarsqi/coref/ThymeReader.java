package org.timeml.tarsqi.coref;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ThymeReader {
    
    public static List<ThymeFile> load(String directory) {
        List<ThymeFile> data = new ArrayList<>();
        System.out.println("Loading " + directory);
        File folder = new File(directory);
        File[] fileList = folder.listFiles(new ThymeFilter());
		Arrays.sort(fileList);
        for (int i = 0; i < fileList.length && i < 3; i++) {
            File file = fileList[i];
            System.out.println(file);
            String contents = readContents(file);
            data.add(new ThymeFile(file, contents));
        }
        return data;
    }

        
	/**
	 * Read the contents of a Thyme file.
	 * 
	 * @param file The file to be read
	 * @return The contents of the file as a string
	 */
    public static String readContents(File file) {
        String contents = null;
		String fileName = file.getPath();
        try { 
            contents = readFile(fileName, Charset.forName("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            System.out.println(e);
        } catch (FileNotFoundException e) {
            System.out.println(e);
        } catch (IOException e) {
            System.out.println(e);
        }
        return contents;
    }
    
    public static String readFile(String path, Charset encoding) throws 
            UnsupportedEncodingException, FileNotFoundException, IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    public static String readFileUTF8(String filePath) throws 
            UnsupportedEncodingException, FileNotFoundException, IOException {
 
        StringBuilder sb = new StringBuilder();
        try (Reader reader = new InputStreamReader(new FileInputStream(filePath), "UTF-8")) {
            try (BufferedReader fin = new BufferedReader(reader)) {
                String line;
                while ((line = fin.readLine()) != null) {
                    sb.append(line).append(System.lineSeparator());
                }
            }
        }
        return sb.toString();
    }
}


/** 
 * We just want to get the files with clinical notes.
 */
class ThymeFilter implements FileFilter {

    public boolean accept(File pathname) {
        String filename = pathname.getName();
		return filename.endsWith("_CLIN");
    }
}