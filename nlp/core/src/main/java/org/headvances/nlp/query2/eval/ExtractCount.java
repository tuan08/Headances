package org.headvances.nlp.query2.eval;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import org.headvances.nlp.query2.QueryContext;
import org.headvances.nlp.query2.QueryDocument;
import org.headvances.nlp.query2.match.RuleMatch;
import org.headvances.util.text.StringMatcher;
import org.headvances.util.text.StringUtil;

public class ExtractCount implements EvalExpression {
	private StringMatcher[] tagmatcher;
	
	public ExtractCount(String paramexp) throws Exception {
		String[] array = StringUtil.toStringArray(paramexp) ;
  	tagmatcher = new StringMatcher[array.length] ;
  	for(int i = 0; i < tagmatcher.length; i++) {
  		tagmatcher[i] = new StringMatcher(array[i]) ;
  	}
	}
	
  public String getName() { return "extractCount"; }

  public Object eval(QueryContext context, QueryDocument doc) throws IOException {
	  int count = 0 ;
	  for(RuleMatch sel : context.getRuleMatch()) {
	  	for(int i = 0; i < tagmatcher.length; i++) {
	  		count += count(tagmatcher[i], sel) ;
	  	}
	  }
  	return (double)count ;
  }
  
  private int count(StringMatcher matcher, RuleMatch rmatch) {
  	int count  = 0 ;
  	Map<String, String[]> extracts = rmatch.getExtracts() ;
  	Iterator<String> i = extracts.keySet().iterator() ;
  	while(i.hasNext()) {
  		if(matcher.matches(i.next())) count++ ;
  	}
  	return count ;
  }
}