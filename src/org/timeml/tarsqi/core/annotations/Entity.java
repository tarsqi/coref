package org.timeml.tarsqi.core.annotations;

import org.w3c.dom.Node;

public class Entity extends Annotation {

	public Entity(Node node) {
		super(node); }
		
	public int getBegin() { 
		return Integer.parseInt(this.getAttribute("begin")); }

	public int getEnd() {
		return Integer.parseInt(this.getAttribute("end")); }

}
