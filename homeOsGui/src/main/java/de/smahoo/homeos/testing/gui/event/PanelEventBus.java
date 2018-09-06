package de.smahoo.homeos.testing.gui.event;

import javax.swing.JPanel;


import de.smahoo.homeos.common.Event;
import de.smahoo.homeos.common.EventListener;
import de.smahoo.homeos.kernel.HomeOs;

import java.awt.CardLayout;
import javax.swing.JTabbedPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.Color;

public class PanelEventBus extends JPanel{

	private EventTableModel tableModel;
	
	
	public PanelEventBus(){		
		
		initGui();
		setTableModel();
		HomeOs.getInstance().getEventBus().addListener(new EventListener() {
			
			@Override
			public void onEvent(Event event) {
				evalEvent(event);				
			}
		});
	}

	private void setTableModel(){
		tableModel = new EventTableModel();
		tblEvents.setModel(tableModel);
		tblEvents.setDefaultRenderer(String.class, new EventTableCellRenderer());	
	}
	
	private void evalEvent(Event event){
		tableModel.addEvent(event);
		scrollPane.getVerticalScrollBar().setValue(tblEvents.getHeight());
	}
	
	private void initGui(){
		setLayout(new CardLayout(0, 0));
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		add(tabbedPane, "name_25570514901754");
		
		JPanel panel = new JPanel();
		tabbedPane.addTab("All Events", null, panel, null);
		panel.setLayout(new CardLayout(0, 0));
		
		scrollPane = new JScrollPane();
		panel.add(scrollPane, "name_25597119874173");
		
		tblEvents = new JTable();
		tblEvents.setGridColor(Color.WHITE);
		scrollPane.setViewportView(tblEvents);
	}

	private JTable tblEvents;
	private JScrollPane scrollPane;
}
