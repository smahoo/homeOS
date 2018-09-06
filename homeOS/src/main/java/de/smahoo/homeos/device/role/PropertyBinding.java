package de.smahoo.homeos.device.role;

import de.smahoo.homeos.device.PhysicalDevice;

public class PropertyBinding {

	private PhysicalDevice physicalDevice = null;
	private String rolePropertyName = null;
	private String devPropertyName = null;
	
	public PropertyBinding(){
		
	}
	
	public PropertyBinding(PhysicalDevice device, String rolePropertyName, String devicePropertyName){
		this();
		this.physicalDevice = device;
		this.rolePropertyName = rolePropertyName;
		this.devPropertyName = devicePropertyName;
	}
	
	public String getRolePropertyName(){
		return rolePropertyName;
	}
	
	public String getDevicePropertyName(){
		return devPropertyName;
	}
	
	public PhysicalDevice getPhysicalDevice(){
		return physicalDevice;
	}
}
