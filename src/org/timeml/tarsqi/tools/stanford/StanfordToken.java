package org.timeml.tarsqi.tools.stanford;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.IndexedWord;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Class to represent a Stanford token. 
 * 
 * The main goal is to provide simpler access to token information and abstract
 * away from whether the information was obtained from a CoreLabel instance or
 * an IndexedWord instance.
 */
public class StanfordToken {
	
	public int start, end, index;
	public String word, lemma, pos;
	
	StanfordToken(CoreLabel token) {
		this.index = token.index();
		this.start = token.beginPosition();
		this.end = token.endPosition();
		this.word = token.word();
		this.lemma = token.lemma();
		this.pos = token.tag();
	}

	StanfordToken(IndexedWord token) {
		this.index = token.index();
		this.start = token.beginPosition();
		this.end = token.endPosition();
		this.word = token.word();
		this.lemma = token.lemma();
		this.pos = token.tag();
	}

	StanfordToken(Node token) {
		Element element = (Element) token;
		this.index = Integer.parseInt(element.getAttribute("index"));
		this.start = Integer.parseInt(element.getAttribute("start"));
		this.end = Integer.parseInt(element.getAttribute("end"));
		this.word = element.getAttribute("word");
		this.lemma = element.getAttribute("lemma");
		this.pos = element.getAttribute("pos");
	}
	
	@Override
	public String toString() {
		return String.format(
				"<Token %d %d-%d %s %s>",
				this.index, this.start, this.end, this.word, this.pos);
	}
}