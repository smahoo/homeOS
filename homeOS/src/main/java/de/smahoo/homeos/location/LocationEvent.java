package de.smahoo.homeos.location;


import de.smahoo.homeos.common.Event;
import de.smahoo.homeos.common.EventType;
import de.smahoo.homeos.device.Device;
import de.smahoo.homeos.device.PhysicalDevice;
import de.smahoo.homeos.location.Location;

public class LocationEvent extends Event{

	protected LocationFunction function = null;
	protected Location location = null;
	protected Device device =null;
	
	public LocationEvent(EventType eventType, Location location){
		super(eventType);		
		this.location = location;
		//this.description = toString();
	}
	
	public LocationEvent(EventType eventType, Location location, LocationFunction function){
		this(eventType,location);		
		this.function = function;	
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
	
	public boolean hasFunction(){
		return (function != null);
	}
	
	public LocationFunction getLocationFunction(){
		return function;
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
