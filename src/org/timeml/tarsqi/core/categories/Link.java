package org.timeml.tarsqi.core.categories;

import org.w3c.dom.Node;

public class Link extends Category{
	
	public Link(Node node) {
		super(node); }

	public String getLID() { return (String) this.attributes.get("lid"); }
	public String getRelType() { return (String) this.attributes.get("relType"); }

}
