package org.headvances.nlp.ml.token;

import java.util.List;

import org.headvances.nlp.token.IToken;
import org.headvances.nlp.token.tag.CharacterTag;
import org.headvances.nlp.token.tag.CurrencyTag;
import org.headvances.nlp.token.tag.DateTag;
import org.headvances.nlp.token.tag.DigitTag;
import org.headvances.nlp.token.tag.EmailTag;
import org.headvances.nlp.token.tag.NumberTag;
import org.headvances.nlp.token.tag.PhoneTag;
import org.headvances.nlp.token.tag.TimeTag;
import org.headvances.nlp.token.tag.TokenTag;
import org.headvances.nlp.token.tag.URLTag;
import org.headvances.nlp.token.tag.WordTag;
import org.headvances.util.text.StringUtil;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class TokenTypeFeatureGenerator implements TokenFeatureGenerator {
	final static int MAX_SPECIAL_CHAR_COUNT = 4 ;
	
	static String[] TYPES = {
		WordTag.WLETTER.getOType(), WordTag.SEQ_LD.getOType(), WordTag.SEQ_LDD.getOType(),
		CharacterTag.TYPE, DigitTag.TYPE, NumberTag.TYPE, URLTag.TYPE, EmailTag.TYPE,
		DateTag.TYPE, TimeTag.TYPE, CurrencyTag.TYPE, PhoneTag.TYPE
	} ;

	public void generate(IToken[] token, int pos, TokenFeatureHolder holder) {
		addTokenType(token, pos - 1, "p1:",  holder) ;
		addTokenType(token, pos    , "p0:",  holder) ;
		addTokenType(token, pos + 1, "n1:",  holder) ;
	}

	private void addTokenType(IToken[] token, int pos, String prefix, TokenFeatureHolder holder) {
		if(pos < 0 || pos >= token.length) return ;
		List<TokenTag> tags = token[pos].getTag() ;
		if(tags != null) {
			for(int i = 0; i < tags.size(); i++) {
				TokenTag selTag = tags.get(i) ;
				if(StringUtil.isIn(selTag.getOType(), TYPES)) {
					holder.addFeature(prefix + selTag.getOType()) ;
				}
				if(selTag instanceof CharacterTag) {
					addCharFeatures((CharacterTag) selTag, prefix, holder) ;
					return ;
				}
			}
		}
	}
	
	void addCharFeatures(CharacterTag ctag, String prefix, TokenFeatureHolder holder) {
		for(CharacterTag.CharDescriptor desc : ctag.getCharDescriptors()) {
			char type = desc.character ;
			if(type == 'l' || type == 'V' || type == 'd') {
				holder.addFeature(prefix + "char:" + type)  ;
			} else {
				if (desc.count >= MAX_SPECIAL_CHAR_COUNT) holder.addFeature("char:" + type)  ;
				else holder.addFeature(prefix +  "char:" + type + ":" + desc.count)  ;
			}
		}
		if(ctag.getSuffix() != null) {
			holder.addFeature(prefix +  "char:suffix:" + ctag.getSuffix()) ;
		}
	}
}