package org.headvances.nlp.query2.match;

import java.util.List;

import org.headvances.nlp.token.IToken;
import org.headvances.nlp.token.tag.CurrencyTag;
import org.headvances.nlp.token.tag.TokenTag;
import org.headvances.nlp.util.ParamHolder;
import org.headvances.util.text.StringMatcher;


public class CurrencyUnitMatcher extends UnitMatcher {
  private StringMatcher[] unitMatcher ;

  public CurrencyUnitMatcher(ParamHolder holder, int allowNextMatchDistance) throws Exception {
    setAllowNextMatchDistance(allowNextMatchDistance) ;
    String[] unit = holder.getFieldValue("unit") ;
    if(unit != null) unitMatcher = StringMatcher.create(unit) ;
  }

  public UnitMatch matches(IToken[] token, int pos) {
    List<TokenTag> tags = token[pos].getTag() ;
    if(tags == null) return null ;
    for(int i = 0; i < tags.size(); i++) {
      TokenTag tag = tags.get(i) ;
      if(!(tag instanceof CurrencyTag)) continue ;
      CurrencyTag implTag = (CurrencyTag) tag ;
      if(unitMatcher != null) {
        if(!matches(unitMatcher, implTag.getUnit())) return null ;
      }
      return new UnitMatch(implTag.getTagValue(), pos, pos + 1) ;
    }
    return null ;
  }
}