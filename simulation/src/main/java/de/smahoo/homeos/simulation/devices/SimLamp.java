package de.smahoo.homeos.simulation.devices;

import java.util.List;


import de.smahoo.homeos.common.EventType;
import de.smahoo.homeos.common.FunctionExecutionException;
import de.smahoo.homeos.common.FunctionParameter;
import de.smahoo.homeos.device.DeviceProperty;
import de.smahoo.homeos.device.PhysicalDeviceEvent;
import de.smahoo.homeos.device.PhysicalDeviceFunction;
import de.smahoo.homeos.device.SimpleDeviceFunction;
import de.smahoo.homeos.devices.Lamp;
import de.smahoo.homeos.property.Property;
import de.smahoo.homeos.property.PropertyType;

public class SimLamp extends SimDevice implements Lamp{

	
	
	public SimLamp(String id){
		super(id);
	}
	
	protected void onPropertyChanged(final DeviceProperty property){
		
	}
	
	protected void generateDeviceFunctions(){
		PhysicalDeviceFunction function;
    	
    	function = new SimpleDeviceFunction(PhysicalDeviceFunction.TURN_ON);
    	this.addDeviceFunction(function);
    	
    	function = new SimpleDeviceFunction(PhysicalDeviceFunction.TURN_OFF);
    	this.addDeviceFunction(function);   
	}
	
	protected void generateProperties(){
		addProperty(new DeviceProperty(PropertyType.PT_BOOLEAN,"isOn",""));
	}
	
	protected void execute(final PhysicalDeviceFunction function, final List<FunctionParameter> params) throws FunctionExecutionException{
		if (function.getFunctionType() == PhysicalDeviceFunction.TURN_ON){
			turnOn();
			dispatchDeviceEvent(new PhysicalDeviceEvent(EventType.FUNCTION_EXECUTED, this,function,params));
			return;
		}
		if (function.getFunctionType() == PhysicalDeviceFunction.TURN_OFF){
			turnOff();
			dispatchDeviceEvent(new PhysicalDeviceEvent(EventType.FUNCTION_EXECUTED, this,function,params));
			return;
		}
	}
	
	public void turnOn(){		
		DeviceProperty prop = getProperty("isOn");
		prop.setValue(true);
		super.turnOn();
	}
	
	public void turnOff(){
		DeviceProperty prop = getProperty("isOn");
		prop.setValue(false);
		super.turnOff();
	}
}
