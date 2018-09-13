package de.smahoo.homeos.driver.cul;

import java.util.List;

import de.smahoo.homeos.common.FunctionParameter;
import de.smahoo.homeos.device.DeviceProperty;
import de.smahoo.homeos.device.PhysicalDeviceFunction;
import de.smahoo.homeos.devices.SensorTemperature;
import de.smahoo.homeos.property.PropertyType;

import de.smahoo.cul.Device;
import de.smahoo.cul.Hms100tf;

public class CulHms100t extends CulDevice implements SensorTemperature{
	
	
		
	public CulHms100t(Device device){
		super(device);		
	}
	
	
	
    protected void generateDeviceFunctions(){
    	// no functions   	   	
	}

	
	protected void execute(PhysicalDeviceFunction function, List<FunctionParameter> params){
		//
	}
		
	protected void generateProperties(){
		addProperty(new DeviceProperty(PropertyType.PT_DOUBLE, "temperature","°C"));	
		addProperty(new DeviceProperty(PropertyType.PT_BOOLEAN,"batteryOk",""));		
	}
	
	@Override
	protected void onPropertyChanged(DeviceProperty property){
		//
	}
	
	protected void setCulProperty(DeviceProperty property){		
	//
	}
	
	@Override
	protected void applyProperties(){
		if (culDevice == null) return;
		Hms100tf hms = (Hms100tf)culDevice;
		//SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		//String dateStr = formatter.format(new Date());
		//System.out.println("["+dateStr+"] HMS UPDATE "+this.getDeviceId()+" "+hms.getTemperature()+"°C/"+hms.getHumidity()+"%");
		
		if (hms.isValuesSet()){
			DeviceProperty p = getProperty("temperature");
			if (p.isValueSet()) {		
				if ((Double)p.getValue() != hms.getTemperature()){
					p.setValue(hms.getTemperature());
				}
			} else p.setValue(hms.getTemperature());
			
		
			
			
			p = getProperty("batteryOk");
			if (p.isValueSet()) {
				if ((Boolean)p.getValue() != hms.isBatteryOk()){
					p.setValue(hms.isBatteryOk());
				}
			} else p.setValue(hms.isBatteryOk());
		}
	}
	
	public double getTemperature(){
		DeviceProperty p = getProperty("temperature");
		if (p.getValue() != null){
			return (Double)p.getValue();
		}
		return 0.0;
	}
	
	

	
	@Override
	public boolean isOn(){
		return isAvailable();
	}
}
