package org.headvances.nlp.addtone;

import org.junit.Test;

public class AddToneUnitTest {
  @Test
  public void test() throws Exception {
    String text = "Tren bau troi chi co 2 ngoi sao"; 
    AddTone tone = new AddTone();
    String output = tone.addTextSegment(text);
    System.out.println(output);
  }
}
