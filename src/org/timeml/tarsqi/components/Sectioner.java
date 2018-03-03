package org.timeml.tarsqi.components;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.timeml.tarsqi.core.TarsqiDocument;

public class Sectioner {

	static String PARAGRAPH = "Paragraph";
	static String LINE = "Line";
	static String LISTING = "Listing";
	static String HEADER = "Header";

	static int MAX_SHORT_SENTENCE_LENGTH = 50;
	static int MIN_ALL_CAPPS_LENGTH = 5;

	static Pattern HEADER_PATTERN = Pattern.compile("^[A-Z ]+");
	
	TarsqiDocument doc;
	List<DocElement> lines;
	List<DocElement> paras;
	
	public Sectioner(TarsqiDocument doc) {
		this.doc = doc;
		this.lines = new ArrayList();
		this.paras = new ArrayList();
	}
	
	/**
	 * Take the text content of a TarsqiDocument and create simple document 
	 * structure. 
	 * 
	 * Document structure now means adding Paragraph, Header, Line and Listing
	 * markers. Initially, any white-line separated sub text will be considered a
	 * paragraph. Then paragraphs are inspected with some simple heuristics to
	 * decide whether a paragraph is a header or a listing. In addition, some
	 * paragraphs start with a section header and these headers are split off.
	 */
	public void parse() {

		splitLines();
		createParagraphs();
		processParagraphs();
		//print();
	}

	public List<DocElement> getLines() {
		return this.lines;	
	}
	
	/**
	 * Split the text content into a list of lines.
	 */
	public void splitLines() {
		StringBuilder sb;
		sb = new StringBuilder();
		int start = 0;
		int i = 0;
		for (i = 0; i < this.doc.text.length(); i++) {
			if (this.doc.text.charAt(i) == '\n') {
				this.lines.add(new DocElement(Sectioner.LINE, start, i, sb.toString()));
				sb = new StringBuilder();
				start = i + 1;
			} else {
				sb.append(this.doc.text.charAt(i));
			}
		}
		if (start < i)
			this.lines.add(new DocElement(Sectioner.LINE, start, i, sb.toString()));
	}

	/**
	 * Create paragraphs from all the lines in the text.
	 * 
	 * A paragraph is a block of lines separated by one or more white lines.
	 */
	private void createParagraphs() {
		DocElement currentPara = new DocElement(Sectioner.PARAGRAPH, 0, 0);
		for (int i = 0; i < this.lines.size(); i++) {
			DocElement line = this.lines.get(i);
			if (line.isEmpty()) {
				addParagraph(currentPara);
				currentPara = new DocElement(Sectioner.PARAGRAPH, line.end + 1, line.end + 1);
			} else {
				// update the current paragraph with the line just read
				currentPara.end = line.end;
				currentPara.dtrs.add(line);
			}
		}
		addParagraph(currentPara);
	}

	/**
	 * Check whether paragraphs can be split and determine whether the paragraph 
	 * is of a special type.
	 * 
	 * This is where all the interesting stuff starts, but at the moment there is 
	 * not that much of it.
	 */
	private void processParagraphs() {
		for (DocElement para : this.paras) {
			if (para.isListing()) {
				para.type = Sectioner.LISTING;
			} else if (para.isHeader()) {
				para.type = Sectioner.HEADER;
				para.text = para.dtrs.get(0).text;
				para.dtrs = new ArrayList<>();
			} else if (para.hasAllCapsPrefix()) {
				// this is a Thyme specific thing, but it may generalize
				para.splitOnPrefix();
			}
		}
	}
	
	private void addParagraph(DocElement element) {
		if (! element.isEmpty())
			this.paras.add(element);
	}
	
	public void prettyPrint() {
		//System.out.println();
		//for (DocElement line : this.lines)
		//	System.out.println(line);
		System.out.println();
		for (DocElement para : this.paras)
			para.prettyPrint(System.out);
		System.out.println();
	}
	
	public void write(String filename) throws FileNotFoundException {
		PrintStream ps = new PrintStream(filename);
		for (DocElement para : this.paras)
			para.prettyPrint(ps);
	}

}


