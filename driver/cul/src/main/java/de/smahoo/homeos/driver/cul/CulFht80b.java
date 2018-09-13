package de.smahoo.homeos.driver.cul;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import de.smahoo.homeos.common.EventType;
import de.smahoo.homeos.common.FunctionParameter;
import de.smahoo.homeos.device.DeviceEvent;
import de.smahoo.homeos.device.DeviceProperty;
import de.smahoo.homeos.device.ParameterizedDeviceFunction;
import de.smahoo.homeos.device.PhysicalDeviceFunction;
import de.smahoo.homeos.device.SimpleDeviceFunction;
import de.smahoo.homeos.devices.HeatingRadiator;
import de.smahoo.homeos.devices.HeatingRtc;
import de.smahoo.homeos.devices.SensorTemperature;
import de.smahoo.homeos.property.Property;
import de.smahoo.homeos.property.PropertyType;

import de.smahoo.cul.Device;
import de.smahoo.cul.Fht80b;

public class CulFht80b extends CulFht80 implements SensorTemperature,HeatingRtc {
	
	public CulFht80b(Device device){
		super(device);		
	}
	
	@Override
    protected void generateDeviceFunctions(){
	
    	PhysicalDeviceFunction function;
    	
    	function = new SimpleDeviceFunction(PhysicalDeviceFunction.TURN_ON);
    	this.addDeviceFunction(function);
    	
    	function = new SimpleDeviceFunction(PhysicalDeviceFunction.TURN_OFF);
    	this.addDeviceFunction(function);   
    	
    	FunctionParameter fm = new FunctionParameter(PropertyType.PT_DOUBLE,"temperature");
    	List<FunctionParameter> l = new ArrayList<FunctionParameter>();
    	l.add(fm);
    	function = new ParameterizedDeviceFunction(PhysicalDeviceFunction.SET_TEMPERATURE,l);
    	
    	this.addDeviceFunction(function);
	}

    @Override
	protected void execute(PhysicalDeviceFunction function, List<FunctionParameter> params){
		Fht80b fht = (Fht80b)this.culDevice;
		
		switch (function.getFunctionType()){
		  //case DeviceFunction.TURN_ON  : fht.turnOn(); dispatchDeviceEvent(new DeviceEvent(DeviceEvent.FUNCTION_EXECUTED, this, function, params)); break;
		  case PhysicalDeviceFunction.TURN_OFF : fht.turnOff(); break;
		  case PhysicalDeviceFunction.SET_TEMPERATURE : 
			  if (params != null){
			      if (params.size() == 1){
				      FunctionParameter fm = params.get(0);
				      Double value = (Double)fm.getValue();
				      fht.setTemperature(value);
				      this.getProperty("desiredTemperature").setValue(value);
				  		this.dispatchDeviceEvent(new DeviceEvent(EventType.DEVICE_PROPERTY_CHANGED, this));	
			      }
			  }
		}
	}
	
	@Override
	protected void generateProperties(){
		addProperty(new DeviceProperty(PropertyType.PT_DOUBLE,"desiredTemperature","°C"));
		addProperty(new DeviceProperty(PropertyType.PT_DOUBLE,"temperature","°C"));
		addProperty(new DeviceProperty(PropertyType.PT_DOUBLE,"actuator","%"));
		addProperty(new DeviceProperty(PropertyType.PT_BOOLEAN,"windowOpen",""));
		addProperty(new DeviceProperty(PropertyType.PT_BOOLEAN,"batteryOk",""));		
	} 

	@Override
	protected void onPropertyChanged(DeviceProperty property){
		// 
	}
	
	/**
	 * Will be called when properties of de.smahoo.cul.Device culDevice changed.
	 * Applies properties of de.smahoo.cul.Device
	 */
	@Override	
	protected void applyProperties(){
		Fht80b fht = (Fht80b)culDevice;
		DeviceProperty dp;
		
		
		if (fht.isTemperatureSet()){
			dp = this.getProperty("temperature");		
		    if (dp.isValueSet()){
		       if (fht.getTemperature() != (Double)(dp.getValue())){
			      dp.setValue(fht.getTemperature());
		       }
		   } else {
			dp.setValue(fht.getTemperature());
		   }
		}
		 
		if (fht.isActuatorSet()){
		   dp = this.getProperty("actuator");		
		   if (dp.isValueSet()){
		       if (fht.getActuator() != (Double)(dp.getValue())){
			        dp.setValue(fht.getActuator());
		       }
		   } else {
			   dp.setValue(fht.getActuator());
		   }
		} 
		
		
		if (fht.isDesiredTemperatureSet()){
			dp = this.getProperty("desiredTemperature");
			if (dp.isValueSet()){
				if (fht.getDesiredTemperature() != (Double)(dp.getValue())){
					dp.setValue(fht.getDesiredTemperature());
				}
			} else {
			     dp.setValue(fht.getDesiredTemperature());
			}			
		}
		
		if (fht.isBatteryOkSet()){
			dp = this.getProperty("batteryOk");
			if (dp != null){
				if (dp.isValueSet()){
					boolean b = (Boolean)dp.getValue();
					if (b != fht.isBatteryOk()){
						dp.setValue(fht.isBatteryOk());
					}
				} else dp.setValue(fht.isBatteryOk());
			}
		}
		
		if (fht.isWindowOpenSet()){
			dp = this.getProperty("windowOpen");
			if (dp != null){
				if (dp.isValueSet()){
					boolean b = (Boolean)dp.getValue();
					if (b != fht.isWindowOpen()){
						dp.setValue(fht.isWindowOpen());
					}
				} else dp.setValue(fht.isWindowOpen());
			}
		}
		
	}
	
	public double getTemperature(){
		DeviceProperty prop = getProperty("temperature");
		if (prop != null){
			if (prop.isValueSet()){
				return (Double)prop.getValue();
			}
		}
		return 0.0;
	}
	
	public double getDesiredTemperature(){
		DeviceProperty prop = getProperty("desiredTemperature");
		if (prop != null){
			if (prop.isValueSet()){
				return (Double)prop.getValue();
			}
		}
		return 0.0;
	}
	
	public void setTemperature(double temperature){
		
		ParameterizedDeviceFunction df = (ParameterizedDeviceFunction)getFunction(PhysicalDeviceFunction.SET_TEMPERATURE);
		FunctionParameter fm = new FunctionParameter(PropertyType.PT_DOUBLE,"temperature");
		fm.setValue(temperature);
    	List<FunctionParameter> l = new ArrayList<FunctionParameter>();
    	l.add(fm);
    	try {
    		df.execute(l);
    	} catch (Exception exc){
    		// ..
    	}
		
		DeviceProperty prop = getProperty("desiredTemperature");
		prop.setValue(temperature);
	}

	
	public boolean isOn(){
		Property prop = this.getProperty("actuator");
		if (prop != null){
			if (prop.isValueSet()){
				double d = (Double)prop.getValue();
				return d > 0.0;
			}
		} 
		return isAvailable();		
	}
	
	
}
