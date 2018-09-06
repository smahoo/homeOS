package de.smahoo.homeos.remote;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.smahoo.homeos.common.EventType;
import de.smahoo.homeos.device.Device;
import de.smahoo.homeos.device.DeviceEvent;
import de.smahoo.homeos.device.DeviceEventListener;
import de.smahoo.homeos.device.PropertyHistoryData;
import de.smahoo.homeos.location.Location;
import de.smahoo.homeos.remote.connection.RemoteConnection;
import de.smahoo.homeos.utils.AttributeValuePair;
import de.smahoo.homeos.utils.xml.XmlUtils;

public abstract class RemoteDevice implements Device {
	protected boolean isOnState;
	protected boolean isAvailableState;
	protected String name;
	protected String id;
	protected Location location;
	protected List<DeviceEventListener> eventListener;
	protected Date lastUpdate = null;
	protected Date connectionDate = null;
	protected RemoteConnection connection;	
	protected RemoteHistoryProcessor historyProcessor = null;
	protected Date lastActivity = null;
	
	public RemoteDevice(){		
		eventListener = new ArrayList<DeviceEventListener>();		
	}
	
	public Date getLastActivityTimeStamp(){
		return lastActivity;
	}
	
	public String getName(){
		return name;
	}	
	
	protected void setConnection(RemoteConnection connection){
		this.connection = connection;
	}
	
	protected void updateName(String name){
		if (this.name.equals(name)) return;
		this.name = name;
		dispatchDeviceEvent(new RemoteDeviceEvent(EventType.DEVICE_RENAMED,this,true));
	}
	
	protected void updateIsOnState(boolean ison){
		if (this.isOnState == ison) return;
		this.isOnState = ison;
		if (ison){
			dispatchDeviceEvent(new RemoteDeviceEvent(EventType.DEVICE_ON,this,true));
		} else {
			dispatchDeviceEvent(new RemoteDeviceEvent(EventType.DEVICE_OFF,this,true));
		}
	}
	
	protected void updateIsAvailableState(boolean isAvailable){
		if (this.isAvailableState == isAvailable) return;
		this.isAvailableState = isAvailable;
		if (isAvailable){
			dispatchDeviceEvent(new RemoteDeviceEvent(EventType.DEVICE_AVAILABLE,this,true));
		} else {
			dispatchDeviceEvent(new RemoteDeviceEvent(EventType.DEVICE_NOT_AVAILABLE,this,true));
		}
	}
	
	protected void updateLocation(Location location){
		if (this.location == location) return;		
		if (this.location != null){
			((RemoteLocation)this.location).deviceList.remove(this);
			this.location = null;
		}		
		this.location = location;
		if (location == null){
			dispatchDeviceEvent(new RemoteDeviceEvent(EventType.LOCATION_REMOVED,this,true));
		}
		((RemoteLocation)this.location).deviceList.add(this);
		dispatchDeviceEvent(new RemoteDeviceEvent(EventType.LOCATION_ASSIGNED,this,true));
	}
	
	public void setName(String name){
		if (this.name.equals(name)) return;
		this.name = name;
		dispatchDeviceEvent(new DeviceEvent(EventType.DEVICE_RENAMED,this));
	}
	
	public String 	getDeviceId(){
		return id;
	}
	
	public Location getLocation(){
		return location;
	}
	
	public void	assignLocation(Location location){
		if (location == this.location) return;
		this.location = location;
		if (location != null){
			location.assignDevice(this);
			dispatchDeviceEvent(new DeviceEvent(EventType.LOCATION_ASSIGNED, this));
		}		
	}
	
	public boolean 	isHidden(){
		return false;
	}
	
	protected void dispatchDeviceEvent(DeviceEvent event){
		for (DeviceEventListener listener : eventListener){
			listener.onDeviceEvent(event);
		}
	}
	
	public void removeDeviceEventListener(DeviceEventListener listener){
		if (eventListener.isEmpty()) return;
		eventListener.remove(listener);
	}
	
	public void addDeviceEventListener(DeviceEventListener listener){
		if (eventListener.contains(listener)) return;
		eventListener.add(listener);
	}
	
	@Override
	public String toString(){
		return getName();
	}
	
	protected void executeFunction(String name){
		executeFunction(name,null);
	}
	
	protected void executeFunction(String name, List<AttributeValuePair> values){
		dispatchDeviceEvent(new RemoteFunctionExecutionEvent(EventType.FUNCTION_EXECUTED, this, name, values));		
	}
	
	@Override
	public boolean isOn(){
		return isOnState;
	}
	
	@Override
	public boolean isAvailable(){
		return isAvailableState;
	}
	
	protected void updateHistoryCache(NodeList properties){
		//FIXME : Available-State is not propagated to RemoteDevice
		
		PropertyHistoryData phd = new PropertyHistoryData(new Date(), this, isOn(), true);
		AttributeValuePair avp = null;
		if (properties.getLength() > 0){
			Element tmp;
			for (int i=0; i<properties.getLength(); i++){
				if (properties.item(i) instanceof Element){
					tmp = (Element)properties.item(i);					
					if (tmp.getTagName().equalsIgnoreCase("property")){
						avp = new AttributeValuePair(tmp.getAttribute("name"));
						avp.setValue(tmp.getAttribute("value"));
						phd.addAttributeValuePair(avp);
					}
				}
			}
		}
		historyProcessor.updateCache(phd);
		
		
	}
	
	protected void update(NodeList properties){
		lastUpdate = new Date();
		updateHistoryCache(properties);
		updateProperties(properties);
	}
	
	protected void setConnectionDate(Date date){
		if (date == null){
			// add gap to historyCache
		}
		
		this.connectionDate = date;
	}
		
	protected Date getConnectionDate(){
		return connectionDate;
	}
	
	public List<PropertyHistoryData> getHistoryData(Date start, Date end){
		return this.historyProcessor.getHistoryData(this, start, end);		
	}	
	
	public Date getLastUpdateTimeStamp(){
		return this.lastUpdate;
	}
	
	abstract protected void updateProperties(NodeList properties);
	//abstract protected void updateHistoryCahce();
}
