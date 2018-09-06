package de.smahoo.homeos.driver.zwave.fibaro;

import java.util.List;

import de.smahoo.homeos.common.FunctionExecutionException;
import de.smahoo.homeos.common.FunctionParameter;
import de.smahoo.homeos.device.DeviceProperty;
import de.smahoo.homeos.device.PhysicalDeviceFunction;
import de.smahoo.homeos.devices.SensorWindow;
import de.smahoo.homeos.driver.zwave.ZWaveDevice;
import de.smahoo.homeos.driver.zwave.ZWaveSleepListener;
import de.smahoo.homeos.driver.zwave.generic.ZWaveGenericSensorBinary;
import de.smahoo.homeos.property.PropertyType;
import de.smahoo.jwave.cmd.JWaveCommandParameterType;
import de.smahoo.jwave.cmd.JWaveNodeCommand;
import de.smahoo.jwave.cmd.JWaveNodeCommandFactory;
import de.smahoo.jwave.cmd.report.JWaveReportBasic;
import de.smahoo.jwave.cmd.report.JWaveReportFactory;
import de.smahoo.jwave.cmd.report.JWaveReportSensorBinary;
import de.smahoo.jwave.node.JWaveNode;

public class Fibaro_DoorWindowSensor extends ZWaveGenericSensorBinary implements SensorWindow{

		
	public Fibaro_DoorWindowSensor(String id, JWaveNode node){
		super(id, node);		
	}
	
	
	@Override
	protected void updateValue(boolean value){
		DeviceProperty property = this.getProperty("open");
		if (property.isValueSet()){
			Boolean pVal = (Boolean)property.getValue();
			if (pVal != value){
				property.setValue(value);				
			}
		} else {
			property.setValue(value);
		}
	}	
	
	public boolean isOpen(){
		return false;
	}
	
	public void initDevice(){
		setWakeUpInterval(480);
		associateNode(1, 1);		
	}
	
	@Override
	protected void generateProperties(){
		addProperty(new DeviceProperty(PropertyType.PT_BOOLEAN, "open", ""));		
	}
	
	
}
