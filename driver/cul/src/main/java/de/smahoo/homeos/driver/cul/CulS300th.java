package de.smahoo.homeos.driver.cul;

import java.util.List;


import de.smahoo.homeos.common.FunctionParameter;
import de.smahoo.homeos.device.DeviceProperty;
import de.smahoo.homeos.device.PhysicalDeviceFunction;
import de.smahoo.homeos.devices.SensorClimate;
import de.smahoo.homeos.property.Property;
import de.smahoo.homeos.property.PropertyType;

import de.runge.cul.Device;
import de.runge.cul.S300th;

public class CulS300th extends CulDevice implements SensorClimate{
	
	public CulS300th(Device device){	
		super(device);		
	}
	
	
	
	@Override
    protected void generateDeviceFunctions(){
	    return;
	}

    @Override
	protected void execute(PhysicalDeviceFunction function, List<FunctionParameter> params){
		return;
	}
	
	@Override
	protected void generateProperties(){
		addProperty(new DeviceProperty(PropertyType.PT_DOUBLE, "temperature","°C"));
		addProperty(new DeviceProperty(PropertyType.PT_DOUBLE, "humidity","%"));	
	}
	
	@Override
	protected void onPropertyChanged(DeviceProperty property){
	
	}
	
	@Override
	protected void applyProperties(){
		if (culDevice == null) return;
		S300th s = (S300th)culDevice;
		DeviceProperty p = getProperty("temperature");
		double d;
		if (p != null){
			if (p.isValueSet()){
			  d = (Double)p.getValue();
			  if (d != s.getTemperature()){				  
			    	p.setValue(s.getTemperature());
			  }
			} else p.setValue(s.getTemperature());
		}
    	p = getProperty("humidity");
    	if (p != null){
    		if (p.isValueSet()){
    			d = (Double)p.getValue();
    			if (d != s.getHumidity()){
    				p.setValue(s.getHumidity());
    			}
    		} else p.setValue(s.getHumidity());
    	}
	}
	
	protected void bindDefaultDeviceRoles(){
		//DeviceRoleFactory drf = new DeviceRoleFactory();
		//List<PropertyBinding> list = new ArrayList<>();
		//list.add(new PropertyBinding("temperature", this.getProperty("temperature")));		
		//drf.createDeviceRole(DeviceRoleType.SENSOR_HUMIDITY, this, null, list);
	}
	
	public double getTemperature(){
		DeviceProperty p = getProperty("temperature");
		if (p.getValue() != null){
			return (Double)p.getValue();
		}
		return 0.0;
	}
	
	public double getHumidity(){
		DeviceProperty p = getProperty("humidity");
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
