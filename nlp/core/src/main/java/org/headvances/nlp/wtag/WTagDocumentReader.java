package org.headvances.nlp.wtag;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.headvances.nlp.token.IToken;
import org.headvances.nlp.token.TokenCollection;
import org.headvances.nlp.token.TokenException;
import org.headvances.nlp.token.analyzer.CommonTokenAnalyzer;
import org.headvances.nlp.token.analyzer.LineAnalyzer;
import org.headvances.nlp.token.analyzer.SentenceSplitterAnalyzer;
import org.headvances.nlp.token.analyzer.TokenAnalyzer;
import org.headvances.nlp.wtag.WTagBoundaryTag;
import org.headvances.nlp.wtag.WTagTokenizer;
import org.headvances.util.IOUtil;
import org.headvances.util.text.StringUtil;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class WTagDocumentReader {
	static TokenAnalyzer[] TOKEN_ANALYZER =  { new CommonTokenAnalyzer() } ;
	
	private TokenAnalyzer[] tokenAnalyzer = TOKEN_ANALYZER ;
	
	public void setTokenAnalyzer(TokenAnalyzer ... analyzer) {
		this.tokenAnalyzer = analyzer ;
	}
	
	public TokenCollection[] read(IToken[] token) throws TokenException {
		for(int i = 0; i < token.length; i++) {
			WTagBoundaryTag btag = token[i].getFirstTagType(WTagBoundaryTag.class) ;
			if(btag == null) btag = new WTagBoundaryTag(StringUtil.EMPTY_ARRAY) ;
			token[i].clearTag() ;
			token[i].add(btag) ;
		}
		
		List<TokenCollection> collections = new ArrayList<TokenCollection>() ;
		for(TokenCollection line : LineAnalyzer.INSTANCE.analyze(token)) collections.add(line) ;
		return collections.toArray(new TokenCollection[collections.size()]) ;
	}
	
	public TokenCollection[] read(File file) throws Exception {
		String text = IOUtil.getFileContentAsString(file, "UTF-8") ;
		return read(text) ;
	}
	
	public TokenCollection[] read(String text) throws TokenException {
		List<String> line = StringUtil.split(text, '\n') ;
		List<TokenCollection> collections = new ArrayList<TokenCollection>() ;
		for(int i = 0; i < line.size(); i++) {
			String selLine = line.get(i).trim() ;
			if(StringUtil.isEmpty(selLine)) continue ;
			IToken[] tokens = new WTagTokenizer(selLine).allTokens() ;
			TokenCollection[] sentences = SentenceSplitterAnalyzer.INSTANCE.analyze(tokens) ;
			for(TokenCollection sentence : sentences) {
				SentenceSplitterAnalyzer.INSTANCE.analyze(tokens) ;
				sentence.analyze(tokenAnalyzer) ;
				collections.add(sentence) ;
			}
		}
		return collections.toArray(new TokenCollection[collections.size()]) ;
	}
}