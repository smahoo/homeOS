package de.smahoo.homeos.simulation.devices;


import de.smahoo.homeos.common.EventType;
import de.smahoo.homeos.device.DeviceEvent;
import de.smahoo.homeos.device.PhysicalDevice;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public abstract class SimDevice extends PhysicalDevice{
	
	protected boolean isOnState = true;

	public SimDevice(String deviceId){
		super(deviceId);
	}
	
	public void setAvailable(boolean available){
		this.setAvailability(available);
	}

	public void turnOff(){
		if (!isOnState) return;
		isOnState = false;
		dispatchDeviceEvent(new DeviceEvent(EventType.DEVICE_OFF, this));
	}
	
	public void turnOn(){
		isOnState = true;
		dispatchDeviceEvent(new DeviceEvent(EventType.DEVICE_ON, this));
	}
	

	public boolean isOn(){
		return isOnState;
	}

	public Element toXml(Document doc){
		Element elem = doc.createElement("device");
		elem.setAttribute("class",this.getClass().getCanonicalName());
		elem.setAttribute("deviceId",this.getDeviceId());
		elem.setAttribute("name", this.getName());
		return elem;
	}
}
