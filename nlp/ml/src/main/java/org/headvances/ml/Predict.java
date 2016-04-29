package org.headvances.ml;

import java.io.PrintStream;

/**
 **/
public class Predict {
	final public String label ;
	final public double probability ;
	
	public Predict(String label, double probability) {
		this.label = label ;
		this.probability = probability ;
	}
	
	public void dump(PrintStream out) {
		out.append(label).append("[").append(Double.toString(probability)).append("]");
		out.println() ;
	}
	
	static public Predict getBestPredict(Predict[] predict) {
		Predict select = predict[0] ;
		for(int i = 1 ; i < predict.length; i++) {
			if(select.probability < predict[i].probability) {
				select = predict[i] ;
			}
		}
		return select ;
	}
	
	static public Predict[] mixPredicts(Predict[] predicts1, Predict[] predicts2, double alpha) {
	  Predict[] mixPredicts = new Predict[predicts1.length];
	  for (int i = 0; i < predicts1.length; i++){
	    for (Predict predict2 : predicts2){
	      if (predicts1[i].label.equals(predict2.label)) {	        
	        String lb = predicts1[i].label;
	        double score = alpha * predicts1[i].probability + (1 - alpha) * predict2.probability;
	        mixPredicts[i] = new Predict(lb, score);
	      }
	    } 
	  }
	  return mixPredicts;
	}
	
	static public void dump(PrintStream out, Predict[] predict) {
		Predict bestPredict = Predict.getBestPredict(predict) ;
		out.print("Best: "); 
		bestPredict.dump(out) ;
		for(int i = 0; i < predict.length; i++) {
			out.append(predict[i].label).
				append("[").append(Double.toString(predict[i].probability)).append("]");
			out.println() ;
		}
		
	}
}
