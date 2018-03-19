package org.timeml.tarsqi.tools.stanford;

import java.io.StringReader;
import java.util.List;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.parser.shiftreduce.ShiftReduceParser;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.trees.Tree;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.timeml.tarsqi.Tarsqi.THYME_SOURCE;
import org.timeml.tarsqi.core.TarsqiDocument;
import org.timeml.tarsqi.io.TarsqiReader;

// Requires extra download of shift-reduce parser models (not part of CoreNLP 
// because they are so big) from https://nlp.stanford.edu/software/srparser.html

// Code below adapted from
// https://github.com/stanfordnlp/CoreNLP/blob/master/src/edu/stanford/nlp/parser/shiftreduce/demo/ShiftReduceDemo.java

/**
 * Demonstrates how to first use the tagger and the ShiftReduceParser. Note that
 * the ShiftReduceParser will not work on untagged text.
 *
 * @author John Bauer
 */
public class ShiftReduceDemo  {

	public static void main(String[] args) {
		String modelPath = "edu/stanford/nlp/models/srparser/englishSR.ser.gz";
		String taggerPath = "edu/stanford/nlp/models/pos-tagger/english-left3words/english-left3words-distsim.tagger";

		for (int argIndex = 0; argIndex < args.length; ) {
			switch (args[argIndex]) {
			case "-tagger":
				taggerPath = args[argIndex + 1];
				argIndex += 2;
				break;
			case "-model":
				modelPath = args[argIndex + 1];
				argIndex += 2;
				break;
			default:
				throw new RuntimeException("Unknown argument " + args[argIndex]);
			}
		}

		MaxentTagger tagger = new MaxentTagger(taggerPath);
		ShiftReduceParser model = ShiftReduceParser.loadModel(modelPath);
		DocumentPreprocessor tokenizer;

		// run it on a short string
		String text = "My dog likes to shake his stuffed chickadee toy.";
		tokenizer = new DocumentPreprocessor(new StringReader(text));
		for (List<HasWord> sentence : tokenizer) {
			List<TaggedWord> tagged = tagger.tagSentence(sentence);
			Tree tree = model.apply(tagged);
			System.out.println();
			tree.pennPrint();
			System.out.println();
		}

		// run it on a file that takes 100+ seconds with the default parser
		String filename = THYME_SOURCE + "train/ID001_clinic_003";
		TarsqiDocument tarsqiDoc = null;
		try {
			tarsqiDoc = new TarsqiReader().readTextFile(filename);
			long t0 = System.currentTimeMillis();
			tokenizer = new DocumentPreprocessor(new StringReader(tarsqiDoc.text));
			for (List<HasWord> sentence : tokenizer) {
				long tStart = System.currentTimeMillis();
				List<TaggedWord> tagged = tagger.tagSentence(sentence);
				Tree tree = model.apply(tagged);
				long tEnd = System.currentTimeMillis();
				float elapsed = (float) (tEnd - tStart);
				elapsed = elapsed / 1000;
				System.out.print(String.format("%3d\t%.3f\n", sentence.size(), elapsed));
			}
			long t1 = System.currentTimeMillis();
			float elapsed = (float) (t1 - t0);
			elapsed = elapsed / 1000;
			System.out.print(String.format("\n\nTOTAL ELAPSED TIME: %f seconds\n\n", elapsed));
		} catch (FileNotFoundException ex) {
			Logger.getLogger(ShiftReduceDemo.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
