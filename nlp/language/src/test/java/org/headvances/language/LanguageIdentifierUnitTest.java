package org.headvances.language;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import com.google.common.base.Optional;
import com.optimaize.langdetect.LanguageDetector;
import com.optimaize.langdetect.LanguageDetectorBuilder;
import com.optimaize.langdetect.i18n.LdLocale;
import com.optimaize.langdetect.ngram.NgramExtractors;
import com.optimaize.langdetect.profiles.LanguageProfile;
import com.optimaize.langdetect.profiles.LanguageProfileReader;

import junit.framework.Assert;

public class LanguageIdentifierUnitTest {
  @Test
  public void test() throws IOException {
    String[] VI_TEXT = {
        "Thủ tướng Chính phủ đồng ý về nguyên tắc UBND thành phố Hà Nội thực hiện đầu tư Dự án xây dựng tuyến đường " + 
        "Minh Khai - Vĩnh Tuy - Yên Duyên đoạn nối từ đường Minh Khai đến đường vành đai 2,5, thành phố Hà Nội theo " + 
        "hình thức Hợp đồng Xây dựng - Chuyển giao."
    } ;

    String[] EN_TEXT = {
        "The 2 letter ISO 639 language code (en, fi, sv, ...) of the language that best matches the specified content.",
        "Oracle is reviewing the Sun product roadmap",
        "and will provide guidance to customers in accordance with Oracle's standard product communication policies",
        "Any resulting features and timing of release of such features as determined by Oracle's review of roadmaps,",
        "are at the sole discretion of Oracle.",
        "All product roadmap information, whether communicated by Sun Microsystems or by Oracle",
        "the does not represent a commitment to deliver any material,",
        "code, or functionality, and should not be relied upon in making purchasing decisions.",
        "It is intended for information purposes only",
        "and may not be incorporated into any contract."	
    } ;

    //load all languages:
    List<LanguageProfile> languageProfiles = new LanguageProfileReader().readAllBuiltIn();

    //build language detector:
    LanguageDetector languageDetector = 
        LanguageDetectorBuilder.create(NgramExtractors.standard()).withProfiles(languageProfiles).build();

    for(int i = 0; i < VI_TEXT.length; i++) {
      Optional<LdLocale> lang = languageDetector.detect(VI_TEXT[i]);
      LdLocale locale = lang.get();
      Assert.assertEquals("vi", locale.getLanguage()) ;
    }
    for(int i = 0; i < EN_TEXT.length; i++) {
      System.out.println("DETECT: " + EN_TEXT[i]);
      Optional<LdLocale> lang = languageDetector.detect(EN_TEXT[i]);
      LdLocale locale = lang.get();
      Assert.assertEquals("en", locale.getLanguage()) ;
    }
  }
}