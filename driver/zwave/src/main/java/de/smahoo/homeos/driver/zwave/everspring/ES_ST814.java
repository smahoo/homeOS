package de.smahoo.homeos.driver.zwave.everspring;

import java.util.List;

import de.smahoo.homeos.common.FunctionExecutionException;
import de.smahoo.homeos.common.FunctionParameter;
import de.smahoo.homeos.device.DeviceProperty;
import de.smahoo.homeos.device.PhysicalDeviceFunction;
import de.smahoo.homeos.devices.SensorClimate;
import de.smahoo.homeos.driver.zwave.ZWaveDevice;
import de.smahoo.homeos.driver.zwave.ZWaveSleepListener;
import de.smahoo.homeos.property.PropertyType;
import de.smahoo.jwave.cmd.JWaveNodeCommand;
import de.smahoo.jwave.cmd.report.JWaveReportFactory;
import de.smahoo.jwave.cmd.report.JWaveReportSensorMultilevel;
import de.smahoo.jwave.node.JWaveNode;

public class ES_ST814 extends ZWaveDevice implements SensorClimate{

	
	public ES_ST814(String id, JWaveNode node){
		super(id,node);		
		super.addSleepListener(new ZWaveSleepListener() {
			
			@Override
			public void onWakeUpIntervalSet(long seconds) {
				//				
			}
			
			@Override
			public void onWakeUp() {				
				requestValues();	
			}
			
			@Override
			public void onSleep() {				
				
			}
		});
	}
	
	protected void initDevice(){		
		requestValues();
		associateNode(01,this.getPrimaryControllerId());	
		//getNode().sendData(cmdFactory.generateCmd_Configuration_Set(6, JWaveCommandParameterType.WORD, 10));
		//getNode().sendData(cmdFactory.generateCmd_Configuration_Set(7, JWaveCommandParameterType.BYTE, 1));
		//getNode().sendData(cmdFactory.generateCmd_Configuration_Get(7));
		
		//getNode().sendData(cmdFactory.generateCmd_Configuration_Set(8, JWaveCommandParameterType.BYTE, 5));		
		//getNode().sendData(cmdFactory.generateCmd_Configuration_Get(8));
		
		
		/*associateNode(1,1);
		JWaveNodeCommandFactory cmdFactory = getNodeCmdFactory();
		getNode().sendData(cmdFactory.generateCmd_Configuration_Get(1));
		getNode().sendData(cmdFactory.generateCmd_Configuration_Get(2));
		getNode().sendData(cmdFactory.generateCmd_Configuration_Get(3));
		getNode().sendData(cmdFactory.generateCmd_Configuration_Get(4));
		getNode().sendData(cmdFactory.generateCmd_Configuration_Get(5));
		getNode().sendData(cmdFactory.generateCmd_Configuration_Get(6));
		getNode().sendData(cmdFactory.generateCmd_Configuration_Get(7));
		getNode().sendData(cmdFactory.generateCmd_Configuration_Set(7, JWaveCommandParameterType.BYTE, 1));
		getNode().sendData(cmdFactory.generateCmd_Configuration_Get(7));
		getNode().sendData(cmdFactory.generateCmd_Configuration_Get(8));
		getNode().sendData(cmdFactory.generateCmd_Configuration_Set(8, JWaveCommandParameterType.BYTE, 1));
		getNode().sendData(cmdFactory.generateCmd_Configuration_Get(8));*/
		
	
		
		//associateNode(1,1);		
	}
	
	protected void requestValues(){
		
		JWaveNodeCommand nodeCmd = cmdFactory.generateCmd_SensorMultilevel_Get(2);		
		getNode().sendData(nodeCmd);		
	}
	
	public double getTemperature(){
		DeviceProperty property = this.getProperty("temperature");
		if (property.isValueSet()){
			Double pVal = (Double)property.getValue();
			return pVal;
		} else {
			return 0.0;
		}
	}
	
	public double getHumidity(){
		DeviceProperty property = this.getProperty("humidity");
		if (property.isValueSet()){
			Double pVal = (Double)property.getValue();
			return pVal;
		} else {
			return 0.0;
		}
	}	
	
	protected void evaluateReceivedNodeCmd(JWaveNodeCommand cmd){		
		try {
			switch (cmd.getCommandClassKey()){
				case 0x31: // COMMAND_CLASS_SENSOR_MULTILEVEL
					setValue(JWaveReportFactory.generateSensorMultilevelReport(cmd));
					break;					
				default:
					break;
			}
		} catch (Exception exc){
			exc.printStackTrace();
			// FIXME
		}
	}
	
	
	
	
	
	
	
	protected void setValue(JWaveReportSensorMultilevel report){

		switch (report.getSensorType()){
		case 1:
			double temperature;
			if (report.getPrecission() > 0){
				temperature = (double)report.getValue()/(double)report.getPrecission();

			} else {
				temperature = (double)report.getValue();
			}
			if (report.getScale() == 1){ // Temperature is given in Fahrenheit
				temperature = (temperature - 32.0) * 5.0/9.0; // transform temperature to celsius
				// value could have more digits after transforming -> reduce to precission again.
				temperature = (double)( Math.round(temperature * report.getPrecission()) )/(double)report.getPrecission();
			}			
			setTemperature(temperature);
			break;		
		case 5: 
			if (report.getPrecission() > 0){
				setHumidity((double)report.getValue()/(double)report.getPrecission());
			} else {
				setHumidity(report.getValue());
			}
			break;
		}
	}
	protected void setTemperature(double temperature){	
		
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
	
	protected void setHumidity(double humidity){
		
		DeviceProperty property = this.getProperty("humidity");
		if (property.isValueSet()){
			Double pVal = (Double)property.getValue();
			if (pVal != humidity){
				property.setValue(humidity);				
			}
		} else {
			property.setValue(humidity);
		}
	}
	

	
	protected void onPropertyChanged(final DeviceProperty property){
		// FIXME
	}
	
	protected void generateDeviceFunctions(){
	
	
	}
	
	protected void generateProperties(){
		addProperty(new DeviceProperty(PropertyType.PT_DOUBLE, "temperature", "°C"));
		addProperty(new DeviceProperty(PropertyType.PT_DOUBLE, "humidity", "%"));		
	}
	
	protected void executeDeviceFunction(final PhysicalDeviceFunction function, final List<FunctionParameter> params) throws FunctionExecutionException{
		//
		
	}
}
