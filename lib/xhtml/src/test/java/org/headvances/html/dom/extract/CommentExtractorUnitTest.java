package org.headvances.html.dom.extract;

import junit.framework.Assert;

import org.headvances.html.fetcher.Fetcher;
import org.headvances.html.fetcher.HttpClientFetcher;
import org.junit.Test;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class CommentExtractorUnitTest {
  static String[] EXPECT_TAG = { } ;

  static URLVerifier VNEXPRESS = new URLVerifier(
  	"",
  	"http://vnexpress.net/gl/vi-tinh/san-pham-moi/2011/09/nokia-n9-chinh-thuc-ra-mat-tai-viet-nam/",
  	DocumentExtractor.Type.article, EXPECT_TAG
  );
  
  static URLVerifier THEGIOIDIDONG = new URLVerifier(
    "ĐIỆN THOẠI DI ĐỘNG IPHONE 4 32GB",
    "http://thegioididong.vn/sieu-thi-dien-thoai-di-dong-apple,san-pham-42-80-49737-13,iphone-4-32gb.aspx",
    DocumentExtractor.Type.product, EXPECT_TAG
  );

  @Test
  public void test() throws Exception {
  	Fetcher fetcher = new HttpClientFetcher();
  	verify(fetcher, THEGIOIDIDONG) ;
  	//verify(fetcher, VNEXPRESS) ;
  }
  
  private void verify(Fetcher fetcher, URLVerifier verifier) throws Exception {
  	ExtractContent extract = verifier.extract(fetcher) ;
  	ExtractBlock comment = extract.getExtractBlock("comment") ;
  	Assert.assertNotNull(comment) ;
  	extract.dump(System.out) ;
  }
}