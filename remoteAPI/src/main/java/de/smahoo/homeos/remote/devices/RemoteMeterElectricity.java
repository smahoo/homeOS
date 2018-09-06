package de.smahoo.homeos.remote.devices;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.smahoo.homeos.common.EventType;
import de.smahoo.homeos.devices.MeterElectricity;
import de.smahoo.homeos.remote.RemoteDevice;
import de.smahoo.homeos.remote.RemoteDeviceEvent;

public class RemoteMeterElectricity extends RemoteDevice implements MeterElectricity{

	private double current = 0.0;
	private double total = 0.0;
	
	
	protected void updateTotal(double total){
		if (this.total == total) return;
		this.total = total;
		dispatchDeviceEvent(new RemoteDeviceEvent(EventType.DEVICE_PROPERTY_CHANGED,this,true));
	}
	
	protected void updateCurrent(double current){
		if (this.current == current) return;
		this.current = current;
		dispatchDeviceEvent(new RemoteDeviceEvent(EventType.DEVICE_PROPERTY_CHANGED,this,true));
	}
	
	protected void updateProperties(NodeList properties){
		if (properties.getLength() > 0){
			Element tmp;
			for (int i=0; i<properties.getLength(); i++){
				if (properties.item(i) instanceof Element){
					tmp = (Element)properties.item(i);
					
					if (tmp.getTagName().equalsIgnoreCase("property")){
						if ((tmp.hasAttribute("name"))&&(tmp.getAttribute("name").equalsIgnoreCase("current"))){
							updateCurrent(Double.parseDouble(tmp.getAttribute("value")));
						}
						if ((tmp.hasAttribute("name"))&&(tmp.getAttribute("name").equalsIgnoreCase("total"))){
							updateTotal(Double.parseDouble(tmp.getAttribute("value")));
						}
					}				
				}
			}
		}
	}
	
	
	public double getTotalConsumption(){
		return total;
	}
	
	
	public double getCurrentConsumption(){
		return current;
	}
	
}
