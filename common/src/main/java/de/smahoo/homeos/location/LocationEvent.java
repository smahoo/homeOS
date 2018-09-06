package de.smahoo.homeos.location;


import de.smahoo.homeos.common.Event;
import de.smahoo.homeos.common.EventType;
import de.smahoo.homeos.device.Device;

public class LocationEvent extends Event{

	protected Location location = null;
	protected Device device =null;
	
	public LocationEvent(EventType eventType, Location location){
		super(eventType);		
		this.location = location;
		//this.description = toString();
	}
	
	
	
	public LocationEvent(EventType eventType, Location location, Device device){
		this(eventType,location);		
		this.device = device;
		//this.description = toString();
	}
	
	
	public boolean hasDevice(){
		return (device != null);
	}
	
	public Device getDevice(){
		return device;
	}
	
	
	@Override
	public String toString(){
		String res = eventType.name()+" \""+location.getName()+"\" ("+location.getId()+") ";
		switch (eventType) {
			
			case LOCATION_ASSIGNED : 
				if (hasDevice()){
					res = res + " Device \""+getDevice().getName()+"\" ("+device.getDeviceId()+")";
				}
		}
		return res;
	}
	
	public Location getLocation(){
		return location;
	}
	
	
}
