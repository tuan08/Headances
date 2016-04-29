/**
 * Copyright (C) 2011 Headvances Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.

 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * This project aim to build a set of library/data to process 
 * the Vietnamese language and analyze the web data
 **/
package org.headvances.swingui.component;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 * $Author: Tuan Nguyen$ 
 **/
public class SortTable {
	public static void main(String args[]) {
		Runnable runner = new Runnable() {
			public void run() {
				JFrame frame = new JFrame("Sorting JTable");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				Object rows[][] = {
						{"AMZN", "Amazon", 41.28},
						{"EBAY", "eBay", 41.57},
						{"GOOG", "Google", 388.33},
						{"MSFT", "Microsoft", 26.56},
						{"NOK", "Nokia Corp", 17.13},
						{"ORCL", "Oracle Corp.", 12.52},
						{"SUNW", "Sun Microsystems", 3.86},
						{"TWX",  "Time Warner", 17.66},
						{"VOD",  "Vodafone Group", 26.02},
						{"YHOO", "Yahoo!", 37.69}
				};
				String columns[] = {"Symbol", "Name", "Price"};
				DefaultTableModel model = new DefaultTableModel(rows, columns) {
					public Class getColumnClass(int column) {
						Class returnValue;
						if ((column >= 0) && (column < getColumnCount())) {
							returnValue = getValueAt(0, column).getClass();
						} else {
							returnValue = Object.class;
						}
						return returnValue;
					}
				};

				final JTable table = new JTable(model);
				RowSorter<TableModel> sorter = new TableRowSorter<TableModel>(model);
				table.setRowSorter(sorter);
				table.addMouseListener(new MouseAdapter(){     
					public void mouseClicked(MouseEvent e) {
						Point p = e.getPoint();
						int row = table.rowAtPoint(p);
						int column = table.columnAtPoint(p);
						System.out.println("select row: " + table.convertRowIndexToModel(row));
					}
				});

				JScrollPane pane = new JScrollPane(table);
				frame.add(pane, BorderLayout.CENTER);
				frame.setSize(300, 150);
				frame.setVisible(true);
			}
		};
		EventQueue.invokeLater(runner);
	}
} 