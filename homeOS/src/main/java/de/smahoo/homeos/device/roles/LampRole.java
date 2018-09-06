package de.smahoo.homeos.device.roles;


import de.smahoo.homeos.common.EventType;
import de.smahoo.homeos.common.FunctionExecutionException;
import de.smahoo.homeos.device.DeviceType;
import de.smahoo.homeos.device.role.DeviceRole;
import de.smahoo.homeos.device.role.DeviceRoleEvent;
import de.smahoo.homeos.device.role.RoleFunction;
import de.smahoo.homeos.devices.Lamp;

public class LampRole extends DeviceRole implements Lamp{

	public LampRole(){
		super(DeviceType.LAMP);
		this.addRoleFunction(new RoleFunction("turnOn",null));
		this.addRoleFunction(new RoleFunction("turnOff",null));
	}
	
	public void turnOn(){
		RoleFunction rf = getRoleFunction("turnOn");
		
		if (rf!=null){
			try {
				rf.execute();			
				dispatchEvent(new DeviceRoleEvent(EventType.FUNCTION_EXECUTED,this,rf));
			} catch (Exception exc){
				// ..
			}
		} 
	}
	
	public void turnOff(){
		RoleFunction rf = getRoleFunction("turnOff");
		if (rf!=null){
			try {
				rf.execute();
				dispatchEvent(new DeviceRoleEvent(EventType.FUNCTION_EXECUTED,this,rf));
			} catch (Exception exc){
				
			}
		}
	}
}
