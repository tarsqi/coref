package org.timeml.tarsqi.core.annotations;

import org.w3c.dom.Node;

public class ALink extends Link {
	
	public ALink(Node node) {
		super(node);
		generateAttributes();
	}
	
	final void generateAttributes() {
		this.lid = getLID();
		this.relType = getRelType();
		this.eventInstanceID = getEventInstanceID();
		this.relatedToEventInstance = getRelatedToEventInstance();
		this.origin = getOrigin();
		this.syntax = getSyntax();
	}
	
}
