package de.smahoo.homeos.simulation.devices;

import java.util.ArrayList;
import java.util.List;


import de.smahoo.homeos.common.EventType;
import de.smahoo.homeos.common.FunctionExecutionException;
import de.smahoo.homeos.common.FunctionParameter;
import de.smahoo.homeos.device.DeviceEvent;
import de.smahoo.homeos.device.DeviceProperty;
import de.smahoo.homeos.device.ParameterizedDeviceFunction;
import de.smahoo.homeos.device.PhysicalDeviceEvent;
import de.smahoo.homeos.device.PhysicalDeviceFunction;
import de.smahoo.homeos.device.SimpleDeviceFunction;
import de.smahoo.homeos.devices.HeatingRtc;
import de.smahoo.homeos.property.Property;
import de.smahoo.homeos.property.PropertyType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SimHeating extends SimDevice implements HeatingRtc{


	
	
	public SimHeating(String id){
		super(id);
	}
	
	protected void onPropertyChanged(final DeviceProperty property){
		
	}
	public double getValvePosition(){
		return 0.0;
	}
	
	protected void generateDeviceFunctions(){
		PhysicalDeviceFunction function;
		
		FunctionParameter fm = new FunctionParameter(PropertyType.PT_DOUBLE,"temperature");
    	List<FunctionParameter> l = new ArrayList<FunctionParameter>();
    	l.add(fm);
    	function = new ParameterizedDeviceFunction(PhysicalDeviceFunction.SET_TEMPERATURE,l);
    	
    	this.addDeviceFunction(function);
    	   
	}
	
	protected void generateProperties(){
		addProperty(new DeviceProperty(PropertyType.PT_DOUBLE,"desiredTemperature","°C"));
	}
	
	protected void execute(final PhysicalDeviceFunction function, final List<FunctionParameter> params) throws FunctionExecutionException{
		switch (function.getFunctionType()){	
		  case PhysicalDeviceFunction.SET_TEMPERATURE : 
			  if (params != null){
			      if (params.size() == 1){
				      FunctionParameter fm = params.get(0);
				      Double value = (Double)fm.getValue();
				     
				      this.getProperty("desiredTemperature").setValue(value);
				      setTemperature(value);
				  
			      }
			  }
		}
	}
	
	public void setTemperature(double temperature){
		this.getProperty("desiredTemperature").setValue(temperature);
		if (temperature > 6.5){
			super.turnOn();
		} else {
			super.turnOff();
		}
		this.dispatchDeviceEvent(new DeviceEvent(EventType.DEVICE_PROPERTY_CHANGED, this));	
	}
	
	public double getDesiredTemperature() {
		Property prop = getProperty("desiredTemperature");
		if (prop.isValueSet()){
			return (Double)prop.getValue();
		} else {
			return 0.0;
		}
	}


}
