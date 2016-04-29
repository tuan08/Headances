package org.headvances.swingui.component ;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.plaf.basic.BasicRadioButtonUI;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.View;
/**
 * $Author: Tuan Nguyen$ 
 **/

public class JTablePagination extends JPanel {
	private static final Color evenColor = new Color(240, 255, 250);
	private static int LR_PAGE_SIZE = 5;

	private final LinkViewRadioButtonUI ui = new LinkViewRadioButtonUI();
	private final Box box = Box.createHorizontalBox();
	
	private final DefaultTableModel model ;
	
	private final TableRowSorter<TableModel> sorter ;
	private final JTable table ;

	public JTablePagination() {
		this.model = new DefaultTableModel(null, (String[])null) {
			public Class<?> getColumnClass(int column) {
			  if(getRowCount() == 0) return Object.class ;
			  return getValueAt(0, column).getClass();
			}
		};
		this.table = new JTable(model) {
			public Component prepareRenderer(TableCellRenderer tcr, int row, int column) {
				Component c = super.prepareRenderer(tcr, row, column);
				if(isRowSelected(row)) {
					c.setForeground(getSelectionForeground());
					c.setBackground(getSelectionBackground());
				} else{
					c.setForeground(getForeground());
					c.setBackground((row%2==0)?evenColor:getBackground());
				}
				return c;
			}
		};
		sorter = new TableRowSorter<TableModel>(model) {
			@Override public void toggleSortOrder(int column) {
				RowFilter<? super TableModel, ? super Integer> f = getRowFilter();
				setRowFilter(null);
				super.toggleSortOrder(column);
				setRowFilter(f);
			}
		};
		table.setFillsViewportHeight(true);
		table.setIntercellSpacing(new Dimension());
		table.setShowGrid(false);
		//table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		table.setRowSorter(sorter);

		setLayout(new BorderLayout());
		add(new JScrollPane(table), BorderLayout.CENTER);
		add(box, BorderLayout.SOUTH);
	}

	public JTable getJTable() { return this.table ; }
	
	public int getModelSelectedRow() {
		int row = table.getSelectedRow() ;
		return table.convertRowIndexToModel(row) ;
	}
	
	public void setData(String[] header, Object[][] data, int pageSize) {
		model.setDataVector(data, header) ;
		sorter.setSortKeys(Arrays.asList(new RowSorter.SortKey(0, SortOrder.ASCENDING)));
		renderPageIterator(pageSize, 1);
	}
	
	public void updateRow(int row, Object[] data) {
	  for(int i = 0; i < data.length; i++) {
	    model.setValueAt(data[i], row, i) ;
	  }
	  model.fireTableRowsUpdated(row, row) ;
	} 
	
	private void renderPageIterator(final int itemsPerPage, final int currentPageIndex) {
		sorter.setRowFilter(null);
		sorter.allRowsChanged();
		sorter.setRowFilter(makeRowFilter(itemsPerPage, currentPageIndex-1));
		ArrayList<JRadioButton> l = new ArrayList<JRadioButton>();
		int startPageIndex = currentPageIndex-LR_PAGE_SIZE;
		if(startPageIndex<=0) startPageIndex = 1;

		int maxPageIndex = (model.getRowCount()/itemsPerPage)+1;
		int endPageIndex = currentPageIndex+LR_PAGE_SIZE-1;
		if(endPageIndex>maxPageIndex) endPageIndex = maxPageIndex;

		if(currentPageIndex>1)
			l.add(makePNRadioButton(itemsPerPage, currentPageIndex-1, "Prev"));
		for(int i=startPageIndex;i<=endPageIndex;i++)
			l.add(makeRadioButton(itemsPerPage, currentPageIndex, i-1));
		if(currentPageIndex<maxPageIndex)
			l.add(makePNRadioButton(itemsPerPage, currentPageIndex+1, "Next"));

		box.removeAll();
		ButtonGroup bg = new ButtonGroup();
		box.add(Box.createHorizontalGlue());
		for(JRadioButton r:l) {
			box.add(r); bg.add(r);
		}
		box.add(Box.createHorizontalGlue());
		box.revalidate();
		box.repaint();
		l.clear();
	}

	private JRadioButton makeRadioButton(final int itemsPerPage, final int current, final int target) {
		JRadioButton radio = new JRadioButton(""+(target+1)) {
			@Override protected void fireStateChanged() {
				ButtonModel model = getModel();
				if(!model.isEnabled()) {
					setForeground(Color.GRAY);
				} else if(model.isPressed() && model.isArmed()) {
					setForeground(Color.GREEN);
				} else if(model.isSelected()) {
					setForeground(Color.RED);
				}
				super.fireStateChanged();
			}
		};
		radio.setForeground(Color.BLUE);
		radio.setUI(ui);
		if(target+1==current) {
			radio.setSelected(true);
		}
		radio.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				renderPageIterator(itemsPerPage, target+1);
			}
		});
		return radio;
	}
	
	private JRadioButton makePNRadioButton(final int itemsPerPage, final int target, String title) {
		JRadioButton radio = new JRadioButton(title);
		radio.setForeground(Color.BLUE);
		radio.setUI(ui);
		radio.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				renderPageIterator(itemsPerPage, target);
			}
		});
		return radio;
	}
	private RowFilter<TableModel,Integer> makeRowFilter(final int itemsPerPage, final int target) {
		return new RowFilter<TableModel,Integer>() {
			@Override public boolean include(Entry<? extends TableModel, ? extends Integer> entry) {
				int ei = entry.getIdentifier();
				int vi = table.convertRowIndexToView(ei);
				return (target*itemsPerPage<=vi && vi<target*itemsPerPage+itemsPerPage);
			}
		};
	}

	static class LinkViewRadioButtonUI extends BasicRadioButtonUI {
		private static Dimension size = new Dimension();
		private static Rectangle viewRect = new Rectangle();
		private static Rectangle iconRect = new Rectangle();
		private static Rectangle textRect = new Rectangle();

		public Icon getDefaultIcon() { return null; }
		
		public synchronized void paint(Graphics g, JComponent c) {
			AbstractButton b = (AbstractButton) c;
			ButtonModel model = b.getModel();
			Font f = c.getFont();
			g.setFont(f);
			FontMetrics fm = c.getFontMetrics(f);

			Insets i = c.getInsets();
			size = b.getSize(size);
			viewRect.x = i.left;
			viewRect.y = i.top;
			viewRect.width = size.width - (i.right + viewRect.x);
			viewRect.height = size.height - (i.bottom + viewRect.y);
			iconRect.x = iconRect.y = iconRect.width = iconRect.height = 0;
			textRect.x = textRect.y = textRect.width = textRect.height = 0;

			String text = SwingUtilities.layoutCompoundLabel(
					c, fm, b.getText(), null, //altIcon != null ? altIcon : getDefaultIcon(),
					b.getVerticalAlignment(), b.getHorizontalAlignment(),
					b.getVerticalTextPosition(), b.getHorizontalTextPosition(),
					viewRect, iconRect, textRect,
					0); //b.getText() == null ? 0 : b.getIconTextGap());

			if(c.isOpaque()) {
				g.setColor(b.getBackground());
				g.fillRect(0,0, size.width, size.height);
			}
			if(text == null) return;

			g.setColor(b.getForeground());
			if(!model.isSelected() && !model.isPressed() && !model.isArmed()
					&& b.isRolloverEnabled() && model.isRollover()) {
				g.drawLine(viewRect.x, viewRect.y+viewRect.height,
						       viewRect.x+viewRect.width, viewRect.y+viewRect.height);
			}
			View v = (View) c.getClientProperty(BasicHTML.propertyKey);
			if(v != null) {
				v.paint(g, textRect);
			} else {
				paintText(g, b, textRect, text);
			}
		}
	}
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				JFrame frame = new JFrame();
				frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
				String[] header = {"Year", "String", "Comment"};
				Object[][] items = new Object[2008][] ;
				for(int i = 0; i < items.length; i++) {
					items[i] = new Object[] {i, "Test: " + i, (i%2==0) ? "" : "comment..."};
				}
				
				final JTablePagination table = new JTablePagination() ;
				table.setData(header, items, 50) ;
				table.getJTable().addMouseListener(new MouseAdapter(){     
					public void mouseClicked(MouseEvent e) {
						Point p = e.getPoint();
						int row = table.getJTable().rowAtPoint(p);
						int column = table.getJTable().columnAtPoint(p);
						if(row < 0) return ;
						System.out.println("select row: " + table.getJTable().convertRowIndexToModel(row));
					}
				});
				frame.getContentPane().add(table);
				frame.setSize(320, 240);
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}
}