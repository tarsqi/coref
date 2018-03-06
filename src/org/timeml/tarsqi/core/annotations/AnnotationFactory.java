package org.timeml.tarsqi.core.annotations;

import org.timeml.tarsqi.definitions.TimeML;
import org.w3c.dom.Node;

/**
 * Class to help create instances of annotation categories. 
 *
 * This class is aware of the difference between the different kinds of annotations
 * and it will select the appropriate one by looking whether the tag name is any
 * one of the known tag names used in TimeML and TTK. The fallback position is to
 * use Annotation.
 */
public class AnnotationFactory {

	public static Annotation createAnnotation(Node n) {
		String tagName = n.getNodeName();
		if (tagName.equals(TimeML.EVENT))
			return new Event(n);
		else if (tagName.equals(TimeML.TIMEX3))
			return new Timex(n);
		else if (tagName.equals(TimeML.ALINK))
			return new ALink(n);
		else if (tagName.equals(TimeML.SLINK))
			return new SLink(n);
		else if (tagName.equals(TimeML.TLINK))
			return new TLink(n);
		else if (nodeIsOneOf(tagName, TimeML.ENTITY_NODES))
			// this is for the lex, ng, vg and s nodes
			return new TimemlAnnotation(n);
		else
			return new Annotation(n);
	}

	private static boolean nodeIsOneOf(String nodeName, String[] nodeNames) {
		// For small arrays, this is claimed to be more eficient than using a set
		for (String s : nodeNames) {
			if (s.equals(nodeName))
				return true;
		}
		return false;
	}
	
}
