package org.timeml.tarsqi.core.categories;

import org.timeml.tarsqi.core.categories.Entity;
import org.w3c.dom.Node;

public class Timex extends Entity {
	
	public Timex(Node node) {
		super(node); }

	public String getTID() { return (String) this.attributes.get("tid"); }
	public String getType() { return (String) this.attributes.get("type"); }
	public String getValue() { return (String) this.attributes.get("value"); }

}
