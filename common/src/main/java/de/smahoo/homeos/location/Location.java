package de.smahoo.homeos.location;

import java.util.List;

import de.smahoo.homeos.device.Device;


public interface Location {
	public List<Device> getAssignedDevices();	
	public boolean hasAssignedDevices();
	public String getName();	
	public void setName(String name);
	public void setLocationType(LocationType type);
	public String getId();		
	public Location getParentLocation();	
	public List<Location> getChildLocations();	
	public boolean hasChildLocations();	
	public void assignDevice(Device device);	
	public void removeDevice(Device device);
	public LocationType getLocationType();
		
}
