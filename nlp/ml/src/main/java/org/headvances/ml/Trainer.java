package org.headvances.ml;

public interface Trainer {
  public void train(String dataDir, String dictFile, String modelFile) throws Exception;
}
