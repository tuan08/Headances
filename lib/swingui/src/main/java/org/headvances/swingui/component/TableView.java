package org.headvances.swingui.component;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextLayout;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.text.BreakIterator;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class TableView extends JTable {
	private DefaultTableModel  model ;
	private TableRowSorter<TableModel> sorter ;
	
	public TableView() {
    setFont(new Font(Font.SANS_SERIF, Font.TRUETYPE_FONT, 12)) ;
    setAutoscrolls(true) ;
    this.model = new DefaultTableModel(null, (String[])null) {
			public Class<?> getColumnClass(int column) {
			  if(getRowCount() == 0) return Object.class ;
			  return getValueAt(0, column).getClass();
			}
			
			public boolean isCellEditable(int row, int col) { return false ; }
		};
    setModel(model) ;
	}
	
	public TableView(String[] header, Object[][] data) {
		this() ;
		setData(header, data) ;
	}
	
	public void setMultilineCellRenderer(boolean bool) {
		if(bool) {
		  setDefaultRenderer(String.class, new MultilineTableCell());
		}
	}
	
	public void setCellRenderer(Class type, TableCellRenderer renderer) {
    setDefaultRenderer(type, renderer);
	}
	
	public void setColWidth(int[] width) {
		for(int i = 0; i < width.length; i++ ) {
			TableColumn col = getColumnModel().getColumn(i);
			if(width[i] > 0) col.setMaxWidth(width[i]) ;
			else col.setResizable(true) ;
		}
	}
	
	public void setData(String[] header, Object[][] data) {
		model.setDataVector(data, header) ;
	}
	
	public void updateRow(int row, Object[] data) {
	  for(int i = 0; i < data.length; i++) {
	    model.setValueAt(data[i], row, i) ;
	  }
	  model.fireTableRowsUpdated(row, row) ;
	}
	
	public int getModelSelectedRow() {
		int row = getSelectedRow() ;
		return convertRowIndexToModel(row) ;
	}
	
	public void setSortableColumn(int[] column) {
		sorter = new TableRowSorter<TableModel>(model);
		sorter.setSortKeys(Arrays.asList(new RowSorter.SortKey(0, SortOrder.ASCENDING)));
		for(int selColumn : column) {
			sorter.setSortable(selColumn, true) ;
		}
		setRowSorter(sorter);
	}
	
	public class MultilineTableCell implements TableCellRenderer {
		class CellArea extends DefaultTableCellRenderer {
			private static final long serialVersionUID = 1L;
			private String text;
			protected int rowIndex;
			protected int columnIndex;
			protected JTable table;
			private int paragraphStart,paragraphEnd;
			private LineBreakMeasurer lineMeasurer;

			public CellArea(String s, JTable tab, int row, int column,boolean isSelected) {
				text = s;
				rowIndex = row;
				columnIndex = column;
				this.table = tab;
				if (isSelected) {
					setForeground(table.getSelectionForeground());
					setBackground(table.getSelectionBackground());
				}
				setFont(tab.getFont()) ;
			}
			
			public void paintComponent(Graphics gr) {
				super.paintComponent(gr);
				if ( text != null && !text.isEmpty() ) {
					Graphics2D g = (Graphics2D) gr;
					if (lineMeasurer == null) {
						AttributedCharacterIterator paragraph = new AttributedString(text).getIterator();
						paragraphStart = paragraph.getBeginIndex();
						paragraphEnd = paragraph.getEndIndex();
						FontRenderContext frc = g.getFontRenderContext();
						lineMeasurer = new LineBreakMeasurer(paragraph,BreakIterator.getWordInstance(), frc);
					}
					float breakWidth = (float)table.getColumnModel().getColumn(columnIndex).getWidth();
					float drawPosY = 0;
					// Set position to the index of the first character in the paragraph.
					lineMeasurer.setPosition(paragraphStart);
					// Get lines until the entire paragraph has been displayed.
					while (lineMeasurer.getPosition() < paragraphEnd) {
						// Retrieve next layout. A cleverer program would also cache
						// these layouts until the component is re-sized.
						TextLayout layout = lineMeasurer.nextLayout(breakWidth);
						// Compute pen x position. If the paragraph is right-to-left we
						// will align the TextLayouts to the right edge of the panel.
						// Note: this won't occur for the English text in this sample.
						// Note: drawPosX is always where the LEFT of the text is placed.
						float drawPosX = layout.isLeftToRight() ? 0 : breakWidth - layout.getAdvance();
						// Move y-coordinate by the ascent of the layout.
						drawPosY += layout.getAscent();
						// Draw the TextLayout at (drawPosX, drawPosY).
						layout.draw(g, drawPosX, drawPosY);
						// Move y-coordinate in preparation for next layout.
						drawPosY += layout.getDescent() + layout.getLeading();
					}
					table.setRowHeight(rowIndex,(int) drawPosY);
				}
			}
		}
		
		public Component getTableCellRendererComponent(JTable table, Object value,boolean isSelected, boolean hasFocus, int row,int column) {
			CellArea area = new CellArea(value.toString(),table,row,column,isSelected);
			return area;
		}   
	}
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				JFrame frame = new JFrame();
				frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
				String[] header = {"Year", "String", "Comment"};
				Object[][] items = new Object[200][] ;
				for(int i = 0; i < items.length; i++) {
					items[i] = new Object[] {i, "Test: " + i, (i%2==0) ? "" : "comment..."};
				}
				
				final TableView table = new TableView() ;
				table.setData(header, items) ;
				table.setSortableColumn(new int[] {0, 1}) ;
				table.addMouseListener(new MouseAdapter(){     
					public void mouseClicked(MouseEvent e) {
						Point p = e.getPoint();
						int row = table.rowAtPoint(p);
						int column = table.columnAtPoint(p);
						if(row < 0) return ;
						System.out.println("select row: " + table.convertRowIndexToModel(row));
					}
				});
				frame.getContentPane().add(new JScrollPane(table));
				frame.setSize(600, 400);
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}
}
