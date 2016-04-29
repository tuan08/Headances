package org.headvances.html.url;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.headvances.data.Document;
import org.headvances.html.fetcher.HttpClientFetcher;
import org.headvances.html.util.HTMLUtils;
import org.junit.Test;

public class URLClusteringUnitTest {
  @Test
  public void testClustering() throws Exception {
    String url = "http://www.webtretho.com";
    HttpClientFetcher fetcher = new HttpClientFetcher();
    Document hdoc = fetcher.fetch(url);
    List<URLVector> urls = new ArrayList<URLVector>();

    Set<String> links = HTMLUtils.extractLinks(hdoc.getContent());
    for (String link : links) {
      urls.add(new URLVector(link));
    }

    URLClustering clustering = new URLClustering();
    Map<String, List<URLVector>> clusters = clustering.clustering(urls);
    for (Iterator it = clusters.keySet().iterator(); it.hasNext();) {
      String key = (String) it.next();
      List<URLVector> cluster = clusters.get(key);
      System.out.println("Centroid: " + key);
      for (URLVector element : cluster) {
        System.out.println(element.getURL());
      }
      System.out.println("================================================");
    }
  }
}
