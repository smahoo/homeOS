package de.smahoo.homeos.device.role;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;


import de.smahoo.homeos.common.EventType;
import de.smahoo.homeos.device.Device;
import de.smahoo.homeos.device.DeviceEvent;
import de.smahoo.homeos.device.DeviceEventListener;
import de.smahoo.homeos.device.DeviceImpl;
import de.smahoo.homeos.device.DeviceProperty;
import de.smahoo.homeos.device.DeviceType;
import de.smahoo.homeos.device.PhysicalDevice;
import de.smahoo.homeos.property.PropertyEvent;
import de.smahoo.homeos.property.PropertyEventListener;

public class DeviceRole extends DeviceImpl implements PropertyEventListener{

	protected DeviceType devType;
	protected String name;
	protected HashMap<String,RoleFunction> roleFunctions;
	protected HashMap<String,RoleProperty> roleProperties;
	protected List<DeviceRoleEventListener> roleEventListeners;
	protected List<Device> devices;
	protected DeviceEventListener deviceEventListener;	
	
	public DeviceRole(DeviceType type){
		super(DeviceRoleFactory.getInstance().generateId(type.name()));
		this.devType = type;
		roleProperties 	= new HashMap<String,RoleProperty>();
		roleFunctions 	= new HashMap<String, RoleFunction>();	
		roleEventListeners = new ArrayList<DeviceRoleEventListener>();
		devices = new ArrayList<Device>();
		deviceEventListener = new DeviceEventListener() {
			
			@Override
			public void onDeviceEvent(DeviceEvent event) {
				
				evaluateDeviceEvent(event);				
			}
		};
	}
	
	
	protected void evaluateDeviceEvent(DeviceEvent event){
		switch (event.getEventType()){
			case DEVICE_ADDED:
			case DEVICE_AVAILABLE:
			case PROPERTY_VALUE_CHANGED:
			case DEVICE_PROPERTY_CHANGED:
			case FUNCTION_EXECUTED: this.lastActivity = new Date();	break;
		}
		if ((event.getEventType() == EventType.DEVICE_ON)||(event.getEventType() == EventType.DEVICE_OFF)){
			if (isOn()){
				dispatchEvent(new DeviceRoleEvent(EventType.DEVICE_ON, this));
			} else {
				dispatchEvent(new DeviceRoleEvent(EventType.DEVICE_OFF,this));
			}
		}
	}
	
	public DeviceRole(DeviceType type, String name){
		this(type);
		this.name = name;
	}
	
	public DeviceType getDeviceType(){
		return devType;
	}
	
	protected void addDevice(Device device){
		if (devices.contains(device)) return;
		((DeviceImpl)device).addDeviceEventListener(deviceEventListener);
		devices.add(device);
	}
	
	protected void addRoleProperty(RoleProperty roleProperty){
		this.roleProperties.put(roleProperty.getName(),roleProperty);
		roleProperty.deviceRole = this;
		roleProperty.addEventListener(this);
		if (roleProperty.bindedProperty instanceof DeviceProperty){
			addDevice(((DeviceProperty)roleProperty.bindedProperty).getDevice());
		}
	}
	
	protected RoleProperty getRoleProperty(String name){
		return roleProperties.get(name);
	}
	
	protected void addRoleFunction(RoleFunction roleFunction){
		this.roleFunctions.put(roleFunction.getName(),roleFunction);
		roleFunction.deviceRole = this;		
	}
	
	public RoleFunction getRoleFunction(String name){
		return roleFunctions.get(name);
	}
	
	public List<RoleProperty> getRoleProperties(){
		List<RoleProperty> list = new ArrayList<RoleProperty>();		
		Set<Entry<String,RoleProperty>> set = roleProperties.entrySet();		
		for (Entry<String, RoleProperty> e : set){
			list.add(e.getValue());
		}		
		return list;
	}
	
	
	
	public List<RoleFunction> getRoleFunctions(){
		List<RoleFunction> list = new ArrayList<RoleFunction>();
		
		Set<Entry<String,RoleFunction>> set = roleFunctions.entrySet();
		
		for (Entry<String,RoleFunction> e : set){
			list.add(e.getValue());
		}		
		return list;
	}
	
	public List<Device> getDevices(){
		List<RoleFunction> roleFunctionList = getRoleFunctions();
		List<Device> devList = new ArrayList<Device>();
				
		for (RoleFunction f : roleFunctionList){
			if (!devList.contains(f.bindedDeviceFunction.getDevice())){
				devList.add(f.bindedDeviceFunction.getDevice());
			}
		}
		
		List<RoleProperty> rolePropertyList = getRoleProperties();
		DeviceProperty tmp;
		for (RoleProperty p : rolePropertyList){
			if (p.bindedProperty instanceof DeviceProperty){
				tmp = (DeviceProperty)p.bindedProperty;
				if (!devList.contains(tmp.getDevice())){					
					devList.add(tmp.getDevice());
				}
			}
		}
		return devList;
	}
	
	
	
	public String getName(){
		return name;
	}
	
	public void setName(String name){
		
		if ((name != null) && (name.equals(this.name))) return;
		this.name = name;
		this.dispatchEvent(new DeviceRoleEvent(EventType.DEVICE_RENAMED,this));
	}
	
	public String toString(){
		return name;
	}
	
	public void onDeviceRoleEvent(DeviceRoleEvent evnt){
		dispatchEvent(evnt);
	}
	
	public void addEventListener(DeviceRoleEventListener listener){
		if (!roleEventListeners.contains(listener)){
			roleEventListeners.add(listener);
		}
	}
	
	public void removeEventListener(DeviceRoleEventListener listener){
		if (roleEventListeners.contains(listener)){
			roleEventListeners.remove(listener);
		}
	}
	
	protected void dispatchEvent(DeviceRoleEvent event){
		if (roleEventListeners.isEmpty()) return;
		for (DeviceRoleEventListener listener : roleEventListeners){
			listener.onDeviceRoleEvent(event);
		}
	}
	
	public void onPropertyEvent(PropertyEvent evnt){
		dispatchEvent(new DeviceRoleEvent(evnt.getEventType(), this));
	}

	
	@Override
	public boolean isOn(){
		boolean result = false;
		
		for (Device device : this.devices){
			result = result || device.isOn();
		}		
		
		return result;
	}

	
	@Override
	public boolean isAvailable(){
		boolean result = false;
		
		for (Device device : this.devices){
			result = result || device.isAvailable();
		}		
		
		return result;
	}
	
}
