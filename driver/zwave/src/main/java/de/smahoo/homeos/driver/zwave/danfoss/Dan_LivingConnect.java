package de.smahoo.homeos.driver.zwave.danfoss;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.smahoo.homeos.common.FunctionExecutionException;
import de.smahoo.homeos.common.FunctionParameter;
import de.smahoo.homeos.device.DeviceProperty;
import de.smahoo.homeos.device.ParameterizedDeviceFunction;
import de.smahoo.homeos.device.PhysicalDeviceFunction;
import de.smahoo.homeos.devices.HeatingRtc;
import de.smahoo.homeos.driver.zwave.ZWaveDevice;
import de.smahoo.homeos.driver.zwave.ZWaveSleepListener;
import de.smahoo.homeos.property.PropertyType;
import de.smahoo.jwave.cmd.JWaveNodeCommand;
import de.smahoo.jwave.cmd.JWaveNodeCommandFactory;
import de.smahoo.jwave.cmd.report.JWaveReportFactory;
import de.smahoo.jwave.cmd.report.JWaveReportThermostatSetpoint;
import de.smahoo.jwave.node.JWaveNode;

public class Dan_LivingConnect extends ZWaveDevice implements HeatingRtc {

	protected double desiredTemperature;
	
	public Dan_LivingConnect(String id, JWaveNode node){
		super(id,node);	
		super.addSleepListener(new ZWaveSleepListener() {
			
			@Override
			public void onWakeUpIntervalSet(long seconds) {
				//				
			}
			
			@Override
			public void onWakeUp() {				
			//	JWaveNodeCommandFactory cmdFactory = getNodeCmdFactory();
			//	getNode().sendData(cmdFactory.generateCmd_Thermostat_Setpoint_Get(1));
			}
			
			@Override
			public void onSleep() {				
				//
			}
		});
	}
	
	public void setTemperature(double temperature){
		boolean change = false;
		DeviceProperty property = this.getProperty("temperature");
		if (property.isValueSet()){
			Double pVal = (Double)property.getValue();
			if (pVal != temperature){
				change = true;				
			}
		} else {
			change = true;
		}
		if (change){			
			getNode().sendData(cmdFactory.generateCmd_Thermostat_Setpoint_Set(1,temperature));
			getNode().sendData(cmdFactory.generateCmd_Thermostat_Setpoint_Get(1));
		}
	}
	
	protected void updateDesiredTemperature(double temperature){
		DeviceProperty property = this.getProperty("temperature");
		if (property.isValueSet()){
			Double pVal = (Double)property.getValue();
			if (pVal != temperature){
				property.setValue(temperature);				
			}
		} else {
			property.setValue(temperature);
		}
	}
	
	public double getDesiredTemperature(){
		DeviceProperty property = this.getProperty("temperature");
		if (property.isValueSet()){
			return (Double)property.getValue();
		}
		return 0;
	}
	
	protected void evaluateReceivedNodeCmd(JWaveNodeCommand cmd){
		try {
			switch (cmd.getCommandClassKey()){
				case 0x43: // COMMAND_CLASS_THERMOSTAT_SETPOINT
					if (cmd.getCommandKey() == 0x03){ // REPORT
						evaluateSetpointReport(JWaveReportFactory.generateThermostatSetpointReport(cmd));
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
	
	protected void evaluateSetpointReport(JWaveReportThermostatSetpoint report){
		try {
			double temperature;
			if (report.getPrecision() > 0){
				temperature = (double)report.getValue()/(double)report.getPrecision();

			} else {
				temperature = (double)report.getValue();
			}
			if (report.getScale() == 1){ // Temperature is given in Fahrenheit
				temperature = (temperature - 32.0) * 5.0/9.0; // transform temperature to celsius
			}
			// precision might have been lost during calculation
			temperature = (double)Math.round(temperature*report.getPrecision())/(double)report.getPrecision(); // better is, ne? 
			updateDesiredTemperature(temperature);
		} catch (Exception exc){
			exc.printStackTrace();
			// FIXME
		}
	}
	
	protected void initDevice(){		
		
		setWakeUpInterval(240);
		requestWakeUpInterval();
		requestBatteryState();
		getNode().sendData(cmdFactory.generateCmd_Clock_Set(new Date()));		
		getNode().sendData(cmdFactory.generateCmd_Thermostat_Setpoint_Get(1));		
	}
	
	
	
	
	protected void onPropertyChanged(final DeviceProperty property){
		//
	}
	
	protected void generateDeviceFunctions(){
		PhysicalDeviceFunction function;
		
		FunctionParameter fm = new FunctionParameter(PropertyType.PT_DOUBLE,"temperature");
    	List<FunctionParameter> l = new ArrayList<FunctionParameter>();
    	l.add(fm);
    	function = new ParameterizedDeviceFunction(PhysicalDeviceFunction.SET_TEMPERATURE,l);
	}
	
	protected void generateProperties(){
		addProperty(new DeviceProperty(PropertyType.PT_DOUBLE, "temperature", "°C"));
	}	
	
	protected void executeDeviceFunction(final PhysicalDeviceFunction function, final List<FunctionParameter> params) throws FunctionExecutionException{
		switch (function.getFunctionType()){		  	
		  case PhysicalDeviceFunction.SET_TEMPERATURE : 
			  if (params != null){
			      if (params.size() == 1){
				      FunctionParameter fm = params.get(0);
				      Double value = (Double)fm.getValue();
				      setTemperature(value);
			      }
			  }
		}
	}
	
	public double getValvePosition(){
		return 0;
	}
	
}
