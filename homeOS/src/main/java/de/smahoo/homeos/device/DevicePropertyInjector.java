package de.smahoo.homeos.device;


import de.smahoo.homeos.device.Device;
import de.smahoo.homeos.device.role.DeviceRole;
import de.smahoo.homeos.device.role.RoleProperty;
import de.smahoo.homeos.property.Property;
import de.smahoo.homeos.property.PropertyType;

public class DevicePropertyInjector {

	public Property injectProperty(Device device, PropertyType propertyType, String propertyName, String propertyUnit){
		Property prop = null;
		
		if (device instanceof PhysicalDevice){
			PhysicalDevice dev = (PhysicalDevice)device;
			prop = new DeviceProperty(propertyType,propertyName,propertyUnit);			
			dev.addProperty((DeviceProperty)prop);
		}
		if (device instanceof DeviceRole){
			prop = new RoleProperty(propertyType,propertyName);
			DeviceRole role = (DeviceRole)device;
			
		}
		return prop;
	}
	
	
	
}
