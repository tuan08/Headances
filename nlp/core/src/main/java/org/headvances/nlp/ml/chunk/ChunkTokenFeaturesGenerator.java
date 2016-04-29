package org.headvances.nlp.ml.chunk;

import java.util.ArrayList;
import java.util.List;

import org.headvances.nlp.NLPResource;
import org.headvances.nlp.dict.Dictionary;
import org.headvances.nlp.dict.DictionaryTaggingAnalyzer;
import org.headvances.nlp.ml.token.TokenFeatures;
import org.headvances.nlp.ml.token.TokenFeaturesGenerator;
import org.headvances.nlp.token.TokenCollection;
import org.headvances.nlp.token.analyzer.CommonTokenAnalyzer;
import org.headvances.nlp.token.analyzer.TokenAnalyzer;
import org.headvances.nlp.wtag.WTagDocumentReader;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class ChunkTokenFeaturesGenerator extends TokenFeaturesGenerator {
	private WTagDocumentReader reader ;
	
	public ChunkTokenFeaturesGenerator() {
		add(new ChunkFeatureGenerator()) ;
    setTargetFeatureGenerator(new ChunkTargetFeatureGenerator()) ;
	}
	
	public TokenFeatures[] generate(String text) throws Exception {
		if(reader == null) {
			reader = new WTagDocumentReader() ;
			Dictionary dict = NLPResource.getInstance().getDictionary(Dictionary.DICT_RES) ;
			TokenAnalyzer[] tokenAnalyzers = new TokenAnalyzer[] {
				new CommonTokenAnalyzer(), new DictionaryTaggingAnalyzer(dict)
			};
			reader.setTokenAnalyzer(tokenAnalyzers) ;
		}
		List<TokenFeatures> holder = new ArrayList<TokenFeatures>() ;
		TokenCollection[] collections = reader.read(text) ;
		for(TokenCollection selCol : collections) {
			TokenFeatures[] tfeatures = generate(selCol.getTokens(), true) ;
			for(TokenFeatures sel : tfeatures) holder.add(sel) ;
		}
		return holder.toArray(new TokenFeatures[holder.size()]) ;
  }
}