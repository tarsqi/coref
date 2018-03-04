package org.timeml.tarsqi.core.annotations;

import org.w3c.dom.Node;

public class SLink extends Link {
	
	public SLink(Node node) {
		super(node);
		generateAttributes();
	}

	final void generateAttributes() {
		this.lid = getLID();
		this.relType = getRelType();
		this.eventInstanceID = getEventInstanceID();
		this.subordinatedEventInstance = getSubordinatedEventInstance();
		this.origin = getOrigin();
		this.syntax = getSyntax();
	}


}
