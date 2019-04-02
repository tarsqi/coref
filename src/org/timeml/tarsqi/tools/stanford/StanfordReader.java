
package org.timeml.tarsqi.tools.stanford;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.timeml.tarsqi.io.TarsqiReader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class StanfordReader {

	public static ArrayList<StanfordSentence> readDependencyParse(String filename) {

		ArrayList<StanfordSentence> sentences = new ArrayList();
        try {
			Document doc = getDOM(filename);
			Element xmlRoot = doc.getDocumentElement();
			NodeList sentenceNodes = xmlRoot.getElementsByTagName("sentence");
			for (int i = 0 ; i < sentenceNodes.getLength() ; i++) {
	            Node sent = sentenceNodes.item(i);
				StanfordSentence stanfordSentence = new StanfordSentence(i, sent);
				//stanfordSentence.prettyPrint();
				sentences.add(stanfordSentence); }
        } catch (SAXException | IOException | ParserConfigurationException ex) {
			Logger.getLogger(TarsqiReader.class.getName()).log(Level.SEVERE, null, ex);
		}
		return sentences;
    }

	private static Document getDOM(String filename)
			throws SAXException, IOException, ParserConfigurationException {

		File file = new File(filename);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(file);
		return doc;
	}

}