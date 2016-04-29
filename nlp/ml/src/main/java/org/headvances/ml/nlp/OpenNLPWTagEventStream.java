package org.headvances.ml.nlp;

import java.util.ArrayList;
import java.util.List;

import opennlp.model.AbstractEventStream;
import opennlp.model.Event;

import org.headvances.ml.nlp.feature.TokenFeatures;
import org.headvances.ml.nlp.feature.TokenFeaturesGenerator;
import org.headvances.nlp.token.IToken;
import org.headvances.nlp.token.TokenCollection;
import org.headvances.nlp.wtag.WTagDocumentReader;
import org.headvances.util.IOUtil;

public class OpenNLPWTagEventStream extends AbstractEventStream {
  private Event next;
  private TokenFeaturesGenerator featuresGenerator;

  private int currentFile = 0;
  private String[] files;

  private TokenFeatures[] tokenFeatures;
  private int currentToken;

  public OpenNLPWTagEventStream(String[] files, TokenFeaturesGenerator featuresGenerator){
    this.featuresGenerator = featuresGenerator;
    this.files = files;
  }

  public Event next () { return next; }

  public boolean hasNext () {
    if(tokenFeatures == null) tokenFeatures = nextTokenFeatures() ;
    if(tokenFeatures == null) return false ;

    if(this.currentToken < tokenFeatures.length) {
      TokenFeatures token = tokenFeatures[currentToken++] ;
      next = new Event(token.getTargetFeature(), token.getFeatures()) ;
      return true ;
    } else { 
      tokenFeatures = null;
      return hasNext();
    }
  }

  private TokenFeatures[] nextTokenFeatures() {
    if(currentFile >= files.length) return null ;
    String file = files[this.currentFile++] ;
    List<TokenFeatures> lines = new ArrayList<TokenFeatures>();
    WTagDocumentReader docReader = featuresGenerator.getDocumentReader() ;
    try {
      String sampleData = IOUtil.getFileContentAsString(file, "UTF-8");
      if(sampleData.trim().length() == 0) return null ;
      TokenCollection[] collection = docReader.read(sampleData) ;
      for(int i = 0; i < collection.length; i++) {
        IToken[] token = collection[i].getTokens() ;
        if(token.length == 0) continue ;
        TokenFeatures[] tfeatures = featuresGenerator.generate(token, true) ;
        for(TokenFeatures tfeature: tfeatures){
          lines.add(tfeature);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    currentToken = 0;
    return lines.toArray(new TokenFeatures[lines.size()]) ;
  }
}