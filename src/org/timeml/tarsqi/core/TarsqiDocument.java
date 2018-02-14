package org.timeml.tarsqi.core;

import java.util.ArrayList;
import org.timeml.tarsqi.core.categories.ALink;
import org.timeml.tarsqi.core.categories.Event;
import org.timeml.tarsqi.core.categories.SLink;
import org.timeml.tarsqi.core.categories.TLink;
import org.timeml.tarsqi.core.categories.Timex;

public class TarsqiDocument {

	public String filename;
	public String text;
	public ArrayList<Event> events;
	public ArrayList<Timex> timexes;
	public ArrayList<ALink> alinks;
	public ArrayList<SLink> slinks;
	public ArrayList<TLink> tlinks;
	
	public TarsqiDocument(String filename) {
		this.filename = filename;
		this.events = new ArrayList<>();
		this.timexes = new ArrayList<>();
		this.alinks = new ArrayList<>();
		this.slinks = new ArrayList<>();
		this.tlinks = new ArrayList<>(); }

	public boolean isValid() {
		if (this.text == null) return false;
		return true; }
	
	public void addText(String text) { this.text = text; }
	public void addEvent(Event e) { this.events.add(e); }
	public void addTimex(Timex t) { this.timexes.add(t); }
	public void addALink(ALink l) { this.alinks.add(l); }
	public void addSLink(SLink l) { this.slinks.add(l); }
	public void addTLink(TLink l) { this.tlinks.add(l); }

	public void prettyPrint() {
		System.out.println(String.format("<TarsqiDocument %s>", this.filename));
		int c = 0;
		for (Event e : this.events) {
			if (c++ >= 10) break;
			System.out.println("   " + e); }
		for (Timex t : this.timexes)
			System.out.println("   " + t);
		for (ALink t : this.alinks) {
			System.out.println("   " + t); }
		for (SLink t : this.slinks) {
			System.out.println("   " + t); }
		c = 0;
		for (TLink t : this.tlinks) {
			if (c++ >= 10) break;
			System.out.println("   " + t); }
	}
}
