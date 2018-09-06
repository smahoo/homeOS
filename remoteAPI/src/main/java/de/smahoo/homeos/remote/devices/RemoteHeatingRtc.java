package de.smahoo.homeos.remote.devices;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.smahoo.homeos.common.EventType;
import de.smahoo.homeos.device.DeviceEvent;
import de.smahoo.homeos.devices.HeatingRtc;
import de.smahoo.homeos.remote.RemoteDevice;
import de.smahoo.homeos.remote.RemoteDeviceEvent;
import de.smahoo.homeos.utils.AttributeValuePair;

public class RemoteHeatingRtc extends RemoteRadiator implements HeatingRtc{
	
	private double desiredTemperature = 0.0;
	
	
	
	public void setTemperature(double temperature){
		if (temperature == desiredTemperature) return;
		desiredTemperature = temperature;
		AttributeValuePair avp = new AttributeValuePair("temperature",""+temperature);
		List<AttributeValuePair> avpList = new ArrayList<AttributeValuePair>();
		avpList.add(avp);
		executeFunction("setTemperature",avpList);
	}
	
	private void updateDesiredTemperature(double temperature){
		if (desiredTemperature == temperature) return;
		desiredTemperature = temperature;
		dispatchDeviceEvent(new RemoteDeviceEvent(EventType.DEVICE_PROPERTY_CHANGED, this,true));
	}
	
	
	public double getDesiredTemperature(){
		return desiredTemperature;
	}

	@Override
	protected void updateProperties(NodeList properties){
		super.updateProperties(properties);
		if (properties.getLength() > 0){
			Element tmp;
			for (int i=0; i<properties.getLength(); i++){
				if (properties.item(i) instanceof Element){
					tmp = (Element)properties.item(i);
					if (tmp.getTagName().equalsIgnoreCase("property")){
						if ((tmp.hasAttribute("name"))&&(tmp.getAttribute("name").equalsIgnoreCase("desiredTemperature"))){
							updateDesiredTemperature(Double.parseDouble(tmp.getAttribute("value")));
						}
					}
				}
			}
		}
	}
	
}
