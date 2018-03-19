package org.timeml.tarsqi.core.annotations;

import edu.stanford.nlp.ling.Label;
import edu.stanford.nlp.trees.Tree;
import java.util.ArrayList;
import java.util.List;

/**
 * Class we can use for representing a tree structure including Stanford's
 * edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation.
 * 
 * This also suspiciously sounds like the TarsqiTree in the Python implementation,
 * so maybe it is better to rename this and maybe even not make it a sub class
 * of Annotation.
 */
public class TreeAnnotation extends Annotation {

	public TreeAnnotation parent;
	public List<TreeAnnotation> children;
	
	public TreeAnnotation(String type) {
		this.type = type;
		this.parent = null;
		this.children = new ArrayList<>();
	}
	
	public TreeAnnotation(Tree tree) {
		// So this seems to require that there is a public constructor on the
		// Annotation class with the same argument signature or one that has no
		// arguments
		Label label = tree.label();
		this.type = label.value();
		this.parent = null;
		this.children = new ArrayList<>();
	}

	/**
	 * Determine the size of the tree starting at this TreeAnnotation element.
	 * 
	 * @return The number of elements in the TreeAnnotation starting at this node.
	 */
	public int treeSize() {
		int count = 1;
		for (TreeAnnotation child : this.children) 
			count += child.treeSize();
		return count;
	}


	public void prettyPrint() {
		prettyPrint("");
	}

	private void prettyPrint(String indentation) {
		System.out.println(indentation + this);
		for (TreeAnnotation child : this.children) {
			child.prettyPrint(indentation + "  ");
		}
	}
}
