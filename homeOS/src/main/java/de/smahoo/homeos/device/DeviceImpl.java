package de.smahoo.homeos.device;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


import de.smahoo.homeos.common.EventType;
import de.smahoo.homeos.db.DataBaseManager;
import de.smahoo.homeos.device.Device;
import de.smahoo.homeos.device.DeviceEvent;
import de.smahoo.homeos.device.DeviceEventListener;
import de.smahoo.homeos.device.PropertyHistoryData;
import de.smahoo.homeos.kernel.HomeOs;
import de.smahoo.homeos.location.Location;

public abstract class DeviceImpl implements Device{
	private String deviceName;
	private Location assignedLocation;
	private String deviceId;
	private boolean hidden;
	private List<DeviceEventListener> eventListeners;
	protected Date lastActivity = null;
		 
	public DeviceImpl(String deviceId){
		this.deviceId = deviceId;
		eventListeners = new ArrayList<DeviceEventListener>();
	}
	
	public Date getLastActivityTimeStamp(){
		return lastActivity;
	}
	
	protected void setLastActivity(){
		lastActivity = new Date();
	}
	
	public String getDeviceId(){
		return deviceId;
	}
	
	
	
	public boolean isHidden(){
		return hidden;
	}
	
	public void setHidden(boolean hidden){
		if (this.hidden == hidden) return;
		this.hidden = hidden;
		if (hidden){
			dispatchDeviceEvent(new DeviceEvent(EventType.DEVICE_HIDDEN, this));
		} else {
			dispatchDeviceEvent(new DeviceEvent(EventType.DEVICE_VISIBLE, this));
		}
	}
	
	public String getName(){
		String name;
		
		if (deviceName != null){
			name = deviceName;
		} else name = deviceId;
		 
		return name;
	}
	
	public void setName(final String name){		
		if (name == null) return;
		if (deviceName != null){
			if (name.toLowerCase().equals(deviceName.toLowerCase())) return;			
		}
		this.deviceName = name;
		dispatchDeviceEvent(new DeviceEvent(EventType.DEVICE_RENAMED, this));
		
	}
	
	public void removeLocation(){				
		if (assignedLocation == null) return;
		assignedLocation.removeDevice(this);
		assignedLocation = null;		
	}
	
	public void assignLocation(final String locationId){
		Location loc = HomeOs.getInstance().getLocationManager().getLocation(locationId);
		if (loc != null){
			assignLocation(loc);
		}
	}
	
	public void assignLocation(Location location){
		if (assignedLocation == location) return;
		if (assignedLocation != null) assignedLocation.removeDevice(this);
		//removeLocation();		
		this.assignedLocation = location;
		if (location != null){
			location.assignDevice(this);
			//dispatchDeviceEvent(new DeviceEvent(EventType.LOCATION_ASSIGNED,this));
		} else {
			//dispatchDeviceEvent(new DeviceEvent(EventType.LOCATION_REMOVED, this));
		}
		
	}
	
	public Location getLocation(){
		return this.assignedLocation;
	}
	
	public void addDeviceEventListener(DeviceEventListener listener){
		if (eventListeners.contains(listener)) return;
		eventListeners.add(listener);
	}
	
	public void removeDeviceEventListener(DeviceEventListener listener){
		if (eventListeners.isEmpty()) return;
		eventListeners.remove(listener);
	}
	
	public void dispatchDeviceEvent(DeviceEvent event){
		switch (event.getEventType()){
			case DEVICE_ADDED:
			case DEVICE_AVAILABLE:
			case PROPERTY_VALUE_CHANGED:
			case DEVICE_PROPERTY_CHANGED:
			case FUNCTION_EXECUTED: this.lastActivity = new Date();	break;
		}
		
		for (DeviceEventListener listener : eventListeners){	
			listener.onDeviceEvent(event);			
		}
	}
	
	public List<PropertyHistoryData> getHistoryData(Date start, Date end){
		return HomeOs.getInstance().getDataBaseManager().getHistoryData(this, start, end);		
	}
	
	//public List<PropertyHistoryData> getHistoryData(String propertyName, Date start, Date end){
	//	return HomeOs.getInstance().getDataBaseManager().getHistoryData(this, propertyName, start, end);		
	//}
	
}
