package org.timeml.tarsqi.core.annotations;

import org.w3c.dom.Node;

public class TLink extends Link {
	
	public TLink(Node node) {
		super(node);
		generateAttributes();
	}
	
	final void generateAttributes() {
		this.lid = getLID();
		this.relType = getRelType();
		if (this.attributes.containsKey(EVENT_INSTANCE_ID))
			this.eventInstanceID = getEventInstanceID();
		if (this.attributes.containsKey(RELATED_TO_EVENT_INSTANCE))
			this.relatedToEventInstance = getRelatedToEventInstance();
		if (this.attributes.containsKey(TIME_ID))
			this.timeID = getTimeID();
		if (this.attributes.containsKey(RELATED_TO_TIME))
			this.relatedToTime = getRelatedToTime();
		this.origin = getOrigin();
		this.syntax = getSyntax();

	}

}
