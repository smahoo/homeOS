package de.smahoo.homeos.device;


import java.util.ArrayList;
import java.util.List;

import de.smahoo.homeos.common.FunctionExecutionException;
import de.smahoo.homeos.common.FunctionParameter;

public class ParameterizedDeviceFunction extends PhysicalDeviceFunction{
	
	List<FunctionParameter> parameter = null;
	
	public ParameterizedDeviceFunction(String name, List<FunctionParameter> parameter){
		this(PhysicalDeviceFunction.MISC, name, parameter);
	}
	
	public ParameterizedDeviceFunction(int functionType, String name, List<FunctionParameter> parameter){
		super(functionType,name);
		this.parameter = parameter;
	}
	
	public ParameterizedDeviceFunction(int functionType, List<FunctionParameter> parameter){
		super(functionType);
		this.parameter = parameter;
	}
	
	public List<FunctionParameter> getNeededParameter(){
		List<FunctionParameter> pList = new ArrayList<FunctionParameter>();
		
		for (FunctionParameter p : parameter){
			pList.add(new FunctionParameter(p.getPropertyType(), p.getName()));
		}
		
		return pList;
	}
	
	public void execute(List<FunctionParameter> params)  throws FunctionExecutionException{
		assignedDevice.executeFunction(this,params);
	}
	
}
