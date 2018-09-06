package de.smahoo.homeos.testing.gui.event;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

public class EventTableCellRenderer implements TableCellRenderer{
	  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
		  DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
		  Component component = renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		  if (isSelected){
			  component.setBackground(Color.DARK_GRAY);
			  component.setForeground(Color.white);
			  return component;
		  }
		  if (row % 2 == 0){
			  component.setBackground(Color.white);
		  } else {
			  component.setBackground(new Color(16119285)); // very light gray
		  }
		  return component;
	  }
}
