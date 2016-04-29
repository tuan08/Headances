package org.headvances.nlp.ml.classify.text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.headvances.nlp.NGram;
import org.headvances.nlp.NLPResource;
import org.headvances.nlp.dict.Dictionary;
import org.headvances.nlp.dict.DictionaryTaggingAnalyzer;
import org.headvances.nlp.ml.classify.FeatureGenerator;
import org.headvances.nlp.ml.classify.FeatureHolder;
import org.headvances.nlp.query2.chunker.CurrencyChunker;
import org.headvances.nlp.query2.match.MatcherResourceFactory;
import org.headvances.nlp.token.IToken;
import org.headvances.nlp.token.TextSegmenter;
import org.headvances.nlp.token.analyzer.CommonTokenAnalyzer;
import org.headvances.nlp.token.analyzer.PunctuationTokenAnalyzer;
import org.headvances.nlp.token.analyzer.TokenAnalyzer;
import org.headvances.nlp.token.tag.MeaningTag;
import org.headvances.nlp.token.tag.WordTag;
import org.headvances.nlp.ws.NGramStatisticWSTokenAnalyzer;

public class TextFeatureGenerator implements FeatureGenerator<TextDocument> {
	final static String  TITLE_PREFIX = "txt" ;
	final static String  TEXT_PREFIX  = "txt" ;

	private boolean normalize = false ;
	private TextSegmenter textSegmenter ; 

	public TextFeatureGenerator() throws Exception{
		Dictionary dict = NLPResource.getInstance().getDictionary(Dictionary.DICT_RES);
		TokenAnalyzer[] textSegmenterAnalyzer = {
			new PunctuationTokenAnalyzer(), new CommonTokenAnalyzer(),
			new CurrencyChunker(new MatcherResourceFactory()),
			new NGramStatisticWSTokenAnalyzer(), 
			new DictionaryTaggingAnalyzer(dict)
		};
		this.textSegmenter = new TextSegmenter(textSegmenterAnalyzer) ;
	}

	public void setNormalize(boolean b) {
		this.normalize = b ;
	}

	public void generate(FeatureHolder holder, TextDocument doc) {
		add(holder, TITLE_PREFIX, doc.getTitle());
		add(holder, TEXT_PREFIX, doc.getContent());
	}

	private void add(FeatureHolder holder, String prefix, String text) {
		if(text == null) return;
		try {
			IToken[] token = textSegmenter.segment(text) ;
			for(IToken sel : token) {
				if(sel.getFirstTagType(MeaningTag.class) != null) {
					holder.add(prefix, sel.getNormalizeForm()) ;
					continue ;
				}  
				WordTag wtag = sel.getFirstTagType(WordTag.class) ;
				if(wtag == WordTag.WLETTER) {
					holder.add(prefix, sel.getNormalizeForm()) ;
					continue ;
				}
			}

			NGramFrequency ngramFreq = new NGramFrequency() ;
			NGram[] ngram = ngrams(token, 2);
			for(int i = 0; i < ngram.length; i++) {
				if(ngram[i].getNumberOfGram() > 1) {
					ngramFreq.addToken(ngram[i].getToken()) ;
				}
			}
			for(String selNGram : ngramFreq.getNGram(2)) {
				holder.add(prefix, selNGram) ;
			}
		} catch(Exception ex) {
			ex.printStackTrace() ;
		}
	}

	static public NGram[] ngrams(IToken[] token, int maxNGram) {
		List<NGram> holder = new ArrayList<NGram>() ;
		for(int i = 0; i < token.length; i++) {
			int limit = i + maxNGram;
			if(limit > token.length) limit = token.length ; 
			for(int j = i; j < limit; j++) {
				MeaningTag mtag = token[j].getFirstTagType(MeaningTag.class) ;
				if(mtag != null) {
					NGram ngram = new NGram(token, i, j + 1) ;
					holder.add(ngram) ;
				} else {
					break ;
				}
			}
		}
		return holder.toArray(new NGram[holder.size()]) ;
	}
	
	static public class NGramFrequency  extends HashMap<String, Integer>{
  	public int getTokenCount(String token) {
  		Integer count = get(token) ;
  		if(count == null) return 0 ; 
  		return count.intValue() ;
  	}

  	public void addToken(String token) {
  		Integer count = get(token) ;
  		if(count == null) put(token, 1) ;
  		else put(token, count.intValue() + 1) ;
  	}
  	
  	public String[] getTokens() {
  		return keySet().toArray(new String[size()]) ;
  	}
  	
  	public String[] getNGram(int minFreq) {
  		List<String> holder = new ArrayList<String>() ;
  		Iterator<Map.Entry<String, Integer>> i = entrySet().iterator() ;
  		while(i.hasNext()) {
  			Map.Entry<String, Integer> entry = i.next();
  			if(entry.getValue() >= minFreq) {
  				holder.add(entry.getKey()) ;
  			}
  		}
  		return holder.toArray(new String[holder.size()]) ;
  	}
  }
}