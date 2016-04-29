package org.headvances.ml.nlp.ent;

import org.headvances.nlp.token.IToken;
import org.headvances.nlp.token.TokenException;
import org.headvances.nlp.token.analyzer.TokenAnalyzer;

abstract public class EntityTokenAnalyzer implements TokenAnalyzer{
  
  abstract public IToken[] analyze(IToken[] token) throws TokenException;
  abstract public String[] tags(IToken[] token) throws TokenException;

}
