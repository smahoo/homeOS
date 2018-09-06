package de.smahoo.homeos.device;

import de.smahoo.homeos.common.Event;
import de.smahoo.homeos.common.EventType;

public class DeviceEvent extends Event{

	protected Device device = null;
	
	public DeviceEvent(EventType eventType, Device device){
		super(eventType);
		this.device = device;
	}
	
	public DeviceEvent(EventType eventType, String description , Device device){
		super(eventType,description);	
		this.device = device;
	}
	
	public Device getDevice(){
		return device;
	}
	
	public String toString(){
		if (device == null){
			return eventType.name();
		}
		return eventType.name()+" "+device.getName()+" ("+device.getDeviceId()+")";
	}
	
}
