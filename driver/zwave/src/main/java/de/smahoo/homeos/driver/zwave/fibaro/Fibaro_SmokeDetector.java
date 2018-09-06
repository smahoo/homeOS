package de.smahoo.homeos.driver.zwave.fibaro;

import java.util.List;

import de.smahoo.homeos.common.FunctionExecutionException;
import de.smahoo.homeos.common.FunctionParameter;
import de.smahoo.homeos.device.DeviceProperty;
import de.smahoo.homeos.device.PhysicalDeviceFunction;
import de.smahoo.homeos.devices.SensorAlarm;
import de.smahoo.homeos.devices.SensorSmoke;
import de.smahoo.homeos.driver.zwave.ZWaveDevice;
import de.smahoo.homeos.driver.zwave.ZWaveSleepListener;
import de.smahoo.homeos.driver.zwave.generic.ZWaveGenericSensorAlarm;
import de.smahoo.homeos.property.PropertyType;
import de.smahoo.jwave.cmd.JWaveNodeCommand;
import de.smahoo.jwave.cmd.JWaveNodeCommandFactory;
import de.smahoo.jwave.node.JWaveNode;

public class Fibaro_SmokeDetector extends ZWaveGenericSensorAlarm implements SensorSmoke{
	
	public Fibaro_SmokeDetector(String id, JWaveNode node){
		super(id,node);	
	}
		
	
	public boolean isSmoke(){
		return isAlarm();
	}
	
	
	protected void requestSmokeAlarm(){	
		getNode().sendData(cmdFactory.generateCmd_SensorAlarm_Get(1));
	}
	
	protected void evaluateReceivedNodeCmd(JWaveNodeCommand cmd){		
		super.evaluateReceivedNodeCmd(cmd);
		try {
			switch (cmd.getCommandClassKey()){
				case 0x9C: // COMMAND_CLASS_SENSOR_ALARM
					
					break;					
				default:
					break;
			}
		} catch (Exception exc){
			exc.printStackTrace();
			// FIXME
		}
	}
	
}
