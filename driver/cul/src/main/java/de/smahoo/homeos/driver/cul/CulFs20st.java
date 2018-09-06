package de.smahoo.homeos.driver.cul;

import java.util.List;


import de.smahoo.homeos.common.EventType;
import de.smahoo.homeos.common.FunctionExecutionException;
import de.smahoo.homeos.common.FunctionParameter;
import de.smahoo.homeos.device.DeviceEvent;
import de.smahoo.homeos.device.DeviceProperty;
import de.smahoo.homeos.device.PhysicalDeviceFunction;
import de.smahoo.homeos.device.SimpleDeviceFunction;
import de.smahoo.homeos.devices.Socket;
import de.smahoo.homeos.property.Property;
import de.smahoo.homeos.property.PropertyType;

import de.runge.cul.Device;
import de.runge.cul.FS20ST;

public class CulFs20st extends CulDevice implements Socket {
	
	protected boolean isOnState = false;
		
	public CulFs20st(Device device){
		super(device);		
	}
	
    protected void generateDeviceFunctions(){
	
    	SimpleDeviceFunction function;
    	
    	function = new SimpleDeviceFunction(PhysicalDeviceFunction.TURN_ON);
    	this.addDeviceFunction(function);
    	
    	function = new SimpleDeviceFunction(PhysicalDeviceFunction.TURN_OFF);
    	this.addDeviceFunction(function);
    	
    	function = new SimpleDeviceFunction(PhysicalDeviceFunction.SWITCH);
    	this.addDeviceFunction(function);
    	
	}

	
    @Override
	protected void execute(PhysicalDeviceFunction function, List<FunctionParameter> params){		
    	
		switch (function.getFunctionType()){
		  case PhysicalDeviceFunction.TURN_ON  : turnOn(); break;
		  case PhysicalDeviceFunction.TURN_OFF : turnOff(); break;
		  case PhysicalDeviceFunction.SWITCH   : switchState(); break;
		}
	}
	
	protected FS20ST getFs20stDevice(){
		return (FS20ST)culDevice;
	}
	
	protected void generateProperties(){
		DeviceProperty p = new DeviceProperty(PropertyType.PT_INTEGER,"channel","");		
		addProperty(p);		
		
	}
	
	@Override
	protected void onPropertyChanged(DeviceProperty property){
		
		setCulProperty(property);
				
	}
	
	protected void setCulProperty(DeviceProperty property){		
		String propertyName = property.getName();
		if (propertyName == null) return;
		
		if (propertyName.equals("channel")){			
				int channel = (Integer)property.getValue();
				getFs20stDevice().setChannel(channel);			
		}
	}
	
	@Override
	protected void applyProperties(){
		if (culDevice == null) return;
		FS20ST fs20st = (FS20ST)culDevice;
		DeviceProperty p = getProperty("channel");
		if (p == null) return;
		if (p.isValueSet()){
			int c = (Integer)p.getValue();
			if (c != fs20st.getChannel()){
				p.setValue(fs20st.getChannel());
			}
		} else p.setValue(fs20st.getChannel());
    	
	}
			
	public void turnOn(){
		FS20ST fs20st = getFs20stDevice();
		fs20st.turnOn();		
		this.isOnState = true;
		this.dispatchDeviceEvent(new DeviceEvent(EventType.DEVICE_ON, this));		
		
	}
	
	public void turnOff(){
		FS20ST fs20st = getFs20stDevice();
		fs20st.turnOff();		
		this.isOnState = false;
		this.dispatchDeviceEvent(new DeviceEvent(EventType.DEVICE_OFF, this));		
		
	}
	
	protected void setTurnOnState(boolean on){
		if (isOnState == on) return;
		isOnState = on;
		if (on){
			this.dispatchDeviceEvent(new DeviceEvent(EventType.DEVICE_ON, this));
		} else {
			this.dispatchDeviceEvent(new DeviceEvent(EventType.DEVICE_OFF, this));
		}
	}
	
	protected void switchState(){
		if (isOn()){
			turnOff();
		} else {
			turnOn();
		}
	}
	
	@Override
	public boolean isOn(){
		return isOnState;
	}
}
