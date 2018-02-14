package org.timeml.tarsqi.core.categories;

import java.util.HashMap;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class Category {

	public final String tagName;
	protected final Node dom;
	protected final HashMap attributes;
	
	Category(Node node) {
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
	
	public String getOrigin() { return (String) this.attributes.get("origin"); }

	public String getAttribute(String attr) {
		return (String) this.attributes.get(attr); }
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("<").append(this.tagName);
		for (Object key : this.attributes.keySet()) {
			Object val = this.attributes.get(key);
			sb.append(String.format(" %s:%s", key, val));
		}
		sb.append(">");
		return sb.toString();
	}
}
