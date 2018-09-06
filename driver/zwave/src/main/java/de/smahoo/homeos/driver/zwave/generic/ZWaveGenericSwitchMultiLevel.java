package de.smahoo.homeos.driver.zwave.generic;

import java.util.ArrayList;
import java.util.List;

import de.smahoo.homeos.common.EventType;
import de.smahoo.homeos.common.FunctionExecutionException;
import de.smahoo.homeos.common.FunctionParameter;
import de.smahoo.homeos.device.DeviceEvent;
import de.smahoo.homeos.device.DeviceProperty;
import de.smahoo.homeos.device.ParameterizedDeviceFunction;
import de.smahoo.homeos.device.PhysicalDeviceFunction;
import de.smahoo.homeos.device.SimpleDeviceFunction;
import de.smahoo.homeos.devices.Dimmable;
import de.smahoo.homeos.devices.Socket;
import de.smahoo.homeos.driver.zwave.ZWaveDevice;
import de.smahoo.homeos.property.PropertyType;
import de.smahoo.jwave.cmd.JWaveCommand;
import de.smahoo.jwave.cmd.JWaveCommandClass;
import de.smahoo.jwave.cmd.JWaveCommandClassSpecification;
import de.smahoo.jwave.cmd.JWaveNodeCommand;
import de.smahoo.jwave.node.JWaveNode;

public class ZWaveGenericSwitchMultiLevel extends ZWaveGenericSwitchBinary implements Dimmable {

	
	
	
	public ZWaveGenericSwitchMultiLevel(String id, JWaveNode node){
		super(id, node);
	}
	
	public void dimmUp(){
		//
	}
	
	public void dimmDown(){
		//
	}
	
	
	public void setDimmLevel(int dimmLevel){
		byte value = (byte)(0xFF * dimmLevel/100);
		getNode().sendData(cmdFactory.generateCmd_SwitchMultilevel_Set(value));
		if (dimmLevel > 0){
			updateOnValue(true);
		} else {
			updateOnValue(false);
		}
	}
	
	@Override
	public void turnOn(){
		getNode().sendData(cmdFactory.generateCmd_SwitchMultilevel_Set(0xFF));
		updateOnValue(true);
	}
	
	@Override	
	public void turnOff(){
		getNode().sendData(cmdFactory.generateCmd_SwitchMultilevel_Set(0));	
		updateOnValue(false);		
	}
	
	protected void initDevice(){
		
	}
	
	protected void evaluateReceivedNodeCmd(JWaveNodeCommand cmd){
		
	}
	
	protected void onPropertyChanged(final DeviceProperty property){
		
	}
	
	protected void generateDeviceFunctions(){
		PhysicalDeviceFunction function;
    	function = new SimpleDeviceFunction(PhysicalDeviceFunction.TURN_ON);
    	this.addDeviceFunction(function);
    	
    	function = new SimpleDeviceFunction(PhysicalDeviceFunction.TURN_OFF);
    	this.addDeviceFunction(function);   
    	
    	FunctionParameter fm = new FunctionParameter(PropertyType.PT_INTEGER,"dimmLevel");
    	List<FunctionParameter> l = new ArrayList<FunctionParameter>();
    	l.add(fm);
    	function = new ParameterizedDeviceFunction(PhysicalDeviceFunction.DIMM,l);
    	this.addDeviceFunction(function);
	}
	
	protected void generateProperties(){
		super.generateProperties();
		addProperty(new DeviceProperty(PropertyType.PT_INTEGER, "dimmLevel", ""));
	}
	
	protected void executeDeviceFunction(final PhysicalDeviceFunction function, final List<FunctionParameter> params) throws FunctionExecutionException{
		switch (function.getFunctionType()){		 
		  case PhysicalDeviceFunction.TURN_ON : turnOn(); break;
		  case PhysicalDeviceFunction.TURN_OFF : turnOff(); break;
		  case PhysicalDeviceFunction.DIMM :
			  if (params != null){
			      if (params.size() == 1){
				      FunctionParameter fm = params.get(0);
				     Integer value = (Integer)fm.getValue();
				      setDimmLevel(value);
			      }
			  }
			  break;
		}
	}
	
	
}
