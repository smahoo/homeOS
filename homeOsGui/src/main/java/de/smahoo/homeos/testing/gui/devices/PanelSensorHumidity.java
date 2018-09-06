package de.smahoo.homeos.testing.gui.devices;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;


import de.smahoo.homeos.devices.SensorHumidity;
import de.smahoo.homeos.testing.gui.PanelDetails;

public class PanelSensorHumidity extends PanelDetails{

	private SensorHumidity currSensor;
	
	public PanelSensorHumidity(){
		
		
		initGui();
	}
	
	public void update(){
		
	}
	
	public void setSensorHumidity(SensorHumidity sensor){
		currSensor = sensor;
		if (sensor == null) {
			lbHumditiy.setText("");
		} else {
			lbHumditiy.setText(sensor.getHumidity()+" %");
		}
	}
	
	public SensorHumidity getSensorTemperature(){
		return currSensor;
	}
	
	private void initGui(){
		JLabel lblNewLabel = new JLabel("Humditity");
		lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		
		lbHumditiy = new JLabel("<humidity>");
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblNewLabel, GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lbHumditiy, GroupLayout.DEFAULT_SIZE, 214, Short.MAX_VALUE)
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel)
						.addComponent(lbHumditiy))
					.addContainerGap(275, Short.MAX_VALUE))
		);
		setLayout(groupLayout);
	}

	private JLabel lbHumditiy;
}
