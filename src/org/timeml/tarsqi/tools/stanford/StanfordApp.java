package org.timeml.tarsqi.tools.stanford;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.parser.shiftreduce.ShiftReduceParser;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.trees.Tree;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.timeml.tarsqi.core.TarsqiDocument;
import org.timeml.tarsqi.io.TarsqiReader;
import static org.timeml.tarsqi.Tarsqi.THYME_CORPUS;
import static org.timeml.tarsqi.Tarsqi.THYME_SOURCE;
import org.timeml.tarsqi.components.Sectioner;

// StanfordNLP API and JavaDoc:
//    https://stanfordnlp.github.io/CoreNLP/api.html
//    https://nlp.stanford.edu/nlp/javadoc/javanlp/


public class StanfordApp {

	public static void main(String[] args) {
		
		runOnTarsqiExample1();    // run on the tarsqi test file
		//runOnTarsqiExample2();	// run on a thyme file processed by tarsqi
		//runParserOnStrings();		// run parser on a couple of strings
		//showSentences();			// print all sentences from a thyme file
		//parseSentences();			// parse sentences from that file
		//parseSentencesUsingSectioner();
		//parseSentencesUsingShiftReduce();
		//runPipelinePlusShiftReduce();
	}

	private static void runOnTarsqiExample1() {
		runOnTarsqiFile(
				"parse", //depparse shiftreduce",
				"src/resources/test.ttk", 
				"src/resources/test-out.xml");
	}

	private static void runOnTarsqiExample2() {
		runOnTarsqiFile(
				"depparse", 
				THYME_CORPUS + "train/ttk-output/ID001_clinic_003",
				THYME_CORPUS + "train/stanford-output/ID001_clinic_003");
	}

	private static void runOnTarsqiFile(String components, String tarsqiFile, String stanfordFile) {
		new StanfordNLP(components).processTarsqiFile(tarsqiFile, stanfordFile);
	}
	
	private static void showSentences() {
		String tarsqiFile = THYME_CORPUS + "train/ttk-output/ID001_clinic_003";
		TarsqiDocument tarsqiDoc = new TarsqiReader().readTarsqiFile(tarsqiFile);
		StanfordNLP snlp = new StanfordNLP("");
		StanfordResult result = snlp.processString(tarsqiDoc.text);
		snlp.showSentences(result.annotation);
	}

	private static void runParserOnStrings() {
		
		String[] sentences = {
			"John sleeps.",
			"Sue sees herself in the mirror." };
		StanfordNLP snlp = new StanfordNLP("parse");
 		for (String sentence : sentences) {
			parseSentence(snlp, sentence);
		}
	}

	private static void parseSentences() {
		// This gives very slow processing on some large and ill-behaved sentences.
		// For example, there is one sentence with 67 tokens (which happens to be 
		// a list of measurements like height and weight) which takes 85 seconds,
		// in addition there are 5 other sentences that take more than a second.
		// Note that the sentences are sentences taken from train/ID001_clinic_003.
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

	private static void parseSentencesUsingSectioner() {
		// This is basically on the same file as parseSentences() but instead of
		// taking the sentences as created by the stanford splitter it takes the
		// leaves as created by the sectioner. The effect on processing time is
		// dramatic in that instead of about 100-120 seconds the file is now
		// parsed in 5-6 seconds.
		String filename = THYME_SOURCE + "train/ID001_clinic_003";
		StanfordNLP snlp = new StanfordNLP("parse");
		try {
			TarsqiDocument tarsqiDoc = new TarsqiReader().readTextFile(filename);
			Sectioner sectioner = new Sectioner(tarsqiDoc);
			sectioner.parse();
			List<String> lines = sectioner.getSections();
			long t0 = System.currentTimeMillis();
			for (String line : lines)
				parseSentence(snlp, line);
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
		//if (length > 10) return;
		long t0 = System.currentTimeMillis();
		StanfordResult result = snlp.processString(sentence);
		long t1 = System.currentTimeMillis();
		System.out.print(String.format("%.3f  %3d    ", (float) (t1 - t0) / 1000, length)); 
		snlp.showSentenceLengths(result.annotation);
		//System.out.println();
		//snlp.showParses(anno, "   ");
	}

	private static void parseSentencesUsingShiftReduce() {
		// This is basically on the same file as parseSentences() but instead of
		// taking the sentences as created by the stanford splitter it takes the
		// leaves as created by the sectioner. The effect on processing time is
		// dramatic in that instead of about 100-120 seconds the file is now
		// parsed in 5-6 seconds.
		String filename = THYME_SOURCE + "train/ID001_clinic_003";
		StanfordNLP snlp = new StanfordNLP("parse");

		try {
			TarsqiDocument tarsqiDoc = new TarsqiReader().readTextFile(filename);

			String models = "edu/stanford/nlp/models/";
			String modelPath = models + "srparser/englishSR.ser.gz";
			String taggerPath = models + "pos-tagger/english-left3words/english-left3words-distsim.tagger";

		    MaxentTagger tagger = new MaxentTagger(taggerPath);
		    ShiftReduceParser model = ShiftReduceParser.loadModel(modelPath);

			Sectioner sectioner = new Sectioner(tarsqiDoc);
			sectioner.parse();
			List<String> lines = sectioner.getSections();
			
			long t0 = System.currentTimeMillis();
			for (String line : lines)
				parseSentenceUsingShiftReduce(snlp, tagger, model, line);
			long t1 = System.currentTimeMillis();
			float elapsed = (float) (t1 - t0);
			elapsed = elapsed / 1000;
			System.out.print(String.format("\n\nTOTAL ELAPSED TIME: %f seconds\n\n", elapsed));
		} catch (IOException ex) {
			Logger.getLogger(StanfordApp.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	private static void parseSentenceUsingShiftReduce(
			StanfordNLP snlp, MaxentTagger tagger, ShiftReduceParser model, String text) {

		// this is actually using shift-reduce plus the sectioner
		int length = text.split(" ").length;
		//if (length > 10) return;
		long t0 = System.currentTimeMillis();

		DocumentPreprocessor tokenizer = new DocumentPreprocessor(new StringReader(text));
	    for (List<HasWord> sentence : tokenizer) {
			List<TaggedWord> tagged = tagger.tagSentence(sentence);
			Tree tree = model.apply(tagged);
			//tree.pennPrint();
		}
		
		long t1 = System.currentTimeMillis();
		System.out.print(String.format("%.3f  %3d    ", (float) (t1 - t0) / 1000, length)); 
		//snlp.showSentenceLengths(anno);
		System.out.println();
		//snlp.showParses(anno, "   ");
	}


	private static void runPipelinePlusShiftReduce() {
		String filename = THYME_SOURCE + "train/ID001_clinic_003";
	}

}
