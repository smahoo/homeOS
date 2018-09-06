package de.smahoo.homeos.device.role;


import de.smahoo.homeos.common.EventType;
import de.smahoo.homeos.device.DeviceEvent;

public class DeviceRoleEvent extends DeviceEvent{
	

	private RoleFunction rf = null;
	
	public DeviceRoleEvent(EventType eventType, DeviceRole role){
		super(eventType, role);		
	}
	
	public DeviceRoleEvent(EventType eventType, DeviceRole role, RoleFunction roleFunction){
		this(eventType,role);
		this.rf = roleFunction;
	}
	
	public DeviceRoleEvent(EventType eventType, String msg, DeviceRole role){
		super(eventType, msg, role);		
	}
	
	
	public boolean hasRoleFunction(){
		return (rf!=null);
	}
	
	public RoleFunction getRoleFunction(){
		return rf;
	}
	@Override
	public String toString(){		
		return super.toString();
	}
	
	
}
