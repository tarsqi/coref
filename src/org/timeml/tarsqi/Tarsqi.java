package org.timeml.tarsqi;

import edu.stanford.nlp.pipeline.Annotation;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.timeml.tarsqi.components.DocElement;
import org.timeml.tarsqi.components.Sectioner;
import org.timeml.tarsqi.core.TarsqiDocument;
import org.timeml.tarsqi.io.TarsqiReader;
import org.timeml.tarsqi.tools.stanford.StanfordDocument;
import org.timeml.tarsqi.tools.stanford.StanfordNLP;

public class Tarsqi {

	// the processed thyme corpus with ttk and stanford output and the thyme text data
	public static String THYME_CORPUS = "/DATA/resources/corpora/thyme/THYME-corpus-processed/";
	public static String THYME_SOURCE = "/DATA/resources/corpora/thyme/THYME-corpus/TextData/";

	// some switches that determine what piece of the code runs
	public static boolean runStanfordOnThymeFiles = false;
	public static boolean runStanfordOnThymeDirectory = false;
	public static boolean loadTarsqiAndStanfordDocuments = false;
	public static boolean runSectionerOnTestFile = false;
	public static boolean runSectionerOnSectionerFile = false;
	public static boolean runSectionerOnThyme = false;
	public static boolean findAllCapsInThyme = false;
	public static boolean loadTarsqiDocument = true;
			
	public static void main(String[] args) {

		if (runStanfordOnThymeFiles) runStanfordOnThymeFiles();
		if (runStanfordOnThymeDirectory) runStanfordOnThymeDirectory();
		if (loadTarsqiAndStanfordDocuments) loadTarsqiAndStanfordDocuments();
		if (runSectionerOnTestFile) runSectioner("src/resources/test.ttk");
		if (runSectionerOnSectionerFile) runSectioner("src/resources/sectioner.ttk");
		if (runSectionerOnThyme) runSectionerOnThyme();
		if (findAllCapsInThyme) findAllCapsInThyme();
		if (loadTarsqiDocument) loadTarsqiDocument();
	}

	/**
	 * Run StanfordNLP on a couple of example thyme file that were processed
	 * by TTK.
	 */ 
	static void runStanfordOnThymeFiles() {
		String input_dir = THYME_CORPUS + "train/ttk-output/";
		String output_dir = THYME_CORPUS + "train/stanford-output/";
		// TODO: the last of these throws an XML error that needs to be resolved.
		String[] reports = { 
			"ID001_clinic_001", "ID001_clinic_003", "ID009_clinic_025",
			"doc0003_CLIN" };
		for (int i = 0 ; i < reports.length ; i++)
			runStanfordOnFile(input_dir + reports[i], output_dir + reports[i]);
	}
	
	/**
	 * Run the Stanford pipeline on a TTK file.
	 * 
	 * @param infile Name of the input file
	 * @param outfile Name of the output file
	 */
	static void runStanfordOnFile(String infile, String outfile) {
		System.out.println(infile);
		TarsqiDocument doc = new TarsqiReader().readTarsqiFile(infile);
		if (doc.isValid()) {
			StanfordNLP snlp = new StanfordNLP("depparse");
			Annotation document = snlp.processString(doc.text);
			snlp.export(doc.filename, document, outfile); }
	}

	/**
	 * Run a Stanford pipeline on a directory of Thyme files that were all
	 * processed by TTK.
	 */
	public static void runStanfordOnThymeDirectory() {
		String IN = THYME_CORPUS + "train/ttk-output/thyme";
		String OUT = THYME_CORPUS + "train/stanford-output/thyme";
		File folder = new File(IN);
		String[] files = folder.list();
		Arrays.sort(files);
		StanfordNLP snlp = new StanfordNLP("depparse");
		for (String file : files) {
			System.out.println(file);
			String infile = IN + "/" + file;
			String outfile = OUT + "/" + file;
			TarsqiDocument doc = new TarsqiReader().readTarsqiFile(infile);
			if (! doc.isValid()) continue;
			try {
				Annotation document = snlp.processString(doc.text);
				snlp.export(infile, document, outfile);
			} catch (Exception ex) {
				Logger.getLogger(StanfordNLP.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}
	
	/**
	 * Testing code to load TTK documents and Stanford output.
	 */
	public static void loadTarsqiAndStanfordDocuments() {
		String tarsqiDir = THYME_CORPUS + "train/ttk-output/";
		String stanfordDir = THYME_CORPUS + "train/stanford-output/";
		String[] reports = { "ID001_clinic_001", "ID001_clinic_003" };
		for (int i = 0 ; i < reports.length ; i++) {
			System.out.println(reports[i]);
			String tarsqiFile = tarsqiDir + reports[i];
			String stanfordFile = stanfordDir + reports[i];
			TarsqiDocument tarsqiDoc = new TarsqiReader().readTarsqiFile(tarsqiFile);
			StanfordDocument stanfordDoc = new StanfordDocument(stanfordFile);
			System.out.println();
			tarsqiDoc.prettyPrint();
			System.out.println();
			stanfordDoc.prettyPrint();
			System.out.println();
		}
	}

	/**
	 * Run the sectioner on a file.
	 * 
	 * @param filename Name of the input file
	 */
	private static void runSectioner(String filename) {
		TarsqiDocument tarsqiDoc = new TarsqiReader().readTarsqiFile(filename);
		Sectioner sectioner = new Sectioner(tarsqiDoc);
		sectioner.parse();
		sectioner.prettyPrint();
	}

	/**
	 * Run sectioner on all Thyme train files. Internally uses a count to break
	 * the loop after some number of iterations.
	 */
	private static void runSectionerOnThyme() {
 
		String thymeDir = THYME_SOURCE + "train/";
		String[] files = getFiles(thymeDir);
		String outDir = "build/out/";
		new File(outDir).mkdir();
		int count = 0;
		for (String file : files) {
			count++;
			if (count > 50) break;
			String infile = thymeDir + "/" + file;
			String outfile = outDir + file;
			try {
				System.out.println(infile);
				TarsqiDocument tarsqiDoc = new TarsqiReader().readTextFile(infile);
				Sectioner sectioner = new Sectioner(tarsqiDoc);
				sectioner.parse();
				sectioner.write(outfile);
			} catch (FileNotFoundException ex) {
				Logger.getLogger(Tarsqi.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

	/**
	 * Analysis method to find all instances of all sequences of all caps words 
	 * at the beginning of a line.
	 */
	private static void findAllCapsInThyme() {
		// Loop through all Thyme files in the train directory and collect all
		// instances where a line starts with a sequence of all capitalized
		// words.
		String thymeDir = THYME_SOURCE + "train/";
		String[] files = getFiles(thymeDir);
		Map<String, ArrayList<DocElement>> caps = new HashMap<>();
		int count = 0;
		for (String file : files) {
			count++;
			if (count > 50) break;
			String infile = thymeDir + "/" + file;
			try {
				TarsqiDocument tarsqiDoc = new TarsqiReader().readTextFile(infile);
				Sectioner sectioner = new Sectioner(tarsqiDoc);
				sectioner.splitLines();
				for (DocElement line : sectioner.getLines()) {
					if (line.hasAllCapsPrefix()) {
						String prefix = line.getPrefix();
						if (caps.get(prefix) == null) 
							caps.put(prefix, new ArrayList<>());
						caps.get(prefix).add(line);
					}
				}
			} catch (FileNotFoundException ex) {
				Logger.getLogger(Tarsqi.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		for (String key : caps.keySet()) {
			String str = String.format("%d %s", caps.get(key).size(), key.trim());
			System.out.println(str);
			for (DocElement line : caps.get(key))
				System.out.println("   " + line);
		}
	}

	/**
	 * Return a sorted list of file names in a directory.
	 * 
	 * @param dirname The name of the directory
	 * @return An array of filenames, not including the path.
	 */
	private static String[] getFiles(String dirname) {
		File dir = new File(dirname);
		String[] files = dir.list();
		Arrays.sort(files);
		return files;
	}

	/**
	 * Test method to load the example TTK document.
	 */
	private static void loadTarsqiDocument() {
		String tarsqiFile = "src/resources/test.ttk";
		TarsqiDocument tarsqiDoc = new TarsqiReader().readTarsqiFile(tarsqiFile);
		tarsqiDoc.prettyPrint();
	}

}
