package org.timeml.tarsqi.tools.stanford;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.ling.Label;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.trees.Tree;
import java.io.File;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class StanfordWriter {

	Document doc;
	Element root, file, sentence, tokens, parse, dependencies;

	public StanfordWriter() throws ParserConfigurationException {

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		dBuilder = dbFactory.newDocumentBuilder();
		this.doc = dBuilder.newDocument();
		this.root = this.doc.createElement("stanfordnlp");
		this.file = this.doc.createElement("file");
		this.dependencies = this.doc.createElement("dependencies");
		this.doc.appendChild(root);
		this.root.appendChild(file);
	}

	void setFileName(String docname) {

		Attr attr = doc.createAttribute("name");
        attr.setValue(docname);
        this.file.setAttributeNode(attr);
	}

	public void addSentence() {

		this.sentence = this.doc.createElement("sentence");
		this.tokens = this.doc.createElement("tokens");
		this.parse = this.doc.createElement("parse");
		this.dependencies = this.doc.createElement("dependencies");
		this.root.appendChild(this.sentence);
		this.sentence.appendChild(this.tokens);
		this.sentence.appendChild(this.parse);
		this.sentence.appendChild(this.dependencies);
	}

	public void addToken(StanfordToken token) {

		Attr attr;
		Element tok = doc.createElement("token");
		String[][] attrs = {
			pair("index", token.index),
			pair("start", token.start),
			pair("end", token.end),
			pair("word", token.word),
			pair("lemma", token.lemma),
			pair("pos", token.pos) };
		addAttrs(tok, attrs);
		this.tokens.appendChild(tok);
	}

	public void addRoot(StanfordToken root) {

		Attr attr = doc.createAttribute("root");
        attr.setValue(Integer.toString(root.index));
        this.dependencies.setAttributeNode(attr);
	}

	public void addParse(String parse) {

		Element tree = doc.createElement("tree");
		tree.appendChild(doc.createTextNode(parse));
		this.parse.appendChild(tree);
	}

	void addPathsToRoot(Tree tree, Tree leaf) {
		// Not sure how to do this, and may not do it anyway because the parser
		// as I use it is just way to slow
		StringBuilder sb = new StringBuilder();
		// as a string this gives you sth like anemia-42 where the number refers,
		// but I do not know how to get to the number without splitting the string
		Label label = leaf.label();
		// and this does give you categories like NP and PP
		for (Tree n : tree.dominationPath(leaf))
			sb.append(" " + n.label());
	}

	public void addDependency(StanfordDependency stanfordDependency) {

		Attr attr;
		Element dep = doc.createElement("dependency");
		String[][] attrs = {
			pair("relation", stanfordDependency.relation),
			pair("governor", stanfordDependency.governor.index),
			pair("dependent", stanfordDependency.dependent.index) };
		addAttrs(dep, attrs);
		this.dependencies.appendChild(dep);
	}

	public void addPath(
			IndexedWord leaf,
			List<IndexedWord> catpath,
			List<SemanticGraphEdge> relpath) {

		StringBuilder sb = new StringBuilder();
		for (IndexedWord w : catpath)
			sb.append(String.format(" %s", w.tag()));
		String cats = sb.toString();
		if (cats.length() > 0) {
			cats = cats.substring(1); }
		sb = new StringBuilder();
		for (SemanticGraphEdge rel : relpath)
			sb.append(String.format(" %s", rel.getRelation()));
		String rels = sb.toString();
		if (rels.length() > 0) {
			rels = rels.substring(1); }
		Attr attr;
		Element path = doc.createElement("path");
		String[][] attrs = {
			pair("leaf", leaf.index()),
			pair("cats", cats),
			pair("rels", rels) };
		addAttrs(path, attrs);
		this.dependencies.appendChild(path);
	}

	private String[] pair(String attr, int val) {

		String[] pair = { attr, Integer.toString(val) };
		return pair; }

	private String[] pair(String attr, String val) {

		String[] pair = { attr, val };
		return pair; }

	private void addAttrs(Element element, String[][] attrs) {

		for (int i = 0 ; i < attrs.length ; i++) {
			Attr attr = doc.createAttribute(attrs[i][0]);
			attr.setValue(attrs[i][1]);
			element.setAttributeNode(attr); }}

	public void write(String filename) throws TransformerException {

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        DOMSource source = new DOMSource(this.doc);
        StreamResult result = new StreamResult(new File(filename));
        transformer.transform(source, result);
        boolean console = false;
		if (console) {
			System.out.println();
			StreamResult consoleResult = new StreamResult(System.out);
	        transformer.transform(source, consoleResult);
		}
	}

}
