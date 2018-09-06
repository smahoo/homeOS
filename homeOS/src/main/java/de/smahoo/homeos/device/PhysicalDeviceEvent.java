package de.smahoo.homeos.device;

import java.util.List;


import de.smahoo.homeos.common.EventType;
import de.smahoo.homeos.common.FunctionParameter;
import de.smahoo.homeos.device.DeviceEvent;

public class PhysicalDeviceEvent extends DeviceEvent {
	
	protected DeviceProperty property = null;
	protected PhysicalDeviceFunction function = null;
	protected List<FunctionParameter> params = null;
	protected List<DeviceProperty> changedProperties = null;
	
	public PhysicalDeviceEvent(EventType eventType, PhysicalDevice device){
		super(eventType,device);	
	}
	
	@Deprecated
	public PhysicalDeviceEvent(EventType eventType, PhysicalDevice device, DeviceProperty property){
		this(eventType, device);
		this.property = property;
		this.description = toString();
	}
	
	public PhysicalDeviceEvent(EventType eventType, PhysicalDevice device, List<DeviceProperty> properties){
		this(eventType, device);
		this.changedProperties = properties;
		this.description = toString();
	}
	
	public PhysicalDeviceEvent(EventType eventType, PhysicalDevice device, PhysicalDeviceFunction function, List<FunctionParameter> params){
		this(eventType, device);
		this.function = function;
		this.params = params;
	}
	

	
	public boolean hasFunction(){
		return (function != null);
	}
	
	public PhysicalDeviceFunction getDeviceFunction(){
		return function;
	}
	
	public boolean hasProperties(){
		return changedProperties != null;
	}
	
	public List<DeviceProperty> getProperties(){
		return this.changedProperties;
	}
	
	@Deprecated
	public boolean hasProperty(){
		return (property != null);
	}
	
	@Deprecated
	public DeviceProperty getProperty(){
		return property;
	}
	
	public PhysicalDevice getDevice(){
		return (PhysicalDevice)device;
	}
	
	
	
	@Override
	public String toString(){
		String res = eventType.name()+" "+device.getDeviceId();
		switch (eventType){
			case LOCATION_ASSIGNED	  : res = res +" ("+device.getLocation().getName()+")"; break;
			case LOCATION_CHANGED	  : res = res +" ("+device.getLocation().getName()+")"; break;			
			case DEVICE_RENAMED		  : res = res +" ("+device.getName()+")"; break;
			case FUNCTION_EXECUTED	  : if (hasFunction()){
				res = res + "." +function.toString();				
				if (params != null){				
					res = res+"(";
					boolean semi = false;
					for (FunctionParameter p : params){
						if (semi) res = res+";";
						res = res + p.getName()+"=\""+p.getValue().toString()+"\"";
						semi = true;
					}
					res = res+")";
				} 
				
			}		
		}		
		return res;
	}
	
	
	@Override
	public String getDescription(){
		return toString();
	}
}
