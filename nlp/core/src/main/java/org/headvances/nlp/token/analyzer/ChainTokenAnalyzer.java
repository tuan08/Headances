package org.headvances.nlp.token.analyzer;

import org.headvances.nlp.token.IToken;
import org.headvances.nlp.token.TokenException;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class ChainTokenAnalyzer implements TokenAnalyzer {
	private TokenAnalyzer[] analyzer ;
	
  public ChainTokenAnalyzer(TokenAnalyzer ... analyzer) {
  	this.analyzer = analyzer ;
  }
  
	public IToken[] analyze(IToken[] tokens) throws TokenException {
		for(TokenAnalyzer sel : analyzer) {
  		tokens = sel.analyze(tokens) ;
  	}
		return tokens ;
  }
	
}