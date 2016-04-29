package org.headvances.nlp.query2.eval;

import java.io.IOException;

import org.headvances.nlp.query2.QueryContext;
import org.headvances.nlp.query2.QueryDocument;
import org.headvances.nlp.query2.match.MatcherResourceFactory;


public class Continue implements EvalExpression {

	public Continue() {} 
	
  public String getName() { return "continue"; }

  public void init(MatcherResourceFactory umFactory, String paramexp) throws Exception {
  }
  
  public Object eval(QueryContext context, QueryDocument doc) throws IOException {
    context.setContinue(true) ;
	  return true ;
  }
}
