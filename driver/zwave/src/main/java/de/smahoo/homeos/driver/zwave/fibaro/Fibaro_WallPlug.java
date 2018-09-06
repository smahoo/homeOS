package de.smahoo.homeos.driver.zwave.fibaro;

import java.util.ArrayList;
import java.util.List;

import de.smahoo.homeos.common.FunctionExecutionException;
import de.smahoo.homeos.common.FunctionParameter;
import de.smahoo.homeos.device.DeviceProperty;
import de.smahoo.homeos.device.ParameterizedDeviceFunction;
import de.smahoo.homeos.device.PhysicalDeviceFunction;
import de.smahoo.homeos.devices.MeterElectricity;
import de.smahoo.homeos.devices.Socket;
import de.smahoo.homeos.driver.zwave.generic.ZWaveGenericSwitchBinary;
import de.smahoo.homeos.property.PropertyType;
import de.smahoo.jwave.cmd.JWaveCommandParameterType;
import de.smahoo.jwave.cmd.JWaveNodeCommand;
import de.smahoo.jwave.cmd.report.JWaveReport;
import de.smahoo.jwave.cmd.report.JWaveReportConfiguration;
import de.smahoo.jwave.cmd.report.JWaveReportFactory;
import de.smahoo.jwave.cmd.report.JWaveReportSensorMultilevel;
import de.smahoo.jwave.node.JWaveNode;

public class Fibaro_WallPlug extends ZWaveGenericSwitchBinary implements Socket, MeterElectricity{

	
	
	
	public Fibaro_WallPlug(String id, JWaveNode node){
		super(id,node);
	}
	
	@Override
	public void initDevice(){
		super.initDevice();
		
		associateNode(3, 1);				
		requestSensorData();
		associateNode(1, 1);
		associateNode(2, 1);
	}
	
	@Override
	protected void evaluateReceivedNodeCmd(JWaveNodeCommand cmd){
		super.evaluateReceivedNodeCmd(cmd);
		try {
			switch (cmd.getCommandClassKey()){
				case 0x31: // COMMAND_CLASS_SENSOR_MULTILEVEL
					if (cmd.getCommandKey() == 0x05){ // REPORT
						evalReport(JWaveReportFactory.generateSensorMultilevelReport(cmd));
					}					
					break;	
				case 0x70: // COMMAND_CLASS_CONFIGURATION
					if (cmd.getCommandKey() == 0x06){ // REPORT
						evalReport(JWaveReportFactory.generateConfigurationReport(cmd));
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
		
	@Override
	protected void evalReport(JWaveReport report){
		super.evalReport(report);
		if (report instanceof JWaveReportSensorMultilevel){
			JWaveReportSensorMultilevel rep = (JWaveReportSensorMultilevel)report;
			double value = rep.getValue();
			if (rep.getPrecission() != 0){
				value = value / (double)rep.getPrecission();
			}
			updatePowerValue(value);
		}
		if (report instanceof JWaveReportConfiguration){
			JWaveReportConfiguration conf = (JWaveReportConfiguration)report;
			if (conf.getParamId() == 61){
				updateColorWhenOn(conf.getValue());	
			}
			if (conf.getParamId() == 62){
				updateColorWhenOff(conf.getValue());			
			}
		}
	}
	
	@Override
	protected void generateDeviceFunctions(){
		super.generateDeviceFunctions();
		  
		List<FunctionParameter> l = new ArrayList<FunctionParameter>();
    	FunctionParameter fm = new FunctionParameter(PropertyType.PT_INTEGER,"color");    	
    	l.add(fm);
    	
    	PhysicalDeviceFunction function;
    	function = new ParameterizedDeviceFunction(PhysicalDeviceFunction.SET_COLOR,"setColorWhenOn()", l);
    	addDeviceFunction(function);
    	
    	function = new ParameterizedDeviceFunction(PhysicalDeviceFunction.SET_COLOR,"setColorWhenOff()", l);
    	addDeviceFunction(function);
	}
	
	public void setColorWhenOn(int color){
		
		getNode().sendData(cmdFactory.generateCmd_Configuration_Set((byte)(61),JWaveCommandParameterType.BYTE,color));
		getNode().sendData(cmdFactory.generateCmd_Configuration_Get((byte)(61)));
	}
	
	public void setColorWhenOff(int color){
		
		getNode().sendData(cmdFactory.generateCmd_Configuration_Set((byte)(62),JWaveCommandParameterType.BYTE,color));
		getNode().sendData(cmdFactory.generateCmd_Configuration_Get((byte)(62)));
	}
	
	@Override
	protected void generateProperties(){
		super.generateProperties();
		addProperty(new DeviceProperty(PropertyType.PT_INTEGER, "color_on", ""));		
		addProperty(new DeviceProperty(PropertyType.PT_INTEGER, "color_off", ""));
		addProperty(new DeviceProperty(PropertyType.PT_DOUBLE, "power", "Watt"));
	}
	
	@Override
	protected void executeDeviceFunction(final PhysicalDeviceFunction function, final List<FunctionParameter> params) throws FunctionExecutionException{
		super.executeDeviceFunction(function, params);
		switch (function.getFunctionType()){		 
		case ParameterizedDeviceFunction.SET_COLOR:
			int color = 0;
			 if (params != null){
			      if (params.size() == 1){
				      FunctionParameter fm = params.get(0);
				      color = (Integer)fm.getValue();
			      }
			 }
			if (function.getName().equalsIgnoreCase("setColorWhenOff()")){
				setColorWhenOff(color);
			}
			if (function.getName().equalsIgnoreCase("setColorWhenOn()")){
				setColorWhenOn(color);
			}			
			
			break;
		}
	}
	
	protected void updateColorWhenOn(int color){
		DeviceProperty property = this.getProperty("color_on");
		if (property.isValueSet()){
			Integer value = (Integer)property.getValue();
			if (value != color){
				property.setValue(color);
			}
		} else {
			property.setValue(color);
		}
	}
		
	protected void updateColorWhenOff(int color){
		DeviceProperty property = this.getProperty("color_off");
		if (property.isValueSet()){
			Integer value = (Integer)property.getValue();
			if (value != color){
				property.setValue(color);
			}
		} else {
			property.setValue(color);
		}
	}
		
	
	protected void updatePowerValue(double value){
		DeviceProperty property = this.getProperty("power");
		if (property.isValueSet()){
			 Double pVal = (Double)property.getValue();
			if (pVal != value){
				property.setValue(value);				
			}
		} else {
			property.setValue(value);
		}
	}
	
	protected void requestSensorData(){
		
		getNode().sendData(cmdFactory.generateCmd_SensorMultilevel_Get());		
	}

	@Override
	public double getTotalConsumption() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getCurrentConsumption() {
		DeviceProperty property = this.getProperty("power");
		if (property.isValueSet()){
			 return (Double)property.getValue();
			
		} 
		return 0;
	}
}
