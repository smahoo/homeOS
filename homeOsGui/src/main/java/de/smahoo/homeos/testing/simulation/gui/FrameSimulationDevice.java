package de.smahoo.homeos.testing.simulation.gui;

import javax.swing.JFrame;


import de.smahoo.homeos.device.DeviceEvent;
import de.smahoo.homeos.device.DeviceEventListener;
import de.smahoo.homeos.devices.MeterElectricity;
import de.smahoo.homeos.devices.SensorClimate;
import de.smahoo.homeos.testing.gui.PanelDetails;
import de.smahoo.homeos.testing.gui.devices.PanelMeterElectricity;
import de.smahoo.homeos.simulation.devices.SimDevice;
import de.smahoo.homeos.simulation.devices.SimHeating;
import de.smahoo.homeos.simulation.devices.SimMeterElectricity;
import de.smahoo.homeos.simulation.devices.SimSensorClimate;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.BorderLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JCheckBox;
import javax.swing.border.EtchedBorder;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

public class FrameSimulationDevice extends JFrame implements DeviceEventListener{

	private SimDevice currDevice = null;
	
	public FrameSimulationDevice(SimDevice device){
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent arg0) {
				removeEventListener();
			}
		});
		setSize(new Dimension(300, 300));
		this.currDevice = device;
		currDevice.addDeviceEventListener(this);
		initGui();		
		updateDetails();
	}
	
	private void removeEventListener(){
		if (currDevice != null){
			currDevice.removeDeviceEventListener(this);
		}
	}
	
	public void onDeviceEvent(DeviceEvent event){
		switch(event.getEventType()){
		case PROPERTY_VALUE_CHANGED:
		case DEVICE_PROPERTY_CHANGED:
		case DEVICE_AVAILABLE:
		case DEVICE_NOT_AVAILABLE:
		case DEVICE_RENAMED: updateDetails(); break;
		}
	}
	
	private void updateDetails(){
		this.setTitle(currDevice.getName());
		chkAvailable.setSelected(currDevice.isAvailable());
		devPanel.update();
		devPanel.revalidate();
	}
	
	private PanelDetails getDevicePanel(){		
		if (currDevice instanceof SimMeterElectricity){
			PanelSimMeterElectricity panel = new PanelSimMeterElectricity((SimMeterElectricity)currDevice);
			return panel;
		}
		if (currDevice instanceof SimSensorClimate){
			PanelSimSensorClimate panel = new PanelSimSensorClimate();
			panel.setSensor((SensorClimate)currDevice);
			return panel;		
		}
		if (currDevice instanceof SimHeating){
			PanelSimHeating panel = new PanelSimHeating((SimHeating)currDevice);
			return panel;
		}
		
		return null;
	}
	
	private void setAvailability(){
		if (currDevice instanceof SimDevice){
			((SimDevice)currDevice).setAvailable(chkAvailable.isSelected());
		}
		
	}
	
	private void initGui(){		
		getContentPane().setLayout(new BorderLayout(0, 0));
		devPanel = getDevicePanel();
		if (devPanel == null){
			devPanel = new PanelDetails() {
				
				@Override
				public void update() {
					// TODO Auto-generated method stub
					
				}
			};
		}
		getContentPane().add(devPanel,BorderLayout.CENTER);
		
		
		JPanel panel = new JPanel();
		panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel.setPreferredSize(new Dimension(10, 75));
		getContentPane().add(panel, BorderLayout.NORTH);
		
		chkAvailable = new JCheckBox("Available");
		chkAvailable.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				setAvailability();
			}
		});
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addComponent(chkAvailable)
					.addContainerGap(189, Short.MAX_VALUE))
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addComponent(chkAvailable)
					.addContainerGap(45, Short.MAX_VALUE))
		);
		panel.setLayout(gl_panel);
	
	}
	
	private PanelDetails devPanel;
	private JCheckBox chkAvailable;
}
