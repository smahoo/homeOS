package de.smahoo.homeos.remote;

import java.util.ArrayList;
import java.util.List;


import org.w3c.dom.NodeList;

import de.smahoo.homeos.device.DeviceType;

public class RemoteMultiDevice extends RemoteDevice{

	protected List<RemoteDevice> remoteDevices = null;
		
	public RemoteMultiDevice(){
		super();
		remoteDevices = new ArrayList<RemoteDevice>();
	}
	
	protected void updateProperties(NodeList properties){
		//
	}
	
	public synchronized List<RemoteDevice> getDevices(){
		List<RemoteDevice> result = new ArrayList<RemoteDevice>();
		for (RemoteDevice dev : remoteDevices){
			result.add(dev);
		}
		return result;
	}
	
	public synchronized boolean hasDevices(){
		return !remoteDevices.isEmpty();
	}
	
	public synchronized boolean hasDevices(DeviceType type){
		for (RemoteDevice dev : remoteDevices){
			if (DeviceType.getDeviceTypes(dev).contains(type)){
				return true;
			}
		}
		return false;
	}
	
	public synchronized List<RemoteDevice> getDevices(DeviceType type){
		List<RemoteDevice> result = new ArrayList<RemoteDevice>();
		for (RemoteDevice dev : remoteDevices){
			if (DeviceType.getDeviceTypes(dev).contains(type)){
				result.add(dev);
			}
		}
		return result;
	}
	
	/*
		
	@Override
	public void 	setName(String name){
		
	}
	
	@Override
	public Location getLocation(){
		return null;
	}
	
	@Override
	public void 	assignLocation(Location location){
		
	}
	
	@Override
	public boolean 	isHidden(){
		return false;
	}
	
	@Override
	public boolean  isOn(){
		return false;
	}
	
	@Override
	public List<PropertyHistoryData> getHistoryData(Date start, Date end){
		return null;
	}*/
	
}
