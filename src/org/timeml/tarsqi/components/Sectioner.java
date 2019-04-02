package org.timeml.tarsqi.components;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.timeml.tarsqi.core.AnnotationLayer;
import org.timeml.tarsqi.core.TarsqiDocument;
import org.timeml.tarsqi.core.annotations.TreeAnnotation;
import static org.timeml.tarsqi.definitions.Components.SECTIONER;

/**
 * Class to add section tags to a TarsqiDocument.
 *
 * This particular sectioner is written with THYME data in mind. In the future
 * there may be more sectioners, in which case we would have subclasses.
 *
 * At the moment no tags are added, but section tags will be written to the
 * standard output when you run the code.
 */

public class Sectioner {

	static String DOCUMENT = "Document";
	static String SECTION = "Section";
	static String PARAGRAPH = "Paragraph";
	static String LINE = "Line";
	static String LISTING = "Listing";
	static String HEADER = "Header";

	static int MAX_SHORT_SENTENCE_LENGTH = 50;
	static int MIN_ALL_CAPPS_LENGTH = 5;

	static Pattern HEADER_PATTERN = Pattern.compile("^[A-Z ]+");

	TarsqiDocument doc;
	List<DocElement> lines;
	List<DocElement> sections;

	public Sectioner(TarsqiDocument doc) {
		this.doc = doc;
		this.lines = new ArrayList();
		this.sections = new ArrayList();
	}

	/**
	 * Take the text content of a TarsqiDocument and create simple document
	 * structure.
	 *
	 * Document structure now means adding Section, Paragraph, Header, Line and
	 * Listing markers. Initially, any white-line separated sub text will be
	 * considered a section. Then sections are inspected using some simple
	 * heuristics to decide whether a section is a header or a listing. In
	 * addition, some section start with a section header and these headers
	 * are split off. At the end, sections that consist of lines only are
	 * replaced with a single paragraph.
	 */
	public void parse() {
		splitLines();
		createSections();
		processSections();
		createParapgraps();
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
		int i;
		for (i = 0; i < this.doc.text.length(); i++) {
			if (this.doc.text.charAt(i) == '\n') {
				this.lines.add(new DocElement(this.doc, Sectioner.LINE, start, i, sb.toString()));
				sb = new StringBuilder();
				start = i + 1;
			} else {
				sb.append(this.doc.text.charAt(i));
			}
		}
		if (start < i)
			this.lines.add(new DocElement(this.doc, Sectioner.LINE, start, i, sb.toString()));
	}

	/**
	 * Create sections from all the lines in the text.
	 *
	 * A section is a block of lines separated by one or more white lines.
	 */
	private void createSections() {
		DocElement currentSection = new DocElement(this.doc, Sectioner.SECTION, 0, 0);
		for (int i = 0; i < this.lines.size(); i++) {
			DocElement line = this.lines.get(i);
			if (line.isEmpty()) {
				addSection(currentSection);
				currentSection = new DocElement(this.doc, Sectioner.SECTION, line.end + 1, line.end + 1);
			} else {
				// update the current paragraph with the line just read
				currentSection.end = line.end;
				currentSection.dtrs.add(line);
			}
		}
		addSection(currentSection);
	}

	/**
	 * Check whether sections can be split and determine whether the section
	 * is of a special type.
	 *
	 * This is where all the interesting stuff starts, but at the moment there is
	 * not that much of it. This will eventually also be a specific to a particular
	 * domain or source.
	 */
	private void processSections() {
		for (DocElement section : this.sections) {
			if (section.isListing()) {
				section.type = Sectioner.LISTING;
			} else if (section.isHeader()) {
				section.type = Sectioner.HEADER;
				section.text = section.dtrs.get(0).text;
				section.dtrs = new ArrayList<>();
			} else if (section.hasAllCapsPrefix()) {
				// this is a Thyme specific thing, but it may generalize
				section.splitOnPrefix();
			}
		}
	}

	/**
	 * Add an element to the sections field, but only if the element is not empty.
	 *
	 * @param element The DocElement to be added.
	 */
	private void addSection(DocElement element) {
		if (! element.isEmpty())
			this.sections.add(element);
	}

	public void prettyPrint() {
		System.out.println();
		for (DocElement section : this.sections)
			section.prettyPrint(System.out);
		System.out.println();
	}

	public void write(String filename) throws FileNotFoundException {
		PrintStream ps = new PrintStream(filename);
		for (DocElement section : this.sections)
			section.prettyPrint(ps);
	}

	/**
	 * Get all leaves from the document structure created by the sectioner.
	 *
	 * @return a List of Strings where each string is the content of a leave node
	 */
	public List<String> getSections() {
		ArrayList<String> leaves = new ArrayList<>();
		for (DocElement section : this.sections)
			section.addLine(leaves);
		return leaves;
	}

	private void createParapgraps() {
		for (DocElement section : this.sections)
			section.createParagraph();
	}

	/**
	 * Take all the elements from the paragraph list and use them to create a new
	 * annotation layer on the TarsqiDocument. The layer created is a tree layer
	 * so its annotations list will have only one element.
	 */
	public void exportSections() {
		// TODO: maybe this should be done in TarsqiDocument or AnnotationLayer
		// so the sectioner needs less knowledge of other classes (well, maybe,
		// but only if the other class receives some tree-like datastructure with
		// a set of fixed accessors and elements woith fixed interfaces).
		AnnotationLayer layer = new AnnotationLayer(SECTIONER, "tree");
		TreeAnnotation top = new TreeAnnotation(DOCUMENT);
		layer.addAnnotation(top);
		this.doc.addLayer(layer);
		for (DocElement section : this.sections)
			section.addToTree(top);
	}

}
