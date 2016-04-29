package org.headvances.nlp.ml.token;

import opennlp.model.AbstractEventStream;
import opennlp.model.Event;

public class OpenNLPEventStream extends AbstractEventStream {
  private Event next;
  private TokenFeaturesGenerator featuresGenerator;

  private int currentFile = 0;
  private String[] files;

  private TokenFeatures[] tokenFeatures;
  private int currentToken;

  public OpenNLPEventStream(String[] files, TokenFeaturesGenerator featuresGenerator){
    this.featuresGenerator = featuresGenerator;
    this.files = files;
  }

  public Event next () { 
  	Event ret = next ;
  	next = null ;
  	return ret ; 
  }

  public boolean hasNext () {
  	if(next != null) return true ;
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
  	String file = files[currentFile++] ;
  	try {
  		currentToken = 0;
  		return featuresGenerator.generateWTagFile(file) ;
  	} catch(Exception ex) {
  		throw new RuntimeException(ex) ;
  	}
  }
}