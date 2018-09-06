package de.smahoo.homeos.driver.zwave.generic;

import java.util.List;

import de.smahoo.homeos.common.FunctionExecutionException;
import de.smahoo.homeos.common.FunctionParameter;
import de.smahoo.homeos.device.DeviceProperty;
import de.smahoo.homeos.device.PhysicalDeviceFunction;
import de.smahoo.homeos.devices.SensorBinary;
import de.smahoo.homeos.driver.zwave.ZWaveDevice;
import de.smahoo.homeos.property.PropertyType;
import de.smahoo.jwave.cmd.JWaveCommandParameterType;
import de.smahoo.jwave.cmd.JWaveNodeCommand;
import de.smahoo.jwave.cmd.JWaveNodeCommandFactory;
import de.smahoo.jwave.cmd.report.JWaveReportBasic;
import de.smahoo.jwave.cmd.report.JWaveReportFactory;
import de.smahoo.jwave.cmd.report.JWaveReportSensorBinary;
import de.smahoo.jwave.node.JWaveNode;

public class ZWaveGenericSensorBinary extends ZWaveDevice implements SensorBinary{

	protected boolean binarySet = false;
	
	public ZWaveGenericSensorBinary(String id, JWaveNode node){
		super(id,node);
	}
	
	@Override
	public boolean isBinarySet(){
		return binarySet;
	}
	
	@Override
	protected void initDevice(){
		if (getNode().supportsCommandClass(0x85)){
			associateNode(1,1);
		}		
		requestSensorBinary();
	}
	
	protected void requestSensorBinary(){
		
		getNode().sendData(cmdFactory.generateCmd_SensorBinary_Get());
	}
	
	
	protected void evaluateReport(JWaveReportSensorBinary report){
		updateValue(report.getValue());
	}
	
	protected void evaluateReport(JWaveReportBasic report){
		updateValue(report.getValue()!=0);
	}
	
	
	protected void updateValue(boolean value){
		DeviceProperty property = this.getProperty("on");
		if (property.isValueSet()){
			Boolean pVal = (Boolean)property.getValue();
			if (pVal != value){
				property.setValue(value);				
			}
		} else {
			property.setValue(value);
		}
	}	
	
	
	protected void evaluateReceivedNodeCmd(JWaveNodeCommand cmd){		
		try {
			switch (cmd.getCommandClassKey()){
				case 0x30: // COMMAND_CLASS_SENSOR_BINARY
						if (cmd.getCommandKey() == 0x03){
							evaluateReport(JWaveReportFactory.generateSensorBinaryReport(cmd));
						}
					break;					
				case 0x20: // COMMAND_CLASS_BASIC
						if (cmd.getCommandKey() == 0x03){ // REPORT
							evaluateReport(JWaveReportFactory.generateBasicReport(cmd));
						}
						if (cmd.getCommandKey() == 0x01){ // SET
							updateValue(JWaveCommandParameterType.toInteger(cmd.getParamValue(0))!=0);
						}
					break;
				default:
					break;
			}
		} catch (Exception exc){
			exc.printStackTrace();
			// FIXME
		}
		
	}
	
	protected void onPropertyChanged(final DeviceProperty property){
		// 
	}
	
	protected void generateDeviceFunctions(){
		//
	}
	
	protected void generateProperties(){
		addProperty(new DeviceProperty(PropertyType.PT_BOOLEAN, "on", ""));		
	}
	
	protected void executeDeviceFunction(final PhysicalDeviceFunction function, final List<FunctionParameter> params) throws FunctionExecutionException{
		//		
	}
	
}
