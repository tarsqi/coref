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
	public ArrayList<AnnotationLayer> layers;
	
	public TarsqiDocument(String filename) {
		this.filename = filename;
		this.events = new ArrayList<>();
		this.timexes = new ArrayList<>();
		this.alinks = new ArrayList<>();
		this.slinks = new ArrayList<>();
		this.tlinks = new ArrayList<>();
		this.layers = new ArrayList<>(); }
	
	public boolean isValid() {
		return this.text != null; }
	
	public void addText(String text) { this.text = text; }
	public void addEvent(Event e) { this.events.add(e); }
	public void addTimex(Timex t) { this.timexes.add(t); }
	public void addALink(ALink l) { this.alinks.add(l); }
	public void addSLink(SLink l) { this.slinks.add(l); }
	public void addTLink(TLink l) { this.tlinks.add(l); }

	public void prettyPrint() {
		System.out.println(String.format("<TarsqiDocument '%s'>", this.filename));
		int max = 3;
		for (int i = 0 ; i < this.events.size() && i < max ; i++)
			System.out.println("   " + this.events.get(i));
		for (int i = 0 ; i < this.timexes.size() && i < max ; i++)
			System.out.println("   " + this.timexes.get(i));
		for (int i = 0 ; i < this.alinks.size() && i < max ; i++)
			System.out.println("   " + this.alinks.get(i));
		for (int i = 0 ; i < this.slinks.size() && i < max ; i++)
			System.out.println("   " + this.slinks.get(i));
		for (int i = 0 ; i < this.tlinks.size() && i < max ; i++)
			System.out.println("   " + this.tlinks.get(i));
	}
}
