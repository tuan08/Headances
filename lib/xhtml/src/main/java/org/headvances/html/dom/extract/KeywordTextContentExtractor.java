package org.headvances.html.dom.extract;

import java.util.ArrayList;
import java.util.List;

import org.headvances.html.dom.TDocument;
import org.headvances.html.dom.TNode;
import org.headvances.html.dom.TNodeXPath;
import org.headvances.html.dom.selector.OrSelector;
import org.headvances.html.dom.selector.Selector;
import org.headvances.html.dom.selector.TagSelector;
import org.headvances.html.dom.selector.TextLengthSelector;
import org.headvances.html.dom.tagger.ContentListBlockTagger;
import org.headvances.html.dom.tagger.KeywordBlockTagger;
import org.headvances.html.dom.tagger.Tagger;
import org.headvances.html.dom.tagger.TextBlockTagger;
import org.headvances.html.dom.tagger.TitleBlockTagger;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class KeywordTextContentExtractor extends ContentExtractor {
	final static String[] CLASSIFIED_KEYWORD = {
		"giá bán", "price", "Địa chỉ", "mobile", "Đăng lúc", "Khu vực", "Ngày đăng",
		"Người đăng tin", "Người đăng", "Mã Tin", 
	} ;
	
	private String mainContentTag ;
	private KeywordBlockTagger keywordTagger ;
	private Selector candidateSelector ;
	private TextBlockTagger textBlockTagger = new TextBlockTagger() ;
	private Tagger listBlockTagger =  new ContentListBlockTagger();
	
	public KeywordTextContentExtractor(String tag, String[] keyword) {
		this.mainContentTag = tag ;
		keywordTagger   = new KeywordBlockTagger(tag + ":keyword", keyword) ;
		listBlockTagger = new ContentListBlockTagger() ;
		TagSelector tagSelector = 
			new TagSelector(keywordTagger.getTag(), TitleBlockTagger.TITLE_CANDIDATE, TextBlockTagger.BLOCK_TEXT) ;
		TextLengthSelector textSelector = new TextLengthSelector(100, 10000) ;
		candidateSelector = new OrSelector(tagSelector, textSelector) ;
	}

	protected ExtractContent extractDetail(TDocument tdoc) {
		keywordTagger.tag(tdoc, tdoc.getRoot()) ;
		textBlockTagger.tag(tdoc, tdoc.getRoot()) ;
		TNode[] nodes = tdoc.getRoot().select(candidateSelector, false) ;
		//for(TNode sel : nodes) {
		//	System.out.println(sel);
		//}
		TNode mainBlock = findMainBlock(nodes) ;
		if(mainBlock != null) {
			ExtractContent extractContent = new ExtractContent() ;
			extractContent.add(createDetail(mainContentTag, tdoc, mainBlock)) ;
			return extractContent;
		}
		return null ;
	}
	
	private TNode findMainBlock(TNode[] nodes) {
		TNode keywordNode = null;
		List<TNode> textNodes = new ArrayList<TNode>() ;
		List<TNode> titleNodes = new ArrayList<TNode>() ;
		for(int i = 0; i < nodes.length; i++) {
			TNode node = nodes[i] ;
			if(node.hasTag(keywordTagger.getTag())) {
				if(keywordNode == null) keywordNode = node ;
			} else if(node.hasTag(TitleBlockTagger.TITLE_CANDIDATE)) {
				titleNodes.add(node) ;
			} else {
				textNodes.add(node);
			}
		}
		if(keywordNode == null) return null ;
		if(titleNodes.size() == 0 && textNodes.size() == 0) return null ;
		TNode candidateNode = findClosestAncestor(keywordNode, titleNodes) ;
		candidateNode = findClosestAncestor(candidateNode, textNodes) ;
		return candidateNode ;
	}
	
	private TNode findClosestAncestor(TNode node, List<TNode> nodes) {
		if(nodes.size() == 0) return node ;
		TNode select = null ;
		int maxSimilarDistance = 0 ;
		for(int i = 0; i < nodes.size(); i++) {
			TNode cnode = nodes.get(i);
			int similarDistance = node.getXPath().similarDistance(cnode.getXPath()) ;
			if(similarDistance > maxSimilarDistance) {
				select = cnode ;
				maxSimilarDistance = similarDistance ;
			}
		}
		TNodeXPath ancestorXPath = node.getXPath().findFirstAncestor(select.getXPath()) ;
		return node.getAncestor(ancestorXPath) ;
	}
	
	protected ExtractContent extractList(TDocument tdoc) {
		TNode[] listNodes = listBlockTagger.tag(tdoc, tdoc.getRoot()) ;
		if(listNodes.length > 0) {
			StringBuilder contentB = new StringBuilder() ;
			for(int j = 0; j < listNodes.length; j++) {
				if(listNodes[j].hasAncestorTag(DETAIL_JUNK_TEXT_TAG)) continue ;
				contentB.append(listNodes[j].getTextContent()).append("\n") ;
			}
			ExtractBlock block = new ExtractBlock("mainContent") ;
			block.setTitle(tdoc.getAnchorText(), null) ;
			block.setContent(contentB.toString(), null);
			ExtractContent extractContent = new ExtractContent();
			extractContent.add(block) ;
			block.setTags(mainContentTag, "content:list") ;
			return extractContent;
		}
		return null ;
	}
	
	protected ExtractContent extractOther(TDocument tdoc) {
		return extractOther(mainContentTag, tdoc) ;
	}
}