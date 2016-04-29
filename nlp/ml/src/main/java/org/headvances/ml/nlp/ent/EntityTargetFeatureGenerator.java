package org.headvances.ml.nlp.ent;

import org.headvances.ml.nlp.feature.FeatureGenerator;
import org.headvances.ml.nlp.feature.TokenFeatureHolder;
import org.headvances.nlp.token.IToken;
import org.headvances.nlp.wtag.WTagBoundaryTag;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class EntityTargetFeatureGenerator implements FeatureGenerator {
	private String[] tag  ;
	
	public EntityTargetFeatureGenerator(EntitySetConfig config) {
		tag = config.tagPrefix ;
	}
	
	public void generate(IToken[] token, int pos, TokenFeatureHolder holder) {
		WTagBoundaryTag btag = (WTagBoundaryTag)token[pos].getFirstTagType(WTagBoundaryTag.TYPE) ;
		if(btag != null) {
			String[] feature = btag.getFeatures() ;
			if(feature != null && feature.length > 0) {
				for(String sel : feature) {
					for(String selPrefix : tag) {
						if(sel.startsWith(selPrefix)) {
							holder.setTargetFeature(sel) ;
							return ;
						}
					}
				}
			}
		}
		holder.setTargetFeature("O") ;
	}	
}