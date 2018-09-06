package de.smahoo.homeos.driver.zwave.vision;

import java.util.List;

import de.smahoo.homeos.common.FunctionExecutionException;
import de.smahoo.homeos.common.FunctionParameter;
import de.smahoo.homeos.device.DeviceProperty;
import de.smahoo.homeos.device.PhysicalDeviceFunction;
import de.smahoo.homeos.device.SimpleDeviceFunction;
import de.smahoo.homeos.devices.Siren;
import de.smahoo.homeos.driver.zwave.ZWaveDevice;
import de.smahoo.homeos.driver.zwave.ZWaveSleepListener;
import de.smahoo.homeos.driver.zwave.generic.ZWaveGenericSensorBinary;
import de.smahoo.homeos.driver.zwave.generic.ZWaveGenericSwitchBinary;
import de.smahoo.homeos.property.PropertyType;
import de.smahoo.jwave.cmd.JWaveNodeCommand;
import de.smahoo.jwave.node.JWaveNode;

public class Vis_ZM1602 extends ZWaveGenericSwitchBinary implements Siren{

	
	
	
	public Vis_ZM1602(String id, JWaveNode node){
		super(id,node);
		super.addSleepListener(new ZWaveSleepListener() {
			
			@Override
			public void onWakeUpIntervalSet(long seconds) {
				//				
			}
			
			@Override
			public void onWakeUp() {				
				
			}
			
			@Override
			public void onSleep() {				
				
			}
		});
	}
	
	public void initDevice(){
		
	}
	
	
	public void startAlarm(){
		turnOn();
	}
	
	public void stopAlarm(){
		super.turnOff();
	}
	
	public boolean isAlarm(){
		return isOn();
	}
	
	
	
	
	
	
	
}
