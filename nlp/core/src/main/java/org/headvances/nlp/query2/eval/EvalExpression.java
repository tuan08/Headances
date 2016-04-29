package org.headvances.nlp.query2.eval;

import org.headvances.nlp.query2.QueryContext;
import org.headvances.nlp.query2.QueryDocument;

public interface EvalExpression {
	public String getName() ;
	public Object eval(QueryContext context, QueryDocument doc) throws Exception ;
}
