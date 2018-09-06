package de.smahoo.homeos.testing.gui.devices;


import de.smahoo.homeos.devices.SensorWindow;
import de.smahoo.homeos.testing.gui.PanelDetails;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class PanelSensorWindow extends PanelDetails{	
	
	private SensorWindow sensorWindow = null;
	
	public PanelSensorWindow(){		
		initGui();
	}	
	
	public void update(){
		if (sensorWindow != null){
			if (sensorWindow.isOpen()){
				lbWindow.setText("Window is open");
			} else {
				lbWindow.setText("Window is closed");
			}
		} else {
			lbWindow.setText("");
		}
	}
	
	public void setSensorWindow(SensorWindow sensorWindow){
		this.sensorWindow = sensorWindow;
		update();
	}
	
	public SensorWindow getSensorWindow(){
		return sensorWindow;
	}
	
	private void initGui(){
		lbWindow = new JLabel("");
		lbWindow.setHorizontalAlignment(SwingConstants.CENTER);
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(lbWindow, GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(lbWindow)
					.addContainerGap(275, Short.MAX_VALUE))
		);
		setLayout(groupLayout);
	}
	
	private JLabel lbWindow;
}
