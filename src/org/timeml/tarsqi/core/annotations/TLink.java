package org.timeml.tarsqi.core.annotations;

import org.w3c.dom.Node;

public class TLink extends Link {
	
	public TLink(Node node) {
		super(node); }
	
	public String getTimeID() { return (String) this.attributes.get("timeID"); }
	public String getEventInstanceID() { return (String) this.attributes.get("eventInstanceID"); }
	public String getRelatedToTime() { return (String) this.attributes.get("relatedToTime"); }
	public String getRelatedToEventInstance() { return (String) this.attributes.get("relatedToEventInstance"); }

}
