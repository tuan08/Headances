package org.headvances.nlp.token.analyzer;

import java.util.ArrayList;
import java.util.List;

import org.headvances.nlp.token.IToken;
import org.headvances.nlp.token.Token;
import org.headvances.nlp.token.TokenException;
import org.headvances.nlp.token.tag.NumberTag;
import org.headvances.util.text.StringUtil;

public class NumberTokenAnalyzer implements TokenAnalyzer {
  final static String[] DIGIT_TOKENS = {
    "không", "khong", "một", "mot", "hai", "ba", "bốn", "bon", "tư", "tu",
    "năm", "nam", "lăm", "nhăm", "nham", "lam", "sáu", "sau", "bảy", "bay",
    "tám", "tam", "chín", "chin","mười", "muoi"
  };

  final static String[] UNIT_TOKENS = {
    "đơn vị", "linh", "lẻ", "mươi", 
    "chục", "chuc", "trăm", "tram", "ngàn", "ngan",
    "nghìn", "nghin", "vạn", "van", "triệu", "trieu", "tỷ", "ty"
  };


  public IToken[] analyze(IToken[] token) throws TokenException {
    List<IToken> holder = new ArrayList<IToken>() ;
    int pos = 0;
    while(pos < token.length){
      int limitIdx = pos+1;
      String norm = token[pos].getNormalizeForm() ;
      if(!StringUtil.isIn(norm, DIGIT_TOKENS)) {
        IToken newToken = new Token(token, pos, limitIdx) ;
        holder.add(newToken);
        pos++;
        continue;
      }

      while(limitIdx < token.length){
        String nextToken = token[limitIdx].getOriginalForm();
        if(StringUtil.isIn(nextToken, DIGIT_TOKENS) || StringUtil.isIn(nextToken, UNIT_TOKENS)){ 
          limitIdx++ ; 
        } else {
          break;
        }
      }

      if(limitIdx - pos >= 1){
        IToken newToken = new Token(token, pos, limitIdx) ;
        double value = getNumberValue(newToken);
        newToken.add(new NumberTag(value));
        holder.add(newToken);
        pos = limitIdx;
      } else 
        pos++;
    }

    return holder.toArray(new IToken[holder.size()]);
  }

  private double getNumberValue(IToken token){
    String[] units = StringUtil.splitAsArray(token.getOriginalForm(), new char[]{' '});
    double value = 0;
    int i = 0;
    while(i < units.length){
      if(StringUtil.isIn(units[i], new String[]{"linh", "lẻ", "le"})) {
        i++; 
        continue;
      }
      double uValue = getUnitValue(units[i]);
      double nValue = 0;
      if(i + 1 < units.length) {
        nValue = getUnitValue(units[i+1]);
        if(uValue == 10 && nValue < 10) {
          uValue += nValue;
          nValue = getUnitValue(units[i+2]);
          i += 3;
        } else if(uValue < 10 && nValue <= 10 && i + 2 < units.length - 1) {
          if(nValue < 10) {
            uValue = uValue*10 + nValue;
            uValue += value;
          }
          if(nValue == 10){
            uValue *= 10;
            uValue += value;
          }
          nValue = getUnitValue(units[i+2]);
          i += 3;
        }  else {
          i += 2;
        }
        value += uValue * nValue;
      } else {
        value += uValue;
        i++;
      }
    }
    return value;
  }

  private double getUnitValue(String unit){
    if(StringUtil.isIn(unit, new String[]{"không", "khong", "linh", "lẻ"})) return 0;
    if(StringUtil.isIn(unit, new String[]{"một", "mot", "đơn vị"})) return 1;
    if(StringUtil.isIn(unit, new String[]{"hai"})) return 2;
    if(StringUtil.isIn(unit, new String[]{"ba"})) return 3;
    if(StringUtil.isIn(unit, new String[]{"bốn", "tư", "bon"})) return 4;
    if(StringUtil.isIn(unit, new String[]{"năm", "lăm", "nam", "lam", "nhăm", "nham"})) return 5;
    if(StringUtil.isIn(unit, new String[]{"sáu", "sau"})) return 6;
    if(StringUtil.isIn(unit, new String[]{"bảy", "bay"})) return 7;
    if(StringUtil.isIn(unit, new String[]{"tám", "tam"})) return 8;
    if(StringUtil.isIn(unit, new String[]{"chín", "chin"})) return 9;
    if(StringUtil.isIn(unit, new String[]{"mười", "mươi", "muoi", "chục"})) return 10;
    if(StringUtil.isIn(unit, new String[]{"trăm", "tram"})) return 100;
    if(StringUtil.isIn(unit, new String[]{"ngàn", "nghìn", "nghin", "ngan"})) return 1000;
    if(StringUtil.isIn(unit, new String[]{"vạn", "van"})) return 10000;
    if(StringUtil.isIn(unit, new String[]{"triệu", "trieu"})) return 10e6;
    if(StringUtil.isIn(unit, new String[]{"tỷ", "ty"})) return 10e9;
    return 0;
  }


}
