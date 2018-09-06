package de.smahoo.homeos.remote;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.smahoo.homeos.device.DeviceType;
import de.smahoo.homeos.remote.connection.RemoteConnection;
import de.smahoo.homeos.remote.devices.RemoteDimmer;
import de.smahoo.homeos.remote.devices.RemoteExtendedTV;
import de.smahoo.homeos.remote.devices.RemoteHeatingRtc;
import de.smahoo.homeos.remote.devices.RemoteIpCamera;
import de.smahoo.homeos.remote.devices.RemoteLamp;
import de.smahoo.homeos.remote.devices.RemoteMeterElectricity;
import de.smahoo.homeos.remote.devices.RemoteRadiator;
import de.smahoo.homeos.remote.devices.RemoteRgbController;
import de.smahoo.homeos.remote.devices.RemoteSensorBinary;
import de.smahoo.homeos.remote.devices.RemoteSensorClimate;
import de.smahoo.homeos.remote.devices.RemoteSensorHumidity;
import de.smahoo.homeos.remote.devices.RemoteSensorMotion;
import de.smahoo.homeos.remote.devices.RemoteSensorTemperature;
import de.smahoo.homeos.remote.devices.RemoteSensorWindow;
import de.smahoo.homeos.remote.devices.RemoteSocket;
import de.smahoo.homeos.remote.devices.RemoteTV;

public class RemoteDeviceFactory {
	RemoteDeviceManager devManager = null;
	RemoteLocationManager locManager = null;
	private RemoteConnection conn = null;
	
		
	public RemoteDeviceFactory(RemoteConnection conn, RemoteDeviceManager deviceManager, RemoteLocationManager locationManager){
		devManager = deviceManager;
		locManager = locationManager;
		this.conn = conn;	
	}
		
	public void generateDevices(Element elem){
		if (!elem.getTagName().equalsIgnoreCase("device")) return;		
		NodeList nodelist = elem.getChildNodes();
		for (int i = 0; i< nodelist.getLength(); i++){
			if (nodelist.item(i) instanceof Element){
				devManager.addDevice(generateDevice((Element)nodelist.item(i)));
			}
		}		
	}	
	
	public RemoteDevice generateDevice(Element elem){
		DeviceType deviceType = getDeviceType(elem.getTagName());
		String deviceId = elem.getAttribute("id");
		if (deviceType == null) return null;
		RemoteDevice remoteDevice = null;
		String name = elem.getAttribute("name");
		String locId = elem.getAttribute("location");		
		switch (deviceType){
			case LAMP: remoteDevice = generateLamp(elem.getChildNodes()); break;
			case SOCKET: remoteDevice = generateSocket(elem.getChildNodes()); break;
			case DIMMER: remoteDevice = generateDimmer(elem.getChildNodes()); break;
			case HEATING_RTC: remoteDevice = generateHeatingRtc(elem.getChildNodes()); break;
			case HEATING_RADIATOR: remoteDevice = generateHeatingRadiator(elem.getChildNodes()); break;
			case SENSOR_TEMPERATURE: remoteDevice = generateSensorTemperature(elem.getChildNodes()); break;
			case SENSOR_CLIMATE: remoteDevice = generateSensorClimate(elem.getChildNodes()); break;
			case SENSOR_HUMIDITY: remoteDevice = generateSensorHumidity(elem.getChildNodes()); break;
			case SENSOR_WINDOW: remoteDevice = generateSensorWindow(elem.getChildNodes()); break;
			case TELEVISION: remoteDevice = generateTelevision(elem.getChildNodes()); break;
			case TELEVISION_EXTENDED: remoteDevice= generateExtendedTelevision(elem.getChildNodes()); break;	
			case SENSOR_MOTION : remoteDevice = generateSensorMotion(elem.getChildNodes()); break;
			case SENSOR_BINARY : remoteDevice = generateSensorBinary(elem.getChildNodes()); break;
			//case METER_ENERGY: remoteDevice = generateMeterEnergy(elem.getChildNodes()); break;
			case IP_CAMERA: 
				remoteDevice = generateIpCamera(elem.getChildNodes()); break;
			case METER_ELECTRICITY: remoteDevice = generateMeterElectricity(elem.getChildNodes()); break;
			//case SWITCH :		
			case METER_ENERGY:
				break;
			case MISC:
				break;
			case RGB_CONTROLLER:
				remoteDevice = generateRgbController(elem.getChildNodes());
				break;
			case SWITCH:
				break;
			case SWITCHABLE:
				break;
			case VENTILATION:
				break;
			default:
				break;
		}
		
		if (remoteDevice == null) return null;
		remoteDevice.id = deviceId;
		remoteDevice.historyProcessor = new RemoteHistoryProcessor(this.conn);
		RemoteLocation location = locManager.getLocation(locId);
		if (elem.hasAttribute("ison")){
			remoteDevice.isOnState = Boolean.parseBoolean(elem.getAttribute("ison"));
		}
		if (elem.hasAttribute("isAvailable")){
			remoteDevice.isAvailableState = Boolean.parseBoolean(elem.getAttribute("isAvailable"));
		}
		if (elem.hasAttribute("lastActivity")){
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				remoteDevice.lastActivity = formatter.parse(elem.getAttribute("lastActivity"));
			} catch (Exception exc){
				exc.printStackTrace();
			}
		}
		if (location != null){			
			remoteDevice.location = location;			
			location.assignDevice(remoteDevice);
		}
		remoteDevice.name = name;	
		if (remoteDevice.isOn()){
			remoteDevice.lastUpdate = new Date();
		}
		remoteDevice.setConnection(conn);
		return remoteDevice;
	}
	
	protected RemoteExtendedTV generateExtendedTelevision(NodeList properties){
		RemoteExtendedTV tv = new RemoteExtendedTV();
		((RemoteDevice)tv).updateProperties(properties);
		
		return tv;
	}
	
	protected RemoteTV generateTelevision(NodeList properties){
		RemoteTV tv = new RemoteTV();
		((RemoteDevice)tv).updateProperties(properties);
		return tv;
	}
	
	protected RemoteLamp generateLamp(NodeList properties){
		return new RemoteLamp();
	}
	
	protected RemoteSocket generateSocket(NodeList properties){
		return new RemoteSocket();
	}
	
	protected RemoteDimmer generateDimmer(NodeList properties){
		return new RemoteDimmer();
	}
	
	private RemoteRgbController generateRgbController(NodeList properties){
		RemoteRgbController rgbCntrl = new RemoteRgbController();
		((RemoteDevice)rgbCntrl).updateProperties(properties);
		return rgbCntrl;
	}
	
	private RemoteMeterElectricity generateMeterElectricity(NodeList properties){
		RemoteMeterElectricity meter = new RemoteMeterElectricity();
		((RemoteDevice)meter).updateProperties(properties);
		return meter;
	}
	
	private RemoteIpCamera generateIpCamera(NodeList properties){
		RemoteIpCamera camera = new RemoteIpCamera();
		((RemoteDevice)camera).updateProperties(properties);
		return camera;
	}
	
	private RemoteSensorBinary generateSensorBinary(NodeList properties){
		RemoteSensorBinary sensor = new RemoteSensorBinary();
		((RemoteDevice)sensor).updateProperties(properties);
		return sensor;
	}
	
	private RemoteSensorMotion generateSensorMotion(NodeList properties){
		RemoteSensorMotion sensor = new RemoteSensorMotion();
		((RemoteDevice)sensor).updateProperties(properties);
		return sensor;
	}
	
	
	private RemoteSensorWindow generateSensorWindow(NodeList properties){
		RemoteSensorWindow sensor = new RemoteSensorWindow();
		((RemoteDevice)sensor).updateProperties(properties);
		return sensor;
	}
	
	private RemoteSensorTemperature generateSensorTemperature(NodeList properties){
		RemoteSensorTemperature sensor = new RemoteSensorTemperature();
		((RemoteDevice)sensor).updateProperties(properties);
		return sensor;
	}
	
	protected RemoteRadiator generateHeatingRadiator(NodeList properties){
		RemoteRadiator radiator = new RemoteRadiator();
		((RemoteDevice)radiator).updateProperties(properties);
		return radiator;
	}
	
	protected RemoteHeatingRtc generateHeatingRtc(NodeList properties){
		RemoteHeatingRtc heating = new RemoteHeatingRtc();
		((RemoteDevice)heating).updateProperties(properties);
		return heating;
	}
	
	protected RemoteSensorClimate generateSensorClimate(NodeList properties){
		RemoteSensorClimate sensor = new RemoteSensorClimate();
		((RemoteDevice)sensor).updateProperties(properties);
		return sensor;
	}
	
	protected RemoteSensorHumidity generateSensorHumidity(NodeList properties){
		RemoteSensorHumidity sensor = new RemoteSensorHumidity();
		((RemoteDevice)sensor).updateProperties(properties);
		return sensor;
	}
	
	protected DeviceType getDeviceType(String tagname){
		if (tagname.equalsIgnoreCase("LAMP")) 				 return DeviceType.LAMP;
		if (tagname.equalsIgnoreCase("SOCKET")) 			 return DeviceType.SOCKET;
		if (tagname.equalsIgnoreCase("DIMMER")) 			 return DeviceType.DIMMER;
		if (tagname.equalsIgnoreCase("HEATINGRTC"))			 return DeviceType.HEATING_RTC;
		if (tagname.equalsIgnoreCase("RADIATOR"))			 return DeviceType.HEATING_RADIATOR;
		if (tagname.equalsIgnoreCase("SWITCH")) 			 return DeviceType.SWITCH;
		if (tagname.equalsIgnoreCase("SENSORWINDOW")) 		 return DeviceType.SENSOR_WINDOW;
		if (tagname.equalsIgnoreCase("SENSORCLIMATE")) 		 return DeviceType.SENSOR_CLIMATE;
		if (tagname.equalsIgnoreCase("SENSORHUMIDITY")) 	 return DeviceType.SENSOR_HUMIDITY;
		if (tagname.equalsIgnoreCase("SENSORTEMPERATURE")) 	 return DeviceType.SENSOR_TEMPERATURE;
		if (tagname.equalsIgnoreCase("TELEVISION")) 		 return DeviceType.TELEVISION;
		if (tagname.equalsIgnoreCase("TELEVISION_EXTENDED")) return DeviceType.TELEVISION_EXTENDED;
		if (tagname.equalsIgnoreCase("SENSORMOTION"))		 return DeviceType.SENSOR_MOTION;
		if (tagname.equalsIgnoreCase("IPCAMERA"))			 return DeviceType.IP_CAMERA;
		if (tagname.equalsIgnoreCase("METERELECTRICITY"))	 return DeviceType.METER_ELECTRICITY;
		if (tagname.equalsIgnoreCase("RGBCONTROLLER"))		 return DeviceType.RGB_CONTROLLER;
		if (tagname.equalsIgnoreCase("SENSORBINARY"))	     return DeviceType.SENSOR_BINARY;
		return null;
	}
	
	
	
}
