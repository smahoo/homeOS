package de.smahoo.homeos.remote;


import de.smahoo.homeos.common.EventType;
import de.smahoo.homeos.device.Device;
import de.smahoo.homeos.device.DeviceEvent;

public class RemoteDeviceEvent extends DeviceEvent{

	private boolean update;
	
	public RemoteDeviceEvent(EventType eventType, Device device, boolean update){
		super(eventType, device);
		this.update = update;
	}
	
	
	public boolean isUpdating(){
		return update;
	}
}
