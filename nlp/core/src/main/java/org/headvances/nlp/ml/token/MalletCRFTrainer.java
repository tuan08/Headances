package org.headvances.nlp.ml.token;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.lang.management.ManagementFactory;
import java.util.regex.Pattern;

import org.headvances.util.TimeReporter;

import cc.mallet.fst.CRF;
import cc.mallet.fst.CRFTrainerByThreadedLabelLikelihood;
import cc.mallet.fst.Transducer;
import cc.mallet.types.InstanceList;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class MalletCRFTrainer extends MLTrainer {
	private TokenFeaturesGenerator generator ;
	private MalletCRFFeatureVectorFactory factory ;
	private TimeReporter timeReporter;

	public MalletCRFTrainer(TokenFeaturesGenerator generator) throws Exception {
    this.generator = generator ;
		timeReporter = new TimeReporter() ;
    factory = new MalletCRFFeatureVectorFactory() ;
	}
	
	
	public void train(String[] file, String outputFile, int iteration) throws Exception {
		InstanceList trainingData = 
			new InstanceList(factory.getDataAlphabet(), factory.getTargetAlphabet());
		for(String selFile : file) {
			TokenFeatures[] tfeatures = generator.generateWTagFile(selFile) ;
			trainingData.add(factory.createTrainInstance(tfeatures)) ;
		}

		CRF crf = trainCRF(trainingData, iteration) ;
		ObjectOutputStream s = new ObjectOutputStream(new FileOutputStream(outputFile));
		s.writeObject(crf);
		s.close();
	}

	private CRF trainCRF(InstanceList training, int iterration) {
		TimeReporter.Time trainTime = timeReporter.getTime("Mallet CRF: Train");
	  trainTime.start();	  
	  
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

		Runtime runtime = Runtime.getRuntime();
		int nrOfCPU = runtime.availableProcessors();
     
		CRFTrainerByThreadedLabelLikelihood crfTrainer = 
			new CRFTrainerByThreadedLabelLikelihood(crf,/*numThreads*/nrOfCPU);
		crfTrainer.setGaussianPriorVariance(10.0);

		crfTrainer.setUseSparseWeights(true);
		crfTrainer.setUseSomeUnsupportedTrick(true);
		crfTrainer.train (training, iterration);
		crfTrainer.shutdown();
		trainTime.stop();
		return crf;
	}
}