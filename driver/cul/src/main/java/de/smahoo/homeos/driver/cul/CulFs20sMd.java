package de.smahoo.homeos.driver.cul;

import java.util.List;


import de.smahoo.homeos.common.EventType;
import de.smahoo.homeos.common.FunctionParameter;
import de.smahoo.homeos.device.DeviceEvent;
import de.smahoo.homeos.device.DeviceProperty;
import de.smahoo.homeos.device.PhysicalDeviceFunction;
import de.smahoo.homeos.devices.SensorMotion;
import de.smahoo.homeos.property.Property;
import de.smahoo.homeos.property.PropertyType;

import de.runge.cul.Device;
import de.runge.cul.Fs20s_md;

public class CulFs20sMd extends CulDevice implements SensorMotion{

	
	
	public CulFs20sMd(Device device){
		super(device);
	}
	
	public boolean isMotion(){
		DeviceProperty p = getProperty("motion");
		if (p.isValueSet()) {
			return (Boolean)p.getValue();
		}
		return false;
	}
	
	@Override
	protected void generateProperties(){
		DeviceProperty property = new DeviceProperty(PropertyType.PT_BOOLEAN,"motion","");
		property.setValue(false);
		addProperty(property);					
	}
		
	
	@Override
	protected void onPropertyChanged(DeviceProperty property){
		// none
	}
	
	
	@Override
	protected void generateDeviceFunctions(){
			
	    	// none
	}

	@Override		
	protected void execute(PhysicalDeviceFunction function, List<FunctionParameter> params){
			// none
	}
	
	@Override
	protected void applyProperties(){
		if (culDevice == null) return;		
		Fs20s_md culDev = (Fs20s_md)culDevice;
		DeviceProperty p = getProperty("motion");
		boolean isM = (culDev.getState(0)!=0);
		
		if (p.isValueSet()){
			Boolean pVal = (Boolean)p.getValue();
			if (pVal != isM){
				p.setValue(isM);				
			}
		} else {
			p.setValue(isM);
		}
		
		
			
	}
	
	
	@Override
	public boolean isOn(){
		return isMotion();
	}
	
}
