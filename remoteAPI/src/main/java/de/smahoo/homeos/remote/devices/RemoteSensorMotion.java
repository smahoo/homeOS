package de.smahoo.homeos.remote.devices;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.smahoo.homeos.common.EventType;
import de.smahoo.homeos.device.DeviceEvent;
import de.smahoo.homeos.devices.SensorMotion;
import de.smahoo.homeos.remote.RemoteDevice;

public class RemoteSensorMotion extends RemoteDevice implements SensorMotion{
	
	protected boolean motion = false;
	
	public boolean isMotion(){
		return motion;
	}
	
	private void setMotion(boolean motion){
		if (this.motion == motion) return;		
		this.motion = motion;
		dispatchDeviceEvent(new DeviceEvent(EventType.DEVICE_PROPERTY_CHANGED,this));
	}
	
	@Override
	protected void updateProperties(NodeList properties){
		if (properties.getLength() > 0){
			Element tmp;
			for (int i=0; i<properties.getLength(); i++){
				if (properties.item(i) instanceof Element){
					tmp = (Element)properties.item(i);
					if (tmp.getTagName().equalsIgnoreCase("property")){
						if ((tmp.hasAttribute("name"))&&(tmp.getAttribute("name").equalsIgnoreCase("isMotion"))){
							setMotion(Boolean.parseBoolean(tmp.getAttribute("value")));
						}
					}
				}
			}
		}
	}

}
