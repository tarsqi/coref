package org.timeml.tarsqi;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.timeml.tarsqi.components.DocElement;
import org.timeml.tarsqi.components.Sectioner;
import org.timeml.tarsqi.core.AnnotationLayer;
import org.timeml.tarsqi.core.TarsqiDocument;
import org.timeml.tarsqi.core.annotations.TreeAnnotation;
import static org.timeml.tarsqi.definitions.Components.SECTIONER;
import org.timeml.tarsqi.io.TarsqiReader;
import org.timeml.tarsqi.tools.stanford.StanfordDocument;
import org.timeml.tarsqi.tools.stanford.StanfordNLP;
import org.timeml.tarsqi.tools.stanford.StanfordResult;


public class Tarsqi {

    // the processed thyme corpus with ttk and stanford output and the thyme text data
	public static String THYME_CORPUS = "/DATA/resources/corpora/thyme/THYME-corpus-processed/";
	public static String THYME_SOURCE = "/DATA/resources/corpora/thyme/THYME-corpus/TextData/";


	public static void main(String[] args) {

		// sectioner
        if (args.length == 2 && args[0].equals("--sectioner")) {
                String fname = args[1];
                runSectioner(fname); }

		// several batch-like top-level run commands
        else if (args.length >= 3 && args[0].equals("--run")) {
            if (args[1].equals("stanford") && args[2].equals("thyme"))
				runStanfordOnThymeDirectory();
			else if (args[1].equals("sectioner") && args[2].equals("thyme"))
				runSectionerOnThyme();
		}

        // testing and statistics
        else if (args.length >= 2 && args[0].equals("--test"))
            test(args);
        else if (args.length >= 3 && args[0].equals("--stats"))
            stats(args);

	}

    /**
     * Calling various tests given command line arguments. Assumes that the
     * first argument is always --test and that there is at least a second
     * argument
     * @param args Array of command line arguments
     */
    private static void test(String[] args) {
        if (args[1].equals("sectioner"))
            runSectioner("src/resources/sectioner.ttk");
		else if (args[1].equals("load-tarsqidoc"))
			loadTarsqiDocument();
		else if (args[1].equals("pipeline"))
			runTarsqiPipeline("src/resources/test.ttk");
		else if (args[1].equals("stanford") && args[2].equals("thyme"))
			runStanfordOnThymeFiles();
		else if (args[1].equals("load-tarsqi-stanford"))
			loadTarsqiAndStanfordDocuments();
    }

    /**
     * Calling various methods to print statistics. Assumes that the first
     * argument is --stats and that there are three or more arguments in total.
     * @param args Array of command line arguments
     */
    private static void stats(String[] args) {
        if (args[1].equals("thyme") && args[2].equals("caps"))
				findAllCapsInThyme();
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
		// TODO: keeps adding annotators and writing to stdout about it for each
		// file, can the writing be suppressed? (annotators are loaded only once)
		for (String report : reports)
			runStanfordOnFile(input_dir + report, output_dir + report);
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
			StanfordResult result = snlp.processString(doc.text);
			snlp.export(doc.filename, result, outfile); }
	}

	/**
	 * Run a Stanford pipeline on a directory of Thyme files that were all
	 * processed by TTK.
	 */
	public static void runStanfordOnThymeDirectory() {
		// Has errors on doc0003_CLIN, doc0003_PATH, doc0008_CLIN, doc0008_PATH,
		// doc0019_CLIN, doc0019_PATH, doc0067_CLIN, doc0067_PATH, doc0104_CLIN,
		// doc0104_PATH, doc0107_PATH, doc0121_CLIN, doc0121_PATH, doc0136_CLIN
		// doc0136_PATH, doc0138_CLIN, doc0138_PATH and more
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
				StanfordResult result = snlp.processString(doc.text);
				snlp.export(infile, result, outfile);
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
	public static void runSectioner(String filename) {
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

	private static void runTarsqiPipeline(String filename) {
		TarsqiDocument tarsqiDoc = new TarsqiReader().readTarsqiFile(filename);
		tarsqiDoc.runSectioner();
		tarsqiDoc.runTagger();
		tarsqiDoc.runChunker();
		tarsqiDoc.prettyPrint();
		AnnotationLayer layer = tarsqiDoc.getLayer(SECTIONER);
		TreeAnnotation top = (TreeAnnotation) layer.annotations.get(0);
		top.prettyPrint();
	}

}
