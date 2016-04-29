package org.headvances.ml.classify.text;

import java.util.ArrayList;
import java.util.List;

import org.headvances.ml.feature.Feature;
import org.headvances.ml.feature.FeatureGenerator;
import org.headvances.ml.feature.FeatureHolder;
import org.headvances.ml.feature.GenerateFeature;
import org.headvances.ml.nlp.ent.CurrencyTag;
import org.headvances.nlp.NGram;
import org.headvances.nlp.NLPResource;
import org.headvances.nlp.dict.Dictionary;
import org.headvances.nlp.dict.DictionaryTaggingAnalyzer;
import org.headvances.nlp.token.IToken;
import org.headvances.nlp.token.TextSegmenter;
import org.headvances.nlp.token.analyzer.CommonTokenAnalyzer;
import org.headvances.nlp.token.analyzer.GroupTokenMergerAnalyzer;
import org.headvances.nlp.token.analyzer.PunctuationTokenAnalyzer;
import org.headvances.nlp.token.analyzer.TokenAnalyzer;
import org.headvances.nlp.token.analyzer.USDTokenAnalyzer;
import org.headvances.nlp.token.analyzer.VNDTokenAnalyzer;
import org.headvances.nlp.token.tag.DigitTag;
import org.headvances.nlp.token.tag.MeaningTag;
import org.headvances.nlp.token.tag.NumberTag;
import org.headvances.nlp.token.tag.PhoneTag;
import org.headvances.nlp.token.tag.WordTag;
import org.headvances.nlp.ws.NGramStatisticWSTokenAnalyzer;
import org.headvances.nlp.ws.WordTreeMatchingAnalyzer;

public class TextFeatureGenerator implements FeatureGenerator<TextDocument> {
  final static String  TITLE_PREFIX = "txt" ;
  final static String  TEXT_PREFIX  = "txt" ;
  static String[] IGNORE_TYPE  = { 
  	CurrencyTag.TYPE, DigitTag.TYPE, NumberTag.TYPE, PhoneTag.TYPE 
  
  } ;
  
  private boolean normalize = false ;
  private TextSegmenter textSegmenter ; 

  public TextFeatureGenerator() throws Exception{
    Dictionary dict = NLPResource.getInstance().getDictionary(Dictionary.DICT_RES);
    TokenAnalyzer dictTagger = new DictionaryTaggingAnalyzer(dict) ;

    //TokenAnalyzer posAnalyzer = new MalletCRFTokenAnalyzer() ;
    //TokenAnalyzer npSuggestAnalyzer =  new EntitySuggestTokenAnalyzer(new NPEntityTokenAnalyzer());
    //TokenAnalyzer numSuggestAnalyzer = new EntitySuggestTokenAnalyzer(new NumEntityTokenAnalyzer());

    TokenAnalyzer[] textSegmenterAnalyzer = {
        new PunctuationTokenAnalyzer(), new CommonTokenAnalyzer(),
        new GroupTokenMergerAnalyzer(),
        new VNDTokenAnalyzer(), new USDTokenAnalyzer(), 
        //new WordTreeMatchingAnalyzer(dict),
        new NGramStatisticWSTokenAnalyzer(), 
        dictTagger,
        //new UnknownWordTokenSplitter(dict)
        //posAnalyzer,s
        //npSuggestAnalyzer, numSuggestAnalyzer
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
  				holder.add(prefix, normalize(sel.getNormalizeForm())) ;
  				continue ;
  			}  
  			WordTag wtag = sel.getFirstTagType(WordTag.class) ;
  			if(wtag == WordTag.WLETTER) {
  				holder.add(prefix, normalize(sel.getNormalizeForm())) ;
  				continue ;
  			}
  		}
  		NGram[] ngram = ngrams(token, 2);
  		for(int i = 0; i < ngram.length; i++) {
  			if(ngram[i].getNumberOfGram() > 1) {
  				GenerateFeature gfeature = holder.add(prefix, normalize(ngram[i].getToken())) ;
  				gfeature.setType(Feature.Type.NGRAM) ;
  			}
  		}
  	} catch(Exception ex) {
  		ex.printStackTrace() ;
  	}
  }
  
  private String normalize(String string) {
  	if(normalize) string = string.replace(' ', '+') ;
  	return string ;
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
}