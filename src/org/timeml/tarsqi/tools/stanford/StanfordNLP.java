package org.timeml.tarsqi.tools.stanford;

import edu.stanford.nlp.coref.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.EnhancedPlusPlusDependenciesAnnotation;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;


public class StanfordNLP {

	//public String inputfile;
	//public Annotation document;
	public StanfordCoreNLP pipeline;
	
	boolean useNER = false;
	boolean useParser = false;
	boolean useDependencyParser = true;
	boolean useCoreference = false;
	
	public StanfordNLP() {
		// creates a StanfordCoreNLP object, with tokenizer, splitter, tagger and 
		// lemmatizer, plus some others as defined by the boolean properties 
		// useNER, useParser etcetera.
		//if (this.pipeline == null) {
		//System.out.println(">>> Initializing pipeline...");
		Properties props = new Properties();
		props.setProperty("annotators", annotators());
		this.pipeline = new StanfordCoreNLP(props); //}
	}
	
	/**
	 * Run Stanford CoreNLP on a string.
	 * 
	 * @param input The input String 
	 * @return 
	 */
	public Annotation processString(String input) {
		// create an empty Annotation and run all Annotators on the input
		Annotation document = new Annotation(input);
		this.pipeline.annotate(document);
		return document;
	}

	/**
	 * Run Stanford CoreNLP on a file.
	 * 
	 * @param input The input file
	 * @return 
	 */
	public Annotation processFile(File input) {
		try {
			String content = new Scanner(input).useDelimiter("\\A").next();
			return processString(content);
		} catch (FileNotFoundException ex) {
			Logger.getLogger(StanfordNLP.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}

	public void processFolder(String input) {
		File folder = new File(input);
		File[] files = folder.listFiles();
		Arrays.sort(files);
		for (File file : files) {
			System.out.println(file);
			Annotation annotation = this.processFile(file);
		} 
	}
	
	private String annotators() {
		List<String> x = new ArrayList<>();
		StringBuilder sb = new StringBuilder("tokenize, ssplit, pos, lemma");
		if (useNER) sb.append(", ner");
		if (useParser) sb.append(", parse");
		if (useDependencyParser) sb.append(", depparse");
		if (useCoreference) sb.append(", coref");		
		return sb.toString();
	}

	public void show(Annotation document) {
		
		// all the sentences in the input
		// CoreMap: a Map that uses class objects as keys and has values with custom types
		// CoreLabel: a CoreMap with additional token-specific methods
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);

		for (CoreMap sentence : sentences) {
			System.out.println(sentence);

			// tokens
			for (CoreLabel token : sentence.get(TokensAnnotation.class))
				System.out.println(new StanfordToken(token));

			// parse tree
			if (useParser)
				System.out.println(sentence.get(TreeAnnotation.class));
			
			// dependency graph
			// https://nlp.stanford.edu/nlp/javadoc/javanlp/edu/stanford/nlp/semgraph/SemanticGraph.html
			if (useDependencyParser) {
				SemanticGraph dependencies = sentence.get(EnhancedPlusPlusDependenciesAnnotation.class);
				StanfordToken root = new StanfordToken(dependencies.getFirstRoot());
				System.out.println(dependencies);
				System.out.println("ROOT = " + root + "\n");
				for (SemanticGraphEdge edge : dependencies.edgeListSorted())
					System.out.println(new StanfordDependency(edge));
			}
			
			// well, let's just show the first one
			break;
		}

		// coreference link graph
		// Each chain stores a set of mentions that link to each other,
		// along with a method for getting the most representative mention
		// Both sentence and token offsets start at 1!
		if (useCoreference) {
			Map<Integer, CorefChain> graph = document.get(CorefChainAnnotation.class);
		}
	}
	
	public void export(String docname, Annotation document, String filename) {

		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		StringBuilder sb = new StringBuilder();
		sb.append(docname + "\n\n");
		for (CoreMap sentence : sentences) {
			collectDependencies(sentence, sb); }

		try (Writer writer = new BufferedWriter(new OutputStreamWriter(
              new FileOutputStream(filename), "utf-8"))) {
			writer.write(sb.toString());
		} catch (UnsupportedEncodingException ex) {
			Logger.getLogger(StanfordNLP.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(StanfordNLP.class.getName()).log(Level.SEVERE, null, ex);
		}
		
	}

	private void collectDependencies(CoreMap sentence, StringBuilder sb) {

		SemanticGraph dependencies = sentence.get(EnhancedPlusPlusDependenciesAnnotation.class);
		if (dependencies == null) return;
	
		StanfordToken root = new StanfordToken(dependencies.getFirstRoot());
			
		sb.append(String.format("\nROOT\t%s\t%s\t%s\n\n", root.start, root.end, root.lemma));
		for (SemanticGraphEdge edge : dependencies.edgeListSorted()) {
			StanfordDependency dep = new StanfordDependency(edge);
			sb.append(String.format(
					"DEPENDENCY\t%s:%s\t%s:%s\t%s\n", 
					dep.governor.start, dep.governor.end,
					dep.dependent.start, dep.dependent.end, dep.relation));
		}
			
		sb.append("\n");
		Set<IndexedWord> leaves = dependencies.getLeafVertices();
		for (IndexedWord leaf : leaves) {
			List<IndexedWord> catpath = dependencies.getPathToRoot(leaf);
			List<SemanticGraphEdge> relpath = dependencies.getShortestUndirectedPathEdges(leaf, dependencies.getFirstRoot());
			sb.append(String.format("CATPATH\t%s:%s\t%s", leaf.beginPosition(), leaf.endPosition(), leaf.tag()));
			for (IndexedWord w : catpath)
				sb.append(String.format("\t%s", w.tag()));
			sb.append("\n");
			sb.append(String.format("RELPATH\t%s:%s\t%s", leaf.beginPosition(), leaf.endPosition(), leaf.tag()));
			for (SemanticGraphEdge rel : relpath)
				sb.append(String.format("\t%s", rel.getRelation()));
			sb.append("\n");
		}
	}

}
