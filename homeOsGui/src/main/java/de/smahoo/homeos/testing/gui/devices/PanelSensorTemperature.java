package de.smahoo.homeos.testing.gui.devices;


import de.smahoo.homeos.devices.SensorTemperature;
import de.smahoo.homeos.testing.gui.PanelDetails;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.LayoutStyle.ComponentPlacement;

public class PanelSensorTemperature extends PanelDetails{

	private SensorTemperature currSensor;
	
	public void update(){
		
	}
	
	public PanelSensorTemperature(){
		
		JLabel lblNewLabel = new JLabel("Temperature");
		lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		
		lbTemperature = new JLabel("<temperature>");
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblNewLabel, GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lbTemperature, GroupLayout.DEFAULT_SIZE, 214, Short.MAX_VALUE)
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel)
						.addComponent(lbTemperature))
					.addContainerGap(275, Short.MAX_VALUE))
		);
		setLayout(groupLayout);
		initGui();
	}
	
	public void setSensorTemperature(SensorTemperature sensor){
		currSensor = sensor;
		if (sensor == null) {
			lbTemperature.setText("");
		} else {
			lbTemperature.setText(sensor.getTemperature()+" °C");
		}
	}
	
	public SensorTemperature getSensorTemperature(){
		return currSensor;
	}
	
	private void initGui(){
		
	}

	private JLabel lbTemperature;
}
