package org.timeml.tarsqi.tools.stanford;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.IndexedWord;
//import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.timeml.tarsqi.core.annotations.Annotation;
import org.timeml.tarsqi.core.annotations.TreeAnnotation;
import static org.timeml.tarsqi.definitions.TimeML.LEX;
import static org.timeml.tarsqi.definitions.TimeML.SENTENCE;

/**
 * A class to make it easier to return a variety of Stanford NLP results. Now
 * used because we want to be able to both return an Annotation and a Tree
 * objects from functions that apply Stanford components.
 */
public class StanfordResult {

	/**
	 * The Annotation object that is the result of pipeline processing
	 */
	public edu.stanford.nlp.pipeline.Annotation annotation = null;

	/**
	 * The Tree object that is the created by the ShiftReduce parser
	 */
	// TODO: should be a list
	public Tree tree = null;

	StanfordResult(edu.stanford.nlp.pipeline.Annotation annotation, Tree tree) {
		this.annotation = annotation;
		this.tree = tree;
	}

	StanfordResult(edu.stanford.nlp.pipeline.Annotation annotation) {
		this.annotation = annotation;
	}

	public ArrayList<ArrayList<Annotation>> getResultsAsTagList() {

		// TODO: make sure that identifiers are unique. Now the code in
		// addSentenceTags() and addConstituentTags() is independents from each
		// other and duplicates identifiers. The easiest way would be to create
		// an IdentifierFactory at this level (but perhaps have one for each
		// sentenceand hand it over to the subroutines.

		// NOTE: there is some redundancy in that the leaves in the constituent
		// tree are most likely identical to the tokens in the sentence. This
		// redundancy should be removed or otherwise all consuming code should
		// be aware of it.

		ArrayList<ArrayList<Annotation>> tags = new ArrayList<>();

		//ArrayList<Annotation> tags = new ArrayList<>();
		List<CoreMap> sentences = this.annotation.get(CoreAnnotations.SentencesAnnotation.class);
		for (int i = 0; i < sentences.size(); i++) {
			ArrayList<Annotation> sentenceTags = new ArrayList<>();
			tags.add(sentenceTags);
			CoreMap sentence = sentences.get(i);
			//System.out.println("\n==> " + sentence + "\n");
			addSentenceTags(sentence, i, sentenceTags);
			//for (org.timeml.tarsqi.core.annotations.Annotation tag : sentenceTags)
			//	System.out.println(tag);
			addConstituentTags(sentence, i, sentenceTags);
			addDependencyTags(sentence, i, sentenceTags);
			//System.out.println();
		}
		return tags;
	}

	/**
	 * Export sentence and token information from the sentence to the tags list.
	 *
	 * Add the sentence as a tag to the tags list and add all tokens in the
	 * sentence as tags to the tags list.
	 *
	 * @param sentence The CoreMap to export tags from.
	 * @param tags The list of Annotations to export to.
	 */
	private void addSentenceTags(
			CoreMap sentence,
			int sid,
			ArrayList<Annotation> tags) {

		// We are adding the tags as instances of Annotation and not instances of
		// TimemlAnnotation, we could consider changing that.
		Annotation tag;
		int p1 = -1;
		int p2 = -1;
		int tid = 0;
		for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
			StanfordToken tok = new StanfordToken(token);
			tag = new Annotation(token, LEX);
			tag.addAttribute("tid", String.format("t%d_%d", sid, tid));
			//System.out.println(tag);
			tags.add(tag);
			tid++;
			if (p1 == -1 || token.beginPosition() < p1)
				p1 = token.beginPosition();
			if (p2 == -1 || token.endPosition() > p2)
				p2 = token.endPosition();
		}
		tag = new Annotation(SENTENCE, p1, p2);
		tag.addAttribute("sid", String.format("s%d", sid));
		//System.out.println(tag);
		tags.add(new Annotation(SENTENCE, p1, p2));
	}

	private void addConstituentTags(
			CoreMap sentence,
			int sid,
			ArrayList<Annotation> tags) {

		Tree root = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
		if (root != null) {
			System.out.println("\n" + root.pennString());
			IdentifierFactory identifiers = new IdentifierFactory();
			TreeAnnotation treeAnnotation = addConstituentTag(tags, root, null, sid, identifiers);
			treeAnnotation.prettyPrint();
		}
	}

	private TreeAnnotation addConstituentTag(
			ArrayList<Annotation> tags,
			Tree tree, TreeAnnotation parent,
			int sid,
			IdentifierFactory identifiers) {

		TreeAnnotation ta = new TreeAnnotation(tree);
		ta.parent = parent;
		if (parent != null)
			parent.children.add(ta);
		CoreLabel label = (CoreLabel) tree.label();

		if (tree.isLeaf()) {
			ta.type = "leaf";
			ta.begin = label.beginPosition();
			ta.end = label.endPosition();
			ta.addAttribute("tid", String.format("t%d_%d", sid, identifiers.newTID()));
			ta.addAttribute("index", label.index());
			ta.addAttribute("word", label.word());
			ta.addAttribute("lemma", label.lemma());
			ta.addAttribute("pos", label.tag());
		} else {
			ta.addAttribute("cid", String.format("c%d_%d", sid, identifiers.newCID()));
		}

		tags.add(ta);

		for (Tree child : tree.getChildrenAsList())
			addConstituentTag(tags, child, ta, sid, identifiers);

		return ta;
	}

	private void addDependencyTags(CoreMap sentence, int i, ArrayList<Annotation> sentenceTags) {
		// you get these even when you simply run the parse annotator
		SemanticGraph dependencies = sentence.get(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class);
		if (dependencies != null) {
			System.out.println("\n" + dependencies);
			IndexedWord root = dependencies.getFirstRoot();
			//writer.addRoot(new StanfordToken(root));
			for (SemanticGraphEdge edge : dependencies.edgeListSorted()) {
				break;
			}
			//writer.addDependency(new StanfordDependency(edge));
			Set<IndexedWord> leaves = dependencies.getLeafVertices();
			for (IndexedWord leaf : leaves) {
				//writer.addPath(
				//	leaf,
				//	dependencies.getPathToRoot(leaf),
				//	dependencies.getShortestUndirectedPathEdges(leaf, root));
			}
			//for (org.timeml.tarsqi.core.annotations.Annotation tag : tags)
			//System.out.println(tag);
		}
	}
}


	class Pair {

	Tree tree;
	Annotation parent;
	TreeAnnotation parentAnnotation;

	Pair(Tree tree, Annotation parent, TreeAnnotation parentAnnotation) {
		this.tree = tree;
		this.parent = parent;
		this.parentAnnotation = parentAnnotation;
	}
}

class IdentifierFactory {

	int cid;
	int tid;

	public IdentifierFactory() {
		this.cid = -1;
		this.tid = -1;
	}

	void reset() {
		this.cid = -1;
		this.tid = -1;
	}

	int newCID() {
		this.cid++;
		return this.cid;
	}

	int newTID() {
		this.tid++;
		return this.tid;
	}
}
