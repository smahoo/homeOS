package de.smahoo.homeos.simulation.devices;

import java.util.List;


import de.smahoo.homeos.common.FunctionExecutionException;
import de.smahoo.homeos.common.FunctionParameter;
import de.smahoo.homeos.device.DeviceProperty;
import de.smahoo.homeos.device.PhysicalDeviceFunction;
import de.smahoo.homeos.devices.SensorCO2;
import de.smahoo.homeos.devices.SensorClimate;
import de.smahoo.homeos.property.Property;
import de.smahoo.homeos.property.PropertyType;

public class SimSensorClimate extends SimDevice implements SensorClimate, SensorCO2{

	protected double currentHumidity;
	protected double currentTemperature;
	protected double currentC02;
	
	public SimSensorClimate(String id){
		super(id);
	}
	
	protected void onPropertyChanged(final DeviceProperty property){
		// 
	}
	
	protected void generateDeviceFunctions(){
		// it's a sensor - functions are note supported
	}
	
	protected void generateProperties(){
		addProperty(new DeviceProperty(PropertyType.PT_DOUBLE,"temperature","°C"));
		addProperty(new DeviceProperty(PropertyType.PT_DOUBLE,"humidity","%"));
		addProperty(new DeviceProperty(PropertyType.PT_DOUBLE, "co2", "ppm"));
	}
	
	protected void execute(final PhysicalDeviceFunction function, final List<FunctionParameter> params) throws FunctionExecutionException{
		// it's a sensor - functions are note supported
	}
	
	public double getTemperature(){
		return currentTemperature;
	}
	
	public double getHumidity(){
		return currentHumidity;
	}

	public double getC02Value(){
		return currentC02;
	}
	
	public void setTemperature(double temperature){
		currentTemperature = temperature;
		DeviceProperty deviceProperty = getProperty("temperature");
		deviceProperty.setValue(temperature);
		this.dispatchChangeEventsIfNeeded();
	}
	
	public void setHumidity(double humidity){
		currentHumidity = humidity;
		DeviceProperty deviceProperty = getProperty("humidity");
		deviceProperty.setValue(humidity);
		this.dispatchChangeEventsIfNeeded();
	}

	public void setC02(double co2){
		currentC02 = co2;
		DeviceProperty devProp = getProperty("co2");
		devProp.setValue(currentC02);
		this.dispatchChangeEventsIfNeeded();
	}
}
