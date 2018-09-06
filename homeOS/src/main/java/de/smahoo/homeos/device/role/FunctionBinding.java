package de.smahoo.homeos.device.role;

import java.util.ArrayList;
import java.util.List;

import de.smahoo.homeos.device.PhysicalDevice;

public class FunctionBinding {

	private String roleFunctionName = null;
	private String deviceFunctionName = null;
	private List<ParameterBinding> parameterList = null;
	private PhysicalDevice physicalDevice = null;
	
	public FunctionBinding(){
		
	}	
	
	public FunctionBinding(String roleFunctionName, String deviceFunctionName){
		this();
		setRoleFunctionName(roleFunctionName);
		setDeviceFunctionName(deviceFunctionName);
	}
	
	public FunctionBinding(PhysicalDevice device, String roleFunctionName, String deviceFunctionName){
		this(roleFunctionName,deviceFunctionName);
		this.physicalDevice = device;
	}
	
	public PhysicalDevice getPhysicalDevice(){
		return physicalDevice;
	}
	
	public void setRoleFunctionName(String name){
		this.roleFunctionName = name;
	}
	
	public void setDeviceFunctionName(String name){
		this.deviceFunctionName = name;
	}
	
	public String getRoleFunctionName(){
		return roleFunctionName;
	}
	
	public String getDeviceFunctionName(){
		return deviceFunctionName;
	}
	
	protected void addParameterBinding(ParameterBinding paramBinding){
		if (parameterList == null){
			parameterList = new ArrayList<ParameterBinding>();
		}
		
		parameterList.add(paramBinding);
	}
	
	protected void addParameterBindings(List<ParameterBinding> paramBindingList){
		for (ParameterBinding pb : paramBindingList){
			addParameterBinding(pb);
		}
	}
	
	public List<ParameterBinding> getParameterBindings(){
		return parameterList;
	}
}
