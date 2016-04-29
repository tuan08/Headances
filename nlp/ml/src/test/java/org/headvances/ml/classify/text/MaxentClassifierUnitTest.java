package org.headvances.ml.classify.text;

import java.util.Scanner;

import org.headvances.data.Document;
import org.headvances.html.dom.TDocument;
import org.headvances.html.dom.extract.DocumentExtractor;
import org.headvances.html.dom.extract.ExtractBlock;
import org.headvances.html.dom.extract.ExtractContent;
import org.headvances.html.fetcher.HttpClientFetcher;
import org.headvances.ml.Predict;
import org.junit.Test;

public class MaxentClassifierUnitTest {
  @Test
  public void test() throws Exception{

    MaxentTextClassifier classifier = new MaxentTextClassifier();
    HttpClientFetcher fetcher = new HttpClientFetcher();

    Scanner scanner = new Scanner(System.in);
    String url = "";

    while(!(url = scanner.nextLine()).equals("stop")){
      Document doc = fetcher.fetch(url);
      TextDocument tdoc = createTextDocument(doc);
      Predict best = classifier.predict(tdoc);
      System.out.println(best.label + " " + best.probability);
    }
  }

  public TextDocument createTextDocument(Document doc){
    TextDocument textdoc;
    DocumentExtractor extractor = new DocumentExtractor() ;
    TDocument tdoc = TDocument.create(doc);
    ExtractContent extractContent = extractor.extract(DocumentExtractor.Type.article, tdoc);
    ExtractBlock block = extractContent.getExtractBlock("mainContent");

    textdoc = new TextDocument(block.getTitle(), block.getDescription(), block.getContent());
    return textdoc;
  }

}
