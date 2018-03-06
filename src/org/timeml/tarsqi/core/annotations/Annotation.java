package org.timeml.tarsqi.core.annotations;

import java.util.HashMap;
import java.util.Map;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

// Could have the same setup as an Annotation in LIF, with type, id, start, end
// and a feature dictionary (here called attributes). Question: if we have id
// and we have an event would the id then always need to be copied from the eid
// or the eiid?

/**
 * Base class for all annotations. 
 * 
 * All attributes of the annotation are stored with their values in an attributes
 * HashMap where the keys and values are strings. Subclasses can use specialized
 * feature access through instance variables with the proper types. For example, 
 * the begin and end features of many annotations can be stored in begin and end
 * variables with the integer type.
 */

public class Annotation {

	/** 
	 * The annotation type, could be one of the TimeML elements like EVENT or
	 * TLINK or any other type.
	 */
	public final String type;
	
	/** 
	 * The DOM Node the annotation was created from, if any. 
	 */
	protected final Node dom;
	
	/**
	 * The attributes or features of the annotation. The keys and values in the
	 * map are always Strings. 
	 */
	protected final Map<String, String> attributes;

	/**
	 * Create an Annotation from an XML Node.
	 * 
	 * Stores the Node in the dom instance variable, sets the type instance
	 * variable to the annotation type (event, tlink, docelement, etcetera) 
	 * and stores all other attributes in a HashMap.
	 * 
	 * @param node the XML Node the annotation is to be created from
	 */
	public Annotation(Node node) {
		this.dom = node;
		this.type = node.getNodeName();
		this.attributes = new HashMap<>();
		NamedNodeMap attrs = node.getAttributes();
		for (int j = 0; j < attrs.getLength(); j++) {
			String x = attrs.item(j).getNodeName();
			String y = attrs.item(j).getNodeValue();
			this.attributes.put(x, y);
		}
	}

	/**
	 * Return the value of a key in the attributes map. 
	 * 
	 * @param attr The attribute to find
	 * @return String or null
	 */
	public String getAttribute(String attr) {
		return this.attributes.get(attr); }
	
	public boolean isEvent() { return false; }

	public boolean isTimex() { return false; }

	public boolean isALink() { return false; }

	public boolean isSLink() { return false; }

	public boolean isTLink() { return false; }
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("<").append(this.type);
		for (String key : this.attributes.keySet()) {
			String val = this.attributes.get(key);
			sb.append(String.format(" %s:%s", key, val)); }
		sb.append(">");
		return sb.toString();
	}
}
