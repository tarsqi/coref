package org.timeml.tarsqi.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.timeml.tarsqi.core.TarsqiDocument;
import org.timeml.tarsqi.core.AnnotationLayer;
import org.timeml.tarsqi.core.annotations.Annotation;
import org.timeml.tarsqi.core.annotations.AnnotationFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * The sole tasks of this class is to take a filename and create an instance of
 * TarsqiDocument. It has public static methods for two kinds of input files:
 * regular text files and files in the Tarsqi TTK format.
 */
public class TarsqiReader {

	TarsqiDocument document;

	/**
	 * Reads a file and creates a TarsqiDocument. The entire file content is
	 * put as is in the TarsqiDocument text instance variable.
	 *
	 * @param filename The file to create a TarsqiDocument for
	 * @return TarsqiDocument
	 * @throws FileNotFoundException
	 */
	public TarsqiDocument readTextFile(String filename) throws FileNotFoundException {
		File file = new File(filename);
		this.document = new TarsqiDocument(file.getPath());
		String content = new Scanner(file).useDelimiter("\\A").next();
		this.document.setText(content);
		return this.document;
	}

	/**
	 * Read a file in the Tarsqi TTK format and create a TarsqiDocument.
	 *
	 * @param filename The name of a file
	 * @return TarsqiDocument
	 */
	public TarsqiDocument readTarsqiFile(String filename) {

		File file = new File(filename);
		this.document = new TarsqiDocument(file.getPath());

		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);
			Element ttk = doc.getDocumentElement();
			ttk.normalize();
			Node text = ttk.getElementsByTagName("text").item(0);
			this.document.setText(text.getFirstChild().getNodeValue());
			readSourceTags(ttk);
			readTarsqiTags(ttk);
		} catch (SAXException | IOException | ParserConfigurationException ex) {
			Logger.getLogger(TarsqiReader.class.getName()).log(Level.SEVERE, null, ex);
		}

		return document;
	}

	/**
	 * Read the tags in the source_tags tag and add them to the TarsqiDocument.
	 *
	 * The source of the new layer will be SOURCE_TAGS.
	 *
	 * @param ttk the top-level element of the ttk document
	 */
	private void readSourceTags(Element ttk) {
		ArrayList<Node> source_tags = getSubElements(ttk, "source_tags");
		AnnotationLayer layer = new AnnotationLayer("SOURCE_TAGS");
		this.document.addLayer(layer);
		for (Node n : source_tags) {
			Annotation annotation = new Annotation(n);
			layer.addAnnotation(annotation);
		}
	}

	/**
	 * Read the tags in the tarsqi_tags tag and add them to the TarsqiDocument.
	 *
	 * The source of the new layer will be TARSQI_TAGS.
	 *
	 * @param ttk the top-level element of the ttk document
	 */
	private void readTarsqiTags(Element ttk) {
		ArrayList<Node> tarsqi_tags = getSubElements(ttk, "tarsqi_tags");
		AnnotationLayer layer = new AnnotationLayer("TARSQI_TAGS");
		this.document.addLayer(layer);
		for (Node n : tarsqi_tags) {
			Annotation annotation = AnnotationFactory.createAnnotation(n);
			// TODO: maybe print warning if annotation is null
			if (annotation != null)
				layer.addAnnotation(annotation);
		}
		this.document.promoteTarsqiTags();
	}

	/**
	 * Utility method to get the elements that are immediate children of the
	 * element identified by a tag name.
	 *
	 * @param top the element in which you search for the element that you want
	 * the children of
	 * @param tagName the element that you want the children of
	 * @return an ArrayList of Node instances
	 */
	private static ArrayList<Node> getSubElements(Element top, String tagName) {
		ArrayList<Node> result = new ArrayList<>();
		Node n = top.getElementsByTagName(tagName).item(0);
		NodeList tags = n.getChildNodes();
		for (int i = 0; i < tags.getLength(); i++) {
			Node sub = tags.item(i);
			if (sub.getNodeType() == Node.ELEMENT_NODE)
				result.add(sub);
		}
		return result;
	}

}
