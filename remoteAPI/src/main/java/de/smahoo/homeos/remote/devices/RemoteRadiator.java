package de.smahoo.homeos.remote.devices;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.smahoo.homeos.common.EventType;
import de.smahoo.homeos.devices.HeatingRadiator;
import de.smahoo.homeos.remote.RemoteDevice;
import de.smahoo.homeos.remote.RemoteDeviceEvent;

public class RemoteRadiator extends RemoteDevice implements HeatingRadiator {

	private double valvePosition = -1.0;
	
	
	protected void updateValvePosition(double position){
		if (valvePosition == position) return;
		valvePosition = position;
		dispatchDeviceEvent(new RemoteDeviceEvent(EventType.DEVICE_PROPERTY_CHANGED,this,true));
	}
	
	
	@Override
	protected void updateProperties(NodeList properties){
		if (properties.getLength() > 0){
			Element tmp;
			for (int i=0; i<properties.getLength(); i++){
				if (properties.item(i) instanceof Element){
					tmp = (Element)properties.item(i);
					if (tmp.getTagName().equalsIgnoreCase("property")){
						if ((tmp.hasAttribute("name"))&&(tmp.getAttribute("name").equalsIgnoreCase("valvepos"))){
							updateValvePosition(Double.parseDouble(tmp.getAttribute("value")));
						}
					}
				}
			}
		}
	}
	
	
	public double getValvePosition(){
		return valvePosition;
	}
	
}
