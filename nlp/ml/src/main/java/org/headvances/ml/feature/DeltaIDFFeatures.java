package org.headvances.ml.feature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DeltaIDFFeatures {
  private Map<String, Label> labels = new HashMap<String, Label>();
  static int docIdTracker;

  public void add(GenerateFeature[] gfeatures, String labelName) {
    if(labels.get(labelName) == null) labels.put(labelName, new Label(labelName));

    Label label = labels.get(labelName);
    for(GenerateFeature sel : gfeatures) {
     label.addFeature(sel); 
    }
  }

  public Feature[] getFeatures(){
    updateWeightInLabel();
    Map<String, Feature> features = new HashMap<String, Feature>();
    Iterator<Label> itr = labels.values().iterator();
    while(itr.hasNext()){
      Label label = itr.next();
      System.out.println(label.getLabelName() + " start");
      int count = 0;
      System.out.print("Evaluating ");
      long start = System.currentTimeMillis();
      Iterator<DeltaFeature> fItr = label.getFeatures().values().iterator();
      while(fItr.hasNext()){
        DeltaFeature feature = fItr.next();
        Feature f = features.get(feature.getFeature());
        if(f == null) {
//          f = new Feature(feature.getFeature());
//          f.setTermFrequency(feature.getFrequency());
          f = createStandardFeature(feature);
          if(f.getWeight() > 0)
            features.put(f.getFeature(), f);
        } else {
          int freq = f.getTermFrequency();
          f.setTermFrequency(freq + feature.getFrequency()); 
        }
        if(count % 1000 == 0) System.out.print(".");
        count++;
      }
      System.out.println();
      System.out.println("Time: " + (System.currentTimeMillis() - start));
      System.out.println("**************************************");

    }
    System.out.println(features.size());
    return features.values().toArray(new Feature[features.size()]);
  }
  
  public Feature getFeature(String labelname, String featureId){
    Feature feature = null;
    Label label = labels.get(labelname);
    if(label != null) {
      DeltaFeature f = label.getFeatures().get(featureId);
      feature = new Feature(f.getFeature());
      feature.setWeight(f.getDeltaIdf());
    }
    return feature;
  }

  private void updateWeightInLabel(){
    Iterator<Label> itr = labels.values().iterator();
    while(itr.hasNext()){
      Label label = itr.next();
      Iterator<DeltaFeature> fItr = label.getFeatures().values().iterator();
    
      int N1 = label.getNumOfDoc();
      int N2 = getNumOfDocTracker() - N1; 
//      System.out.println(label.getLabelName() + " " + label.getFeatures().size() + " " + N1 
//          + " " + N2 + " " + getTotalDocInOtherLabel(label.getLabelName()));
      while(fItr.hasNext()){
        DeltaFeature feature = fItr.next();
        int df1 = feature.getIdocs().size();
        int df2 = getNumOfDocByFeature(feature.getFeature()) - df1;

        double numerator = (double) N1 * df2 + 0.5;
        double denominator = (double) N2 * df1 + 0.5;

        float weight = (float) (Math.log((double) numerator / denominator));
        feature.setDeltaIdf(-weight);
      }
    }
  }
  
  private Feature createStandardFeature(DeltaFeature feature){
    Feature f = new Feature(feature.getFeature());
    float weight = 0;
    int docFrequency  = 0;
    Iterator<Label> itr = labels.values().iterator();
    while(itr.hasNext()){
      Label label = itr.next();
      if(label.getFeatures().get(feature.getFeature()) == null) continue;
      weight += label.getProbality() * feature.getDeltaIdf();
      docFrequency += label.getNumOfDocByFeature(feature.getFeature());
      //System.out.println(label.getLabelName() + " " + weight + " " + docFrequency);
    }
    f.setWeight(weight);
    f.setDocFrequency(docFrequency);
    f.setTermFrequency(feature.getFrequency());
    return f;
  }
  
  private int getNumOfDocByFeature(String featureId){
    int num = 0;
    Iterator<Label> itr = labels.values().iterator();
    while(itr.hasNext()){
      Label label = itr.next();
      num += label.getNumOfDocByFeature(featureId);
    }
    return num;
  }
  
  static public int getNumOfDocTracker(){ return docIdTracker; }
  public void setNumOfDocTracker(int num){ docIdTracker = num; }

  static class Label {
    private String labelName;
    private Map<String, DeltaFeature> features = new HashMap<String, DeltaFeature>();

    public Label(String label){ this.labelName = label; }

    public String getLabelName() {  return labelName; }
    public void setLabelName(String labelName) { this.labelName = labelName; }

    public Map<String, DeltaFeature> getFeatures() { return features; }
    public void setFeatures(Map<String, DeltaFeature> features) { this.features = features; }
    public void addFeature(GenerateFeature sel){
      DeltaFeature feature = features.get(sel.getFeature());
      if(feature == null){
        feature = new DeltaFeature(sel.getFeature());
        feature.addIdoc(new IDocument(sel.getDocId(), sel.getFrequency()));
        features.put(feature.getFeature(), feature);
      } else {feature.addIdoc(new IDocument(sel.getDocId(), sel.getFrequency())); }
    }

    public int getNumOfDocByFeature(String featureId){
      DeltaFeature feature = features.get(featureId);
      if(feature == null) return 0;
      else return feature.getIdocs().size();
    }
    
    public float getProbality(){
      return (float) getNumOfDoc()/docIdTracker;
    }
    
    private int getNumOfDoc(){
      Set<String> docs = new HashSet<String>();
      Iterator<DeltaFeature> itr = features.values().iterator();
      while(itr.hasNext()){
        for(IDocument idoc: itr.next().getIdocs()) docs.add( "" + idoc.getDocId());
      }
      return docs.size();
    }
  } 

  static class DeltaFeature{
    private String feature;
    private float deltaIdf;
    private List<IDocument> idocs = new ArrayList<IDocument>();

    public DeltaFeature(String feature){ this.feature = feature; }

    public String getFeature() { return feature; }
    public void setFeature(String feature) { this.feature = feature; }
    
    public int getFrequency() { 
      int frequency = 0;
      for(IDocument idoc: idocs) frequency += idoc.getFrequency();
      return frequency; 
    }

    public float getDeltaIdf(){ return this.deltaIdf; }
    public void setDeltaIdf(float deltaIdf){ this.deltaIdf = deltaIdf; }
    
    public List<IDocument> getIdocs() { return idocs; }
    public void setIdocs(List<IDocument> idocs) { this.idocs = idocs; }
    public void addIdoc(IDocument idoc){ idocs.add(idoc); }
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
