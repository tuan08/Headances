package org.headvances.ml.nlp.pos;

import org.headvances.ml.nlp.feature.FeatureGenerator;
import org.headvances.ml.nlp.feature.TokenFeatureHolder;
import org.headvances.nlp.token.IToken;
import org.headvances.nlp.wtag.WTagBoundaryTag;

public class POSTargetFeatureGenerator implements FeatureGenerator {
  public void generate(IToken[] token, int pos, TokenFeatureHolder holder) {
    WTagBoundaryTag btag = (WTagBoundaryTag)token[pos].getFirstTagType(WTagBoundaryTag.TYPE) ;
    if(btag != null) {
      String[] feature = btag.getFeatures() ;
      if(feature != null && feature.length > 0) {
        for(String sel : feature) {
          if(sel.startsWith("pos:")) {
            holder.setTargetFeature(sel) ;
            return ;
          }
        }
      }
    }
    holder.setTargetFeature("O") ;
  }
}