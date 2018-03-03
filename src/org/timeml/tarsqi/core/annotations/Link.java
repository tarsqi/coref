package org.timeml.tarsqi.core.annotations;

import org.w3c.dom.Node;

public class Link extends Annotation{
	
	public Link(Node node) {
		super(node); }

	public String getLID() { return (String) this.attributes.get("lid"); }
	public String getRelType() { return (String) this.attributes.get("relType"); }

}
