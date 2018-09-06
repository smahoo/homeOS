package de.smahoo.homeos.device.roles;

import java.util.ArrayList;
import java.util.List;


import de.smahoo.homeos.common.FunctionExecutionException;
import de.smahoo.homeos.common.FunctionParameter;
import de.smahoo.homeos.device.DeviceType;
import de.smahoo.homeos.device.role.DeviceRole;
import de.smahoo.homeos.device.role.RoleFunction;
import de.smahoo.homeos.property.PropertyType;

public class HeatingRole extends DeviceRole{

	public HeatingRole(){
		super(DeviceType.HEATING_RTC);
		FunctionParameter fp = new FunctionParameter(PropertyType.PT_DOUBLE,"temperature");
		List<FunctionParameter> fpl = new ArrayList<FunctionParameter>();
		fpl.add(fp);
		this.addRoleFunction(new RoleFunction("setTemperature", fpl));
	}
	
	public void setTemperature(double temperature) throws FunctionExecutionException{
		RoleFunction rf = this.getRoleFunction("setTemperature");
		FunctionParameter fp = new FunctionParameter(PropertyType.PT_DOUBLE,temperature,"temperature");
		List<FunctionParameter> fpl = new ArrayList<FunctionParameter>();
		fpl.add(fp);
		rf.execute(fpl);
	}
	
}
