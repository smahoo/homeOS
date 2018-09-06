package de.smahoo.homeos.testing.gui.devices;


import de.smahoo.homeos.devices.Switch;
import de.smahoo.homeos.testing.gui.PanelDetails;

import java.awt.BorderLayout;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class PanelSwitch extends PanelDetails{

	private Switch currSwitch;
	
	
	
	public PanelSwitch(){
	
		initGui();
	}
	
	
	public void setSwitch(Switch newSwitch){
		currSwitch = newSwitch;
		update();
	}
	
	
	public void update(){
		DefaultTableModel model = new DefaultTableModel();
		model.addColumn("Button");
		model.addColumn("State");
		
		Object[] row;
		
		for (int i = 0; i< currSwitch.getButtonCount(); i++){
			row = new Object[2];
			row[0] = "Button "+(i+1);
			if (currSwitch.isButtonPressed(i)){
				row[1]="pressed";
			} else {
				row[1] = "released";
			}
			model.addRow(row);
		}
		tblButtons.setModel(model);
		
	}
	
	private void initGui(){
		setLayout(new BorderLayout(0, 0));
		
		scrollPane = new JScrollPane();
		add(scrollPane, BorderLayout.CENTER);
		
		tblButtons = new JTable();
		scrollPane.setViewportView(tblButtons);
	}
	
	private JScrollPane scrollPane;
	private JTable tblButtons;
}
