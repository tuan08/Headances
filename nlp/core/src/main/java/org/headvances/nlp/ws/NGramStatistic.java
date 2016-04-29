package org.headvances.nlp.ws;

import java.io.IOException;
import java.text.DecimalFormat;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class NGramStatistic {
  private String word;
  //private int    frequency;
  private float  distribution;
  
  private String nWord;
  //private int    nWordFrequency;
  private float  nWordDistribution;

  public NGramStatistic() {
  }

  public NGramStatistic(String word, String nword, int frequency, int nwordFreq, float dist, float nwordDist) {
    this.word = word;
    this.nWord = nword;
    //this.frequency = frequency;
    this.distribution = dist;
    this.nWordDistribution = nwordDist;
    //this.nWordFrequency = nwordFreq;
  }

  public String getWord() { return word; }
  public void   setWord(String gramWord) { this.word = gramWord; }
  
//  public int  getFrequency() { return frequency; }
//  public void setFrequency(int numFrequency) { this.frequency = numFrequency ; }
  
  @JsonSerialize(using = DistributionSerializer.class)
  public float getDistribution() { return distribution; }
  public void setDistribution(float distribution) { this.distribution = distribution ; }
  
  public String getNWord() { return nWord ; }
  public void   setNWord(String gramWordNormalize) { 
  	this.nWord = gramWordNormalize; 
  }
  
//  public int getNWordFrequency() { return nWordFrequency; }
//  public void setNWordFrequency(int numFrequencyNormalize) {
//    this.nWordFrequency = numFrequencyNormalize;
//  }

  @JsonSerialize(using = DistributionSerializer.class)
  public float getNWordDistribution() { return nWordDistribution; }
  public void setNWordDistribution(float distributionNormalize) {
    this.nWordDistribution = distributionNormalize;
  }
  
  static public class DistributionSerializer extends JsonSerializer<Float> {
  	static DecimalFormat DECIMAL_FORMATER = new DecimalFormat("#.###") ;
  	public void serialize(Float value, JsonGenerator gen, SerializerProvider arg2) throws IOException, JsonProcessingException {
  		String formattedDate = DECIMAL_FORMATER.format(value);
  		gen.writeString(formattedDate);
  	}
  }
}