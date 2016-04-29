package org.headvances.swingui.component;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.AbstractListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.LineBorder;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class JListHtml extends JList {
	public JListHtml() {
		setCellRenderer(new HtmlItemRenderer());
		setAutoscrolls(true) ;
	}
	
	public void setItems(String[] item) {
		Item[] dataItem = new Item[item.length] ;
		for(int i = 0; i < item.length; i++) {
			dataItem[i] = new Item(item[i], "") ;
		}
		setModel(new DataModel(dataItem)) ;
	}
	
	public void setItems(String[] item, String[] tooltip) {
		Item[] dataItem = new Item[item.length] ;
		for(int i = 0; i < item.length; i++) {
			dataItem[i] = new Item(item[i], tooltip[i]) ;
		}
		setModel(new DataModel(dataItem)) ;
	}
	
	static public class DataModel extends AbstractListModel {
		Item[] item ;
		public DataModel(Item[] item) { this.item = item ; }
    public int getSize() { return item.length ; }
    public Object getElementAt(int index) { return item[index]; }
	}
	
	static public class Item {
		String label ;
		String tooltip ;
		
		public Item(String label, String tooltip) {
			this.label = label ;
			this.tooltip = tooltip ;
		}
	}
	
	static class HtmlItemRenderer extends JLabel implements ListCellRenderer {
	  private static final Color HIGHLIGHT_COLOR = new Color(0, 0, 128);

	  public HtmlItemRenderer() {
	    setOpaque(true);
	    setIconTextGap(12);
	  }

	  public Component getListCellRendererComponent(JList list, Object value,
	                                                int index, boolean isSelected, 
	                                                boolean cellHasFocus) {
	    setMaximumSize(new Dimension(500, 100)) ;
	  	setBorder(new LineBorder(Color.LIGHT_GRAY, 1)) ;
	    if (isSelected) {
	      setBackground(HIGHLIGHT_COLOR);
	      setForeground(Color.white);
	    } else {
	      setBackground(Color.white);
	      setForeground(Color.black);
	    }
	    Item itemData = (Item) value ;
	    String labelText = 
	    	String.format("<html><div WIDTH=%d>%s</div></html>", list.getWidth(), itemData.label);
	    String html = "<html>" + itemData.label + "</html>" ;
	    setText(labelText) ;
	    setToolTipText(itemData.tooltip) ;
	    return this;
	  }
	}
}
