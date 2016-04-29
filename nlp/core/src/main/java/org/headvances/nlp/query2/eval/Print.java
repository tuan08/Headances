package org.headvances.nlp.query2.eval;

import java.io.IOException;

import org.headvances.nlp.query2.QueryContext;
import org.headvances.nlp.query2.QueryDocument;

public class Print implements EvalExpression {
	private String msg ;
	
	public Print(String paramexp) { msg = paramexp ; }
	
  public String getName() { return "print"; }

  public Object eval(QueryContext context, QueryDocument doc) throws IOException {
  	System.out.println(msg);
  	return true ;
  }
}