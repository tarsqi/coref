package org.timeml.tarsqi.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.timeml.tarsqi.core.TarsqiDocument;
import org.timeml.tarsqi.core.annotations.ALink;
import org.timeml.tarsqi.core.annotations.Event;
import org.timeml.tarsqi.core.annotations.SLink;
import org.timeml.tarsqi.core.annotations.TLink;
import org.timeml.tarsqi.core.annotations.Timex;
import org.w3c.dom.Document;	
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * The sole tasks of this class is to take a filename and create an instance if
 * TarsqiDocument. It has public static methods for two kinds of input files:
 * regular text files and files in the Tarsqi TTK format.
 */
public class TarsqiReader {

	static final String[] 
		ENTITY_NODES = {"EVENT", "TIMEX3", "lex", "s", "ng", "vb", "docelement"};

	static final String[] 
		LINK_NODES = {"ALINK", "SLINK", "TLINK" };
	
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

	private void readSourceTags(Element ttk) {
		// these should be added to their own layer
		Node source_tags = ttk.getElementsByTagName("source_tags").item(0);
		NodeList tags = source_tags.getChildNodes();
		for (int i = 0; i < tags.getLength(); i++) {
			Node n = tags.item(i);
			String tagName = n.getNodeName();
		}
	}
	
	private void readTarsqiTags(Element ttk) {
		Node tarsqi_tags = ttk.getElementsByTagName("tarsqi_tags").item(0);
		NodeList tags = tarsqi_tags.getChildNodes();
		for (int i = 0; i < tags.getLength(); i++) {
			Node n = tags.item(i);
			String tagName = n.getNodeName();
			if (n.getNodeType() != Node.ELEMENT_NODE) continue;
			if (nodeIsEntity(tagName))
				addEntity(n, tagName);
			else if (nodeIsLink(tagName))
				addLink(n, tagName);
		}
	}


	private void addEntity(Node n, String tagName) {
		if (tagName.equals("EVENT")) {
			Event e = new Event(n);
			this.document.addEvent(e);
		} else if (tagName.equals("TIMEX3")) {
			Timex t = new Timex(n);
			this.document.addTimex(t);
		}
	}

	private void addLink(Node n, String tagName) {
		if (tagName.equals("ALINK")) {
			ALink al = new ALink(n);
			this.document.addALink(al);
		} else if (tagName.equals("SLINK")) {
			SLink sl = new SLink(n);
			this.document.addSLink(sl);
		} else if (tagName.equals("TLINK")) {
			TLink tl = new TLink(n);
			this.document.addTLink(tl);
		}
	}
	
	private boolean nodeIsEntity(String nodeName) {
		return nodeIsOneOf(nodeName, ENTITY_NODES); }

	private boolean nodeIsLink(String nodeName) {
		return nodeIsOneOf(nodeName, LINK_NODES); }

	private boolean nodeIsOneOf(String nodeName, String[] nodeNames) {
		// For small arrays, this is claimed to be more eficient than using a set
		for(String s: nodeNames){
			if(s.equals(nodeName)) return true; }
		return false;
	}

}
