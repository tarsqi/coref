package org.timeml.tarsqi.components;


import java.io.File;
import java.util.List;
import org.timeml.tarsqi.io.ThymeFile;
import org.timeml.tarsqi.tools.stanford.StanfordNLP;
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
			runStanford(inputDir, outputDir);
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
	public static void runStanford(String inputDir, String outputDir) {

		StanfordNLP parser = new StanfordNLP("depparse");
        System.out.println("SOURCE: " + inputDir + "\nTARGET: " + outputDir + "\n");
        runDependencyParserOnDirectory(parser, inputDir, outputDir);
	}

   	public static void runDependencyParserOnDirectory(
            StanfordNLP parser, String inputDir, String outputDir) {

        System.out.println("\n" + inputDir + "\n");
		new File(outputDir).mkdir();

		String[] fnames = getFileNames(inputDir);
		for (String fname : fnames) {
 			String inpath = inputDir + "/" + fname;
			String outpath = outputDir + "/" + fname;
            File file = new File(inpath);
            if (file.isDirectory())
                runDependencyParserOnDirectory(parser, inpath, outpath);
            else
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

	private static void train(String ecbDir, String depparseDir, String trainDir) {
		System.out.println(ecbDir + "\n" + depparseDir + "\n" + trainDir);
		// OFFLINE adjust converter to also print text files
		// OFFLINE run TTK on all ECB TarsqiDocuments
		// LOAD ECB files and create TarsqiDocuments
		// LOAD ECB annotations
		// LOAD dependency parses
		// ADD chain attributes to events
		// ADD dependency parses to TarsqiDocuments (paths to top to events)
	}

}
