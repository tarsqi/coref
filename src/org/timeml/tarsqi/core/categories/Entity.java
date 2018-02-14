package org.timeml.tarsqi.core.categories;

import org.w3c.dom.Node;

public class Entity extends Category {

	public Entity(Node node) {
		super(node); }
		
	public int getBegin() { return (int) this.attributes.get("begin"); }
	public int getEnd() { return (int) this.attributes.get("end"); }

}
