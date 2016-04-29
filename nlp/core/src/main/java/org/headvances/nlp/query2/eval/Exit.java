package org.headvances.nlp.query2.eval;

import java.io.IOException;

import org.headvances.nlp.query2.QueryContext;
import org.headvances.nlp.query2.QueryDocument;

public class Exit implements EvalExpression {
	public Exit(String exp) throws Exception {
	}
	
  public String getName() { return "exit"; }

  public Object eval(QueryContext context, QueryDocument doc) throws IOException {
  	context.setComplete(true) ;
  	return true ;
  }
}