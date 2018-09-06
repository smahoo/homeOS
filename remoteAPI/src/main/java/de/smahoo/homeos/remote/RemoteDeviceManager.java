package de.smahoo.homeos.remote;


import java.util.ArrayList;
import java.util.Date;

import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.smahoo.homeos.common.Event;
import de.smahoo.homeos.common.EventListener;
import de.smahoo.homeos.common.EventType;
import de.smahoo.homeos.device.Device;
import de.smahoo.homeos.device.DeviceEvent;
import de.smahoo.homeos.device.DeviceEventListener;

public class RemoteDeviceManager {

	private List<RemoteDevice> deviceList;
	private DeviceEventListener eventListener;
	private List<EventListener> eventListeners;
	
	RemoteDeviceManager(){
		deviceList = new ArrayList<RemoteDevice>();
		eventListeners = new ArrayList<EventListener>();
		eventListener = new DeviceEventListener() {
			
			@Override
			public void onDeviceEvent(DeviceEvent event) {
				evaluateDeviceEvent(event);
				
			}
		};
	}
	
	protected void removeEventListener(EventListener listener){
		if (eventListeners.isEmpty()) return;
		eventListeners.remove(listener);
	}
	
	protected void addEventListener(EventListener listener){
		if (eventListeners.contains(listener)) return;
		eventListeners.add(listener);
	}
	
	protected void dispatchEvent(Event event){
		if (eventListeners.isEmpty()) return;
		for (EventListener listener : eventListeners){
			listener.onEvent(event);
		}
	}
	
	void init(Element elem, RemoteDeviceFactory factory){
		if (elem == null) return;
		if (!elem.getTagName().equalsIgnoreCase("devicelist")) return;
		NodeList nodelist = elem.getChildNodes();		
		for (int i=0; i<nodelist.getLength(); i++){
			if (nodelist.item(i) instanceof Element){
				factory.generateDevices((Element)nodelist.item(i));
			}
		}
	}
	
	public Device getDevice(String id){
		for (Device dev : deviceList){
			if (dev != null){
				if (dev.getDeviceId() != null){
					if (dev.getDeviceId().equalsIgnoreCase(id)) return dev;
					if (dev.getDeviceId().contains("@")){
						String subId = dev.getDeviceId().substring(dev.getDeviceId().indexOf("@")+1);
						if (id.equalsIgnoreCase(subId)){
							return dev;
						}
					}
				}
			}
		}
		return null;
	}
	
	public void removeDevice(String id){
		removeDevice(getDevice(id));
	}
	
	public void removeDevice(Device device){
		deviceList.remove(device);
		if (device.getLocation() != null){
			device.getLocation().removeDevice(device);
		}
		dispatchEvent(new DeviceEvent(EventType.DEVICE_REMOVED, device));
	}
	
	void addDevice(RemoteDevice device){
		if (device == null) return;
		if (deviceList.contains(device)) return;
		device.addDeviceEventListener(eventListener);
		deviceList.add(device);		
	}
	
	
	
	private void evaluateDeviceEvent(DeviceEvent event){
		dispatchEvent(event);
	}
	
	
	protected synchronized void setConnectionDate(Date date){
		for (Device device : deviceList){
			((RemoteDevice)device).setConnectionDate(date);
		}
	}
	
	public List<Device> getDeviceList(){
		List<Device> list = new ArrayList<Device>();
		for (Device device : deviceList){
			list.add(device);
		}
		return list;
	}
	
}
