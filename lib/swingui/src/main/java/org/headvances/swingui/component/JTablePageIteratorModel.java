package org.headvances.swingui.component;

import java.util.List;

import org.headvances.util.PageList;


abstract public class JTablePageIteratorModel<T> extends javax.swing.table.AbstractTableModel {
	protected PageList<T> items ;
	protected List<T> currPage ;
	
	public JTablePageIteratorModel(PageList<T> items) throws Exception {
		this.items = items ;
		currPage = items.currentPage() ;
	}
	
	public PageList<T> getItems() { return items ; }

	public int getRowCount() { return currPage.size() ; }

  public int getCurrentPage() { return items.getCurrentPage() ; }

  public void setPage(int page) throws Exception { 
  	currPage = items.getPage(page) ; 
  }
}
