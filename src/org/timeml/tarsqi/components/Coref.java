package org.timeml.tarsqi.components;


import java.io.File;
import java.util.List;
import org.timeml.tarsqi.io.ThymeFile;
import org.timeml.tarsqi.tools.stanford.StanfordNLP;
import org.timeml.tarsqi.core.TarsqiDocument;
import org.timeml.tarsqi.io.TarsqiReader;
import static org.timeml.tarsqi.utils.File.getFiles;


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
	}


	/**
	 * Run the Stanford dependency parser on ECB files.
	 *
	 * @param inputDir the directory with ECB files in TTK format, this is the
	 * directory that contains the data directory that has all of the topic
	 * directories and was created offline (by utilities/convert.py in
	 * https://github.com/tarsqi/ttk
	 *
	 * @param outputDir directory to write the output to
	 *
	 * TODO: this code is too much tied in to ECB, should have a different flow,
	 * either by handing in a directory with ttk files (with ttk extensions) that
	 * can be walked or by giving a list of ttk files.
	 *
	 * NOTE: the TODO above and the description of inputDIr are actually
	 * anticipating code that runs on all topics, note that now the input needs
	 * to be a single topic directory.
	 */
	public static void runStanford(String inputDir, String outputDir) {

		new File(outputDir).mkdir();
		StanfordNLP stan = new StanfordNLP("depparse");


		String[] ecb_files = getFiles(inputDir);
		for (String file : ecb_files) {
			String infile = inputDir + "/" + file;
			//System.out.println(infile);
			String outfile = outputDir + "/" + file;
			//System.out.println(infile);
			TarsqiDocument tarsqiDoc = new TarsqiReader().readTarsqiFile(infile);
			System.out.println(tarsqiDoc);
			stan.processTarsqiFile(infile, outfile);
		}
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

}
