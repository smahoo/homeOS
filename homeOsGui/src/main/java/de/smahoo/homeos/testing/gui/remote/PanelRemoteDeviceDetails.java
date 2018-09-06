package de.smahoo.homeos.testing.gui.remote;


import de.smahoo.homeos.device.Device;
import de.smahoo.homeos.device.DeviceType;
import de.smahoo.homeos.devices.Dimmable;
import de.smahoo.homeos.devices.HeatingRtc;
import de.smahoo.homeos.devices.Lamp;
import de.smahoo.homeos.devices.MeterElectricity;
import de.smahoo.homeos.devices.SensorClimate;
import de.smahoo.homeos.devices.SensorHumidity;
import de.smahoo.homeos.devices.SensorTemperature;
import de.smahoo.homeos.devices.SensorWindow;
import de.smahoo.homeos.devices.Socket;
import de.smahoo.homeos.remote.RemoteAPI;
import de.smahoo.homeos.testing.gui.PanelDetails;
import de.smahoo.homeos.testing.gui.devices.PanelDimmer;
import de.smahoo.homeos.testing.gui.devices.PanelHeating;
import de.smahoo.homeos.testing.gui.devices.PanelLamp;
import de.smahoo.homeos.testing.gui.devices.PanelMeterElectricity;
import de.smahoo.homeos.testing.gui.devices.PanelSensorClimate;
import de.smahoo.homeos.testing.gui.devices.PanelSensorHumidity;
import de.smahoo.homeos.testing.gui.devices.PanelSensorTemperature;
import de.smahoo.homeos.testing.gui.devices.PanelSensorWindow;
import de.smahoo.homeos.testing.gui.devices.PanelSocket;

import java.awt.GridLayout;
import java.util.List;

import javax.swing.JPanel;
import java.awt.CardLayout;

public class PanelRemoteDeviceDetails extends PanelDetails{
	private Device currentDevice = null;
	private RemoteAPI remoteApi = null;
	private PanelDetails currentPanelDetails = null;
	
	public PanelRemoteDeviceDetails(RemoteAPI remoteApi) {
		this.remoteApi = remoteApi;
		initGui();
	}

	
	public void setDevice(Device device){
		currentDevice = device;
		panelCommonRemoteDetails.setDevice(device);
		setPanelDeviceType();		
	}
	
	private void setPanelDeviceType(){
		List<DeviceType> devTypeList = DeviceType.getDeviceTypes(currentDevice);
		DeviceType devType = null;
		if (devTypeList.size() > 0){
			devType = devTypeList.get(0);
		}
		if (devType == null){
			// ...
			return;
		}
		if (currentPanelDetails != null){
			panelDeviceDetails.remove(currentPanelDetails);
			currentPanelDetails = null;
		}
		switch (devType){
		case LAMP: currentPanelDetails = generatePanelLamp(); break;
		case DIMMER: currentPanelDetails = generatePanelDimmer(); break;
		case SOCKET: currentPanelDetails = generatePanelSocket(); break;
		case SENSOR_CLIMATE: currentPanelDetails = generatePanelClimate(); break;
		case SENSOR_TEMPERATURE : currentPanelDetails = generatePanelTemperature(); break;
		case SENSOR_HUMIDITY : currentPanelDetails = generatePanelHumidity(); break;
		case SENSOR_WINDOW: currentPanelDetails = generatePanelWindow(); break;
		case HEATING_RTC: currentPanelDetails = generatePanelHeating(); break;
		case METER_ELECTRICITY: currentPanelDetails = generatePanelMeterElectricity(); break;
		}
		if (currentPanelDetails != null){
			panelDeviceDetails.add(currentPanelDetails);
		}
	}
	
	
	private PanelDetails generatePanelMeterElectricity(){
		PanelMeterElectricity panel = new PanelMeterElectricity();
		panel.setMeter((MeterElectricity)currentDevice);
		return panel;
	}
	
	private PanelDetails generatePanelHeating(){
		PanelHeating panel = new PanelHeating();
		panel.setHeating((HeatingRtc)currentDevice);
		return panel;
	}
	
	private PanelDetails generatePanelClimate(){
		PanelSensorClimate panel = new PanelSensorClimate();
		panel.setSensorClimate((SensorClimate)currentDevice);
		return panel;
	}
	
	private PanelDetails generatePanelTemperature(){
		PanelSensorTemperature panel = new PanelSensorTemperature();
		panel.setSensorTemperature((SensorTemperature)currentDevice);
		return panel;
	}
	
	private PanelDetails generatePanelHumidity(){
		PanelSensorHumidity panel = new PanelSensorHumidity();
		panel.setSensorHumidity((SensorHumidity)currentDevice);
		return panel;
	}
	
	private PanelDetails generatePanelWindow(){
		PanelSensorWindow panel = new PanelSensorWindow();
		panel.setSensorWindow((SensorWindow)currentDevice);
		return panel;
	}
	
	private PanelDetails generatePanelSocket(){
		PanelSocket panel = new PanelSocket();
		panel.setSocket((Socket)currentDevice);
		return panel;
	}
	
	private PanelDetails generatePanelDimmer(){
		PanelDimmer panel = new PanelDimmer();
		panel.setDimmer((Dimmable)currentDevice);
		return panel;
	}
	
	private PanelDetails generatePanelLamp(){
		PanelLamp panel = new PanelLamp();
		panel.setLamp((Lamp)currentDevice);
		return panel;
	}
	
	public void update(){
		panelCommonRemoteDetails.update();
		if (currentPanelDetails != null){
			currentPanelDetails.update();
		}
	}
	
	private void initGui(){
		setLayout(new GridLayout(1, 0, 0, 0));
		
		panelCommonRemoteDetails = new PanelCommonRemoteDetails(remoteApi);
		add(panelCommonRemoteDetails);
		
		panelDeviceDetails = new JPanel();
		add(panelDeviceDetails);
	
	}
	
	private PanelCommonRemoteDetails panelCommonRemoteDetails;
	private JPanel panelDeviceDetails;
}
