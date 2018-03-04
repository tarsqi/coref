package org.timeml.tarsqi.core.annotations;

import org.w3c.dom.Node;

public class Timex extends TimemlAnnotation {
	
	public String tid, type, value, functionInDocument;
	
	public Timex(Node node) {
		super(node); 
		generateAttributes();
	}

	final void generateAttributes() {
		this.tid = getTID();
		this.type = getType(); 
		this.value = getValue();
		this.functionInDocument = getFunctionInDocument();
	}
}
