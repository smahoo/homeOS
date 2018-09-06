package de.smahoo.homeos.testing.simulation.gui;


import de.smahoo.homeos.device.PhysicalDevice;
import de.smahoo.homeos.devices.SensorClimate;
import de.smahoo.homeos.testing.gui.PanelDetails;
import de.smahoo.homeos.simulation.devices.SimSensorClimate;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.border.EmptyBorder;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PanelSimSensorClimate extends PanelDetails{
	
	SensorClimate currSensor = null;
	PhysicalDevice phyDevice = null;
	
	public PanelSimSensorClimate(){
		setBorder(new EmptyBorder(0, 0, 0, 5));	
		initGui();
		update();
	}
	
	public void update(){
		setValues();
		updateLblTemperature();
		updateLblHumidity();
	}
	
	public void setSensor(SensorClimate sensor){
		currSensor = sensor;
		if (sensor instanceof PhysicalDevice){
			setPhysicalDevice((PhysicalDevice)sensor);			
		} else {
			setPhysicalDevice(null);
		}
		setValues();
	}
	
	private void setPhysicalDevice(PhysicalDevice device){
		phyDevice = device;		
		update();
	}
	public SensorClimate getSensor(){
		return currSensor;
	}
	
	private void setValues(){
		if (phyDevice == null) return;
		if (phyDevice.getProperty("temperature").isValueSet()){
			double temp = (Double)(phyDevice.getProperty("temperature").getValue());
			sliderTemperature.setValue((int)(temp*2));
		}
		if (phyDevice.getProperty("humidity").isValueSet()){
			double hum = (Double)(phyDevice.getProperty("humidity").getValue());		
			sliderHumidity.setValue((int)hum);
		}
	}
	
	private void setTemperature(){
		double temp = ((double)sliderTemperature.getValue()/2.0);
		((SimSensorClimate)currSensor).setTemperature(temp);
	}
	
	private void setHumidity(){
		double hum = sliderHumidity.getValue();
		((SimSensorClimate)currSensor).setHumidity(hum);
	}
	
	private void updateLblTemperature(){
		lbTemperature.setText(""+((double)sliderTemperature.getValue()/2.0)+"°C");
	}
	
	private void updateLblHumidity(){
		lbHumidity.setText(""+sliderHumidity.getValue()+"%");
	}
	
	private void initGui(){
		JLabel lblNewLabel = new JLabel("Temperature");
		lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JLabel lblNewLabel_1 = new JLabel("Humidity");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.RIGHT);
		
		sliderTemperature = new JSlider();
		sliderTemperature.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				setTemperature();
			}
		});
		sliderTemperature.setValue(0);
		sliderTemperature.setMinimum(-30);
		sliderTemperature.setMaximum(75);
		sliderTemperature.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				updateLblTemperature();				
			}
		});
		
		sliderHumidity = new JSlider();
		sliderHumidity.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				setHumidity();
			}
		});
		sliderHumidity.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				updateLblHumidity();
			}
		});
		
		lbTemperature = new JLabel("22.5\u00B0C");
		lbTemperature.setHorizontalAlignment(SwingConstants.RIGHT);
		
		lbHumidity = new JLabel("100%");
		lbHumidity.setHorizontalAlignment(SwingConstants.RIGHT);
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(lblNewLabel)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(sliderTemperature, 0, 0, Short.MAX_VALUE))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(lblNewLabel_1, GroupLayout.PREFERRED_SIZE, 62, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(sliderHumidity, GroupLayout.DEFAULT_SIZE, 156, Short.MAX_VALUE)))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
						.addComponent(lbHumidity, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(lbTemperature, GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addComponent(lblNewLabel))
						.addComponent(sliderTemperature, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE)
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addComponent(lbTemperature)))
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(18)
							.addComponent(lblNewLabel_1))
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(6)
							.addComponent(sliderHumidity, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(15)
							.addComponent(lbHumidity)))
					.addContainerGap(69, Short.MAX_VALUE))
		);
		setLayout(groupLayout);
	}
	
	private JSlider sliderTemperature;
	private JSlider sliderHumidity;
	private JLabel lbTemperature;
	private JLabel lbHumidity;
}
