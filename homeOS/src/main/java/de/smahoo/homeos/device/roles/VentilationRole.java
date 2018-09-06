package de.smahoo.homeos.device.roles;


import de.smahoo.homeos.common.EventType;
import de.smahoo.homeos.common.FunctionExecutionException;
import de.smahoo.homeos.device.DeviceType;
import de.smahoo.homeos.device.role.DeviceRole;
import de.smahoo.homeos.device.role.DeviceRoleEvent;
import de.smahoo.homeos.device.role.RoleFunction;

public class VentilationRole extends DeviceRole{

	
	public VentilationRole(){
		super(DeviceType.VENTILATION);
		this.addRoleFunction(new RoleFunction("turnOn",null));
		this.addRoleFunction(new RoleFunction("turnOff",null));
	}
	
	public void turnOn() throws FunctionExecutionException{
		RoleFunction rf = getRoleFunction("turnOn");
		if (rf!=null){
			rf.execute();
			dispatchEvent(new DeviceRoleEvent(EventType.FUNCTION_EXECUTED,this,rf));
		} 
	}
	
	public void turnOff() throws FunctionExecutionException{
		RoleFunction rf = getRoleFunction("turnOff");
		if (rf!=null){
			rf.execute();
			dispatchEvent(new DeviceRoleEvent(EventType.FUNCTION_EXECUTED,this,rf));
		} 
	}
}
