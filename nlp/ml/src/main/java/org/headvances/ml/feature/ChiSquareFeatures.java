package org.headvances.ml.feature;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.headvances.json.JSONWriter;

public class ChiSquareFeatures {
  private Map<String, Label> labelStats = new LinkedHashMap<String, Label>();
  private int numOfDocId;

  public ChiSquareFeatures(){}

  public void add(GenerateFeature[] gfeatures, String labelName) {
    Label label = labelStats.get(labelName);
    if(label == null) label =  new Label(labelName);

    for(GenerateFeature sel : gfeatures){
      String clusterLabel = sel.getCategory();
      label.addFCluster(clusterLabel, new ChisquareFeature(clusterLabel, sel.getFeature(), sel.getFrequency()));
    }
    labelStats.put(labelName, label);
  }

  public Feature[] getFeatures(double threashold){
    getTopCluster(threashold);
    
    Map<String, Feature> features = new HashMap<String, Feature>();
    Iterator<Label> lItr = labelStats.values().iterator();
    int idx = 0;
    while(lItr.hasNext()) {
      Label label = lItr.next();
      Iterator<FeatureCluster> tItr = label.getFClusters().values().iterator();
      while(tItr.hasNext()){
        FeatureCluster fcluster = tItr.next();
        Iterator<ChisquareFeature> itr = fcluster.getFeatures().values().iterator();
        while(itr.hasNext()){
          ChisquareFeature feature = itr.next();

          if(features.get(feature.getFeature()) == null){
            Feature f = new Feature(feature.getFeature());
            double chi = getChiWeightOfFeatureOnScropus(feature);
            f.setId(idx++);
            f.setWeight((float) chi);
            features.put(f.getFeature(), f);
          }
        }
      }
    }
    reset();
    return features.values().toArray(new Feature[features.size()]);
  }

  private void updateChiWeight(){
    int totalFeatureOccurences = getTotalOccurencesOfAllFeatures();

    Iterator<Label> lItr = labelStats.values().iterator();
    while(lItr.hasNext()) {
      Label label = lItr.next();
      Iterator<FeatureCluster> tItr = label.getFClusters().values().iterator();
      while(tItr.hasNext()){
        FeatureCluster fcluster = tItr.next();
        Iterator<ChisquareFeature> itr = fcluster.getFeatures().values().iterator();
        while(itr.hasNext()){
          ChisquareFeature feature = itr.next();
          // Chi-Square Statistics: Chi(t, ci)
          //        - a: the frequency of times term t and class c co-occur, 
          //        - b: the frequency of times the term t occurs without class c, 
          //        - c: the frequency of times class c occurs without term t, 
          //        - d: the frequency of times neither class c nor term t, 
          //        - n: the total frequency of documents. 

          int a = feature.getFrequency();
          int b = getTotalOccurencesOfFeature(feature) - a;
          int c = label.getTotalOccurences() - a; 
          int d = totalFeatureOccurences - a - b - c;

          double numerator = ((double) numOfDocId)*(a*d - b*c)*(a*d - b*c);
          double denominator = ((double)(a + b))*(a + c)*(c + d)*(b + d);

          double chiWeight =  numerator/denominator;
          feature.setChiWeight(chiWeight);
        }
      }
      label.setProbability(totalFeatureOccurences);
    }
  }

  public double getChiWeightOfFeatureOnScropus(ChisquareFeature feature){
    double sum = 0;
    Iterator<Label> lItr = labelStats.values().iterator();
    while(lItr.hasNext()) {
      Label label = lItr.next();
      if(label.hasFeature(feature))
        sum += label.getProbability() * feature.getChiWeight();
    }
    return sum;
  }

  private void getTopCluster(double threashold){
    updateChiWeight();

    Iterator<Label> lItr = labelStats.values().iterator();
    while(lItr.hasNext()) {
      Label label = lItr.next();
      Iterator<FeatureCluster> tItr = label.getFClusters().values().iterator();
      while(tItr.hasNext()){
        FeatureCluster fcluster = tItr.next();
        fcluster.updateTopFeatures(threashold);
      }
    }
  }

  private int getTotalOccurencesOfFeature(ChisquareFeature feature){
    int total = 0;
    Iterator<Label> lItr = labelStats.values().iterator();
    while(lItr.hasNext()) {
      Label label = lItr.next();
      if(!label.hasFeature(feature)) continue;

      FeatureCluster fcluster = label.getFClusters().get(feature.getCluster());
      ChisquareFeature f = fcluster.getFeatures().get(feature.getFeature());
      total += f.getFrequency();
    }
    return total;
  }

  private int getTotalOccurencesOfAllFeatures(){
    int total = 0;
    Iterator<Label> lItr = labelStats.values().iterator();
    while(lItr.hasNext()) {
      Label label = lItr.next();
      total += label.getTotalOccurences();
    }
    return total;
  }
  
  public void setNumOfDocId(int num){ this.numOfDocId = num; }
  
  public void save(String file) throws Exception {
    JSONWriter writer = new JSONWriter(file) ;
    writer.write(this) ;
    writer.close() ;
  }
  
  public void reset(){ labelStats.clear(); }

  static class Label {
    private String label;
    private Map<String, FeatureCluster> fclusters = new LinkedHashMap<String, FeatureCluster>();
    private int totalOccurences   = 0; 
    private double probability = 0.0;

    public Label(String label){ this.label = label; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public Map<String, FeatureCluster> getFClusters() { return fclusters; }
    public int getTotalOccurences(){ return this.totalOccurences; }

    public void addFCluster(String clusterLabel, ChisquareFeature feature){
      FeatureCluster featureCluster = fclusters.get(clusterLabel);
      if(featureCluster == null){
        featureCluster = new FeatureCluster(clusterLabel);
        featureCluster.addFeature(feature);
        fclusters.put(clusterLabel, featureCluster);
      } else { featureCluster.addFeature(feature); }
      totalOccurences += feature.getFrequency();
    }

    public boolean hasFeature(ChisquareFeature feature){
      FeatureCluster fcluster = fclusters.get(feature.getCluster());
      if(fcluster != null){ return fcluster.hasFeature(feature);}
      return false;
    }

    public double getProbability(){ return this.probability;}
    public void setProbability(int totalOccurencesOfAllLabel){
      probability = (double) totalOccurences/totalOccurencesOfAllLabel;
    }
  }

  static class FeatureCluster {
    private String clusterLabel;
    private Map<String, ChisquareFeature> features = new LinkedHashMap<String, ChisquareFeature>();

    public FeatureCluster(String type) { this.clusterLabel = type; }

    public String getClusterLabel() { return clusterLabel; }
    public void setClusterLabel(String type) { this.clusterLabel = type; }

    public Map<String, ChisquareFeature> getFeatures() { return features; }

    public void updateTopFeatures(double threashold) {
      final int total = features.size();
      
      ChisquareFeature[] sortedFeatures = features.values().toArray(new ChisquareFeature[features.size()]);
      Arrays.sort(sortedFeatures, new FeatureComparator());

      int numOfFeatures = 0;
      for(int i = 0; i < sortedFeatures.length; i++){
        ChisquareFeature feature = sortedFeatures[i];
        if(feature.getFeature().contains("frequency") || feature.getFeature().contains("deep")) continue;
        if((float)numOfFeatures/total > threashold) { features.remove(feature.getFeature()); }
        numOfFeatures++;
      }
    }
    
    public void addFeature(ChisquareFeature feature) {
      ChisquareFeature f = features.get(feature.getFeature());
      if (f == null) { features.put(feature.getFeature(), feature); } 
      else { f.incrFrequency(feature.getFrequency()); }
    }

    public boolean hasFeature(ChisquareFeature feature){
      if(features.get(feature.getFeature()) != null) return true;
      return false;
    }

    private static class FeatureComparator implements Comparator<ChisquareFeature> {
      public int compare(ChisquareFeature f1, ChisquareFeature f2) {
        if (f1.getChiWeight() < f2.getChiWeight())  return 1;
        else if (f1.getChiWeight() > f2.getChiWeight())  return -1;
        else return  0 ;
      }
    }
  }

  static class ChisquareFeature {
    private String cluster;
    private String feature;
    private int frequency;
    private double chiWeight;

    public ChisquareFeature(String cluster, String feature, int freq) {
      this.cluster = cluster;
      this.feature = feature;
      this.frequency = freq;
    }

    public String getCluster() { return cluster; }
    public void setCluster(String cluster) { this.cluster = cluster; }

    public String getFeature() { return feature; }
    public void setFeature(String feature) { this.feature = feature; }

    public int getFrequency() { return frequency; }
    public void incrFrequency(int freq) { this.frequency += freq; }
    public void setFrequency(int frequency) { this.frequency = frequency; }

    public double getChiWeight() { return chiWeight; }
    public void setChiWeight(double chiWeight) { this.chiWeight = chiWeight;}
  }
}
