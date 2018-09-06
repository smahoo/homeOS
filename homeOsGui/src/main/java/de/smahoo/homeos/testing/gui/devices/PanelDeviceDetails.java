package de.smahoo.homeos.testing.gui.devices;

import javax.swing.JPanel;


import de.smahoo.homeos.automation.Rule;
import de.smahoo.homeos.automation.RuleGroup;
import de.smahoo.homeos.device.Device;
import de.smahoo.homeos.device.DeviceEvent;
import de.smahoo.homeos.device.DeviceEventListener;
import de.smahoo.homeos.device.DeviceImpl;
import de.smahoo.homeos.device.DeviceType;
import de.smahoo.homeos.device.PhysicalDevice;
import de.smahoo.homeos.device.role.DeviceRole;
import de.smahoo.homeos.devices.Dimmable;
import de.smahoo.homeos.devices.ExtendedTelevision;
import de.smahoo.homeos.devices.HeatingRtc;
import de.smahoo.homeos.devices.Lamp;
import de.smahoo.homeos.devices.SensorClimate;
import de.smahoo.homeos.devices.SensorHumidity;
import de.smahoo.homeos.devices.SensorTemperature;
import de.smahoo.homeos.devices.SensorWindow;
import de.smahoo.homeos.devices.Socket;
import de.smahoo.homeos.devices.Switch;
import de.smahoo.homeos.devices.Television;
import de.smahoo.homeos.driver.Driver;
import de.smahoo.homeos.location.Location;
import de.smahoo.homeos.testing.gui.PanelDetails;
import de.smahoo.homeos.testing.gui.SelectionListener;
import de.smahoo.homeos.testing.gui.db.FrameHistoryData;

import javax.swing.JSplitPane;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.BorderLayout;
import javax.swing.border.EtchedBorder;
import java.awt.FlowLayout;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class PanelDeviceDetails extends JPanel implements SelectionListener, DeviceEventListener{
	
	private PanelDetails currPanelDevice =null;
	private PanelDetails currPanelExtendedDevice = null;
	private Device currDevice = null;
	
	public PanelDeviceDetails() {
		initGui();
		setDevice(null);
	}

	public void setDevice(Device device){		
		if (device == currDevice) return;
		if (currDevice != null){
			((DeviceImpl)currDevice).removeDeviceEventListener(this);
		}		
		currDevice = device;		
		setPanel(null);
		if (currDevice != null){
			((DeviceImpl)currDevice).addDeviceEventListener(this);
		}
		panelCommonDeviceDetails.setDevice(device);
		setExtendedPanel(device);
		
	}
	
	private void updateDetails(){		
		if (currPanelDevice != null) {
			currPanelDevice.update();
		}
		if (currPanelExtendedDevice != null){
			currPanelExtendedDevice.update();
		}
		panelCommonDeviceDetails.update();
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
	
	public void onDeviceSelected(Device device){
		//
	}
	
	public void onDriverSelected(Driver driver){
		//
	}
	
	public void onLocationSelected(Location location){
		//
	}	
	
	public void onRuleSelected(Rule rule){
		
	}  
	
	public void onRuleGroupSelected(RuleGroup group){
		
	}
	
	
	public void onDeviceTypeSelected(DeviceType deviceType){
		if (deviceType == null){
			setPanel(null);
			return;
		}
		switch(deviceType){
		case LAMP: setLamp((Lamp)panelCommonDeviceDetails.getDevice()); return;
			case SOCKET : setSocket((Socket)panelCommonDeviceDetails.getDevice()); return;
			case SENSOR_CLIMATE: setSensorClimate((SensorClimate)panelCommonDeviceDetails.getDevice()); return;
			case SENSOR_HUMIDITY: setSensorHumidity((SensorHumidity)panelCommonDeviceDetails.getDevice()); return;
			case SENSOR_TEMPERATURE: setSensorTemperature((SensorTemperature)panelCommonDeviceDetails.getDevice()); return;
			case TELEVISION: setTelevision((Television)panelCommonDeviceDetails.getDevice()); return;
			case TELEVISION_EXTENDED: setTelevisionExtended((ExtendedTelevision)panelCommonDeviceDetails.getDevice()); return;
			case DIMMER : setDimmer((Dimmable)panelCommonDeviceDetails.getDevice()); return;
			case HEATING_RTC: setHeating((HeatingRtc)panelCommonDeviceDetails.getDevice()); return;
			case SENSOR_WINDOW: setSensorWindow((SensorWindow)panelCommonDeviceDetails.getDevice()); return;
			case SWITCH : setSwitch((Switch)panelCommonDeviceDetails.getDevice()); return;
		
		}
		setPanel(null);
	}
	
	private void getHistoryData(){
		FrameHistoryData frame = new FrameHistoryData();
		frame.setDevice(this.currDevice);
		frame.setVisible(true);
	}
	
	private void setExtendedPanel(Device device){
		if (device == null){
			setExtendedPanel((PanelDetails)null);
			return;
		}
		if (device instanceof PhysicalDevice){
			PhysicalDevice dev = (PhysicalDevice)device;
			PanelPhysicalDevice panel = new PanelPhysicalDevice();
			panel.setPhysicalDevice(dev);
			setExtendedPanel(panel);
			return;
		}
		if (device instanceof DeviceRole){
			DeviceRole role = (DeviceRole)device;
			PanelDeviceRole panel = new PanelDeviceRole();
			panel.setDeviceRole(role);
			setExtendedPanel(panel);
			return;
		}
		setExtendedPanel((PanelDetails)null);
	}
	
	
	
	private void setExtendedPanel(PanelDetails panel){
		if (currPanelExtendedDevice != null){
			pnlRight.remove(currPanelExtendedDevice);
			repaint();
		}
		currPanelExtendedDevice = panel;
		if (panel == null){
			this.revalidate();
			return;
		}
		pnlRight.add(currPanelExtendedDevice);
		this.revalidate();
	}
	
	private void setPanel(PanelDetails panel){
		if (currPanelDevice != null){			
				panelDevice.remove(currPanelDevice);				
				this.repaint();
		}
		currPanelDevice=panel;
		if (panel == null){
			this.revalidate();
			return;
		}
		panelDevice.add(currPanelDevice);
		this.revalidate();			
	}
	
	private void setSwitch(Switch s){
		PanelSwitch panel = new PanelSwitch();
		panel.setSwitch(s);
		setPanel(panel);
	}
	
	private void setSensorWindow(SensorWindow sensorWindow){
		PanelSensorWindow panel = new PanelSensorWindow();
		panel.setSensorWindow(sensorWindow);
		setPanel(panel);
	}
	
	private void setHeating(HeatingRtc heating){
		PanelHeating panel = new PanelHeating();
		panel.setHeating(heating);
		setPanel(panel);
	}
	
	private void setDimmer(Dimmable dimmer){
		PanelDimmer panel = new PanelDimmer();
		panel.setDimmer(dimmer);
		setPanel(panel);
	}
	
	private void setSensorHumidity(SensorHumidity sensor){
		PanelSensorHumidity panel = new PanelSensorHumidity();
		panel.setSensorHumidity(sensor);
		setPanel(panel);
	}
	
	private void setTelevisionExtended(ExtendedTelevision tv){
		PanelTelevisionExtended panel = new PanelTelevisionExtended();
		panel.setTelevision(tv);
		setPanel(panel);
	}
	
	private void setTelevision(Television tv){
		PanelTelevision panel = new PanelTelevision();
		panel.setTelevision(tv);
		setPanel(panel);
	}
	
	private void setSensorTemperature(SensorTemperature sensor){
		PanelSensorTemperature panel = new PanelSensorTemperature();
		panel.setSensorTemperature(sensor);		
		setPanel(panel);
	}
	
	private void setSensorClimate(SensorClimate sensor){
		PanelSensorClimate panel = new PanelSensorClimate();
		panel.setSensorClimate(sensor);
		setPanel(panel);
	}
	
	private void setSocket(Socket socket){
		PanelSocket panel = new PanelSocket();
		panel.setSocket(socket);
		setPanel(panel);
	}
	
	private void setLamp(Lamp lamp){		
		PanelLamp panel = new PanelLamp();
		panel.setLamp(lamp);
		setPanel(panel);
	}
	
	private void initGui(){
		setLayout(new BorderLayout(0, 0));
		
		splitPane = new JSplitPane();
		splitPane.setBorder(null);
		add(splitPane);
		
		pnlLeft = new JPanel();
		pnlLeft.setPreferredSize(new Dimension(450, 10));
		pnlLeft.setSize(new Dimension(450, 0));
		splitPane.setLeftComponent(pnlLeft);
		pnlLeft.setLayout(new BorderLayout(0, 0));
		
		panelCommonDeviceDetails = new PanelCommonDeviceDetails();
		panelCommonDeviceDetails.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panelCommonDeviceDetails.addSelectionListener(this);
		panelCommonDeviceDetails.setPreferredSize(new Dimension(500, 250));
		pnlLeft.add(panelCommonDeviceDetails, BorderLayout.NORTH);
		
		panelDevice = new JPanel();
		panelDevice.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		pnlLeft.add(panelDevice, BorderLayout.CENTER);
		panelDevice.setLayout(new BorderLayout(0, 0));
		
		panel_1 = new JPanel();
		panel_1.setPreferredSize(new Dimension(10, 50));
		panel_1.setSize(new Dimension(100, 100));
		pnlLeft.add(panel_1, BorderLayout.SOUTH);
		panel_1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		btnHistoryData = new JButton("getHistoryData");
		btnHistoryData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				getHistoryData();
			}
		});
		panel_1.add(btnHistoryData);
		
		pnlRight = new JPanel();
		splitPane.setRightComponent(pnlRight);
		pnlRight.setLayout(new BorderLayout(0, 0));
		
		
	}
	
	private JSplitPane splitPane;
	private PanelCommonDeviceDetails panelCommonDeviceDetails;
	private JPanel pnlLeft;
	private JPanel panelDevice;
	private JPanel pnlRight;
	private JPanel panel_1;
	private JButton btnHistoryData;
}
