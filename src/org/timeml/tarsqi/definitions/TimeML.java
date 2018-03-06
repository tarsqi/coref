package org.timeml.tarsqi.definitions;

/** 
 * Defines static final variables for all kinds of TimeML related information.
 * 
 * Includes names of categories and properties of those categories as well as
 * definitions of groups of categories. Technically, not all variables reflect
 * TimeML types, some of them are just used in TTK.
 */
public class TimeML {

	// Top-level tags
	
	public static final String EVENT = "EVENT";
	public static final String TIMEX3 = "TIMEX3";
	public static final String DOC_ELEMENT = "docelement";
	public static final String SENTENCE = "s";
	public static final String NG = "ng";
	public static final String VG = "vg";
	public static final String LEX = "lex";
	public static final String ALINK = "ALINK";
	public static final String SLINK = "SLINK";
	public static final String TLINK = "TLINK";
	
	public static final String[]
		ENTITY_NODES = {EVENT, TIMEX3, LEX, SENTENCE, NG, VG, DOC_ELEMENT};

	public static final String[] 
		LINK_NODES = {ALINK, SLINK, TLINK };

	// Tag properties
	
	public static final String BEGIN = "begin";
	public static final String END = "end";
	public static final String ORIGIN = "origin";

	public static final String TID = "tid";
	public static final String TYPE = "type";
	public static final String VALUE = "value";
	public static final String FUNCTION_IN_DOCUMENT = "functionInDocument";
	
	public static final String EID = "eid";
	public static final String EIID = "eiid";
	public static final String CLASS = "class";
	public static final String POS = "pos";
	public static final String EPOS = "epos";
	public static final String TENSE = "tense";
	public static final String ASPECT = "aspect";
	public static final String POLARITY = "polarity";
	public static final String MODALITY = "modality";

	public static final String LID = "lid";
	public static final String RELTYPE = "relType";
	public static final String TIME_ID = "timeID";
	public static final String RELATED_TO_TIME = "relatedToTime";
	public static final String EVENT_INSTANCE_ID = "eventInstanceID";
	public static final String RELATED_TO_EVENT_INSTANCE = "relatedToEventInstance";
	public static final String SUBORDINATED_EVENT_INSTANCE = "subordinatedEventInstance";
	public static final String SYNTAX = "syntax";

}
