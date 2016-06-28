package org.headvances.crawler.fetch.http;

import info.monitorenter.cpdetector.io.ASCIIDetector;
import info.monitorenter.cpdetector.io.ByteOrderMarkDetector;
import info.monitorenter.cpdetector.io.CodepageDetectorProxy;
import info.monitorenter.cpdetector.io.JChardetFacade;
import info.monitorenter.cpdetector.io.ParsingDetector;
import info.monitorenter.cpdetector.io.UnicodeDetector;
import info.monitorenter.cpdetector.io.UnknownCharset;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.headvances.util.text.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EncodingDetector {
  private static final Logger logger = LoggerFactory.getLogger(EncodingDetector.class);
  static private EncodingDetector instance  ;

  private static ThreadLocal<EncodingDetector> localThread = new ThreadLocal<EncodingDetector>() {
    protected synchronized EncodingDetector initialValue() {
      EncodingDetector detector = new EncodingDetector() ;
      return detector ;
    }
  };


  private CodepageDetectorProxy detector  ;

  public EncodingDetector() {
    detector = CodepageDetectorProxy.getInstance() ;
    detector.add(new ByteOrderMarkDetector()) ;
    detector.add(new ParsingDetector(false)) ;
    detector.add(JChardetFacade.getInstance()) ;
    detector.add(ASCIIDetector.getInstance()) ;
    detector.add(UnicodeDetector.getInstance()) ;
  }

  public Charset detect(InputStream is, int length) throws IllegalArgumentException, IOException {
    Charset ret = detector.detectCodepage(is, length) ;
    if(ret == null || ret instanceof UnknownCharset) {
      logger.info("Cannot detect the encoding, use the defaut encoding utf-8");
      ret = StringUtil.UTF8 ;
    }
    return ret ;
  }

  synchronized static public EncodingDetector getInstance() { 
    if(instance == null) instance = new EncodingDetector() ;
    return instance ; 
  } 

  static public EncodingDetector getLocalThreadInstance() { return localThread.get() ; }
}
