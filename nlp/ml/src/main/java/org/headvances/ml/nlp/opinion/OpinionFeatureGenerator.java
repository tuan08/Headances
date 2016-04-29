package org.headvances.ml.nlp.opinion;

import java.util.List;

import org.headvances.ml.feature.FeatureGenerator;
import org.headvances.ml.feature.FeatureHolder;
import org.headvances.nlp.dict.WordTreeDictionary;
import org.headvances.nlp.query2.match.RuleMatch;
import org.headvances.nlp.token.IToken;
import org.headvances.nlp.token.tag.CurrencyTag;
import org.headvances.nlp.token.tag.DigitTag;
import org.headvances.nlp.token.tag.NumberTag;
import org.headvances.nlp.token.tag.PhoneTag;
import org.headvances.nlp.token.tag.TokenTag;
import org.headvances.nlp.ws.WordTreeMatchingAnalyzer;
import org.headvances.util.text.StringUtil;

public class OpinionFeatureGenerator implements FeatureGenerator<Opinion> {
	final static int SURROUND_DIST = 9 ;
	
  private String[] keyword ;
	private WordTreeMatchingAnalyzer wordAnalyzer ; 
	
  public OpinionFeatureGenerator(String[] keyword, WordTreeDictionary dict) {
  	this.keyword = new String[keyword.length];
  	for(int i = 0; i < keyword.length; i++) {
  	  this.keyword[i] = keyword[i].toLowerCase() ;
  	}
  	
  	dict.add(keyword) ;
  	wordAnalyzer = new WordTreeMatchingAnalyzer(dict.getWordTree()) ;
  }
  
	public void generate(FeatureHolder holder, Opinion opinion) {
		RuleMatch rmatch = opinion.getRuleMatch() ;
  	
  	IToken[] tokens = rmatch.getTokenCollection().getTokens() ;
  	tokens = wordAnalyzer.analyze(tokens) ;
  	
  	int keywordPos = -1 ;
  	for(int i = 0; i < tokens.length; i++) {
  		if(StringUtil.isIn(tokens[i].getNormalizeForm(), keyword)) {
  			keywordPos = i ;
  			break ;
  		}
  	}
  	if(keywordPos < 0) return ;
  	
  	int start = keywordPos - SURROUND_DIST ;
  	if(start < 0) start = 0 ;
  	int stop = keywordPos  + SURROUND_DIST ;
  	if(stop > tokens.length) stop = tokens.length ;
  	for(int i = start; i < stop; i++) {
  		if(i != keywordPos) {
    		String dist = "far" ;
    		int absDist = Math.abs(i - keywordPos) ;
    		if(absDist < 5) dist = "close" ;
  			if(i < keywordPos) {
  				String prefix = "bf:" + dist ;
  				addTokenFeature(holder, prefix, tokens[i]);
  				//addOpinionSynset(holder, prefix, tokens[i]) ;
  			} else {
  				String prefix = "af:" + dist ;
  				addTokenFeature(holder, prefix, tokens[i]);
  				//addOpinionSynset(holder, prefix, tokens[i]) ;
  			} 
  		} 
  	}
  	addLength(holder, tokens) ;
	}
	
	private void addTokenFeature(FeatureHolder holder, String prefix, IToken token) {
		List<TokenTag> tags = token.getTag() ;
		if(tags != null) {
			for(int i = 0; i < tags.size(); i++) {
				TokenTag tag = tags.get(i) ;
				if(tag instanceof CurrencyTag) {
					holder.add(prefix, "TAG:CURRENCY") ; return ;
				} 
				if(tag instanceof PhoneTag) {
					holder.add(prefix, "TAG:PHONE") ; return ;
				}
				if(tag instanceof DigitTag) {
					holder.add(prefix, "TAG:DIGIT") ; return ;
				}
				if(tag instanceof NumberTag) {
					holder.add(prefix, "TAG:NUMBER") ; return ;
				}
			}
		}
		String feature = token.getNormalizeForm() ;
		feature = feature.replace(' ', '+') ;
		holder.add(prefix, feature) ; 
	}
	
	private void addLength(FeatureHolder holder, IToken[] token) {
		if(token.length < 8) {
			holder.add("sentence", "short") ;
		} else if(token.length < 15) {
			holder.add("sentence", "medium") ;
		} else {
			holder.add("sentence", "long") ;
		}
	}
}