package org.timeml.tarsqi.tools.stanford;

import edu.stanford.nlp.pipeline.Annotation;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.timeml.tarsqi.core.TarsqiDocument;
import org.timeml.tarsqi.core.TarsqiReader;
import static org.timeml.tarsqi.Tarsqi.THYME_CORPUS;

public class StanfordApp {

	//public static String THYME_CORPUS =  "/DATA/resources/corpora/thyme/THYME-corpus-processed/";

	public static void main(String[] args) {
		
		//runOnTarsqiExample1();	// run on the tarsqi test file
		//runOnTarsqiExample2();	// run on a thyme file processed by tarsqi
		//runParserOnStrings();		// run parser on a couple of strings
		//getSentences();			// print all sentences frm a thyme file
		parseSentences();			// parse sentences from that file
	}

	private static void runOnTarsqiExample1() {
		String tarsqiFile = "src/resources/test.ttk";
		String stanfordFile = "src/resources/test.xml";
		runOnTarsqiFile("depparse", tarsqiFile, stanfordFile);
	}

	private static void runOnTarsqiExample2() {
		String tarsqiFile = THYME_CORPUS + "train/ttk-output/ID001_clinic_003";
		String stanfordFile = THYME_CORPUS + "train/stanford-output/ID001_clinic_003";
		runOnTarsqiFile("depparse", tarsqiFile, stanfordFile);
	}

	private static void runOnTarsqiFile(String components, String tarsqiFile, String stanfordFile) {
		TarsqiDocument tarsqiDoc = new TarsqiReader().read(tarsqiFile);
		StanfordNLP snlp = new StanfordNLP(components);
		Annotation anno = snlp.processString(tarsqiDoc.text);
		snlp.show(anno);
		snlp.export(tarsqiFile, anno, stanfordFile);
	}

	private static void getSentences() {
		String tarsqiFile = THYME_CORPUS + "train/ttk-output/ID001_clinic_003";
		TarsqiDocument tarsqiDoc = new TarsqiReader().read(tarsqiFile);
		StanfordNLP snlp = new StanfordNLP("");
		Annotation anno = snlp.processString(tarsqiDoc.text);
		snlp.showSentences(anno);
	}

	private static void runParserOnStrings() {
		
		String[] sentences = {
			"John sleeps.",
			"It is always a good idea to try this on a long string but maybe not too long",
			"Sue sees herself in the mirror." };
		StanfordNLP snlp = new StanfordNLP("parse");
 		for (String sentence : sentences) {
			parseSentence(snlp, sentence);
		}
	}

	private static void parseSentences() {
		String filename = "src/resources/sentences.txt";
		Charset utf8 = StandardCharsets.UTF_8;
		StanfordNLP snlp = new StanfordNLP("parse");
		try {
			long t0 = System.currentTimeMillis();
			List<String> sentences = Files.readAllLines(Paths.get(filename), utf8);
			for (String sentence : sentences)
				parseSentence(snlp, sentence);
			long t1 = System.currentTimeMillis();
			float elapsed = (float) (t1 - t0);
			elapsed = elapsed / 1000;
			System.out.print(String.format("\n\nTOTAL ELAPSED TIME: %f seconds\n\n", elapsed));
		} catch (IOException ex) {
			Logger.getLogger(StanfordApp.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	private static void parseSentence(StanfordNLP snlp, String sentence) {
		int length = sentence.split(" ").length;
		if (length > 10) return;
		long t0 = System.currentTimeMillis();
		Annotation anno = snlp.processString(sentence);
		long t1 = System.currentTimeMillis();
		System.out.print(String.format("%.3f  %3d ", (float) (t1 - t0) / 1000, length)); 
		snlp.showParses(anno);
	}


}
