package org.headvances.ml.svm;

import java.io.IOException;
import java.util.List;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_problem;

public class SVMTrainer {
  private svm_parameter param;
  private svm_problem prob;
  private svm_model model;

  public SVMTrainer(List<svm_node[]> vx, List<Double> vy, int numFeatures) throws IOException {
    getParamater();
    this.prob = getProblem(vx, vy, numFeatures);
  }

  private void getParamater() {
    param = new svm_parameter();
    // default values
    param.svm_type = svm_parameter.C_SVC;
    param.kernel_type = svm_parameter.LINEAR;
    param.degree = 3;
    param.gamma = 0;
    param.coef0 = 0;
    param.nu = 0.5;
    param.cache_size = 100;
    param.C = 1;
    param.eps = 1e-3;
    param.p = 0.1;
    param.shrinking = 1;
    param.probability = 1;
    param.nr_weight = 0;
    param.weight_label = new int[0];
    param.weight = new double[0];

  }

  public void do_cross_validation(int nr_fold) {
    int i;
    int total_correct = 0;
    double total_error = 0;
    double sumv = 0, sumy = 0, sumvv = 0, sumyy = 0, sumvy = 0;
    double[] target = new double[prob.l];

    svm.svm_cross_validation(prob, param, nr_fold, target);
    if (param.svm_type == svm_parameter.EPSILON_SVR
        || param.svm_type == svm_parameter.NU_SVR) {
      for (i = 0; i < prob.l; i++) {
        double y = prob.y[i];
        double v = target[i];
        total_error += (v - y) * (v - y);
        sumv += v;
        sumy += y;
        sumvv += v * v;
        sumyy += y * y;
        sumvy += v * y;
      }
      System.out.print("Cross Validation Mean squared error = " + total_error
          / prob.l + "\n");
      System.out.print("Cross Validation Squared correlation coefficient = "
          + ((prob.l * sumvy - sumv * sumy) * (prob.l * sumvy - sumv * sumy))
          / ((prob.l * sumvv - sumv * sumv) * (prob.l * sumyy - sumy * sumy))
          + "\n");
    } else {
      for (i = 0; i < prob.l; i++)
        if (target[i] == prob.y[i])
          ++total_correct;
      System.out.print("Cross Validation Accuracy = " + 100.0 * total_correct
          / prob.l + "%\n");
    }
  }

  private svm_problem getProblem(List<svm_node[]> vx, List<Double> vy, int numFeatures) throws IOException {
    svm_problem problem = new svm_problem();
    problem.l = vy.size();
    problem.x = new svm_node[problem.l][];
    for (int i = 0; i < problem.l; i++) {
      problem.x[i] = vx.get(i);
    }
    problem.y = new double[problem.l];
    for (int i = 0; i < problem.l; i++) {
      problem.y[i] = vy.get(i);
    }
    if (param.gamma == 0 && numFeatures > 0) {
      param.gamma = 1.0 / numFeatures;
    }

    return problem;
  }

  public void train(String model_file_name) throws IOException {
    model = svm.svm_train(prob, param);
    svm.svm_save_model(model_file_name, model);
  }
}