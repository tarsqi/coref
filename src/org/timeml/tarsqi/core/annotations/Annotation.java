package org.timeml.tarsqi.core.annotations;

import edu.stanford.nlp.ling.CoreLabel;
import java.util.HashMap;
import java.util.Map;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;


/**
 * Base class for all annotations.
 *
 * Gives direct access to type, begin and end fields as well as a HashMap for
 * all other attributes. The HashMap contains String values only. Subclasses can
 * use specialized feature access through other fields with the proper types.
 */

public class Annotation {

	/**
	 * The annotation type. Could be one of the TimeML elements like EVENT or
	 * TLINK or any other type.
	 */
	public String type;

	/**
	 * The identifier for the annotation. This is not used yet, but is mostly
	 * intended to ensure uniqueness for annotations. It is not the same as an
	 * identifier that may come with some tag or annotation, like the eid of
	 * events, those identifiers will be stored in the attributes map.
	 */
	public String id;

	/**
	 * Starting position of the annotation. Can be -1 if the annotation does
	 * not have a span.
	 */
	public int begin;

	/**
	 * Ending position of the annotation. Can be -1 if the annotation does
	 * not have a span.
	 */
	public int end;

	/**
	 * The DOM Node the annotation was created from, if any.
	 */
	protected Node dom;

	/**
	 * The attributes or features of the annotation. The keys and values in the
	 * map are always Strings.
	 */
	protected Map<String, Object> attributes;

	/**
	 * Create an Annotation from an XML Node.
	 *
	 * Stores the Node in the dom instance variable, sets the type instance
	 * variable to the annotation type (event, tlink, docelement, etcetera)
	 * and stores all node attributes in a HashMap.
	 *
	 * @param node The XML Node the annotation is to be created from
	 */
	public Annotation(Node node) {
		this.dom = node;
		this.type = node.getNodeName();
		this.begin = -1;
		this.end = -1;
		this.attributes = new HashMap<>();
		NamedNodeMap attrs = node.getAttributes();
		for (int j = 0; j < attrs.getLength(); j++) {
			String x = attrs.item(j).getNodeName();
			String y = attrs.item(j).getNodeValue();
			this.attributes.put(x, y);
		}
	}

	public Annotation() {
		this.dom = null;
		this.type = null;
		this.begin = -1;
		this.end = -1;
		this.attributes = new HashMap<>();
	}

	/**
	 * Create an Annotation from an XML Node and begin and end positions.
	 *
	 * Stores the Node in the dom instance variable, sets the type instance
	 * variable to the annotation type (event, tlink, docelement, etcetera),
	 * sets the begin and end position and stores all node attributes in a
	 * HashMap.
	 *
	 * @param node The XML Node the annotation is to be created from
	 * @param begin
	 * @param end
	 */
	public Annotation(Node node, int begin, int end) {
		this.dom = node;
		this.type = node.getNodeName();
		this.begin = begin;
		this.end = end;
		this.attributes = new HashMap<>();
		NamedNodeMap attrs = node.getAttributes();
		for (int j = 0; j < attrs.getLength(); j++) {
			String x = attrs.item(j).getNodeName();
			String y = attrs.item(j).getNodeValue();
			this.attributes.put(x, y);
		}
	}

	/**
	 * Create an Annotation from a type and begin and end positions.
	 *
	 * Sets the type instance to the annotation type given, sets the begin and
	 * end position.
	 *
	 * @param type
	 * @param begin
	 * @param end
	 */
	public Annotation(String type, int begin, int end) {
		this.dom = null;
		this.type = type;
		this.begin = begin;
		this.end = end;
		this.attributes = new HashMap<>();
	}

	/**
	 * Create an Annotation from a Stanford CoreLabel and a type name.
	 *
	 * The "word", "lemma" and "pos" attributes are all String valued, but the
	 * "index" is an integer (referring to the position in the parent).
	 *
	 * @param clabel The CoreLabel that Annotation is to be created from
	 * @param type The type name for the annotation
	 */
	public Annotation(CoreLabel clabel, String type) {
		this.dom = null;
		this.type = type;
		this.begin = clabel.beginPosition();
		this.end = clabel.endPosition();
		this.attributes = new HashMap<>();
		this.attributes.put("index", String.format("%d", clabel.index()));
		this.attributes.put("word", clabel.word());
		this.attributes.put("lemma", clabel.lemma());
		this.attributes.put("pos", clabel.tag());
	}

	/*
	public Annotation(Label label, String type) {
		this.dom = null;
		this.type = type;
		this.begin = -1;
		this.end = -1;
		this.attributes = new HashMap<>();
	}
	*/


	/**
	 * Return the value of a key in the attributes map.
	 *
	 * @param attr The attribute to find
	 * @return Object or null
	 */
	public Object getAttribute(String attr) {
		return this.attributes.get(attr); }

	public void addAttribute(String attr, Object value) {
		this.attributes.put(attr, value); }

	public boolean isEvent() { return false; }

	public boolean isTimex() { return false; }

	public boolean isALink() { return false; }

	public boolean isSLink() { return false; }

	public boolean isTLink() { return false; }

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("<").append(this.type);
		if (this.begin > -1)
			sb.append(String.format(" begin=%d end=%d", this.begin, this.end));
		for (String key : this.attributes.keySet()) {
			//String val = this.attributes.get(key);
			Object val = this.attributes.get(key);
			sb.append(String.format(" %s=%s", key, val)); }
		sb.append(">");
		return sb.toString();
	}

	public int treeSize() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
}
