package org.timeml.tarsqi.components;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.timeml.tarsqi.core.AnnotationLayer;
import static org.timeml.tarsqi.core.AnnotationLayer.STANFORD_DEPENDENCIES;
import org.timeml.tarsqi.core.TarsqiDocument;
import org.timeml.tarsqi.core.annotations.Annotation;
import org.timeml.tarsqi.core.annotations.Event;
import org.timeml.tarsqi.core.annotations.TreeAnnotation;
import org.timeml.tarsqi.io.TarsqiReader;
import org.timeml.tarsqi.io.ThymeFile;
import org.timeml.tarsqi.tools.stanford.StanfordDependency;
import org.timeml.tarsqi.tools.stanford.StanfordNLP;
import static org.timeml.tarsqi.tools.stanford.StanfordReader.readDependencyParse;
import org.timeml.tarsqi.tools.stanford.StanfordSentence;
import org.timeml.tarsqi.tools.stanford.StanfordToken;
import static org.timeml.tarsqi.utils.File.getFileNames;


public class Coref {

	// this is where ECB lives, for reference and to be deleted at some point
	static final String ECB = "/DATA/resources/corpora/EventCorefBank/ECB1.0.ttk";
	static final String ECB_TOPIC_1 = ECB + "/data/1";

	// some old locations for Thyme, not used but keep for now
	static final String THYME_CORPUS =  "/DATA/resources/corpora/thyme/THYME-corpus-processed/";
	static final String INPUT = THYME_CORPUS + "train/ttk-output/thyme/ID009_clinic_025";
	static final String THYME = "/DATA/resources/corpora/thyme/THYME-corpus";
	static List<ThymeFile> data;


	public static void main(String[] args) {

		if (args[0].equals("--run-stanford")) {
			String inputDir = args[1];
			String outputDir = args[2];
			runDependencyParser(inputDir, outputDir);
		}
		else if (args[0].equals("--train")) {
			String ecbDir = args[1];
			String depparseDir = args[2];
			String trainDir = args[3];
			train(ecbDir, depparseDir, trainDir);
		}
	}


	/**
	 * Run the Stanford dependency parser on TTK files in a directory tree.
	 *
	 * @param inputDir directory with files in TTK format
	 * @param outputDir directory to write the output to, will have the same
	 * internal structure as inputDir
	 */
	public static void runDependencyParser(String inputDir, String outputDir) {

		StanfordNLP parser = new StanfordNLP("depparse");
		System.out.println("SOURCE: " + inputDir + "\nTARGET: " + outputDir + "\n");
		runDependencyParserOnDirectory(parser, inputDir, outputDir, "ecb");
	}

	public static void runDependencyParserOnDirectory(
			StanfordNLP parser, String inputDir, String outputDir) {

		runDependencyParserOnDirectory(parser, inputDir, outputDir, null);
	}

	public static void runDependencyParserOnDirectory(
			StanfordNLP parser, String inputDir, String outputDir, String ext) {

		System.out.println("\n" + inputDir + "\n");
		new File(outputDir).mkdir();
		String[] fnames = getFileNames(inputDir);
		for (String fname : fnames) {
 			String inpath = inputDir + "/" + fname;
			String outpath = outputDir + "/" + fname;
			File file = new File(inpath);
			if (file.isDirectory())
				runDependencyParserOnDirectory(parser, inpath, outpath, ext);
			else if (file.isFile() && (ext == null || inpath.endsWith("." + ext)))
				runDependencyParserOnFile(parser, inpath, outpath);
		}
	}

	public static void runDependencyParserOnFile(
			StanfordNLP parser, String inFile, String outFile) {

		System.out.println("   " + inFile);
		parser.processTarsqiFile(inFile, outFile);
	}

	public static void thymeExperiment(String[] args) {
		/*
		System.out.println();
		int start, end;
		data = ThymeReader.load(THYME + "/TextData/dev");
		System.out.println(data);
		System.out.println(data.get(0).file);
		start = 410; end = 521;
		System.out.println(data.get(0).text.substring(start, end));
		//runStanford(data.get(0).text.substring(start, end));
		*/
	}

	public static void train(String ecbDir, String depparseDir, String trainDir) {

		System.out.println("\nTTK: " + ecbDir);
		System.out.println("DEP: " + depparseDir);
		System.out.println("OUT: " + trainDir + "\n");

		ArrayList<TarsqiDocument> docs = loadECB(ecbDir, depparseDir);
	}

	/**
	 * Loads ECB data for training. Assumes the ECB documents are in the TTK
	 * format and contain the following information: (1) MENTION tags from the
	 * original corpus (in the SOURCE_TAGS annotation layer of TarsqiDocuments),
	 * and (2) the results from running the TTK over the documents. Also loads
	 * the dependency parse that was created separately.
	 *
	 * @param ecbDir path to the directory with ECB data
	 * @param depparseDir path to the directory with dependency parses of the ECB
	 * data
	 */
	private static ArrayList<TarsqiDocument> loadECB(String ecbDir, String depparseDir) {

		ArrayList<TarsqiDocument> docs = new ArrayList();
		String relativePath = "/data/1/1.ecb";
		String ecbFile = ecbDir + relativePath;
		String depparseFile = depparseDir + relativePath;
		TarsqiDocument tarsqiDoc = createTarsqiDocument(ecbFile, depparseFile);
		docs.add(tarsqiDoc);
		return docs;
	}


	private static TarsqiDocument createTarsqiDocument(String path1, String path2) {


		System.out.println(path1);
		System.out.println(path2 + "\n");
		TarsqiDocument tarsqiDoc = new TarsqiReader().readTarsqiFile(path1);
		addChainsToEvents(tarsqiDoc);
		ArrayList<StanfordSentence> sentences = readDependencyParse(path2);

		// create layer for dependency parses and add it to the document
		AnnotationLayer deplayer = new AnnotationLayer(STANFORD_DEPENDENCIES, "tree");
		// TODO: TOP, and other annotation names, should be defined elsewhere
		TreeAnnotation top = new TreeAnnotation("TOP");
		tarsqiDoc.addLayer(deplayer);
		deplayer.addAnnotation(top);

		TreeAnnotation anno;

		for (StanfordSentence sentence : sentences) {
			TreeAnnotation s = new TreeAnnotation("SENTENCE");
			s.addAttribute("index", sentence.index);
			top.addChildNode(s);
			for (StanfordToken tok : sentence.tokens) {
				anno = createToken(tok);
				s.addChildNode(anno); }
			StanfordToken root = sentence.dependenciesRoot;
			anno = createDependencyRoot(root);
			s.addChildNode(anno);
			for (StanfordDependency dependency : sentence.dependencies) {
				anno = createDependency(dependency);
				s.addChildNode(anno); }
		}

		tarsqiDoc.prettyPrint();
		//deplayer.prettyPrint();
		return tarsqiDoc;
	}

	/**
	 * Creates an instance of TreeAnnotation with type TOKEN from a StanfordToken
	 * @param token
	 * @return the TreeAnnotation created from the token
	 */
	private static TreeAnnotation createToken(StanfordToken token) {
		TreeAnnotation anno = new TreeAnnotation("TOKEN", token.start, token.end);
		anno.addAttribute("index", token.index);
		anno.addAttribute("lemma", token.lemma);
		anno.addAttribute("word", token.word);
		return anno;
	}

	private static TreeAnnotation createDependencyRoot(StanfordToken token) {
		TreeAnnotation anno = new TreeAnnotation("DEPENDENCY_ROOT");
		anno.addAttribute("index", token.index);
		return anno;
	}

	private static TreeAnnotation createDependency(StanfordDependency dependency) {
		TreeAnnotation anno = new TreeAnnotation("DEPENDENCY");
		anno.addAttribute("relation", dependency.relation);
		anno.addAttribute("governor", dependency.governor.index);
		anno.addAttribute("dependent", dependency.dependent.index);
		return anno;
	}


	/**
	 * Add the information from MENTION tags in the source tags annotation layer
	 * to EVENT tags in the Tarsqi layer.
	 *
	 * @param tarsqiDoc
	 */
	private static void addChainsToEvents(TarsqiDocument tarsqiDoc) {
		AnnotationLayer sourceTags = tarsqiDoc.getSourceLayer();
		//System.out.println();
		//sourceTags.prettyPrint();
		//System.out.println();
		for (Annotation annotation : sourceTags.annotations) {
			if (annotation.type.equals("MENTION")) {
				Object chain = annotation.getAttribute("CHAIN");
				String begin = (String) annotation.getAttribute("begin");
				String end = (String) annotation.getAttribute("end");
				boolean eventFound = false;
				for (Event event : tarsqiDoc.events) {
					if (event.begin == Integer.parseInt(begin)
							&& event.end == Integer.parseInt(end)) {
						eventFound = true;
						event.addAttribute("chain", chain); }
				}
				if (! eventFound)
					System.out.println("Warning, no matching event found");
			}
		}

		if (false) {
			System.out.println();
			for (Event event : tarsqiDoc.events)
				System.out.println(event);
			System.out.println(); }
	}

}
