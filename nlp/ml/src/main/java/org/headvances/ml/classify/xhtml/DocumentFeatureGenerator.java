package org.headvances.ml.classify.xhtml;

import org.headvances.html.dom.TDocument;
import org.headvances.ml.classify.xhtml.feature.DOMFeatureGenerator;
import org.headvances.ml.classify.xhtml.feature.ShortTextFeatureGenerator;
import org.headvances.ml.classify.xhtml.feature.URLFeatureGenerator;
import org.headvances.ml.feature.FeatureGenerator;
import org.headvances.ml.feature.FeatureHolder;

public class DocumentFeatureGenerator implements FeatureGenerator<TDocument> {	
	private URLFeatureGenerator        urlFeatureGenerator ;
	private ShortTextFeatureGenerator  textFeatureGenerator ;
	private DOMFeatureGenerator        cssFeatureGenerator ;

	public DocumentFeatureGenerator() throws Exception {
		urlFeatureGenerator     = new URLFeatureGenerator() ;		
		textFeatureGenerator    = new ShortTextFeatureGenerator() ;
		cssFeatureGenerator     = new DOMFeatureGenerator() ;
	}

	public void generate(FeatureHolder holder, TDocument doc) {
		urlFeatureGenerator.generate(holder, doc) ;		
		textFeatureGenerator.generate(holder, doc) ;
		cssFeatureGenerator.generate(holder, doc) ;
	}
}