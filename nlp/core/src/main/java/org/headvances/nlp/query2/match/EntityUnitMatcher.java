package org.headvances.nlp.query2.match;

import org.headvances.nlp.dict.EntityDictionary;
import org.headvances.nlp.dict.Meaning;
import org.headvances.nlp.util.ParamHolder;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class EntityUnitMatcher extends TreeWordMatcher {
	private String   name ;
	private String[] type ;
	
	public EntityUnitMatcher(EntityDictionary dict, ParamHolder pholder, int allowNextMatchDistance) {
		setAllowNextMatchDistance(allowNextMatchDistance) ;
		this.name = pholder.getFirstFieldValue("name") ;
		this.type = pholder.getFieldValue("type") ;
		Meaning[] meanings = dict.find(name, type) ;
		for(Meaning sel : meanings) {
			addWord(sel.getName()) ;
			addWord(sel.getVariant()) ;
		}
	}
}