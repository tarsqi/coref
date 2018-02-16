package org.timeml.tarsqi.tools.stanford;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class StanfordSentence {
	
	Element dom;
	int index;
	List<StanfordToken> tokens;
	Map<Integer, StanfordToken> tokenIdx;
	List<StanfordDependency> dependencies;
	StanfordToken dependenciesRoot;
	
	public StanfordSentence(int index, Node sentence) {

		this.dom = (Element) sentence;
		this.index = index;
		initializeTokens();
		initializeDependencies();
	}

	private void initializeTokens() {
		this.tokens = new ArrayList();
		this.tokenIdx = new HashMap() {};
		NodeList tokenElements = this.dom.getElementsByTagName("token");
		for (int i = 0; i < tokenElements.getLength(); i++) {
			StanfordToken tok = new StanfordToken(tokenElements.item(i));
			this.tokens.add(tok);
			this.tokenIdx.put(tok.index, tok); }
	}

	private void initializeDependencies() {
		this.dependencies = new ArrayList();
		NodeList dependencyElements = this.dom.getElementsByTagName("dependency");
		for (int i = 0; i < dependencyElements.getLength(); i++) {
			StanfordDependency dep = 
					new StanfordDependency(dependencyElements.item(i), this.tokenIdx);
			//System.out.println(dep);
			this.dependencies.add(dep); }
		Element dependenciesElement = (Element) this.dom.getElementsByTagName("dependencies").item(0);
		String rootIndex = dependenciesElement.getAttribute("root");
		this.dependenciesRoot = this.tokenIdx.get(Integer.parseInt(rootIndex));
	}
	
	public int length() {
		return this.tokens.size();
	}
}
