package de.smahoo.homeos.kernel.remote.result;

import java.text.SimpleDateFormat;
import java.util.List;

import de.smahoo.homeos.devices.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.smahoo.homeos.device.Device;
import de.smahoo.homeos.device.DeviceType;

public class DeviceRequestResultItem extends RemoteResultItem {

	protected Device device = null;
	protected DeviceType deviceType = null;
	
	public DeviceRequestResultItem(Device device){
		this(device,null);
	}
	
	public Device getDevice(){
		return device;
	}
	
	public DeviceRequestResultItem(Device device, DeviceType deviceType){
		this.device = device;
		this.deviceType = deviceType;
	}
	
	protected Element generateElement(Document doc, DeviceType deviceType){
		Element elem = null;
		Element child;
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		switch (deviceType){
		case LAMP: 
			elem = doc.createElement("lamp");
			break;
		case TELEVISION_EXTENDED: 
			elem = doc.createElement("television_extended");
			break;
		case TELEVISION: 
			elem = doc.createElement("television");		
			break;
		case VENTILATION :
			elem = doc.createElement("ventilator");
			break;
		case HEATING_RTC:
			elem = doc.createElement("heatingrtc");
			HeatingRtc heating = (HeatingRtc)device;
			child = doc.createElement("property");
			child.setAttribute("name", "desiredTemperature");
			child.setAttribute("value",""+heating.getDesiredTemperature());
			elem.appendChild(child);
			child = doc.createElement("property");
			child.setAttribute("name","valvepos");
			child.setAttribute("value",""+heating.getValvePosition());
			elem.appendChild(child);
			break;
		case HEATING_RADIATOR:
			elem = doc.createElement("radiator");
			HeatingRadiator radiator = (HeatingRadiator)device;;
			child = doc.createElement("property");
			child.setAttribute("name","valvepos");
			child.setAttribute("value",""+radiator.getValvePosition());
			elem.appendChild(child);
			break;
		case SENSOR_BINARY:
			elem = doc.createElement("sensorbinary");
			SensorBinary sensorBinary = (SensorBinary)device;
			child = doc.createElement("property");
			child.setAttribute("name","value");
			child.setAttribute("value",""+sensorBinary.isBinarySet());
			elem.appendChild(child);
			break;
		case SENSOR_CO2:
			elem = doc.createElement("sensorco2");
			SensorCO2 sensorCo2 = (SensorCO2)device;
			child = doc.createElement("property");
			child.setAttribute("name","co2");
			child.setAttribute("value", ""+ sensorCo2.getC02Value());
			elem.appendChild(child);

			break;
		case SENSOR_HUMIDITY: 
			elem = doc.createElement("sensorhumidity");
			SensorHumidity sensor = (SensorHumidity)device;
			child = doc.createElement("property");
			child.setAttribute("name","humdidity");
			child.setAttribute("value",""+sensor.getHumidity());
			elem.appendChild(child);
			break;
		case SENSOR_TEMPERATURE: 
			elem = doc.createElement("sensortemperature");
			SensorTemperature sensorTemp = (SensorTemperature)device;
			child = doc.createElement("property");
			child.setAttribute("name","temperature");
			child.setAttribute("value",""+sensorTemp.getTemperature());
			elem.appendChild(child);
			break;
		case SENSOR_CLIMATE: 
			elem = doc.createElement("sensorclimate");
			SensorClimate sensorClimate = (SensorClimate)device;
			child = doc.createElement("property");
			child.setAttribute("name","humidity");
			child.setAttribute("value",""+sensorClimate.getHumidity());
			elem.appendChild(child);
			child = doc.createElement("property");
			child.setAttribute("name","temperature");
			child.setAttribute("value",""+sensorClimate.getTemperature());
			elem.appendChild(child);
			break;
		case SENSOR_WINDOW: 
			elem = doc.createElement("sensorwindow");
			SensorWindow sensorWindow = (SensorWindow)device;
			child = doc.createElement("property");
			child.setAttribute("name","isOpen");
			child.setAttribute("value",""+sensorWindow.isOpen());
			elem.appendChild(child);
			break;
		case SENSOR_MOTION: 
			elem = doc.createElement("sensormotion");
			SensorMotion sensorMotion = (SensorMotion)device;
			child = doc.createElement("property");
			child.setAttribute("name","isMotion");
			child.setAttribute("value",""+sensorMotion.isMotion());
			elem.appendChild(child);
			break;
		case IP_CAMERA:
			elem = doc.createElement("ipcamera");
			IpCamera cam = (IpCamera)device;
			child = doc.createElement("property");
			child.setAttribute("name","isPasswordProtected");
			child.setAttribute("value",""+cam.isPasswordProtected());
			elem.appendChild(child);
			child = doc.createElement("property");
			child.setAttribute("name","url");
			child.setAttribute("value",""+cam.getUrl());
			elem.appendChild(child);			
			break;		
		case METER_ENERGY:
		case METER_ELECTRICITY:
			if (deviceType == DeviceType.METER_ELECTRICITY) {
				elem = doc.createElement("meterElectricity");
			} else {
				elem = doc.createElement("meterEnergy");
			}
			MeterEnergy me = (MeterEnergy)device;
			child = doc.createElement("property");
			child.setAttribute("name","current");
			child.setAttribute("value",""+me.getCurrentConsumption());
			child.setAttribute("unit","kW");
			elem.appendChild(child);
			child = doc.createElement("property");
			child.setAttribute("name","total");
			child.setAttribute("value",""+me.getTotalConsumption());
			child.setAttribute("unit","kWh");
			elem.appendChild(child);
			break;
		case SOCKET:
			elem = doc.createElement("socket");
			break;
		case SWITCHABLE:
			elem = doc.createElement("socket");
			break;
		case SWITCH :
			elem = doc.createElement("switch");
			break;
		case DIMMER : 
			elem = doc.createElement("dimmer");
			break;
		case MISC:
			break;
		case RGB_CONTROLLER:
			
				elem = doc.createElement("rgbcontroller");
			RGBController rgbCntrl = (RGBController)device;
			
			child = doc.createElement("property");
			child.setAttribute("name","red");
			child.setAttribute("value",""+rgbCntrl.getRed());
			child.setAttribute("unit","");
			elem.appendChild(child);
			child = doc.createElement("property");
			child.setAttribute("name","green");
			child.setAttribute("value",""+rgbCntrl.getGreen());
			child.setAttribute("unit","");
			elem.appendChild(child);
			child = doc.createElement("property");
			child.setAttribute("name","blue");
			child.setAttribute("value",""+rgbCntrl.getBlue());
			child.setAttribute("unit","");
			elem.appendChild(child);
			
			break;
		default:
			break;
		
		}
		
		if (elem != null){
			elem.setAttribute("id",deviceType.name()+"@"+device.getDeviceId());
			elem.setAttribute("name",device.getName());
			elem.setAttribute("ison",""+device.isOn());
			elem.setAttribute("isAvailable",""+device.isAvailable());
			
			if (device.getLastActivityTimeStamp() != null){
				elem.setAttribute("lastActivity", formatter.format(device.getLastActivityTimeStamp()));
			}
			if (device.getLocation() != null){
				elem.setAttribute("location", device.getLocation().getId());
			}
		}
		
		return elem;
	}
	
	public Element generateElement(Document doc){
		Element elem = null;
		if (deviceType != null){
			elem = generateElement(doc,deviceType);
		} else {
			elem = doc.createElement("device");
			elem.setAttribute("deviceId",device.getDeviceId());
			List<DeviceType> list = DeviceType.getDeviceTypes(device);
			for (DeviceType type : list){
				elem.appendChild(generateElement(doc,type));
			}
		}
		return elem;
	}
}
