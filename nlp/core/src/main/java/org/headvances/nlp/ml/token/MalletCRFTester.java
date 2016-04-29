package org.headvances.nlp.ml.token;

import java.text.DecimalFormat;

import org.headvances.nlp.NLPResource;
import org.headvances.util.TimeReporter;
import org.headvances.util.text.TabularPrinter;

import cc.mallet.fst.CRF;
import cc.mallet.fst.MaxLatticeDefault;
import cc.mallet.fst.Transducer;
import cc.mallet.types.Instance;
import cc.mallet.types.LabelAlphabet;
import cc.mallet.types.Sequence;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class MalletCRFTester extends MLTester {
	private TokenFeaturesGenerator generator ;
	private Transducer classifier ;
	private MalletCRFFeatureVectorFactory factory ;
	
  private TimeReporter timeReporter;
  private TimeReporter.Time testTime ;
  
  private MLTestLog  allLog ;
  private Appendable out = System.out ;
  private boolean printToken = false ;
  
	public MalletCRFTester(TokenFeaturesGenerator generator, String modelRes) throws Exception {
		this.generator = generator ;
		if(modelRes.indexOf(':') < 0) modelRes = "file:" + modelRes ;
    CRF crf = NLPResource.getInstance().getObject(modelRes) ;
    this.classifier = crf ;
    this.factory = new MalletCRFFeatureVectorFactory(crf.getInputAlphabet(), (LabelAlphabet) crf.getOutputAlphabet()) ;
    timeReporter = new TimeReporter() ;
    testTime = timeReporter.getTime("Mallet CRF: Test");
	}
	
	public MLTestLog test(String wtagFile) throws Exception {
		TokenFeatures[] tokenFeatures = generator.generateWTagFile(wtagFile) ;
		Instance instance = factory.createDecodeInstance(tokenFeatures) ;
		Sequence input = (Sequence) instance.getData();
		Sequence[] outputs = apply(classifier, input, /*nBestOption.value*/ 1);
		for (int a = 0; a < outputs.length; a++) {
			if(outputs[a].size() != input.size()) {
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
				tokenFeatures[j].getToken(), target, expectTarget
			};
			if(printToken) tabularPrinter.printRow(column, false) ;
		}
		DecimalFormat formater = new DecimalFormat("#.00%") ;
		String ratioStr = formater.format((double)correctCount/tokenFeatures.length) ;
		out.append("Correct ratio: " + correctCount + "/" + tokenFeatures.length + " " + ratioStr + "\n");
		out.append("-------------------------------------------------------------------\n");
		return new MLTestLog(tokenFeatures.length, correctCount, tokenFeatures.length - correctCount) ;
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
}