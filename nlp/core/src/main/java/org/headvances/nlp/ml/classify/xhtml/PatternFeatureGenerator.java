package org.headvances.nlp.ml.classify.xhtml;

import org.headvances.nlp.ml.classify.FeatureHolder;
import org.headvances.nlp.ml.classify.FeatureUtil;
import org.headvances.util.text.StringMatcher;
import org.headvances.util.text.StringUtil;

public class PatternFeatureGenerator {
	static String[] PATTERNS= {
		"*article*", "*tintuc*", "*tin-tuc*", "*news*",
		"*forum*", "*post*", "*thread*", "*diendan*",
		"*blog*", "*wordpress*",
		"*product*", "*prod-*", "*-prod*", "*prod_*", "*_prod*", "*sanpham*",
		"*classified*", "*raovat*", "*rao-vat*", 
		"*job*", "*vieclam*",
		
		"*content*", "*title*", "*header*", "*item*", "*cart*", "*detail*"
	} ;

	static char[] TOKEN_SEP = {' ', '-', '_', '.'};
	
	private StringMatcher[] matchers ;

	public PatternFeatureGenerator() {
		matchers = new StringMatcher[PATTERNS.length] ;
		for(int i = 0; i < matchers.length; i++) {
			matchers[i] = new StringMatcher(PATTERNS[i]) ;
		}
	}


	public void generate(FeatureHolder holder, String prefix, String string) {
		if(StringUtil.isEmpty(string)) return ;
		String[] token = StringUtil.splitAsArray(string, TOKEN_SEP) ;
		generate(holder,  prefix, token) ;
	}

	public void generate(FeatureHolder holder, String prefix, String[] token) {
		for(String sel : token) {
			if(!StringUtil.isEmpty(sel)) {
				for(StringMatcher matcher : matchers) {
					if(matcher.matches(sel)) {
						holder.add(prefix, FeatureUtil.normalize(sel));
						break ;
					}
				}
			}
		}
	}
}