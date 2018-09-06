package de.smahoo.homeos.location;

import java.util.ArrayList;
import java.util.List;


import de.smahoo.homeos.common.EventType;
import de.smahoo.homeos.device.Device;
import de.smahoo.homeos.location.Location;
import de.smahoo.homeos.location.LocationEvent;
import de.smahoo.homeos.location.LocationEventListener;
import de.smahoo.homeos.location.LocationType;


public abstract class LocationImpl implements Location{

	LocationManager locManager;
	List<LocationEventListener> eventListeners;
	
	
	protected String name;
	protected String id;
	protected Location parentLocation;
	protected List<Location> childLocations;
	protected List<Device> assignedDevices;
	protected List<LocationFunction> locationFunctions;
	protected LocationType locationType = LocationType.LT_NOT_GIVEN;
	
	public LocationImpl(){
		childLocations = new ArrayList<Location>();
		assignedDevices = new ArrayList<Device>();
		eventListeners = new ArrayList<LocationEventListener>();
		locationFunctions = new ArrayList<LocationFunction>();
	}
	
	public LocationType getLocationType(){
		return locationType;
	}
	
	public void addEventListener(LocationEventListener listener){
		if (listener == null) return;
		if (eventListeners.contains(listener)) return;
		eventListeners.add(listener);
	}
	
	public void removeEventListener(LocationEventListener listener){
		if (listener == null) return;
		if (!eventListeners.contains(listener)) return;
		eventListeners.remove(listener);
	}
	
	protected void dispatchLocationEvent(LocationEvent evnt){
		if (evnt == null) return;
		if (eventListeners.isEmpty()) return;
		for (LocationEventListener lel : eventListeners){
			lel.onLocationEvent(evnt);
		}
	}
	
	public List<Device> getAssignedDevices(){
		return assignedDevices;
	}
	
	public boolean hasAssignedDevices(){
		return !assignedDevices.isEmpty();
	}
	
	@Override
	public String toString(){
		return getName();
	}
	
	public String getName(){
		return name;
	}
	
	public void setName(String name){
		if (this.name != null){
			if (this.name.equals(name)){
				return;				
			}
		}
		this.name = name;
		dispatchLocationEvent(new LocationEvent(EventType.LOCATION_RENAMED, this));
		
	}
	
	public String getId(){
		return id;
	}
		
	public Location getParentLocation(){
		return parentLocation;
	}
	
	public List<Location> getChildLocations(){
		return this.childLocations;
	}
	
	public boolean hasChildLocations(){
		return !childLocations.isEmpty();
	}
	
	public void assignDevice(Device device){
		if (assignedDevices.contains(device)) return;
		assignedDevices.add(device);
		device.assignLocation(this);
		dispatchLocationEvent(new LocationEvent(EventType.LOCATION_ASSIGNED, this, device));
	}
	
	public void removeDevice(Device device){
		if (!assignedDevices.contains(device));
		assignedDevices.remove(device);
		
	}
	
	
	public void setLocationType(LocationType type){
		if (this.getLocationType() == type){
			return;
		}
		this.locationType = type;
		this.dispatchLocationEvent(new LocationEvent(EventType.LOCATION_CHANGED,this));
	}
			
	
}
