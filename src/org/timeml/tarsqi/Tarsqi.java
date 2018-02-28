package org.timeml.tarsqi;

import edu.stanford.nlp.pipeline.Annotation;
import java.io.File;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.timeml.tarsqi.components.Sectioner;
import org.timeml.tarsqi.core.TarsqiDocument;
import org.timeml.tarsqi.core.TarsqiReader;
import org.timeml.tarsqi.tools.stanford.StanfordDocument;
import org.timeml.tarsqi.tools.stanford.StanfordNLP;

public class Tarsqi {

	public static String THYME_CORPUS =  "/DATA/resources/corpora/thyme/THYME-corpus-processed/";

	public static void main(String[] args) {

		//runStanfordOnThymeFiles();
		//runStanfordOnThymeDirectory();
		//loadTarsqiAndStanfordDocuments();
		//runSectioner("src/resources/texst.ttk");
		runSectioner("src/resources/sectioner.ttk");
		
	}

	static void runStanfordOnThymeFiles() {
		// Run StanfordNLP on four example thyme file that were processed by TTK,
		// the lasst of these throws an XML error that needs to be solved
		String input_dir = THYME_CORPUS + "train/ttk-output/";
		String output_dir = THYME_CORPUS + "train/stanford-output/";
		String[] reports = { "ID001_clinic_001", "ID001_clinic_003" };
		// "ID001_clinic_001", "ID001_clinic_003",	"ID009_clinic_025", "doc0003_CLIN" };
		for (int i = 0 ; i < reports.length ; i++)
			runStanfordOnFile(input_dir + reports[i], output_dir + reports[i]);
		//runStanfordOnFile(input_dir + "ID009_clinic_025", output_dir + "ID009_clinic_025");
	}
	
	static void runStanfordOnFile(String infile, String outfile) {
		System.out.println(infile);
		TarsqiDocument doc = new TarsqiReader().read(infile);
		if (doc.isValid()) {
			StanfordNLP snlp = new StanfordNLP("depparse");
			Annotation document = snlp.processString(doc.text);
			snlp.export(doc.filename, document, outfile); }
	}
	
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
			TarsqiDocument doc = new TarsqiReader().read(infile);
			if (! doc.isValid()) continue;
			try {
				Annotation document = snlp.processString(doc.text);
				snlp.export(infile, document, outfile);
			} catch (Exception ex) {
				Logger.getLogger(StanfordNLP.class.getName()).log(Level.SEVERE, null, ex);				
			}
		}
	}
	
	public static void loadTarsqiAndStanfordDocuments() {
		String tarsqiDir = THYME_CORPUS + "train/ttk-output/";
		String stanfordDir = THYME_CORPUS + "train/stanford-output/";
		String[] reports = { "ID001_clinic_001", "ID001_clinic_003" };
		for (int i = 0 ; i < reports.length ; i++) {
			System.out.println(reports[i]);
			String tarsqiFile = tarsqiDir + reports[i];
			String stanfordFile = stanfordDir + reports[i];
			TarsqiDocument tarsqiDoc = new TarsqiReader().read(tarsqiFile);
			StanfordDocument stanfordDoc = new StanfordDocument(stanfordFile);
			System.out.println();
			tarsqiDoc.prettyPrint();
			System.out.println();
			stanfordDoc.prettyPrint();
			System.out.println();
		}
	}

	private static void runSectioner(String filename) {
		TarsqiDocument tarsqiDoc = new TarsqiReader().read(filename);
		Sectioner sectioner = new Sectioner(tarsqiDoc);
		sectioner.parse();
		//System.out.println(tarsqiDoc);
		//tarsqiDoc.prettyPrint();
	}
}
