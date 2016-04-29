package org.headvances.ml.swingui.nlp.fexplorer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.headvances.ml.swingui.nlp.NLPPane;
import org.headvances.ml.swingui.nlp.WorkingWorkspace;
import org.headvances.swingui.SwingUtil;
import org.headvances.util.FileUtil;

public class FileTreePanel extends JPanel {
	final static public FileSystemView SFV = FileSystemView.getFileSystemView();
	private JTree tree;
	private JPopupMenu popup ;

	public FileTreePanel() {
		this.setLayout(new BorderLayout());
		this.tree = createJTree() ;
		final JScrollPane jsp = new JScrollPane(this.tree);
		jsp.setBorder(new EmptyBorder(0, 0, 0, 0));
		this.add(jsp, BorderLayout.CENTER);
	}
	
	private JTree createJTree() {
		popup = new JPopupMenu();
		popup.add(new OpenAction());
		popup.add(new DeleteAction());
		popup.add(new RefreshAction());
		popup.setPopupSize(new Dimension(100, 50)) ;
		
		File[] roots = File.listRoots();
		FileTreeNode rootTreeNode = new FileTreeNode(roots);
		JTree tree = new JTree(rootTreeNode);
		tree.setCellRenderer(new FileTreeNodeCellRenderer());
		tree.setRootVisible(false);
		tree.add(popup) ;
		tree.addMouseListener(new PopupTrigger());
		return tree ;
	}
	
	private void openSelectedFile() {
		FileTreeNode select = (FileTreeNode) tree.getLastSelectedPathComponent() ;
		NLPPane app = SwingUtil.findAncestorOfType(FileTreePanel.this, NLPPane.class) ;
		WorkingWorkspace wspace = app.getWorkingWorkspace() ;
		if(select.file.isDirectory()) return ;
		wspace.openFile(select.file) ;
	}
	
	class PopupTrigger extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			if(e.getClickCount() > 1) openSelectedFile() ;
		}
		
		public void mouseReleased(MouseEvent e) {
			if (e.isPopupTrigger()) {
				int x = e.getX();
				int y = e.getY();
				TreePath path = tree.getPathForLocation(x, y);
				if (path != null) {
					popup.show(tree, x, y);
				}
			}
		}
	}
	
	class DeleteAction extends AbstractAction {
		public DeleteAction() { super("Delete") ; }
		
		public void actionPerformed(ActionEvent e) {
			FileTreeNode select = (FileTreeNode) tree.getLastSelectedPathComponent();
			try {
				if(select.isFileSystemRoot) {
					JOptionPane.showMessageDialog(FileTreePanel.this, "Cannot delete a drive", "Delete File", JOptionPane.ERROR_MESSAGE) ;
					return  ;
				} 
				String mesg = "Do you really want to delete the file " + select.file.getName() ;
				int confirm = JOptionPane.showConfirmDialog(FileTreePanel.this, mesg, "Delete File", JOptionPane.WARNING_MESSAGE);
				if(confirm == JOptionPane.OK_OPTION) {
					TreePath selectedPath = tree.getLeadSelectionPath() ;
					FileUtil.remove(select.file, false) ;
					((DefaultTreeModel) tree.getModel()).reload() ;
					tree.setSelectionPath(selectedPath) ;
				}
			} catch(Exception ex) {
				JOptionPane.showMessageDialog(FileTreePanel.this, ex.getMessage(), "Delete File", JOptionPane.ERROR_MESSAGE) ;
			}
		}
	}

	class RefreshAction extends AbstractAction {
		public RefreshAction() { super("Refresh") ; }

		public void actionPerformed(ActionEvent e) {
			TreePath selectedPath = tree.getLeadSelectionPath() ;
			((DefaultTreeModel) tree.getModel()).reload() ;
			tree.setSelectionPath(selectedPath) ;
		}
	}
	
	class OpenAction extends AbstractAction {
		public OpenAction() { super("Open") ; }
		
		public void actionPerformed(ActionEvent e) {
			openSelectedFile() ;
		}
	}
}