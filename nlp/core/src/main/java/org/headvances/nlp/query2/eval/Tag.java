package org.headvances.nlp.query2.eval;

import java.io.IOException;

import org.headvances.nlp.query2.QueryContext;
import org.headvances.nlp.query2.QueryDocument;
import org.headvances.util.text.StringUtil;

public class Tag implements EvalExpression {
	private String[] tag;
	
	public Tag(String paramexp) throws Exception {
		tag = StringUtil.toStringArray(paramexp) ;
	}
	
  public String getName() { return "tag"; }


  public Object eval(QueryContext context, QueryDocument doc) throws IOException {
	  for(String sel : tag) context.setTag(sel) ;
  	return true ;
  }
}
