package org.timeml.tarsqi.tools.stanford;

import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.trees.GrammaticalRelation;

public class StanfordDependency {

	public StanfordToken governor, dependent;
	public GrammaticalRelation relation;
	
	public StanfordDependency(SemanticGraphEdge edge) {
		this.relation = edge.getRelation();
		this.governor = new StanfordToken(edge.getGovernor());
		this.dependent = new StanfordToken(edge.getDependent());
	}
	
	@Override
	public String toString() {
		return String.format(
				"<Dependency %s\n   %s\n   %s'>", 
				this.relation, this.governor, this.dependent);
	}
}
