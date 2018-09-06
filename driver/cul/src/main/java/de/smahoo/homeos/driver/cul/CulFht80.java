package de.smahoo.homeos.driver.cul;


import java.util.List;



import de.smahoo.homeos.common.FunctionParameter;
import de.smahoo.homeos.device.DeviceProperty;
import de.smahoo.homeos.device.PhysicalDeviceFunction;
import de.smahoo.homeos.devices.HeatingRadiator;
import de.smahoo.homeos.devices.HeatingRtc;
import de.smahoo.homeos.property.Property;
import de.smahoo.homeos.property.PropertyType;

import de.runge.cul.Device;
import de.runge.cul.Fht80;

public class CulFht80 extends CulDevice implements HeatingRadiator {
	
	public CulFht80(Device device){
		super(device);		
	}
	
	@Override
    protected void generateDeviceFunctions(){
		//
	}

    @Override
	protected void execute(PhysicalDeviceFunction function, List<FunctionParameter> params){
		//
	}
	
	@Override
	protected void generateProperties(){	
		addProperty(new DeviceProperty(PropertyType.PT_DOUBLE,"actuator","%"));			
	} 

	@Override
	protected void onPropertyChanged(DeviceProperty property){
		// 
	}
	
	/**
	 * Will be called when properties of de.runge.cul.Device culDevice changed.
	 * Applies properties of de.runge.cul.Device
	 */
	@Override	
	protected void applyProperties(){
		Fht80 fht = (Fht80)culDevice;
		DeviceProperty dp;
				 
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
		
		
	}
		
	public double getValvePosition(){
		Property prop = this.getProperty("actuator");
		if (prop != null){
			if (prop.isValueSet()){
				double d = (Double)prop.getValue();
				return d;
			}
		} 
		return 0.0;
	}
	
	public boolean isOn(){
		Property prop = this.getProperty("actuator");
		if (prop != null){
			if (prop.isValueSet()){
				double d = (Double)prop.getValue();
				return d > 0.0;
			}
		} 
		return false;
		
		
	}

}