package de.smahoo.homeos.device.roles;

import java.util.Date;


import de.smahoo.homeos.device.DeviceType;
import de.smahoo.homeos.device.role.DeviceRole;
import de.smahoo.homeos.device.role.DeviceRoleEvent;
import de.smahoo.homeos.device.role.RoleProperty;
import de.smahoo.homeos.devices.SensorHumidity;
import de.smahoo.homeos.property.Property;
import de.smahoo.homeos.property.PropertyType;

public class SensorHumidityRole extends DeviceRole implements SensorHumidity{


	
	public SensorHumidityRole(){		
		super(DeviceType.SENSOR_HUMIDITY);
		this.addRoleProperty(new RoleProperty(PropertyType.PT_DOUBLE,"humidity"));
	}
	
	public double getHumidity(){
		RoleProperty prop = this.getRoleProperty("humidity");
		if (prop != null){
			return (Double)prop.getValue();
		}
		return 0.0;
	}
	
	
}
