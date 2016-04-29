package org.headvances.ml.nlp.ent;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import opennlp.model.GenericModelReader;
import opennlp.model.MaxentModel;
import opennlp.model.PlainTextFileDataReader;

import org.headvances.ml.nlp.feature.TokenFeatures;
import org.headvances.ml.nlp.feature.TokenFeaturesGenerator;
import org.headvances.nlp.token.IToken;
import org.headvances.nlp.token.TokenCollection;
import org.headvances.nlp.token.TokenException;
import org.headvances.util.IOUtil;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class OpenNLPEntityTokenAnalyzer extends EntityTokenAnalyzer {
  private TokenFeaturesGenerator featuresGenerator ;
  private MaxentModel model;

  public OpenNLPEntityTokenAnalyzer() throws Exception{
    this("classpath:ml/nlp/entity.all.opennlp.maxent", "all");
  }
  
  public OpenNLPEntityTokenAnalyzer(String modelRes, String configName) throws Exception {
    this(modelRes, EntitySetConfig.getConfig(configName));
  }
	
	public OpenNLPEntityTokenAnalyzer(String modelRes, EntitySetConfig config) throws Exception {
    featuresGenerator = MLENT.createTokenFeaturesGenerator(config);
    InputStream input = IOUtil.loadRes(modelRes);
    BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8")) ;
    model = new GenericModelReader(new PlainTextFileDataReader(reader)).getModel();
  }

  public IToken[] analyze(IToken[] token) throws TokenException {
    if(token.length == 0) return token;

    TokenFeatures[] tfeatures = featuresGenerator.generate(token, false) ; 
    String [] outcomes = getOutcomes(tfeatures);
    
    List<IToken> holder = new ArrayList<IToken>() ;
    int i = 0;
    while(i < token.length) {
      String target = outcomes[i] ;
      if(target.endsWith(":B")) {
        int start = i ;
        i++ ;
        while(i < token.length && outcomes[i].endsWith(":I")) {
          i++ ;
        }
        String entityType = target.substring(0, target.length() - 2);
        if(i - start > 1) {
          IToken newToken = new TokenCollection(token, start, i) ;
          setEntityTag(entityType, newToken) ;
          holder.add(newToken);
        } else {
          setEntityTag(entityType, token[start]) ;
          holder.add(token[start]);
        }
      } else {
        holder.add(token[i]) ;
        i++ ;
      }
    }
    return holder.toArray(new IToken[holder.size()]);
  }
  
  public String[] tags(IToken[] token) throws TokenException {
    if(token.length == 0) return null;
    TokenFeatures[] tfeatures = featuresGenerator.generate(token, false) ; 
    String [] outcomes = getOutcomes(tfeatures);
    return outcomes;
  }
  
  private String[] getOutcomes(TokenFeatures[] tFeatures){
    List<String> outcomes = new ArrayList<String>();
    for(TokenFeatures tfeature: tFeatures){
      double[] ocs = model.eval(tfeature.getFeatures());
      String target = model.getBestOutcome(ocs);
      outcomes.add(target);
    }    
    return outcomes.toArray(new String[outcomes.size()]);
  }
  
  protected void setEntityTag(String type, IToken token) {
    token.add(new EntityTag(type, token.getOriginalForm())) ;
  }
}