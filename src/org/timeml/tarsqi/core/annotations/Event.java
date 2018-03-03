package org.timeml.tarsqi.core.annotations;

import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Node;

public class Event extends Entity {
	
	public static final String EID = "eid";
	public static final String EIID = "eiid";
	public static final String CLASS = "class";
	public static final String TENSE = "tense";
	public static final String ASPECT = "aspect";
	
	public String tense, aspect;
	
	public Event(Node node) {
		super(node); 
		// TODO: hmmm, not sure how to deal with all these regular TimeML 
		// attributes, it would be nice to just have an annotation object much
		// like the one in LIF, but we would probably also enjoy specialized
		// behavior for some types. If we have a variable like this.tense,
		// should it then be private (and save) or public (and easy to access)? 
		generateAttributes();
	}

	final void generateAttributes() {
		this.tense = generateTenseValue();
		this.aspect = generateAspectValue();
	}
	
	private String generateTenseValue() {
		return (String) this.attributes.getOrDefault(TENSE, "NONE"); }

	private String generateAspectValue() {
		return (String) this.attributes.getOrDefault(ASPECT, "NONE"); }

	public boolean checkAttributes() {
		List<Object[]> attrs = new ArrayList();
		attrs.add( new Object[] {this.tense, generateTenseValue()} );
		attrs.add( new Object[] {this.aspect, generateAspectValue()} );
		for (Object[] pair : attrs) {
			if (pair[0] != pair[1]) return false; }
		return true;
	}


	public String getEID() { return (String) this.attributes.get("eid"); }
	public String getEIID() { return (String) this.attributes.get("eiid"); }
	public String getPos() { return (String) this.attributes.get("pos"); }
	public String getEpos() { return (String) this.attributes.get("epos"); }
	public String getForm() { return (String) this.attributes.get("form"); }
	//public String getTense() { return (String) this.attributes.getOrDefault("tense", "NONE"); }
	public String getAspect() { return (String) this.attributes.get("aspect"); }
	public String getEventClass() { return (String) this.attributes.get("class"); }

	// 
	public String getTense() { return this.tense; }
	
}
