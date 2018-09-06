package de.smahoo.homeos.driver.cul;

import java.util.List;


import de.smahoo.homeos.common.FunctionParameter;
import de.smahoo.homeos.device.DeviceProperty;
import de.smahoo.homeos.device.PhysicalDeviceFunction;
import de.smahoo.homeos.devices.SensorWindow;
import de.smahoo.homeos.property.Property;
import de.smahoo.homeos.property.PropertyType;

import de.runge.cul.Device;
import de.runge.cul.Fhttk;

public class CulFhttk extends CulDevice implements SensorWindow{
	
	
		
	public CulFhttk(Device device){
		super(device);		
	}
	
    protected void generateDeviceFunctions(){
	
    	// no functions
    	
	}

	
	protected void execute(PhysicalDeviceFunction function, List<FunctionParameter> params){
		// no functions
	}
	
	protected void generateProperties(){
		DeviceProperty p = new DeviceProperty(PropertyType.PT_BOOLEAN,"open","");		
		addProperty(p);		
		
	}
	
	@Override
	protected void onPropertyChanged(DeviceProperty property){	
		
	}
	
	protected void setCulProperty(DeviceProperty property){		
		String propertyName = property.getName();
		if (propertyName == null) return;
		
		if (propertyName.equals("open")){			
			//	boolean open = (boolean)property.getValue();
			//	Fhttk fhttk = (Fhttk)culDevice;
			//	//FIX: not able to set property			
		}
	}
	
	@Override
	protected void applyProperties(){
		if (culDevice == null) return;
		Fhttk fhttk =  (Fhttk)culDevice;
		DeviceProperty p = getProperty("open");
		if (p == null) return;
		if (p.isValueSet()){
			boolean b = (Boolean)p.getValue();
			if (b != fhttk.isOpen()){
				p.setValue(fhttk.isOpen());
			}
		} else p.setValue(fhttk.isOpen());
    	
	}
	
	public boolean isOpen(){
		DeviceProperty p = getProperty("open");
		if (p == null) return false;
		if (p.isValueSet()){
			return (Boolean)p.getValue();			
		} 
		return false;
	}
	
	@Override
	public boolean isOn(){
		return isAvailable();
	}
}
