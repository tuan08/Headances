package org.headvances.nlp.ml.chunk;

import org.headvances.nlp.ml.token.TokenFeatureGenerator;
import org.headvances.nlp.ml.token.TokenFeatureHolder;
import org.headvances.nlp.token.IToken;
import org.headvances.nlp.token.tag.PosTag;
import org.headvances.nlp.wtag.WTagBoundaryTag;
import org.headvances.util.text.StringUtil;

public class ChunkFeatureGenerator implements TokenFeatureGenerator {
	
  public void generate(IToken[] token, int pos, TokenFeatureHolder holder) {
    addMeaning(token, pos, "p0:", holder);
  }

  void addMeaning(IToken[] token, int pos, String label, TokenFeatureHolder holder) {
    if (pos < 0 || pos >= token.length) return;
    PosTag ptag = token[pos].getFirstTagType(PosTag.class);
    if (ptag != null) {
      String[] posTag = ptag.getPosTag();
      for (int i = 0; i < posTag.length; i++)
        holder.addFeature(label + posTag[i]);
    } else {
      WTagBoundaryTag btag = (WTagBoundaryTag)token[pos].getFirstTagType(WTagBoundaryTag.TYPE) ;
      if(btag != null) {
        String[] feature = btag.getFeatures() ;
        if(feature != null && feature.length > 0) {
          for(String sel : feature) {
            if(sel.startsWith("pos:")) {
              holder.addFeature(label + sel) ;              
            }
          }
        }
      }
    }
    String value = StringUtil.joinStringArray(token[pos].getWord(), "+");
    holder.addFeature(label + value);
  }
}