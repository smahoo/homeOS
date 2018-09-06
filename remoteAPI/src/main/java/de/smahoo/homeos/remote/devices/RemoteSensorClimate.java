package de.smahoo.homeos.remote.devices;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.smahoo.homeos.common.EventType;
import de.smahoo.homeos.device.DeviceEvent;
import de.smahoo.homeos.devices.SensorClimate;
import de.smahoo.homeos.remote.RemoteDevice;
import de.smahoo.homeos.remote.RemoteDeviceEvent;

public class RemoteSensorClimate extends RemoteDevice implements SensorClimate{
	
	private double temperature;
	private double humidity;
	
	public double getTemperature(){
		return temperature;
	}
	public double getHumidity(){
		return humidity;
	}
	
	private void updateTemperature(double temperature){
		if (this.temperature == temperature) return;
		this.temperature = temperature;
		dispatchDeviceEvent(new RemoteDeviceEvent(EventType.DEVICE_PROPERTY_CHANGED,this,true));
	}
	
	private void updateHumidity(double humidity){
		if (this.humidity == humidity) return;
		this.humidity = humidity;
		dispatchDeviceEvent(new RemoteDeviceEvent(EventType.DEVICE_PROPERTY_CHANGED,this,true));
	}
	
	
	
	protected void updateProperties(NodeList properties){
		if (properties.getLength() > 0){
			Element tmp;
			for (int i=0; i<properties.getLength(); i++){
				if (properties.item(i) instanceof Element){
					tmp = (Element)properties.item(i);
					if (tmp.getTagName().equalsIgnoreCase("property")){
						if ((tmp.hasAttribute("name"))&&(tmp.getAttribute("name").equalsIgnoreCase("temperature"))){
							updateTemperature(Double.parseDouble(tmp.getAttribute("value")));
						}
					}
					if (tmp.getTagName().equalsIgnoreCase("property")){
						if ((tmp.hasAttribute("name"))&&(tmp.getAttribute("name").equalsIgnoreCase("humidity"))){
							updateHumidity(Double.parseDouble(tmp.getAttribute("value")));
						}
					}
				}
			}
		}
	}
}
