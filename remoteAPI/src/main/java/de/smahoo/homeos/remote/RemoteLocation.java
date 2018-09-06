package de.smahoo.homeos.remote;

import java.util.ArrayList;
import java.util.List;


import de.smahoo.homeos.device.Device;
import de.smahoo.homeos.location.Location;
import de.smahoo.homeos.location.LocationType;

public class RemoteLocation implements Location{

	protected String name;
	protected String id;
	protected List<RemoteDevice> deviceList;
	protected List<RemoteLocation> childLocations;
	protected RemoteLocation parent;
	protected LocationType locationType = LocationType.LT_NOT_GIVEN;
		
	public RemoteLocation(){
		childLocations = new ArrayList<RemoteLocation>();
		deviceList = new ArrayList<RemoteDevice>();
	}
		
	public LocationType getLocationType(){
		return locationType;
	}
	
	public List<Device> getAssignedDevices(){
		List<Device> list = new ArrayList<Device>();
		
		for (Device device : deviceList){
			list.add(device);
		}
		
		return list;
	}
	
	public boolean hasAssignedDevices(){
		return !deviceList.isEmpty();
	}
	
	public String getName(){
		return name;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getId(){
		return id;
	}
		
	public Location getParentLocation(){
		return parent;
	}
	
	public List<Location> getChildLocations(){
		List<Location> locList = new ArrayList<Location>();
		for (Location location : childLocations){
			locList.add(location);
		}
		return locList;
	}
	
	public boolean hasChildLocations(){
		return !childLocations.isEmpty();
	}
	
	public void assignDevice(Device device){
		if (deviceList.contains(device)) return;		
		deviceList.add((RemoteDevice)device);
		device.assignLocation(this);
	}
	
	public void removeDevice(Device device){
		if (!deviceList.contains(device))return;
		deviceList.remove(device);
		device.assignLocation(null);
	}
	
	@Override
	public void setLocationType(LocationType type){
		// FIXME: 
	}
	
	@Override
	public String toString(){
		return getName();
	}
}
