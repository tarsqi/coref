package org.timeml.tarsqi.core.annotations;

import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Node;

public class Event extends TimemlAnnotation {
	
	public String eid, eiid, eclass, pos, epos;
	public String tense, aspect, polarity, modality;
	
	public Event(Node node) {
		super(node); 
		generateAttributes();
	}

	final void generateAttributes() {
		this.begin = getBegin();
		this.end = getEnd();
		this.eid = getEID();
		this.eiid = getEIID();
		this.eclass = getEClass();
		this.pos = getPOS();
		this.epos = getEPOS();
		this.tense = getTense();
		this.aspect = getAspect();
		this.polarity = getPolarity();
		this.modality = getModality();
		this.origin = getOrigin();
	}

	public boolean checkAttributes() {
		// NOTE. With the attributes map plus the added top-level fields there is
		// some redundancy. In addition, while the map is protected any outsider
		// could change the other top-level field. This method compares the two
		// sets of attributes.
		List<Object[]> attrs = new ArrayList();
		attrs.add( new Object[] {this.begin, getBegin()} );
		attrs.add( new Object[] {this.end, getEnd()} );
		attrs.add( new Object[] {this.eid, getEID()} );
		attrs.add( new Object[] {this.eiid, getEIID()} );
		attrs.add( new Object[] {this.eclass, getEClass()} );
		attrs.add( new Object[] {this.pos, getPOS()} );
		attrs.add( new Object[] {this.epos, getEPOS()} );
		attrs.add( new Object[] {this.tense, getTense()} );
		attrs.add( new Object[] {this.aspect, getAspect()} );
		attrs.add( new Object[] {this.polarity, getPolarity()} );
		attrs.add( new Object[] {this.modality, getModality()} );
		for (Object[] pair : attrs) {
			if (pair[0] != pair[1]) return false; }
		return true;
	}
	
}
