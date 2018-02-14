package org.timeml.tarsqi.core;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.timeml.tarsqi.core.categories.ALink;
import org.timeml.tarsqi.core.categories.Event;
import org.timeml.tarsqi.core.categories.SLink;
import org.timeml.tarsqi.core.categories.TLink;
import org.timeml.tarsqi.core.categories.Timex;
import org.w3c.dom.Document;	
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class TarsqiReader {

	public static final String[] 
		ENTITY_NODES = {"EVENT", "TIMEX3", "lex", "s", "ng", "vb", "docElement"};

	public static final String[] 
		LINK_NODES = {"ALINK", "SLINK", "TLINK" };
	
	public TarsqiDocument read(String filename) {
		File file = new File(filename);
		return read(file);
	}
	
	public TarsqiDocument read(File file) {

		TarsqiDocument document = new TarsqiDocument(file.getPath());
		
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);
			Element ttk = doc.getDocumentElement();
			ttk.normalize();

			NodeList children = ttk.getChildNodes();
			Node text = ttk.getElementsByTagName("text").item(0);
			document.addText(text.getFirstChild().getNodeValue());
			Node tarsqi_tags = ttk.getElementsByTagName("tarsqi_tags").item(0);
			NodeList tags = tarsqi_tags.getChildNodes();
			
			for (int i = 0; i < tags.getLength(); i++) {
				Node n = tags.item(i);
				String tagName = n.getNodeName();
				if (n.getNodeType() != Node.ELEMENT_NODE) continue;
				if (nodeIsEntity(tagName))
					addEntity(document, n, tagName);
				else if (nodeIsLink(tagName))
					addLink(document, n, tagName);
			}

		} catch (SAXException ex) {
			Logger.getLogger(TarsqiReader.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(TarsqiReader.class.getName()).log(Level.SEVERE, null, ex);
		} catch (ParserConfigurationException ex) {
			Logger.getLogger(TarsqiReader.class.getName()).log(Level.SEVERE, null, ex);
		}

		return document;
	}

	private void addEntity(TarsqiDocument document, Node n, String tagName) {
		if (tagName.equals("EVENT")) {
			Event e = new Event(n);
			document.addEvent(e);
		} else if (tagName.equals("TIMEX3")) {
			Timex t = new Timex(n);
			document.addTimex(t);
		}
	}

	private void addLink(TarsqiDocument document, Node n, String tagName) {
		if (tagName.equals("ALINK")) {
			ALink al = new ALink(n);
			document.addALink(al);
		} else if (tagName.equals("SLINK")) {
			SLink sl = new SLink(n);
			document.addSLink(sl);
		} else if (tagName.equals("TLINK")) {
			TLink tl = new TLink(n);
			document.addTLink(tl);
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