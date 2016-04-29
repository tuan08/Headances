package org.headvances.html.dom.tagger;

import java.util.ArrayList;
import java.util.List;

import org.headvances.html.dom.TDocument;
import org.headvances.html.dom.TNode;
import org.headvances.html.dom.TNodeUtil;
import org.headvances.html.dom.selector.Selector;
import org.headvances.util.text.CosineSimilarity;
import org.headvances.util.text.StringUtil;

/**
 * Author : Tuan Nguyen
 *          tuan.nguyen@headvances.com
 * Apr 24, 2010  
 */
public class TitleBlockTagger  extends Tagger {
	final static public String TITLE_CANDIDATE      = "block:title-candidate" ;
	
	public TNode[] tag(TDocument tdoc, TNode root) {
		Selector selector = new TNodeSelector(tdoc) ;
		TNode[] nodes = root.select(selector) ;
		for(TNode sel : nodes) sel.addTag(TITLE_CANDIDATE) ;
		return nodes ;
	}
	
	static public class TNodeSelector implements Selector {
		int minLength = Integer.MAX_VALUE, maxLength = Integer.MIN_VALUE ;
		String[][] tCandidate ;
		
		TNodeSelector(TDocument tdoc) {
			List<String> tCandidates = new ArrayList<String>() ;
			String anchorText = tdoc.getAnchorText() ;
			if(anchorText != null && anchorText.length() > 3) tCandidates.add(anchorText) ;
			String title = TNodeUtil.getTitle(tdoc.getRoot()) ;
			if(title != null && title.length() < 150) {
				List<String> splitTitles = StringUtil.split(title, " - ", " | ") ;
				for(int i = 0; i < splitTitles.size(); i++) {
					String sel  = splitTitles.get(i) ;
					if(sel.length() > 20) tCandidates.add(sel) ;
				}
			}
			buildCandidates(tCandidates) ;
		}
		
		private void buildCandidates(List<String> tCandidates) {
			this.tCandidate = new String[tCandidates.size()][] ;
			for(int i = 0; i < tCandidates.size(); i++) {
				String sel = tCandidates.get(i) ;
				this.tCandidate[i] = CosineSimilarity.split(sel) ;
				int min = sel.length() - 15 ;
				int max = sel.length() + 15 ;
				if(min < minLength) minLength = min ;
				if(max > maxLength) maxLength = max ;
			}
			if(minLength < 0) minLength = 0 ;
		}
		
    public boolean isSelected(TNode node) {
    	if(node.getNodeValue() == null) return false ;
  		String text = node.getNodeValue() ;
  		int length = text.length() ;
  		if(length >= minLength && length <= maxLength) {
  			if("title".equals(node.getParent().getNodeName())) return false ;
  			if(TNodeUtil.getAncestor(node, "meta", 3) != null) return false ;
  			
  			for(int i = 0; i < tCandidate.length; i++) {
  				String[] token = CosineSimilarity.split(text) ;
  			  double similarity = CosineSimilarity.INSTANCE.similarity(tCandidate[i], token) ;
  			  if(similarity > 0.8) return true ;
  			}
  		}
  		return false ;
    }
	}
}