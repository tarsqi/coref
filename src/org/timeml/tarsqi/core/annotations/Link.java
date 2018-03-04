package org.timeml.tarsqi.core.annotations;

import org.w3c.dom.Node;

public class Link extends TimemlAnnotation {
	
	public String lid, relType, syntax;
	public String eventInstanceID, timeID, relatedToTime;
	public String relatedToEventInstance, subordinatedEventInstance;
	
	public Link(Node node) {
		super(node);
	}
}
