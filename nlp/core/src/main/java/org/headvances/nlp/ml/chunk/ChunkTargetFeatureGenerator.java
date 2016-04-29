package org.headvances.nlp.ml.chunk;

import org.headvances.nlp.ml.token.TokenFeatureGenerator;
import org.headvances.nlp.ml.token.TokenFeatureHolder;
import org.headvances.nlp.token.IToken;
import org.headvances.nlp.wtag.WTagBoundaryTag;

public class ChunkTargetFeatureGenerator implements TokenFeatureGenerator{
  public void generate(IToken[] token, int pos, TokenFeatureHolder holder) {
    WTagBoundaryTag btag = (WTagBoundaryTag)token[pos].getFirstTagType(WTagBoundaryTag.TYPE) ;
    if(btag != null) {
      String[] feature = btag.getFeatures() ;
      if(feature != null && feature.length > 0) {
        for(String sel : feature) {
          if(sel.startsWith("chunk:")) {
            holder.setTargetFeature(sel) ;
            return ;
          }
        }
      }
    }
    holder.setTargetFeature("chunk:O") ;
  }
}