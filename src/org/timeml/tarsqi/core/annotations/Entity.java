package org.timeml.tarsqi.core.annotations;

import org.w3c.dom.Node;

public class Entity extends Annotation {

	public Entity(Node node) {
		super(node); }
		
	public int getBegin() { return (int) this.attributes.get("begin"); }
	public int getEnd() { return (int) this.attributes.get("end"); }

}
