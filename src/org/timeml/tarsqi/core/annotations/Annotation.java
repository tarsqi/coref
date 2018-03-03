package org.timeml.tarsqi.core.annotations;

import java.util.HashMap;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

// Maybe call this Annotation and rename the package

// Now it is always created from an XML node, will probably need other ways to
// create one and maybe do not want to keep the the dom node in this.dom.

// Could have the same setup as an ANnoation in LIF, with type, id, start, end
// and a feature dictionary (here called attributes). Question: if we have id
// and we have an event would the id then always need to be copied from the eid
// or the eiid?

public class Annotation {

	public final String tagName;
	protected final Node dom;
	protected final HashMap attributes;
	
	Annotation(Node node) {
		this.dom = node;
		this.tagName = node.getNodeName();
		this.attributes = new HashMap();
		NamedNodeMap attrs = node.getAttributes();
		for (int j = 0; j < attrs.getLength(); j++) {
			String x = attrs.item(j).getNodeName();
			String y = attrs.item(j).getNodeValue();
			this.attributes.put(x, y);
		}
	}

	/** 
`	 * Convenience method to get the "origin" attribute in the attributes map.
	 * 
	 * @return String or null
	 */
	public Object getOrigin() {
		return getAttribute("origin"); }

	/**
	 * Return the value of a key in the attributes map. 
	 * 
	 * @param attr The attribute to find
	 * @return String or null
	 */
	public Object getAttribute(String attr) {
		return this.attributes.get(attr); }
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("<").append(this.tagName);
		for (Object key : this.attributes.keySet()) {
			Object val = this.attributes.get(key);
			sb.append(String.format(" %s:%s", key, val)); }
		sb.append(">");
		return sb.toString();
	}
}
