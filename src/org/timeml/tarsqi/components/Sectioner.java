package org.timeml.tarsqi.components;

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
	
	TarsqiDocument doc;
	List<DocElement> lines;
	List<DocElement> paras;
	
	public Sectioner(TarsqiDocument doc) {
		this.doc = doc;
		this.lines = new ArrayList();
		this.paras = new ArrayList();
	}
	
	/**
	 * Take the text content of the TarsqiDocument and create simple document 
	 * structure. 
	 * 
	 * Document structure now means adding paragraph and section markers. Any 
	 * white-line separated sub text will be considered a paragraph. In addition,
	 * some very short lines will be made into separate elements although it is
	 * not yet clear what to name them. This includes enumerations. Very short
	 * paragraphs may be named headers. And we may also split off beginnings of
	 * lines that are all caps.
	 */
	public void parse() {

		splitLines();
		createParagraphs();
		processParagraphs();
		print();
	}

	/**
	 * Split the text content into a list of lines.
	 */
	private void splitLines() {
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
	 * This is where all the interesting stuff happens, but at the moment there is 
	 * not that much of it.
	 */
	private void processParagraphs() {
		for (DocElement para : this.paras) {
			if (para.isListing())
				para.type = Sectioner.LISTING;
			else if (para.isHeader()) 
				para.type = Sectioner.HEADER;
			else if (para.hasAllCapsPrefix())
				// this is a Thyme specific thing, but it may generalize
				para.splitOnPrefix();
		}
	}
	
	private void addParagraph(DocElement element) {
		if (! element.isEmpty())
			this.paras.add(element);
	}
	
	private void print() {
		System.out.println();
		for (DocElement line : this.lines)
			System.out.println(line);
		System.out.println();
		for (DocElement para : this.paras)
			para.pp("");
		System.out.println();
	}

}


class DocElement {

	static int MAX_SHORT_SENTENCE_LENGTH = 50;

	static Pattern HEADER_PATTERN = Pattern.compile("^[A-Z ]+( |\\.)");

	public int start, end;
	String type, text, prefix;
	List<DocElement> dtrs;

	DocElement(String type, int start, int end) {
		init(type, start, end);
	}

	DocElement(String type, int start, int end, String text) {
		init(type, start, end);
		this.text = text;
	}

	final void init(String type, int start, int end) {
		this.type = type;
		this.start = start;
		this.end = end;
		this.dtrs = new ArrayList<>();
		this.prefix = null;
	}
	
	boolean isEmpty() {
		return this.start == this.end;
	}
	
	/** 
	 * Determine whether the element is a header.
	 * 
	 * @return True if the paragraph has one line only and this line looks like
	 * a header, which currently means that the length of the line is checked.
	 */
	boolean isHeader() {
		return this.dtrs.size() == 1 
				&& this.end - this.start < MAX_SHORT_SENTENCE_LENGTH;
	}
	
	boolean isListing() {
		if (this.dtrs.size() == 1)
			return false;
		for (DocElement line : this.dtrs) {
			if (line.text.length() > 30)
				return false;
		}
		return true;
	}

	/** 
	 * Determines whether the element starts with what looks like a section header.
	 * 
	 * Only looks at the first line of the element.
	 */
	boolean hasAllCapsPrefix() {
		String firstLine = this.dtrs.get(0).text;
		Matcher matcher = DocElement.HEADER_PATTERN.matcher(firstLine);
		if (matcher.find()) {
			this.prefix = matcher.group();
			return true;
		}
		return false;
	}

	void splitOnPrefix() {
		int p1 = this.start;
		int p2 = this.start + this.prefix.length();
		int p3 = this.end;
		DocElement firstLine = this.dtrs.get(0);
		DocElement element1 = new DocElement(Sectioner.HEADER, p1, p2);
		DocElement element2 = new DocElement(Sectioner.PARAGRAPH, p2, p3);
		// the first new element is a header, set its text to the prefix and
		// clean out the prefix, since it has no sub structure we do not add dtrs
		element1.text = this.prefix;
		this.prefix = null;
		// add lines to the second new element, the first line is special in that it
		// needs to be altered
		String newFirstLineText = firstLine.text.substring(element1.text.length());
		element2.dtrs.add(
			new DocElement(Sectioner.LINE, p2, firstLine.end, newFirstLineText));
		for (int i = 1; i < this.dtrs.size(); i++)
			element2.dtrs.add(
				this.dtrs.get(i));
		// replace the existing dtrs with the two new elements
		this.dtrs = new ArrayList<>();
		this.dtrs.add(element1);
		this.dtrs.add(element2);
	}

	
	@Override
	public String toString() {
		String textString = "";
		if (this.text != null)
			textString = " '" + this.text + "'";
		return String.format("<%s %d:%d%s>", 
				this.type, this.start, this.end, textString);
	}

	
	void pp(String indentation) {
		System.out.println(indentation + this);
		for (DocElement dtr : this.dtrs)
			dtr.pp(indentation + "   ");
	}
	
}
