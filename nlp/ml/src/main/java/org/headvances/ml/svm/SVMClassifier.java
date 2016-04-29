package org.headvances.ml.svm;

import org.headvances.ml.Predict;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;

public class SVMClassifier {
  private svm_model model;
  private int predict_probability = 0;
  private String[] slabels;

  public SVMClassifier(svm_model _model) {
    this.model = _model;
  }

  public SVMClassifier(svm_model _model, int _predict_probability, String[] labels) {
    this.model = _model;
    this.predict_probability = _predict_probability;
    this.slabels = labels;
  }

  public double classifier(svm_node[] x, double threshold) {
    int svm_type = svm.svm_get_svm_type(model);
    int nr_class = svm.svm_get_nr_class(model);
    double[] prob_estimates = null;
    int[] labels = null;
    if (predict_probability == 1) {
      if(svm_type == svm_parameter.EPSILON_SVR || svm_type == svm_parameter.NU_SVR) {
        System.out
            .print("Prob. model for test data: target value = predicted value + z,\nz: Laplace distribution e^(-|z|/sigma)/(2sigma),sigma="
                + svm.svm_get_svr_probability(model) + "\n");
      } else {
        labels = new int[nr_class];
        svm.svm_get_labels(model, labels);
        prob_estimates = new double[nr_class];
      }
    }

    // Show results
    double v;
    if (predict_probability == 1 && 
    	  (svm_type == svm_parameter.C_SVC || svm_type == svm_parameter.NU_SVC)) {
      v = svm.svm_predict_probability(model, x, prob_estimates);
      double max = 0.0;
      // Print probability of classes
      for (int j = 0; j < nr_class; j++) {
        System.out.print(labels[j] + ": " + prob_estimates[j] + " ");
        if(prob_estimates[j] > max) max = prob_estimates[j];
      }

      System.out.println("");
      // Print id of prediction class
      if (max > threshold) {
        System.out.println((int) v + ":" + slabels[(int) v]);
      } else {
        System.out.println("Try more analysis...");
      }

    } else {
      v = svm.svm_predict(model, x);
      System.out.println(v + " ");
    }
    return v;
  }
  
  public Predict[] classify(svm_node[] x) {
    int svm_type = svm.svm_get_svm_type(model);
    int nr_class = svm.svm_get_nr_class(model);
    double[] prob_estimates = null;
    int[] labels = null;
    if (predict_probability == 1) {
      if(svm_type == svm_parameter.EPSILON_SVR || svm_type == svm_parameter.NU_SVR) {
        System.out
            .print("Prob. model for test data: target value = predicted value + z,\nz: Laplace distribution e^(-|z|/sigma)/(2sigma),sigma="
                + svm.svm_get_svr_probability(model) + "\n");
      } else {
        labels = new int[nr_class];
        svm.svm_get_labels(model, labels);
        prob_estimates = new double[nr_class];
      }
    }
    if (predict_probability == 1 && 
    	  (svm_type == svm_parameter.C_SVC || svm_type == svm_parameter.NU_SVC)) {
      svm.svm_predict_probability(model, x, prob_estimates);
      Predict[] predict = new Predict[nr_class] ;
      for (int j = 0; j < nr_class; j++) {
          predict[j] = new Predict( slabels[labels[j]], prob_estimates[j]) ;
      }
      return predict ;
    } 
    return null ;
  }
}
