package org.headvances.analysis.swui.xhtml;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JToolBar;

import org.headvances.analysis.AnalysisHistory;
import org.headvances.analysis.cluster.task.GetAnalysisHistoryTask;
import org.headvances.analysis.swui.AnalysisContext;
import org.headvances.analysis.swui.ComponentNode;
import org.headvances.analysis.swui.ComponentPanel;
import org.headvances.cluster.ClusterClient;
import org.headvances.data.Document;
import org.headvances.data.Entity;
import org.headvances.data.HtmlDocumentUtil;
import org.headvances.swingui.component.JTablePagination;
import org.headvances.util.text.StringUtil;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class XHTMLAnalysisHistoriesPanel extends ComponentPanel {
  private ComponentNode compNode ;
	private JTablePagination listView ;
	private DetailView      detailView ;
	private Document[]  docs ;
	
	public XHTMLAnalysisHistoriesPanel(ComponentNode compNode) throws Exception {
	  this.compNode = compNode;
		update() ;
	}
	
	protected void initJToolBar(JToolBar toolBar){
		JButton clearHistoryBtn = new JButton("Clear History") ;
		clearHistoryBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent event) {
    		try {
//    		  GetAnalysisHistoryTask task = new GetAnalysisHistoryTask();
//          AnalysisClient client = AnalysisContext.getInstance().getClient();
//          AnalysisHistory history = client.execute(task, compNode.getMember());
//          history.clear() ;
//          update() ;
        } catch (Exception e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
		});
		toolBar.add(clearHistoryBtn) ;
	}

	protected JComponent createBodyPane() {
		detailView = new DetailView() ;
		listView = new JTablePagination() ;
		listView.getJTable().addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
						int viewRow = listView.getModelSelectedRow();
						if (viewRow >= 0) {
							detailView.update(docs[viewRow]) ;
						}
				}
		});
		
		JSplitPane splitPane = 
			new JSplitPane(JSplitPane.VERTICAL_SPLIT, listView , detailView);
		splitPane.setAutoscrolls(false); 
		return splitPane ;
	}
	
	protected void update() throws Exception {
	  GetAnalysisHistoryTask task = new GetAnalysisHistoryTask();
    ClusterClient client = AnalysisContext.getInstance().getClient();
    AnalysisHistory history = client.execute(task, compNode.getMember());
		docs = history.getAnalysisDocuments() ;
		populate();
	}
	public void populate(){
	  String[] header = { 
	      "#", "Title", "Site", "Tag"
	    } ;
	    Object[][]  data = new Object[docs.length][] ;
	    for(int i = 0; i < docs.length; i++) {
	      String title = HtmlDocumentUtil.getHtmlLink(docs[i]).getString("anchorText") ;
	      String url = HtmlDocumentUtil.getHtmlLink(docs[i]).getString("url");
	      if(title == null || title.length() == 0) title = "Unknown Title" ;
	      String tags = StringUtil.joinStringArray(docs[i].getTags()) ;
	      if(tags == null) tags = "" ;
	      data[i] = new Object[] {
	        i,
	        title,
	        HtmlDocumentUtil.getDomain(url), 
	        tags
	      };
	    }
	    listView.setData(header, data, 30) ;
	}

	static public class DetailView extends JPanel {
		private JTextArea  jsonView ;
		private JTextPane  contentView ;
		private JTextPane  textAnalyzeView ;

		public DetailView() {
			setAutoscrolls(false) ;
			setLayout(new BorderLayout()) ;
			JTabbedPane tabbedPane = new JTabbedPane();
			tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
			add(tabbedPane, BorderLayout.CENTER) ;

			jsonView = new JTextArea() ;
			jsonView.setFont(new Font(Font.MONOSPACED, Font.TRUETYPE_FONT, 12));
			jsonView.setWrapStyleWord(true) ;
			jsonView.setLineWrap(true) ;
			tabbedPane.add("JSON", new JScrollPane(jsonView));
			
			contentView = new JTextPane();
			contentView.setContentType("text/plain") ;
			contentView.setEditable(false); 
			tabbedPane.add("Content", new JScrollPane(contentView));
			
			textAnalyzeView = new JTextPane();
			textAnalyzeView.setContentType("text/plain") ;
			textAnalyzeView.setEditable(false); 
			tabbedPane.add("Text Density", new JScrollPane(textAnalyzeView));
		}

		public void update(Document doc) {
			Document hdoc = new Document() ;
			hdoc.copy(doc) ;
			hdoc.setContent("N/A") ;
			try {
				jsonView.setText(Document.JSON_SERIALIZER.toString(hdoc)) ;
			} catch (IOException e) {
				e.printStackTrace();
			}
			contentView.setText(doc.getContent()) ;
			
			StringBuilder b = new StringBuilder() ;
			buildEntity(b, doc.getEntity("icontent")) ;
			buildEntity(b, doc.getEntity("comment")) ;
			textAnalyzeView.setText(b.toString()) ;
		}
		
		private void buildEntity(StringBuilder b, Entity entity) {
			if(entity == null) return ;
			if(b.length() > 0) {
				b.append("\n---------------------------------------------------------------------\n") ;
			}
			b.append("TITLE: ").append(entity.get("title")).append("\n\n") ;
			b.append("DESCRIPTION: ").append(entity.get("description")).append("\n\n") ;
			b.append("CONTENT: \n").append(entity.get("content")) ;
		}
	}
}