package de.smahoo.homeos.device.role;

public class ParameterBinding {
	String roleFunctionProperty = null;
	String devFunctionProperty = null;
	
	
	public ParameterBinding(String roleProp, String devProp){
		this.roleFunctionProperty = roleProp;
		this.devFunctionProperty = devProp;
	}
	
	public boolean isRoleParameterFix(){
		return roleFunctionProperty.startsWith("#");
	}
	
	public String getRoleParameterName(){
		return roleFunctionProperty;
	}
	
	public String getDeviceParameterName(){
		return devFunctionProperty;
	}
	
	public String getFixedRoleParameterValue(){
		if (!isRoleParameterFix()){
			return null;
		}
		String res = roleFunctionProperty.substring(1);
		
		return res;
	}
}
