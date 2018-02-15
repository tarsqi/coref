package org.timeml.tarsqi.tools.stanford;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class StanfordDocument {

	String filename;
	Document doc;
	List<StanfordSentence> sentences;
			
	public StanfordDocument(String filename) {

		this.filename = filename;
		readXML(filename);
		NodeList sentences = this.doc.getElementsByTagName("sentence");
		Node sentence;
		for (int i = 0; i < sentences.getLength(); i++) {
			sentence = sentences.item(i);
			this.sentences.add(new StandfordSentence(sentence));
		}
	}

	private boolean isValid() {
		return this.doc != null; }
	
	private void readXML(String filename) {
		
		try {
			File xmlFile = new File(filename);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			this.doc = dBuilder.parse(xmlFile);
		} catch (SAXException ex) {
			Logger.getLogger(StanfordDocument.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(StanfordDocument.class.getName()).log(Level.SEVERE, null, ex);
		} catch (ParserConfigurationException ex) {
			Logger.getLogger(StanfordDocument.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	@Override
	public String toString() {
		return String.format("<StanfordDocument %s", this.filename); }
	
	public void prettyPrint() {
		System.out.println(this);
		System.out.println();
	}

	public void printXML() {
		try {
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			DOMSource source = new DOMSource(this.doc);
			StreamResult console = new StreamResult(System.out);
			System.out.println();
			transformer.transform(source, console);
		} catch (TransformerConfigurationException ex) {
			Logger.getLogger(StanfordDocument.class.getName()).log(Level.SEVERE, null, ex);
		} catch (TransformerException ex) {
			Logger.getLogger(StanfordDocument.class.getName()).log(Level.SEVERE, null, ex);
		}

	}
 
	
}
