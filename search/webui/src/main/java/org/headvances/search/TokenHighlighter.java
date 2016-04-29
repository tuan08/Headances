package org.headvances.search;

import java.util.Arrays;
import java.util.Comparator;

import org.headvances.nlp.token.CharacterComparator;
import org.headvances.nlp.token.IToken;
import org.headvances.nlp.token.WordTokenizer;
import org.headvances.nlp.util.CharacterSet;
import org.headvances.util.HeapTree;
import org.headvances.util.text.StringUtil;
/**
 * @author Tuan Nguyen
 */
abstract public class TokenHighlighter {
  final static MatchPhraseScoreComparator PHRASE_SCORE_COMPARATOR = new MatchPhraseScoreComparator() ;
  final static MatchPhraseOrderComparator PHRASE_ORDER_COMPARATOR = new MatchPhraseOrderComparator() ;
 
  private char[][] bufsTHighlight ;
  private CharacterComparator charComparator = CharacterComparator.NO_VN_ACCENT ;
  
  public TokenHighlighter(String[] highlightWords) {
    if(highlightWords == null || highlightWords.length == 0) {
    } else {
      bufsTHighlight  = new char[highlightWords.length][] ;
      for(int i = 0; i < highlightWords.length; i++)  bufsTHighlight[i] = highlightWords[i].toCharArray() ;
    }
  }
  
  public void setCharacterComparator(CharacterComparator comparator) {
    this.charComparator = comparator ;
  }
  
  public String[] extractBestMatch(String text, int max) {
    if(bufsTHighlight == null) return StringUtil.EMPTY_ARRAY ;
    StringBuilder b = new StringBuilder() ;
    WordTokenizer tokenizer = new WordTokenizer(text) ;
    int  matchScore =  0, order = 0;
    int numberOfTokenInPhrase = 0 ;
    HeapTree<MatchPhrase> holder = new HeapTree<MatchPhrase>(max, PHRASE_SCORE_COMPARATOR) ;
    IToken token = null ;
    while((token = tokenizer.next()) != null) {
    	if(b.length() > 0) b.append(' ') ;
      if(numberOfTokenInPhrase > 20 || isEndPhrase(token)) {
        if(matchScore > 0) {
          MatchPhrase phrase = new MatchPhrase(order++, matchScore, b.toString()) ;
          holder.insert(phrase) ;
        }
        b.setLength(0) ;
        matchScore = 0 ;
        numberOfTokenInPhrase = 0 ;
      } else {
        if(matchToken(token, bufsTHighlight)) {
          beforeRender(b, token.getOriginalForm()) ;
          b.append(token.getOriginalForm()) ;
          afterRender(b, token.getOriginalForm()) ;
          matchScore++ ;
        } else {
        	b.append(token.getOriginalForm()) ;
        }
        numberOfTokenInPhrase++ ;
      }
    }
    if(matchScore > 0) {
      MatchPhrase phrase = new MatchPhrase(order++, matchScore, b.toString()) ;
      holder.insert(phrase) ;
    }
    if(holder.size() == 0) return StringUtil.EMPTY_ARRAY ;
    MatchPhrase[] mphrases = holder.toArray(new MatchPhrase[holder.size()]) ;
    Arrays.sort(mphrases, PHRASE_ORDER_COMPARATOR) ;
    String[] ret = new String[mphrases.length] ;
    for(int i = 0; i < mphrases.length; i++) ret[i] = mphrases[i].phrase ;
    return ret ;
  }
  
  public String getHighlightText(String text) {
    if(bufsTHighlight == null) return text ;
    if(text == null || text.length() == 0) return text ;
    StringBuilder b = new StringBuilder() ;
    WordTokenizer tokenizer = new WordTokenizer(text) ;
    IToken token = null ;
    while((token = tokenizer.next()) != null) {
    	String currentToken = token.getOriginalForm() ;
    	if(b.length() > 0) b.append(' ') ;
    	if(matchToken(token, bufsTHighlight)) {
    		beforeRender(b, currentToken) ;
    		b.append(currentToken) ;
    		afterRender(b, currentToken) ;
    	} else {
    		b.append(currentToken) ;
    	}
    }
    return b.toString();
  }
  
  
  abstract public void beforeRender(StringBuilder b, String match) ;
  abstract public void afterRender(StringBuilder b, String match) ;
   
  private boolean matchToken(IToken token, char[][] bufsTHighlight) {
    char[] buf = token.getNormalizeFormBuf() ;
  	for(char[] matchBuf : bufsTHighlight) {
  		if(buf.length != matchBuf.length) continue ;
  		boolean match = true ;
  		for(int i = 0; i < buf.length; i++) {
  			if(charComparator.compare(buf[i], matchBuf[i]) != 0) {
  				match = false ;
  				break; 
  			}
  		}
  		if(match) return true ;
    }
    return false ;
  }
  
  private boolean isEndPhrase(IToken token) {
  	String nform = token.getNormalizeForm() ;
  	if(nform.length() == 1) {
  		if(CharacterSet.isEndSentence(nform.charAt(0))) return true ;
  	} else if(nform.length() == 3) {
  		if("...".equals(nform)) return true ;
  	}
    return false ;
  }
  
  static public class MatchPhrase {
    int order ;
    int score ;
    String phrase ;
    
    public MatchPhrase(int order, int score, String phrase) {
      this.order = order ;
      this.score = score ;
      this.phrase = phrase ;
    }
  }
  
  static public class MatchPhraseScoreComparator implements Comparator<MatchPhrase>{
    public int compare(MatchPhrase arg0, MatchPhrase arg1) {
      return arg0.score - arg1.score;
    }
  }
  
  static public class MatchPhraseOrderComparator implements Comparator<MatchPhrase>{
    public int compare(MatchPhrase arg0, MatchPhrase arg1) {
      return arg0.order - arg1.order;
    }
  }
}