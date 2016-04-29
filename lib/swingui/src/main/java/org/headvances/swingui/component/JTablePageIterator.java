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
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

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
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.plaf.basic.BasicRadioButtonUI;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.View;

import org.headvances.util.ListPageList;
import org.headvances.util.PageList;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class JTablePageIterator extends JPanel {
	private static final Color evenColor = new Color(240, 255, 250);

	private final LinkViewRadioButtonUI ui = new LinkViewRadioButtonUI();
	private final Box box = Box.createHorizontalBox();
	
	private final JTable table = new JTable() {
		@Override public Component prepareRenderer(TableCellRenderer tcr, int row, int column) {
			Component c = super.prepareRenderer(tcr, row, column);
			if(isRowSelected(row)) {
				c.setForeground(getSelectionForeground());
				c.setBackground(getSelectionBackground());
			} else {
				c.setForeground(getForeground());
				c.setBackground((row % 2 == 0) ? evenColor : getBackground());
			}
			return c;
		}
	};
	
	public JTablePageIterator(JTablePageIteratorModel tmodel) {
		table.setModel(tmodel) ;
		table.setFillsViewportHeight(true);
		table.setIntercellSpacing(new Dimension());
		table.setShowGrid(false) ;
		table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		
		setLayout(new BorderLayout()) ;
		add(new JScrollPane(table));
		add(box, BorderLayout.SOUTH);
		renderPageBox();
	}
	
	private void renderPageBox() {
		JTablePageIteratorModel<?> model = (JTablePageIteratorModel<?>)table.getModel() ;
		PageList<?> items = model.getItems() ;
		final int currPageIdx = items.getCurrentPage() ;
		int[] range = items.getSubRange(items.getCurrentPage(), 10) ;
		ArrayList<JRadioButton> l = new ArrayList<JRadioButton>();

		int startPageIndex = range[0];
		int endPageIndex   = range[1];

		if(currPageIdx > 1) l.add(makeRadioButton(currPageIdx, currPageIdx - 1, "Prev"));
		for(int i = startPageIndex; i <= endPageIndex; i++)
			l.add(makeRadioButton(currPageIdx, i , "" + i));
		if(currPageIdx < items.getAvailablePage())
			l.add(makeRadioButton(currPageIdx, currPageIdx + 1, "Next"));

		box.removeAll();
		ButtonGroup bg = new ButtonGroup();
		box.add(Box.createHorizontalGlue());
		for(JRadioButton r : l) {
			box.add(r); bg.add(r);
		}
		box.add(Box.createHorizontalGlue());
		box.revalidate();
		box.repaint()   ;
	}

	private JRadioButton makeRadioButton(final int current, final int target, final String title) {
		JRadioButton rbutton = new JRadioButton(title) {
			protected void fireStateChanged() {
				ButtonModel model = getModel();
				if(!model.isEnabled()) setForeground(Color.GRAY);
				else if(model.isPressed() && model.isArmed()) setForeground(Color.GREEN);
				else if(model.isSelected()) setForeground(Color.RED);
				super.fireStateChanged();
			}
		};
		rbutton.setForeground(Color.BLUE);
		rbutton.setUI(ui);
		if(target == current) {
			rbutton.setSelected(true);
		}
		rbutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JTablePageIteratorModel<?> model = (JTablePageIteratorModel<?>)table.getModel() ;
				try {
	        model.setPage(target) ;
	        model.fireTableDataChanged() ;
        } catch (Exception ex) {
	        ex.printStackTrace();
        }
				renderPageBox();
			}
		});
		return rbutton;
	}
	
	static class LinkViewRadioButtonUI extends BasicRadioButtonUI {
		private static Dimension size     = new Dimension();
		private static Rectangle viewRect = new Rectangle();
		private static Rectangle iconRect = new Rectangle();
		private static Rectangle textRect = new Rectangle();

		public Icon getDefaultIcon() { return null ; }
		
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

			String text = 
				SwingUtilities.layoutCompoundLabel(
					c, fm, b.getText(), null, //altIcon != null ? altIcon : getDefaultIcon(),
							b.getVerticalAlignment(), b.getHorizontalAlignment(),
							b.getVerticalTextPosition(), b.getHorizontalTextPosition(),
							viewRect, iconRect, textRect,
							0); //b.getText() == null ? 0 : b.getIconTextGap());

			if(c.isOpaque()) {
				g.setColor(b.getBackground());
				g.fillRect(0,0, size.width, size.height);
			}
			
			if(text==null) return;

			g.setColor(b.getForeground());
			if(!model.isSelected() && !model.isPressed() && !model.isArmed() && b.isRolloverEnabled() && model.isRollover()) {
				g.drawLine(viewRect.x, viewRect.y+viewRect.height, viewRect.x + viewRect.width, viewRect.y + viewRect.height);
			}
			View v = (View) c.getClientProperty(BasicHTML.propertyKey);
			if(v != null) {
				v.paint(g, textRect);
			} else{
				paintText(g, b, textRect, text);
			}
		}
	}

	static class TestTableModel extends JTablePageIteratorModel<Object[]> {
		static final String[] COLUMN_NAMES = { "Year", "String", "Comment"};
    
		public TestTableModel() throws Exception {
			super(genData()) ;
		}

		public String getColumnName(int columnIndex) { return COLUMN_NAMES[columnIndex] ; }
		
		public int getColumnCount() { return COLUMN_NAMES.length ; }

    public Object getValueAt(int row, int column) { return currPage.get(row)[column] ; }
	
    static ListPageList<Object[]> genData() throws Exception {
			Object[][] data = new Object[500][] ;
			for(int i = 0; i < data.length ; i++) {
				data[i] = new Object[] { i, "Test: " + i, (i%2==0) ? "" : "comment..." } ;
			}
			ListPageList<Object[]> items = new ListPageList<Object[]>(10, data) ;
			return items ;
    }
	}
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				JFrame frame = new JFrame();
				frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
				try {
	        frame.getContentPane().add(new JTablePageIterator(new TestTableModel()));
        } catch (Exception e) {
	        e.printStackTrace();
        }
				frame.setSize(320, 240);
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}
}