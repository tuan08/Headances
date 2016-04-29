package org.headvances.ml.feature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class IDFFeatures {
  private Map<String, IFeature> ifeatures = new LinkedHashMap<String, IFeature>() ;
  private int numOfDocId;

  public void add(GenerateFeature[] gfeatures, String labelName) {
    for(GenerateFeature sel : gfeatures) {
      String featureId = sel.getFeature() ;
      IFeature ifeature = ifeatures.get(featureId) ;
      if(ifeature == null) {
        ifeature = new IFeature(sel) ;
        ifeatures.put(ifeature.getFeature(), ifeature) ;
      }
      ifeature.add(sel.getDocId(), sel.getFrequency()) ;
    }
  }

  public Feature[] getFeatures() {
    updateIDF() ;
    Map<String, Feature> features = new HashMap<String, Feature>();
    int idTracker = 0;
    Iterator<IFeature> i = ifeatures.values().iterator() ;
    while(i.hasNext()) {
      IFeature ifeature = i.next() ;
      GenerateFeature gfeature = ifeature.getGenerateFeature() ;
      Feature feature = new Feature(gfeature.getFeature()) ;
      feature.setId(idTracker);
      feature.setType(gfeature.getType()) ;
      feature.setTermFrequency(ifeature.getTotalOccurence());
      feature.setDocFrequency(ifeature.getNumOfIdocs()) ;
      feature.setWeight(ifeature.getIdf());     
      
      features.put(feature.getFeature(), feature);
      idTracker++;
    }
    reset();
    return features.values().toArray(new Feature[features.size()]);
  }
  
  private void updateIDF() {
    Iterator<IFeature> i = ifeatures.values().iterator() ;
    while(i.hasNext()) {
      IFeature ifeature = i.next() ;
      ifeature.setIdf((float) Math.log10((double)numOfDocId/ifeature.getNumOfIdocs()));
    }
  }
  
  public void setNumOfDocId(int num){ this.numOfDocId = num;}

  public void reset() {
    ifeatures.clear() ;
  }

  static class IFeature {
    private GenerateFeature feature ;
    private List<IDocument> idocs = new ArrayList<IDocument>() ;
    private float idf ;

    public IFeature() {} ;

    public IFeature(GenerateFeature feature) {
      this.feature = feature ;
    }

    public GenerateFeature getGenerateFeature() { return this.feature ; }
    
    public String getFeature() { return this.feature.getFeature() ; }

    public void setIdf(float idf){ this.idf = idf;}
    public float getIdf(){ return this.idf; }

    public int getNumOfIdocs(){ return idocs.size(); }
  
    public int getTotalOccurence(){
      int num = 0;
      for(IDocument idoc: idocs){
        num += idoc.getFrequency();
      }
      return num;
    }

    public void add(int docId, int frequency) {
      idocs.add(new IDocument(docId, frequency)) ;
    }
  }

  static class IDocument {
    private int docId ;
    private int frequency ;

    public IDocument(int docId, int frequency) {
      this.docId = docId ;
      this.frequency = frequency ;
    }

    public int getDocId() { return docId; }
    public void setDocId(int docId) { this.docId = docId; }

    public int getFrequency() { return frequency; }
    public void setFrequency(int frequency) { this.frequency = frequency; }
  }
}
