package org.headvances.search.rest.analyse;

import java.util.ArrayList;
import java.util.List;

import org.headvances.ml.nlp.chunk.CRFChunkTokenAnalyzer;
import org.headvances.ml.nlp.pos.OpenNLPPOSTokenAnalyzer;
import org.headvances.ml.nlp.ws.CRFWSTokenAnalyzer;
import org.headvances.ml.nlp.ws.OpenNLPWSTokenAnalyzer;

import org.headvances.nlp.NLPResource;
import org.headvances.nlp.dict.Dictionary;
import org.headvances.nlp.dict.DictionaryTaggingAnalyzer;
import org.headvances.nlp.dict.UnknownWordTokenSplitter;
import org.headvances.nlp.query2.chunker.AddressChunker;
import org.headvances.nlp.query2.chunker.CurrencyChunker;
import org.headvances.nlp.query2.chunker.DateChunker;
import org.headvances.nlp.query2.chunker.PhoneNumberChunker;
import org.headvances.nlp.query2.chunker.TimeChunker;
import org.headvances.nlp.query2.match.MatcherResourceFactory;
import org.headvances.nlp.token.IToken;
import org.headvances.nlp.token.TextSegmenter;
import org.headvances.nlp.token.TokenCollection;
import org.headvances.nlp.token.analyzer.CommonTokenAnalyzer;
import org.headvances.nlp.token.analyzer.PunctuationTokenAnalyzer;
import org.headvances.nlp.token.analyzer.SentenceSplitterAnalyzer;
import org.headvances.nlp.token.analyzer.TokenAnalyzer;
import org.headvances.nlp.ws.NGramStatisticWSTokenAnalyzer;
import org.headvances.util.text.StringUtil;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class WordAnalyzer {
	private Algorithm opennlp ;
	private Algorithm crf ;
	private Algorithm statistic ;
	
	private TokenCollectionRenderer renderer ;
	
	public WordAnalyzer() throws Exception {
		Dictionary dict = NLPResource.getInstance().getDictionary(Dictionary.DICT_RES) ;
		opennlp = new Algorithm(dict, new OpenNLPWSTokenAnalyzer()) ;
		crf = new Algorithm(dict, new CRFWSTokenAnalyzer()) ;
		statistic = new Algorithm(dict, new NGramStatisticWSTokenAnalyzer()) ;
		renderer = new TokenCollectionRenderer(dict) ;
	}
	
	public String[] analyze(String text, String algorithm, String show) throws Exception {
		String[] line = text.split("\n|\r");
		List<String> holder = new ArrayList<String>() ;
		Algorithm wordAnalyzer = statistic ;
		if("crf".equals(algorithm)) {
			wordAnalyzer = crf ;
		} else if("opennlp".equals(algorithm)) {
			wordAnalyzer = opennlp ;
		}
		for(int i = 0; i < line.length; i++) {
			line[i] = line[i].trim() ;
			if(StringUtil.isEmpty(line[i])) continue ;
			StringBuilder b = new StringBuilder() ;
			TokenCollection[] collections = wordAnalyzer.wordSegment(line[i]) ;
			for(TokenCollection wsCollection : collections) {
				TokenCollection chunkCollection = wordAnalyzer.chunk(wsCollection) ;
				TokenCollection entityCollection = wordAnalyzer.extractEntity(wsCollection) ;
				String html = renderer.render(wsCollection, chunkCollection, entityCollection); 
				b.append(html) ;
			}
			holder.add(b.toString()) ;
		}
		return holder.toArray(new String[holder.size()]) ;
	}
	
  static public class Algorithm {
  	private TokenAnalyzer[] wordAnalyzer ;
  	private TokenAnalyzer[] chunkAnalyzer ;
  	private TokenAnalyzer[] entityAnalyzer ;
  	
  	public Algorithm(Dictionary dict, TokenAnalyzer wordAnalyzer) throws Exception {
  		MatcherResourceFactory resFactory = new MatcherResourceFactory() ;
  		this.wordAnalyzer = new TokenAnalyzer[] {
  			new PunctuationTokenAnalyzer(), new CommonTokenAnalyzer(),
  			
  			new TimeChunker(), new DateChunker(),
  			new PhoneNumberChunker(resFactory),
  			new CurrencyChunker(resFactory),
  			
  			wordAnalyzer,
  			new UnknownWordTokenSplitter(dict),
  			new DictionaryTaggingAnalyzer(dict),
  			new AddressChunker(resFactory)
  		};
  		
  		this.entityAnalyzer = new TokenAnalyzer[] {
  			new CurrencyChunker(resFactory),
  			new AddressChunker(resFactory), 
  		} ;
  		
  		this.chunkAnalyzer = new TokenAnalyzer[] {
  			new OpenNLPPOSTokenAnalyzer(), 
  	    new CRFChunkTokenAnalyzer()
  		};
  	}
  		
  	public TokenCollection[] wordSegment(String text) throws Exception {
  		TextSegmenter segmenter = new TextSegmenter(wordAnalyzer) ;
  		IToken[] token = segmenter.segment(text) ;
  		TokenCollection[] collections = SentenceSplitterAnalyzer.INSTANCE.analyze(token) ;
  		return collections ;
  	}
  	
  	public TokenCollection extractEntity(TokenCollection collection) throws Exception {
//  		IToken[] token = collection.getSingleTokens() ;
//  		for(TokenAnalyzer sel : entityAnalyzer) {
//  			token = sel.analyze(token) ;
//  		}
//  		return new TokenCollection(token) ;
  		return collection ;
  	}
  	
  	public TokenCollection chunk(TokenCollection collection) throws Exception {
  		IToken[] token = collection.getSingleTokens() ;
  		for(TokenAnalyzer sel : chunkAnalyzer) {
  			token = sel.analyze(token) ;
  		}
  		return new TokenCollection(token) ;
  	}
	}
}