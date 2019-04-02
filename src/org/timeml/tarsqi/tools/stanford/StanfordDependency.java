package org.timeml.tarsqi.tools.stanford;

import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import java.util.Map;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class StanfordDependency {

	public StanfordToken governor, dependent;
	public String relation;

	public StanfordDependency(SemanticGraphEdge edge) {
		this.relation = edge.getRelation().getShortName();
		this.governor = new StanfordToken(edge.getGovernor());
		this.dependent = new StanfordToken(edge.getDependent());
	}

	StanfordDependency(Node node, Map<Integer, StanfordToken> tokenIdx) {
		Element element = (Element) node;
		this.relation = element.getAttribute("relation");
		int governorIndex = Integer.parseInt(element.getAttribute("governor"));
		int dependentIndex = Integer.parseInt(element.getAttribute("dependent"));
		this.governor = tokenIdx.get(governorIndex);
		this.dependent = tokenIdx.get(dependentIndex);
	}

	public String toString() {
		return String.format(
				"<Dependency %d:%s %s %d:%s>",
				this.governor.index, this.governor.word, this.relation,
				this.dependent.index, this.dependent.word);
	}

	public String prettyPrint() {
		return String.format(
				"<Dependency %s\n   %s\n   %s'>",
				this.relation, this.governor, this.dependent);
	}
}
