package org.timeml.tarsqi.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.timeml.tarsqi.core.categories.Category;

class AnnotationLayer {
	
	Map metadata;
	List<Category> annotations;
	
	AnnotationLayer() {
		this.metadata = new HashMap<>();
		this.annotations = new ArrayList<>();
	}
}
