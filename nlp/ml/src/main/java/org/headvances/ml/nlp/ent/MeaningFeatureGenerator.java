package org.headvances.ml.nlp.ent;

import java.util.List;

import org.headvances.ml.nlp.feature.FeatureGenerator;
import org.headvances.ml.nlp.feature.TokenFeatureHolder;
import org.headvances.nlp.dict.Dictionary;
import org.headvances.nlp.token.IToken;
import org.headvances.nlp.token.tag.CharacterTag;
import org.headvances.nlp.token.tag.DateTag;
import org.headvances.nlp.token.tag.DigitTag;
import org.headvances.nlp.token.tag.EmailTag;
import org.headvances.nlp.token.tag.NumberTag;
import org.headvances.nlp.token.tag.PhoneTag;
import org.headvances.nlp.token.tag.PosTag;
import org.headvances.nlp.token.tag.TimeTag;
import org.headvances.nlp.token.tag.TokenTag;
import org.headvances.nlp.token.tag.URLTag;
import org.headvances.util.text.StringUtil;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class MeaningFeatureGenerator implements FeatureGenerator {
	static String[] NORMALIZE_TYPES = {
		DigitTag.TYPE, NumberTag.TYPE, CharacterTag.TYPE, URLTag.TYPE, EmailTag.TYPE,
		DateTag.TYPE, TimeTag.TYPE, CurrencyTag.TYPE, PhoneTag.TYPE
	} ;
	
	private Dictionary dict ;
	
	public MeaningFeatureGenerator(Dictionary dict, EntitySetConfig config) {
		this.dict = dict ;
	}
	
	public void generate(IToken[] token, int pos, TokenFeatureHolder holder) {
		addMeaning(token, pos - 3,  "p3:", holder) ;
		addMeaning(token, pos - 2,  "p2:", holder) ;
		addMeaning(token, pos - 1,  "p1:", holder) ;
		addMeaning(token, pos,      "p0:", holder) ;
		addMeaning(token, pos + 1,  "n1:", holder) ;
		addMeaning(token, pos + 2,  "n2:", holder) ;
		addMeaning(token, pos + 3,  "n3:", holder) ;
	}
	
  void addMeaning(IToken[] token, int pos, String label, TokenFeatureHolder holder) {
		if(pos < 0 || pos >= token.length) return ;
		List<TokenTag> tags = token[pos].getTagByType(NORMALIZE_TYPES) ;
  	if(tags != null && tags.size() > 0) {
  		for(int i = 0; i < tags.size(); i++) {
  			holder.addFeature(label + tags.get(i).getOType()) ;
  		}
  		return ;
  	}
  	TokenTag tag = token[pos].getFirstTagType(PosTag.class) ;
		if(tag != null) holder.addFeature(label + tag.getTagValue()) ;
  	holder.addFeature(label + StringUtil.joinStringArray(token[pos].getWord(), "+")) ;
  }
}