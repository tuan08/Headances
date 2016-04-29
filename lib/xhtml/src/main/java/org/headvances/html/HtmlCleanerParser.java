package org.headvances.html;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import org.headvances.html.dom.TNode;
import org.headvances.html.dom.TDocumentBuilder;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.ContentNode;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.HtmlSerializer;
import org.htmlcleaner.PrettyHtmlSerializer;
import org.htmlcleaner.TagNode;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class HtmlCleanerParser implements TDocumentBuilder {
	final static public HtmlCleanerParser INSTANCE = new HtmlCleanerParser() ;
	
	final HtmlCleaner htmlCleaner ;
	final HtmlSerializer htmlSerializer ;
	
	public HtmlCleanerParser() {
		CleanerProperties props = new CleanerProperties();
		// set some properties to non-default values
		props.setTranslateSpecialEntities(true);
		props.setTransResCharsToNCR(true);
		props.setOmitComments(true);
		props.setOmitUnknownTags(true) ;
		props.setUseEmptyElementTags(false) ;
		
		htmlCleaner = new HtmlCleaner(props);
		htmlSerializer = new PrettyHtmlSerializer(props) ;
	}
	
	public TagNode parse(String html) {
		TagNode node = htmlCleaner.clean(html) ;
		return node ;
	}
	
	public String reformat(String html) throws IOException {
		StringWriter w = new StringWriter();
		TagNode node = htmlCleaner.clean(html) ;
		htmlSerializer.write(node, w, "UTF-8") ;
		return w.toString() ;
	}
	
	public TNode toTNode(String html) {
		TagNode node = htmlCleaner.clean(html) ;
		return buildTNode(null, node) ;
	}
	
	private TNode buildTNode(TNode parent, TagNode node) {
		TNode tnode = new TNode(parent) ;
		tnode.setNodeName(node.getName().toLowerCase()) ; 
		String nodeName  = node.getName() ;
		if("meta".equalsIgnoreCase(nodeName)) {
			tnode.addAttribute("name", node.getAttributeByName("name")) ;
			tnode.addAttribute("http-equiv", node.getAttributeByName("http-equiv")) ;
			tnode.addAttribute("content", node.getAttributeByName("content")) ;
		}
		
		if("base".equalsIgnoreCase(nodeName)) {
			tnode.addAttribute("href", node.getAttributeByName("href")) ;
		}
		
		if("a".equalsIgnoreCase(nodeName)) {
			tnode.addAttribute("href", node.getAttributeByName("href")) ;
			tnode.addAttribute("title", node.getAttributeByName("title")) ;
		}
		
		if("img".equalsIgnoreCase(nodeName)) {
			tnode.addAttribute("src", node.getAttributeByName("src")) ;
		  tnode.addAttribute("alt", node.getAttributeByName("alt")) ;
		}
		
		tnode.setCssClass(node.getAttributeByName("class")) ;
		tnode.setElementId(node.getAttributeByName("id")) ;

		List nlist = node.getChildren() ;
		if(nlist.size() > 0) {
			for(int i = 0; i < nlist.size(); i++) {
				Object htmlNode = nlist.get(i) ;
				if(htmlNode instanceof ContentNode) {
					TNode tnodeChild = new TNode(tnode) ;
					tnodeChild.setNodeName("#text") ;
					tnodeChild.setNodeValue(((ContentNode)htmlNode).getContent().toString()) ;
					tnode.addChild(tnodeChild) ;
				} else if(htmlNode instanceof TagNode) {
					TagNode nchild = (TagNode)htmlNode ;
					String nchildNodeName = nchild.getName() ;
					if(nchildNodeName == null) continue ;
					if("script".equalsIgnoreCase(nchildNodeName)) continue ;
					if("style".equalsIgnoreCase(nchildNodeName)) continue ;
					if("iframe".equalsIgnoreCase(nchildNodeName)) continue ;
					TNode child = buildTNode(tnode, nchild) ;
					tnode.addChild(child) ;
				}
			}
		}
		return tnode ;
	}
}