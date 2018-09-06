package de.smahoo.homeos.driver.zwave.generic;

import java.util.List;

import de.smahoo.homeos.common.EventType;
import de.smahoo.homeos.common.FunctionExecutionException;
import de.smahoo.homeos.common.FunctionParameter;
import de.smahoo.homeos.device.DeviceEvent;
import de.smahoo.homeos.device.DeviceProperty;
import de.smahoo.homeos.device.PhysicalDeviceFunction;
import de.smahoo.homeos.device.SimpleDeviceFunction;
import de.smahoo.homeos.devices.Switchable;
import de.smahoo.homeos.driver.zwave.ZWaveDevice;
import de.smahoo.homeos.property.PropertyType;
import de.smahoo.jwave.cmd.JWaveCommand;
import de.smahoo.jwave.cmd.JWaveCommandClass;
import de.smahoo.jwave.cmd.JWaveCommandClassSpecification;
import de.smahoo.jwave.cmd.JWaveCommandParameterType;
import de.smahoo.jwave.cmd.JWaveNodeCommand;
import de.smahoo.jwave.cmd.report.JWaveReport;
import de.smahoo.jwave.cmd.report.JWaveReportBasic;
import de.smahoo.jwave.cmd.report.JWaveReportFactory;
import de.smahoo.jwave.cmd.report.JWaveReportSwitchBinary;
import de.smahoo.jwave.node.JWaveNode;

public class ZWaveGenericSwitchBinary extends ZWaveDevice implements Switchable{

	
	
	
	
	public ZWaveGenericSwitchBinary(String id, JWaveNode node){
		super(id, node);
	}
	
	public void turnOn(){
		JWaveCommandClassSpecification defs = getZWaveDefinitions();
		JWaveCommandClass cmdClass = defs.getCommandClass(0x25); // COMMAND_CLASS_SWITCH_BINARY
		if (cmdClass == null){
			// FIXME: communicate that (e.g. Exception, Error-Event)
			return;
		}
		JWaveCommand cmd = cmdClass.getCommand(0x01); // SWITCH_BINARY_SET
		
		
		JWaveNodeCommand nodeCmd = new JWaveNodeCommand(cmd);
		try {
			nodeCmd.setParamValue(0, 0xFF);
		} catch (Exception exc){
			exc.printStackTrace();
		}
		this.getNode().sendData(nodeCmd);
		updateOnValue(true);
	}
	
	@Override
	public boolean isOn(){
		DeviceProperty property = this.getProperty("toggle");
		if (property.isValueSet()){
			return (Boolean)property.getValue();
		}
		return false;
	}
	
	public void turnOff(){
		JWaveCommandClassSpecification defs = getZWaveDefinitions();
		JWaveCommandClass cmdClass = defs.getCommandClass(0x25); // COMMAND_CLASS_SWITCH_BINARY
		if (cmdClass == null){
			// FIXME: communicate that (e.g. Exception, Error-Event)
			return;
		}
		JWaveCommand cmd = cmdClass.getCommand(0x01); // SWITCH_BINARY_SET		
		
		JWaveNodeCommand nodeCmd = new JWaveNodeCommand(cmd);
		try {
			nodeCmd.setParamValue(0, 0x00);
		} catch (Exception exc){
			exc.printStackTrace();
		}
		this.getNode().sendData(nodeCmd);
		updateOnValue(false);
	}
	
	protected void initDevice(){
		if (getNode().supportsCommandClass(0x85)){
			associateNode(01, 01);
		}
		requestOnState();
	}
	
	protected void requestOnState(){
		
		getNode().sendData(cmdFactory.generateCmd_SwitchBinary_Get());
	}
	
	protected void updateOnValue(boolean value){
		DeviceProperty property = this.getProperty("toggle");
		if (property.isValueSet()){
			Boolean pVal = (Boolean)property.getValue();
			if (pVal != value){
				property.setValue(value);				
			}
		} else {
			property.setValue(value);
		}
		if (value == true){
			dispatchDeviceEvent(new DeviceEvent(EventType.DEVICE_ON, this));
		} else {
			dispatchDeviceEvent(new DeviceEvent(EventType.DEVICE_OFF, this));
		}
	}
	
	protected void evalReport(JWaveReport report){
		if (report instanceof JWaveReportBasic){
			updateOnValue(((JWaveReportBasic)report).getValue() != 0);
		}
		if (report instanceof JWaveReportSwitchBinary){
			updateOnValue(((JWaveReportSwitchBinary)report).getValue());
		}
	}
	
	protected void evaluateReceivedNodeCmd(JWaveNodeCommand cmd){
		try {
			switch (cmd.getCommandClassKey()){
				case 0x25: // COMMAND_CLASS_SWITCH_BINARY
					if (cmd.getCommandKey() == 0x03){ // REPORT
						evalReport(JWaveReportFactory.generateSwitchBinaryReport(cmd));
					}					
					break;					
				case 0x20: // COMMAND_CLASS_BASIC
					if (cmd.getCommandKey() == 0x01){ // SET
						updateOnValue(JWaveCommandParameterType.toInteger(cmd.getParamValue(0))!=0);
					}
				default:
					break;
			}
		} catch (Exception exc){
			exc.printStackTrace();
			// FIXME
		}
	}

	protected void onPropertyChanged(final DeviceProperty property){
		
	}
	
	protected void generateDeviceFunctions(){
		PhysicalDeviceFunction function;
    	function = new SimpleDeviceFunction(PhysicalDeviceFunction.TURN_ON);
    	this.addDeviceFunction(function);
    	
    	function = new SimpleDeviceFunction(PhysicalDeviceFunction.TURN_OFF);
    	this.addDeviceFunction(function);   
	}
	
	protected void generateProperties(){
		addProperty(new DeviceProperty(PropertyType.PT_BOOLEAN, "toggle", ""));		
	}
	
	protected void executeDeviceFunction(final PhysicalDeviceFunction function, final List<FunctionParameter> params) throws FunctionExecutionException{

		switch (function.getFunctionType()){		 
		  case PhysicalDeviceFunction.TURN_ON : turnOn(); break;
		  case PhysicalDeviceFunction.TURN_OFF : turnOff(); break;
		}
	}
	
}
