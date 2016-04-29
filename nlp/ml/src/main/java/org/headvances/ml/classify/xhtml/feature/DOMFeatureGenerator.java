package org.headvances.ml.classify.xhtml.feature;

import org.headvances.html.dom.TDocument;
import org.headvances.html.dom.TNode;
import org.headvances.html.dom.TNodeVisitor;
import org.headvances.ml.feature.FeatureGenerator;
import org.headvances.ml.feature.FeatureHolder;

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