package de.smahoo.homeos.testing.gui.devices;


import de.smahoo.homeos.devices.SensorClimate;
import de.smahoo.homeos.testing.gui.PanelDetails;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.LayoutStyle.ComponentPlacement;

public class PanelSensorClimate extends PanelDetails{

	private SensorClimate currSensor = null;
	
	public PanelSensorClimate(){	
		initGui();
	}
	
	public void update(){
		lbTemperature.setText(currSensor.getTemperature()+" °C");
		lbHumidity.setText(currSensor.getHumidity()+" %");
	}
	
	public void setSensorClimate(SensorClimate sensor){
		currSensor = sensor;
		if (sensor == null) return;
		update();
	}
	
	public SensorClimate getSensorClimate(){
		return currSensor;
	}
	
	private void initGui(){
		JLabel lblNewLabel = new JLabel("Temperature");
		lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		
		lbTemperature = new JLabel("<temperature>");
		
		JLabel lblNewLabel_1 = new JLabel("Humidity");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.RIGHT);
		
		lbHumidity = new JLabel("<humidity>");
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(lblNewLabel_1, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(lblNewLabel, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(lbHumidity, GroupLayout.DEFAULT_SIZE, 156, Short.MAX_VALUE)
						.addComponent(lbTemperature, GroupLayout.DEFAULT_SIZE, 156, Short.MAX_VALUE))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel)
						.addComponent(lbTemperature))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel_1)
						.addComponent(lbHumidity))
					.addContainerGap(255, Short.MAX_VALUE))
		);
		setLayout(groupLayout);
	}
	
	private JLabel lbTemperature;
	private JLabel lbHumidity;
	
}
