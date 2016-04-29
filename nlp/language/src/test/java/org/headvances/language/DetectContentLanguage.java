package org.headvances.language;

import org.apache.hadoop.conf.Configuration;
import org.apache.nutch.analysis.lang.LanguageIdentifier;
import org.headvances.util.IOUtil;
import org.junit.Test;

public class DetectContentLanguage {
  @Test
  public void test() throws Exception {
  	LanguageIdentifier identifier = new LanguageIdentifier(new Configuration()) ;
  	String text = IOUtil.getResourceAsString("file:///", "UTF-8") ;
  	System.out.println();
  }
}