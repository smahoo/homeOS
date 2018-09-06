package de.smahoo.homeos.remote.devices;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.smahoo.homeos.common.EventType;
import de.smahoo.homeos.device.DeviceEvent;
import de.smahoo.homeos.devices.SensorBinary;
import de.smahoo.homeos.remote.RemoteDevice;

public class RemoteSensorBinary extends RemoteDevice implements SensorBinary{

	private boolean value = false;
	
	@Override
	public boolean isBinarySet() {	
		return value;
	}
	
	
	private void setValue(boolean value){
		if (this.value == value) return;		
		this.value = value;
		dispatchDeviceEvent(new DeviceEvent(EventType.DEVICE_PROPERTY_CHANGED,this));
	}

	@Override
	protected void updateProperties(NodeList properties) {
		if (properties.getLength() > 0){
			Element tmp;
			for (int i=0; i<properties.getLength(); i++){
				if (properties.item(i) instanceof Element){
					tmp = (Element)properties.item(i);
					if (tmp.getTagName().equalsIgnoreCase("property")){
						if ((tmp.hasAttribute("name"))&&(tmp.getAttribute("name").equalsIgnoreCase("value"))){
							setValue(Boolean.parseBoolean(tmp.getAttribute("value")));
						}
					}
				}
			}
		}
		
	}

}
