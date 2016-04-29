package org.headvances.ml.nlp;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.headvances.ml.nlp.feature.MalletFeatureVectorFactory;
import org.headvances.ml.nlp.feature.TokenFeatures;
import org.headvances.ml.nlp.feature.TokenFeaturesGenerator;
import org.headvances.nlp.NLPResource;
import org.headvances.nlp.token.IToken;
import org.headvances.nlp.token.TokenCollection;
import org.headvances.nlp.wtag.WTagDocumentSet;
import org.headvances.nlp.wtag.WTagDocumentReader;
import org.headvances.util.ConsoleUtil;
import org.headvances.util.IOUtil;
import org.headvances.util.TimeReporter;
import org.headvances.util.text.TabularPrinter;

import cc.mallet.fst.CRF;
import cc.mallet.fst.CRFTrainerByThreadedLabelLikelihood;
import cc.mallet.fst.MEMM;
import cc.mallet.fst.MEMMTrainer;
import cc.mallet.fst.MaxLatticeDefault;
import cc.mallet.fst.Transducer;
import cc.mallet.types.Alphabet;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.LabelAlphabet;
import cc.mallet.types.Sequence;
/**
 * $Author: Tuan Nguyen$ 
 **/
abstract public class MalletML implements ML {
	private String algorithm = "crf";
	private String modelRes ;
	private TokenFeaturesGenerator featuresGenerator ;
	private Transducer classifier ;
	private MalletFeatureVectorFactory factory ;
	
  private TimeReporter timeReporter;

	public MalletML() { }
	
	public MalletML(String modelRes, boolean loadModel) throws Exception {
	  init(modelRes, loadModel) ;
	}
	
	protected void init(String modelRes, boolean loadModel) throws Exception {
    this.modelRes = modelRes ;
    this.featuresGenerator = createTokenFeaturesGenerator() ;
    if(loadModel) {
      if(modelRes.indexOf(':') < 0) modelRes = "file:" + modelRes ;
      CRF crf = NLPResource.getInstance().getObject(modelRes) ;
      this.classifier = crf ;
      this.factory = new MalletFeatureVectorFactory(crf.getInputAlphabet(), (LabelAlphabet) crf.getOutputAlphabet(), featuresGenerator) ;
    }
  }
	
	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm ;
	}
	
	public void setTimeReporter(TimeReporter timeReporter){ this.timeReporter = timeReporter; }

	abstract protected TokenFeaturesGenerator createTokenFeaturesGenerator() throws Exception ;

	public void train(String[] samples, String modelRes, int iteration) throws Exception {
	  TimeReporter.Time trainTime = timeReporter.getTime("MalletCRFML: Train");
	  trainTime.start();
	  
		MalletFeatureVectorFactory factory = new MalletFeatureVectorFactory(featuresGenerator) ;
		InstanceList trainingData = 
			new InstanceList(factory.getDataAlphabet(), factory.getTargetAlphabet());
		WTagDocumentReader docReader = featuresGenerator.getDocumentReader() ;
		for(String file : samples) {
			System.out.println("Add Train Sample: " + file);
			String sampleData = IOUtil.getFileContentAsString(file, "UTF-8") ;
			if(sampleData.trim().length() == 0) continue ;
			TokenCollection[] collection = docReader.read(sampleData) ;
			for(int i = 0; i < collection.length; i++) {
				IToken[] token = collection[i].getTokens() ;
				if(token.length == 0) continue ;
				trainingData.add(factory.createTrainInstance(token)) ;
			}
		}

		System.out.println("Statistic of features in training data: " + factory.getDataAlphabet().size());
		System.out.println("Statistic of predicates: " + factory.getDataAlphabet().size());
		Alphabet targetLabels = factory.getTargetAlphabet();
		StringBuilder buf = new StringBuilder("Target Labels:");
		for (int i = 0; i < targetLabels.size(); i++) {
			buf.append(" ").append(targetLabels.lookupObject(i).toString());
		}
		System.out.println(buf.toString());

		Transducer classifier = null;
		if("crf".equals(algorithm)) {
			classifier = trainCRF(trainingData, iteration);
		} else if("memm".equals(algorithm)) {
			classifier = trainMEMM(trainingData, iteration);
		}

		ObjectOutputStream s = new ObjectOutputStream(new FileOutputStream(modelRes));
		s.writeObject(classifier);
		s.close();
		
		trainTime.stop();
	}

	private CRF trainCRF(InstanceList training, int iterration) {
		CRF crf = new CRF(training.getDataAlphabet(), training.getTargetAlphabet());

		Pattern forbiddenPat = null ;//Pattern.compile("\\s");
		Pattern allowedPat = null; //Pattern.compile(".*");
		String startName =
			crf.addOrderNStates(training, /*order*/new int[]{1}, null, /*defaultLabel*/ "O", forbiddenPat, allowedPat, /*connected*/ true);
		for (int i = 0; i < crf.numStates(); i++) {
			crf.getState(i).setInitialWeight(Transducer.IMPOSSIBLE_WEIGHT);
		}
		crf.getState(startName).setInitialWeight(0.0);
		System.out.println("Training on " + training.size() + " instances");

		CRFTrainerByThreadedLabelLikelihood crfTrainer = 
			new CRFTrainerByThreadedLabelLikelihood(crf,/*numThreads*/ 1);
		crfTrainer.setGaussianPriorVariance(10.0);

		crfTrainer.setUseSparseWeights(true);
		crfTrainer.setUseSomeUnsupportedTrick(true);
		crfTrainer.train (training, iterration);
		crfTrainer.shutdown();
		return crf;
	}

	private MEMM trainMEMM(InstanceList training, int iterration) {
		MEMM memm = new MEMM(training.getDataAlphabet(), training.getTargetAlphabet());
		memm.addFullyConnectedStatesForLabels();
		memm.setWeightsDimensionAsIn(training);
		
		MEMMTrainer memmt = new MEMMTrainer(memm);
		memmt.train(training, iterration);  // Set weights dimension, gathers training sets, etc.
		return memm ;
	}

	public MLTestLog test(String[] file, boolean printToken) throws Exception {
	  TimeReporter.Time testTime = timeReporter.getTime("MalletCRFML: Test");
	  testTime.start();
	  
		PrintStream out = ConsoleUtil.getUTF8SuportOutput() ;
		MLTestLog allLog = new MLTestLog(0, 0, 0) ;
		for(String sel : file) {
			MLTestLog log  = test(out, sel, printToken) ;
			allLog.merge(log) ;
		}
		DecimalFormat formater = new DecimalFormat("#.00%") ;
		double ratio = (double)allLog.hit/allLog.token ;
		System.out.println("Correct Ratio For All: " + formater.format(ratio) + ", " + allLog.hit + "/" + allLog.token);
		
		testTime.stop();
		return allLog ;
	}

	public MLTestLog test(PrintStream out, String file, boolean printToken) throws Exception {
		out.println("Test File: " + file);
		out.println("Statistic of predicates: " + factory.getDataAlphabet().size());
		String sample = IOUtil.getFileContentAsString(file, "UTF-8") ;
		if(sample.trim().length() == 0) return new MLTestLog(0, 0, 0);
		TokenCollection[] collection = featuresGenerator.getDocumentReader().read(sample) ;
		MLTestLog allLog = new MLTestLog(0,0,0) ;
		for(int i = 0; i < collection.length; i++) {
			IToken[] token = collection[i].getTokens() ;
			MLTestLog log = test(out, file, token, printToken) ;
			allLog.merge(log) ;
		}
		return allLog ;
	}
	
	public MLTestLog testText(PrintStream out, String sample, boolean printToken) throws Exception {
		out.println("Statistic of predicates: " + factory.getDataAlphabet().size());
		if(sample.trim().length() == 0) return new MLTestLog(0, 0, 0);
		TokenCollection[] collection = featuresGenerator.getDocumentReader().read(sample) ;
		MLTestLog allLog = new MLTestLog(0,0,0) ;
		for(int i = 0; i < collection.length; i++) {
			IToken[] token = collection[i].getTokens() ;
			MLTestLog log = test(out, "text", token, printToken) ;
			allLog.merge(log) ;
		}
		return allLog ;
	}

	public MLTestLog test(PrintStream out, String file, IToken[] token, boolean printToken) throws Exception {
		if(token.length == 0) return new MLTestLog(0, 0, 0) ;
		TokenFeatures[] tokenFeatures = featuresGenerator.createValueTarget(token) ;
		Instance instance = factory.createDecodeInstance(token) ;
		
		Sequence input = (Sequence) instance.getData();
		Sequence[] outputs = apply(classifier, input, /*nBestOption.value*/ 1);
		for (int a = 0; a < outputs.length; a++) {
			if (outputs[a].size() != input.size()) {
				System.out.println("Failed to decode input sequence " + file + ", answer " + a);
				return new MLTestLog(0, 0, 0) ;
			}
		}

		int correctCount = 0 ;
		int[] width = {30, 30, 30} ;
		TabularPrinter tabularPrinter = new TabularPrinter(out, width) ;
		for (int j = 0; j < input.size(); j++) {
			String target = outputs[0].get(j).toString() ;
			String expectTarget = tokenFeatures[j].getTargetFeature() ; 
			if(target.equals(tokenFeatures[j].getTargetFeature()))  {
				correctCount++ ;
			} else {
				expectTarget += " X";
			}
			String[] column = {
					tokenFeatures[j].getFeatures()[0],
					target,
					expectTarget
			};
			if(printToken) tabularPrinter.printRow(column, false) ;
		}
		DecimalFormat formater = new DecimalFormat("#.00%") ;
		String ratioStr = formater.format((double)correctCount/tokenFeatures.length) ;
		out.println("Correct ratio: " + correctCount + "/" + tokenFeatures.length + " " + ratioStr);
		out.println("-------------------------------------------------------------------");
		return new MLTestLog(token.length, correctCount, token.length - correctCount) ;
	}

	private Sequence[] apply(Transducer model, Sequence input, int nBest) {
		Sequence[] answers;
		if (nBest == 1) {
			answers  = new Sequence[] {model.transduce (input) };
		} else {
			MaxLatticeDefault lattice = new MaxLatticeDefault (model, input, null, /*cache-size*/ 100000);
			answers = lattice.bestOutputSequences(nBest).toArray(new Sequence[0]);
		}
		return answers;
	}

	public void crossValidation(String sample, double splitRatio, int iteration) throws Exception {
	  TimeReporter.Time validationTime = timeReporter.getTime("MalletCRFML: Cross-validtion");
	  validationTime.start(); 
	  
	  TimeReporter.Time splitTime = timeReporter.getTime("MalletCRFML: Data Spliting");
	  splitTime.start();
		WTagDocumentSet set = new WTagDocumentSet(sample, ".*\\.(tagged|wtag)") ;
		List<String> trainFiles = new ArrayList<String>() ;
		List<String> testFiles = new ArrayList<String>() ;
		String[] files = set.getFiles() ;
		for(int i = 0; i < files.length; i++) {
			double random = Math.random() ;
			if(random < splitRatio) testFiles.add(files[i]) ;
			else trainFiles.add(files[i]) ;
		}
		splitTime.stop();
		
		train(trainFiles.toArray(new String[trainFiles.size()]), modelRes, iteration) ;

		CRF crf = NLPResource.getInstance().getObject("file:" + modelRes) ;
		this.classifier = crf ;
		this.factory = new MalletFeatureVectorFactory(crf.getInputAlphabet(), (LabelAlphabet) crf.getOutputAlphabet(), featuresGenerator) ;

		test(testFiles.toArray(new String[testFiles.size()]), false) ;
		validationTime.stop();
	}
}
