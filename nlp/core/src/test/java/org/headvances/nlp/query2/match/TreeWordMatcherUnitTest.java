package org.headvances.nlp.query2.match;

import org.headvances.nlp.token.WordTokenizer;
import org.junit.Assert;
import org.junit.Test;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class TreeWordMatcherUnitTest {
  @Test
  public void testConstruction() throws Exception {
    TreeWordMatcher tree = new TreeWordMatcher() ;
    tree.addWord("test") ;
    tree.addWord("kinh") ;
    tree.addWord("kinh doanh") ;
    tree.addWord("bat dong san") ;
    tree.addWord("kinh doanh mua ban") ;
    tree.dump(0) ;

    System.out.println(tree.matches(new WordTokenizer("kinh").allTokens(), 0)) ;
    System.out.println(tree.matches(new WordTokenizer("kinh doanh").allTokens(), 0)) ;
    System.out.println(tree.matches(new WordTokenizer("kinh doanh mua").allTokens(), 0)) ;
    System.out.println(tree.matches(new WordTokenizer("kinh doanh mua ban").allTokens(), 0)) ;
    Assert.assertEquals(0, tree.matches(new WordTokenizer("kinh").allTokens(), 0)) ;
    Assert.assertEquals(2, tree.matches(new WordTokenizer("kinh doanh").allTokens(), 0)) ;
    Assert.assertEquals(2, tree.matches(new WordTokenizer("kinh doanh mua").allTokens(), 0)) ;
    Assert.assertEquals(4, tree.matches(new WordTokenizer("kinh doanh mua ban").allTokens(), 0)) ;
    Assert.assertEquals(3, tree.matches(new WordTokenizer("bat dong san").allTokens(), 0)) ;
  }
}