package de.smahoo.homeos.driver.cul;

import java.util.ArrayList;
import java.util.List;


import de.smahoo.homeos.common.FunctionParameter;
import de.smahoo.homeos.device.DeviceProperty;
import de.smahoo.homeos.device.ParameterizedDeviceFunction;
import de.smahoo.homeos.device.PhysicalDeviceFunction;
import de.smahoo.homeos.property.PropertyType;

import de.runge.cul.Device;
import de.runge.cul.FS20irf;

public class CulFs20irf extends CulDevice{

	public CulFs20irf (Device device){
		super(device);		
	}
	
    protected void generateDeviceFunctions(){
	
    	ParameterizedDeviceFunction function; 
    	
    	FunctionParameter fm = new FunctionParameter(PropertyType.PT_INTEGER,"channel");
    	List<FunctionParameter> l = new ArrayList<FunctionParameter>();
    	l.add(fm);
    	function = new ParameterizedDeviceFunction(PhysicalDeviceFunction.SEND, l);
    	    	
    	this.addDeviceFunction(function);
    	
	}

	
    @Override
	protected void execute(PhysicalDeviceFunction function, List<FunctionParameter> params){
		FS20irf fs20irf = (FS20irf)this.culDevice;
		
		switch (function.getFunctionType()){
		  case PhysicalDeviceFunction.SEND  :
			  if (params != null){
			      if (params.size() == 1){
				      FunctionParameter fm = params.get(0);
				      int value = (Integer)fm.getValue();
				      fs20irf.send(value);				      
			      }
			  }
			  		  
		}
	}
	
	protected FS20irf getFs20irfDevice(){
		return (FS20irf)culDevice;
	}
	
	@Override
	public boolean isOn(){
		return isAvailable();
	}
	
	@Override
	protected void generateProperties(){}
	
	@Override
	protected void onPropertyChanged(DeviceProperty property){}
			
	@Override
	protected void applyProperties(){}
	
}
