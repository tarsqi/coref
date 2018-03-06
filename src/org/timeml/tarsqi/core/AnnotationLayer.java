package org.timeml.tarsqi.core;

import java.util.ArrayList;
import java.util.List;
import org.timeml.tarsqi.core.annotations.Annotation;

public class AnnotationLayer {
	
	String source;
	List<Annotation> annotations;
	
	public AnnotationLayer(String source) {
		this.source = source;
		this.annotations = new ArrayList<>();
	}
	
	public List<Annotation> getAnnotations() {
		return this.annotations;
	}
	
	public void addAnnotation(Annotation annotation) {
		this.annotations.add(annotation);
	}
	
	@Override
	public String toString() {
		return String.format(
				"<AnnotationLayer %s with %d tags>",
				this.source,
				this.annotations.size());
	}
}
