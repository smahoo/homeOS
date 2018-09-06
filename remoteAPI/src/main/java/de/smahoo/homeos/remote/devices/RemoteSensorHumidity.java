package de.smahoo.homeos.remote.devices;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.smahoo.homeos.common.EventType;
import de.smahoo.homeos.device.DeviceEvent;
import de.smahoo.homeos.devices.SensorHumidity;
import de.smahoo.homeos.remote.RemoteDevice;
import de.smahoo.homeos.remote.RemoteDeviceEvent;

public class RemoteSensorHumidity extends RemoteDevice implements SensorHumidity{
	private double humidity;
	
	public double getHumidity(){
		return humidity;
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
						if ((tmp.hasAttribute("name"))&&(tmp.getAttribute("name").equalsIgnoreCase("humidity"))){
							updateHumidity(Double.parseDouble(tmp.getAttribute("value")));
						}
					}
				}
			}
		}
	}
	
}
