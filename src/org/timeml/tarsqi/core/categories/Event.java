package org.timeml.tarsqi.core.categories;

import org.timeml.tarsqi.core.categories.Entity;
import org.w3c.dom.Node;

public class Event extends Entity {
	
	public Event(Node node) { 
		super(node); }

	public String getEID() { return (String) this.attributes.get("eid"); }
	public String getEIID() { return (String) this.attributes.get("eiid"); }
	public String getPos() { return (String) this.attributes.get("pos"); }
	public String getEpos() { return (String) this.attributes.get("epos"); }
	public String getForm() { return (String) this.attributes.get("form"); }
	public String getTense() { return (String) this.attributes.get("tense"); }
	public String getAspect() { return (String) this.attributes.get("aspect"); }
	public String getEventClass() { return (String) this.attributes.get("class"); }
	
	
}
