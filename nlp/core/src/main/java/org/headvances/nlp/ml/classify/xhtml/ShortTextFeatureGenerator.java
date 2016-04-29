package org.headvances.nlp.ml.classify.xhtml;

import org.headvances.html.dom.TDocument;
import org.headvances.html.dom.TNode;
import org.headvances.html.dom.TNodeVisitor;
import org.headvances.nlp.ml.classify.FeatureGenerator;
import org.headvances.nlp.ml.classify.FeatureHolder;
import org.headvances.nlp.ml.classify.FeatureUtil;
import org.headvances.nlp.token.IToken;
import org.headvances.nlp.token.TextSegmenter;
import org.headvances.nlp.token.analyzer.CommonTokenAnalyzer;
import org.headvances.nlp.token.analyzer.GroupTokenMergerAnalyzer;
import org.headvances.nlp.token.analyzer.PunctuationTokenAnalyzer;
import org.headvances.nlp.token.analyzer.TokenAnalyzer;
import org.headvances.nlp.token.analyzer.USDTokenAnalyzer;
import org.headvances.nlp.token.analyzer.VNDTokenAnalyzer;
import org.headvances.nlp.token.tag.CurrencyTag;
import org.headvances.nlp.token.tag.MeaningTag;

public class ShortTextFeatureGenerator implements FeatureGenerator<TDocument> {
	final static String TEXT_PREFIX   = "text" ;
	final static String LINK_PREFIX   = "link" ;
	
	private TextSegmenter textSegmenter ;	
	
	public ShortTextFeatureGenerator() throws Exception {
		TokenAnalyzer[] textSegmenterAnalyzer = {
			new PunctuationTokenAnalyzer(), new CommonTokenAnalyzer(),
			new GroupTokenMergerAnalyzer(),
			new VNDTokenAnalyzer(), new USDTokenAnalyzer(), 
		};
		this.textSegmenter = new TextSegmenter(textSegmenterAnalyzer) ;
	}

	public void generate(final FeatureHolder holder, TDocument doc) {
		TNodeVisitor visitor = new TNodeVisitor() {
			public int onVisit(TNode node) {
				String text = node.getNodeValue();
				if (text != null) {
					if (text.length() > 2 && text.length() < 16) {
						TNode ancestor = node.getAncestorByNodeName("a");
						if(ancestor == null) {
							add(holder, TEXT_PREFIX, text) ;
						} else {              
							add(holder, LINK_PREFIX, text) ;
						}            
					}  
				}
				return CONTINUE;
			}
		};
		doc.getRoot().visit(visitor);
	}

	private void add(FeatureHolder holder, String prefix, String text) {
		try {
			IToken[] token = textSegmenter.segment(text) ;
			boolean meaning = false ;
			for(IToken sel : token) {
				MeaningTag mtag = sel.getFirstTagType(MeaningTag.class) ;
				if(mtag != null) {
					meaning = true ;
					holder.add(prefix, sel.getNormalizeForm()) ;
					continue ;
				}
				CurrencyTag ctag = sel.getFirstTagType(CurrencyTag.class) ;
				if(ctag != null) {
					holder.add(prefix, ctag.getOType()) ;
					continue ;
				}
			}
			if(meaning) holder.add(prefix, FeatureUtil.normalize(text)) ;
		} catch(Exception ex) {
			ex.printStackTrace() ;
		}
	}
}