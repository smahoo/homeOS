package de.smahoo.homeos.device.roles;

import java.util.ArrayList;
import java.util.List;


import de.smahoo.homeos.common.EventType;
import de.smahoo.homeos.common.FunctionExecutionException;
import de.smahoo.homeos.common.FunctionParameter;
import de.smahoo.homeos.device.DeviceType;
import de.smahoo.homeos.device.role.DeviceRole;
import de.smahoo.homeos.device.role.DeviceRoleEvent;
import de.smahoo.homeos.device.role.RoleFunction;
import de.smahoo.homeos.devices.ExtendedTelevision;
import de.smahoo.homeos.property.PropertyType;

public class TelevisionRole extends DeviceRole implements ExtendedTelevision{

	public TelevisionRole(){
		super(DeviceType.TELEVISION);
		this.addRoleFunction(new RoleFunction("power",null));		
		this.addRoleFunction(new RoleFunction("volumeUp",null));
		this.addRoleFunction(new RoleFunction("volumeDown",null));
		this.addRoleFunction(new RoleFunction("channelUp",null));
		this.addRoleFunction(new RoleFunction("channelDown",null));
		this.addRoleFunction(new RoleFunction("mute",null));
		List<FunctionParameter> parameter = new ArrayList<FunctionParameter>();
		parameter.add(new FunctionParameter(PropertyType.PT_DOUBLE, "channel"));
		this.addRoleFunction(new RoleFunction("setChannel",parameter));
	}
	
	public void mute(){
		RoleFunction rf = getRoleFunction("mute");
		if (rf!=null){
			try {
				rf.execute();
				dispatchEvent(new DeviceRoleEvent(EventType.FUNCTION_EXECUTED,this,rf));
			} catch (Exception exc){
				// ...
			}
			
		} 
	}
	
	public void power(){
		RoleFunction rf = getRoleFunction("power");
		if (rf!=null){
			try {
				rf.execute();
				dispatchEvent(new DeviceRoleEvent(EventType.FUNCTION_EXECUTED,this,rf));
			} catch (Exception exc){
				// ..
			}
		} 
	}
	
	public void channelUp(){
		RoleFunction rf = getRoleFunction("channelUp");
		if (rf!=null){
			try {
				rf.execute();
				dispatchEvent(new DeviceRoleEvent(EventType.FUNCTION_EXECUTED,this,rf));
			} catch (Exception exc){
				// ..
			}
		} 
	}
	
	public void channelDown(){
		RoleFunction rf = getRoleFunction("channelDown");
		try {
			rf.execute();
			dispatchEvent(new DeviceRoleEvent(EventType.FUNCTION_EXECUTED,this,rf));
		} catch (Exception exc){
			// ..
		}
	}
	
	public void volumeUp(){
		RoleFunction rf = getRoleFunction("volumeUp");
		if (rf!=null){
			try {
				rf.execute();
				dispatchEvent(new DeviceRoleEvent(EventType.FUNCTION_EXECUTED,this,rf));
			} catch (Exception exc){
				// ..
			}
		} 
	}
	
	public void volumeDown(){
		RoleFunction rf = getRoleFunction("volumeDown");
		if (rf!=null){
			try {
				rf.execute();
				dispatchEvent(new DeviceRoleEvent(EventType.FUNCTION_EXECUTED,this,rf));
			} catch (Exception exc){
				// ..
			}
		} 
	}
	
	public void setChannel(int channel){
		RoleFunction rf = getRoleFunction("setChannel");
		if (rf!=null){
			List<FunctionParameter> params = rf.getNeededFunctionParameters();
			for (FunctionParameter fp : params){
				if (fp.getName().equalsIgnoreCase("channel")){
					fp.setValue(channel);
				}			
			}
			try {
				rf.execute();
				dispatchEvent(new DeviceRoleEvent(EventType.FUNCTION_EXECUTED,this,rf));
			} catch (Exception exc){
				// ..
			}
		} 
	}
	
}
