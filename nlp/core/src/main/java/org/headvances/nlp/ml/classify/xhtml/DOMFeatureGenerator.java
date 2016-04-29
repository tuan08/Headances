package org.headvances.nlp.ml.classify.xhtml;

import org.headvances.html.dom.TDocument;
import org.headvances.html.dom.TNode;
import org.headvances.html.dom.TNodeVisitor;
import org.headvances.nlp.ml.classify.FeatureGenerator;
import org.headvances.nlp.ml.classify.FeatureHolder;

public class DOMFeatureGenerator implements FeatureGenerator<TDocument> {
	final static String CSS_PREFIX   = "dom:css" ;
	final static String DOM_ID       = "dom:id" ;

	private PatternFeatureGenerator patternGenerator = new PatternFeatureGenerator() ;

	public void generate(final FeatureHolder holder, TDocument doc) {
		TNodeVisitor visitor = new TNodeVisitor() {
			public int onVisit(TNode node) {
				patternGenerator.generate(holder, CSS_PREFIX, node.getCssClass()) ;
				patternGenerator.generate(holder, DOM_ID, node.getElementId()) ;
				return CONTINUE ;
			}
		};
		doc.getRoot().visit(visitor) ;
	}
}