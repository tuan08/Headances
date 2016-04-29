package org.headvances.ml.nlp.ent;

import org.headvances.nlp.token.IToken;
import org.headvances.nlp.token.TokenCollection;
import org.headvances.nlp.token.UnitAbbreviation;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class CurrencyTag extends QuantityTag {
	final static public String TYPE = "qt:currency" ;
	
	final static public UnitAbbreviation[] CURRENCY =
		UnitAbbreviation.merge(UnitAbbreviation.VND, UnitAbbreviation.USD) ;
	
	static String[] UNITS = UnitAbbreviation.getUnits(CURRENCY) ;
	
	public CurrencyTag(IToken token) {
		if(token instanceof TokenCollection) {
			init((TokenCollection) token) ;
		} else {
			init(token) ;
		}
	}

	private void init(TokenCollection tseq) {
		Object[] token = getTokenValue(tseq) ;
		setQuantity(selectQuantity(token)) ;
		setUnit(selectUnit(token, UNITS, "vnd")) ;
	}

	private void init(IToken sel) {
		Object[] token = getTokenValue(sel) ;
		setQuantity(selectQuantity(token)) ;
		setUnit(selectUnit(token, UNITS, "vnd")) ;
	}
	
	public String getOType() { return TYPE ; }
	
  public boolean isTypeOf(String type) { return TYPE.equals(type); }
}
