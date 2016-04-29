package org.headvances.ml.swingui.classify;

import java.awt.BorderLayout ;
import java.awt.Desktop ;
import java.awt.event.ActionEvent ;
import java.awt.event.ActionListener ;
import java.awt.event.KeyAdapter ;
import java.awt.event.KeyEvent ;
import java.io.File ;
import java.net.URI ;

import javax.swing.JButton ;
import javax.swing.JFileChooser ;
import javax.swing.JPanel ;
import javax.swing.JScrollPane ;
import javax.swing.JToolBar ;

import org.headvances.data.Document ;
import org.headvances.data.Entity ;
import org.headvances.data.HtmlDocumentUtil ;
import org.headvances.swingui.SwingUtil ;
import org.headvances.swingui.component.TableView ;
import org.headvances.util.text.StringUtil ;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class DocumentLabelerPane extends JPanel {
	private TableView table ;
	private Document[] modifyingDocs ;
	
	public DocumentLabelerPane() {
		setLayout(new BorderLayout()) ;

		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		JButton openBtn = new JButton("Open");
		openBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				try {
					JFileChooser fc = new JFileChooser();
					fc.setCurrentDirectory(new File(".")) ;
					fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
					int returnVal = fc.showOpenDialog(DocumentLabelerPane.this);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						final File file = fc.getSelectedFile();
						Context.getInstance().newDocumentIO(file.getCanonicalPath()) ;
						populate(Context.getInstance().getDocumentIO().next()) ;
					}
				} catch (Throwable ex) {
					SwingUtil.showError(DocumentLabelerPane.this, "Input Files", ex) ;
				}
			}
		});
		toolBar.add(openBtn);
		JButton commitAndNextBtn = new JButton("Commit & Next");
		commitAndNextBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				try {
					DocumentIO io = Context.getInstance().getDocumentIO() ;
					io.write(modifyingDocs) ;
					populate(io.next()) ;
				} catch (Throwable ex) {
					SwingUtil.showError(DocumentLabelerPane.this, "Commit & Next", ex) ;
				}
			}
		});
		toolBar.add(commitAndNextBtn);
		add(toolBar, BorderLayout.NORTH) ;
		table = new TableView() ;
		table.addKeyListener(new ShortcutKeyListener());
		add(new JScrollPane(table), BorderLayout.CENTER) ;
	}
	
	private void populate(Document[] doc) {
		modifyingDocs = doc ;
		String[] header = {"URL", "Anchor Text", "Size", "Labels"} ;
		Object[][] data = new Object[doc.length][] ;
		for(int i = 0; i < doc.length; i++) {
			data[i] = createRow(doc[i]) ;
		}
		table.setData(header, data) ;
		table.setSortableColumn(new int[] {0, 1, 2}) ;
		table.setColWidth(new int[] {650, 500, 50, 200}) ;
	}
	
	private void updateRow(String label) {
		int modelRow = table.getModelSelectedRow() ;
		Document doc  = modifyingDocs[modelRow] ;
		doc.addLabel(label) ;
		table.updateRow(modelRow, createRow(doc)) ;
		int nextSelectedRow = table.getSelectedRow() + 1;
		if(nextSelectedRow < table.getRowCount()) {
			table.setRowSelectionInterval(nextSelectedRow, nextSelectedRow) ;
		}
	}
	
	private Object[] createRow(Document doc) {
		Entity htmlLink = HtmlDocumentUtil.getHtmlLink(doc) ;
		String url = htmlLink.get("url") ;
		String anchorText = htmlLink.get("anchorText") ;
		String labels = StringUtil.joinStringArray(doc.getLabels()) ;
		if(labels == null) labels = "" ;
		String content = doc.getContent() ;
		int size = 0;
		if(content != null) size = content.length() ;
		Object[] data = new Object[] {
		  url, anchorText, size, labels
		};
		return data ;
	}
	
	public class ShortcutKeyListener extends KeyAdapter {
		public void keyPressed(KeyEvent e) {
			char key = e.getKeyChar() ;
			if(e.isControlDown() && e.getKeyCode() == KeyEvent.VK_O) onCtr_O() ;
			if(e.isControlDown() && e.getKeyCode() == KeyEvent.VK_N) onCtr_N() ;
			if(e.isControlDown() && e.getKeyCode() == KeyEvent.VK_C) onCtr_C() ;
			else if(key == 'a') updateRow("content:article:detail") ;
			else if(key == 'A') updateRow("content:article:list") ;
			else if(key == 'b') updateRow("content:blog:detail") ;
			else if(key == 'B') updateRow("content:blog:list") ;
			else if(key == 'f') updateRow("content:forum:detail") ;
			else if(key == 'F') updateRow("content:forum:list") ;
			else if(key == 'c') updateRow("content:classified:detail") ;
			else if(key == 'C') updateRow("content:classified:list") ;
			else if(key == 'p') updateRow("content:product:detail") ;
			else if(key == 'P') updateRow("content:product:list") ;
			else if(key == 'j') updateRow("content:job:detail");
			else if(key == 'J') updateRow("content:job:list");
      else if(key == 'o') updateRow("content:other:detail") ;
      else if(key == 'O') updateRow("content:other:list") ;
      else if(key == 'e') updateRow("content:error");
      else if(key == 'i') updateRow("content:ignore");
		}
		
		private void onCtr_C() {
		  int modelRow = table.getModelSelectedRow();
		  Document doc = modifyingDocs[modelRow];
		  doc.setLabels(null);
		  table.updateRow(modelRow, createRow(doc));
		}
		
		private void onCtr_N(){
		  try {
        DocumentIO io = Context.getInstance().getDocumentIO() ;
        io.write(modifyingDocs) ;
        populate(io.next()) ;
      } catch (Throwable ex) {
        SwingUtil.showError(DocumentLabelerPane.this, "Commit & Next", ex) ;
      }
		}
		
		private void onCtr_O() {
			if (Desktop.isDesktopSupported()) {
				Desktop desktop = Desktop.getDesktop();
				if(desktop.isSupported(Desktop.Action.BROWSE)) {
					int row = table.getModelSelectedRow() ;
					Document doc = modifyingDocs[row];
					String url = HtmlDocumentUtil.getHtmlLink(doc).get("url");
					try {
            desktop.browse(new URI(url)) ;
          } catch (Throwable t) {
            SwingUtil.showError(DocumentLabelerPane.this, "Open url: " + url, t);
          }
				}
			}
		}
	}
}
