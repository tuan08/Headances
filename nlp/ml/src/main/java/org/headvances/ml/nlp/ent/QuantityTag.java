package org.headvances.ml.nlp.ent;

import java.util.ArrayList;
import java.util.List;

import org.headvances.nlp.token.IToken;
import org.headvances.nlp.token.TokenCollection;
import org.headvances.nlp.token.tag.DigitTag;
import org.headvances.nlp.token.tag.NumberTag;
import org.headvances.nlp.token.tag.TokenTag;
import org.headvances.util.text.NumberUtil;
import org.headvances.util.text.StringUtil;
/**
 * $Author: Tuan Nguyen$ 
 **/
abstract public class QuantityTag extends TokenTag {
	final static public String[] NGAN_TOKENS =  { "ngàn", "ngan" } ;
	final static public String[] TRIEU_TOKENS = { "triệu", "trieu", "tr" } ;
	final static public String[] TY_TOKENS =    { "tỷ", "ty", "tỉ", "ti" } ;
	
	private double quantity ;
	private String unit ;
	private String tokenValue ;
	
	public String getTagValue() { return this.tokenValue ; }
	public void setTagValue(String value) { this.tokenValue = value ; }
	
	public double getQuantity() { return this.quantity ; }
	public void setQuantity(double quantity) { this.quantity = quantity ; }
	
	public String getUnit() { return this.unit ; }
	public void setUnit(String unit) { this.unit = unit ; }
	
	protected Object[] getTokenValue(TokenCollection tseq) {
		IToken[] token = tseq.getTokens() ;
		List<Object> holder = new ArrayList<Object>() ;
		for(int i = 0; i < token.length; i++) {
			DigitTag digitTag = (DigitTag) token[i].getFirstTagType(DigitTag.TYPE) ;
			if(digitTag != null) {
				Long value = digitTag.getLongValue() ;
				if(value != null) {
					holder.add(value) ;
					continue ;
				}
			}
		  NumberTag number = (NumberTag) token[i].getFirstTagType(NumberTag.TYPE) ;
		  if(number != null) {
		  	holder.add(number.getValue()) ;
		  	continue; 
		  }
		  if(splitNumber(holder, token[i])) continue ;
		  holder.add(token[i].getNormalizeForm()) ;
		}
		return holder.toArray(new Object[holder.size()]) ;
	}
	
	protected Object[] getTokenValue(IToken token) {
		List<Object> holder = new ArrayList<Object>() ;
		DigitTag digitTag = (DigitTag) token.getFirstTagType(DigitTag.TYPE) ;
		if(digitTag != null) {
			Long value = digitTag.getLongValue() ;
			if(value != null) {
				holder.add(value) ;
				return holder.toArray(new Object[holder.size()]) ;
			}
		}
	  NumberTag number = (NumberTag) token.getFirstTagType(NumberTag.TYPE) ;
	  if(number != null) {
	  	holder.add(number.getValue()) ;
	  	return holder.toArray(new Object[holder.size()]) ;
	  }
	  if(splitNumber(holder, token)) {
	  	return holder.toArray(new Object[holder.size()]) ;
	  }
	  holder.add(token.getNormalizeForm()) ;
		return holder.toArray(new Object[holder.size()]) ;
	}
	
	private boolean splitNumber(List<Object> holder, IToken token) {
		char[] buf = token.getNormalizeFormBuf() ;
		int separator = -1 ;
		for(int i = 0; i < buf.length; i++) {
			char c = buf[i] ;
			if((c >= 0 || c <=9) || c == '.' || c == ',') {
				
			} else {
				if(separator < 0) {
					separator = i ;
					break ;
				}
			}
		}
		if(separator > 0) {
			String number = new String(buf, 0, separator) ;
			String suffix = new String(buf, separator, buf.length) ;
			Double numberValue = NumberUtil.parseRealNumber(number.toCharArray()) ;
			if(numberValue != null) {
				if(StringUtil.isIn(suffix, NGAN_TOKENS)){
					numberValue = numberValue.doubleValue() *  1000 ;
					suffix = null ;
				} else if(StringUtil.isIn(suffix, TRIEU_TOKENS)){
					numberValue = numberValue.doubleValue() *  1000000 ;
					suffix = null ;
				} else if(StringUtil.isIn(suffix, TY_TOKENS)){
					numberValue = numberValue.doubleValue() *  1000000000 ;
					suffix = null ;
				}
				holder.add(numberValue) ;
				if(suffix != null && suffix.length() > 0) holder.add(suffix) ;
 				return true ;
			}
		}
		return false ;
	}
	
	protected double selectQuantity(Object[] value) {
		double selValue = -1d ;
		for(int i = 0 ; i < value.length; i++) {
			if(value[i] instanceof Double) {
				Double val = (Double) value[i] ;
				if(val.doubleValue() > selValue) selValue = val.doubleValue() ;
			} else if(value[i] instanceof Long) {
				Long val = (Long) value[i] ;
				if(val.doubleValue() > selValue) selValue = val.doubleValue() ;
			}
		}
		return selValue ;
	}
	
	protected String selectUnit(Object[] value, String[] candidate, String dcandidate) {
		String selUnit = null ;
		for(int i = 0 ; i < value.length; i++) {
			if(value[i] instanceof String) {
				String unit = (String) value[i] ;
				for(String selCandidate : candidate) {
					if(unit.indexOf(selCandidate) >= 0) return unit ;
				}
				if(selUnit == null) selUnit = unit ;
			}
		}
		if(selUnit == null) selUnit = dcandidate ;
		return selUnit ;
	}
}