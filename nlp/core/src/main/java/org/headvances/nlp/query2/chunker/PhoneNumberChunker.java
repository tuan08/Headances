package org.headvances.nlp.query2.chunker;

import java.util.List;

import org.headvances.nlp.query2.match.MatcherResourceFactory;
import org.headvances.nlp.query2.match.RuleMatcher;
import org.headvances.nlp.token.IToken;
import org.headvances.nlp.token.analyzer.PhoneNumberUtil;
import org.headvances.nlp.token.tag.PhoneTag;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class PhoneNumberChunker extends QueryChunker {
  public PhoneNumberChunker(MatcherResourceFactory mrFactory) throws Exception {
    super(mrFactory) ;
    String phoneNumExp =  "(\\d{1,4}[.\\-]?)+" ;

    String regexWithBracket = "regex{\\(} .0. regex{\\+?\\d{2,4}} .0. regex{\\)}";

    defineMatches(
      // The format start with bracket followed by two or three frequency groups
      "/ " + regexWithBracket + " .1. regex{\\d{2,4}} .0. regex{\\d{2,4}} .0. regex{\\d{2,4}}" ,
      "/ " + regexWithBracket + " .1. regex{\\d{2,4}} .0. regex{\\d{2,4}}" ,
      // The format start with bracket followed by a common format
      "/ " + regexWithBracket + " .0. regex{[0-9.\\-]+}" , 
      // The format contains spaces between frequency groups
      "/ regex{\\d{2,4}} .0. regex{\\d{6,8}}" ,
      "/ regex{\\d{2,4}} .0. regex{\\d{2,4}} .0. regex{\\d{2,4}}" , 
      // Common format
      "/ regex{(\\d{1,4}[.\\-]?){2,5}}"
    ) ;
  }

  protected void onMatch(List<IToken> holder, RuleMatcher rmatcher, IToken[] token, int from, int to) {
  	IToken set = createChunking(token, from, to) ;
  	String number = set.getOriginalForm();
  	boolean ignore = false ;
  	if(number.length() < 8) ignore = true ; 
  	else if(number.endsWith("000")) ignore = true ;
  	if(ignore) {
  		for(int i = from; i < to; i++) holder.add(token[i]) ;
  		return ;
  	}
  	String provider = PhoneNumberUtil.getMobileProvider(number);
  	set.add(new PhoneTag(number, provider, "phone"));
  	holder.add(set) ;
  }
}