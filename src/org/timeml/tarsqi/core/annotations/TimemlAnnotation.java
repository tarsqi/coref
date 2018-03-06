package org.timeml.tarsqi.core.annotations;

import static org.timeml.tarsqi.definitions.TimeML.*;
import org.w3c.dom.Node;

/**
 * Class that defines the fields used by TimeML tags and that contains some
 * common methods.
 * 
 * What distinguishes this class and all its subclasses from the Annotation class
 * is that some of the attributes from the attributes map are copied to top-level
 * public instance variables with the correct type. Note that all values in the
 * attributes map are Strings so for all attributes that are exposed we need to
 * write methods that know what types are required. All subclasses should define 
 * a generateAttributes() method that calls get methods for individual attributes
 * as appropriate and assign the return values to the corresponding fields.
 * 
 * TimeML attributes are defined in this class as well a bunch of attributes that
 * are not TimeML attributes, but that are attributes added by Tarsqi. It is not
 * necessary for all these attributes to be defined, we could have some of them
 * just in the attributes map, but in general we choose to have them and their
 * getters for easy access.
 */
public class TimemlAnnotation extends Annotation {
	
	public int begin, end;
	public String origin;
	
	public TimemlAnnotation(Node node) {
		super(node);
	}

	// for all TimeML tags
	
	protected int getBegin() {
		return getIntegerValue(BEGIN); }

	protected int getEnd() {
		return getIntegerValue(END); }

	protected String getOrigin() {
		return getStringValue(ORIGIN, "NONE"); }

	// TIMEX3 tags
	
	protected String getTID() { 
		return getStringValue(TID, "NONE"); }
	
	protected String getType() { 
		return getStringValue(TYPE, "NONE"); }
	
	protected String getValue() { 
		return getStringValue(VALUE, "NONE"); }

	protected String getFunctionInDocument() { 
		return getStringValue(FUNCTION_IN_DOCUMENT, "NONE"); }
	
	// EVENT tags
	
	protected String getEID() {
		return getStringValue(EID, "NONE"); }

	protected String getEIID() {
		return getStringValue(EIID, "NONE"); }

	protected String getEClass() {
		// Note how this one has an irregular name because we cannot use
		// getCLass(); similarly, the field on the instance is eclass instead
		// of class
		return getStringValue(CLASS, "OCCURRENCE"); }

	protected String getPOS() {
		return getStringValue(POS, "NONE"); }

	protected String getEPOS() {
		return getStringValue(EPOS, "NONE"); }

	protected String getPolarity() {
		return getStringValue(POLARITY, "POS"); }

	protected String getModality() {
		return getStringValue(MODALITY, "NONE"); }

	protected String getTense() {
		return getStringValue(TENSE, "NONE"); }

	protected String getAspect() {
		return getStringValue(ASPECT, "NONE"); }

	// ALINK, SLINK and TLINK tags
	
	protected String getLID() {
		return getStringValue(LID, "NONE"); }

	protected String getRelType() {
		return getStringValue(RELTYPE, "NONE"); }

	protected String getSyntax() {
		return getStringValue(SYNTAX, "NONE"); }

	protected String getEventInstanceID() {
		return getStringValue(EVENT_INSTANCE_ID, "NONE"); }

	protected String getRelatedToEventInstance() {
		return getStringValue(RELATED_TO_EVENT_INSTANCE, "NONE"); }

	protected String getSubordinatedEventInstance() {
		return getStringValue(SUBORDINATED_EVENT_INSTANCE, "NONE"); }

	protected String getTimeID() {
		return getStringValue(TIME_ID, "NONE"); }

	protected String getRelatedToTime() {
		return getStringValue(RELATED_TO_TIME, "NONE"); }
	
	/**
	 * Return the value of the attribute as a string, using the default value
	 * given if the attributes map does not contain the attribute.
	 */
	private String getStringValue(String attr, String defaultValue) {
		return (String) this.attributes.getOrDefault(attr, defaultValue); }

	/**
	 * Return the value of the attribute as an integer.
	 */
	private int getIntegerValue(String attr) throws NumberFormatException {
		// note that this fails if the annotation does not have the attribute;
		// an alternative would be to use "-1" as a default, but that would
		// only work for begin and end and not for other attributes; another
		// option might be for this method to return null for those cases
		return Integer.parseInt(this.attributes.get(attr)); }

}
