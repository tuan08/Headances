package org.headvances.ml.nlp.opinion;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.headvances.nlp.query2.match.RuleMatch;
import org.headvances.nlp.token.IToken;
import org.headvances.nlp.token.TokenCollection;
import org.headvances.util.MD5;
import org.headvances.util.text.StringUtil;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class Opinion {
	private String   id ;
	private String   documentId ;
	private String   opinion ;
	private String[] category ;
	private String[] tag ;
	private String   label ;

	transient private RuleMatch rMatch;

	public Opinion() { }

	public String getId() { return id; }
	public void setId(String id) { this.id = id; }

	public String getDocumentId() { return documentId; }
	public void setDocumentId(String documentId) { this.documentId = documentId; }

	public String getOpinion() { return opinion;}
	public void setOpinion(String opinion) { this.opinion = opinion; }

	@JsonIgnore
	public RuleMatch getRuleMatch() { return rMatch; }
	public void setRuleMatch(RuleMatch rMatch) { this.rMatch = rMatch; }

	public String[] getCategory() { return category;}
	public void setCategory(String[] category) { this.category = category; }
	public void addCategory(String cat){ this.category = StringUtil.merge(category, cat); }

	public String[] getTag() { return tag;}
	public void setTag(String[] tag) { this.tag = tag ; }
	public void addTag(String tag){ this.tag = StringUtil.merge(this.tag, tag); }

	public String getLabel() { return this.label ; }
	public void   setLabel(String label) { this.label = label; }

	public byte getRank() {
		if("-3".equals(label)) return -3 ;
		if("-2".equals(label)) return -2 ;
		if("-1".equals(label)) return -1 ;
		if("1".equals(label)) return 1 ;
		if("2".equals(label)) return 2 ;
		if("3".equals(label)) return 3 ;
		return 0 ;
	}

	public void computeId(String docId) {
		TokenCollection collection = null ; 
		if(this.rMatch != null) {
			collection = rMatch.getTokenCollection() ;
		}
		computeId(docId, collection) ;
	}

	public void computeId(String docId, TokenCollection collection) {
		this.documentId = docId ;
		String text  = this.opinion ;
		if(collection != null) {
			IToken[] tokens = collection.getTokens() ;
			StringBuilder b = new StringBuilder() ;
			for(IToken sel : tokens) {
				String token = sel.getNormalizeForm() ;
				if(token.length() == 1) continue ;
				if("re".equals(token)) continue ;
				if("re:".equals(token)) continue ;
				boolean ignore = false ;
				for(char selChar : sel.getNormalizeFormBuf()) {
					if(Character.isLetter(selChar)) continue ;
					if(Character.isDigit(selChar)) continue ;
					if(selChar == '-' || selChar == '_') continue ;
					ignore = true ;
					break ;
				}
				if(!ignore) b.append(token) ;
			}
			text = b.toString() ;
		}
		MD5 md5 = MD5.digest(text) ;
		this.id = md5.toString() ;
	}
}