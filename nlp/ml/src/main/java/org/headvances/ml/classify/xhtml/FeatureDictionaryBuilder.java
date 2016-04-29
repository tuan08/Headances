package org.headvances.ml.classify.xhtml;

import java.util.Arrays;

import org.headvances.data.Document;
import org.headvances.html.dom.TDocument;
import org.headvances.json.JSONMultiFileReader;
import org.headvances.ml.classify.xhtml.feature.ChiSquareFeatureCollector;
import org.headvances.ml.classify.xhtml.feature.FeatureCollector;
import org.headvances.ml.classify.xhtml.feature.IDFFeatureCollector;
import org.headvances.ml.feature.Feature;
import org.headvances.ml.feature.FeatureDictionary;
import org.headvances.ml.feature.FeatureGenerator;
import org.headvances.util.CommandParser;
import org.headvances.util.FileUtil;

public class FeatureDictionaryBuilder {
  private ChiSquareFeatureCollector chiSquareCollector ;
  private IDFFeatureCollector       idfCollector ;
  private FeatureDictionary         chisquareDictionary ;
  private FeatureDictionary         idfDictionary ;
  
  public FeatureDictionaryBuilder(FeatureGenerator<TDocument> generator) throws Exception{
    chiSquareCollector = new ChiSquareFeatureCollector(generator);
    idfCollector = new IDFFeatureCollector(generator);
  }

  public void setThreshold(double threshold){ {
  	chiSquareCollector.setThreshold(threshold); }
  }

  public void process(String dataDir) throws Exception {
    JSONMultiFileReader reader = new JSONMultiFileReader(dataDir);
    Document doc;
    System.out.print("Collect Feature ") ;
    int count = 0;
    while((doc = reader.next(Document.class)) != null) {
    	if(!acceptDocument(doc)) continue ;
    	TDocument tdoc = TDocument.create(doc) ;
    	String labelName = FeatureCollector.getLabel(doc);
    	chiSquareCollector.collect(tdoc, labelName);
      idfCollector.collect(tdoc, labelName);
      count++ ;
      if(count % 1000 == 0) {
        System.out.print('.') ;
      }
    }
    reader.close();
    System.out.println() ;
  }
  
  public FeatureDictionary getChisquareDictionary(){
  	if(chisquareDictionary == null) {
  		FeatureDictionary idfDict       = getIDFDictionary() ;
  		chisquareDictionary = chiSquareCollector.getFeatureDictionary() ;
  		chisquareDictionary.resetFeatureParameter(idfDict) ;
  		chisquareDictionary.resetFeatureId() ;
  	}
    return chisquareDictionary ;
  }
  
  public FeatureDictionary getIDFDictionary(){
  	if(idfDictionary == null) {
  		idfDictionary = idfCollector.getFeatureDictionary() ;
  		idfDictionary.resetFeatureId() ;
  	}
  	return idfDictionary ;
  }

  public FeatureDictionary getTopIDFDictionary(float topFeatures){
  	FeatureDictionary idfDict       = idfCollector.getFeatureDictionary() ;
  	FeatureDictionary retDict = new FeatureDictionary() ;
  	retDict.setLabels(idfDict.getLabels()) ;
  	Feature[] features = idfDict.getFeatures() ;
  	Arrays.sort(features, Feature.WEIGHT_COMPARATOR) ;
  	for(int i = 0; i < features.length; i++) {
  		retDict.addFeature(features[i]) ;
  		float progress = i/(float)features.length ;
  		if(progress > topFeatures) break ;
  	}
  	retDict.resetFeatureId() ;
  	return retDict ;
  }

  static public boolean acceptDocument(Document doc) {
  	if(doc.hasLabel("content:error") || doc.getContent().length() < 1000) return false ; 
  	return true ;
  }
  
  static public void main(String[] args) throws Exception {
  	if(args.length == 0) {
  		args = new String[] {
  				"-data", "e:/label-data"
  		};
  	}
    CommandParser command = new CommandParser("feature:") ;
    command.addMandatoryOption("data", true, "The data file in json format") ;
    command.addOption("outdir", true, "The output data dir") ;
    if(!command.parse(args)) return ;
    command.printHelp() ;
    
    String dataDir = command.getOption("data", null) ;
    String outdir = command.getOption("outdir", "target/dictionary") ;
    FileUtil.mkdirs(outdir) ;
    
  	FeatureGenerator<TDocument> generator = new DocumentFeatureGenerator(); 
    FeatureDictionaryBuilder dictBuilder  = new FeatureDictionaryBuilder(generator);
    dictBuilder.setThreshold(0.1f) ;
    dictBuilder.process(dataDir) ;
    FeatureDictionary chisquare = dictBuilder.getChisquareDictionary() ;
    chisquare.save(outdir + "/chisquare.dict.json") ;
    chisquare.printFeatures(outdir + "/chisquare.features", Feature.DOC_FRQ_COMPARATOR) ;
    
    FeatureDictionary idf = dictBuilder.getIDFDictionary() ;
    idf.save(outdir + "/idf.dict.json") ;
    idf.printFeatures(outdir + "/idf.features", Feature.DOC_FRQ_COMPARATOR) ;
  }
}