package org.headvances.search.swui.search;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.headvances.data.Document;
import org.headvances.data.Entity;
import org.headvances.json.JSONSerializer;
import org.headvances.search.ESJSONClient;
import org.headvances.search.swui.ElasticSearchContext;
import org.headvances.swingui.component.TableView;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class SearchPanel extends JPanel {
	private JToolBar        toolBar    ;
	private QueryFormPanel  queryForm  ;
	private TableView       listView ;
	private DetailView      detailView ;
	private SearchResponse  response ;

	public SearchPanel() {
		setLayout(new BorderLayout());
		queryForm = new QueryFormPanel(this) ;

		toolBar = new JToolBar() ;
		toolBar.setFloatable(false) ;
		JButton queryBtn = new JButton("Query") ;
		queryBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				queryForm.setVisible(true) ;
			}
		});
		toolBar.add(queryBtn) ;
		add(toolBar, BorderLayout.NORTH) ;
		add(createBodyPane(), BorderLayout.CENTER) ;
	}

	protected JComponent createBodyPane() {
		detailView = new DetailView() ;
		listView = new TableView() ;
		listView.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent event) {
						int viewRow = listView.getSelectedRow();
						if (viewRow >= 0) {
							detailView.update(response.getHits().hits()[viewRow]) ;
						}
					}
				}
		);
		JSplitPane splitPane = 
			new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(listView) , detailView);
		splitPane.setDividerLocation(0.5) ;
		//splitPane.setOneTouchExpandable(true);
		splitPane.setAutoscrolls(false); 
		return splitPane ;
	}

	public void updateSearch(String index, String type, int limit, String jsonQuery) throws Exception {
		ESJSONClient client = 
			new ESJSONClient(ElasticSearchContext.getInstance().getESClient(), index, type) ;
		this.response = client.search(jsonQuery) ;
		SearchHit[] hits = response.getHits().hits() ;

		String[] header = { 
				"#", "Title", "Score"
		} ;
		Object[][]	data = new Object[hits.length][] ;
		for(int i = 0; i < hits.length; i++) {
			String title = "Unknown Title" ;
			Object fValue = hits[i].sourceAsMap().get("title") ;
			if(fValue != null) title = fValue.toString() ;
			data[i] = new Object[] {
					i,
					title,
					hits[i].getScore(),
			};
		}
		listView.setData(header, data) ;
		listView.setColWidth(new int[] {25, -1, 50}) ;
	}

	static public class DetailView extends JPanel {
		private JTextArea    jsonView ;
		private JTextArea    entityView ;

		public DetailView() {
			setLayout(new BorderLayout()) ;
			JTabbedPane tabbedPane = new JTabbedPane();
			tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
			add(tabbedPane, BorderLayout.CENTER) ;

			jsonView = new JTextArea() ;
			jsonView.setFont(new Font(Font.MONOSPACED, Font.TRUETYPE_FONT, 12));
			jsonView.setWrapStyleWord(true) ;
			jsonView.setLineWrap(true) ;
			tabbedPane.add("JSON", new JScrollPane(jsonView));

			entityView = new JTextArea() ;
			entityView.setFont(new Font(Font.MONOSPACED, Font.TRUETYPE_FONT, 12));
			entityView.setWrapStyleWord(true) ;
			entityView.setLineWrap(true) ;
			tabbedPane.add("Entities", new JScrollPane(entityView));
		}

		public void update(SearchHit hit) {
			try {
				String source = hit.sourceAsString() ;
				JSONSerializer serializer = new JSONSerializer() ;
				serializer.setIgnoreUnknownProperty(true) ;
				Document doc = serializer.fromString(source, Document.class) ;
				StringBuilder b = new StringBuilder() ;
				buildEntity(b, doc.getEntity("icontent")) ;
				buildEntity(b, doc.getEntity("comment")) ;
				entityView.setText(b.toString()) ;
				
				doc.setContent(null) ;
				doc.getEntities().clear() ;
				jsonView.setText(serializer.toString(doc)) ;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		private void buildEntity(StringBuilder b, Entity icontent) {
			if(icontent == null) return ;
			if(b.length() > 0) {
				b.append("\n=====================================================================\n") ;
			}
			b.append("TITLE: ").append(icontent.getString("title")).append("\n\n") ;
			b.append("DESCRIPTION: ").append(icontent.getString("description")).append("\n\n") ;
			b.append("CONTENT: \n").append(icontent.getString("content")) ;
		}
	}
}