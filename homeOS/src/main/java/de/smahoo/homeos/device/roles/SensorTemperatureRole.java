package de.smahoo.homeos.device.roles;

import java.util.Date;


import de.smahoo.homeos.device.DeviceType;
import de.smahoo.homeos.device.role.DeviceRole;
import de.smahoo.homeos.device.role.DeviceRoleEvent;
import de.smahoo.homeos.device.role.RoleProperty;
import de.smahoo.homeos.devices.SensorTemperature;
import de.smahoo.homeos.property.Property;
import de.smahoo.homeos.property.PropertyType;

public class SensorTemperatureRole extends DeviceRole implements SensorTemperature{

	
	public SensorTemperatureRole(){
		super(DeviceType.SENSOR_TEMPERATURE);
		this.addRoleProperty(new RoleProperty(PropertyType.PT_DOUBLE,"temperature"));
	}
	
	public double getTemperature(){
		RoleProperty prop = this.getRoleProperty("temperature");
		if (prop != null){
			return (Double)prop.getValue();
		}
		return (Double)null;
	}
	
	
}
