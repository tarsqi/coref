package org.timeml.tarsqi.tools.stanford;

import edu.stanford.nlp.coref.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.parser.shiftreduce.ShiftReduceParser;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.BasicDependenciesAnnotation;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.timeml.tarsqi.core.TarsqiDocument;
import org.timeml.tarsqi.io.TarsqiReader;


public class StanfordNLP {

	public StanfordCoreNLP pipeline;
	
	MaxentTagger tagger;
	ShiftReduceParser model;
	
	// these will be set to true by the annotators() method, which is given the
	// list of components that should be applied
	boolean ner = false;
	boolean parse = false;
	boolean depparse = false;
	boolean coref = false;
	boolean shiftreduce = false;
	
	String models = "edu/stanford/nlp/models/";
	// Requires an extra download of stanford-srparser-2014-10-23-models.jar
	String modelPath = models + "srparser/englishSR.ser.gz";
	String taggerPath = models + "pos-tagger/english-left3words/english-left3words-distsim.tagger";
	
	public StanfordNLP(String components) {
		// creates a StanfordCoreNLP object, with tokenizer, splitter, tagger and 
		// lemmatizer, plus some others as defined by the components argument
		//if (this.pipeline == null) {
		//System.out.println(">>> Initializing pipeline...");
		Properties props = new Properties();
		props.setProperty("annotators", annotators(components));
		this.pipeline = new StanfordCoreNLP(props);
		loadModels();
	}

	/**
	 * Load the models used by the shift-reduce parser.
	 */
	private void loadModels() {
		if (this.shiftreduce) {
			this.tagger = new MaxentTagger(taggerPath);
			this.model = ShiftReduceParser.loadModel(modelPath); }		
	}
	
	/**
	 * Run Stanford CoreNLP on a string.
	 * 
	 * @param input The input String 
	 * @return A StanfordResult with all annotations from the pipeline and from 
	 * the shift-reduce parser
	 */
	public StanfordResult processString(String input) {
		// Create an empty Annotation and run all Annotators on the input
		Annotation annotation = new Annotation(input);
		Tree tree = null;
		this.pipeline.annotate(annotation);
		// In addition, run the ShiftReduce parser if needed, the ShiftReduce 
		// cannot be run via the pipeline so we do it this way
		// TODO: figure out if we can use the sentences, token and tags inside 
		// the Annotation object so we do not tokenize and tag twice
		if (this.shiftreduce) {
			DocumentPreprocessor tokenizer;
			tokenizer = new DocumentPreprocessor(new StringReader(input));
			for (List<HasWord> sentence : tokenizer) {
				List<TaggedWord> tagged = this.tagger.tagSentence(sentence);
				// TODO: this is wrong since it overwrites results from the
				// previous sentence, must append to a list
				tree = this.model.apply(tagged); }
		}
		return new StanfordResult(annotation, tree);
	}

	/**
	 * Run Stanford CoreNLP on a file.
	 * 
	 * @param input The input file
	 * @return A StanfordResult or null if the file was not found.
	 */
	public StanfordResult processFile(File input) {
		try {
			String content = new Scanner(input).useDelimiter("\\A").next();
			return processString(content);
		} catch (FileNotFoundException ex) {
			Logger.getLogger(StanfordNLP.class.getName()).log(Level.SEVERE, null, ex);
			return null;
		}
	}

	public void processTarsqiFile(String tarsqiFile, String stanfordFile) {
		TarsqiDocument tarsqiDoc = new TarsqiReader().readTarsqiFile(tarsqiFile);
		StanfordResult result = this.processString(tarsqiDoc.text);
		result.getResultsAsTagList();
		//this.show(anno);
		// TODO: instead add the annotation to the TarsqiDocument
		this.export(tarsqiFile, result, stanfordFile);
	}

	// TODO: this does not make a lot of sense now since it does not save anything
	public void processFolder(String input) {
		File folder = new File(input);
		File[] files = folder.listFiles();
		Arrays.sort(files);
		for (File file : files) {
			System.out.println(file);
			this.processFile(file);
		} 
	}
	
	private String annotators(String components) {
		Set<String> comps = new HashSet(Arrays.asList(components.split("\\s+")));
		//List<String> x = new ArrayList<>();
		// always use basic processing
		StringBuilder sb = new StringBuilder("tokenize, ssplit, pos, lemma");
		if (comps.contains("ner")) {
			this.ner = true;
			sb.append(", ner"); }
		if (comps.contains("parse")) {
			this.parse = true;
			sb.append(", parse"); }
		if (comps.contains("depparse")) {
			this.depparse = true;
			sb.append(", depparse"); }
		if (comps.contains("coref")) {
			this.coref = true;
			sb.append(", coref"); }	
		if (comps.contains("shiftreduce")) {
			this.shiftreduce = true; }
		return sb.toString();
	}

	public void show(Annotation document) {
		
		// CoreMap: a Map that uses class objects as keys and has values with custom types
		// CoreLabel: a CoreMap with additional token-specific methods
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);

		for (CoreMap sentence : sentences) {
			System.out.println(sentence);

			// tokens
			for (CoreLabel token : sentence.get(TokensAnnotation.class))
				System.out.println(new StanfordToken(token));

			// parse tree
			if (parse)
				System.out.println(sentence.get(TreeAnnotation.class));
			
			// dependency graph
			// https://nlp.stanford.edu/nlp/javadoc/javanlp/edu/stanford/nlp/semgraph/SemanticGraph.html
			if (depparse) {
				SemanticGraph dependencies = sentence.get(BasicDependenciesAnnotation.class);
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
		if (coref) {
			Map<Integer, CorefChain> graph = document.get(CorefChainAnnotation.class);
		}
	}

	void showSentences(Annotation anno) {
		List<CoreMap> sentences = anno.get(SentencesAnnotation.class);
		for (CoreMap sentence : sentences)
			System.out.println(sentence.toString().replaceAll("\\s+"," "));
	}

	void showSentenceLengths(Annotation anno) {
		List<CoreMap> sentences = anno.get(SentencesAnnotation.class);
		String[] tokens;
		for (CoreMap sentence : sentences) {
			tokens = sentence.toString().replaceAll("\\s+", " ").split(" ");
			System.out.print(tokens.length + " "); }
		System.out.println();
	}

	void showParses(Annotation anno, String indent) {
		List<CoreMap> sentences = anno.get(SentencesAnnotation.class);
		for (CoreMap sentence : sentences) 
			System.out.println(indent + sentence.get(TreeAnnotation.class));
	}
	
	public void export(String docname, StanfordResult result, String filename) {
	
		try {
			Annotation document = result.annotation;
			StanfordWriter writer = new StanfordWriter();
			writer.setFileName(docname);
			for (CoreMap sentence : document.get(SentencesAnnotation.class)) {
				writer.addSentence();
				for (CoreLabel token : sentence.get(TokensAnnotation.class))
					writer.addToken(new StanfordToken(token));
				if (parse) {
					Tree tree = sentence.get(TreeAnnotation.class);
					List<Tree> allLeaves = tree.getLeaves();
					writer.addParse(tree.toString());
					for (Tree leaf : allLeaves) {
						writer.addPathsToRoot(tree, leaf);
					}
					// This gives a nullpointer error on the dependencies() method
					// Set<Dependency<Label, Label, Object>> deps = tree.dependencies();
					// for (Dependency<Label, Label, Object> dep : deps) {
					//	System.out.println(dep);
					//	System.out.println(new StanfordDependency(dep)); }
				}
					
				if (depparse) {
					SemanticGraph dependencies = sentence.get(BasicDependenciesAnnotation.class);
					IndexedWord root = dependencies.getFirstRoot();
					writer.addRoot(new StanfordToken(root));
					for (SemanticGraphEdge edge : dependencies.edgeListSorted())
						writer.addDependency(new StanfordDependency(edge));
					Set<IndexedWord> leaves = dependencies.getLeafVertices();
					for (IndexedWord leaf : leaves) {
						writer.addPath(
							leaf, 
							dependencies.getPathToRoot(leaf),
							dependencies.getShortestUndirectedPathEdges(leaf, root)); }
				}
			}
			writer.write(filename);
		} catch (ParserConfigurationException | TransformerException ex) {
			Logger.getLogger(StanfordNLP.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

}
