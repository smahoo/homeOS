package de.smahoo.homeos.device;

import java.util.ArrayList;
import java.util.List;


import de.smahoo.homeos.common.EventType;
import de.smahoo.homeos.device.Device;
import de.smahoo.homeos.device.DeviceEvent;
import de.smahoo.homeos.device.DeviceEventListener;
import de.smahoo.homeos.device.DeviceType;
import de.smahoo.homeos.device.role.DeviceRole;
import de.smahoo.homeos.device.role.DeviceRoleEvent;
import de.smahoo.homeos.device.role.DeviceRoleEventListener;
import de.smahoo.homeos.driver.Driver;
import de.smahoo.homeos.location.Location;

public class DeviceManager implements DeviceEventListener, DeviceRoleEventListener{

	List<Device> deviceList;
	List<PhysicalDevice> physicalDeviceList;
	List<DeviceRole> roleList;	
	List<DeviceEventListener> deviceEventListeners = null;
	List<DeviceRoleEventListener> deviceRoleEventListeners = null;

	public DeviceManager(){
		physicalDeviceList = new ArrayList<PhysicalDevice>();
		roleList = new ArrayList<DeviceRole>();
		deviceEventListeners = new ArrayList<DeviceEventListener>();
		deviceRoleEventListeners = new ArrayList<DeviceRoleEventListener>();
		deviceList = new ArrayList<Device>();
	}
	
	public PhysicalDevice getPhysicalDevice(String deviceId){
		String id = deviceId;
		if (deviceId.contains("@")){
			id = deviceId.substring(deviceId.indexOf("@")+1);
		}
		for(PhysicalDevice device : this.physicalDeviceList){
			if (device.getDeviceId().toLowerCase().equals(id.toLowerCase())){
				return device;
			}
		}
		return null;
	}
	
	public Device getDevice(String deviceId){
		String id = deviceId;
		if (deviceId.contains("@")){
			id = deviceId.substring(deviceId.indexOf("@")+1);
		}
		for (Device device :this.deviceList){
			if (device.getDeviceId().toLowerCase().equals(id.toLowerCase())){
				return device;
			}
		}		
		return null;
	}
	
	@Override
	public synchronized void onDeviceEvent(DeviceEvent evt) {		
		dispatchDeviceEvent(evt);		
	}
	
	public synchronized void onDeviceRoleEvent(DeviceRoleEvent event){
		this.dispatchDeviceRoleEvent(event);
		this.dispatchDeviceEvent(event);
	}
	
	public synchronized void deleteDevice(Device device){
		
		if (device instanceof PhysicalDevice){
			deleteDevice((PhysicalDevice)device);
		}
		
		if (device instanceof DeviceRole){
			deleteDevice((DeviceRole)device);
		}
	}
	
	protected synchronized void deleteDevice(PhysicalDevice device){
		if (!physicalDeviceList.contains(device)) return;
		
		if (device.getLocation() != null){
			device.removeLocation();
		}
		physicalDeviceList.remove(device);
		deviceList.remove(device);
		dispatchDeviceEvent(new PhysicalDeviceEvent(EventType.DEVICE_REMOVED, (PhysicalDevice)device));	
		// FIXME Check if there exists a role assigned to this device		
	}
	
	protected synchronized void deleteDevice(DeviceRole role){
		//this.roleList		
	}
	
	public synchronized void addDevice(PhysicalDevice device, Driver driver){
		if (physicalDeviceList.contains(device)) return;
		if (device == null){
			return;
		}
		device.driver = driver;
		device.deviceManager = this;
		device.addDeviceEventListener(this);
		physicalDeviceList.add(device);
		deviceList.add(device);		
		dispatchDeviceEvent(new PhysicalDeviceEvent(EventType.DEVICE_ADDED, device));		
	}
	
	public synchronized void removeDevice(PhysicalDevice device){
		device.removeDeviceEventListener(this);
		physicalDeviceList.remove(device);
		deviceList.remove(device);
		dispatchDeviceEvent(new PhysicalDeviceEvent(EventType.DEVICE_REMOVED, device));
	}
	
	public synchronized void addDeviceEventListener(DeviceEventListener listener){
		this.deviceEventListeners.add(listener);
	}
	
	public synchronized void addDeviceRoleEventListener(DeviceRoleEventListener listener){
		if (this.deviceRoleEventListeners.contains(listener)) return;
		this.deviceRoleEventListeners.add(listener);
	}
	
	public synchronized void removeDeviceRoleEventListener(DeviceRoleEventListener listener){
		if (!deviceRoleEventListeners.contains(listener)) return;
		deviceRoleEventListeners.remove(listener);
	}
	
	protected synchronized void dispatchDeviceEvent(DeviceEvent evnt){
		if (this.deviceEventListeners.isEmpty()) return;		
		for (DeviceEventListener listener : deviceEventListeners){
			listener.onDeviceEvent(evnt);
		}
	}
	
	protected synchronized void dispatchDeviceRoleEvent(DeviceRoleEvent event){
		if (this.deviceRoleEventListeners.isEmpty()) return;
		for (DeviceRoleEventListener listener : deviceRoleEventListeners){
			listener.onDeviceRoleEvent(event);
		}
	}
	
	public List<PhysicalDevice> getPhysicalDevices(){
		return physicalDeviceList;
	}
	
	
	public void addDeviceRole(DeviceRole role){
		if (roleList.contains(role)) return;
		role.addEventListener(this);
		roleList.add(role);
		if (role instanceof Device){
			deviceList.add(role);
		}
		this.dispatchDeviceRoleEvent(new DeviceRoleEvent(EventType.ROLE_ADDED, role));
		this.dispatchDeviceEvent(new DeviceEvent(EventType.DEVICE_ADDED, role));
	}
	
	public List<DeviceRole> getDeviceRoles(){
		return this.roleList;
	}
	
	public List<DeviceRole> getDeviceRoles(DeviceType type){
		List<DeviceRole> list = new ArrayList<DeviceRole>();		
		for (DeviceRole role : roleList){
			if (role.getDeviceType()==type){
				list.add(role);
			}
		}		
		return list;
	}
	
	public List<Device> getDevices(){
		List<Device> list = new ArrayList<Device>();
		for (Device d: deviceList){
			list.add(d);
		}
		return list;
	}
	
	public List<Device> getDevices(Driver driver){
		List<Device> list = new ArrayList<Device>();
		PhysicalDevice tmp;
		for (Device d : deviceList){
			if (d instanceof PhysicalDevice){
				tmp = (PhysicalDevice)d;
				if (tmp.getDriver() == driver){
					list.add(tmp);
				}
			}
		}
		
		return list;
	}
	
	public List<Device> getDevices(Location location){
		List<Device> list = new ArrayList<Device>();
		
		for (Device device : this.deviceList){		
				if (device.getLocation() == location){
					list.add(device);
				}
		}		
		return list;
	}
	
	public List<Device> getDevices(DeviceType type){
		List<Device> list = new ArrayList<Device>();
		
		for (Device device : this.deviceList){		
				Class<?> c = DeviceType.getDeviceInterface(type);				
				if (c != null){
					if (c.isInstance(device)){
						list.add(device);						
					}
				}			
		}		
		return list;
	}
	
	public List<Device> getDevices(DeviceType type, Location location){
		List<Device> list = new ArrayList<Device>();
		
		for (Device device : this.deviceList){		
			if (device.getLocation() == location){
				Class<?> c = DeviceType.getDeviceInterface(type);				
				if (c != null){
					if (c.isInstance(device)){
						list.add(device);						
					}
				}			
			}
		}		
		return list;
	}
	
	
	
	
}
