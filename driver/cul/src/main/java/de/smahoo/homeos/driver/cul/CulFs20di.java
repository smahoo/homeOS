package de.smahoo.homeos.driver.cul;

import java.util.ArrayList;
import java.util.List;


import de.smahoo.homeos.common.EventType;
import de.smahoo.homeos.common.FunctionExecutionException;
import de.smahoo.homeos.common.FunctionParameter;
import de.smahoo.homeos.device.DeviceEvent;
import de.smahoo.homeos.device.ParameterizedDeviceFunction;
import de.smahoo.homeos.device.PhysicalDeviceFunction;
import de.smahoo.homeos.device.SimpleDeviceFunction;
import de.smahoo.homeos.devices.Dimmable;
import de.smahoo.homeos.property.PropertyType;

import de.smahoo.cul.Device;
import de.smahoo.cul.Fs20di;

public class CulFs20di extends CulFs20st implements Dimmable{

	public CulFs20di(Device device){
		super(device);		
	}
	
	
	protected void generateDeviceFunctions(){
		super.generateDeviceFunctions();
    	SimpleDeviceFunction function;
    	
    	function = new SimpleDeviceFunction(PhysicalDeviceFunction.DIMM_DOWN);
    	this.addDeviceFunction(function);
    	
    	function = new SimpleDeviceFunction(PhysicalDeviceFunction.DIMM_UP);
    	this.addDeviceFunction(function);
    	    	
    	ParameterizedDeviceFunction pf;
    	List<FunctionParameter> paramList;
    	
    	paramList = new ArrayList<FunctionParameter>();
    	paramList.add(new FunctionParameter(PropertyType.PT_LONG,"milliseconds"));
    	pf = new ParameterizedDeviceFunction(PhysicalDeviceFunction.SET_TURN_OFF_DIMMTIME, "setTurnOffDimmTime", paramList);    	
    	this.addDeviceFunction(pf);
    	pf = new ParameterizedDeviceFunction(PhysicalDeviceFunction.SET_TURN_ON_DIMMTIME, "setTurnOnDimmTime", paramList);    	
    	this.addDeviceFunction(pf);
    	
	}

	@Override
	protected void execute(PhysicalDeviceFunction function, List<FunctionParameter> params){
		super.execute(function, params);
		Fs20di fs20 = (Fs20di)this.culDevice;		
		switch (function.getFunctionType()){
		  case PhysicalDeviceFunction.DIMM_UP  : fs20.dimmUp(); break;
		  case PhysicalDeviceFunction.DIMM_DOWN : fs20.dimmDown();break;
		  case PhysicalDeviceFunction.SET_TURN_OFF_DIMMTIME :
			  if (!params.isEmpty()){
				  FunctionParameter p = params.get(0);
				  long milliseconds;
				  try {
					  milliseconds = (Long)p.getValue();
				  } catch (Exception exc){
					  exc.printStackTrace();
					return;
				  }
				  fs20.setOffDimmTime(milliseconds);
				  
			  }
			  break;
		  case PhysicalDeviceFunction.SET_TURN_ON_DIMMTIME : 
			  if (!params.isEmpty()){
				  FunctionParameter p = params.get(0);
				  long milliseconds;
				  try {
					  milliseconds = (Long)p.getValue();
				  } catch (Exception exc){
					  exc.printStackTrace();
					  return;
				  }
				  fs20.setOnDimmTime(milliseconds);				  
			  }
			  break;
	  }// switch
	}
	
	public void dimmUp(){
		PhysicalDeviceFunction df = getFunction("dimmUp");
		if (df != null){
			try {
				if (!isOnState){
					isOnState = true;					
					this.dispatchDeviceEvent(new DeviceEvent(EventType.DEVICE_ON, this));		
				}
				df.execute();				
			} catch (Exception exc){
				// ...
			}
		}
	}
	
	public void dimmDown(){
		PhysicalDeviceFunction df = getFunction("dimmDown");
		if (df != null){
			try {
				df.execute();
			} catch (Exception exc){
				// ...
			}
		}
	}
	
	public void setDimmLevel(int dimmLevel){
		// FIXME: not implemented yet -> ain't that easy
	}

}
