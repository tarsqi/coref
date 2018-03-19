package org.timeml.tarsqi.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.timeml.tarsqi.components.Sectioner;
import org.timeml.tarsqi.core.annotations.ALink;
import org.timeml.tarsqi.core.annotations.Annotation;
import org.timeml.tarsqi.core.annotations.Event;
import org.timeml.tarsqi.core.annotations.SLink;
import org.timeml.tarsqi.core.annotations.TLink;
import org.timeml.tarsqi.core.annotations.Timex;
import org.timeml.tarsqi.core.annotations.TreeAnnotation;
import static org.timeml.tarsqi.definitions.Components.SECTIONER;
import static org.timeml.tarsqi.definitions.Components.STANFORD_TAGGER;
import org.timeml.tarsqi.tools.stanford.StanfordNLP;
import org.timeml.tarsqi.tools.stanford.StanfordResult;

public class TarsqiDocument {

	public String filename;
	public String text;
	public ArrayList<Event> events;
	public ArrayList<Timex> timexes;
	public ArrayList<ALink> alinks;
	public ArrayList<SLink> slinks;
	public ArrayList<TLink> tlinks;
	public ArrayList<AnnotationLayer> layers;
	public Map<String, AnnotationLayer> layerIdx;

	public TarsqiDocument(String filename) {
		this.filename = filename;
		this.events = new ArrayList<>();
		this.timexes = new ArrayList<>();
		this.alinks = new ArrayList<>();
		this.slinks = new ArrayList<>();
		this.tlinks = new ArrayList<>();
		this.layers = new ArrayList<>();
		this.layerIdx = new HashMap<>();
	}

	public AnnotationLayer getTarsqiLayer() {
		return this.layerIdx.get("TARSQI_TAGS");
	}

	public AnnotationLayer getLayer(String name) {
		return this.layerIdx.get(name);
	}

	public boolean isValid() {
		return this.text != null; }

	public void setText(String text) { this.text = text; }
	public void addEvent(Event e) { this.events.add(e); }
	public void addTimex(Timex t) { this.timexes.add(t); }
	public void addALink(ALink l) { this.alinks.add(l); }
	public void addSLink(SLink l) { this.slinks.add(l); }
	public void addTLink(TLink l) { this.tlinks.add(l); }

	public void addLayer(AnnotationLayer layer) {
		this.layers.add(layer);
		this.layerIdx.put(layer.name, layer);
	}

	/**
	 * Take all Tarsqi tags and put them in the easy access lists on the top level.
	 */
	public void promoteTarsqiTags() {
		for (Annotation annotation : getTarsqiLayer().getAnnotations()) {
			if (annotation.isEvent())
				this.events.add((Event) annotation);
			else if (annotation.isTimex())
				this.timexes.add((Timex) annotation);
			else if (annotation.isALink())
				this.alinks.add((ALink) annotation);
			else if (annotation.isSLink())
				this.slinks.add((SLink) annotation);
			else if (annotation.isTLink())
				this.tlinks.add((TLink) annotation);

		}
	}

	public void prettyPrint() {
		System.out.println(String.format("<TarsqiDocument '%s'>", this.filename));
		int max = 3;
		for (AnnotationLayer layer : this.layers) {
			System.out.println("   " + layer); }
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

	/**
	 * Run the sectioner on the TarsqiDocument. The end result is that a new
	 * layer with sections is added to the TarsqiDocument.
	 */
	public void runSectioner() {
		Sectioner sectioner = new Sectioner(this);
		sectioner.parse();
		sectioner.prettyPrint();
		// this exports the sections from the sectioner to a layer on the
		// TarsqiDocument stored on the sectioner
		sectioner.exportSections();
	}

	/**
	 * Run the Stanford splitter, tokenizer and tagger. Data are added to a new
	 * annotation layer for the Stanford results and exported from that layer to
	 * the TARSQI_TAGS layer.
	 */
	public void runTagger() {
		// TODO: maybe push some of this functionailty to a Tagger class
		StanfordNLP snlp = new StanfordNLP("");
		// TODO: use the sectioner results
		StanfordResult result = snlp.processString(this.text);
		AnnotationLayer layer = new AnnotationLayer(STANFORD_TAGGER, "flat");
		this.addLayer(layer);
		for (ArrayList<Annotation> sentence_tags : result.getResultsAsTagList()) {
			for (Annotation tag : sentence_tags)
				layer.addAnnotation(tag);
		}
		// TODO: export to TARSQI_TAGS layer
	}

	/**
	 * Run the chunker.
	 */
	public void runChunker() {
	}

}
