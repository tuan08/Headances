package org.headvances.util.text;

import junit.framework.Assert;

import org.junit.Test;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class StringMatcherUnitTest {
	@Test
	public void test() throws Exception {
		StringMatcher matcher = new StringMatcher("*test*") ;
		Assert.assertTrue(matcher.matches("this is a test")) ;
		
		StringMatcher emailMatcher = new StringMatcher("*@gmail.com") ;
		Assert.assertTrue(emailMatcher.matches("tuan.nguyen@gmail.com")) ;
	}
}