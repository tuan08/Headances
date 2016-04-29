package org.headvances.ml.nlp.chunk;

import java.util.ArrayList;
import java.util.List;

import org.headvances.nlp.token.IToken;
import org.headvances.nlp.token.TokenCollection;
import org.headvances.nlp.token.TokenException;

import cc.mallet.types.Instance;
import cc.mallet.types.Sequence;

public class CRFChunkTokenAnalyzer extends CRFChunkTagTokenAnalyzer {
  public CRFChunkTokenAnalyzer() throws Exception  { super() ; }
  public CRFChunkTokenAnalyzer(String crfModelFile) throws Exception {
    super(crfModelFile) ;
  }
  
  public IToken[] analyze(IToken[] token) throws TokenException {
  	if(token.length == 0) return token ;
    Instance instance = factory.createDecodeInstance(token) ;
    Sequence input = (Sequence) instance.getData();
    Sequence[] outputs = apply(crf, input, /*nBestOption.value*/ 1);
    for (int a = 0; a < outputs.length; a++) {
      if (outputs[a].size() != input.size()) {
        throw new TokenException("Failed to decode input sequence, answer " + a);
      }
    }
    List<IToken> holder = new ArrayList<IToken>() ;
    int i = 0;
    while(i < token.length) {
      String target = outputs[0].get(i).toString() ;
      if(target.endsWith(":B")) {
      	target = target.substring(0, target.length() - 2) ;
      	int start = i ;
        i++ ;
        while(i < token.length && outputs[0].get(i).toString().endsWith(":I")) {
          i++ ;
        }
        if(i - start == 1) {
        	token[start].add(new ChunkTag(token[start].getNormalizeForm(), target)) ;
        	holder.add(token[start]);
        } else {
          IToken newToken = new TokenCollection(token, start, i) ;
          newToken.add(new ChunkTag(newToken.getNormalizeForm(), target)) ;
          holder.add(newToken);
        }
      } else {
        holder.add(token[i]) ;
        token[i].add(new ChunkTag(token[i].getNormalizeForm(), target)) ;
        i++ ;
      }
    }
    return holder.toArray(new IToken[holder.size()]);
  }
}