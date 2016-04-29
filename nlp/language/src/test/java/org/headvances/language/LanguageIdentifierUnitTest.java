package org.headvances.language;

import java.io.File;
import java.io.PrintWriter;

import junit.framework.Assert;

import org.apache.hadoop.conf.Configuration;
import org.apache.nutch.analysis.lang.LanguageIdentifier;
import org.apache.nutch.analysis.lang.NGramProfile;
import org.headvances.util.FileUtil;
import org.headvances.util.IOUtil;
import org.junit.Test;

public class LanguageIdentifierUnitTest {
  @Test
  public void test() {
  	LanguageIdentifier identifier = new LanguageIdentifier(new Configuration()) ;
  	String[] VI_TEXT = {
  			"Chá»‰nh sá»­a áº£nh bá»‹ lá»—i cĂ¢n báº±ng tráº¯ng",
  			"Sau trĂ ng phĂ¡o tay ná»“ng nhiá»‡t cá»§a Thá»§ tÆ°á»›ng Nguyá»…n Táº¥n DÅ©ng vĂ  gáº§n 4.000 ngÆ°á»�i táº¡i Trung tĂ¢m há»™i nghá»‹ quá»‘c gia",
  			"LĂ  sinh viĂªn TrÆ°á»�ng Ä�áº¡i há»�c Paris VI (UniversitĂ© Pierre et Marie Curie)",
  			"Trung tĂ¢m NghiĂªn cá»©u Khoa há»�c Quá»‘c gia PhĂ¡p (CNRS) tá»« nÄƒm 1998, láº¥y báº±ng Habilitation Ă  Diriger les Recherches",
  			"khi Ä‘Æ°á»£c 33 tuá»•i, NgĂ´ Báº£o ChĂ¢u Ä‘Æ°á»£c nhĂ  nÆ°á»›c Viá»‡t Nam phong Ä‘áº·c cĂ¡ch hĂ m giĂ¡o sÆ°",
  			"NÄƒm 2007, Ă´ng Ä‘á»“ng thá»�i lĂ m viá»‡c táº¡i TrÆ°á»�ng Ä�áº¡i há»�c Paris XI",
  			"Viá»‡n nghiĂªn cá»©u cao cáº¥p Princeton, New Jersey, Hoa Ká»³",
  			"Trong nÄƒm 2008, Ă´ng cĂ´ng bá»‘ chá»©ng minh Bá»• Ä‘á»� cÆ¡ báº£n cho cĂ¡c Ä‘áº¡i sá»‘ Lie",
  			"hay cĂ²n gá»�i lĂ  Bá»• Ä‘á»� cÆ¡ báº£n Langlands",
  			"Cuá»‘i nÄƒm 2009, cĂ´ng trĂ¬nh nĂ y Ä‘Ă£ Ä‘Æ°á»£c táº¡p chĂ­ Time",
  			"bĂ¬nh chá»�n lĂ  1 trong 10 phĂ¡t minh khoa há»�c tiĂªu biá»ƒu cá»§a nÄƒm 2009"
  	} ;
  	
  	String[] EN_TEXT = {
  			"The 2 letter ISO 639 language code (en, fi, sv, ...) of the language that best matches the specified content.",
  			"Oracle is reviewing the Sun product roadmap",
  			"and will provide guidance to customers in accordance with Oracle's standard product communication policies",
  			"Any resulting features and timing of release of such features as determined by Oracle's review of roadmaps,",
  			"are at the sole discretion of Oracle.",
  			"All product roadmap information, whether communicated by Sun Microsystems or by Oracle",
  			"does not represent a commitment to deliver any material,",
  			"code, or functionality, and should not be relied upon in making purchasing decisions.",
  			"It is intended for information purposes only",
  			"and may not be incorporated into any contract."	
  	} ;
  	
  	for(int i = 0; i < VI_TEXT.length; i++) {
  		Assert.assertEquals("vi", identifier.identify(VI_TEXT[i])) ;
  	}
  	for(int i = 0; i < EN_TEXT.length; i++) {
  		Assert.assertEquals("en", identifier.identify(EN_TEXT[i])) ;
  	}
  }
  
  //@Test
  public void generatevi() throws Exception {
  	//http://wiki.apache.org/nutch/LangIdentifier
  	//command java org.apache.nutch.analysis.lang.NGramProfile -create <profile-name> <filename> <encoding>
  	String[] file = FileUtil.findFiles("d:/data-vn/traindata/vi", ".*\\.txt") ;
  	PrintWriter writer = new PrintWriter(new File("target/data.sample"), "UTF-8") ;
  	for(String sel : file) {
  		writer.append(IOUtil.getFileContentAsString(sel, "UTF-8")) ;
  		writer.append("\n\n") ;
  	}
  	String[] args = { "-create", "vi", "target/data.sample", "UTF-8" } ;
  	NGramProfile.main(args) ;
  	System.out.println("Generate vi.ngp profile to src/main/resources/org/apache/nutch/analysis/lang/vi.ngp") ;
  }
}