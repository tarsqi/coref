package org.timeml.tarsqi.components;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import static org.timeml.tarsqi.components.Sectioner.LINE;
import static org.timeml.tarsqi.components.Sectioner.PARAGRAPH;
import static org.timeml.tarsqi.components.Sectioner.SECTION;
import org.timeml.tarsqi.core.TarsqiDocument;
import org.timeml.tarsqi.core.annotations.TreeAnnotation;

/**
 * Class to represent a structural element in a document.
 *
 * A document can be represented as a list of top-level elements and each element
 * can have a non-empty list of daughters. This class is used by the Sectioner
 * which starts of with a flat list of elements and then tries to add more
 * structure using a variety of heuristics, some of which are implemented here.
 */
public class DocElement {

	public int start;
	public int end;
	public String type;

	TarsqiDocument doc;
	String text;
	String prefix;
	List<DocElement> dtrs;


	DocElement(TarsqiDocument doc, String type, int start, int end) {
		init(doc, type, start, end);
	}

	DocElement(TarsqiDocument doc, String type, int start, int end, String text) {
		init(doc, type, start, end);
		this.text = text;
	}

	final void init(TarsqiDocument doc, String type, int start, int end) {
		this.doc = doc;
		this.type = type;
		this.start = start;
		this.end = end;
		this.dtrs = new ArrayList<>();
		this.prefix = null;
	}

	boolean isEmpty() {
		return this.start == this.end;
	}

	public String getPrefix() {
		return this.prefix;
	}

	public String getFirstLine() {
		// for simple elements without dtrs the first line is the text
		String firstLine = this.text;
		// And this is when we are dealing with a paragraph or other element
		// without text but with dtrs
		if (this.dtrs.size() > 0)
			firstLine = this.dtrs.get(0).text;
		return firstLine;
	}

	/**
	 * Determine whether the element is a header.
	 *
	 * @return True if the DocElement has one line only and this line looks like
	 * a header, which currently means that the length of the line is checked.
	 */
	boolean isHeader() {
		return
			this.dtrs.size() == 1
			&& this.end - this.start < Sectioner.MAX_SHORT_SENTENCE_LENGTH;
	}

	/**
	 * Determine whether an element is a listing.
	 *
	 * Lists are defined as 2 or more lines, but with no line being longer than
	 * 30 characters.
	 *
	 * @return True if the DocElement looks like a listing.
	 */
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
	 * Checks whether the first line of the element starts with a a couple of words
	 * in all caps. If it is, and as long as the words together have more than
	 * some number of characters, then store the prefix and return true.
	 *
	 * @return boolean
	 */
	public boolean hasAllCapsPrefix() {
		String firstLine = this.getFirstLine();
		Matcher matcher = Sectioner.HEADER_PATTERN.matcher(firstLine);
		if (matcher.find()) {
			// TODO: with the new pattern we now want to maybe test here for
			// the next character
			this.prefix = matcher.group().trim();
			int prefixLength = this.prefix.length();
			if (prefixLength >= Sectioner.MIN_ALL_CAPPS_LENGTH
				&& hasBoundaryAt(prefixLength)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Split the DocElement, which typically is a Paragraph, into two parts.
	 *
	 * The first one is a Header that contains just the prefix and the second
	 * is a Paragraph that contains all remaining content. The content in the
	 * dtrs instance variable, which is a list of Lines, will be replaced with
	 * the two new elements.
	 */
	void splitOnPrefix() {
		int p1 = this.start;
		int p2 = this.start + this.prefix.length();
		int p3 = this.end;
		DocElement firstLine = this.dtrs.get(0);
		DocElement element1 = new DocElement(this.doc, Sectioner.HEADER, p1, p2);
		DocElement element2 = new DocElement(this.doc, Sectioner.SECTION, p2, p3);
		// the first new element is a header, set its text to the prefix and
		// clean out the prefix, since it has no sub structure we do not add dtrs
		element1.text = this.prefix;
		this.prefix = null;
		// add lines to the second new element, the first line is special in that it
		// needs to be altered
		String newFirstLineText = firstLine.text.substring(element1.text.length());
		element2.dtrs.add(new DocElement(this.doc, Sectioner.LINE, p2, firstLine.end, newFirstLineText));
		for (int i = 1; i < this.dtrs.size(); i++) {
			element2.dtrs.add(this.dtrs.get(i));
		}
		// replace the existing dtrs with the two new elements
		this.dtrs = new ArrayList<>();
		this.dtrs.add(element1);
		this.dtrs.add(element2);
	}

	/**
	 * Turn the document element into a single Paragraph without any daughters if
	 * the element is a Section and if all the daughters are Lines.
	 */
	public void createParagraph() {
		if (this.isParagraph()) {
			this.type = PARAGRAPH;
			this.dtrs = new ArrayList<>(); }
		for (DocElement dtr : this.dtrs)
			dtr.createParagraph();
	}

	public boolean isParagraph() {
		if (this.type.equals(PARAGRAPH))
			return true;
		if (this.type.equals(SECTION)) {
			for (DocElement dtr : this.dtrs) {
				if (! dtr.type.equals(LINE))
					return false;
			}
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		String dots = " ....................";
		String textString = "";
		if (this.text != null) {
			textString = " '" + this.text + "'";
			if (textString.length() > 70) {
				textString = textString.substring(0, 50) + dots;
			}
		}
		return String.format("<%s %d:%d%s>", this.type, this.start, this.end, textString);
	}

	void prettyPrint() {
		prettyPrint(System.out, "");
	}

	void prettyPrint(String indentation) {
		prettyPrint(System.out, indentation);
	}

	void prettyPrint(PrintStream out) {
		prettyPrint(out, "");
	}

	void prettyPrint(PrintStream out, String indentation) {
		out.println(indentation + this);
		for (DocElement dtr : this.dtrs) {
			dtr.prettyPrint(out, indentation + "   ");
		}
	}

	/**
	 * Determines whether there is a boundary in the first line of the element
	 * at position idx. Currently a boundary is defined as a period, a colon or
	 * a space.
	 *
	 * @param idx the position in the
	 * @return true if there is a boundary, false otherwise
	 */
	private boolean hasBoundaryAt(int idx) {
		String firstLine = this.getFirstLine();
		if (firstLine.length() == idx)
			return true;
		char c = firstLine.charAt(idx);
		return (c == '.' || c == ' ' || c == ':');
	}

	/**
	 * Recursively collect the text content of leave nodes. Will add the text
	 * content in the leaves parameter or recurse over the dtrs field.
	 *
	 * @param leaves the ArrayList that the text is added to
	 */
	void addLine(ArrayList<String> leaves) {
		if (this.dtrs.isEmpty())
			leaves.add(this.text);
		else {
			for (DocElement dtr : this.dtrs)
				dtr.addLine(leaves);
		}
	}

	/**
	 * Create a TreeAnnotation and add it to its parent.
	 *
	 * @param parent A TreeAnnotation, the parent of the new annotation.
	 */
	void addToTree(TreeAnnotation parent) {
		TreeAnnotation anno = new TreeAnnotation(this.type);
		anno.begin = this.start;
		anno.end = this.end;
		parent.children.add(anno);
		for (DocElement dtr : this.dtrs)
			dtr.addToTree(anno);
	}

}
